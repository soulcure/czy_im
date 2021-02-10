package com.youmai.hxsdk.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.PagerIndicatorAdapter;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.dialog.HxMediaStoreDialog;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.QiniuUrl;
import com.youmai.hxsdk.utils.ToastUtil;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2017/6/8.
 */

public class PictureIndicatorActivity extends SdkBaseActivity {

    private Context mContext;
    private int mPosition = 0;
    private ImageView iv_save;
    private ArrayList<CacheMsgBean> beanList;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //全屏，隐藏状态栏，导航条透明
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }*/

        setContentView(R.layout.activity_picture_indicator);
        mContext = this;
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        initView();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    private void initView() {
        //String[] array = getIntent().getStringArrayExtra("image");
        mPosition = getIntent().getIntExtra("index", 0);

        //final List<String> list = Arrays.asList(array);
        beanList = getIntent().getParcelableArrayListExtra("beanList");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        iv_save = (ImageView) findViewById(R.id.iv_save);

        final List<View> listView = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        for (final CacheMsgBean item : beanList) {
            final CacheMsgImage cacheImage = (CacheMsgImage) item.getJsonBodyObj();

            final String fid = cacheImage.getFid();
            String filePath = cacheImage.getFilePath();

            String url = "";

            //使用网络地址
            if (!TextUtils.isEmpty(fid)) {
                switch (cacheImage.getOriginalType()) {
                    case CacheMsgImage.SEND_IS_ORI_RECV_NOT_ORI:
                        url = QiniuUrl.getThumbImageUrl(fid, QiniuUrl.SCALE);
                        break;
                    default:
                        url = AppConfig.getImageUrl(fid);
                        break;

                }
            }

            //如果本地存在
            if (item.isRightUI()) {
                if (!TextUtils.isEmpty(filePath)
                        && new File(filePath).exists()) {
                    url = filePath;
                }
            }

            View view = mInflater.inflate(R.layout.item_picture, null);

            final PhotoView imageView = (PhotoView) view.findViewById(R.id.img_content);
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(imageView);

            final TextView original = (TextView) view.findViewById(R.id.tv_download_original);

            original.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    original.setVisibility(View.GONE);
                    String url = AppConfig.getImageUrl(fid);

                    Glide.with(mContext)
                            .load(url)
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(imageView);
                    //2017/11/3 刷新list为已下载原图
                    Intent intent = new Intent(SendMsgService.ACTION_UPDATE_MSG);
                    intent.putExtra("CacheMsgBean", item);
                    localBroadcastManager.sendBroadcast(intent);
                }
            });

            if (!TextUtils.isEmpty(fid)
                    && (cacheImage.getOriginalType() == CacheMsgImage.SEND_IS_ORI_RECV_NOT_ORI)) {
                original.setVisibility(View.VISIBLE);
            } else {
                original.setVisibility(View.GONE);
            }
            listView.add(view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_save.setVisibility(View.GONE);
                    onBackPressed();
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    HxMediaStoreDialog hxDialog = new HxMediaStoreDialog(mContext);
                    HxMediaStoreDialog.HxCallback callback =
                            new HxMediaStoreDialog.HxCallback() {
                                @Override
                                public void onCallSavePhoto() {
                                    if (mPosition < beanList.size()) {
                                        CacheMsgImage cacheImage = (CacheMsgImage) beanList.get(mPosition).getJsonBodyObj();
                                        String fid = cacheImage.getFid();
                                        String path;
                                        if (!TextUtils.isEmpty(fid)) {
                                            path = AppConfig.getImageUrl(fid);
                                        } else {
                                            path = cacheImage.getFilePath();
                                        }
                                        saveBitmap(path);
                                    }
                                }

                                @Override
                                public void onSendtoFriend() {

                                    Toast.makeText(mContext, "暂未开发", Toast.LENGTH_SHORT).show();

                                    /*Intent intent = new Intent();
                                    intent.setAction("com.youmai.huxin.recent");
                                    intent.putExtra("type", "forward_msg");
                                    intent.putExtra("data", item);
                                    startActivityForResult(intent, 300);*/
                                }
                            };
                    hxDialog.setHxMediaStoreDialog(callback);
                    hxDialog.show();
                    return false;
                }
            });
        }

        PagerIndicatorAdapter adapter = new PagerIndicatorAdapter(this, listView);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mPosition);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                Log.d("YW", "onPageSelected position: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheMsgImage cacheImage = (CacheMsgImage) beanList.get(mPosition).getJsonBodyObj();
                String fid = cacheImage.getFid();
                String path;
                if (!TextUtils.isEmpty(fid)) {
                    path = AppConfig.getImageUrl(fid);
                } else {
                    path = cacheImage.getFilePath();
                }
                saveBitmap(path);
            }
        });
    }


    private void saveBitmap(final String path) {
        Log.w("123", "path:" + path);
        Glide.with(this)
                .asBitmap()
                .load(path)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        String uri = insertImage(mContext.getContentResolver(), resource, path, "huxin pic");
                        String path = AppUtils.getPath(mContext, Uri.parse(uri));
                        String sdcard_path = Environment.getExternalStorageDirectory().getPath() + "/";
                        ToastUtil.showToast(mContext, getString(R.string.hx_save_success) + path.replace(sdcard_path, ""));
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
    }

    public String insertImage(ContentResolver cr,
                              Bitmap source,
                              String title,
                              String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        //增加时间信息，让图片显示在图库的前面
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);//增加修改时间

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (source != null && url != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                } finally {
                    if (imageOut != null) {
                        imageOut.close();
                    }
                }
//                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
//                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
//                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
                cr.update(url, values, null, null);//更新图库
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }
        return stringUrl;
    }
}
