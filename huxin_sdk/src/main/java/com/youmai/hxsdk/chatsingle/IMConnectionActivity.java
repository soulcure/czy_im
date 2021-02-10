package com.youmai.hxsdk.chatsingle;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.CameraActivity;
import com.youmai.hxsdk.packet.RedPacketActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRedPackage;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.map.LocationActivity;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.activity.FileManagerActivity;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerRefreshUIListener;
import com.youmai.hxsdk.module.groupchat.ChatDetailsActivity;
import com.youmai.hxsdk.module.movierecord.MediaStoreUtils;
import com.youmai.hxsdk.module.picker.PhotoPickerManager;
import com.youmai.hxsdk.module.picker.PhotoPreviewActivity;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.service.download.bean.FileQueue;
import com.youmai.hxsdk.service.sendmsg.SendMsg;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.CompressImage;
import com.youmai.hxsdk.utils.CompressVideo;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.utils.VideoUtils;
import com.youmai.hxsdk.view.LinearLayoutManagerWithSmoothScroller;
import com.youmai.hxsdk.view.chat.InputMessageLay;
import com.youmai.smallvideorecord.model.OnlyCompressOverBean;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-07 10:31
 * Description:  im 界面
 */
public class IMConnectionActivity extends SdkBaseActivity implements
        IMMsgCallback, InputMessageLay.KeyBoardBarViewListener,
        PickerRefreshUIListener {
    /*
     * Const.
     */
    public static final String TAG = IMConnectionActivity.class.getSimpleName();

    //srsm add @20170214
    public static final String DST_NAME = "DST_NAME";
    public static final String DST_UUID = "DST_UUID";
    public static final String DST_PHONE = "DST_PHONE";
    public static final String DST_AVATAR = "DST_AVATAR";
    public static final String DST_USERNAME = "DST_USERNAME";
    public static final String EXTRA_SCROLL_POSITION = "EXTRA_SCROLL_POSITION";

    private static final int REQUEST_CODE_PICTURE = 200;
    private static final int REQUEST_CODE_LOCATION = 201;
    public static final int REQUEST_CODE_CAMERA = 202;
    public static final int REQUEST_CODE_CARD = 203;
    public static final int REQUEST_CODE_FORWAED = 204;
    public static final int REQUEST_REMIND_CODE = 205;
    public static final int REQUEST_CODE_DETAIL = 206;
    public static final int REQUEST_CODE_RED_PACKET = 207;

    private static final int MSG_GET_CONTACT_ID = 300;

    private final int CAMERA_PERMISSION_REQUEST = 400; //权限申请自定义码
    private final int RECORD_PERMISSION_REQUEST = 401; //权限申请自定义码

    public static final int RESULT_CODE_CLEAN = 501;

    public static final long MAX_SENDER_FILE = 50 * 1024 * 1024;

    //UI
    private RecyclerView recyclerView;
    LinearLayoutManagerWithSmoothScroller manager;
    public InputMessageLay keyboardLay;

    //Data.
    private IMListAdapter imListAdapter;

    private TextView tvTitle;
    private ImageView ivGroup; //建群

    private String dstNickName;  //目标昵称
    private String dstUuid;      //目标UUID
    private String dstPhone;      //目标手机号
    private String dstAvatar;     //目标的头像
    private String dstUserName;   //目标的username

    private boolean isPause = false;
    private boolean isMsgReceive = false;
    private boolean isOpenEmotion = false;

    private boolean isSmoothBottom = true;//是否滑动完成
    private boolean canSliding = true;//是否正可以滑动最底
    private long mScrollPosition;

    private LocalBroadcastManager localBroadcastManager;
    private LocalMsgReceiver mLocalMsgReceiver;

    private Context mContext;
    private boolean isOriginal = false;

    private NormalHandler mHandler;

    /**
     * 消息广播
     * 下载视频文件本地广播接收器
     */
    private class LocalMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SendMsgService.ACTION_SEND_MSG.equals(action)) {
                //消息发送的通知
                SendMsg sendMsg = intent.getParcelableExtra("data");
                CacheMsgBean cacheMsgBean = sendMsg.getMsg();
                imListAdapter.refreshItemUI(cacheMsgBean);

                if (!isPause && TextUtils.equals(sendMsg.getFrom(), SendMsgService.FROM_IM)) {
                    String type = intent.hasExtra("type") ? intent.getStringExtra("type") : null;
                    if (TextUtils.equals(type, SendMsgService.NOT_TCP_CONNECT)) {
                        //tcp尚未连接
                        showTcpTipDialog();
                    }
                }
            } else if (SendMsgService.ACTION_UPDATE_MSG.equals(intent.getAction())) {
                CacheMsgBean cacheMsgBean = intent.getParcelableExtra("CacheMsgBean");
                List<CacheMsgBean> cacheMsgBeanList = imListAdapter.getMsgBeanList();
                int size = cacheMsgBeanList.size();
                for (int i = 0; i < size; i++) {
                    CacheMsgBean oriMsgBean = cacheMsgBeanList.get(i);
                    if (cacheMsgBean.getId().equals(oriMsgBean.getId())) {
                        CacheMsgImage cacheImage = (CacheMsgImage) oriMsgBean.getJsonBodyObj();
                        cacheImage.setOriginalType(CacheMsgImage.SEND_IS_ORI_RECV_IS_ORI);
                        oriMsgBean.setJsonBodyObj(cacheImage);

                        CacheMsgHelper.instance().insertOrUpdate(mContext, oriMsgBean);
                        break;
                    }
                }

            } else if (SendMsgService.ACTION_NEW_MSG.equals(intent.getAction())) {
                CacheMsgBean cacheMsgBean = intent.getParcelableExtra("CacheNewMsg");
                imListAdapter.refreshIncomingMsgUI(cacheMsgBean);
            }

        }
    }


    private static class NormalHandler extends Handler {
        private final WeakReference<IMConnectionActivity> mTarget;

        NormalHandler(IMConnectionActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            IMConnectionActivity act = mTarget.get();
            switch (msg.what) {
                case MSG_GET_CONTACT_ID:
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_single);
        mContext = this;

        mHandler = new NormalHandler(this);

        Intent fromIntent = getIntent();

        dstNickName = fromIntent.getStringExtra(DST_NAME);
        dstUuid = fromIntent.getStringExtra(DST_UUID);
        dstPhone = fromIntent.getStringExtra(DST_PHONE);
        dstAvatar = fromIntent.getStringExtra(DST_AVATAR);
        dstUserName = fromIntent.getStringExtra(DST_USERNAME);

        if (StringUtils.isEmpty(dstUuid)) {
            dstUuid = HuxinSdkManager.instance().getUuid();
        }

        mScrollPosition = fromIntent.getLongExtra(EXTRA_SCROLL_POSITION, 0);

        //注册视频文件下载广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalMsgReceiver = new LocalMsgReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SendMsgService.ACTION_SEND_MSG);
        filter.addAction(SendMsgService.ACTION_UPDATE_MSG);
        filter.addAction(SendMsgService.ACTION_NEW_MSG);
        localBroadcastManager.registerReceiver(mLocalMsgReceiver, filter);

        initView();
        initData();
        IMMsgManager.instance().setImMsgCallback(this);

        HuxinSdkManager.instance().getStackAct().addActivity(this);

        IMMsgManager.instance().removeBadge(dstUuid);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaManager.resume();

        PickerManager.getInstance().setRefreshUIListener(this);
        IMMsgManager.instance().removeBadge(dstUuid);

        if (isPause && isMsgReceive) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mScrollPosition != 0) {
                        recyclerView.scrollToPosition(imListAdapter.getItemPosition(mScrollPosition));
                    } else {
                        imListAdapter.focusBottom(true);
                    }
                    mScrollPosition = 0;
                }
            }, 100);
        }
        isPause = false;
    }

    public void handleIntent(Intent intent) {
        dstNickName = intent.getStringExtra(DST_NAME);
        dstUuid = intent.getStringExtra(DST_UUID);

        if (!TextUtils.isEmpty(dstNickName)) {
            tvTitle.setText(dstNickName);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isOpenEmotion) {
            isOpenEmotion = false;
        } else {
            keyboardLay.hideAutoView();
            keyboardLay.setEditableState(false);
            keyboardLay.setMoreState(true);
        }
        MediaManager.pause();

        isPause = true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            boolean isGranted = true;
            for (int item : grantResults) {
                if (item == PackageManager.PERMISSION_DENIED) {
                    isGranted = false;
                    break;
                }
            }
            if (isGranted) {
                startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_CAMERA);
            } else {
                showPermissionDialog(this);
            }
        }
    }

    /**
     * 权限提示框
     *
     * @param context
     */
    private void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.permission_title))
                .setMessage(context.getString(R.string.permission_content));

        builder.setPositiveButton(context.getString(R.string.hx_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AppUtils.startAppSettings(mContext);
                        arg0.dismiss();
                    }
                });

        builder.setNegativeButton(context.getString(R.string.hx_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //拦截适配器里更多状态,恢复原始消息状态
            if (imListAdapter != null && imListAdapter.isShowSelect) {
                imListAdapter.cancelMoreStat();
                return true;
            }
            closeTip();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        MediaManager.release();
        imListAdapter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(mLocalMsgReceiver);
        localBroadcastManager = null;

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.removeMessages(MSG_GET_CONTACT_ID);
        }

        PickerManager.getInstance().setRefreshUIListener(null);

        //任务完成后，可以销毁消息服务
        Intent intent = new Intent(this, SendMsgService.class);
        intent.putExtra("flag", false);
        startService(intent);

        //srsm add
        IMMsgManager.instance().removeImMsgCallback(this);

        PhotoPickerManager.getInstance().clearMap();
        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }


    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(dstNickName);

        /*TextView tvError = findViewById(R.id.tv_error);
        if (!HuxinSdkManager.instance().isConnect()) {
            tvError.setVisibility(View.VISIBLE);
        }
        tvError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(APath.RE_LOGIN)
                        .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP)  //添加Flag
                        .withBoolean("login_out", true)
                        .navigation(mContext);
                HuxinSdkManager.instance().getStackAct().finishAllActivity();
            }
        });*/

        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        if (tvBack != null) {
            //tvBack.setVisibility(View.GONE);
            tvBack.setText("");
            tvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imListAdapter != null && imListAdapter.isShowSelect) {
                        imListAdapter.cancelMoreStat();
                        return;
                    }
                    closeTip();
                }
            });
        }

        ivGroup = (ImageView) findViewById(R.id.img_right);
        ivGroup.setVisibility(View.VISIBLE);
        ivGroup.setImageResource(R.drawable.tint_src_add_group_selector);
        ivGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IMConnectionActivity.this, ChatDetailsActivity.class);
                intent.putExtra(ChatDetailsActivity.DS_NAME, dstNickName);
                intent.putExtra(ChatDetailsActivity.DS_USER_AVATAR, dstAvatar);
                intent.putExtra(ChatDetailsActivity.DS_UUID, dstUuid);
                intent.putExtra(ChatDetailsActivity.DS_USERNAME, dstUserName);
                startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }
        });

        keyboardLay = (InputMessageLay) findViewById(R.id.keyboard_lay);
        keyboardLay.setOnKeyBoardBarViewListener(this);
        keyboardLay.setEditableState(false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isSmoothBottom) {
                        keyboardLay.hideAutoView();
                    }
                }
                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        if (!isSmoothBottom) {
                            isSmoothBottom = true;
                            if (imListAdapter.mThemeIndex != -1) {
                                imListAdapter.notifyItemChanged(imListAdapter.mThemeIndex);
                            }
                        }
                        break;
                    case SCROLL_STATE_DRAGGING:
                    case SCROLL_STATE_SETTLING:
                        break;
                }
            }
        });
    }

    private void initData() {
        manager = new LinearLayoutManagerWithSmoothScroller(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        imListAdapter = new IMListAdapter(this, recyclerView, dstUuid);
        imListAdapter.setMoreListener(new IMListAdapter.OnClickMoreListener() {
            @Override
            public void showMore(boolean isShow) {
            }

            @Override
            public void hasSelectMsg(boolean selected) {

            }
        });
        recyclerView.setAdapter(imListAdapter);

        if (mScrollPosition != 0) {
            recyclerView.scrollToPosition(imListAdapter.getItemPosition(mScrollPosition));
        } else {
            recyclerView.scrollToPosition(imListAdapter.getItemCount() - 1); // scroll to bottom
            imListAdapter.focusBottom(false, 30);//scrollToPosition在item超长时，不能滑到最底，这里补救
        }
        imListAdapter.setListener(new IMListAdapter.OnListener() {
            @Override
            public void smoothScroll(final int position, long delayMillis, final boolean isScrollerTop) {
                isSmoothBottom = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView.getLayoutManager() instanceof LinearLayoutManagerWithSmoothScroller) {
                            ((LinearLayoutManagerWithSmoothScroller) recyclerView.getLayoutManager()).setScrollerTop(false);
                        }
                        recyclerView.smoothScrollToPosition(position);
                    }
                }, delayMillis);
            }

            @Override
            public void hasEdit(boolean hasEdit) {
                canSliding = !hasEdit;
            }

            @Override
            public void hidden(final boolean hidden) {
                if (keyboardLay != null) {
                    keyboardLay.hideAutoView();
                }
            }

            @Override
            public void deleteMsgCallback(int type) {
                if (!isFinishing()) {
                    Intent intent = new Intent();
                    intent.putExtra("dstUuid", dstUuid);
                    intent.putExtra("isDeleteMsgType", type);
                    setResult(Activity.RESULT_OK, intent);
                }
            }

            @Override
            public void onHandleAvatarClick() {
            }
        });
    }

    PopupWindow popupWindow;
    long i = 0;


    private void showPopUp(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hx_im_more_lay, null);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.im_more_contact_lay);
        View view1 = view.findViewById(R.id.im_more_line);
        View popupWindowView = popupWindow.getContentView();
        TextView contactText = (TextView) popupWindowView.findViewById(R.id.im_more_contact_text);
        contactText.setText(R.string.hx_im_pop_contact_add);
        layout.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i + 2000 < System.currentTimeMillis()) {
                    i = System.currentTimeMillis();
                    //跳转联系人
                    popupWindow.dismiss();
                }
            }
        });

    }


    private ArrayList<String> docPaths = new ArrayList<>();

    /**
     * 文件选择器
     */
    private void showFileChooser() {
//        docPaths.clear();
//        String[] docs = {".txt", ".docx", ".doc", ".ppt", ".pptx", ".xls", ".xlsx", ".pdf"};
//        String[] videos = {".mp4"/*, ".rmvb", ".avi", ".3gp"*/};
//        String[] audios = {".mp3"};
//        String[] zips = {".zip", ".rar"};
//        //String[] apks = {".apk"};
//
//        FilePickerBuilder.getInstance().setMaxCount(1)
//                .setSelectedFiles(docPaths)
//                .setActivityTheme(R.style.HxSdkTheme)
//                .addFileSupport(getString(R.string.doc), docs)
//                .addFileSupport(getString(R.string.video), videos)
//                .addFileSupport(getString(R.string.audio), audios)
//                .addFileSupport(getString(R.string.zip), zips)
//                //.addFileSupport(getString(R.string.apk), apks)
//                .enableDocSupport(false)
//                .pickFile(this);

        docPaths.clear();
        PickerManager.getInstance().addDocTypes();
        Intent intent = new Intent(this, FileManagerActivity.class);
        intent.putExtra("dstUuid", dstUuid);
        startActivity(intent);
    }


    //发送消息(启动后台消息服务)
    private void sendMsg(CacheMsgBean msg) {
        //HuxinSdkManager.instance().reLoginDialog();
        Intent intent = new Intent(this, SendMsgService.class);
        intent.putExtra("data", msg);
        intent.putExtra("data_from", SendMsgService.FROM_IM);
        startService(intent);
    }

    public CacheMsgBean getBaseMsg() {
        return new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setMsgStatus(CacheMsgBean.SEND_GOING)
                .setSenderUserId(HuxinSdkManager.instance().getUuid())
                .setSenderRealName(HuxinSdkManager.instance().getRealName())
                .setSenderAvatar(HuxinSdkManager.instance().getHeadUrl())
                .setSenderMobile(HuxinSdkManager.instance().getPhoneNum())
                .setSenderSex(HuxinSdkManager.instance().getSex())
                .setSenderUserName(HuxinSdkManager.instance().getUserName())
                .setReceiverUserId(dstUuid)
                .setTargetName(dstNickName)
                .setTargetUserName(dstUserName)
                .setTargetAvatar(dstAvatar)
                .setTargetUuid(dstUuid);
    }

    /**
     * 发送文字(或表情)
     */
    private void sendTxt(final String content, int refContent, boolean isInput) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        if (isInput) {
            cacheMsgBean.setMsgType(CacheMsgBean.SEND_TEXT).setJsonBodyObj(new CacheMsgTxt().setMsgTxt(content));
        } else {
            cacheMsgBean.setMsgType(CacheMsgBean.SEND_EMOTION).setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, refContent));
        }

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }


    /**
     * 发送位置
     */
    public void sendMap(Intent data) {
        final String url = data.getStringExtra("url");
        final double longitude = data.getDoubleExtra("longitude", 0);
        final double latitude = data.getDoubleExtra("latitude", 0);
        final int zoomLevel = data.getIntExtra("zoom_level", 0);
        final String address = data.getStringExtra("address");

        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.SEND_LOCATION)
                .setJsonBodyObj(new CacheMsgMap()
                        .setLongitude(longitude)
                        .setLatitude(latitude)
                        .setScale(zoomLevel)
                        .setAddress(address)
                        .setImgUrl(url));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 发送语音
     *
     * @param seconds
     * @param filePath
     */
    private void sendVoice(float seconds, String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.SEND_VOICE)
                .setJsonBodyObj(new CacheMsgVoice().setVoiceTime(seconds + "").setVoicePath(filePath));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 相册选择回调或拍照回调
     *
     * @param path
     */
    private void sendTakenPic(String path, boolean isOriginal) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.SEND_IMAGE)
                .setJsonBodyObj(new CacheMsgImage()
                        .setFilePath(path)
                        .setOriginalType(isOriginal ? CacheMsgImage.SEND_IS_ORI : CacheMsgImage.SEND_NOT_ORI));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 发送文件.
     *
     * @param file
     */

    public void sendFile(File file) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.SEND_FILE)
                .setJsonBodyObj(new CacheMsgFile()
                        .setFilePath(file.getAbsolutePath())
                        .setFileSize(file.length())
                        .setFileName(file.getName())
                        .setFileRes(IMHelper.getFileImgRes(file.getName(), false)));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 发送视频
     */
    public void sendVideo(String filePath, String framePath, long millisecond) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.SEND_VIDEO)
                .setJsonBodyObj(new CacheMsgVideo()
                        .setVideoPath(filePath)
                        .setFramePath(framePath)
                        .setTime(millisecond));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }


    /**
     * 发送红包
     */
    public void sendRedPackage(Intent data) {
        final String value = data.getStringExtra("value");
        final String redTitle = data.getStringExtra("redTitle");
        final String redUuid = data.getStringExtra("redUuid");

        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.SEND_REDPACKAGE)
                .setJsonBodyObj(new CacheMsgRedPackage()
                        .setRedStatus(CacheMsgRedPackage.RED_PACKET_REVIEW)
                        .setValue(value)
                        .setRedUuid(redUuid)
                        .setRedTitle(redTitle));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    //录音
    private boolean isRecordPermission() {
        boolean res = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<>(2);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.RECORD_AUDIO);
            }

            if (list.size() > 0) {
                String[] array = new String[list.size()];
                list.toArray(array); // fill the array
                ActivityCompat.requestPermissions(this, array, RECORD_PERMISSION_REQUEST);
                res = false;
            }
        }
        return res;
    }


    //拍照
    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<>(3);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.RECORD_AUDIO);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.CAMERA);
            }

            if (list.size() > 0) {
                String[] array = new String[list.size()];
                list.toArray(array); // fill the array
                ActivityCompat.requestPermissions(this, array, CAMERA_PERMISSION_REQUEST);
            } else {
                startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_CAMERA);
            }
        } else {
            startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_CAMERA);
        }
    }


    /**
     * 发位置
     */
    private void sendLocation() {
        Intent intent = new Intent();
        intent.putExtra(LocationActivity.FROM_TO_IM, true);
        intent.putExtra(LocationActivity.DST_UUID, dstUuid);
        intent.setClass(this, LocationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOCATION);
    }


    /**
     * 发红包
     */
    private void sendRedPacket() {
        Intent intent = new Intent(this, RedPacketActivity.class);
        intent.putExtra(RedPacketActivity.TARGET_ID, dstUuid);
        intent.putExtra(RedPacketActivity.TARGET_NAME, dstNickName);
        intent.putExtra(RedPacketActivity.TARGET_AVATAR, dstAvatar);
        startActivityForResult(intent, REQUEST_CODE_RED_PACKET);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PhotoPreviewActivity.REQUEST_CODE_PHOTO) { //图片
            ArrayList<String> photoPaths = new ArrayList<>();
            if (data == null) {
                isOriginal = PhotoPickerManager.getInstance().isOriginal();
                photoPaths.addAll(PhotoPickerManager.getInstance().getPaths());
            } else {
                isOriginal = data.getBooleanExtra(FilePickerConst.KEY_IS_ORIGINAL, false);
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            }
            PhotoPickerManager.getInstance().clear();
            if (photoPaths.size() == 0) {
                return;
            }

            String path = photoPaths.get(0);
            if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                    || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
                new CompressVideoAsyncTask().execute(path);
            } else {
                if (isOriginal) {
                    for (String item : photoPaths) {
                        sendTakenPic(item, isOriginal);
                    }
                } else {
                    //final ArrayList<String> thumbList = new ArrayList<>();
                    final int size = photoPaths.size();
                    for (int i = 0; i < size; i++) {
                        //final int index = i;
                        Luban.with(mContext)
                                .load(photoPaths.get(i))
                                .ignoreBy(100)
                                .setTargetDir(FileConfig.getThumbImagePaths())
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        LogUtils.d("wul", "getImageThumb --- onSuccess:" + file.getAbsolutePath());
                                        /*thumbList.add(file.getAbsolutePath());
                                        if (index == (size - 1)) {
                                            new CompressImageAsyncTask().execute(thumbList.toArray(new String[thumbList.size()]));
                                        }*/
                                        sendTakenPic(file.getAbsolutePath(), isOriginal);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        LogUtils.d("wul", "getImageThumb --- onError:" + e.getMessage());
                                    }
                                }).launch();
                    }
                }
            }

        } else if (requestCode == REQUEST_CODE_PICTURE
                && resultCode == Activity.RESULT_OK) { //图片
            final String filePath = data.getStringExtra("path");
            sendTakenPic(filePath, isOriginal);
        } else if (requestCode == REQUEST_CODE_LOCATION
                && resultCode == Activity.RESULT_OK) {  //地图
            sendMap(data);
        } else if (requestCode == FilePickerConst.REQUEST_CODE_DOC
                && resultCode == Activity.RESULT_OK) {
            //交换名片编辑名片返回 发送(用不到)
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            //拍照回来后
            if (resultCode == CameraActivity.RESULT_CODE_IMAGE) {
                //图片
                String path = data.getStringExtra("filePath");
                sendTakenPic(path, isOriginal);
            } else if (resultCode == CameraActivity.RESULT_CODE_VIDEO) {
                //视频
                String framePath = data.getStringExtra("framePath");
                String path = data.getStringExtra("filePath");
                long millisecond = VideoUtils.instance(getBaseContext()).getVideoTime(path);
                if (millisecond == 0L) {
                    //拿不到系统读取时间，再用自定义计算时间
                    millisecond = data.getLongExtra("millisecond", 0L);
                }
                sendVideo(path, framePath, millisecond);
            } else if (resultCode == CameraActivity.RESULT_CODE_ERROR) {
                Toast.makeText(this, "相机有误，请返回重试!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_FORWAED
                && resultCode == 200) {
            //批量转发后的回调
            imListAdapter.cancelMoreStat();

        } else if (requestCode == REQUEST_CODE_DETAIL) {
            if (resultCode == RESULT_CODE_CLEAN) {
                imListAdapter.clearMsg();
            }

        } else if (requestCode == REQUEST_CODE_RED_PACKET
                && resultCode == Activity.RESULT_OK) {  //红包
            // send red
            sendRedPackage(data);
        }
    }


    //返回事件
    void closeTip() {
        if (isSend) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(false)// 屏蔽返回键
                    .setTitle(getString(R.string.hx_prompt))
                    .setMessage(getString(R.string.hx_upload_file));

            builder.setPositiveButton(getString(R.string.hx_agree),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            arg0.dismiss();
                            onBackPressed();
                        }
                    });

            builder.setNegativeButton(getString(R.string.hx_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            arg0.dismiss();
                        }
                    });
            builder.show();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onRefresh(ArrayList<String> paths, int resultCode) {
        Log.e("YW", "resultCode: " + resultCode);
        if (paths != null && paths.size() > 0 && resultCode == FilePickerConst.IM_REQUEST_CALLBACK) {
            final File file = new File(paths.get(0));
            if (file.exists()) {
                if (file.length() > MAX_SENDER_FILE) {
                    Toast.makeText(this, R.string.hx_imadapter_file, Toast.LENGTH_SHORT).show();
                } else {
                    if (!CommonUtils.isNetworkAvailable(this)) {
                        ToastUtil.showToast(this, getString(R.string.hx_imadapter_wifi_break));
                        return;
                    }
                    sendFile(file);
                }
            } else {
                Toast.makeText(this, "文件不存在!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBuddyMsgCallback(CacheMsgBean cacheMsgBean) {
        //刷新界面
        if (!isFinishing()) {
            if (cacheMsgBean.getSenderUserId().equals(dstUuid)
                    && cacheMsgBean.getGroupId() == 0) {
                imListAdapter.refreshIncomingMsgUI(cacheMsgBean);
                isMsgReceive = true;
            }
        }
    }


    @Override
    public void onCommunityMsgCallback(CacheMsgBean cacheMsgBean) {

    }

    @Override
    public void onOwnerMsgCallback(CacheMsgBean cacheMsgBean) {

    }


    // 初始化、执行上传
    private volatile boolean isSend = false;


    private void showTcpTipDialog() {
    }

    private void refreshFinishVideo(FileQueue fileQueue) {
        long mid = fileQueue.getMid();
        CacheMsgBean cacheMsgBean = CacheMsgHelper.instance().queryById(this, mid);
        if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
            CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
            cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
            cacheMsgBean.setProgress(fileQueue.getPro());
            imListAdapter.refreshItemUI(cacheMsgBean);
        }
    }


    /**
     * 监听软件的状态
     */
    @Override
    public void OnKeyBoardStateChange(int state, int height) {
        if (state == InputMessageLay.KEYBOARD_STATE_NONE) {
            //键盘隐藏
        } else if (state == InputMessageLay.KEYBOARD_STATE_FUNC) {
            //输入布局显示
            if (isSmoothBottom && canSliding) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        imListAdapter.focusBottom(false);
                    }
                });
            }
        } else {
            //键盘显示
            if (isSmoothBottom && canSliding) {
                if (height > 0) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imListAdapter.focusBottom(false);
                                }
                            }, 300);
                        }
                    });
                } else if (height == -1 && keyboardLay.keyHasFocus()) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imListAdapter.focusBottom(false);
                                }
                            }, 300);
                        }
                    });
                }
            }

        }
    }

    /**
     * 输入框点击发送语音
     */
    @Override
    public void onKeyBoardVoice(String voiceFile, int time) {
        sendVoice(time, voiceFile);
    }

    /**
     * 输入框点击发送信息
     *
     * @param msg 输入的内容
     */
    @Override
    public void onKeyBoardSendMsg(String msg) {
        sendTxt(msg, -1, true);
    }

    /**
     * 输入框点击添加表情
     */
    @Override
    public void onKeyBoardAddEmotion() {
    }

    /**
     * 输入框点击发送表情
     */
    @Override
    public void onKeyBoardEmotion(String content, int refContent) {
        sendTxt(content, refContent, false);
    }

    @Override
    public void onScroll() {
        imListAdapter.focusBottom(false);
    }


    @Override
    public void onGroupAt() {
    }

    @Override
    public void onGroupAtDel() {

    }

    /**
     * 输入框点击更多菜单的点击事件
     */
    @Override
    public void onKeyBoardMore(int type) {
        if (type == InputMessageLay.TYPE_PHOTO) {
            PhotoPreviewActivity.start(this);
        } else if (type == InputMessageLay.TYPE_CAMERA) {
            useCamera();
        } else if (type == InputMessageLay.TYPE_LOCATION) {
            sendLocation();
        } else if (type == InputMessageLay.TYPE_FILE) {
            showFileChooser();
        } else if (type == InputMessageLay.TYPE_RED_PACKET) {
            //发送红包
            sendRedPacket();
        }

    }

    @Override
    public boolean onClickVoice() {
        return isRecordPermission();
    }

    @Override
    public void onMoreForward() {
        TreeMap<Integer, CacheMsgBean> selectMsg = imListAdapter.getSelectMsg();
        ArrayList<CacheMsgBean> msgList = new ArrayList<>();
        for (Object o : selectMsg.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            CacheMsgBean val = (CacheMsgBean) entry.getValue();
            msgList.add(val);
        }

        Intent intent = new Intent();
        intent.setAction("com.youmai.huxin.recent");
        intent.putExtra("type", "forward_msg_list");
        intent.putParcelableArrayListExtra("dataList", msgList);
        startActivityForResult(intent, REQUEST_CODE_FORWAED);
    }

    @Override
    public void onMoreGarbage() {

    }

    @Override
    public void onDrivingExit() {

    }


    /**
     * 压缩图片
     */
    private class CompressImageAsyncTask extends AsyncTask<String[], Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String[]... params) {
            ArrayList<String> res = new ArrayList<>();
            for (String item : params[0]) {
                res.add(CompressImage.compressImage(item));

                if (!isOriginal) {
                    File file = new File(item);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<String> res) {
            for (String item : res) {
                if (TextUtils.isEmpty(item)) {
                    Toast.makeText(mContext, "未找到文件", Toast.LENGTH_SHORT).show();
                } else {
                    sendTakenPic(item, isOriginal);
                }

            }
        }
    }

    /**
     * 压缩视频
     */
    private class CompressVideoAsyncTask extends AsyncTask<String, Void, OnlyCompressOverBean> {
        @Override
        protected void onPreExecute() {
            showProgress("", "视频压缩中...", -1);
        }

        @Override
        protected OnlyCompressOverBean doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                return null;
            } else {
                File video = new File(url);
                if (!video.exists()) {
                    return null;
                }

                OnlyCompressOverBean bean = CompressVideo.compressVideo(params[0]);
                String videoPath = AbFileUtil.renameVideoTo2(bean.getVideoPath());
                String picPath = AbFileUtil.renamePicTo2(bean.getPicPath());

                if (videoPath.toLowerCase().endsWith(".m")) {
                    try {
                        String[] videoParams = MediaStoreUtils.getVideoParams(videoPath);
                        long time = Long.parseLong(videoParams[0]);
                        bean.setVideoTime(time);
                        bean.setVideoPath(videoPath);
                        bean.setPicPath(picPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return bean;
            }
        }

        @Override
        protected void onPostExecute(OnlyCompressOverBean res) {
            hideProgress();
            if (res == null) {
                Toast.makeText(mContext, "未找到文件", Toast.LENGTH_SHORT).show();
            } else {
                sendVideo(res.getVideoPath(), res.getPicPath(), res.getVideoTime());
            }

        }
    }

    private ProgressDialog mProgressDialog;

    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!TextUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        if (Build.VERSION.SDK_INT >= 17 && !isDestroyed() && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}
