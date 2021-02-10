package com.youmai.hxsdk.module.picker.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.youmai.hxsdk.module.picker.model.LocalImage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by colin on 2017/10/17.
 */

public class LocalVideoLoader extends AsyncTaskLoader<ArrayList<LocalImage>> {

    private final String[] LOCAL_VIDEOS = {
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.VideoColumns.TITLE
    };

    private Context mContext;

    public LocalVideoLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ArrayList<LocalImage> loadInBackground() {
        ArrayList<LocalImage> videos = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                LOCAL_VIDEOS,
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC");

        if (cursor != null) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String path = cursor.getString(1);

                File file = new File(path);
                if (file.exists()) {
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
                    if (uri == null) {
                        continue;
                    }
                    LocalImage localImage = new LocalImage();
                    localImage.setPath(path);
                    localImage.setOriginalUri(uri);
                    videos.add(localImage);
                }
            }
            cursor.close();
        }

        return videos;
    }


}
