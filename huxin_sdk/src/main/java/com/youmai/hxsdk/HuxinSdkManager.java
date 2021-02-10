package com.youmai.hxsdk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.charservice.IMOwnerActivity;
import com.youmai.hxsdk.charservice.ServiceMsgNotifyActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.helper.ContactHelper;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.entity.IpConfig;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.loader.ChatMsgLoader;
import com.youmai.hxsdk.loader.ChatMsgLoaderAct;
import com.youmai.hxsdk.loader.OwnerMsgLoader;
import com.youmai.hxsdk.loader.OwnerMsgLoaderAct;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.IMContentUtil;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.chat.utils.EmotionInit;

import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by colin on 2016/7/15.
 * sdk 接口类
 */
public class HuxinSdkManager {
    private static final String TAG = HuxinSdkManager.class.getSimpleName();

    private static final int HANDLER_THREAD_INIT_CONFIG_START = 1;
    private static final int HANDLER_THREAD_AUTO_LOGIN = 2;

    private static final int LOADER_ID_GEN_MESSAGE_LIST = 11;

    private static HuxinSdkManager instance;


    private enum BIND_STATUS {
        IDLE, BINDING, BINDED
    }

    private HuxinService.HuxinServiceBinder huxinService = null;
    private BIND_STATUS binded = BIND_STATUS.IDLE;

    private Context mContext;

    private List<InitListener> mInitListenerList;
    private LoginStatusListener mLoginStatusListener;

    private ProcessHandler mProcessHandler;

    private StackAct mStackAct;
    private UserInfo mUserInfo;
    private ServiceInfo mServiceInfo;
    private boolean isKicked;

    private String pushToken;

    /**
     * SDK初始化结果监听器
     */
    public interface InitListener {
        void success();

        void fail();
    }

    public interface LoginStatusListener {
        void onKickOut();

        void onReLoginSuccess();
    }


    /**
     * 私有构造函数
     */
    private HuxinSdkManager() {
        mStackAct = StackAct.instance();
        mUserInfo = new UserInfo();
        mServiceInfo = new ServiceInfo();
        mInitListenerList = new ArrayList<>();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 获取呼信sdk单例索引
     *
     * @return
     */
    public static HuxinSdkManager instance() {
        if (instance == null) {
            instance = new HuxinSdkManager();
        }
        return instance;
    }


    /**
     * 呼信sdk初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.init(context, null);
    }

    public StackAct getStackAct() {
        return mStackAct;
    }

    /**
     * 初始化ARouter
     * 保证在application对ARouter初始化
     */
    void initARouter() {
        if (BuildConfig.DEBUG) {    // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();      // 打印日志
            ARouter.openDebug();    // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        if (mContext instanceof Application) {
            ARouter.init((Application) mContext); // 尽可能早，推荐在Application中初始化
        }
    }

    /**
     * 呼信sdk初始化
     *
     * @param context
     */
    public void init(final Context context, InitListener listener) {
        String processName = AppUtils.getProcessName(context, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(context.getPackageName());
            if (!defaultProcess) {
                return;
            }
        }

        mContext = context.getApplicationContext();
        IMMsgManager.instance().init(mContext);
        mUserInfo.fromJson(mContext);
        mServiceInfo.fromJson(mContext);
        GreenDBIMManager.instance(mContext);

        RespBaseBean.setProtocolCallBack(new ProtocolCallBack() {
            @Override
            public void sessionExpire() {
                reLogin();
            }
        });

        initARouter();

        if (listener != null) {
            mInitListenerList.add(listener);
        }

        if (binded == BIND_STATUS.IDLE) {
            binded = BIND_STATUS.BINDING;
            initHandler();

            mProcessHandler.sendEmptyMessage(HANDLER_THREAD_INIT_CONFIG_START);

        } else if (binded == BIND_STATUS.BINDING) {

            //do nothing

        } else if (binded == BIND_STATUS.BINDED) {
            for (InitListener item : mInitListenerList) {
                item.success();
            }
            mInitListenerList.clear();
        }
    }


    private void initWork(Context context) {
        if (AppConfig.LAUNCH_MODE == 0) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_color_test), Toast.LENGTH_SHORT).show();
        } else if (AppConfig.LAUNCH_MODE == 1) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_color_beta), Toast.LENGTH_SHORT).show();
        }

        EmotionInit.init(context.getApplicationContext());     //表情初始化
        //initEmo();


        Intent intent = new Intent(context.getApplicationContext(), HuxinService.class);
        context.getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.v(TAG, "HuxinSdkManager in init");

    }


    /**
     * 呼信sdk销毁
     *
     * @param
     */
    public void destroy() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            binded = BIND_STATUS.IDLE;
            mContext.getApplicationContext().unbindService(serviceConnection);
        }

    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public LoginStatusListener getLoginStatusListener() {
        return mLoginStatusListener;
    }

    public void setLoginStatusListener(LoginStatusListener listener) {
        this.mLoginStatusListener = listener;
    }

    public void setUserInfo(UserInfo info) {
        if (isLogin()) {
            loginOut();
        }

        mUserInfo = info;
        mUserInfo.saveJson(mContext);

        String uuid = info.getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            socketLogin(uuid);
            GreenDBIMManager.instance(mContext).initUuid(uuid);
        }
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        mServiceInfo = serviceInfo;
        mServiceInfo.saveJson(mContext);
    }


    public String getServiceUuid() {
        return mServiceInfo.getUuid();
    }

    public String getServiceName() {
        return mServiceInfo.getRealName();
    }

    public String getServiceAvatar() {
        return mServiceInfo.getAvatar();
    }


    public String getServiceUserName() {
        return mServiceInfo.getUserName();
    }


    public String getServicePhone() {
        return mServiceInfo.getPhoneNum();
    }

    public String getDisplayName() {
        return mUserInfo.getDisplayName();
    }


    public String getUuid() {
        return mUserInfo.getUuid();
    }

    public String getUserId() {
        return mUserInfo.getUserId();
    }

    public String getPhoneNum() {
        return mUserInfo.getPhoneNum();
    }

    public String getRealName() {
        return mUserInfo.getRealName();
    }

    public String getSex() {
        return mUserInfo.getSex();
    }

    public void setSex(String sex) {
        mUserInfo.setSex(sex);
    }

    public void setHeadUrl(String headUrl) {
        mUserInfo.setAvatar(headUrl);
    }

    public String getHeadUrl() {
        return mUserInfo.getAvatar();
    }

    public String getUserName() {
        return mUserInfo.getUserName();
    }

    public void setNickName(String nickName) {
        mUserInfo.setNickName(nickName);
    }

    public String getNickName() {
        return mUserInfo.getNickName();
    }

    public String getAccessToken() {
        return mUserInfo.getAccessToken();
    }

    public void setAccessToken(String accessToken) {
        mUserInfo.setAccessToken(accessToken);
    }

    public long getExpireTime() {
        return mUserInfo.getExpireTime();
    }

    public void setExpireTime(long expireTime) {
        mUserInfo.setExpireTime(expireTime);
    }

    public String getAppTs() {
        return mUserInfo.getAppTs();
    }

    public void setAppTs(String appTs) {
        mUserInfo.setAppTs(appTs);
    }

    public String getKey() {
        return mUserInfo.getKey();
    }

    public void setKey(String key) {
        mUserInfo.setKey(key);
    }

    public String getSecret() {
        return mUserInfo.getSecret();
    }

    public void setSecret(String secret) {
        mUserInfo.setSecret(secret);
    }


    public String getOrgName() {
        return mUserInfo.getOrgName();
    }

    public void setOrgName(String orgName) {
        mUserInfo.setOrgName(orgName);
    }

    public String getOrgId() {
        return mUserInfo.getOrgId();
    }

    public void setOrgId(String orgId) {
        mUserInfo.setOrgId(orgId);
    }


    public void loginOut() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            clearUserData();
        }
    }

    /**
     * 判断SDK是否被踢
     *
     * @return
     */
    public boolean isKicked() {
        return isKicked;
    }

    /**
     * 判断SDK是否登录
     *
     * @return
     */
    public boolean isLogin() {
        boolean res = false;
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            res = huxinService.isLogin();
        }
        return res;
    }


    private void initAppForMainProcess(Context context) {
        String processName = AppUtils.getProcessName(context, android.os.Process.myPid());
        Log.e("colin", "processName:" + processName);
        if (processName != null) {
            boolean defaultProcess = processName.equals(context.getPackageName());
            if (defaultProcess) {
                HuxinSdkManager.instance().init(context);
            }
        }
    }


    /**
     * 添加实时消息接收接口
     *
     * @param callback
     */
    public void setImMsgCallback(IMMsgCallback callback) {
        IMMsgManager.instance().setImMsgCallback(callback);
    }

    /**
     * 移除实时消息接收接口
     *
     * @param callback
     */
    public void removeImMsgCallback(IMMsgCallback callback) {
        IMMsgManager.instance().removeImMsgCallback(callback);
    }


    /**
     * 获取本地数据库缓存消息接口
     *
     * @param fragment
     * @param groupType
     * @param listener
     */
    public void chatMsgFromCache(Fragment fragment, int groupType, ProtoCallback.CacheMsgCallBack listener) {
        ChatMsgLoader callback = new ChatMsgLoader(fragment.getContext(), groupType, listener);

        if (fragment.getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            fragment.getLoaderManager().initLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        } else {
            fragment.getLoaderManager().restartLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        }
    }

    /**
     * 获取本地数据库缓存消息接口
     *
     * @param activity
     * @param listener
     */
    public void chatMsgFromCache(Activity activity, ProtoCallback.CacheMsgCallBack listener) {
        ChatMsgLoaderAct callback = new ChatMsgLoaderAct(activity, listener);

        if (activity.getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            activity.getLoaderManager().initLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        } else {
            activity.getLoaderManager().restartLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        }
    }


    /**
     * 获取本地数据库缓存消息接口
     *
     * @param fragment
     * @param listener
     */
    public void chatOwnerMsgFromCache(Fragment fragment, ProtoCallback.CacheMsgCallBack listener) {
        OwnerMsgLoader callback = new OwnerMsgLoader(fragment.getContext(), listener);

        if (fragment.getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            fragment.getLoaderManager().initLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        } else {
            fragment.getLoaderManager().restartLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        }
    }


    /**
     * 获取本地数据库缓存消息接口
     *
     * @param activity
     * @param listener
     */
    public void chatOwnerMsgFromCache(Activity activity, ProtoCallback.CacheMsgCallBack listener) {
        OwnerMsgLoaderAct callback = new OwnerMsgLoaderAct(activity, listener);

        if (activity.getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            activity.getLoaderManager().initLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        } else {
            activity.getLoaderManager().restartLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        }
    }


    /**
     * 重新登录
     */
    private void reLogin() {
        Intent intent = new Intent(mContext, LoginPromptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }


    /**
     * 判断SDK服务是否已经绑定成功
     *
     * @return
     */
    public boolean isBinded() {
        boolean res = false;
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            res = true;
        }
        return res;
    }


    /**
     * 判断tcp是否连接成功
     *
     * @return
     */
    public boolean isConnect() {
        boolean res = false;
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            res = huxinService.isConnect();
        }
        return res;
    }


    /**
     * tcp 重新连接
     */
    public void imReconnect() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.reConnect();
        }
    }


    /**
     * 关闭tcp连接
     *
     * @return
     */
    public void close() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.close();
        }
    }


    private void clearUserData() {
        close();
        mUserInfo.clear(mContext);

        //CacheMsgHelper.instance().deleteAll(mContext);
        //MorePushManager.unregister(mContext);//反注册送服务
        //SPDataUtil.setUserInfoJson(mContext, "");

        IMMsgManager.instance().clearShortcutBadger();
    }

    public void reLoginQuiet() {
        if (isLogin()) {
            isKicked = false;
        }
        final Activity act = getStackAct().currentActivity();
        if (isKicked && act != null) {
            String uuid = HuxinSdkManager.instance().getUuid();
            if (!TextUtils.isEmpty(uuid)) {
                final ProgressDialog progressDialog = new ProgressDialog(act);
                progressDialog.setMessage("正在重新登录，请稍后...");
                progressDialog.show();

                String ip = AppUtils.getStringSharedPreferences(mContext, "IP", AppConfig.getSocketHost());
                int port = AppUtils.getIntSharedPreferences(mContext, "PORT", AppConfig.getSocketPort());

                InetSocketAddress isa = new InetSocketAddress(ip, port);
                connectTcp(uuid, isa);
                isKicked = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);
            }
        }
    }


    private AlertDialog mAlertDialog;

    public void reLoginDialog() {
        if (isLogin()) {
            isKicked = false;
        }

        final Activity act = getStackAct().currentActivity();
        if (isKicked && act != null) {
            if (mAlertDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setMessage(R.string.relogin_info);
                builder.setNegativeButton(R.string.hx_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        close();
                        mAlertDialog = null;
                    }
                });

                builder.setPositiveButton(R.string.relogin_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String uuid = HuxinSdkManager.instance().getUuid();

                        if (!TextUtils.isEmpty(uuid)) {
                            final ProgressDialog progressDialog = new ProgressDialog(act);
                            progressDialog.setMessage("正在重新登录，请稍后...");
                            progressDialog.show();

                            String ip = AppUtils.getStringSharedPreferences(mContext, "IP", AppConfig.getSocketHost());
                            int port = AppUtils.getIntSharedPreferences(mContext, "PORT", AppConfig.getSocketPort());

                            InetSocketAddress isa = new InetSocketAddress(ip, port);
                            connectTcp(uuid, isa);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 1000);

                            mAlertDialog = null;

                        }
                    }
                });
                mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                        close();
                        mAlertDialog = null;
                    }
                });
                mAlertDialog = builder.create();
            }

            try {
                if (!mAlertDialog.isShowing()) {
                    mAlertDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void waitBindingProto(final GeneratedMessage msg, final int commandId, final ReceiveListener callback) {
        init(mContext, new InitListener() {
            @Override
            public void success() {
                huxinService.sendProto(msg, commandId, callback);
            }


            @Override
            public void fail() {
                String log = "bind server fail!";
                LogFile.inStance().toFile(log);
            }
        });
    }


    private void waitBindingNotify(final NotifyListener listener) {
        init(mContext, new InitListener() {
            @Override
            public void success() {
                huxinService.setNotifyListener(listener);
            }

            @Override
            public void fail() {
                String log = "bind server fail!";
                LogFile.inStance().toFile(log);
            }
        });
    }


    /**
     * 发送socket协议
     *
     * @param msg       消息体
     * @param commandId 命令码
     * @param callback  回调
     */
    private void sendProto(final GeneratedMessage msg, final int commandId, final ReceiveListener callback) {
        if (mContext != null) {
            if (binded == BIND_STATUS.BINDED) {

                /*boolean reLogin = (commandId == YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE
                        || commandId == YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE);
                if (!reLogin) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            reLoginDialog();
                        }
                    });
                }*/

                huxinService.sendProto(msg, commandId, callback);
            } else {
                waitBindingProto(msg, commandId, callback);
            }
        } else {
            throw new IllegalStateException("huxin sdk no init");
        }

    }


    public void setNotifyListener(NotifyListener listener) {
        if (mContext != null) {
            if (binded == BIND_STATUS.BINDED) {
                huxinService.setNotifyListener(listener);
            } else {
                waitBindingNotify(listener);
            }
        } else {
            throw new IllegalStateException("huxin sdk no init");
        }

    }

    public void clearNotifyListener(NotifyListener listener) {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.clearNotifyListener(listener);
        }
    }


    /**
     * req socket ip and port
     * tcp login
     */
    private void socketLogin(final String uuid) {
        String url = AppConfig.getTcpHost(uuid);

        HttpConnector.httpGet(url, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                IpConfig resp = GsonUtil.parse(response, IpConfig.class);

                String ip = AppConfig.getSocketHost();
                int port = AppConfig.getSocketPort();

                if (resp != null) {
                    ip = resp.getIp();
                    port = resp.getPort();

                    AppUtils.setStringSharedPreferences(mContext, "IP", ip);
                    AppUtils.setIntSharedPreferences(mContext, "PORT", port);

                    boolean isClear = AppUtils.getBooleanSharedPreferences(mContext, "clear_msg", false);
                    if (!isClear) {
                        AppUtils.setBooleanSharedPreferences(mContext, "clear_msg", true);
                        CacheMsgHelper.instance().deleteAll(mContext); //清除数据库缓存
                        clearMsgBadge(mContext);
                    }

                }

                InetSocketAddress isa = new InetSocketAddress(ip, port);
                connectTcp(uuid, isa);
            }
        });
    }


    /**
     * java 获取上传文件token
     *
     * @param
     */
    public void getUploadFileToken(IPostListener callback) {
        String url = AppConfig.GET_UPLOAD_FILE_TOKEN;

        String imei = "358695075682679";
        String phoneNum = "18664992691";
        int uid = 907;
        String sid = "f713e697f32b1242d7b78d4c63dc1ef5";

        ContentValues params = new ContentValues();
        params.put("msisdn", phoneNum);
        params.put("uid", uid);// 海外登录去除号码验证，只保留数字验证参数issms=3
        params.put("sid", sid); //保存
        params.put("termid", imei); //动态
        params.put("sign", AppConfig.appSign(phoneNum, imei));// 发行的渠道
        params.put("v", "5");
        params.put("ps", "-4202-8980600-");

        HttpConnector.httpPost(url, params, callback);
    }


    /**
     * 红包接口签名方法
     *
     * @param params
     * @return
     */
    public static String redPackageSign(@NonNull ContentValues params) {
        List<String> list = new ArrayList<>();
        try {
            for (Map.Entry<String, Object> entry : params.valueSet()) {
                String key = entry.getKey(); // name
                String value = entry.getValue().toString(); // value
                list.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append("&");
        }
        sb.append("secret=" + ColorsConfig.getSecret());

        return AppUtils.md5(sb.toString()).toUpperCase();

    }


    /**
     * 用户tcp协议重登录，仅仅用于测试
     *
     * @param uuid
     * @param isa
     */
    public void connectTcp(String uuid, InetSocketAddress isa) {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.connectTcp(uuid, isa);
        }
    }

    public boolean sendMsgReply(long msgId) {
        String uuid = getUuid();
        YouMaiMsg.ChatMsg_Ack.Builder builder = YouMaiMsg.ChatMsg_Ack.newBuilder();

        builder.setUserId(uuid);
        builder.setMsgId(msgId);
        YouMaiMsg.ChatMsg_Ack reply = builder.build();
        sendProto(reply, YouMaiBasic.COMMANDID.CID_CHAT_MSG_ACK_VALUE, null);
        return true;
    }


    public boolean sendPushMsgReply(long msgId) {
        String uuid = getUuid();
        YouMaiMsg.PushMsgAck.Builder builder = YouMaiMsg.PushMsgAck.newBuilder();

        builder.setUserId(uuid);
        builder.setMsgId(msgId);
        YouMaiMsg.PushMsgAck reply = builder.build();
        sendProto(reply, YouMaiBasic.COMMANDID.CID_PUSH_MSG_ACK_VALUE, null);
        return true;
    }


    /**
     * 获取好友列表
     *
     * @param listener
     */
    public void reqContactList(@NonNull final ProtoCallback.ContactListener listener) {
        final long cacheTime = AppUtils.getLongSharedPreferences(mContext, "buddy_update_time", 0);

        YouMaiBuddy.IMGetBuddyReqListReq.Builder builder = YouMaiBuddy.IMGetBuddyReqListReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setUpdateTime(cacheTime);

        YouMaiBuddy.IMGetBuddyReqListReq req = builder.build();
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiBuddy.IMGetBuddyReqListRsp ack = YouMaiBuddy.IMGetBuddyReqListRsp.parseFrom(pduBase.body);
                    List<ContactBean> resList = new ArrayList<>();

                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        long time = ack.getUpdateTime();
                        AppUtils.setLongSharedPreferences(mContext, "buddy_update_time", time);
                        if (cacheTime != time) {
                            ContactHelper.instance().delAllContact(mContext);

                            List<String> uuidList = new ArrayList<>();

                            List<YouMaiBuddy.IMBuddyInfo> list = ack.getBuddyInfosList();

                            for (YouMaiBuddy.IMBuddyInfo item : list) {
                                uuidList.add(item.getUserId());

                                if (item.getStatus() == 2) {//状态（删除：0；好友：1；拉黑：2）
                                    setBuddyBlack(item.getUserId());
                                }

                                ContactBean bean = new ContactBean();
                                bean.setUuid(item.getUserId());
                                bean.setStatus(item.getStatus());
                                resList.add(bean);
                            }
                            reqUserInfos(uuidList, resList, false, listener);
                        } else {
                            List<ContactBean> list = ContactHelper.instance().toQueryContactList(mContext);
                            if (list != null) {
                                resList = list;
                            }
                            listener.result(resList);
                        }
                    }


                } catch (ExceptionInInitializerError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        };

        sendProto(req, YouMaiBasic.COMMANDID.CID_BUDDY_LIST_REQ_VALUE, callback);
    }

    /**
     * 需要查询资料的用户
     *
     * @param queryUuid
     */
    public void reqUserInfo(String queryUuid, @NonNull final ProtoCallback.UserInfo listener) {
        YouMaiBuddy.IMGetUserInfoReq.Builder builder = YouMaiBuddy.IMGetUserInfoReq.newBuilder();
        builder.setUserId(getUuid());
        builder.addUserItemList(queryUuid);
        builder.setType(1);//(0:彩管家用户 1：彩之云用户）
        YouMaiBuddy.IMGetUserInfoReq rsp = builder.build();
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiBuddy.IMGetUserInfoRsp ack = YouMaiBuddy.IMGetUserInfoRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        int count = ack.getUserInfoListCount();
                        if (count > 0) {
                            YouMaiBuddy.UserInfo userInfo = ack.getUserInfoList(0);

                            String uuid = userInfo.getUserId();
                            String avatar = userInfo.getAvator();
                            String nickName = userInfo.getNickName();
                            String mobile = userInfo.getPhone();
                            String realName = userInfo.getRealName();
                            String orgName = userInfo.getOrgName();
                            String userName = userInfo.getUserName();
                            int sex = userInfo.getSex();

                            ContactBean temp = ContactHelper.instance().toQueryById(mContext, uuid);
                            if (temp != null) {
                                temp.setUuid(uuid);
                                temp.setAvatar(avatar);
                                temp.setNickName(nickName);
                                temp.setMobile(mobile);
                                temp.setRealName(realName);
                                temp.setUserName(userName);
                                temp.setOrgName(orgName);
                                temp.setSex(String.valueOf(sex));
                                ContactHelper.instance().insertOrUpdate(mContext, temp);
                                listener.result(temp);
                            } else {
                                ContactBean bean = new ContactBean();
                                bean.setUuid(uuid);
                                bean.setAvatar(avatar);
                                bean.setNickName(nickName);
                                bean.setMobile(mobile);
                                bean.setRealName(realName);
                                bean.setUserName(userName);
                                bean.setOrgName(orgName);
                                bean.setSex(String.valueOf(sex));
                                ContactHelper.instance().insertOrUpdate(mContext, bean);
                                listener.result(bean);
                            }
                        }
                    }
                } catch (ExceptionInInitializerError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        };

        sendProto(rsp, YouMaiBasic.COMMANDID.CID_USER_INFO_REQ_VALUE, callback);
    }


    /**
     * 需要查询资料的用户
     *
     * @param queryList
     */
    private void reqUserInfos(final List<String> queryList, final List<ContactBean> resList,
                              final boolean isGroupMember,
                              @NonNull final ProtoCallback.ContactListener listener) {

        YouMaiBuddy.IMGetUserInfoReq.Builder builder = YouMaiBuddy.IMGetUserInfoReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setType(1);//(0:彩管家用户 1：彩之云用户）
        builder.addAllUserItemList(queryList);

        YouMaiBuddy.IMGetUserInfoReq rsp = builder.build();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiBuddy.IMGetUserInfoRsp ack = YouMaiBuddy.IMGetUserInfoRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<ContactBean> tempList = new ArrayList<>();

                        List<ContactBean> cacheList = ContactHelper.instance().toQueryContactList(mContext);

                        List<YouMaiBuddy.UserInfo> list = ack.getUserInfoListList();
                        for (YouMaiBuddy.UserInfo item : list) {
                            String uuid = item.getUserId();
                            String avatar = item.getAvator();
                            String nickName = item.getNickName();
                            String mobile = item.getPhone();
                            String realName = item.getRealName();
                            String userName = item.getUserName();
                            String orgName = item.getOrgName();
                            int sex = item.getSex();

                            ContactBean bean = new ContactBean();

                            bean.setId(findEntityId(uuid, cacheList));
                            bean.setUuid(uuid);
                            bean.setAvatar(avatar);
                            bean.setNickName(nickName);
                            bean.setMobile(mobile);
                            bean.setRealName(realName);
                            bean.setUserName(userName);
                            bean.setOrgName(orgName);
                            bean.setSex(String.valueOf(sex));

                            for (ContactBean contactBean : resList) {
                                if (contactBean.getUuid().equals(uuid)) {
                                    if (isGroupMember) {
                                        bean.setMemberRole(contactBean.getMemberRole());
                                        bean.setStatus(findEntityStatus(uuid, cacheList));
                                    } else {
                                        bean.setStatus(contactBean.getStatus());
                                    }
                                    break;
                                }
                            }
                            tempList.add(bean);
                        }

                        listener.result(tempList);


                        ContactHelper.instance().insertOrUpdate(mContext, tempList);

                    }

                } catch (ExceptionInInitializerError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        };

        sendProto(rsp, YouMaiBasic.COMMANDID.CID_USER_INFO_REQ_VALUE, callback);

    }


    /**
     * 添加好友请求
     *
     * @param dstUuid
     * @param remark
     */
    public void addFriend(final String dstUuid, YouMaiBuddy.BuddyOptType type, String remark,
                          @NonNull final ProtoCallback.AddFriendListener listener) {
        YouMaiBuddy.IMOptBuddyReq.Builder builder = YouMaiBuddy.IMOptBuddyReq.newBuilder();

        builder.setSrcUserId(getUuid());  //消息发送方
        builder.setDestUserId(dstUuid); //消息接受方
        builder.setOptType(type);
        builder.setOptRemark(remark);

        builder.setUsername(getPhoneNum());
        builder.setNickname(getNickName());
        builder.setRealName(getRealName());
        builder.setAvatar(getHeadUrl());

        int temp = 0;
        if (type == YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_AGREE) {
            temp = YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_AGREE_VALUE;
        } else if (type == YouMaiBuddy.BuddyOptType.BUDDY_OPT_DEL) {
            temp = YouMaiBuddy.BuddyOptType.BUDDY_OPT_DEL_VALUE;
        } else if (type == YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_BLACKLIST) {
            temp = YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_BLACKLIST_VALUE;
        }
        final int optType = temp;

        YouMaiBuddy.IMOptBuddyReq rsp = builder.build();
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiBuddy.IMOptBuddyRsp ack = YouMaiBuddy.IMOptBuddyRsp.parseFrom(pduBase.body);

                    if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_OK) {
                        clearBuddyListCache();
                        switch (optType) {
                            case YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_AGREE_VALUE:
                                buddyAgree(dstUuid);
                                break;
                            case YouMaiBuddy.BuddyOptType.BUDDY_OPT_DEL_VALUE://状态（删除：0；好友：1；拉黑：2）
                                ContactHelper.instance().updateStatusById(mContext, dstUuid, 0);
                                break;
                            case YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_BLACKLIST_VALUE://状态（删除：0；好友：1；拉黑：2）
                                ContactHelper.instance().updateStatusById(mContext, dstUuid, 2);
                                break;
                        }
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_READD) {
                        clearBuddyListCache();
                        ContactHelper.instance().updateStatusById(mContext, dstUuid, 1);
                    }

                    listener.result(ack);

                } catch (ExceptionInInitializerError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        };

        sendProto(rsp, YouMaiBasic.COMMANDID.CID_BUDDY_LIST_OPT_REQ_VALUE, callback);
    }


    /**
     * 清除好友缓存列表
     */
    public void clearBuddyListCache() {
        AppUtils.setLongSharedPreferences(mContext, "buddy_update_time", 0);
    }

    /**
     * 上传用户信息
     */
    public void uploadUserInfo() {
        YouMaiBuddy.IMInfoOptReq.Builder builder = YouMaiBuddy.IMInfoOptReq.newBuilder();
        YouMaiBuddy.UserInfo.Builder userInfo = YouMaiBuddy.UserInfo.newBuilder();

        userInfo.setUserId(getUuid());
        userInfo.setNickName(getNickName());
        userInfo.setUserName(getUserName());
        userInfo.setPhone(getPhoneNum());
        userInfo.setAvator(getHeadUrl());
        userInfo.setOrgName(getOrgName());
        userInfo.setRealName(getRealName());

        try {
            int sex = Integer.parseInt(getSex());
            userInfo.setSex(sex);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            int sex = Integer.parseInt(getSex());
            userInfo.setSex(sex);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        builder.setUserInfo(userInfo.build());

        builder.setUserId(getUuid());
        builder.setType(1);//(0:删除，1：添加）

        YouMaiBuddy.IMInfoOptReq rsp = builder.build();
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiBuddy.IMGetUserInfoRsp ack = YouMaiBuddy.IMGetUserInfoRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {

                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        sendProto(rsp, YouMaiBasic.COMMANDID.CID_USER_INFO_OPT_REQ_VALUE, callback);
    }


    /**
     * 设置home activity
     *
     * @param homeAct
     */
    public void setHomeAct(Class homeAct) {
        IMMsgManager.instance().setHomeAct(homeAct);
    }


    /**
     * 设置是否关闭消息通知栏
     *
     * @param notify
     */
    public void setNotify(boolean notify) {
        IMMsgManager.instance().setNotify(notify);
    }

    /**
     * 是否关闭消息通知栏
     */
    public boolean isNotify() {
        return IMMsgManager.instance().isNotify();
    }


    /**
     * 获取好友黑名单
     *
     * @param uuid
     */
    public boolean getBuddyBlack(String uuid) {
        return AppUtils.getBooleanSharedPreferences(mContext, "black" + uuid, false);
    }


    /**
     * 设置好友黑名单
     *
     * @param uuid
     */
    public void setBuddyBlack(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "black" + uuid, true);
    }

    /**
     * 移除好友黑名单
     *
     * @param uuid
     */
    public void removeBuddyBlack(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "black" + uuid, false);
    }


    /**
     * 获取消息免打扰
     *
     * @param uuid
     * @return
     */
    public boolean getNotDisturb(String uuid) {
        return AppUtils.getBooleanSharedPreferences(mContext, "notify" + uuid, false);
    }

    /**
     * 获取消息免打扰
     *
     * @param groupId
     * @return
     */
    public boolean getNotDisturb(int groupId) {
        return AppUtils.getBooleanSharedPreferences(mContext, "notify" + groupId, false);
    }

    /**
     * 设置消息免打扰
     *
     * @param uuid
     * @return
     */
    public void setNotDisturb(String uuid) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        if (!temp.contains("#" + uuid)) {
            temp = temp + "#" + uuid;
            AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);
        }

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + uuid, true);
    }


    /**
     * 设置消息免打扰
     *
     * @param groupId
     * @return
     */
    public void setNotDisturb(int groupId) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        if (!temp.contains("#" + groupId)) {
            temp = temp + "#" + groupId;
            AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);
        }

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + groupId, true);
    }

    /**
     * 移除消息免打扰
     *
     * @param uuid
     */
    public void removeNotDisturb(String uuid) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        temp = temp.replaceAll("#" + uuid, "");
        AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + uuid, false);
    }

    /**
     * 移除消息免打扰
     *
     * @param groupId
     */
    public void removeNotDisturb(int groupId) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        temp = temp.replaceAll("#" + groupId, "");
        AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + groupId, false);
    }


    /**
     * 获取消息置顶
     *
     * @param uuid
     * @return
     */

    public boolean getMsgTop(String uuid) {
        return AppUtils.getBooleanSharedPreferences(mContext, "top" + uuid, false);
    }

    /**
     * 获取消息置顶
     *
     * @param groupId
     * @return
     */
    public boolean getMsgTop(int groupId) {
        return AppUtils.getBooleanSharedPreferences(mContext, "top" + groupId, false);
    }

    /**
     * 设置消息置顶
     *
     * @param uuid
     * @return
     */
    public void setMsgTop(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + uuid, true);
    }

    /**
     * 设置消息置顶
     *
     * @param groupId
     * @return
     */
    public void setMsgTop(int groupId) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + groupId, true);
    }

    /**
     * 移除消息置顶
     *
     * @param uuid
     */
    public void removeMsgTop(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + uuid, false);
    }

    /**
     * 移除消息置顶
     *
     * @param groupId
     */
    public void removeMsgTop(int groupId) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + groupId, false);
    }


    /**
     * 修改昵称
     */
    public void reqModifyNickName(String nickName) {
        setNickName(nickName);
        uploadUserInfo();
    }


    /**
     * 修改头像
     */
    public void reqModifyAvatar(String url) {
        setHeadUrl(url);
        uploadUserInfo();
    }


    /**
     * 修改小区
     */
    public void reqModifyOrgName(String orgName) {
        setOrgName(orgName);
        uploadUserInfo();
    }


    /**
     * 修改性别
     */
    public void reqModifySex(String sex) {
        setSex(sex);
        uploadUserInfo();
    }

    public void regeditCommonPushMsg(@NonNull final ProtoCallback.BuddyNotify notify) {
        IMMsgManager.instance().setPushMsgNotify(notify);
    }

    /**
     * 进入单聊页面
     *
     * @param context
     * @param uuid
     * @param nickName
     * @param avatar
     * @param userName
     * @param mobile
     */
    public void entryChatSingle(Context context, String uuid, String nickName,
                                String avatar, String userName, String mobile) {

        Intent intent = new Intent(context, IMConnectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(IMConnectionActivity.DST_UUID, uuid);
        intent.putExtra(IMConnectionActivity.DST_NAME, nickName);
        intent.putExtra(IMConnectionActivity.DST_AVATAR, avatar);
        intent.putExtra(IMConnectionActivity.DST_USERNAME, userName);
        intent.putExtra(IMConnectionActivity.DST_PHONE, mobile);
        context.startActivity(intent);
    }


    /**
     * 进入群聊页面
     *
     * @param context
     * @param groupId
     * @param groupName
     */
    public void entryChatGroup(Context context, int groupId, int groupType, String groupName) {
        Intent intent = new Intent(context, IMGroupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra(IMGroupActivity.DST_UUID, groupId);
        intent.putExtra(IMGroupActivity.GROUP_TYPE, groupType);
        intent.putExtra(IMGroupActivity.DST_NAME, groupName);

        context.startActivity(intent);
    }


    /**
     * 进入客户单聊页面
     *
     * @param context
     */
    public void entryChatService(Context context) {

        Intent intent = new Intent(context, IMOwnerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(IMOwnerActivity.DST_UUID, mServiceInfo.getUuid());
        intent.putExtra(IMOwnerActivity.DST_NAME, mServiceInfo.getNickName());
        intent.putExtra(IMOwnerActivity.DST_AVATAR, mServiceInfo.getAvatar());
        intent.putExtra(IMOwnerActivity.DST_USERNAME, mServiceInfo.getUserName());
        intent.putExtra(IMOwnerActivity.DST_PHONE, mServiceInfo.getPhoneNum());
        context.startActivity(intent);
    }


    /**
     * 进入客服经理消息列表页面
     *
     * @param context
     */
    public void entryServiceManager(Context context) {
        Intent intent = new Intent(context, ServiceMsgNotifyActivity.class);
        context.startActivity(intent);
    }

    /**
     * 获取未读客服经理消息数目
     */
    public int unreadServiceManagerMessage() {
        /*int unreadServiceCount = IMMsgManager.instance().getAllBadgeOwnerCount()
                + IMMsgManager.instance().getAllBadgeCommCount();
        return unreadServiceCount;*/
        return IMMsgManager.instance().getAllBadgeOwnerCount();
    }


    /**
     * 获取未读好友和社群消息数目
     */
    public int unreadBuddyAndCommMessage() {
        int unread = IMMsgManager.instance().getAllBadgeBubbyCount()
                + IMMsgManager.instance().getAllBadgeCommCount();
        return unread;
    }


    /**
     * 进入客户单聊页面
     *
     * @param context
     * @param uuid
     * @param nickName
     * @param avatar
     * @param userName
     * @param mobile
     */
    public void entryChatService(Context context, String uuid, String nickName,
                                 String avatar, String userName, String mobile) {

        Intent intent = new Intent(context, IMOwnerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(IMOwnerActivity.DST_UUID, uuid);
        intent.putExtra(IMOwnerActivity.DST_NAME, nickName);
        intent.putExtra(IMOwnerActivity.DST_AVATAR, avatar);
        intent.putExtra(IMOwnerActivity.DST_USERNAME, userName);
        intent.putExtra(IMOwnerActivity.DST_PHONE, mobile);
        context.startActivity(intent);
    }


    /**
     * 发送文字
     *
     * @param destUuid
     * @param content
     */
    public void sendTextService(String destUuid, String content, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendText(content);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);


    }


    /**
     * 发送位置
     *
     * @param destUuid
     * @param longitude
     * @param latitude
     * @param scale
     * @param label
     * @param callback
     */
    public void sendLocationService(String destUuid, double longitude, double latitude,
                                    int scale, String label, ReceiveListener callback) {

        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendLongitude(longitude + "");
        imContentUtil.appendLaitude(latitude + "");
        imContentUtil.appendScale(scale + "");
        imContentUtil.appendLabel(label);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);

    }


    /**
     * tcp发送图片
     *
     * @param destUuid
     * @param fileId
     * @param imgWidth
     * @param imgHeight
     * @param quality
     * @param callback
     */
    public void sendPictureService(String destUuid, String fileId, String imgWidth, String imgHeight,
                                   String quality, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendPictureId(fileId);
        imContentUtil.appendImgWidth(imgWidth);
        imContentUtil.appendImgHeight(imgHeight);
        imContentUtil.appendDescribe(quality); // 是否原图
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);

    }


    /**
     * tcp 发送音频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendAudioService(String destUuid, String fileId, String secondsTime, String sourcePhone,
                                 String forwardCount, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendAudioId(fileId);
        imContentUtil.appendBarTime(secondsTime);
        imContentUtil.appendSourcePhone(sourcePhone);
        imContentUtil.appendForwardCount(forwardCount);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);


    }


    /**
     * tcp发送视频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendVideoService(String destUuid, String fileId, String frameId, String name, String size,
                                 String time, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.addVideo(fileId, frameId, name, size, time);//body的内容
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);

    }

    /**
     * tcp发送视频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendFileService(String destUuid, String fileId,
                                String fileName, String fileSize,
                                ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);

    }


    /**
     * 发送个人红包
     *
     * @param destUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void sendRedPackageService(String destUuid, String redUuid, String value, String title,
                                      ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SEND_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);
    }


    /**
     * 打开个人红包
     *
     * @param destUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void openRedPackageService(String destUuid, String redUuid, String value, String title,
                                      ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_GET_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_CSERVICE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);
        imContentUtil.appendRedPackageReceiveName(getDisplayName());
        imContentUtil.appendRedPackageDone("1");

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE, callback);
    }


    /**
     * 发送文字
     *
     * @param destUuid
     * @param content
     */
    public void sendText(String destUuid, String content, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendText(content);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);


    }


    /**
     * 发送位置
     *
     * @param destUuid
     * @param longitude
     * @param latitude
     * @param scale
     * @param label
     * @param callback
     */
    public void sendLocation(String destUuid, double longitude, double latitude,
                             int scale, String label, ReceiveListener callback) {

        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendLongitude(longitude + "");
        imContentUtil.appendLaitude(latitude + "");
        imContentUtil.appendScale(scale + "");
        imContentUtil.appendLabel(label);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);

    }


    /**
     * tcp发送图片
     *
     * @param destUuid
     * @param fileId
     * @param imgWidth
     * @param imgHeight
     * @param quality
     * @param callback
     */
    public void sendPicture(String destUuid, String fileId, String imgWidth, String imgHeight,
                            String quality, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendPictureId(fileId);
        imContentUtil.appendImgWidth(imgWidth);
        imContentUtil.appendImgHeight(imgHeight);
        imContentUtil.appendDescribe(quality); // 是否原图
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);

    }


    /**
     * tcp 发送音频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendAudio(String destUuid, String fileId, String secondsTime, String sourcePhone,
                          String forwardCount, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendAudioId(fileId);
        imContentUtil.appendBarTime(secondsTime);
        imContentUtil.appendSourcePhone(sourcePhone);
        imContentUtil.appendForwardCount(forwardCount);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);


    }


    /**
     * tcp发送视频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendVideo(String destUuid, String fileId, String frameId, String name, String size,
                          String time, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.addVideo(fileId, frameId, name, size, time);//body的内容
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);

    }

    /**
     * tcp发送视频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendFile(String destUuid, String fileId,
                         String fileName, String fileSize,
                         ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);

    }


    /**
     * 发送个人红包
     *
     * @param destUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void sendRedPackage(String destUuid, String redUuid, String value, String title,
                               ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SEND_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * 打开个人红包
     *
     * @param destUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void openRedPackage(String destUuid, String redUuid, String value, String title,
                               ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_GET_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_BUDDY);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);
        imContentUtil.appendRedPackageReceiveName(getDisplayName());
        imContentUtil.appendRedPackageDone("1");

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * 创建群组
     *
     * @param callback
     */
    public void createGroup(String groupName, @NonNull List<YouMaiGroup.GroupMemberItem> list,
                            ReceiveListener callback) {
        createGroup(groupName, YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT, list, callback);
    }


    /**
     * 创建群组
     *
     * @param callback
     */
    public void createGroup(String groupName, YouMaiBasic.GroupType type,
                            @NonNull List<YouMaiGroup.GroupMemberItem> list,
                            ReceiveListener callback) {
        YouMaiGroup.GroupCreateReq.Builder builder = YouMaiGroup.GroupCreateReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupName(groupName);
        builder.setGroupType(type);
        builder.addAllMemberList(list);
        YouMaiGroup.GroupCreateReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_CREATE_REQ_VALUE, callback);
    }


    /**
     * 创建群组
     *
     * @param callback
     */
    public void createGroupById(@NonNull String groupName, @NonNull List<ContactBean> contacts,
                                ReceiveListener callback) {
        createGroupById(groupName, YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT, contacts, callback);
    }


    /**
     * 创建群组
     *
     * @param callback
     */
    public void createGroupById(@NonNull String groupName, YouMaiBasic.GroupType type,
                                @NonNull List<ContactBean> contacts,
                                ReceiveListener callback) {
        if (ListUtils.isEmpty(contacts)) {
            return;
        }

        YouMaiGroup.GroupCreateReq.Builder builder = YouMaiGroup.GroupCreateReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupName(groupName);
        builder.setGroupType(type);

        YouMaiGroup.GroupMemberItem.Builder self = YouMaiGroup.GroupMemberItem.newBuilder();
        self.setMemberId(getUuid());
        self.setUserName(getHeadUrl());
        self.setMemberRole(0);
        builder.addMemberList(self.build());

        for (ContactBean item : contacts) {
            YouMaiGroup.GroupMemberItem.Builder memberItem = YouMaiGroup.GroupMemberItem.newBuilder();
            if (!getUuid().equals(item.getUuid())) {
                memberItem.setMemberRole(2);
                memberItem.setMemberId(item.getUuid());
                memberItem.setUserName(item.getAvatar());
                builder.addMemberList(memberItem.build());
            }
        }

        YouMaiGroup.GroupCreateReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_CREATE_REQ_VALUE, callback);
    }


    /**
     * 删除群组
     *
     * @param groupId
     * @param callback
     */
    public void delGroup(int groupId, ReceiveListener callback) {
        YouMaiGroup.GroupDissolveReq.Builder builder = YouMaiGroup.GroupDissolveReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        YouMaiGroup.GroupDissolveReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_DISSOLVE_REQ_VALUE, callback);
    }


    /**
     * 删除聊天消息
     *
     * @param targetUuid
     */
    public void delMsgChat(String targetUuid) {
        CacheMsgHelper.instance().deleteAllMsg(mContext, targetUuid);
        //去掉未读消息计数
        IMMsgManager.instance().removeBadge(targetUuid);
    }


    /**
     * 删除群聊天消息
     *
     * @param groupId
     */
    public void delMsgChat(int groupId) {
        delMsgChat(String.valueOf(groupId));
    }


    /**
     * 清空本地缓存
     *
     * @param context
     */
    public void clearCache(Context context) {
        CacheMsgHelper.instance().deleteAll(context); //清除数据库缓存

        FileCacheManager.clearAllCache(context); //清除文件缓存
    }


    /**
     * 清空本地未读消息计数
     *
     * @param context
     */
    public void clearMsgBadge(Context context) {
        IMMsgManager.instance().clearMsgBadge(context);
    }


    /**
     * 添加/删除 群组成员
     *
     * @param type
     * @param list
     * @param groupId
     * @param groupType
     * @param callback
     */
    public void changeGroupMember(YouMaiGroup.GroupMemberOptType type,
                                  List<YouMaiGroup.GroupMemberItem> list,
                                  int groupId, int groupType, ReceiveListener callback) {
        YouMaiGroup.GroupMemberChangeReq.Builder builder = YouMaiGroup.GroupMemberChangeReq.newBuilder();

        builder.setType(type);
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setGroupType(YouMaiBasic.GroupType.valueOf(groupType));
        builder.addAllMemberList(list);
        YouMaiGroup.GroupMemberChangeReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_CHANGE_MEMBER_REQ_VALUE, callback);
    }


    /**
     * 请求群列表
     *
     * @param list
     * @param callback
     */
    public void reqGroupList(List<YouMaiGroup.GroupItem> list, ReceiveListener callback) {
        YouMaiGroup.GroupListReq.Builder builder = YouMaiGroup.GroupListReq.newBuilder();
        builder.setUserId(getUuid());
        builder.addAllGroupItemList(list);

        YouMaiGroup.GroupListReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_LIST_REQ_VALUE, callback);
    }


    /**
     * 获取群成员列表
     *
     * @param groupId
     * @param callback
     */
    public void reqGroupMember(int groupId, ReceiveListener callback) {
        YouMaiGroup.GroupMemberReq.Builder builder = YouMaiGroup.GroupMemberReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setUpdateTime(System.currentTimeMillis());

        YouMaiGroup.GroupMemberReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_MEMBER_REQ_VALUE, callback);
    }


    /**
     * 获取群成员列表
     *
     * @param groupId
     */
    public void reqGroupMember(int groupId, @NonNull final ProtoCallback.ContactListener callback) {
        YouMaiGroup.GroupMemberReq.Builder builder = YouMaiGroup.GroupMemberReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setUpdateTime(System.currentTimeMillis());

        YouMaiGroup.GroupMemberReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_MEMBER_REQ_VALUE, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberRsp ack = YouMaiGroup.GroupMemberRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<YouMaiGroup.GroupMemberItem> memberListList = ack.getMemberListList();

                        List<String> uuidList = new ArrayList<>();
                        List<ContactBean> contactBeans = new ArrayList<>();

                        for (YouMaiGroup.GroupMemberItem item : memberListList) {
                            ContactBean contact = new ContactBean();
                            String uuid = item.getMemberId();

                            contact.setUuid(uuid);
                            contact.setMemberRole(item.getMemberRole());
                            contact.setNickName(item.getMemberName());
                            contact.setUserName(item.getUserName());
                            contact.setAvatar(item.getUserName());

                            contactBeans.add(contact);

                            uuidList.add(uuid);
                        }
                        reqUserInfos(uuidList, contactBeans, true, callback);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 获取群资料
     *
     * @param callback
     */
    public void reqGroupInfo(int groupId, long updateTime, ReceiveListener callback) {
        YouMaiGroup.GroupInfoReq.Builder builder = YouMaiGroup.GroupInfoReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setUpdateTime(updateTime);

        YouMaiGroup.GroupInfoReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_INFO_REQ_VALUE, callback);
    }


    /**
     * 修改群资料
     *
     * @param groupId
     * @param groupName
     * @param groupAvatar
     * @param callback
     */
    public void reqModifyGroupInfo(int groupId, String ownerId, String ownerName, String groupName,
                                   String groupTopic,
                                   String groupAvatar,
                                   YouMaiGroup.GroupInfoModifyType type,
                                   ReceiveListener callback) {
        YouMaiGroup.GroupInfoModifyReq.Builder builder = YouMaiGroup.GroupInfoModifyReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setSrcOwnerName(getDisplayName());
        builder.setDstOwnerName(ownerName);
        builder.setGroupId(groupId);
        builder.setGroupName(groupName);
        builder.setGroupAvatar(groupAvatar);
        builder.setTopic(groupTopic);
        if (!StringUtils.isEmpty(ownerId)) {
            builder.setOwnerId(ownerId);
        }
        builder.setType(type);
        YouMaiGroup.GroupInfoModifyReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_INFO_MODIFY_REQ_VALUE, callback);
    }


    /**
     * 拉取组织结构
     *
     * @return
     */
    public void reqOrgInfo(String groupId, ReceiveListener callback) {
        YouMaiBuddy.IMGetOrgReq defaultInstance = YouMaiBuddy.IMGetOrgReq.getDefaultInstance();
        YouMaiBuddy.IMGetOrgReq.Builder builder = defaultInstance.toBuilder();
        builder.setOrgId(groupId);
        YouMaiBuddy.IMGetOrgReq orgReq = builder.build();
        sendProto(orgReq, YouMaiBasic.COMMANDID.CID_ORG_LIST_REQ_VALUE, callback);
    }


    /**
     * 发送文字
     *
     * @param groupId
     * @param groupName
     * @param content
     */
    public void sendTextInGroup(int groupId, int groupType, String groupName, String content,
                                ArrayList<String> atList, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);

        if (!ListUtils.isEmpty(atList)) {
            for (String item : atList) {
                msgData.addForcePushIdsList(item);
            }
        }

        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }


        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendText(content);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);

    }


    /**
     * 发送位置
     *
     * @param groupId
     * @param groupName
     * @param longitude
     * @param latitude
     * @param scale
     * @param label
     * @param callback
     */
    public void sendLocationInGroup(int groupId, int groupType, String groupName, double longitude, double latitude,
                                    int scale, String label, ReceiveListener callback) {

        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendLongitude(longitude + "");
        imContentUtil.appendLaitude(latitude + "");
        imContentUtil.appendScale(scale + "");
        imContentUtil.appendLabel(label);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * tcp发送图片
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param imgWidth
     * @param imgHeight
     * @param quality
     * @param callback
     */
    public void sendPictureInGroup(int groupId, int groupType, String groupName, String fileId,
                                   String imgWidth, String imgHeight, String quality,
                                   ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendPictureId(fileId);
        imContentUtil.appendDescribe(quality); // 是否原图
        imContentUtil.appendImgWidth(imgWidth);
        imContentUtil.appendImgHeight(imgHeight);

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * tcp 发送音频
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param callback
     */
    public void sendAudioInGroup(int groupId, int groupType, String groupName, String fileId, String secondsTime, String sourcePhone,
                                 String forwardCount, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendAudioId(fileId);
        imContentUtil.appendBarTime(secondsTime);
        imContentUtil.appendSourcePhone(sourcePhone);
        imContentUtil.appendForwardCount(forwardCount);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);

    }


    /**
     * tcp发送视频
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param callback
     */
    public void sendVideoInGroup(int groupId, int groupType, String groupName, String fileId, String frameId, String name, String size,
                                 String time, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.addVideo(fileId, frameId, name, size, time);//body的内容
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }

    /**
     * tcp发送视频
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param callback
     */
    public void sendFileInGroup(int groupId, int groupType, String groupName, String fileId, String fileName, String fileSize,
                                ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * 发送群红包
     *
     * @param groupId
     * @param redUuid
     * @param value
     * @param callback
     */
    public void sendRedPackageInGroup(int groupId, int groupType, String groupName, String redUuid, String value, String title, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SEND_RED_ENVELOPE);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * 打开群红包
     *
     * @param groupId
     * @param sendUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void openRedPackageInGroup(int groupId, int groupType, String sendUuid, String redUuid,
                                      String value, String title, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getDisplayName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setDestUserId(sendUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_GET_RED_ENVELOPE);

        if (groupType == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_COMMUNITY);
        } else {
            msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);
        }

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);
        imContentUtil.appendRedPackageReceiveName(getDisplayName());
        imContentUtil.appendRedPackageDone("1");

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }

    public void reqRedPackageShareConfig(IGetListener listener) {
        String url = ColorsConfig.LISHI_SHARECONFIG;
        ContentValues params = new ContentValues();
        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);

    }


    public void reqRedPackageStandardConfig(IGetListener listener) {
        String url = ColorsConfig.LISHI_STANDARDCONFIG;
        ContentValues params = new ContentValues();
        ColorsConfig.commonYouMaiParams(params);
        HttpConnector.httpGet(url, params, listener);
    }


    public void reqRedPackageList(IGetListener listener) {
        String url = ColorsConfig.LISHI_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void reqSendSingleRedPackage(double moneySingle, String blessing, String pano,
                                        String transPassword, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getDisplayName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("lsType", 1);
        params.put("numberTotal", 1);
        params.put("moneySingle", moneySingle);
        params.put("blessing", blessing);
        params.put("pano", pano);
        params.put("transPassword", transPassword);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void reqSendGroupRedPackageFix(double moneySingle, int numberTotal, String blessing, String pano,
                                          String transPassword, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getDisplayName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("lsType", 1);
        params.put("numberTotal", numberTotal);
        params.put("moneySingle", moneySingle);
        params.put("blessing", blessing);
        params.put("pano", pano);
        params.put("transPassword", transPassword);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void reqSendGroupRedPackageRandom(double moneyTotal, int numberTotal,
                                             String blessing, String pano, String transPassword,
                                             IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getDisplayName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("lsType", 2);
        params.put("numberTotal", numberTotal);
        params.put("moneyTotal", moneyTotal);
        params.put("blessing", blessing);
        params.put("pano", pano);
        params.put("transPassword", transPassword);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void openRedPackage(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_OPEN;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void grabRedPackage(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_GRAB;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getDisplayName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void redPackageDetail(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void redSendPacketDetail(String month, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redReceivePacketDetail(String month, IGetListener listener) {
        String url = ColorsConfig.LISHI_RECEIVE_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redSendPacketList(String month, int page, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("page", page);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redReceivePacketList(String month, int page, IGetListener listener) {
        String url = ColorsConfig.LISHI_RECEIVE_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("page", page);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    /**
     * 验证支付密码
     */
    public void checkPayPwd(String password, IGetListener listener) {
        String nameSpace = ColorsConfig.CHECK_PAYPWD;

        String url = ColorsConfig.CP_MOBILE_HOST + nameSpace;
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();
        params.put("password", password);
        params.put("key", getKey());
        params.put("secret", getSecret());
        params.put("ve", "1.0.0");
        params.put("ts", ts);
        ColorsConfig.cpMobileSign(params, nameSpace);

        HttpConnector.httpGet(url, params, listener);
    }


    public void reqScoreHistory(String access_token, IGetListener listener) {
        String url = ColorsConfig.ICE_EVISIT_LIST;
        ContentValues params = new ContentValues();
        String userId = getUserId();
        params.put("customer_id", userId);
        params.put("access_token", access_token);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    /**
     * bind service callback
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof HuxinService.HuxinServiceBinder) {
                huxinService = (HuxinService.HuxinServiceBinder) service;
                binded = BIND_STATUS.BINDED;
                for (InitListener item : mInitListenerList) {
                    item.success();
                }
                mInitListenerList.clear();
                Log.v(TAG, "Service Connected...");
            }
        }

        // 连接服务失败后，该方法被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            huxinService = null;
            binded = BIND_STATUS.IDLE;
            for (InitListener item : mInitListenerList) {
                item.fail();
            }
            mInitListenerList.clear();
            Log.e(TAG, "Service Failed...");
        }
    };


    private Long findEntityId(String uuid, List<ContactBean> cacheList) {
        Long id = null;
        if (cacheList != null && cacheList.size() > 0) {
            for (ContactBean item : cacheList) {
                if (item.getUuid().equals(uuid)) {
                    id = item.getId();
                    break;
                }
            }
        }
        return id;
    }

    private int findEntityStatus(String uuid, List<ContactBean> cacheList) {
        int status = 0;
        if (cacheList != null && cacheList.size() > 0) {
            for (ContactBean item : cacheList) {
                if (item.getUuid().equals(uuid)) {
                    status = item.getStatus();
                    break;
                }
            }
        }
        return status;
    }


    private void buddyAgree(final String dstUuid) {
        reqUserInfo(dstUuid, new ProtoCallback.UserInfo() {
            @Override
            public void result(ContactBean contactBean) {
                String dstAvatar = contactBean.getAvatar();
                String dstNickName = contactBean.getDisplayName();
                String content = "我通过了你的好友请求，可以开始聊天了";

                CacheMsgBean cacheMsgBean = new CacheMsgBean()
                        .setMsgTime(System.currentTimeMillis())
                        .setMsgType(CacheMsgBean.BUDDY_AGREE)
                        .setMemberChanged(content)
                        .setTargetName(dstNickName)
                        .setTargetAvatar(dstAvatar)
                        .setTargetUuid(dstUuid);

                CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);

                ContactHelper.instance().updateStatusById(mContext, dstUuid, 1);
            }
        });
    }

    private void autoLogin() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            mProcessHandler.sendEmptyMessage(HANDLER_THREAD_AUTO_LOGIN);
        }
    }


    /**
     * 线程初始化
     */
    private void initHandler() {
        if (mProcessHandler == null) {
            HandlerThread handlerThread = new HandlerThread(
                    "handler looper Thread");
            handlerThread.start();
            mProcessHandler = new ProcessHandler(handlerThread.getLooper());
        }
    }

    /**
     * 子线程handler,looper
     *
     * @author Administrator
     */
    private class ProcessHandler extends Handler {

        public ProcessHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_THREAD_INIT_CONFIG_START:
                    initWork(mContext);
                    break;
                case HANDLER_THREAD_AUTO_LOGIN:
                    /*String phoneSim = AppUtils.getPhoneNumber(mContext);  // "+8618688159700"
                    String phoneCache = getPhoneNum();

                    String phone;
                    if (!StringUtils.isEmpty(phoneSim)) {
                        phone = phoneSim;
                    } else {
                        phone = phoneCache;
                    }*/

                    String phone = getPhoneNum();

                    if (!StringUtils.isEmpty(phone)) {
                        if (phone.startsWith("+86")) {
                            phone = phone.substring(3);
                        }
                        if (AppUtils.isMobileNum(phone) || phone.equals("4000")/*&& !getPhoneNum().equals(phone)*/) {

                        }
                    }

                    break;
                default:
                    break;
            }

        }

    }


}
