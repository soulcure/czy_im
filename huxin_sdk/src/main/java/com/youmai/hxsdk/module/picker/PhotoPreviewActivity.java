package com.youmai.hxsdk.module.picker;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.picker.adapter.AlbumContentAdapter;
import com.youmai.hxsdk.module.picker.loader.LocalImageLoader;
import com.youmai.hxsdk.module.picker.model.GridSpacingItemDecoration;
import com.youmai.hxsdk.module.picker.model.LocalImage;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PhotoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int LOADER_IMAGE_ID = 1000;

    public static final String DIR_NAME = "dir_name";
    public static final String FILE_LIST = "file_list";

    public static final String FROM_TO_IM = "from_to_im";

    public final static String KEY_SELECTED_MEDIA = "SELECTED_PHOTOS";
    public static final int REQUEST_CODE_PHOTO = 233;


    public final static int GIRD_COUNT = 4;
    public static final int DRI_WIDTH = 10;

    public static final String ALL_IMAGE_KEY = "最近照片";
    public static final String ALL_VIDEO_KEY = "本地视频";


    private Context mContext;

    private TextView tv_title;
    private TextView tv_cancel;
    private ImageView img_back;
    private TextView tv_preview;
    private Button btn_send;

    private RecyclerView recyclerView;

    private AlbumContentAdapter albumContentAdapter;

    private CheckBox cb_original;

    private boolean fromToIM;

    private ProgressDialog mProgressDialog;

    private LoaderManager.LoaderCallbacks imageLoader = new LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<LocalImage>>>() {
        @Override
        public Loader<HashMap<String, ArrayList<LocalImage>>> onCreateLoader(int id, Bundle args) {
            showProgress("", "图片加载中...", -1);
            return new LocalImageLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<HashMap<String, ArrayList<LocalImage>>> loader, HashMap<String, ArrayList<LocalImage>> data) {
            if (data == null) {
                return;
            }

            PhotoPickerManager.getInstance().putAll(data);

            List<LocalImage> dataList = PhotoPickerManager.getInstance().get(ALL_IMAGE_KEY);

            if (dataList != null) {
                if (albumContentAdapter == null) {
                    albumContentAdapter = new AlbumContentAdapter(mContext, dataList);
                    recyclerView.setAdapter(albumContentAdapter);
                    tv_title.setText(ALL_IMAGE_KEY);
                }
            }

            hideProgress();

        }

        @Override
        public void onLoaderReset(Loader<HashMap<String, ArrayList<LocalImage>>> loader) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        mContext = this;

        fromToIM = getIntent().getBooleanExtra(FROM_TO_IM, false);

        initView();
        initData();

        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_preview = (TextView) findViewById(R.id.tv_preview);
        btn_send = (Button) findViewById(R.id.btn_send);
        cb_original = (CheckBox) findViewById(R.id.cb_is_original);

        tv_cancel.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_preview.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager imageGridManger = new GridLayoutManager(this, GIRD_COUNT);
        recyclerView.setLayoutManager(imageGridManger);

        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(GIRD_COUNT, DRI_WIDTH, true);
        recyclerView.addItemDecoration(itemDecoration);

        btn_send.setEnabled(false);
        cb_original.setChecked(false);
    }


    private void initData() {
        String dirName = getIntent().getStringExtra(DIR_NAME);
        if (TextUtils.isEmpty(dirName)) {
            dirName = ALL_IMAGE_KEY;
        }

        tv_title.setText(dirName);

        List<LocalImage> dataList = PhotoPickerManager.getInstance().getAlbums().get(dirName);

        if (ListUtils.isEmpty(dataList)) {
            getLoaderManager().initLoader(LOADER_IMAGE_ID, null, imageLoader);
        } else {
            albumContentAdapter = new AlbumContentAdapter(this, dataList);
            recyclerView.setAdapter(albumContentAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (albumContentAdapter != null
                /*&& !ListUtils.isEmpty(PhotoPickerManager.getInstance().getPaths())*/) {
            albumContentAdapter.notifyDataSetChanged();
            setSendText(PhotoPickerManager.getInstance().getPaths());
        }

    }

    public void setSendText(List<String> list) {
        int size = list.size();

        if (size == 0) {
            btn_send.setEnabled(false);
        } else {
            btn_send.setEnabled(true);
        }

        String format = getResources().getString(R.string.hx_im_send_img_count);
        btn_send.setText(String.format(format, size, AlbumContentAdapter.MAX_IMAGE_COUNT));
    }


    public static void start(Activity activity) {
        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putExtra(FROM_TO_IM, true);
        activity.startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send) {

            if (HuxinSdkManager.instance().getStackAct().hasActivity(PhotoDirectoryActivity.class)) {
                HuxinSdkManager.instance().getStackAct().finishActivity(PhotoDirectoryActivity.class);
            }

            returnData(PhotoPickerManager.getInstance().getPaths());
        } else if (id == R.id.img_back) {
            onBackPressed();
        } else if (id == R.id.tv_cancel) {
            PhotoPickerManager.getInstance().clear();
            //albumContentAdapter.clearCheckView();
            finish();

        } else if (id == R.id.tv_preview) {
            ArrayList<String> list = PhotoPickerManager.getInstance().getPaths();
            if (list != null && list.size() > 0) {

                String path = list.get(0);
                if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                        || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
                    Intent intent = new Intent(mContext, PreviewVideoActivity.class);
                    intent.putExtra("video", path);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, PreviewImageActivity.class);
                    intent.putStringArrayListExtra("image", list);
                    intent.putExtra("index", 0);
                    intent.putExtra(FilePickerConst.KEY_IS_ORIGINAL, cb_original.isChecked());
                    //mContext.startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE_PHOTO);
                }
            }
        }
    }


    private void returnData(ArrayList<String> paths) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(KEY_SELECTED_MEDIA, paths);
        intent.putExtra(FilePickerConst.KEY_IS_ORIGINAL, cb_original.isChecked());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO) {
            cb_original.setChecked(data.getBooleanExtra(FilePickerConst.KEY_IS_ORIGINAL, false));
        }
    }

    @Override
    public void onBackPressed() {
        if (fromToIM) {
            Intent intent = new Intent(this, PhotoDirectoryActivity.class);
            intent.putExtra("isLoader", true);
            //intent.putExtra("map", mData);
            startActivity(intent);
        }

        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
        super.onDestroy();
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
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!com.youmai.smallvideorecord.utils.StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public boolean getIsOriginal() {
        return cb_original.isChecked();
    }
}
