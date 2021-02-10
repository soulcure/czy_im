package com.youmai.hxsdk.module.filemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerManagerListener;
import com.youmai.hxsdk.module.filemanager.adapter.FileClassifyAdapter;
import com.youmai.hxsdk.module.filemanager.bean.Document;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2017.08.29 18:04
 * 描述：
 */
public class FileClassifyActivity extends SdkHomeActivity implements View.OnClickListener, PickerManagerListener {

    private static final String TAG = "FileClassifyActivity";
    public static final String TXT_TITLE = "txt_title";
    public static final String TXT_LIST_DATA = "txt_list_data";

    //bundle 数据
    private String txt_title;
    ArrayList<Document> mListData;

    //view
    private TextView tv_file_type;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    //adapter
    private FileClassifyAdapter mAdapter;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_back) {
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.hx_activity_file_manager_classify);
        super.onCreate(savedInstanceState);

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!ListUtils.isEmpty(mListData)) {
            mListData.clear();
            mListData = null;
        }
        HuxinSdkManager.instance().getStackAct().finishActivity(FileClassifyActivity.this);
    }

    @Override
    public void initData() {
        txt_title = getIntent().getStringExtra(TXT_TITLE);
        mListData = getIntent().getParcelableArrayListExtra(TXT_LIST_DATA);
    }

    @Override
    public void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(txt_title);

        tv_file_type = (TextView) findViewById(R.id.tv_file_type);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        if (mListData == null || mListData.size() <= 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

    }

    @Override
    public void bindData() {
        tv_file_type.setText(txt_title);
        mAdapter = new FileClassifyAdapter(mContext, mListData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void bindClick() {
        PickerManager.getInstance().setPickerManagerListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_MEDIA_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    returnData(PickerManager.getInstance().getSelectedFiles());
                }
                break;
        }
    }

    private void returnData(ArrayList<String> paths) {
        if (PickerManager.getInstance().getRefreshUIListener() != null) {
            PickerManager.getInstance().getRefreshUIListener().onRefresh(paths, PickerManager.getInstance().getRequestCode());
        }
        if (PickerManager.getInstance().getRefreshUI2Listener() != null) {
            PickerManager.getInstance().getRefreshUI2Listener().onRefresh(paths, PickerManager.getInstance().getRequestCode());
        }
        HuxinSdkManager.instance().getStackAct().finishAll(1);
        //PickerManager.getInstance().setPickerManagerListener(null);
        PickerManager.getInstance().clearSelections();
        finish();
    }

    @Override
    public void onItemSelected(int currentCount) {
        // undo nothing
    }

    @Override
    public void onSingleItemSelected(ArrayList<String> paths) {
        //选择发送的文件
        Log.d(TAG, "select file ready to send: " + paths.toString());
        returnData(paths);
    }

}
