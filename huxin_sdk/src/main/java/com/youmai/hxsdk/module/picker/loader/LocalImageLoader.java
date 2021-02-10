package com.youmai.hxsdk.module.picker.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.youmai.hxsdk.module.picker.PhotoPreviewActivity;
import com.youmai.hxsdk.module.picker.model.LocalImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by colin on 2017/10/17.
 */

public class LocalImageLoader extends AsyncTaskLoader<HashMap<String, ArrayList<LocalImage>>> {

    private final String[] LOCAL_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION

    };

    private Context mContext;

    public LocalImageLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public HashMap<String, ArrayList<LocalImage>> loadInBackground() {
        HashMap<String, ArrayList<LocalImage>> albumMap = new HashMap<>();
        ArrayList<LocalImage> images = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                LOCAL_IMAGES,
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC");
        if (cursor == null) {
            return albumMap;
        }
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String path = cursor.getString(1);

            File file = new File(path);
            if (file.exists()) {
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
                if (uri == null) {
                    continue;
                }
                String folder = file.getParentFile().getName();

                LocalImage localImage = new LocalImage();
                localImage.setPath(path);
                localImage.setOriginalUri(uri);
                images.add(localImage);
                //判断文件夹是否已经存在
                if (albumMap.containsKey(folder)) {
                    albumMap.get(folder).add(localImage);
                } else {
                    ArrayList<LocalImage> files = new ArrayList<>();
                    files.add(localImage);
                    albumMap.put(folder, files);
                }
            }
        }
        albumMap.put(PhotoPreviewActivity.ALL_IMAGE_KEY, images);
        cursor.close();
        return albumMap;
    }
}
