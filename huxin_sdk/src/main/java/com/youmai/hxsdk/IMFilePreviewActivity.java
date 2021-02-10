package com.youmai.hxsdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.http.DownloadListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;

import java.io.File;

/**
 * 作者：create by YW
 * 日期：2016.11.23 16:15
 * 描述：
 */
public class IMFilePreviewActivity extends SdkBaseActivity {

    public static final String IM_FILE_BEAN = "im_file_bean";
    public static final String FULL_FILE_BEAN = "full_file_bean";
    public static final String FULL_VIEW_FILE = "full_view_file";

    private static final int MSG_UPDATE = 0x01;

    private ImageView tv_preview_back; //返回键
    private ImageView iv_file_logo; //文件logo
    private TextView tv_file_name; //文件name
    private LinearLayout ll_progress_parent;//进度条父布局
    private ProgressBar pb_progress_bar; //进度条
    private TextView tv_open_file; //第三方打开文档
    private TextView downloadingProgressText;//当前下载进度

    private boolean isOpenFile = false;//是否打开文件
    public boolean isFullViewFile = false;//是否弹屏处过来打开的
    private boolean isExit = false;//是否关闭线程

    private String totalSize;//文件总大小

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_filepreview);

        initView();
        initDownload();

    }

    @Override
    public void onResume() {
        super.onResume();
        isExit = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isExit = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        tv_preview_back = (ImageView) findViewById(R.id.tv_im_preview_back);
        iv_file_logo = (ImageView) findViewById(R.id.iv_im_file_type);
        tv_file_name = (TextView) findViewById(R.id.tv_im_file_name);
        ll_progress_parent = (LinearLayout) findViewById(R.id.ll_im_progress_parent);
        pb_progress_bar = (ProgressBar) findViewById(R.id.pb_im_progress_bar);
        tv_open_file = (TextView) findViewById(R.id.tv_im_open_file);
        downloadingProgressText = (TextView) findViewById(R.id.downloading_progress_text);
        initListener();

    }

    private CacheMsgBean cacheMsgBean;
    private CacheMsgFile cacheMsgFile;

    private void initDownload() {

        cacheMsgBean = getIntent().getParcelableExtra(IM_FILE_BEAN);
        isFullViewFile = getIntent().getBooleanExtra(FULL_VIEW_FILE, false);

        if (null == cacheMsgBean) {
            return;
        }

        cacheMsgFile = (CacheMsgFile) cacheMsgBean.getJsonBodyObj();

        if (null == cacheMsgFile) {
            return;
        }

        boolean isFileExist = new File(FileConfig.getFileDownLoadPath(), cacheMsgFile.getFileName()).exists();
        if ((cacheMsgBean.getMsgType() == CacheMsgBean.RECEIVE_FILE && !cacheMsgBean.isRightUI() && isFileExist)
                || (cacheMsgBean.getMsgType() == CacheMsgBean.SEND_FILE && cacheMsgBean.isRightUI())) {
            isOpenFile = true;
            ll_progress_parent.setVisibility(View.GONE);
            tv_open_file.setVisibility(View.VISIBLE);
        }

        totalSize = IMHelper.convertFileSize(cacheMsgFile.getFileSize());
        downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), "0k", totalSize));
        tv_file_name.setText(cacheMsgFile.getFileName());
        iv_file_logo.setImageResource(IMHelper.getFileImgRes(cacheMsgFile.getFileName(), false));

        if (cacheMsgBean.getMsgType() == CacheMsgBean.RECEIVE_FILE && !isFileExist) {
            //new Thread(new DownloadFile(cacheMsgFile.getFileUrl(), cacheMsgFile.getFileName())).start();
            breakDownload(cacheMsgFile.getFileUrl(), cacheMsgFile.getFileName());
        }

    }

    private void initListener() {

        tv_preview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenFile) {
                    if (!cacheMsgBean.isRightUI()) {
                        openFile(new File(FileConfig.getFileDownLoadPath(), cacheMsgFile.getFileName()));
                    } else {
                        openFile(new File(cacheMsgFile.getFilePath()));
                    }
                } else {
                    breakDownload(cacheMsgFile.getFileUrl(), cacheMsgFile.getFileName());
                }
            }
        });
    }

    private void breakDownload(String url, String fileName) {
        OkHttpConnector.httpDownload(url, null, null,
                fileName, new DownloadListener() {
                    @Override
                    public void onProgress(int cur, int total) {
                        pb_progress_bar.setProgress(cur);
                        pb_progress_bar.setMax(total);
                        String progress = String.format(getString(R.string.hx_sdk_downloading2),
                                IMHelper.convertFileSize(cur),
                                IMHelper.convertFileSize(total));
                        downloadingProgressText.setText(progress);
                    }

                    @Override
                    public void onFail(String err) {
                        isOpenFile = false;
                        tv_open_file.setVisibility(View.VISIBLE);
                        tv_open_file.setText("重新下载");
                    }

                    @Override
                    public void onSuccess(String path) {
                        ll_progress_parent.setVisibility(View.GONE);
                        tv_open_file.setText(R.string.hx_sdk_open_other_app);
                        tv_open_file.setVisibility(View.VISIBLE);
                        isOpenFile = true;
                    }
                });
    }


    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);

            //Uri uri = Uri.fromFile(file);
            //targetSdkVersion >= 24
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileImProvider",
                    file);
            //设置intent的data和Type属性。
            intent.setDataAndType(uri, type);
            //跳转
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "该文件无法打开", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private final String[][] MIME_MapTable = { //{后缀名，MIME类型}

            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };
}
