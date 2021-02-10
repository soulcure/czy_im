package com.youmai.hxsdk.module.filemanager.loader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.bean.Document;
import com.youmai.hxsdk.module.filemanager.bean.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.DATA;

/**
 * Created by yw on 2017.8.31.
 */
public class FileListLoader extends AsyncTaskLoader<List<Document>> {

    final String[] DOC_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Files.FileColumns.TITLE
    };

    private final String[] mFileTypes = new String[]{"txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "apk", "mp4", "mp3", "zip", "rar"};
    List<Document> mApps;
    PackageManager mPm;
    private Context mContext;

    public FileListLoader(Context context) {
        super(context);

        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
        mPm = getContext().getPackageManager();
        mContext = context.getApplicationContext();
    }

    /**
     * This is where the bulk of our work is done.  This function is called in a background thread and
     * should generate a new set of data to be published by the loader.
     */
    @Override
    public List<Document> loadInBackground() {

        ArrayList<Document> documents = new ArrayList<>();

        String[] projection = DOC_PROJECTION;
        String selection = getSelection(mFileTypes);
        String[] selectionArgs = getSelectionArgs(mFileTypes);

        //查询所有文件 效率不够
        /*String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;*/

        Cursor cursor = mContext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                null);

        if (cursor != null) {
            documents = getDocumentFromCursor(cursor);
            cursor.close();
        }

        return documents;
    }

    /**
     * Called when there is new data to deliver to the client.  The super class will take care of
     * delivering it; the implementation here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<Document> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }
        List<Document> oldApps = mApps;
        mApps = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }

    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mApps);
        }

        if (takeContentChanged() || mApps == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<Document> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mApps != null) {
            onReleaseResources(mApps);
            mApps = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated with an actively loaded data
     * set.
     */
    protected void onReleaseResources(List<Document> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

    private String getSelection(String[] types) {
        String res = "";
        String item = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        for (int i = 0; i < types.length; i++) {
            if (i == (types.length - 1)) {
                res += item;
            } else {
                res += (item + " or ");
            }

        }
        return res;
    }

    private String[] getSelectionArgs(String[] types) {
        ArrayList<String> list = new ArrayList<>();
        for (String item : types) {
            list.add(MimeTypeMap.getSingleton().getMimeTypeFromExtension(item));
        }

        String[] stockArr = new String[list.size()];
        stockArr = list.toArray(stockArr);

        return stockArr;
    }

    private ArrayList<Document> getDocumentFromCursor(Cursor data) {
        ArrayList<Document> documents = new ArrayList<>();
        try {
            while (data.moveToNext()) {

                int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
                String path = data.getString(data.getColumnIndexOrThrow(DATA));
                String title = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));
                long date = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));

                if (path != null) {
                    FileType fileType = getFileType(PickerManager.getInstance().getFileTypes(), path);
                    if (fileType != null && !(new File(path).isDirectory())) {

                        String size = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                        if (Integer.parseInt(size) <= 0) {
                            continue;
                        }
                        Document document = new Document(imageId, title, path);
                        document.setFileType(fileType);
                        document.setSize(size);

                        File file = new File(path);
                        document.setModifyDate(file.lastModified());

                        String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                        if (mimeType != null && !TextUtils.isEmpty(mimeType))
                            document.setMimeType(mimeType);
                        else {
                            document.setMimeType("");
                        }

                        if (mimeType.contains("text/plain") || mimeType.contains("application/msword")
                                || mimeType.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                || mimeType.contains("application/vnd.ms-excel")
                                || mimeType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                || mimeType.contains("application/vnd.ms-powerpoint")
                                || mimeType.contains("application/vnd.openxmlformats-officedocument.presentationml.presentation")
                                || mimeType.contains("application/pdf") ) {
                            PickerManager.getInstance().getTxtList().add(document);
                        } else if (mimeType.contains("video/mp4") || mimeType.contains("video/3gpp")) {
                            PickerManager.getInstance().getVideoList().add(document);
                        } else if (mimeType.contains("audio/mpeg") || mimeType.contains("audio/x-mpeg")) {
                            PickerManager.getInstance().getMusicList().add(document);
                        } else if (mimeType.contains("application/vnd.android.package-archive")) {
                            PickerManager.getInstance().getAppList().add(document);
                        } else if (mimeType.contains("application/zip") || mimeType.contains("application/x-zip-compressed")) {
                            PickerManager.getInstance().getZipList().add(document);
                        }

                        if (!documents.contains(document)) {
                            documents.add(document);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documents;
    }

    private FileType getFileType(ArrayList<FileType> types, String path) {
        for (int index = 0; index < types.size(); index++) {
            for (String string : types.get(index).extensions) {
                if (path.endsWith(string))
                    return types.get(index);
            }
        }
        return null;
    }

}
