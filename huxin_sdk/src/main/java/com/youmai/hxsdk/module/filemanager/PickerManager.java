package com.youmai.hxsdk.module.filemanager;

import android.os.Environment;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.bean.BaseFile;
import com.youmai.hxsdk.module.filemanager.bean.Document;
import com.youmai.hxsdk.module.filemanager.bean.FileType;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerManagerListener;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerRefreshUI2Listener;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerRefreshUIListener;
import com.youmai.hxsdk.module.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yw on 2017.8.31.
 */
public class PickerManager {

    private static PickerManager ourInstance = null;
    private int maxCount = FilePickerConst.DEFAULT_MAX_COUNT;
    private int currentCount;
    private PickerManagerListener pickerManagerListener;
    protected PickerRefreshUIListener mRefreshUIListener;
    private PickerRefreshUI2Listener mRefreshUI2Listener;

    public static PickerManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new PickerManager();
        }
        return ourInstance;
    }

    public void nullOurInstance() {
        ourInstance = null;
    }

    private ArrayList<String> mediaFiles;
    private ArrayList<String> docFiles;
    private ArrayList<FileType> fileTypes;

    private int theme = R.style.HxSdkTheme;

    private boolean showVideos;

    private boolean docSupport = true;

    private boolean enableCamera = true;

    private boolean enableOrientation = false;

    private boolean showFolderView = true;

    private String providerAuthorities;

    private PickerManager() {
        mediaFiles = new ArrayList<>();
        docFiles = new ArrayList<>();
        fileTypes = new ArrayList<>();
    }

    public void setMaxCount(int count) {
        clearSelections();
        this.maxCount = count;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setPickerManagerListener(PickerManagerListener pickerManagerListener) {
        this.pickerManagerListener = pickerManagerListener;
    }

    public void setRefreshUIListener(PickerRefreshUIListener listener) {
        mRefreshUIListener = listener;
    }

    public PickerRefreshUIListener getRefreshUIListener() {
        return mRefreshUIListener;
    }

    public void setRefreshUI2Listener(PickerRefreshUI2Listener listener) {
        this.mRefreshUI2Listener = listener;
    }

    public PickerRefreshUI2Listener getRefreshUI2Listener() {
        return mRefreshUI2Listener;
    }

    public void add(String path, int type) {
        if (path != null && shouldAdd()) {
            if (!mediaFiles.contains(path) && type == FilePickerConst.FILE_TYPE_MEDIA)
                mediaFiles.add(path);
            else if (type == FilePickerConst.FILE_TYPE_DOCUMENT)
                docFiles.add(path);
            else
                return;

            currentCount++;

            if (pickerManagerListener != null) {
                pickerManagerListener.onItemSelected(currentCount);

                if (maxCount == 1)
                    pickerManagerListener.onSingleItemSelected(type == FilePickerConst.FILE_TYPE_MEDIA ? getSelectedPhotos() : getSelectedFiles());
            }
        }
    }

    public void add(ArrayList<String> paths, int type) {
        for (int index = 0; index < paths.size(); index++) {
            add(paths.get(index), type);
        }
    }

    public void remove(String path, int type) {
        if ((type == FilePickerConst.FILE_TYPE_MEDIA) && mediaFiles.contains(path)) {
            mediaFiles.remove(path);
            currentCount--;

        } else if (type == FilePickerConst.FILE_TYPE_DOCUMENT) {
            docFiles.remove(path);

            currentCount--;
        }

        if (pickerManagerListener != null) {
            pickerManagerListener.onItemSelected(currentCount);
        }
    }

    public boolean shouldAdd() {
        return currentCount < maxCount;
    }

    public ArrayList<String> getSelectedPhotos() {
        return mediaFiles;
    }

    public ArrayList<String> getSelectedFiles() {
        return docFiles;
    }

    public ArrayList<String> getSelectedFilePaths(ArrayList<BaseFile> files) {
        ArrayList<String> paths = new ArrayList<>();
        for (int index = 0; index < files.size(); index++) {
            paths.add(files.get(index).getPath());
        }
        return paths;
    }

    public void clearSelections() {
        docFiles.clear();
        mediaFiles.clear();
        fileTypes.clear();
        currentCount = 0;
        maxCount = 0;
        releaseList();
    }

    private void releaseList() {
        txtList.clear();
        videoList.clear();
        musicList.clear();
        zipList.clear();
        appList.clear();
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public boolean showVideo() {
        return showVideos;
    }

    public void setShowVideos(boolean showVideos) {
        this.showVideos = showVideos;
    }

    public boolean isShowFolderView() {
        return showFolderView;
    }

    public void setShowFolderView(boolean showFolderView) {
        this.showFolderView = showFolderView;
    }

    public void addFileType(FileType fileType) {
        fileTypes.add(fileType);
    }

    public void addDocTypes() {
        String[] pdfs = {"pdf"};
        fileTypes.add(new FileType(FilePickerConst.PDF, pdfs, R.drawable.hx_icon_folder_pdf));

        String[] docs = {"doc", "docx", "dot", "dotx"};
        fileTypes.add(new FileType(FilePickerConst.DOC, docs, R.drawable.hx_icon_folder_word));

        String[] ppts = {"ppt", "pptx"};
        fileTypes.add(new FileType(FilePickerConst.PPT, ppts, R.drawable.hx_icon_folder_ppt));

        String[] xlss = {"xls", "xlsx"};
        fileTypes.add(new FileType(FilePickerConst.XLS, xlss, R.drawable.hx_icon_folder_xls));

        String[] txts = {"txt"};
        fileTypes.add(new FileType(FilePickerConst.TXT, txts, R.drawable.hx_icon_folder_txt));

        String[] mp4s = {"mp4", "3pg"};
        fileTypes.add(new FileType(FilePickerConst.VIDEO, mp4s, R.drawable.hx_icon_folder_default));

        String[] mp3s = {"mp3"};
        fileTypes.add(new FileType(FilePickerConst.MUSIC, mp3s, R.drawable.hx_icon_folder_music));

        String[] apks = {"apk"};
        fileTypes.add(new FileType(FilePickerConst.APP, apks, R.drawable.hx_icon_folder_default));

        String[] zips = {"zip", "rar"};
        fileTypes.add(new FileType(FilePickerConst.ZIP, zips, R.drawable.hx_icon_folder_zip));

        //String[] pics = {"jpg", "png"};
        //fileTypes.add(new FileType(FilePickerConst.PICTURE, pics, R.drawable.hx_icon_folder_default));
    }

    public ArrayList<FileType> getFileTypes() {
        return fileTypes;
    }

    public boolean isDocSupport() {
        return docSupport;
    }

    public void setDocSupport(boolean docSupport) {
        this.docSupport = docSupport;
    }

    public boolean isEnableCamera() {
        return enableCamera;
    }

    public void setEnableCamera(boolean enableCamera) {
        this.enableCamera = enableCamera;
    }

    public boolean isEnableOrientation() {
        return enableOrientation;
    }

    public void setEnableOrientation(boolean enableOrientation) {
        this.enableOrientation = enableOrientation;
    }

    public String getProviderAuthorities() {
        return providerAuthorities;
    }

    public void setProviderAuthorities(String providerAuthorities) {
        this.providerAuthorities = providerAuthorities;
    }

    // add by yw
    private ArrayList<Document> txtList = new ArrayList<>();
    private ArrayList<Document> videoList = new ArrayList<>();
    private ArrayList<Document> musicList = new ArrayList<>();
    private ArrayList<Document> zipList = new ArrayList<>();
    private ArrayList<Document> appList = new ArrayList<>();
    private boolean isReverse = false;

    public ArrayList<Document> getTxtList() {
        return txtList;
    }

    public ArrayList<Document> getVideoList() {
        return videoList;
    }

    public ArrayList<Document> getMusicList() {
        return musicList;
    }

    public ArrayList<Document> getZipList() {
        return zipList;
    }

    public ArrayList<Document> getAppList() {
        return appList;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public void reverseList() {
        if (!isReverse()) {
            Collections.sort(txtList, new Document());
            Collections.sort(videoList, new Document());
            Collections.sort(musicList, new Document());
            Collections.sort(zipList, new Document());
            Collections.sort(appList, new Document());
        }
    }

    public ArrayList<Document> loadLocalWeiXinFile() {
        ArrayList<Document> listFiles = new ArrayList<>();
        File weFile = new File(Environment.getExternalStorageDirectory(), "/tencent/MicroMsg/Download");
        File weMedia = new File(Environment.getExternalStorageDirectory(), "/tencent/MicroMsg/WeiXin");
        if (weFile.exists()) {
            listFiles.addAll(Utils.refreshFileList(weFile));
        }
        if (weMedia.exists()) {
            listFiles.addAll(Utils.refreshFileList(weMedia));
        }
        Collections.sort(listFiles, new Document());
        return listFiles;
    }

    public ArrayList<Document> loadLocalQQFile() {
        ArrayList<Document> listFiles = new ArrayList<>();
        File qqFile = new File(Environment.getExternalStorageDirectory(), "/tencent/QQfile_recv");
        if (qqFile.exists()) {
            listFiles.addAll(Utils.refreshFileList(qqFile));
        }
        Collections.sort(listFiles, new Document());
        return listFiles;
    }

    public ArrayList<Document> loadDefaultDownloadFile() {
        ArrayList<Document> listFiles = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory(), "/Download");
        if (file.exists()) {
            listFiles.addAll(Utils.refreshFileList(file, new ArrayList<Document>()));
        }
        Collections.sort(listFiles, new Document());
        return listFiles;
    }

    public ArrayList<Document> loadDownloadFile(String path) {
        ArrayList<Document> listFiles = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (file.exists()) {
            listFiles.addAll(Utils.refreshFileList(file, new ArrayList<Document>()));
        }
        Collections.sort(listFiles, new Document());
        return listFiles;
    }

    private String dstUuid; // IM & 电话 对方号码
    private int requestCode; // 分类请求码

    public String getDstUuid() {
        return dstUuid;
    }

    public void setDstUuid(String dstUuid) {
        this.dstUuid = dstUuid;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

}
