package com.youmai.hxsdk.chatgroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.IMFilePreviewActivity;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.CropMapActivity;
import com.youmai.hxsdk.activity.PictureIndicatorActivity;
import com.youmai.hxsdk.packet.RedPacketDetailActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.helper.ContactHelper;
import com.youmai.hxsdk.dialog.HxRedPacketDialog;
import com.youmai.hxsdk.entity.GroupAtItem;
import com.youmai.hxsdk.entity.red.OpenRedPacketResult;
import com.youmai.hxsdk.http.DownloadListener;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRedPackage;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.module.videoplayer.VideoPlayerActivity;
import com.youmai.hxsdk.module.videoplayer.bean.VideoDetailInfo;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.QiniuUrl;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.LinearLayoutManagerWithSmoothScroller;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;
import com.youmai.hxsdk.view.progressbar.CircleProgressView;
import com.youmai.hxsdk.view.text.CopeTextView;
import com.youmai.hxsdk.view.tip.TipView;
import com.youmai.hxsdk.view.tip.bean.TipBean;
import com.youmai.hxsdk.view.tip.listener.ItemListener;
import com.youmai.hxsdk.view.tip.tools.TipsType;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import jp.wasabeef.glide.transformations.MaskTransformation;


/**
 * Created by colin on 2018/3/23.
 * im message adapter
 */
public class IMGroupAdapter extends RecyclerView.Adapter {

    private static final String TAG = IMGroupAdapter.class.getSimpleName();

    private static final int TXT_LEFT = 1; //文字左
    private static final int IMG_LEFT = 2;  //图片左
    private static final int MAP_LEFT = 3; //地图左
    private static final int VOICE_LEFT = 4; //声音左
    private static final int VIDEO_LEFT = 5;//视频左
    private static final int FILE_LEFT = 6; //文件左
    private static final int RED_PACKAGE_LEFT = 7; //红包左

    private static final int TXT_RIGHT = 11; //文字右
    private static final int IMG_RIGHT = 12; //图片右
    private static final int MAP_RIGHT = 13; //地图右
    private static final int VIDEO_RIGHT = 14;//视频右
    private static final int VOICE_RIGHT = 15; //声音右
    private static final int FILE_RIGHT = 16;//文件右
    private static final int RED_PACKAGE_RIGHT = 17; //红包右

    private static final int MEMBER_CHANGED = 101;//群成员修改
    private static final int NAME_CHANGED = 102;//群名修改
    private static final int OWNER_CHANGED = 103;//群主转让
    private static final int RED_PACKET_OPENED = 104;//红包被打开
    private static final int OPEN_RED_PACKET_SUCCESS = 105;//我领取了红包

    private static final int HANDLER_REFRESH_PROGREE = 0;

    private Activity mAct;
    private RecyclerView mRecyclerView;
    private int mGroupId;
    private List<CacheMsgBean> mImBeanList;
    public int mThemeIndex = -1;

    private OnListener listener;

    public boolean isShowSelect = false;//用于控制显示更多的选项框

    private TreeMap<Integer, CacheMsgBean> selectMsg = new TreeMap<>();

    private OnClickMoreListener moreListener;
    private UIHandler mHandler;

    public IMGroupAdapter(Activity act, RecyclerView recyclerView, int groupId) {
        mAct = act;
        mHandler = new UIHandler(this);

        mRecyclerView = recyclerView;
        mGroupId = groupId;
        //srsm add

        mImBeanList = CacheMsgHelper.instance().toQueryCacheMsgListAndSetRead(mAct, groupId, true);

        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRecyclerView.getItemAnimator().setAddDuration(0);
        mRecyclerView.getItemAnimator().setRemoveDuration(0);
        mRecyclerView.getItemAnimator().setMoveDuration(0);
    }

    public List<CacheMsgBean> getMsgBeanList() {
        return mImBeanList;
    }

    public void clearMsg() {
        mImBeanList = CacheMsgHelper.instance().toQueryCacheMsgListAndSetRead(mAct, mGroupId, true);
        notifyDataSetChanged();
    }

    public TreeMap<Integer, CacheMsgBean> getSelectMsg() {
        return selectMsg;
    }

    public void setListener(OnListener listener) {
        this.listener = listener;
    }

    public void setMoreListener(OnClickMoreListener moreListener) {
        this.moreListener = moreListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mAct);
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TXT_LEFT:
                view = inflater.inflate(R.layout.hx_group_im_left_txt_item, parent, false);
                holder = new TxtViewHolder(view);
                break;
            case TXT_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_txt_item, parent, false);
                holder = new TxtViewHolder(view);
                break;
            case IMG_LEFT:
                view = inflater.inflate(R.layout.hx_group_im_left_img_item, parent, false);
                holder = new ImgViewHolder(view);
                break;
            case IMG_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_img_item, parent, false);
                holder = new ImgViewHolder(view);
                break;
            case MAP_LEFT:
                view = inflater.inflate(R.layout.hx_group_im_left_map_item, parent, false);
                holder = new MapViewHolder(view);
                break;
            case MAP_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_map_item, parent, false);
                holder = new MapViewHolder(view);
                break;
            case VOICE_LEFT:
                view = inflater.inflate(R.layout.hx_group_im_left_voice_item, parent, false);
                holder = new VoiceViewHolder(view);
                break;
            case VOICE_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_voice_item, parent, false);
                holder = new VoiceViewHolder(view);
                break;
            case FILE_LEFT:
                view = inflater.inflate(R.layout.hx_group_im_left_file_item, parent, false);
                holder = new FileViewHolder(view);
                break;
            case FILE_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_file_item, parent, false);
                holder = new FileViewHolder(view);
                break;
            case VIDEO_LEFT:
                view = inflater.inflate(R.layout.hx_group_im_left_video_item, parent, false);
                holder = new VideoViewHolder(view);
                break;
            case VIDEO_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_video_item, parent, false);
                holder = new VideoViewHolder(view);
                break;
            case RED_PACKAGE_LEFT:
                view = inflater.inflate(R.layout.hx_group_im_left_red_package_item, parent, false);
                holder = new RedPackageHolder(view);
                break;
            case RED_PACKAGE_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_red_package_item, parent, false);
                holder = new RedPackageHolder(view);
                break;
            case MEMBER_CHANGED:
                view = inflater.inflate(R.layout.hx_group_im_member_change_item, parent, false);
                holder = new GroupChangedViewHolder(view);
                break;
            case NAME_CHANGED:
                view = inflater.inflate(R.layout.hx_group_im_member_change_item, parent, false);
                holder = new GroupChangedViewHolder(view);
                break;
            case OWNER_CHANGED:
                view = inflater.inflate(R.layout.hx_group_im_member_change_item, parent, false);
                holder = new GroupChangedViewHolder(view);
                break;
            case RED_PACKET_OPENED:
                view = inflater.inflate(R.layout.hx_red_packet_opened_item, parent, false);
                holder = new RedPacketOpenedViewHolder(view);
                break;
            case OPEN_RED_PACKET_SUCCESS:
                view = inflater.inflate(R.layout.hx_red_packet_opened_item, parent, false);
                holder = new OpenRedPacketSuccessViewHolder(view);
                break;
            default:
                //默认视图，用于解析错误的消息
                view = inflater.inflate(R.layout.hx_fragment_im_left_txt_item, parent, false);
                holder = new BaseViewHolder(view);
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindCommon((BaseViewHolder) holder, position);

        if (holder instanceof ImgViewHolder) { //图片
            onBindPic((ImgViewHolder) holder, position);
        } else if (holder instanceof TxtViewHolder) {  //文字
            onBindTxt((TxtViewHolder) holder, position);
        } else if (holder instanceof VoiceViewHolder) {  //声音
            onBindVoice((VoiceViewHolder) holder, position);
        } else if (holder instanceof MapViewHolder) { //地图
            onBindMap((MapViewHolder) holder, position);
        } else if (holder instanceof FileViewHolder) { //文件
            onBindFile((FileViewHolder) holder, position);
        } else if (holder instanceof VideoViewHolder) {//视频
            onBindVideo((VideoViewHolder) holder, position);
        } else if (holder instanceof RedPackageHolder) {//发红包
            onBindRedPackage((RedPackageHolder) holder, position);
        } else if (holder instanceof RedPacketOpenedViewHolder) {//红包被领取
            onBindRedPacketOpened((RedPacketOpenedViewHolder) holder, position);
        } else if (holder instanceof OpenRedPacketSuccessViewHolder) {//我领取了红包
            onBindOpenRedPacketSuccess((OpenRedPacketSuccessViewHolder) holder, position);
        } else if (holder instanceof GroupChangedViewHolder) {//群成员变动
            onBindGroupChanged((GroupChangedViewHolder) holder, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        int oriType = -1;
        switch (cacheMsgBean.getMsgType()) {
            case CacheMsgBean.SEND_TEXT:
                oriType = TXT_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_TEXT:
                oriType = TXT_LEFT;
                break;
            case CacheMsgBean.SEND_VOICE:
                oriType = VOICE_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_VOICE:
                oriType = VOICE_LEFT;
                break;
            case CacheMsgBean.SEND_IMAGE:
                oriType = IMG_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_IMAGE:
                oriType = IMG_LEFT;
                break;
            case CacheMsgBean.SEND_LOCATION:
                oriType = MAP_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_LOCATION:
                oriType = MAP_LEFT;
                break;
            case CacheMsgBean.SEND_FILE:
                oriType = FILE_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_FILE:
                oriType = FILE_LEFT;
                break;
            case CacheMsgBean.SEND_VIDEO:
                oriType = VIDEO_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_VIDEO:
                oriType = VIDEO_LEFT;
                break;
            case CacheMsgBean.SEND_REDPACKAGE:
                oriType = RED_PACKAGE_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_REDPACKAGE:
            case CacheMsgBean.OPEN_REDPACKET:
                oriType = RED_PACKAGE_LEFT;
                break;
            case CacheMsgBean.GROUP_MEMBER_CHANGED:
                oriType = MEMBER_CHANGED;
                break;
            case CacheMsgBean.GROUP_NAME_CHANGED:
                oriType = NAME_CHANGED;
                break;
            case CacheMsgBean.GROUP_TRANSFER_OWNER:
                oriType = OWNER_CHANGED;
                break;
            case CacheMsgBean.RECEIVE_PACKET_OPENED:
                oriType = RED_PACKET_OPENED;
                break;
            case CacheMsgBean.PACKET_OPENED_SUCCESS:
                oriType = OPEN_RED_PACKET_SUCCESS;
                break;
        }
        return oriType;
    }

    /**
     * 文件
     *
     * @param holder
     * @param position
     */
    private void onBindFile(final FileViewHolder holder, final int position) { //文件
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgFile cacheMsgFile = (CacheMsgFile) cacheMsgBean.getJsonBodyObj();

        int resId = IMHelper.getFileImgRes(cacheMsgFile.getFileName(), false);

        Glide.with(mAct)
                .load(resId)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(holder.fileIV);

        holder.fileNameTV.setText(cacheMsgFile.getFileName());
        holder.fileSizeTV.setText(IMHelper.convertFileSize(cacheMsgFile.getFileSize()));

        showSendStart(holder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, holder.senderDateTV, cacheMsgBean.getMsgTime());

        final boolean isRight = cacheMsgBean.isRightUI();
        if (!isRight) {
            if (holder.tv_name != null) {
                holder.tv_name.setText(cacheMsgBean.getSenderRealName());
            }
        }

        if (isRight) {
            if (cacheMsgBean.getMsgStatus() == CacheMsgBean.SEND_SUCCEED) {
                holder.fileSizeTV.setVisibility(View.VISIBLE);
                holder.filePbar.setVisibility(View.GONE);
            } else {
                holder.fileSizeTV.setVisibility(View.GONE);
                holder.filePbar.setVisibility(View.VISIBLE);
            }
        } else {
            holder.fileSizeTV.setVisibility(View.VISIBLE);
            holder.filePbar.setVisibility(View.GONE);
        }

        holder.fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mAct, IMFilePreviewActivity.class);
                intent.putExtra(IMFilePreviewActivity.IM_FILE_BEAN, cacheMsgBean);
                mAct.startActivity(intent);
            }
        });

    }

    /**
     * 图片
     *
     * @param imgViewHolder
     * @param position
     */
    private void onBindPic(final ImgViewHolder imgViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgImage cacheMsgImage = (CacheMsgImage) cacheMsgBean.getJsonBodyObj();
        String leftUrl;
        switch (cacheMsgImage.getOriginalType()) {
            case CacheMsgImage.SEND_IS_ORI_RECV_NOT_ORI:
                leftUrl = QiniuUrl.getThumbImageUrl(cacheMsgImage.getFid(), QiniuUrl.SCALE);
                break;
            default:
                leftUrl = AppConfig.getImageUrl(cacheMsgImage.getFid());
                break;
        }
        String rightUrl = TextUtils.isEmpty(cacheMsgImage.getFilePath()) ? leftUrl : cacheMsgImage.getFilePath();

        showSendStart(imgViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, imgViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        boolean isRight = cacheMsgBean.isRightUI();
        if (!isRight) {
            if (imgViewHolder.tv_name != null) {
                imgViewHolder.tv_name.setText(cacheMsgBean.getSenderRealName());
            }
        }

        int width = mAct.getResources().getDimensionPixelOffset(R.dimen.im_pic_width);
        int height = mAct.getResources().getDimensionPixelOffset(R.dimen.im_pic_height);

        Glide.with(mAct)
                .load(isRight ? rightUrl : leftUrl)
                .apply(new RequestOptions()
                        //.onlyRetrieveFromCache(true)
                        .override(width, height)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_voice_bg_right : R.drawable.hx_im_voice_bg_left)))
                .into(imgViewHolder.senderImg);

        imgViewHolder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CacheMsgBean> beanList = new ArrayList<>();
                for (CacheMsgBean item : mImBeanList) {
                    if (CacheMsgBean.SEND_IMAGE == item.getMsgType()
                            || CacheMsgBean.RECEIVE_IMAGE == item.getMsgType()) { //图片
                        beanList.add(item);
                    }
                }
                int index = beanList.indexOf(cacheMsgBean);
                Intent intent = new Intent(mAct, PictureIndicatorActivity.class);
                intent.putExtra("index", index);
                intent.putParcelableArrayListExtra("beanList", beanList);
                mAct.startActivity(intent);
            }
        });
    }

    /**
     * 视频数据
     */
    private void onBindVideo(final VideoViewHolder videoViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
        showSendStart(videoViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        final String videoPath = cacheMsgVideo.getVideoPath();//本地视频
        final String framePath = cacheMsgVideo.getFramePath();//本地视频首帧
        final long time = cacheMsgVideo.getTime();//视频时长(毫秒)
        final String videoUrl = AppConfig.getImageUrl(cacheMsgVideo.getVideoId());    //上传视频Url
        String leftUrl = AppConfig.getImageUrl(cacheMsgVideo.getFrameId());     //上传视频首帧Url
        String rightUrl = framePath;
        if (rightUrl == null || !new File(rightUrl).exists()) {
            rightUrl = AppConfig.getImageUrl(cacheMsgVideo.getFrameId());
        }

        videoViewHolder.timeText.setText(TimeUtils.getTimeFromMillisecond(time));

        showMsgTime(position, videoViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        final boolean isRight = cacheMsgBean.isRightUI();
        if (!isRight) {
            if (videoViewHolder.tv_name != null) {
                videoViewHolder.tv_name.setText(cacheMsgBean.getSenderRealName());
            }
        }

        Glide.with(mAct)
                .load(isRight ? rightUrl : leftUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hx_im_default_img)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_voice_bg_right : R.drawable.hx_im_voice_bg_left)))
                .into(videoViewHolder.videoImg);

        if (TextUtils.isEmpty(videoPath) && cacheMsgBean.getProgress() != 0) {
            videoViewHolder.videoPlayImg.setVisibility(View.GONE);
            videoViewHolder.videoCircleProgressView.setVisibility(View.VISIBLE);
            videoViewHolder.videoCircleProgressView.setProgress(cacheMsgBean.getProgress());
            videoViewHolder.videoImg.setEnabled(false);
        } else if (TextUtils.isEmpty(videoPath) && cacheMsgBean.getProgress() == -1) {
            //下载失败的显示
            videoViewHolder.videoPlayImg.setVisibility(View.VISIBLE);
            videoViewHolder.videoCircleProgressView.setVisibility(View.GONE);
            videoViewHolder.lay.setEnabled(true);
        } else {
            videoViewHolder.videoPlayImg.setVisibility(View.VISIBLE);
            videoViewHolder.videoCircleProgressView.setVisibility(View.GONE);
            videoViewHolder.lay.setEnabled(true);
        }
        videoViewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(videoPath)) {
                    downVideo(position, videoUrl, cacheMsgBean);
                } else {
                    File videoFile = new File(videoPath);
                    if (videoFile.exists()) {
                        VideoDetailInfo info = new VideoDetailInfo();
                        info.setVideoPath(cacheMsgBean.isRightUI() ? videoPath : videoPath); //视频路径
                        Intent intent = new Intent(mAct, VideoPlayerActivity.class);
                        intent.putExtra("info", info);
                        mAct.startActivity(intent);
                    } else {
                        //文件被删掉，重新下载
                        cacheMsgVideo.setVideoPath("");
                        cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
                        cacheMsgBean.setProgress(0);
                        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                        downVideo(position, videoUrl, cacheMsgBean);
                    }
                }
            }
        });
    }


    /**
     * 红包
     */
    private void onBindRedPackage(final RedPackageHolder redPackageHolder, final int position) {
        final CacheMsgBean bean = mImBeanList.get(position);
        //final Long msgId = bean.getMsgId();

        final CacheMsgRedPackage redPackage = (CacheMsgRedPackage) bean.getJsonBodyObj();
        showSendStart(redPackageHolder, bean.getMsgStatus(), bean, position);

        final String name = bean.getSenderRealName();
        final String avatar = bean.getSenderAvatar();

        final String value = redPackage.getValue();
        final String title = redPackage.getRedTitle();
        final String redStatus = redPackage.getRedStatus();
        final String redUuid = redPackage.getRedUuid();
        final int isGrabbed = redPackage.getIsGrabbed();
        final int status = redPackage.getStatus();

        showMsgTime(position, redPackageHolder.senderDateTV, bean.getMsgTime());

        final boolean isRight = bean.isRightUI();
        if (!isRight) {
            if (redPackageHolder.tv_name != null) {
                redPackageHolder.tv_name.setText(bean.getSenderRealName());
            }
        }


        ImageView img_red_package = redPackageHolder.img_red_package;
        TextView tv_red_title = redPackageHolder.tv_red_title;
        TextView tv_red_status = redPackageHolder.tv_red_status;


        View view = redPackageHolder.lay;

        tv_red_title.setText(title);
        tv_red_status.setText(redStatus);

        if (isGrabbed == 1 || status == 4) {
            img_red_package.setImageResource(R.drawable.ic_open_red_package);
            view.setBackgroundResource(R.drawable.hx_red_package_disable);
            tv_red_status.setText(CacheMsgRedPackage.RED_PACKET_OPENED);
        } else {
            img_red_package.setImageResource(R.drawable.ic_send_red_package);
            view.setBackgroundResource(R.drawable.hx_red_package_enable);

            if (status == -1) {
                tv_red_status.setText(CacheMsgRedPackage.RED_PACKET_OVERDUE);
                view.setBackgroundResource(R.drawable.hx_red_package_disable);
            } else if (status >= 2) {
                tv_red_status.setText(CacheMsgRedPackage.RED_PACKET_IS_OPEN_GROUP);
                view.setBackgroundResource(R.drawable.hx_red_package_disable);
            } else {
                if (isRight) {
                    tv_red_status.setText(CacheMsgRedPackage.RED_PACKET_REVIEW);
                } else {
                    tv_red_status.setText(CacheMsgRedPackage.RED_PACKET_RECEIVE);
                }

                view.setBackgroundResource(R.drawable.hx_red_package_enable);
            }
        }

        redPackageHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGrabbed == 1 && !TextUtils.isEmpty(value)) {
                    Intent in = new Intent(mAct, RedPacketDetailActivity.class);
                    in.putExtra(RedPacketDetailActivity.OPEN_TYPE, RedPacketDetailActivity.GROUP_PACKET);
                    in.putExtra(RedPacketDetailActivity.AVATAR, avatar);
                    in.putExtra(RedPacketDetailActivity.NICKNAME, name);
                    in.putExtra(RedPacketDetailActivity.VALUE, value);
                    in.putExtra(RedPacketDetailActivity.REDTITLE, title);
                    in.putExtra(RedPacketDetailActivity.REDUUID, redUuid);
                    in.putExtra(RedPacketDetailActivity.MSGBEAN, bean);
                    mAct.startActivity(in);
                } else {
                    showProgress("", "正在加载...", -1);
                    openRedPackage(bean, redUuid);
                }
            }
        });
    }


    private ProgressDialog mProgressDialog;

    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(mAct, theme);
            else
                mProgressDialog = new ProgressDialog(mAct);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!TextUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    private void openRedPackage(final CacheMsgBean uiBean, final String redUuid) {
        HuxinSdkManager.instance().openRedPackage(redUuid, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                        && mAct.isDestroyed()) {
                    return;
                }

                OpenRedPacketResult bean = GsonUtil.parse(response, OpenRedPacketResult.class);
                if (bean != null && bean.isSuccess()) {
                    final int status = bean.getContent().getStatus();  //利是状态：-1已过期 ,0未拆开 ,1未领完 ,2已撤回 ,3已退款 ,4已领完
                    int canOpen = bean.getContent().getCanOpen(); //是否可以开这个利是：0否1是
                    int isGrabbed = bean.getContent().getIsGrabbed(); //用户是否已抢到了该利是：0否1是
                    int type = bean.getContent().getLsType();//1定额利是2拼手气利是
                    int owner = bean.getContent().getIsSelfOwner();//是否本人的利是：0否1是

                    final CacheMsgRedPackage redPackage = (CacheMsgRedPackage) uiBean.getJsonBodyObj();

                    final String name = uiBean.getSenderRealName();
                    final String avatar = uiBean.getSenderAvatar();
                    final String title = redPackage.getRedTitle();
                    final String value = redPackage.getValue();

                    redPackage.setStatus(status);
                    redPackage.setCanOpen(canOpen);
                    redPackage.setIsGrabbed(isGrabbed);
                    redPackage.setRedUuid(redUuid);
                    uiBean.setJsonBodyObj(redPackage);

                    if (owner == 1 && type == 1) {
                        if (status == 4) {
                            redPackage.setStatus(4);
                            uiBean.setJsonBodyObj(redPackage);
                            //add to db
                            CacheMsgHelper.instance().insertOrUpdate(mAct, uiBean);
                            refreshItemUI(uiBean);
                        }

                        Intent in = new Intent(mAct, RedPacketDetailActivity.class);
                        in.putExtra(RedPacketDetailActivity.OPEN_TYPE, RedPacketDetailActivity.GROUP_PACKET);
                        in.putExtra(RedPacketDetailActivity.AVATAR, avatar);
                        in.putExtra(RedPacketDetailActivity.NICKNAME, name);
                        in.putExtra(RedPacketDetailActivity.VALUE, value);
                        in.putExtra(RedPacketDetailActivity.REDTITLE, title);
                        in.putExtra(RedPacketDetailActivity.REDUUID, redUuid);
                        in.putExtra(RedPacketDetailActivity.MSGBEAN, uiBean);
                        in.putExtra(RedPacketDetailActivity.CANOPEN, false);
                        mAct.startActivity(in);
                    } else {
                        HxRedPacketDialog.Builder builder = new HxRedPacketDialog.Builder(mAct);
                        builder.setUiBean(uiBean);
                        builder.setRemark(title);
                        builder.setRedUuid(redUuid);
                        builder.setStatus(status);
                        builder.setCanOpen(canOpen);
                        builder.setIsGrabbed(isGrabbed);
                        builder.setSinglePacket(false);
                        builder.setType(type);
                        builder.setGroup(true);

                        if (mAct instanceof IMGroupActivity) {
                            IMGroupActivity act = (IMGroupActivity) mAct;
                            builder.setGroupName(act.getGroupName());
                        }

                        builder.setListener(new HxRedPacketDialog.OnRedPacketListener() {
                            @Override
                            public void onCloseClick() {
                            }

                            @Override
                            public void onOpenClick(double moneyDraw) {

                                Intent in = new Intent(mAct, RedPacketDetailActivity.class);
                                in.putExtra(RedPacketDetailActivity.OPEN_TYPE, RedPacketDetailActivity.GROUP_PACKET);
                                in.putExtra(RedPacketDetailActivity.AVATAR, avatar);
                                in.putExtra(RedPacketDetailActivity.NICKNAME, name);
                                in.putExtra(RedPacketDetailActivity.VALUE, String.valueOf(moneyDraw));
                                in.putExtra(RedPacketDetailActivity.REDTITLE, title);
                                in.putExtra(RedPacketDetailActivity.REDUUID, redUuid);
                                in.putExtra(RedPacketDetailActivity.MSGBEAN, uiBean);

                                if (moneyDraw == 0) {
                                    in.putExtra(RedPacketDetailActivity.CANOPEN, false);
                                }

                                mAct.startActivity(in);
                                if (moneyDraw > 0) {
                                    redPackage.setIsGrabbed(1);
                                    redPackage.setValue(String.valueOf(moneyDraw));

                                    uiBean.setJsonBodyObj(redPackage);
                                    //add to db
                                    CacheMsgHelper.instance().insertOrUpdate(mAct, uiBean);
                                    refreshItemUI(uiBean);
                                }
                            }
                        });

                        HxRedPacketDialog dialog = builder.builder();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                notifyDataSetChanged();
                            }
                        });
                        dialog.show();

                    }

                }


                hideProgress();
            }
        });
    }


    /**
     * 红包被领取
     */
    private void onBindRedPacketOpened(final RedPacketOpenedViewHolder holder, final int position) {
        final CacheMsgBean bean = mImBeanList.get(position);
        final CacheMsgRedPackage redPackage = (CacheMsgRedPackage) bean.getJsonBodyObj();

        final String name = bean.getSenderRealName();
        final String avatar = bean.getSenderAvatar();

        final String title = redPackage.getRedTitle();
        final String redUuid = redPackage.getRedUuid();
        final String moneyDraw = redPackage.getValue();

        showMsgTime(position, holder.senderDateTV, bean.getMsgTime());

        if (!TextUtils.isEmpty(name)) {
            String content = name + "领取了你的";
            /*String keyword = "利是";

            int start = content.indexOf(keyword);

            int length = keyword.length();
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.hx_color_red_packet)),
                    start, start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_red_open.setText(style);*/

            holder.tv_red_open.setText(content);
        }


        holder.tv_red_packet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mAct, RedPacketDetailActivity.class);
                in.putExtra(RedPacketDetailActivity.OPEN_TYPE, RedPacketDetailActivity.GROUP_PACKET);
                in.putExtra(RedPacketDetailActivity.AVATAR, avatar);
                in.putExtra(RedPacketDetailActivity.NICKNAME, name);
                in.putExtra(RedPacketDetailActivity.VALUE, moneyDraw);
                in.putExtra(RedPacketDetailActivity.REDTITLE, title);
                in.putExtra(RedPacketDetailActivity.REDUUID, redUuid);
                in.putExtra(RedPacketDetailActivity.MSGBEAN, bean);
                mAct.startActivity(in);
            }
        });
    }


    /**
     * 红包被领取
     */
    private void onBindOpenRedPacketSuccess(final OpenRedPacketSuccessViewHolder holder, final int position) {
        final CacheMsgBean bean = mImBeanList.get(position);
        final CacheMsgRedPackage redPackage = (CacheMsgRedPackage) bean.getJsonBodyObj();

        final String name = bean.getSenderRealName();
        final String avatar = bean.getSenderAvatar();

        final String title = redPackage.getRedTitle();
        final String redUuid = redPackage.getRedUuid();
        final String moneyDraw = redPackage.getValue();

        showMsgTime(position, holder.senderDateTV, bean.getMsgTime());

        if (!TextUtils.isEmpty(name)) {
            String content = "你领取了" + name + "的";
            holder.tv_red_open.setText(content);
        }


        holder.tv_red_packet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mAct, RedPacketDetailActivity.class);
                in.putExtra(RedPacketDetailActivity.OPEN_TYPE, RedPacketDetailActivity.GROUP_PACKET);
                in.putExtra(RedPacketDetailActivity.AVATAR, avatar);
                in.putExtra(RedPacketDetailActivity.NICKNAME, name);
                in.putExtra(RedPacketDetailActivity.VALUE, moneyDraw);
                in.putExtra(RedPacketDetailActivity.REDTITLE, title);
                in.putExtra(RedPacketDetailActivity.REDUUID, redUuid);
                in.putExtra(RedPacketDetailActivity.MSGBEAN, bean);
                mAct.startActivity(in);
            }
        });
    }


    /**
     * 群成员变更
     */
    private void onBindGroupChanged(final GroupChangedViewHolder holder, final int position) {
        final CacheMsgBean bean = mImBeanList.get(position);

        showMsgTime(position, holder.senderDateTV, bean.getMsgTime());

        String content = bean.getMemberChanged();
        holder.tv_group_changed.setText(content);
    }


    private void downVideo(final int position, String path, final CacheMsgBean cacheMsgBean) {
        String filePath = FileConfig.getVideoDownLoadPath();
        String fileName = "video_" + System.currentTimeMillis() + ".jv";

        OkHttpConnector.httpDownload(path, null, filePath, fileName, new DownloadListener() {
            @Override
            public void onProgress(int cur, int total) {
                if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {

                    int progress = (int) (cur * 1.0f / total * 100);
                    cacheMsgBean.setProgress(progress);

                    Message msg = mHandler.obtainMessage(HANDLER_REFRESH_PROGREE);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", cacheMsgBean);
                    msg.setData(bundle);
                    mHandler.handleMessage(msg);
                }

            }

            @Override
            public void onFail(String err) {
                cacheMsgBean.setProgress(-1);
                mImBeanList.set(position, cacheMsgBean);//更新数据
                CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                refreshItemUI(cacheMsgBean);
            }

            @Override
            public void onSuccess(String path) {
                if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
                    cacheMsgBean.setProgress(100);

                    CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
                    cacheMsgVideo.setVideoPath(path);
                    cacheMsgBean.setJsonBodyObj(cacheMsgVideo);

                    CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                    refreshItemUI(cacheMsgBean);

                    File videoFile = new File(path);
                    if (videoFile.exists()) {
                        VideoDetailInfo info = new VideoDetailInfo();
                        info.setVideoPath(path); //视频路径
                        Intent intent = new Intent(mAct, VideoPlayerActivity.class);
                        intent.putExtra("info", info);
                        mAct.startActivity(intent);
                    }
                }
            }
        });
    }


    /**
     * 文字
     *
     * @param txtViewHolder
     * @param position
     */
    private void onBindTxt(TxtViewHolder txtViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);

        CacheMsgTxt cacheMsgTxt = (CacheMsgTxt) cacheMsgBean.getJsonBodyObj();
        String txtContent = cacheMsgTxt.getMsgTxt();

        showSendStart(txtViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, txtViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        boolean isRight = cacheMsgBean.isRightUI();
        if (!isRight) {
            if (txtViewHolder.tv_name != null) {
                txtViewHolder.tv_name.setText(cacheMsgBean.getSenderRealName());
            }
        }

        if (txtContent != null) {
            SpannableString msgSpan = new SpannableString(txtContent);
            msgSpan = EmoticonHandler.getInstance(mAct).getTextFace(txtContent, msgSpan, 0,
                    Utils.getFontSize(txtViewHolder.senderTV.getTextSize()));
            txtViewHolder.senderTV.setText(msgSpan);
            txtViewHolder.senderTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            txtViewHolder.senderTV.setOnClickLis(new CopeTextView.OnCopeListener() {
                @Override
                public void copeText() {
                }

                @Override
                public void forwardText(CharSequence s) {
                    ARouter.getInstance()
                            .build(APath.MSG_FORWARD)
                            .withString("type", "forward_msg")
                            .withParcelable("data", cacheMsgBean)
                            .navigation(mAct, 300);
                }

                @Override
                public void collect() {
                }

                @Override
                public void read() {

                }

                @Override
                public void remind() {

                }

                @Override
                public void delete() {
                    //删除消息的操作
                    deleteMsg(cacheMsgBean, position, true);
                }

                @Override
                public void more() {
                }
            });
        }
    }

    /**
     * 地图
     *
     * @param mapViewHolder
     * @param position
     */
    private void onBindMap(final MapViewHolder mapViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        CacheMsgMap cacheMsgMap = (CacheMsgMap) cacheMsgBean.getJsonBodyObj();

        String mapUrl = cacheMsgMap.getImgUrl();
        final String mapAddr = cacheMsgMap.getAddress();
        final double latitude = cacheMsgMap.getLatitude();
        final double longitude = cacheMsgMap.getLongitude();

        showSendStart(mapViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, mapViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        final boolean isRight = cacheMsgBean.isRightUI();
        if (!isRight) {
            if (mapViewHolder.tv_name != null) {
                mapViewHolder.tv_name.setText(cacheMsgBean.getSenderRealName());
            }
        }

        Glide.with(mAct)
                .load(mapUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_card_map_right : R.drawable.hx_im_card_map_left)))
                .into(mapViewHolder.senderMap);

        mapViewHolder.senderAddr.setText(mapAddr);

        mapViewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setClass(mAct, CropMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("labelAddress", mapAddr);
                mAct.startActivity(intent);
            }
        });
    }

    /**
     * 声音
     *
     * @param voiceViewHolder
     * @param position
     */
    AnimationDrawable voicePlayAnim;
    private int mPlayVoicePosition;
    private ImageView mPlayVoiceIV;

    private TipView voiceTip;

    TipView tipView;
    public float mRawX;
    public float mRawY;

    private void onBindVoice(final VoiceViewHolder voiceViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();

        String voiceTime = cacheMsgVoice.getVoiceTime();

        showSendStart(voiceViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, voiceViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        final boolean isRight = cacheMsgBean.isRightUI();
        if (!isRight) {
            if (voiceViewHolder.tv_name != null) {
                voiceViewHolder.tv_name.setText(cacheMsgBean.getSenderRealName());
            }
        }

        if (voiceViewHolder.readIV != null) {
            if (cacheMsgVoice.isHasLoad()) { //到达
                voiceViewHolder.readIV.setVisibility(View.INVISIBLE);
            } else {
                voiceViewHolder.readIV.setVisibility(View.VISIBLE);
            }
        }

        voiceViewHolder.senderTime.setText(getFormateVoiceTime(voiceTime));

        voiceViewHolder.voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
                    voicePlayAnim.stop();
                    if (mPlayVoiceIV != null) {
                        mPlayVoiceIV.setImageResource(mImBeanList.get(mPlayVoicePosition).isRightUI()
                                ? R.drawable.hx_im_right_anim_v3 : R.drawable.hx_im_left_anim_v3);
                    }
                }

                if (MediaManager.isPlaying()) {
                    MediaManager.release();
                    if (mPlayVoicePosition == position) {
                        return;
                    } else {
                        //停止文本朗读
                        CacheMsgBean cacheMsgBean1 = mImBeanList.get(mPlayVoicePosition);
                        if (cacheMsgBean1.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE) {
                            if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
                                voicePlayAnim.stop();
                            }
                            cacheMsgBean1.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                            CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean1);
                            mImBeanList.set(mPlayVoicePosition, cacheMsgBean1);
                            notifyItemChanged(mPlayVoicePosition);
                        }
                    }
                }

                mPlayVoicePosition = position;
                mPlayVoiceIV = voiceViewHolder.voiceIV;

                if (!isRight) {
                    if (!cacheMsgVoice.isHasLoad()) {
                        //add to db
                        cacheMsgVoice.setHasLoad(true);
                        cacheMsgBean.setJsonBodyObj(cacheMsgVoice);
                        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                    }
                    voiceViewHolder.readIV.setVisibility(View.INVISIBLE);
                }

                voiceViewHolder.voiceIV.setImageResource(isRight ? R.drawable.hx_im_voice_right_anim : R.drawable.hx_im_voice_left_anim);
                voicePlayAnim = (AnimationDrawable) voiceViewHolder.voiceIV.getDrawable();
                voicePlayAnim.start();

                String path;
                if (TextUtils.isEmpty(cacheMsgVoice.getVoicePath())) {
                    path = cacheMsgVoice.getVoiceUrl();
                } else {
                    path = cacheMsgVoice.getVoicePath();
                }

                //播放声音
                MediaManager.playSound(path, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        voicePlayAnim.stop();
                        voiceViewHolder.voiceIV.setImageResource(isRight ? R.drawable.hx_im_right_anim_v3 : R.drawable.hx_im_left_anim_v3);
                    }
                });
            }
        });

    }

    public String getFormateVoiceTime(String voiceTime) {
        try {
            float voiceF = Float.valueOf(voiceTime);
            return Math.round(voiceF) + "\"";
        } catch (Exception e) {
            return voiceTime;
        }
    }


    private void showMsgTime(int position, TextView textView, long curTime) {
        textView.setText(TimeUtils.dateFormat(curTime));

        if (position >= 1) {
            final CacheMsgBean cacheMsgBean = mImBeanList.get(position - 1);
            long lastTime = cacheMsgBean.getMsgTime();

            if (curTime - lastTime < 30 * 1000) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }

    }


    //支持失败重传
    //根据flag显示发送状态 0:发送成功  -1:正在发送  2:短信发送  4:发送错误  5:文本播放语音 6:提醒状态
    private void showSendStart(final BaseViewHolder viewHolder, int flag, final CacheMsgBean bean, final int position) {

        if (flag == CacheMsgBean.SEND_SUCCEED
                || flag == CacheMsgBean.RECEIVE_READ) {
            //显示到达状态
            if (viewHolder.progressBar != null) {
                viewHolder.progressBar.setVisibility(View.GONE);
            }
            if (viewHolder.smsImg != null) {
                viewHolder.smsImg.setVisibility(View.GONE);
                viewHolder.smsImg.setOnClickListener(null);
            }
        } else if (flag == CacheMsgBean.SEND_FAILED) {
            //显示发送失败状态
            if (viewHolder.progressBar != null) {
                viewHolder.progressBar.setVisibility(View.GONE);
            }
            if (viewHolder.smsImg != null) {
                viewHolder.smsImg.setVisibility(View.VISIBLE);
                viewHolder.smsImg.setImageResource(R.drawable.hx_im_send_error2_icon);
                viewHolder.smsImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (HuxinSdkManager.instance().isKicked()) {
                            HuxinSdkManager.instance().reLoginDialog();
                            return;
                        }*/
                        //重传
                        bean.setMsgStatus(CacheMsgBean.SEND_GOING);
                        updateSendStatus(bean, position);
                        Intent intent = new Intent(mAct, SendMsgService.class);
                        intent.putExtra("isGroup", true);
                        intent.putExtra("data", bean);
                        intent.putExtra("data_from", SendMsgService.FROM_IM);
                        mAct.startService(intent);
                    }
                });
            }


        } else {
            //正在发送状态
            if (viewHolder.progressBar != null) {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
            }

            if (viewHolder.smsImg != null) {
                viewHolder.smsImg.setVisibility(View.GONE);
                viewHolder.smsImg.setOnClickListener(null);
            }
        }

    }

    void updateSendStatus(CacheMsgBean cacheMsgBean, int position) {
        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);//更新数据库
        if (position < mImBeanList.size()) {
            mImBeanList.get(position).setMsgStatus(cacheMsgBean.getMsgStatus());//更新列表显示数据
            notifyItemChanged(position);
        }
    }


    @Override
    public int getItemCount() {
        return mImBeanList == null ? 0 : mImBeanList.size();
    }


    public int getItemPosition(long id) {
        int index;
        for (index = 0; index < mImBeanList.size(); index++) {
            if (mImBeanList.get(index).getId() == id) {
                return index;
            }
        }
        return index;
    }

    private class TxtViewHolder extends BaseViewHolder {

        protected CopeTextView senderTV;

        public TxtViewHolder(View itemView) {
            super(itemView);
            senderTV = (CopeTextView) itemView.findViewById(R.id.sender_tv);
        }
    }

    private class VoiceViewHolder extends BaseViewHolder {

        protected TextView senderTime;
        protected View voiceBtn;
        protected ImageView voiceIV;
        protected View readIV;


        public VoiceViewHolder(View itemView) {
            super(itemView);
            senderTime = (TextView) itemView.findViewById(R.id.sender_time);
            voiceBtn = itemView.findViewById(R.id.item_btn);
            voiceIV = (ImageView) itemView.findViewById(R.id.voice_iv);
            readIV = itemView.findViewById(R.id.read_iv);
        }
    }

    private class MapViewHolder extends BaseViewHolder {

        protected ImageView senderMap;
        protected TextView senderAddr;
        protected View btnMap;

        public MapViewHolder(View itemView) {
            super(itemView);
            senderMap = (ImageView) itemView.findViewById(R.id.sender_map);
            senderAddr = (TextView) itemView.findViewById(R.id.sender_addr);
            btnMap = itemView.findViewById(R.id.item_btn);
        }
    }


    private class ImgViewHolder extends BaseViewHolder {

        protected ImageView senderImg;
        protected View imgBtn;

        public ImgViewHolder(View itemView) {
            super(itemView);
            senderImg = (ImageView) itemView.findViewById(R.id.sender_img);
            imgBtn = itemView.findViewById(R.id.item_btn);
        }
    }

    public class FileViewHolder extends BaseViewHolder {
        public ProgressBar filePbar;
        private View fileBtn;
        public ImageView fileIV;
        public TextView fileNameTV;
        public TextView fileSizeTV;


        FileViewHolder(View itemView) {
            super(itemView);
            filePbar = (ProgressBar) itemView.findViewById(R.id.file_pbar);
            fileBtn = itemView.findViewById(R.id.item_btn);
            fileNameTV = (TextView) itemView.findViewById(R.id.file_name);
            fileSizeTV = (TextView) itemView.findViewById(R.id.file_size);
            fileIV = (ImageView) itemView.findViewById(R.id.file_iv);
        }
    }


    private class VideoViewHolder extends BaseViewHolder {

        View lay;
        ImageView videoImg;
        TextView timeText;
        ImageView videoPlayImg;
        CircleProgressView videoCircleProgressView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            lay = itemView.findViewById(R.id.item_btn);
            videoImg = (ImageView) itemView.findViewById(R.id.sender_img);
            timeText = (TextView) itemView.findViewById(R.id.item_video_time_text);
            videoPlayImg = (ImageView) itemView.findViewById(R.id.item_video_play_img);
            videoCircleProgressView = (CircleProgressView) itemView.findViewById(R.id.item_video_pro);
        }
    }

    private class RedPackageHolder extends BaseViewHolder {

        View lay;
        ImageView img_red_package;
        TextView tv_red_title;
        TextView tv_red_status;

        public RedPackageHolder(View itemView) {
            super(itemView);
            lay = itemView.findViewById(R.id.item_btn);
            img_red_package = (ImageView) itemView.findViewById(R.id.img_red_package);
            tv_red_title = (TextView) itemView.findViewById(R.id.tv_red_title);
            tv_red_status = (TextView) itemView.findViewById(R.id.tv_red_status);
        }
    }


    class RedPacketOpenedViewHolder extends BaseViewHolder {
        TextView tv_red_open;
        TextView tv_red_packet;

        RedPacketOpenedViewHolder(View itemView) {
            super(itemView);
            tv_red_open = (TextView) itemView.findViewById(R.id.tv_red_packet_opened);
            tv_red_packet = (TextView) itemView.findViewById(R.id.tv_red_packet);
        }
    }

    class OpenRedPacketSuccessViewHolder extends BaseViewHolder {
        TextView tv_red_open;
        TextView tv_red_packet;

        OpenRedPacketSuccessViewHolder(View itemView) {
            super(itemView);
            tv_red_open = (TextView) itemView.findViewById(R.id.tv_red_packet_opened);
            tv_red_packet = (TextView) itemView.findViewById(R.id.tv_red_packet);
        }
    }


    class GroupChangedViewHolder extends BaseViewHolder {
        TextView tv_group_changed;

        GroupChangedViewHolder(View itemView) {
            super(itemView);
            tv_group_changed = (TextView) itemView.findViewById(R.id.tv_group_changed);
        }
    }

    private void onBindCommon(final BaseViewHolder baseViewHolder, final int position) {
        final CacheMsgBean bean = mImBeanList.get(position);

        String avatar;
        //头像
        if (bean.isRightUI()) {  //自己的头像
            avatar = HuxinSdkManager.instance().getHeadUrl();
        } else {
            avatar = bean.getTargetAvatar();
        }
        int size = mAct.getResources().getDimensionPixelOffset(R.dimen.card_head);
        if (baseViewHolder.senderIV != null) {
            Glide.with(mAct).load(avatar)
                    .apply(new RequestOptions()
                            .transform(new GlideRoundTransform())
                            .override(size, size)
                            .placeholder(R.drawable.color_default_header)
                            .error(R.drawable.color_default_header)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(baseViewHolder.senderIV);

            baseViewHolder.senderIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isRightUI()) {  //自己的头像
                        ARouter.getInstance().build(APath.USER_INFO_ACT)
                                .withString("useruuid", HuxinSdkManager.instance().getUuid())
                                .navigation(mAct);
                    } else {
                        String uuid = bean.getSenderUserId();
                        boolean isBuddy = ContactHelper.instance().queryBuddyById(mAct, uuid);
                        if (isBuddy) {
                            ARouter.getInstance().build(APath.BUDDY_FRIEND)
                                    .withString("useruuid", uuid)
                                    .navigation(mAct);
                        } else {
                            ARouter.getInstance().build(APath.BUDDY_NOT_FRIEND)
                                    .withString("useruuid", uuid)
                                    .navigation(mAct);
                        }
                    }
                }
            });

            baseViewHolder.senderIV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mAct instanceof IMGroupActivity) {
                        IMGroupActivity act = (IMGroupActivity) mAct;

                        String nickName = bean.getSenderRealName();
                        String uuid = bean.getSenderUserId();

                        act.addAtName(nickName);
                        act.addAtUuid(new GroupAtItem(nickName, uuid));
                    }
                    return true;
                }
            });
        }

        if (baseViewHolder.itemBtn != null) {
            //删除消息
            //TxTViewHolder类型另外处理，TextView添加autoLink属性后会拦截ViewGroup的事件分发,删除消息的提示窗放到CopeTextView里处理
            baseViewHolder.itemBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mRawX = event.getRawX();
                        mRawY = event.getRawY();
                    }
                    return false;
                }
            });

            baseViewHolder.itemBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    List<TipBean> tips = null;
                    if (baseViewHolder instanceof VoiceViewHolder) {
                        tips = TipsType.getVoiceType();
                    } else if (baseViewHolder instanceof ImgViewHolder
                            || baseViewHolder instanceof VideoViewHolder
                            || baseViewHolder instanceof MapViewHolder
                            || baseViewHolder instanceof FileViewHolder) {
                        tips = TipsType.getOtherType();
                    }
                    if (tips != null) {
                        tipView = new TipView(mAct, tips, mRawX, mRawY);
                        tipView.setListener(new ItemListener() {
                            @Override
                            public void delete() {
                                deleteMsg(bean, position, true);
                            }

                            @Override
                            public void copy() {
                            }

                            @Override
                            public void collect() {
                                //收藏操作
                            }

                            @Override
                            public void forward() {
                                //转发操作
                                ARouter.getInstance()
                                        .build(APath.MSG_FORWARD)
                                        .withString("type", "forward_msg")
                                        .withParcelable("data", bean)
                                        .navigation(mAct, 300);

                            }

                            @Override
                            public void read() {

                            }

                            @Override
                            public void remind() {
                            }

                            @Override
                            public void turnText() {
                            }

                            @Override
                            public void more() {
                                moreAction(position);
                            }

                            @Override
                            public void emoKeep() {
                            }
                        });
                        tipView.show(view);
                    }
                    return true;
                }
            });
        }

    }


    /**
     * 删除单条消息
     *
     * @param cacheMsgBean
     * @param position
     */
    void deleteMsg(CacheMsgBean cacheMsgBean, int position, boolean refreshUI) {
        //如果是删除最新的那个或是最后一个   通知主页刷新
        if (position == (mImBeanList.size() - 1)) {
            int type = 1;
            if (mImBeanList.size() == 1) {
                type = 2;
            }
            listener.deleteMsgCallback(type);
        }

        CacheMsgHelper.instance().deleteOneMsg(mAct, cacheMsgBean.getId());
        //删除本地
        mImBeanList.remove(cacheMsgBean);
        if (refreshUI) {
            notifyDataSetChanged();
        }
    }


    class BaseViewHolder extends RecyclerView.ViewHolder {
        TextView senderDateTV;
        ImageView senderIV;
        View itemBtn;
        View contentLay;
        ProgressBar progressBar;
        ImageView smsImg;
        TextView tv_name;

        BaseViewHolder(View itemView) {
            super(itemView);
            senderDateTV = (TextView) itemView.findViewById(R.id.sender_date);
            senderDateTV.setTextSize(12);
            senderIV = (ImageView) itemView.findViewById(R.id.sender_iv);
            itemBtn = itemView.findViewById(R.id.item_btn);
            contentLay = itemView.findViewById(R.id.img_content_lay);// 中间背景
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbar);
            smsImg = (ImageView) itemView.findViewById(R.id.im_sms_img);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }


    //开启批量处理
    private void moreAction(int position) {
        isShowSelect = true;
        selectMsg.clear();
        selectMsg.put(position, mImBeanList.get(position));
        notifyDataSetChanged();
        if (moreListener != null) {
            moreListener.showMore(true);
        }
    }

    //隐藏批量多选框
    public void cancelMoreStat() {
        isShowSelect = false;
        notifyDataSetChanged();
    }

    //srsm add start
    public void onStop() {
        if (voiceTip != null && voiceTip.isShowing()) {
            voiceTip.dismiss();
        }
    }


    //发送消息的刷新
    public void addAndRefreshUI(CacheMsgBean cacheMsgBean) {
        //add to db
        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);

        mImBeanList.add(cacheMsgBean);
        if (getItemCount() > 1) {
            notifyItemRangeChanged(getItemCount() - 2, 2);//需要更新上一条
        } else {
            notifyItemChanged(getItemCount() - 1);
        }

        focusBottom(false);
    }

    /**
     * 收到消息的刷新
     *
     * @param cacheMsgBean
     */
    public void refreshIncomingMsgUI(CacheMsgBean cacheMsgBean) {
        //closeMenuDialog();//关闭菜单视图
        cacheMsgBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
        addAndRefreshUI(cacheMsgBean);
    }

    //刷新单个item
    public void refreshItemUI(CacheMsgBean cacheMsgBean) {
        long mid = cacheMsgBean.getId();
        int index = -1;
        if (mImBeanList.size() > 0) {
            for (int i = mImBeanList.size() - 1; i >= 0; i--) {
                if (mImBeanList.get(i).getId() == mid) {
                    index = i;
                    break;
                }
            }
            if (index != -1 && index < mImBeanList.size()) {
                mImBeanList.set(index, cacheMsgBean);//更新数据
                notifyItemChanged(index);
            }
        }
    }


    //刷新单个item的进度
    public void refreshItemUI(long mid, int p) {
        int index = -1;
        if (mImBeanList.size() > 0) {
            for (int i = mImBeanList.size() - 1; i >= 0; i--) {
                if (mImBeanList.get(i).getId() == mid) {
                    index = i;
                    break;
                }
            }
            if (index != -1 && index < mImBeanList.size()) {
                CacheMsgBean cacheMsgBean = mImBeanList.get(index);
                if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
                    CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
                    cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
                    cacheMsgBean.setProgress(p);
                    mImBeanList.set(index, cacheMsgBean);//更新数据
                    notifyItemChanged(index);
                }
            }
        }
    }


    public void update(CacheMsgBean cacheMsgBean, int position) {
        mImBeanList.set(position, cacheMsgBean);
    }

    public void focusBottom(final boolean smoothScroll) {
        focusBottom(smoothScroll, 300);
    }

    public void focusBottom(final boolean smoothScroll, int delayMillis) {

        if (mAct != null
                && mRecyclerView != null
                && mRecyclerView.getLayoutManager() != null
                && getItemCount() > 0) {

            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                            && mAct.isDestroyed()) {
                        return;
                    }

                    if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManagerWithSmoothScroller) {
                        ((LinearLayoutManagerWithSmoothScroller) mRecyclerView.getLayoutManager()).setScrollerTop(false);
                    }
                    if (!mRecyclerView.canScrollVertically(1)) {
                        //到底了,不用滑动
                        return;
                    }
                    if (smoothScroll) {
                        mRecyclerView.smoothScrollToPosition(getItemCount() - 1);
                    } else {
                        int c = mRecyclerView.getLayoutManager().getItemCount() - 1;
                        int lastPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (c - lastPosition < 3) {
                            //已经显示最后一个,移动少的用滑动
                            mRecyclerView.smoothScrollToPosition(c);
                        } else {
                            mRecyclerView.scrollToPosition(c);
                        }
                    }
                }
            }, delayMillis);
        }
    }


    public interface OnListener {
        /**
         * @param position      滚动的位置
         * @param delayMillis   滚动触发时间
         * @param isScrollerTop true:滚动到该item的顶部    false:滚动到item底部
         */
        void smoothScroll(int position, long delayMillis, boolean isScrollerTop);

        /**
         * @param hasEdit 在编辑状态下不允许滑动最底
         */
        void hasEdit(boolean hasEdit);

        void hidden(boolean hidden);

        /**
         * 删除记录回调，1 删除最新的那条，2 全部删除，记录为空
         *
         * @param type
         */
        void deleteMsgCallback(int type);

        /**
         * 头像点击事件
         */
        void onHandleAvatarClick();
    }

    public interface OnClickMoreListener {
        void showMore(boolean isShow);

        void hasSelectMsg(boolean selected);
    }


    private static class UIHandler extends Handler {
        private final WeakReference<IMGroupAdapter> mTarget;

        UIHandler(IMGroupAdapter target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            final IMGroupAdapter adapter = mTarget.get();

            switch (msg.what) {
                case HANDLER_REFRESH_PROGREE:
                    Bundle bundle = msg.getData();
                    CacheMsgBean cacheMsgBean = bundle.getParcelable("item");
                    adapter.refreshItemUI(cacheMsgBean);
                    break;
                default:
                    break;
            }
        }

    }

}
