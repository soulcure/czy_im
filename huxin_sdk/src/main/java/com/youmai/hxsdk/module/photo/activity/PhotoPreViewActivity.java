package com.youmai.hxsdk.module.photo.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.qiniu.android.storage.UpProgressHandler;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.service.sendmsg.PostFile;
import com.youmai.hxsdk.service.sendmsg.QiniuUtils;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;

import java.io.File;

/**
 * 作者：create by YW
 * 日期：2017.09.08 11:48
 * 描述：
 */

public class PhotoPreViewActivity extends SdkPhotoActivity implements View.OnClickListener {

    public static final String TAG = "PhotoPreViewActivity";
    public static final String URL = "file_url";
    public static final String DST_UUID = "dst_uuid";

    private String mImagePath, dstUuid;

    ImageView mPhotoPreview;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_title_right) {
            File file = new File(mImagePath);
            if (!file.exists()) {
                return;
            }
            uploadFile(mImagePath);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hx_activity_photo_preview);
        super.onCreate(savedInstanceState);
        isPreview = false;
        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().finishActivity(PhotoPreViewActivity.this);
    }

    @Override
    public void initView() {
        bindViews();
    }

    @Override
    public void initData() {
        mImagePath = getIntent().getStringExtra(URL);
        dstUuid = getIntent().getStringExtra(DST_UUID);
    }

    private void bindViews() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("选择图片");
        TextView tv_title_right = (TextView) findViewById(R.id.tv_title_right);
        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setText("发送");
        tv_title_right.setOnClickListener(this);

        mPhotoPreview = (ImageView) findViewById(R.id.iv_photo_preview);

        try {
            Glide.with(mContext)
                    .load(mImagePath)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop())
                    .into(mPhotoPreview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadFile(final String path) {
        UpProgressHandler upProgressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.v(TAG, "percent=" + (percent * 100));
            }
        };

        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setMsgStatus(CacheMsgBean.SEND_GOING)
                .setSenderUserId(HuxinSdkManager.instance().getUuid())
                .setReceiverUserId(dstUuid)
                .setTargetName(dstUuid)
                .setTargetUuid(dstUuid);

        cacheMsgBean.setMsgType(CacheMsgBean.SEND_IMAGE)
                .setJsonBodyObj(new CacheMsgImage()
                        .setFilePath(path)
                        .setOriginalType(CacheMsgImage.SEND_NOT_ORI));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null

        final String imgWidth = options.outWidth + "";
        final String imgHeight = options.outHeight + "";


        PostFile postFile = new PostFile() {
            @Override
            public void success(final String fileId, final String desPhone) {
                //已上传七牛，但仍未送达到用户，处于发送状态
                CacheMsgImage msgBody = (CacheMsgImage) cacheMsgBean.getJsonBodyObj();
                msgBody.setFid(fileId);
                cacheMsgBean.setJsonBodyObj(msgBody);

                HuxinSdkManager.instance().sendPicture(dstUuid, fileId,
                        imgWidth, imgHeight, "thumbnail", new ReceiveListener() {
                            @Override
                            public void OnRec(PduBase pduBase) {
                                try {
                                    final YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                                    //long msgId = ack.getMsgId();
                                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                                    } else {
                                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                                    }
                                    CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);

                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

            }

            @Override
            public void fail(String msg) {
                CacheMsgImage msgBody = (CacheMsgImage) cacheMsgBean.getJsonBodyObj();
                msgBody.setFid("-2");
                cacheMsgBean.setJsonBodyObj(msgBody);

                cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);

            }
        };

        QiniuUtils qiniuUtils = new QiniuUtils();
        qiniuUtils.postFileToQiNiu(path, dstUuid, upProgressHandler, postFile);
    }

}
