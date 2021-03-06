package com.youmai.hxsdk.picker.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.youmai.hxsdk.picker.FilePickerConst;
import com.youmai.hxsdk.picker.cursors.DocScannerTask;
import com.youmai.hxsdk.picker.cursors.loadercallbacks.FileResultCallback;
import com.youmai.hxsdk.picker.cursors.loadercallbacks.PhotoDirLoaderCallbacks;
import com.youmai.hxsdk.picker.models.Document;
import com.youmai.hxsdk.picker.models.PhotoDirectory;

public class MediaStoreHelper {

    public static void getPhotoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
        if (activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_IMAGE) != null)
            activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
        else
            activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    }

    public static void getVideoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
        if (activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_VIDEO) != null)
            activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
        else
            activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    }

    public static void getDocs(FragmentActivity activity, FileResultCallback<Document> fileResultCallback) {
        new DocScannerTask(activity, fileResultCallback).execute();
    }
}