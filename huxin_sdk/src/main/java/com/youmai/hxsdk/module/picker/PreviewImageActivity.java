package com.youmai.hxsdk.module.picker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.adapter.PagerIndicatorAdapter;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.picker.adapter.AlbumContentAdapter;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2017/6/8.
 */

public class PreviewImageActivity extends SdkBaseActivity {

    private Context mContext;
    private int mPosition = 0;

    private ImageView img_back;
    private TextView tv_title;
    private CheckBox cb_item;
    private Button btn_send;
    private ArrayList<String> mList;
    private CheckBox cb_isOriginal;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        mContext = this;
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        cb_item = (CheckBox) findViewById(R.id.cb_item);

        cb_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhotoPickerManager.getInstance().getPaths().size() > AlbumContentAdapter.MAX_IMAGE_COUNT) {
                    Toast.makeText(mContext, "最多只能选择5张图片", Toast.LENGTH_SHORT).show();
                    cb_item.setChecked(false);
                    return;
                }
            }
        });
        cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String path = mList.get(mPosition);

                if (isChecked) {
                    PhotoPickerManager.getInstance().addPath(path);
                } else {
                    PhotoPickerManager.getInstance().removePath(path);
                }
                setSendText();
            }
        });

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HuxinSdkManager.instance().getStackAct().hasActivity(PhotoDirectoryActivity.class)) {
                    HuxinSdkManager.instance().getStackAct().finishActivity(PhotoDirectoryActivity.class);
                }

                ArrayList<String> paths = PhotoPickerManager.getInstance().getPaths();
                if (ListUtils.isEmpty(paths)) {
                    String path = mList.get(mPosition);
                    paths.add(path);
                }

                returnData(paths);
            }
        });

        cb_isOriginal = (CheckBox) findViewById(R.id.cb_is_original);
        cb_isOriginal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PhotoPickerManager.getInstance().setOriginal(isChecked);
            }
        });
        boolean value = getIntent().getBooleanExtra(FilePickerConst.KEY_IS_ORIGINAL, false);
        cb_isOriginal.setChecked(value);
        PhotoPickerManager.getInstance().setOriginal(value);


        mList = getIntent().getStringArrayListExtra("image");
        int index = getIntent().getIntExtra("index", 0);
        mPosition = index;

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        final List<View> listView = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();

        if (mList != null && mList.size() > 0) {
            for (final String url : mList) {
                View view = mInflater.inflate(R.layout.item_preview_image, null);
                listView.add(view);

                PhotoView imageView = (PhotoView) view.findViewById(R.id.img_content);
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .error(R.drawable.hx_icon_rd)
                                .fitCenter())
                        .into(imageView);
            }
        }

        PagerIndicatorAdapter adapter = new PagerIndicatorAdapter(this, listView);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                Log.d("YW", "onPageSelected position: " + position);
                tv_title.setText((mPosition + 1) + "/" + mList.size());

                String url = mList.get(mPosition);
                ArrayList<String> paths = PhotoPickerManager.getInstance().getPaths();
                if (paths.contains(url)) {
                    cb_item.setChecked(true);
                } else {
                    cb_item.setChecked(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setSendText();
        tv_title.setText((mPosition + 1) + "/" + mList.size());

        String path = mList.get(mPosition);
        ArrayList<String> list = PhotoPickerManager.getInstance().getPaths();
        if (list.contains(path)) {
            cb_item.setChecked(true);
        }
    }


    public void setSendText() {
        ArrayList<String> list = PhotoPickerManager.getInstance().getPaths();
        String format = getResources().getString(R.string.hx_im_send_img_count);

        btn_send.setText(String.format(format, list.size(), AlbumContentAdapter.MAX_IMAGE_COUNT));
    }

    private void returnData(ArrayList<String> paths) {
        if (HuxinSdkManager.instance().getStackAct().hasActivity(PhotoPreviewActivity.class)) {
            HuxinSdkManager.instance().getStackAct().finishActivity(PhotoPreviewActivity.class);
        }

        Intent intent = new Intent();
        intent.putStringArrayListExtra(PhotoPreviewActivity.KEY_SELECTED_MEDIA, paths);
        intent.putExtra(FilePickerConst.KEY_IS_ORIGINAL, cb_isOriginal.isChecked());
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent().putExtra(FilePickerConst.KEY_IS_ORIGINAL, cb_isOriginal.isChecked());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
