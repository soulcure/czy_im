package com.youmai.hxsdk.picker.cursors;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.youmai.hxsdk.picker.PickerManager;
import com.youmai.hxsdk.picker.cursors.loadercallbacks.FileResultCallback;
import com.youmai.hxsdk.picker.models.Document;
import com.youmai.hxsdk.picker.models.FileType;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.DATA;

/**
 * Created by droidNinja on 01/08/16.
 */
public class DocScannerTask extends AsyncTask<Void, Void, List<Document>> {

    final String[] DOC_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Files.FileColumns.TITLE
    };
    private final FileResultCallback<Document> resultCallback;
    private static ArrayList<Document> documents = new ArrayList<>();

    private final Context context;
    private final String[] mFileTypes = new String[]{"txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt",
            "pptx", "apk", "mp4", "mp3", "zip", "rar"};


    public DocScannerTask(Context context, FileResultCallback<Document> fileResultCallback) {
        this.context = context;
        this.resultCallback = fileResultCallback;
    }

    @Override
    protected List<Document> doInBackground(Void... voids) {

        if (documents != null && documents.size() > 0) {
            return documents;
        }

        final String[] projection = DOC_PROJECTION;

        String selection = getSelection(mFileTypes);
        String[] selectionArgs = getSelectionArgs(mFileTypes);

        //查询所有文件 效率不够
        /*String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;*/

        final Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
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


    @Override
    protected void onPostExecute(List<Document> documents) {
        super.onPostExecute(documents);
        if (resultCallback != null) {
            resultCallback.onResultCallback(documents);
        }
    }

    private ArrayList<Document> getDocumentFromCursor(Cursor data) {
        ArrayList<Document> documents = new ArrayList<>();
        while (data.moveToNext()) {

            int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String path = data.getString(data.getColumnIndexOrThrow(DATA));
            String title = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));

            if (path != null) {

                FileType fileType = getFileType(PickerManager.getInstance().getFileTypes(), path);
                if (fileType != null && !(new File(path).isDirectory())) {

                    Document document = new Document(imageId, title, path);
                    document.setFileType(fileType);

                    String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                    if (mimeType != null && !TextUtils.isEmpty(mimeType))
                        document.setMimeType(mimeType);
                    else {
                        document.setMimeType("");
                    }

                    document.setSize(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));

                    if (!documents.contains(document))
                        documents.add(document);
                }
            }
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
