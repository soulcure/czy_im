package com.youmai.hxsdk.module.filemanager.constant;

/**
 * Created by yw on 28/07/16.
 */
public class FilePickerConst {

    public static final int REQUEST_CODE_PHOTO = 233;
    public static final int REQUEST_CODE_DOC = 234;

    public static final int REQUEST_CODE_MEDIA_DETAIL = 235;

    public final static int DEFAULT_MAX_COUNT = 9;
    public final static int DEFAULT_COLUMN_NUMBER = 3;

    public final static int MEDIA_PICKER = 0x11;
    public final static int DOC_PICKER = 0x12;

    public final static String KEY_SELECTED_MEDIA = "SELECTED_PHOTOS";
    public final static String KEY_SELECTED_DOCS = "SELECTED_DOCS";

    public final static String EXTRA_PICKER_TYPE = "EXTRA_PICKER_TYPE";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String EXTRA_FILE_TYPE = "EXTRA_FILE_TYPE";
    public final static String EXTRA_BUCKET_ID = "EXTRA_BUCKET_ID";
    public final static String ALL_PHOTOS_BUCKET_ID = "ALL_PHOTOS_BUCKET_ID";
    public final static String PPT_MIME_TYPE = "application/mspowerpoint";
    public final static String KEY_IS_ORIGINAL = "is_original";

    public final static int FILE_TYPE_MEDIA = 1;
    public final static int FILE_TYPE_DOCUMENT = 2;

    public final static int MEDIA_TYPE_IMAGE = 1;
    public final static int MEDIA_TYPE_VIDEO = 3;
    public final static int MEDIA_TYPE_IMAGE_VIDEO = 101;

    // add by yw
    public final static int IM_REQUEST_CALLBACK = 0x1001;
    public final static int CALL_REQUEST_CALLBACK = 0x1002;

    public final static String PDF = "PDF";
    public final static String PPT = "PPT";
    public final static String DOC = "DOC";
    public final static String XLS = "XLS";
    public final static String TXT = "TXT";

    public final static String VIDEO = "video";
    public final static String MUSIC = "music";
    public final static String PICTURE = "picture";
    public final static String APP = "app";
    public final static String ZIP = "zip";

    public enum FILE_TYPE {
        PDF,
        WORD,
        EXCEL,
        PPT,
        TXT,
        VIDEO,
        AUDIO,
        ZIP,
        APK,
        UNKNOWN
    }

}
