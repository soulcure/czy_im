/*
 * Copyright (c) 2016.  [597415099@qq.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youmai.hxsdk.module.movierecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import com.youmai.hxsdk.utils.FileUtils;
import com.youmai.hxsdk.utils.LogUtils;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

public final class MediaStoreUtils {
    private MediaStoreUtils() {
    }

    public static Intent getPickImageIntent(final Context context) {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        return Intent.createChooser(intent, "Select picture");
    }

    /**
     * 获取视频第一帧
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmapFromVideo(String path) {
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
            Bitmap bitmap = media.getFrameAtTime();
            media.release();
            return bitmap;
        } catch (Exception e) {
            LogUtils.e("", e.toString());
        }
        return null;
    }

    /**
     * 获取视频长度.
     *
     * @param path
     * @return
     */
    public static String[] getVideoParams(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String[] arrays = new String[3];
        try {
//            if (mUri != null) {
//                HashMap<String, String> headers = mHeaders;
//                if (headers == null) {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
//                mmr.setDataSource(mUri, headers);

//            } else {
//                mmr.setDataSource(mFD, mOffset, mLength);
//            }
            mmr.setDataSource(path);

            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

            arrays[0] = duration;
            arrays[1] = width;
            arrays[2] = height;
        } catch (Exception ex) {
            LogUtils.e("xx", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return arrays;
    }

    //将长度转换为时间
    public static String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    /**
     * 插入视频到相册
     * @param activity
     * @param file
     */
    public static void insertToGallery(Activity activity, File file) {
        File targetFile = file;
        File rootDir = Environment.getExternalStorageDirectory();
        File dir = new File(rootDir,  Environment.DIRECTORY_DCIM + File.separator + "Camera");
        File newFile = new File(dir, file.getName());
        try {
            FileUtils.copyFile(file.getAbsolutePath(), newFile.getAbsolutePath());
            targetFile = newFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(
                activity, new String[] { targetFile.getAbsolutePath() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        LogUtils.e("xx", "Finished scanning " + path + " New row: " + uri);
                    }
                } );

    }

}
