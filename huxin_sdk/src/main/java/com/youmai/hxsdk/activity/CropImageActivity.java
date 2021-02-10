package com.youmai.hxsdk.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.dialog.HxMediaStoreDialog;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;

/**
 * 作者：create by YW
 * 日期：2016.08.23 19:54
 * 描述：
 */
public class CropImageActivity extends SdkBaseActivity {

    private CacheMsgBean cacheMsgBean;
    private String imgUrl;
    private ImageView img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hx_crop_img_view);
        String scheme = getIntent().getScheme();//获得Scheme名称

        if (!StringUtils.isEmpty(scheme) && scheme.equals("img")) {
            String uri = getIntent().getDataString();//获得Uri全部路径
            imgUrl = uri.replace("img://", "");
        } else {
            imgUrl = getIntent().getStringExtra("isImageUrl");
        }

        img = (ImageView) findViewById(R.id.iv_img_url);
        ImageView iv_save = (ImageView) findViewById(R.id.iv_save);

        Glide.with(getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HxMediaStoreDialog hxDialog = new HxMediaStoreDialog(mContext);
                HxMediaStoreDialog.HxCallback callback =
                        new HxMediaStoreDialog.HxCallback() {
                            @Override
                            public void onCallSavePhoto() {
                                if (imgUrl.startsWith("http://")) {
                                    saveBitmap(imgUrl);
                                } else {
                                    ToastUtil.showToast(CropImageActivity.this, getString(R.string.hx_picture_save));
                                }
                            }

                            @Override
                            public void onSendtoFriend() {
                                Intent intent = new Intent();
                                intent.setAction("com.youmai.huxin.recent");
                                intent.putExtra("type", "forward_msg");
                                intent.putExtra("data", cacheMsgBean);
                                mContext.startActivity(intent);
                            }
                        };
                hxDialog.setHxMediaStoreDialog(callback);
                hxDialog.show();
                return false;
            }
        });

        iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap(imgUrl);
            }
        });
    }

    private void saveBitmap(final String path) {
        Glide.with(this)
                .asBitmap()
                .load(path)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        saveImageToGallery(mContext, path, resource);
                    }
                });
    }

    public void saveImageToGallery(Context context, String fileName, Bitmap bmp) {
        // 2.把文件插入到系统图库
        String uri = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);

        //ToastUtil.showToast(CropImageActivity.this, getString(R.string.hx_picture_save));
        String path = AppUtils.getPath(mContext, Uri.parse(uri));

        String sdcard_path = Environment.getExternalStorageDirectory().getPath() + "/";

        ToastUtil.showToast(mContext, getString(R.string.hx_save_success) + path.replace(sdcard_path, ""));
    }

}
