package com.youmai.hxsdk.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.GeneratedMessage;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LogFile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TcpClient extends PduUtil implements Runnable {

    private static final String TAG = TcpClient.class.getSimpleName();

    private static final int TCP_RE_CONNECT = 0;

    //tcp 连接状态
    private enum TCP_STATUS {
        IDLE, CONNECTING, CONNECTED
    }

    private static final int SOCKET_BUFFER_SIZE = 500 * 1024; //500KB
    private static final int HEART_BEAT_INTERVAL = 30;//5 * 60;  //心跳间隔5分钟，单位为秒
    private static final int MAX_HEARTBEAT = 1;  //最大的心跳丢失次数

    private ScheduledExecutorService heartBeatScheduled;

    private volatile TCP_STATUS tcpStatus = TCP_STATUS.IDLE;

    private boolean isLogin = false;

    private int mSeqNum = 1; //服务器发送notify消息，SeqNum默认为0
    private int heartBeatCount = 0;

    private final Context mContext;
    private final Handler mHandler;

    private SocketChannel socketChannel;
    private InetSocketAddress mRemoteAddress;

    private IClientListener mCallBack;  //socket连接成功回调
    /**
     * socket网络发送线程对象
     **/
    private TcpSendThread mSender;

    /**
     * 发送缓冲队列（用于socket不通的情况）
     */
    private final LinkedBlockingQueue<ByteBuffer> mCacheQueue;
    /**
     * 发送队列
     */
    private final LinkedBlockingQueue<ByteBuffer> mSendQueue;
    /**
     * 接收buffer
     */
    private final ByteBuffer receiveBuffer;

    /**
     * 协议消息派发
     */
    private final ConcurrentHashMap<Integer, ReceiveListener> mCommonListener;


    /**
     * 协议监听
     */
    private final List<NotifyListener> mNotifyListener;


    /**
     * socket连接成功回调
     */
    public interface IClientListener {
        void connectSuccess();
    }


    public TcpClient(Context context) {
        mContext = context;
        mHandler = new TcpHandler(this);
        mCommonListener = new ConcurrentHashMap<>();
        mNotifyListener = new ArrayList<>();

        mCacheQueue = new LinkedBlockingQueue<>();
        mSendQueue = new LinkedBlockingQueue<>();
        receiveBuffer = ByteBuffer.allocate(SOCKET_BUFFER_SIZE);
        Log.v(TAG, "new TcpClient() be called");
    }

    /**
     * tcp 管理类
     *
     * @param remoteAddress 地址
     */
    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        mRemoteAddress = remoteAddress;
    }

    public void setCallBack(IClientListener callBack) {
        mCallBack = callBack;
    }

    /**
     * 连接socket
     */
    private void connect() {
        connect(null);
    }

    /**
     * 连接socket
     *
     * @param callback 连接成功回调
     */
    public void connect(IClientListener callback) {

        if (mContext != null
                && mRemoteAddress != null
                && AppUtils.isNetworkConnected(mContext)) {
            if (tcpStatus == TCP_STATUS.IDLE) {
                if (callback != null) {
                    mCallBack = callback;
                }
                Thread thread = new Thread(this);
                thread.setName("socket thread");
                thread.start();
                Log.v(TAG, "tcp is connecting");
                LogFile.inStance().toFile("tcp is connecting");
            }
        } else {
            Log.e(TAG, "mobile network not connected or not init");
        }
    }


    /**
     * 发送协议接口
     *
     * @param msg      消息体
     * @param callback 回调
     */
    public synchronized void sendProto(GeneratedMessage msg, int commandId, ReceiveListener callback) {
        PduBase pduBase = new PduBase();
        int seq_num = getSeqNum();

        pduBase.command_id = commandId;

        String uuid = HuxinSdkManager.instance().getUuid();
        if (TextUtils.isEmpty(uuid)) {
            Log.e(TAG, "not find user id");
            return;
        }

        pduBase.user_id = uuid.getBytes();
        pduBase.seq_id = seq_num;
        pduBase.length = msg.getSerializedSize();
        pduBase.body = msg.toByteArray();


        Log.v(TAG, "sendProto userId:" + uuid);
        Log.v(TAG, "sendProto seq_num:" + seq_num);
        Log.v(TAG, "sendProto command_id:" + pduBase.command_id);

        if (callback != null) {
            mCommonListener.put(seq_num, callback);
            mHandler.postDelayed(callback.getRunnable(), 5000);
        }
        sendPdu(pduBase);
    }

    /**
     * mSeqNum 线程安全
     *
     * @return int
     */
    private synchronized int getSeqNum() {
        return mSeqNum++;
    }

    public void setNotifyListener(NotifyListener listener) {
        for (int i = 0; i < mNotifyListener.size(); i++) {
            NotifyListener item = mNotifyListener.get(i);
            if (item.getCommandId() == listener.getCommandId()) {
                mNotifyListener.set(i, listener);
                return;
            }
        }
        mNotifyListener.add(listener);
    }

    public void clearNotifyListener(NotifyListener listener) {
        mNotifyListener.remove(listener);
    }


    /**
     * 关闭socket
     */
    public void close() {
        heartBeatCount = 0;
        tcpStatus = TCP_STATUS.IDLE;
        isLogin = false;
        try {
            if (socketChannel != null) {
                socketChannel.close();
            }
            if (mSender != null) {
                mSender.close();
            }
            if (heartBeatScheduled != null
                    && !heartBeatScheduled.isShutdown()) {
                heartBeatScheduled.shutdown();
            }

            mSendQueue.clear();

            if (mHandler.hasMessages(TCP_RE_CONNECT)) {
                mHandler.removeMessages(TCP_RE_CONNECT);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socketChannel = null;
            mSender = null;
            heartBeatScheduled = null;
        }
        Log.e(TAG, "tcp is closed");
        LogFile.inStance().toFile("tcp is closed");
    }


    /**
     * Socket连接是否是正常的
     *
     * @return 是否连接
     */
    public boolean isConnect() {
        return tcpStatus == TCP_STATUS.CONNECTED && socketChannel != null && socketChannel.isConnected();
    }

    public boolean isIdle() {
        return tcpStatus == TCP_STATUS.IDLE;
    }

    /**
     * 关闭socket 重新连接
     */
    public void reConnect() {
        close();
        connect();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        if (!login) {
            close();
        }
        isLogin = login;
    }

    private synchronized void sendPdu(PduBase pduBase) {
        ByteBuffer buffer = serializePdu(pduBase);
        if (mSender != null
                && isConnect()) {
            mSender.send(buffer);
        } else {
            sendToCacheQueue(buffer);
            String log = "tcp not connect,and send pdu to CacheQueue";
            Log.v(TAG, log);
            LogFile.inStance().toFile(log);

            reconnect();
        }
    }


    @Override
    public void OnRec(final PduBase pduBase) {
        final int key = pduBase.seq_id;
        String onRecSeqId = "OnRec pduBase seq_num:" + pduBase.seq_id;
        String onRecLength = "OnRec pduBase length:" + pduBase.length;
        String logCommandId = "common Listener command_id:" + pduBase.command_id;
        byte[] user_id = pduBase.user_id;
        String userId = new String(user_id);

        String uuid = HuxinSdkManager.instance().getUuid();
        if (TextUtils.isEmpty(uuid) || TextUtils.isEmpty(userId) || !userId.equals(uuid)) {
            Log.e(TAG, "user id check error ");
            return;
        }


        Log.v(TAG, onRecSeqId);
        Log.v(TAG, onRecLength);
        Log.v(TAG, logCommandId);

        LogFile.inStance().toFile(onRecSeqId);
        LogFile.inStance().toFile(onRecLength);
        LogFile.inStance().toFile(logCommandId);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ReceiveListener callback = mCommonListener.get(key);

                if (callback != null) {
                    callback.OnRec(pduBase);
                    mHandler.removeCallbacks(callback.getRunnable());
                    mCommonListener.remove(key);
                } else {
                    OnCallback(pduBase);
                }
            }
        });


    }


    @Override
    public void OnCallback(PduBase pduBase) {
        for (NotifyListener item : mNotifyListener) {
            if (item.getCommandId() == pduBase.command_id) {
                item.OnRec(pduBase.body);
                break;
            }
        }
    }


    @Override
    public void run() {
        try {
            socketConnect();
            tcpReceive();
        } catch (Throwable e) {
            Log.v(TAG, "TcpClient error " + e.toString());
            LogFile.inStance().toFile("TcpClient error " + e.toString());

            reconnect();
        }
    }


    /**
     * 开始心跳
     */
    private void startHeartBeat() {
        heartBeatScheduled = Executors.newScheduledThreadPool(1);
        heartBeatScheduled.scheduleAtFixedRate(new Runnable() {
            public void run() {
                heatBeat();
            }
        }, HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
    }


    /**
     * 心跳协议请求
     */
    private void heatBeat() {
        ++heartBeatCount;
        if (heartBeatCount > MAX_HEARTBEAT) {
            reConnect();
        }

        YouMaiBasic.Heart_Beat.Builder heart_builder = YouMaiBasic.Heart_Beat.newBuilder();
        YouMaiBasic.Heart_Beat heart = heart_builder.build();
        sendProto(heart, YouMaiBasic.COMMANDID.HEART_BEAT_VALUE, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                Log.v(TAG, "heart ack success");
                heartBeatCount = 0;
            }
        });
        Log.v(TAG, "start send heart");

    }


    /**
     * 连接socket
     *
     * @throws IOException
     */
    private void socketConnect() {
        tcpStatus = TCP_STATUS.CONNECTING;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            socketChannel.socket().setSendBufferSize(SOCKET_BUFFER_SIZE);
            socketChannel.socket().setReceiveBufferSize(SOCKET_BUFFER_SIZE);
            socketChannel.socket().setKeepAlive(true);
            //socketChannel.socket().setReuseAddress(false);
            socketChannel.socket().setSoLinger(false, 0);
            //socketChannel.socket().setSoTimeout(0);  //超时5秒
            //socketChannel.socket().setTcpNoDelay(true);
            socketChannel.connect(mRemoteAddress);

            while (!socketChannel.finishConnect()) {  //非阻塞模式,必需设置
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e(TAG, "socket connect" + e.toString());
                    LogFile.inStance().toFile("socket connect" + e.toString());
                }
            }
        } catch (Exception e) {
            tcpStatus = TCP_STATUS.IDLE;
            Log.e(TAG, "socketConnect exception" + e.toString());
            LogFile.inStance().toFile("socketConnect exception" + e.toString());
        }

        if (socketChannel.isConnected()) {
            mSender = new TcpSendThread();
            mSender.start();    //开启发送线程

            tcpStatus = TCP_STATUS.CONNECTED;

            if (mCallBack != null) {
                mCallBack.connectSuccess();//socket连接成功回调
            }

            Log.v(TAG, "tcp is connect success");
            LogFile.inStance().toFile("tcp is connect success");

            while (!mCacheQueue.isEmpty()) {
                ByteBuffer buffer = mCacheQueue.poll();
                mSendQueue.offer(buffer);
            }
        }

        startHeartBeat();    //开启心跳
    }

    /**
     * socket receive
     *
     * @throws IOException
     */

    private void tcpReceive() throws IOException {
        try {
            Log.v(TAG, "tcp is Blocking model read buffer");
            receiveBuffer.clear();
            while (socketChannel != null && socketChannel.isConnected()
                    && (socketChannel.read(receiveBuffer)) > 0) {
                receiveBuffer.flip();
                Log.v(TAG, "tcp read buffer");
                while (ParsePdu(receiveBuffer) > 0) {
                    Log.v(TAG, "read while loop");
                }
            }
        } catch (AsynchronousCloseException e) {
            Log.e(TAG, "tcpReceive exception" + e.toString());
            LogFile.inStance().toFile("tcpReceive exception" + e.toString());
        }

    }

    private void sendToCacheQueue(ByteBuffer buffer) {
        mCacheQueue.offer(buffer);
    }

    private void reconnect() {
        if (!mHandler.hasMessages(TCP_RE_CONNECT)) {
            Message msg = mHandler.obtainMessage(TCP_RE_CONNECT);
            mHandler.sendMessageDelayed(msg, 2000);
        }
    }

    /**
     * socket 发送线程类
     */
    private class TcpSendThread implements Runnable {
        boolean isExit = false;  //是否退出

        /**
         * 发送线程开启
         */
        public void start() {
            Thread thread = new Thread(this);
            thread.setName("tcpSend-thread");
            thread.start();
        }

        public void send(ByteBuffer buffer) {
            synchronized (this) {
                if (buffer != null) {
                    mSendQueue.offer(buffer);
                    notify();
                }
            }

        }


        /**
         * 发送线程关闭
         */
        public void close() {
            synchronized (this) { // 激活线程
                isExit = true;
                notify();
            }
        }

        @Override
        public void run() {
            while (!isExit) {
                Log.v(TAG, "tcpSend-thread is running");

                synchronized (mSendQueue) {
                    while (!mSendQueue.isEmpty()
                            && socketChannel != null
                            && socketChannel.isConnected()) {
                        ByteBuffer buffer = mSendQueue.poll();
                        if (buffer == null) {
                            continue;
                        }
                        buffer.flip();
                        Log.v(TAG, "tcp will send buffer...");

                        if (buffer.remaining() > 0) {
                            int count;
                            try {
                                while (buffer.hasRemaining() && (count = socketChannel.write(buffer)) > 0) {
                                    String log = "tcp send buffer count:" + count;
                                    Log.v(TAG, log);
                                    LogFile.inStance().toFile(log);
                                }
                            } catch (Exception e) {
                                Log.v(TAG, "tcp send error " + e.toString());
                                LogFile.inStance().toFile("tcp send error " + e.toString());
                                sendToCacheQueue(buffer);

                                reconnect();

                            } finally {
                                buffer.clear();
                            }
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {

                        }

                    }//#while
                }

                synchronized (this) {
                    try {
                        wait();// 发送完消息后，线程进入等待状态
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }//#run


    }//# TcpSendThread


    /**
     * service handler
     */
    public static class TcpHandler extends Handler {
        private final WeakReference<TcpClient> mTarget;

        TcpHandler(TcpClient target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            final TcpClient tcpClient = mTarget.get();
            switch (msg.what) {
                case TCP_RE_CONNECT:
                    if (tcpClient != null) {
                        Log.v(TAG, "tcp is reconnect");
                        LogFile.inStance().toFile("tcp is reconnect");
                        if (tcpClient.isIdle()
                                && AppUtils.isNetworkConnected(tcpClient.mContext)) {
                            tcpClient.reConnect();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


}