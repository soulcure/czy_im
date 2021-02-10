package com.youmai.hxsdk.config;

import com.youmai.hxsdk.utils.AppUtils;

public class AppConfig {

    /**
     * 高德提供的静态地图的KEY
     */
    public static String staticMapKey = "76ca0c2476c8739d4c85473929c126ec";

    /**
     * 七牛 SecretKey
     */
    //public static String QiNiuSecretKey = "gcb2e34zD2velqZvz-IQG9Mw6VjetY__3wWcytd_";


    /**
     * pref文件名定义
     */
    public static final String SHARED_PREFERENCES = "sdk_app";

    public static final boolean QINIU_ENABLE = false;


    /**
     * HuXin 服务器连接配置
     */
    public final static int LAUNCH_MODE = 0; //0彩生活测试服务器        1彩生活预发布服务器           2彩生活正式服务器

    private final static String SOCKET_URL[] = new String[]{"http://core.im.test.colourlife.com/", "http://core.im.beta.colourlife.com/", "http://imcore.czy.colourlife.com/"};

    private final static String SOCKET_HOST[] = new String[]{"120.78.8.165", "39.108.74.138", "47.107.68.13"};

    private final static int SOCKET_PORT[] = new int[]{6681, 6681, 6681};

    private static String getSocketUrl() {
        return SOCKET_URL[LAUNCH_MODE];
    }

    public static String getSocketHost() {
        return SOCKET_HOST[LAUNCH_MODE];
    }

    public static int getSocketPort() {
        return SOCKET_PORT[LAUNCH_MODE];
    }


    /**
     * http post 文件前，获取文件token
     */
    public static final String GET_UPLOAD_FILE_TOKEN = "http://api.ihuxin.net/jhuxin-openapi/qy/fileUpload/upToken";

    private static final String DOWNLOAD_FILE_PATH = "http://file.ihuxin.net/";


    /***
     * sign 签名
     *
     * @param phoneNum
     * @param imei
     * @return
     */
    public static String appSign(String phoneNum, String imei) {
        String channelUrl = "msisdn=" + phoneNum + "&termid=" + imei;
        return AppUtils.md5("00000" + channelUrl + "999999");
    }


    /**
     * 获取图片下载地址
     *
     * @param fid
     * @return
     */
    public static String getImageUrl(String fid) {
        if (QINIU_ENABLE) {
            return DOWNLOAD_FILE_PATH + fid;
        } else {
            return ColorsConfig.loadUrl(fid);
        }
    }


    /**
     * tcp 负载均衡 host
     */
    public static String getTcpHost(String uuid) {
        return getSocketUrl() + "api/v1/sum?userid=" + uuid;
    }

}
