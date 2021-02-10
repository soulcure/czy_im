package com.youmai.hxsdk.module.filemanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.bean.Document;
import com.youmai.hxsdk.module.filemanager.loader.FileListLoader;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class FileManagerActivity extends SdkHomeActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<List<Document>> {

    private static final String TAG = "FileManagerActivity";
    public static final String REQUEST_CODE_CALLBACK = "requestCode";

    private String dstUuid;
    private ArrayList<Document> mQQListData, mWeiXinListData, mDownloadListData;
    private ProgressDialog progressDialog = null;

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, FileClassifyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int i = v.getId();
        if (i == R.id.tv_back) {
            finish();
            isPreview = false;

        } else if (i == R.id.tv_jump_txt) {
            intent.putExtra(FileClassifyActivity.TXT_TITLE, getString(R.string.hx_file_manager_txt));
            intent.putParcelableArrayListExtra(FileClassifyActivity.TXT_LIST_DATA, PickerManager.getInstance().getTxtList());
            startActivity(intent);

        } else if (i == R.id.tv_jump_video) {
            intent.putExtra(FileClassifyActivity.TXT_TITLE, getString(R.string.hx_file_manager_video));
            intent.putParcelableArrayListExtra(FileClassifyActivity.TXT_LIST_DATA, PickerManager.getInstance().getVideoList());
            startActivity(intent);

        } else if (i == R.id.tv_jump_music) {
            intent.putExtra(FileClassifyActivity.TXT_TITLE, getString(R.string.hx_file_manager_music));
            intent.putParcelableArrayListExtra(FileClassifyActivity.TXT_LIST_DATA, PickerManager.getInstance().getMusicList());
            startActivity(intent);

        } else if (i == R.id.tv_jump_download) {
            Intent it = new Intent(FileManagerActivity.this, FileDownloadActivity.class);
            it.putExtra(FileDownloadActivity.TITLE, getString(R.string.hx_file_manager_download));
            it.putParcelableArrayListExtra(FileDownloadActivity.QQ_LIST_DATA, mQQListData);
            it.putParcelableArrayListExtra(FileDownloadActivity.WEIXIN_LIST_DATA, mWeiXinListData);
            it.putParcelableArrayListExtra(FileDownloadActivity.DOWNLOAD_LIST_DATA, mDownloadListData);
            startActivity(it);

        } else if (i == R.id.tv_jump_app) {
            intent.putExtra(FileClassifyActivity.TXT_TITLE, getString(R.string.hx_file_manager_app));
            intent.putParcelableArrayListExtra(FileClassifyActivity.TXT_LIST_DATA, PickerManager.getInstance().getAppList());
            startActivity(intent);

        } else if (i == R.id.tv_jump_zip) {
            intent.putExtra(FileClassifyActivity.TXT_TITLE, getString(R.string.hx_file_manager_zip));
            intent.putParcelableArrayListExtra(FileClassifyActivity.TXT_LIST_DATA, PickerManager.getInstance().getZipList());
            startActivity(intent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hx_activity_file_manager);
        super.onCreate(savedInstanceState);

        PickerManager.getInstance().getTxtList().clear();
        PickerManager.getInstance().getVideoList().clear();
        PickerManager.getInstance().getMusicList().clear();
        PickerManager.getInstance().getAppList().clear();
        PickerManager.getInstance().getZipList().clear();

        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoad();
        bindDataToView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (!ListUtils.isEmpty(mDownloadListData)) {
            mDownloadListData.clear();
            mDownloadListData = null;
        }

        if (!ListUtils.isEmpty(mQQListData)) {
            mQQListData.clear();
            mQQListData = null;
        }

        if (!ListUtils.isEmpty(mWeiXinListData)) {
            mWeiXinListData.clear();
            mWeiXinListData = null;
        }

        PickerManager.getInstance().setReverse(false);
        PickerManager.getInstance().clearSelections();
        //PickerManager.getInstance().setPickerManagerListener(null);
        HuxinSdkManager.instance().getStackAct().removeActivity(this);
        HuxinSdkManager.instance().getStackAct().finishAll(1);
        System.gc();
    }

    private void startLoad() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.hx_file_manager_loading));
        progressDialog.show();

        getSupportLoaderManager().initLoader(1, null, this);

    }

    @Override
    public Loader<List<Document>> onCreateLoader(int id, Bundle args) {

        //args 是getSupportLoaderManager().initLoader传过来的数据
        Log.e(TAG, "onCreateLoader");

        return new FileListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Document>> loader, final List<Document> data) {

        Log.e(TAG, "onLoadFinished-> " + data.size());

        PickerManager.getInstance().reverseList();
        PickerManager.getInstance().setReverse(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_txt_count.setText("(" + PickerManager.getInstance().getTxtList().size() + ")");
                tv_video_count.setText("(" + PickerManager.getInstance().getVideoList().size() + ")");
                tv_music_count.setText("(" + PickerManager.getInstance().getMusicList().size() + ")");
                tv_app_count.setText("(" + PickerManager.getInstance().getAppList().size() + ")");
                tv_zip_count.setText("(" + PickerManager.getInstance().getZipList().size() + ")");
            }
        }, 300);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Document>> loader) {

        Log.e(TAG, "onLoaderReset");
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    TextView tv_jump_txt, tv_txt_count;
    TextView tv_jump_video, tv_video_count;
    TextView tv_jump_music, tv_music_count;
    TextView tv_jump_download, tv_download_count;
    TextView tv_jump_app, tv_app_count;
    TextView tv_jump_zip, tv_zip_count;

    @Override
    public void initData() {
        dstUuid = getIntent().getStringExtra("dstUuid");
        PickerManager.getInstance().setDstUuid(dstUuid);
        int requestCode = getIntent().getIntExtra(REQUEST_CODE_CALLBACK, FilePickerConst.IM_REQUEST_CALLBACK);
        PickerManager.getInstance().setRequestCode(requestCode);
    }

    @Override
    public void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(getString(R.string.hx_file_manager_select_03));

        tv_jump_txt = (TextView) findViewById(R.id.tv_jump_txt);
        tv_jump_video = (TextView) findViewById(R.id.tv_jump_video);
        tv_jump_music = (TextView) findViewById(R.id.tv_jump_music);
        tv_jump_download = (TextView) findViewById(R.id.tv_jump_download);
        tv_jump_app = (TextView) findViewById(R.id.tv_jump_app);
        tv_jump_zip = (TextView) findViewById(R.id.tv_jump_zip);

        tv_txt_count = (TextView) findViewById(R.id.tv_txt_count);
        tv_video_count = (TextView) findViewById(R.id.tv_video_count);
        tv_music_count = (TextView) findViewById(R.id.tv_music_count);
        tv_download_count = (TextView) findViewById(R.id.tv_download_count);
        tv_app_count = (TextView) findViewById(R.id.tv_app_count);
        tv_zip_count = (TextView) findViewById(R.id.tv_zip_count);
    }

    @Override
    public void bindClick() {
        tv_jump_txt.setOnClickListener(this);
        tv_jump_video.setOnClickListener(this);
        tv_jump_music.setOnClickListener(this);
        tv_jump_download.setOnClickListener(this);
        tv_jump_app.setOnClickListener(this);
        tv_jump_zip.setOnClickListener(this);
    }

    private void bindDataToView() {

        mQQListData = PickerManager.getInstance().loadLocalQQFile();
        mWeiXinListData = PickerManager.getInstance().loadLocalWeiXinFile();
        if (Build.BRAND.equalsIgnoreCase("vivo")) {
            mDownloadListData = PickerManager.getInstance().loadDownloadFile("/" + getString(R.string.hx_file_manager_download));
        } else if (Build.BRAND.equalsIgnoreCase("HUAWEI")) {
            mDownloadListData = PickerManager.getInstance().loadDownloadFile("Downloads");
        } else if (Build.BRAND.equalsIgnoreCase("honor")) {
            mDownloadListData = PickerManager.getInstance().loadDownloadFile("/Browser");
        } else if (Build.BRAND.equalsIgnoreCase("oppo")) {
            mDownloadListData = PickerManager.getInstance().loadDownloadFile("/ColorOs/Browser/download");
        } else if (Build.BRAND.equalsIgnoreCase("meizu")) {
            mDownloadListData = PickerManager.getInstance().loadDownloadFile("/Download/Browser");
        } else if (Build.BRAND.equalsIgnoreCase("lenovo")) {
            mDownloadListData = PickerManager.getInstance().loadDownloadFile("/Download");
        } /*else if (Build.BRAND.equalsIgnoreCase("LeEco")) {
            mDownloadListData = PickerManager.getInstance().loadDownloadFile("/Download");
        } */ else {
            mDownloadListData = PickerManager.getInstance().loadDefaultDownloadFile();
        }
        tv_download_count.setText("(" + (mWeiXinListData.size() + mQQListData.size() + mDownloadListData.size()) + ")");

        //Log.e("YW", "mDownloadListData: " + mDownloadListData.size());
    }

}
