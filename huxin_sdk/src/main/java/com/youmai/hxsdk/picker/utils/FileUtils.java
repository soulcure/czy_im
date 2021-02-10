package com.youmai.hxsdk.picker.utils;

import android.text.TextUtils;

import java.io.File;

import com.youmai.hxsdk.picker.FilePickerConst;
import com.youmai.hxsdk.R;

/**
 * Created by droidNinja on 08/03/17.
 */

public class FileUtils {

    public static int getTypeDrawable(String path) {
        if (getFileType(path) == FilePickerConst.FILE_TYPE.TXT)
            return R.drawable.hx_icon_folder_txt;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.WORD)
            return R.drawable.hx_icon_folder_word;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.EXCEL)
            return R.drawable.hx_icon_folder_xls;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.PPT)
            return R.drawable.hx_icon_folder_ppt;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.PDF)
            return R.drawable.hx_icon_folder_pdf;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.VIDEO)
            return R.drawable.hx_icon_folder_default;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.AUDIO)
            return R.drawable.hx_icon_folder_music;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.ZIP)
            return R.drawable.hx_icon_folder_zip;
        if (getFileType(path) == FilePickerConst.FILE_TYPE.APK)
            return R.drawable.img_apk;
        else
            return R.drawable.hx_icon_folder_default;
    }

    public static FilePickerConst.FILE_TYPE getFileType(String path) {
        String fileExtension = Utils.getFileExtension(new File(path));
        if (TextUtils.isEmpty(fileExtension))
            return FilePickerConst.FILE_TYPE.UNKNOWN;

        if (isExcelFile(path))
            return FilePickerConst.FILE_TYPE.EXCEL;
        if (isDocFile(path))
            return FilePickerConst.FILE_TYPE.WORD;
        if (isPPTFile(path))
            return FilePickerConst.FILE_TYPE.PPT;
        if (isPDFFile(path))
            return FilePickerConst.FILE_TYPE.PDF;
        if (isTxtFile(path))
            return FilePickerConst.FILE_TYPE.TXT;
        if (isVideoFile(path))
            return FilePickerConst.FILE_TYPE.VIDEO;
        if (isAudioFile(path))
            return FilePickerConst.FILE_TYPE.AUDIO;
        if (isZipFile(path))
            return FilePickerConst.FILE_TYPE.ZIP;
        if (isApkFile(path))
            return FilePickerConst.FILE_TYPE.APK;
        else
            return FilePickerConst.FILE_TYPE.UNKNOWN;
    }

    public static boolean isExcelFile(String path) {
        String[] types = {"xls", "xlsx"};
        return Utils.contains(types, path);
    }

    public static boolean isDocFile(String path) {
        String[] types = {"doc", "docx", "dot", "dotx"};
        return Utils.contains(types, path);
    }

    public static boolean isPPTFile(String path) {
        String[] types = {"ppt", "pptx"};
        return Utils.contains(types, path);
    }

    public static boolean isPDFFile(String path) {
        String[] types = {"pdf"};
        return Utils.contains(types, path);
    }

    public static boolean isTxtFile(String path) {
        String[] types = {"txt"};
        return Utils.contains(types, path);
    }

    public static boolean isVideoFile(String path) {
        String[] types = {"mp4", "rmvb", "avi", "3gp"};
        return Utils.contains(types, path);
    }

    public static boolean isAudioFile(String path) {
        String[] types = {"mp3", "wav", "amr"};
        return Utils.contains(types, path);
    }

    public static boolean isZipFile(String path) {
        String[] types = {"zip", "rar"};
        return Utils.contains(types, path);
    }

    public static boolean isApkFile(String path) {
        String[] types = {"apk"};
        return Utils.contains(types, path);
    }

}
