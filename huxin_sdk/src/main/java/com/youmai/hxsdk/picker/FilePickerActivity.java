package com.youmai.hxsdk.picker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.ArrayList;

import com.youmai.hxsdk.R;

import com.youmai.hxsdk.picker.fragments.DocFragment;
import com.youmai.hxsdk.picker.fragments.DocPickerFragment;
import com.youmai.hxsdk.picker.fragments.MediaFolderPickerFragment;
import com.youmai.hxsdk.picker.fragments.MediaDetailPickerFragment;
import com.youmai.hxsdk.picker.fragments.MediaPickerFragment;
import com.youmai.hxsdk.picker.utils.FragmentUtil;

public class FilePickerActivity extends AppCompatActivity implements
        MediaDetailPickerFragment.PhotoPickerFragmentListener,
        DocFragment.PhotoPickerFragmentListener,
        MediaFolderPickerFragment.PhotoPickerFragmentListener,
        MediaPickerFragment.MediaPickerFragmentListener,
        PickerManagerListener {

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setTheme(PickerManager.getInstance().getTheme());
        setContentView(R.layout.hx_activity_file_picker);
        if (!PickerManager.getInstance().isEnableOrientation())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
    }


    /*@Override
    protected void onResume() {
        super.onResume();
        //FloatViewUtil.instance().hideFloatView();
    }*/


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ArrayList<String> selectedPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            type = intent.getIntExtra(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER);

            setToolbarTitle(0);

            if (selectedPaths != null) {
                if (type == FilePickerConst.MEDIA_PICKER)
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_MEDIA);
                else
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_DOCUMENT);
            }

            PickerManager.getInstance().setPickerManagerListener(this);
            openSpecificFragment(type, selectedPaths);
        }
    }

    private void openSpecificFragment(int type, ArrayList<String> selectedPaths) {
        if (PickerManager.getInstance().getMaxCount() == 1)
            selectedPaths.clear();

        if (type == FilePickerConst.MEDIA_PICKER) {
            MediaPickerFragment photoFragment = MediaPickerFragment.newInstance();
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        } else {
            if (PickerManager.getInstance().isDocSupport())
                PickerManager.getInstance().addDocTypes();

            DocPickerFragment photoFragment = DocPickerFragment.newInstance(selectedPaths);
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        }
    }

    private void setToolbarTitle(int count) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (PickerManager.getInstance().getMaxCount() > 1)
                actionBar.setTitle(String.format(getString(R.string.attachments_title_text), count, PickerManager.getInstance().getMaxCount()));
            else {
                if (type == FilePickerConst.MEDIA_PICKER)
                    actionBar.setTitle(R.string.select_photo_text);
                else
                    actionBar.setTitle(R.string.select_doc_text);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_done) {
            if (type == FilePickerConst.MEDIA_PICKER)
                returnData(PickerManager.getInstance().getSelectedPhotos());
            else
                returnData(PickerManager.getInstance().getSelectedFiles());

            return true;
        } else if (i == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(int currentCount) {
        setToolbarTitle(currentCount);
    }

    @Override
    public void onSingleItemSelected(ArrayList<String> paths) {
        returnData(paths);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_MEDIA_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    if (type == FilePickerConst.MEDIA_PICKER)
                        returnData(PickerManager.getInstance().getSelectedPhotos());
                    else
                        returnData(PickerManager.getInstance().getSelectedFiles());
                }
                break;
        }
    }

    private void returnData(ArrayList<String> paths) {
        Intent intent = new Intent();
        if (type == FilePickerConst.MEDIA_PICKER)
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA, paths);
        else
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, paths);
        setResult(RESULT_OK, intent);
        finish();
    }
}
