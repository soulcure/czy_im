package com.youmai.hxsdk.forward;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.MessageAdapter;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.dialog.HxSendDialog;
import com.youmai.hxsdk.dialog.HxSendMapDialog;
import com.youmai.hxsdk.dialog.HxSendVideoDialog;
import com.youmai.hxsdk.dialog.HxSendVoiceDialog;
import com.youmai.hxsdk.dialog.listener.SendDialogListener;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.module.videoplayer.VideoPlayerActivity;
import com.youmai.hxsdk.module.videoplayer.bean.VideoDetailInfo;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.TimeUtils;

import java.util.List;

/**
 * 主页-沟通
 * A simple {@link Fragment} subclass.
 */
public class ForwardFragment extends Fragment implements ForwardImp {
    private final String TAG = ForwardFragment.class.getSimpleName();

    private final int LOADER_ID_GEN_MESSAGE_LIST = 100;

    private String msgFrom;//消息从哪来
    private String type;

    private RecyclerView recyclerView;
    private ForwardAdapter forwardAdapter;

    private CacheMsgBean forwardMsg;//单条消息

    // 换号登录需要去判断改变
    private LinearLayoutManager mLinearLayoutManager;


    String dstUuid;
    String dstNickName;
    String dstUserName;
    String dstAvatar;

    private String groupName;  //群组名称
    private int groupId;      //群组ID


    public ForwardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HuxinSdkManager.instance().chatMsgFromCache(this, 0,
                new ProtoCallback.CacheMsgCallBack() {
                    @Override
                    public void result(List<ExCacheMsgBean> data) {
                        forwardAdapter.changeMessageList(data);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_forward, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("msg_from")) {
            msgFrom = intent.getStringExtra("msg_from");
        }
        if (intent.hasExtra("data")) {
            forwardMsg = intent.getParcelableExtra("data");
        }
        type = intent.getStringExtra("type");

        initView(view);
    }


    private void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        forwardAdapter = new ForwardAdapter(getActivity());
        recyclerView.setAdapter(forwardAdapter);
        forwardAdapter.setOnItemClickListener(new ForwardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExCacheMsgBean bean, int position) {
                if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_SINGLE) {
                    dstUuid = bean.getTargetUuid();
                    dstNickName = bean.getDisplayName();
                    dstUserName = bean.getTargetUserName();
                    dstAvatar = bean.getTargetAvatar();

                    forwardMsg(dstNickName);
                } else if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_GROUP) {
                    groupId = bean.getGroupId();
                    groupName = bean.getDisplayName();


                    forwardMsg(dstNickName);
                }
            }
        });
    }


    @Override
    public void forwardTxt(String titleStr) {
        final String content = ((CacheMsgTxt) forwardMsg.getJsonBodyObj()).getMsgTxt();
        HxSendDialog.Builder builder = new HxSendDialog.Builder(getContext())
                .setTitle(titleStr)
                .setContent(content)
                .setOnListener(new HxSendDialog.OnClickListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        sendMsg(forwardMsg);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {

                    }
                });
        builder.create().show();
    }


    @Override
    public void forwardEmotion(String titleStr) {
        CacheMsgEmotion jsonBody = (CacheMsgEmotion) forwardMsg.getJsonBodyObj();
        final int emoRes = jsonBody.getEmotionRes();
        final String emoUri = "";
        HxSendDialog.Builder builder = new HxSendDialog.Builder(getContext())
                .setTitle(titleStr)
                .setEmotionRes(emoRes)
                .setEmotionUri(emoUri)
                .setOnListener(new HxSendDialog.OnClickListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        sendMsg(forwardMsg);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {

                    }
                });
        builder.create().show();
    }

    HxSendVoiceDialog voiceDialog;

    @Override
    public void forwardVoice(String titleStr) {
        final CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) forwardMsg.getJsonBodyObj();
        String time = cacheMsgVoice.getVoiceTime();
        HxSendVoiceDialog.Builder builder = new HxSendVoiceDialog.Builder(getContext())
                .setTitle(titleStr)
                .setTime(time)
                .setPlayListener(new HxSendVoiceDialog.PlayListener() {
                    @Override
                    public void play() {
                        //播放声音
                        if (voiceDialog != null) {
                            if (MediaManager.isPlaying()) {
                                MediaManager.pause();
                                voiceDialog.play();
                            } else {
                                //开始播放
                                voiceDialog.play();
                                MediaManager.playSound(cacheMsgVoice.getVoicePath(), new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        if (voiceDialog != null) {
                                            voiceDialog.restStart();
                                        }
                                    }
                                });
                            }
                        }
                    }
                })
                .setOnListener(new SendDialogListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        if (MediaManager.isPlaying()) {
                            MediaManager.release();
                        }
                        sendMsg(forwardMsg);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {
                        if (MediaManager.isPlaying()) {
                            MediaManager.release();
                        }
                    }
                });
        voiceDialog = builder.create();
        voiceDialog.show();
        voiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (MediaManager.isPlaying()) {
                    MediaManager.release();
                }
            }
        });
    }

    @Override
    public void forwardMap(String titleStr) {
        CacheMsgMap cacheMsgMap = (CacheMsgMap) forwardMsg.getJsonBodyObj();
        String content = cacheMsgMap.getImgUrl();
        String addressStr = cacheMsgMap.getAddress();
        HxSendMapDialog.Builder builder = new HxSendMapDialog.Builder(getContext())
                .setTitle(titleStr)
                .setAddress(addressStr)
                .setPicUrl(content)
                .setOnListener(new SendDialogListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        sendMsg(forwardMsg);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {

                    }
                });
        builder.create().show();
    }


    @Override
    public void forwardPicture(String titleStr) {
        String filePath = ((CacheMsgImage) forwardMsg.getJsonBodyObj()).getFilePath();
        String fid = ((CacheMsgImage) forwardMsg.getJsonBodyObj()).getFid();
        //优先显示本地
        final String url;
        if (TextUtils.isEmpty(filePath)) {
            url = AppConfig.getImageUrl(fid);
        } else {
            url = filePath;
        }
        HxSendDialog.Builder builder = new HxSendDialog.Builder(getContext())
                .setTitle(titleStr)
                .setPicUrl(url)
                .setOnListener(new HxSendDialog.OnClickListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        sendMsg(forwardMsg);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void forwardFile(String titleStr) {
        final String content = "已选择" +
                ((CacheMsgFile) forwardMsg.getJsonBodyObj()).getFileName();
        HxSendDialog.Builder builder = new HxSendDialog.Builder(getContext())
                .setTitle(titleStr)
                .setContent(content)
                .setOnListener(new HxSendDialog.OnClickListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        sendMsg(forwardMsg);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void forwardVideo(String titleStr) {
        CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) forwardMsg.getJsonBodyObj();
        String frameId = cacheMsgVideo.getFrameId();
        String videoId = cacheMsgVideo.getVideoId();
        String time = TimeUtils.getTimeFromMillisecond(cacheMsgVideo.getTime());
        String frameUrl = AppConfig.getImageUrl(frameId);
        final String videoUrl = AppConfig.getImageUrl(videoId);
        HxSendVideoDialog.Builder builder = new HxSendVideoDialog.Builder(getContext())
                .setTitle(titleStr)
                .setFrameUrl(frameUrl)
                .setTime(time)
                .setPlayListener(new HxSendVideoDialog.PlayListener() {
                    @Override
                    public void play() {
                        //点击播放
                        VideoDetailInfo info = new VideoDetailInfo();
                        info.setVideoPath(forwardMsg.isRightUI() ? videoUrl : videoUrl); //视频路径
                        Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
                        intent.putExtra("info", info);
                        getContext().startActivity(intent);
                    }
                })
                .setOnListener(new SendDialogListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        sendMsg(forwardMsg);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {

                    }
                });
        builder.create().show();
    }


    /**
     * 转发操作
     */
    private void forwardMsg(final String name) {

        String titleStr = String.format(getString(R.string.hx_dialog_send_person), name);

        int msgType = forwardMsg.getMsgType();
        if (msgType == CacheMsgBean.SEND_TEXT
                || msgType == CacheMsgBean.RECEIVE_TEXT) {
            //文本
            forwardTxt(titleStr);
        } else if (msgType == CacheMsgBean.SEND_EMOTION
                || msgType == CacheMsgBean.RECEIVE_EMOTION) {
            //表情
            forwardEmotion(titleStr);
        } else if (msgType == CacheMsgBean.SEND_VOICE
                || msgType == CacheMsgBean.RECEIVE_VOICE) {
            //语音
            forwardVoice(titleStr);
        } else if (msgType == CacheMsgBean.SEND_LOCATION
                || msgType == CacheMsgBean.RECEIVE_LOCATION) {
            //位置
            forwardMap(titleStr);
        } else if (msgType == CacheMsgBean.SEND_IMAGE
                || msgType == CacheMsgBean.RECEIVE_IMAGE) {
            //图片
            forwardPicture(titleStr);
        } else if (msgType == CacheMsgBean.SEND_FILE
                || msgType == CacheMsgBean.RECEIVE_FILE) {
            //文件
            forwardFile(titleStr);
        } else if (msgType == CacheMsgBean.SEND_VIDEO
                || msgType == CacheMsgBean.RECEIVE_VIDEO) {
            //视频
            forwardVideo(titleStr);
        }

    }


    //发送消息
    private void sendMsg(CacheMsgBean msg) {
        msg.setId(null);
        msg.setMsgTime(System.currentTimeMillis())
                .setMsgStatus(CacheMsgBean.SEND_GOING)
                .setSenderUserId(HuxinSdkManager.instance().getUuid())
                .setSenderRealName(HuxinSdkManager.instance().getRealName())
                .setSenderAvatar(HuxinSdkManager.instance().getHeadUrl())
                .setSenderMobile(HuxinSdkManager.instance().getPhoneNum())
                .setSenderSex(HuxinSdkManager.instance().getSex())
                .setSenderUserName(HuxinSdkManager.instance().getUserName());

        if (groupId > 0) {
            msg.setGroupId(groupId)
                    .setTargetName(groupName)
                    .setTargetUuid(groupId + "");
        } else {
            msg.setReceiverUserId(dstUuid)
                    .setTargetName(dstNickName)
                    .setTargetUserName(dstUserName)
                    .setTargetAvatar(dstAvatar)
                    .setTargetUuid(dstUuid);
        }


        CacheMsgHelper.instance().insertOrUpdate(getContext(), msg);
        Intent intent = new Intent(getContext(), SendMsgService.class);

        intent.putExtra("data_from", SendMsgService.FROM_IM);
        intent.putExtra("data", msg);
        getActivity().startService(intent);
        forwardSuccess(100);
    }

    private void forwardSuccess(int resultCode) {
        getActivity().setResult(resultCode);
        getActivity().finish();
    }
}
