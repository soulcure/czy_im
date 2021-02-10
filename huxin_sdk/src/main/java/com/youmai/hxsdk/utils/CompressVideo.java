package com.youmai.hxsdk.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.youmai.smallvideorecord.JianXiCamera;
import com.youmai.smallvideorecord.LocalMediaCompress;
import com.youmai.smallvideorecord.model.AutoVBRMode;
import com.youmai.smallvideorecord.model.BaseMediaBitrateConfig;
import com.youmai.smallvideorecord.model.LocalMediaConfig;
import com.youmai.smallvideorecord.model.OnlyCompressOverBean;

import java.io.File;

/**
 * from Whatsapp like Image Compression in Android with demo
 * Created by soulcure on 2017/5/31.
 */

public class CompressVideo {
    private static final String TAG = CompressVideo.class.getSimpleName();

    public static OnlyCompressOverBean compressVideo(String moviePath) {

        initSmallVideo();

        BaseMediaBitrateConfig compressMode = new AutoVBRMode();
        String sRate = ""; //视频帧率（默认为原视频）
        String scale = "2.5"; //缩放视频比例，为浮点型，大于1有效
        int iRate = 0;
        float fScale = 0;
        if (!TextUtils.isEmpty(sRate)) {
            iRate = Integer.valueOf(sRate);
        }
        if (!TextUtils.isEmpty(scale)) {
            fScale = Float.valueOf(scale);
        }
        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
        final LocalMediaConfig config = buidler
                .setVideoPath(moviePath)
                .captureThumbnailsTime(1)
                .doH264Compress(compressMode)
                .setFramerate(iRate)
                .setScale(fScale)
                .build();
        return new LocalMediaCompress(config).startCompress(true);
    }


    public static void initSmallVideo() {
        // 设置拍摄视频缓存路径
        File dcim = Environment.getExternalStorageDirectory();
        if (com.youmai.smallvideorecord.utils.DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim + "/HuXin/zero/");
            } else {
                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/") + "/HuXin/zero/");
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim + "/HuXin/zero/");
        }
        // 初始化拍摄
        JianXiCamera.initialize(false, null);
    }

}
