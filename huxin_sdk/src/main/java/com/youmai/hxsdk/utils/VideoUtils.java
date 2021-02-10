package com.youmai.hxsdk.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.VideoView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.config.FileConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2016.10.19 14:22
 * 描述：视频播放类
 */
public class VideoUtils {

    private Context mContext;

    private static volatile VideoUtils mVideoUtil;

    private VideoUtils(Context context) {
        this.mContext = context;
    }

    public static VideoUtils instance(Context context) {

        if (mVideoUtil == null) {
            mVideoUtil = new VideoUtils(context);
        }
        return mVideoUtil;
    }

    /**
     * @param urlFid    视频文件fid
     * @param videoView 播放视频控件view
     * @param isTip     tip: true：提示视频播放失败  false: 加载本地默视频播放
     */
    public void loadVideo(final String urlFid, final VideoView videoView, final boolean isTip) {

        final String filePath = AppConfig.getImageUrl(urlFid);
        final String absolutePath = FileConfig.getVideoDownLoadPath();
        String hasFile = AbFileUtil.hasFilePath(filePath /*+ ".mp4"*/, absolutePath);

        if (AbFileUtil.isEmptyString(hasFile)) {
            if (!CommonUtils.isNetworkAvailable(mContext)) {
                if (isTip) {
                    ToastUtil.showToast(mContext, "视频播放失败了");
                } else {
                    setDefaultShow(videoView);//播放默认视频秀
                }
            } else {
                final AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
                    String lastDownload = "";
                    long lastTime = 0;

                    @Override
                    protected String doInBackground(Object... params) {
                        long now = System.currentTimeMillis();
                        if (now - lastTime < 10 * 1000 && lastDownload.equals(filePath)) {
                            return null;
                        }
                        lastTime = now;
                        lastDownload = filePath;
                        return AbFileUtil.downloadFile(filePath /*+ ".mp4"*/, absolutePath);
                    }

                    @Override
                    protected void onPostExecute(String path) {
                        super.onPostExecute(path);

                        if (path != null) {
                            File file = new File(path);
                            if (file.exists()) {
                                loadLocalVideo(path, null, videoView);
                            } else {
                                if (isTip) {
                                    ToastUtil.showToast(mContext, "视频播放失败了");
                                } else {
                                    onLoadLocalVideo(videoView);
                                }
                            }
                        }
                    }
                };
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            this.loadLocalVideo(hasFile, null, videoView);
        }
    }

    /* 设置默认视频 */
    private void setDefaultShow(VideoView videoView) {
        onLoadLocalVideo(videoView);
    }

    /* 拿本地的默认视频 */
    private void onLoadLocalVideo(VideoView videoView) {
        String assetsVideo = getRawVideo(mContext, "sample_video_");
        if (assetsVideo != null) {
            loadLocalVideo(null, assetsVideo, videoView);
            return;
        }
    }

    /* filePath 本地文件路径 */
    public void loadLocalVideo(String filePath, String fileUrl, VideoView videoView) {
        Uri uri = fileUrl == null ? null : Uri.parse(fileUrl);
        setVideo(filePath, uri, videoView);
    }

    private void setVideo(final String filePath, final Uri fileUri, VideoView videoView) {

        if (fileUri != null) {
            videoView.setVideoURI(fileUri);
        } else {
            videoView.setVideoPath(filePath);//downloadFile  filePath
        }
        videoView.start();
        setListener(filePath, videoView);
    }

    private void setListener(final String filePath, VideoView videoView) {

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    mp.start();
                    mp.setVolume(0f, 0f);
                    mp.setLooping(true);
                } catch (IllegalStateException e) {
                    LogUtils.e(Constant.SDK_UI_TAG, "Exception...");
                }
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (filePath != null) {
                    File file = new File(filePath);
                    file.deleteOnExit();
                }
                return true;
            }
        });
    }

    private String getRawVideo(Context context, String pt) {
        Class<?> clsR = null;
        String className = context.getPackageName() + ".R";
        try {
            Class<?> cls = Class.forName(className);
            for (Class<?> childClass : cls.getClasses()) {
                String simple = childClass.getSimpleName();
                if (simple.equals("raw")) {
                    clsR = childClass;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (clsR == null)
            return null;

        Field[] fields = clsR.getDeclaredFields();
        int rawId;
        String rawName;
        ArrayList<Integer> videoList = new ArrayList<Integer>();
        for (int i = 0; i < fields.length; i++) {
            try {
                rawId = fields[i].getInt(clsR);
                rawName = fields[i].getName();
                if (rawName.startsWith(pt)) {
                    videoList.add(rawId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (videoList.size() > 0) {
            int rand = (int) (Math.random() * videoList.size());
            rawId = videoList.get(rand);
            return "android.resource://" + context.getPackageName() + "/" + rawId;
        }
        return null;
    }

    //获取视频时间
    public long getVideoTime(String path) {
        long time = 0L;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            time = mediaPlayer.getDuration();//毫秒
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaPlayer.release();
        }
        return time;
    }
}
