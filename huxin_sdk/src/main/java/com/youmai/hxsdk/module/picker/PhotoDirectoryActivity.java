package com.youmai.hxsdk.module.picker;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.picker.adapter.AlbumFolderAdapter;
import com.youmai.hxsdk.module.picker.loader.LocalImageLoader;
import com.youmai.hxsdk.module.picker.loader.LocalVideoLoader;
import com.youmai.hxsdk.module.picker.model.LocalImage;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class PhotoDirectoryActivity extends AppCompatActivity {

    private final static int LOADER_VIDEO_ID = 1001;
    private final static int LOADER_IMAGE_ID = 1000;

    private final static int START_PHOTO_VIEW = 2000;

    private Context mContext;

    private TextView tv_cancel;

    private RecyclerView recyclerView;

    private AlbumFolderAdapter albumFolderAdapter;


    /**
     * 本地所有相册数据
     * key:相册名称
     * name:指定相册下的图片列表
     */


    private ProgressDialog mProgressDialog;

    private LoaderManager.LoaderCallbacks imageLoader = new LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<LocalImage>>>() {
        @Override
        public Loader<HashMap<String, ArrayList<LocalImage>>> onCreateLoader(int id, Bundle args) {
            return new LocalImageLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<HashMap<String, ArrayList<LocalImage>>> loader, HashMap<String, ArrayList<LocalImage>> data) {
            if (data == null) {
                return;
            }
            PhotoPickerManager.getInstance().putAll(data);

            setDirView();
            hideProgress();
        }

        @Override
        public void onLoaderReset(Loader<HashMap<String, ArrayList<LocalImage>>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks videoLoader = new LoaderManager.LoaderCallbacks<ArrayList<LocalImage>>() {
        @Override
        public Loader<ArrayList<LocalImage>> onCreateLoader(int id, Bundle args) {
            return new LocalVideoLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<LocalImage>> loader, ArrayList<LocalImage> data) {
            if (data != null && data.size() > 0) {
                PhotoPickerManager.getInstance().put(PhotoPreviewActivity.ALL_VIDEO_KEY, data);
            }
            setDirView();
            hideProgress();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<LocalImage>> loader) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_picker);
        mContext = this;

        ArrayList<LocalImage> images = PhotoPickerManager.getInstance().get(PhotoPreviewActivity.ALL_IMAGE_KEY);
        if (ListUtils.isEmpty(images)) {
            getLoaderManager().initLoader(LOADER_VIDEO_ID, null, imageLoader);
            showProgress("", "图片加载中...", -1);
        }

        ArrayList<LocalImage> videos = PhotoPickerManager.getInstance().get(PhotoPreviewActivity.ALL_VIDEO_KEY);
        if (ListUtils.isEmpty(videos)) {
            getLoaderManager().initLoader(LOADER_VIDEO_ID, null, videoLoader);
            showProgress("", "视频加载中...", -1);
        }

        initView();
        setDirView();

        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    private void initView() {
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerManager.getInstance().clear();
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        albumFolderAdapter = new AlbumFolderAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(albumFolderAdapter);
    }


    private void setDirView() {
        albumFolderAdapter.setData(PhotoPickerManager.getInstance().getAlbums());
    }


    public void setPhotoView(String key) {
        Intent intent = new Intent(this, PhotoPreviewActivity.class);
        intent.putExtra(PhotoPreviewActivity.DIR_NAME, key);
        startActivityForResult(intent, START_PHOTO_VIEW);
    }


    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            //mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!TextUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_PHOTO_VIEW) {
            setDirView();
        }
    }

    @Override
    public void onBackPressed() {
        PhotoPickerManager.getInstance().clear();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
        super.onDestroy();
    }
}
