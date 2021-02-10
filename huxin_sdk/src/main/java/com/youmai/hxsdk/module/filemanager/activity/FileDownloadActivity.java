package com.youmai.hxsdk.module.filemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.bean.Document;
import com.youmai.hxsdk.module.filemanager.view.FileDownloadItemView;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2017.08.30 13:57
 * 描述：
 */
public class FileDownloadActivity extends SdkHomeActivity implements View.OnClickListener {

    public static final String TITLE = "title";
    public static final String QQ_LIST_DATA = "qq_list_data";
    public static final String WEIXIN_LIST_DATA = "weixin_list_data";
    public static final String DOWNLOAD_LIST_DATA = "download_list_data";

    private ArrayList<Document> mQQListData, mWeiXinListData, mDownloadListData;
    private String mTitle;

    private FileDownloadItemView ll_item_qq, ll_item_weixin, ll_item_download;

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, FileDLClassifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int i = v.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.ll_item_qq) {
            intent.putExtra(FileDLClassifyActivity.TITLE, getString(R.string.hx_file_manager_qq));
            intent.putParcelableArrayListExtra(FileDLClassifyActivity.DOWNLOAD_LIST_DATA, mQQListData);
            startActivity(intent);

        } else if (i == R.id.ll_item_weixin) {
            intent.putExtra(FileDLClassifyActivity.TITLE, getString(R.string.hx_file_manager_weixin));
            intent.putParcelableArrayListExtra(FileDLClassifyActivity.DOWNLOAD_LIST_DATA, mWeiXinListData);
            startActivity(intent);

        } else if (i == R.id.ll_item_download) {
            intent.putExtra(FileDLClassifyActivity.TITLE, getString(R.string.hx_file_manager_download_content));
            intent.putParcelableArrayListExtra(FileDLClassifyActivity.DOWNLOAD_LIST_DATA, mDownloadListData);
            startActivity(intent);

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.hx_activity_file_manager_download);
        super.onCreate(savedInstanceState);

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }

    @Override
    public void initData() {
        mTitle = getIntent().getStringExtra(TITLE);
        mQQListData = getIntent().getParcelableArrayListExtra(QQ_LIST_DATA);
        mWeiXinListData = getIntent().getParcelableArrayListExtra(WEIXIN_LIST_DATA);
        mDownloadListData = getIntent().getParcelableArrayListExtra(DOWNLOAD_LIST_DATA);
    }

    @Override
    public void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(mTitle);

        ll_item_weixin = (FileDownloadItemView) findViewById(R.id.ll_item_weixin);
        ll_item_qq = (FileDownloadItemView) findViewById(R.id.ll_item_qq);
        ll_item_download = (FileDownloadItemView) findViewById(R.id.ll_item_download);

    }

    @Override
    public void bindClick() {
        ll_item_qq.setOnClickListener(this);
        ll_item_weixin.setOnClickListener(this);
        ll_item_download.setOnClickListener(this);
    }

    @Override
    public void bindData() {
        if (mQQListData != null && mQQListData.size() > 0) {
            ll_item_qq.setCountText(mQQListData.size());
        }
        if (mWeiXinListData != null && mWeiXinListData.size() > 0) {
            ll_item_weixin.setCountText(mWeiXinListData.size());
        }
        if (mDownloadListData != null && mDownloadListData.size() > 0) {
            ll_item_download.setCountText(mDownloadListData.size());
        }
    }
}
