package com.youmai.hxsdk.config;


import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.entity.AuthConfig;
import com.youmai.hxsdk.entity.UploadResult;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.service.sendmsg.PostFile;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorsConfig {

    public static final String GROUP_DEFAULT_NAME = "GroupName:GOURP@#$%^&*()"; //默认群组名的前缀
    public static final String GROUP_EMPTY_MSG = "    "; //群组默认填充消息

    public static final String ColorLifeAppId = "czy_im";  //彩生活服务集团
    //public static final String ColorLifeAppName = "彩生活服务集团";  //彩生活服务集团
    //public static final String HEAD_ICON_URL = "http://avatar.ice.colourlife.com/";//头像


    private static final String SECRET[] = new String[]{"OkX4AbbuFPKGcMDA1Jzk", "DwZ7qKKHUCD3DMpEKmgx", "DwZ7qKKHUCD3DMpEKmgx"};

    /**
     * 租户ID
     */
    private static final String CORP_UUID = "a8c58297436f433787725a94f780a3c9";

    /**
     * 文件微服务上传地址
     */
    private static final String ICE_UPLOAD[] = new String[]{"https://micro-file.colourlife.com/v1/pcUploadFile", "https://micro-file.colourlife.com/v1/pcUploadFile", "https://micro-file.colourlife.com/v1/pcUploadFile"};

    /**
     * 文件微服务下载地址
     */
    private static final String ICE_DOWNLOAD[] = new String[]{"http://114.119.7.98:3020/v1/down/", "http://120.25.148.153:30020/v1/down/", "http://120.25.148.153:30020/v1/down/"};


    //彩之云 APPID TOEKN 定义
    private static final String COLOR_APPID[] = new String[]{"ICECZYIM-XE17-EZE5-TGLX-59FCF8D4PW6K", "ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245", "ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245"};
    private static final String COLOR_TOKEN[] = new String[]{"exKSzQyuWGoctaTZTblK", "AXPHrD48LRa8xYVkgV4c", "AXPHrD48LRa8xYVkgV4c"};

    private static final String YOUMAI_APPID[] = new String[]{"ICECZYLS-UQIG-Z86H-WOZX-1GJNTB5KWRDU", "ICECZYLS-UKDR-YSUY-KVES-97YU5RCAUOUM", "ICECZYLS-UKDR-YSUY-KVES-97YU5RCAUOUM"};
    private static final String YOUMAI_TOKEN[] = new String[]{"ox3PM1mZeZDlpWHze8Jx", "xOicZXzIltfIRgyTKqTv", "xOicZXzIltfIRgyTKqTv"};

    private final static String SOCKET_URL[] = new String[]{"https://openapi-test.colourlife.com/v1/", "https://openapi.colourlife.com/v1/", "https://openapi.colourlife.com/v1/"};


    private static String getIceUpload() {
        return ICE_UPLOAD[AppConfig.LAUNCH_MODE];
    }

    private static String getIceDownload() {
        return ICE_DOWNLOAD[AppConfig.LAUNCH_MODE];
    }

    private static String getIceHost() {
        return SOCKET_URL[AppConfig.LAUNCH_MODE];
    }


    public static final String LISHI_SHARECONFIG = getIceHost() + "clsfwopenapi/lishi/config/shareConfig";
    public static final String LISHI_STANDARDCONFIG = getIceHost() + "clsfwopenapi/lishi/config/standardConfig";
    public static final String LISHI_LIST = getIceHost() + "clsfwopenapi/lishi/cqb/fp/list";
    public static final String LISHI_SEND = getIceHost() + "clsfwopenapi/lishi/send";
    public static final String LISHI_OPEN = getIceHost() + "clsfwopenapi/lishi/open";
    public static final String LISHI_GRAB = getIceHost() + "clsfwopenapi/lishi/grab";
    public static final String LISHI_DETAIL = getIceHost() + "clsfwopenapi/lishi/detail";
    public static final String LISHI_SEND_DETAIL = getIceHost() + "clsfwopenapi/lishi/history/send/profile";
    public static final String LISHI_RECEIVE_DETAIL = getIceHost() + "clsfwopenapi/lishi/history/receive/profile";
    public static final String LISHI_SEND_LIST = getIceHost() + "clsfwopenapi/lishi/history/send/list";
    public static final String LISHI_RECEIVE_LIST = getIceHost() + "clsfwopenapi/lishi/history/receive/list";


    /**
     * 彩之云 E家访
     */
    public static final String ICE_EVISIT = getIceHost() + "evisit/api/comment/commit";

    /**
     * 彩之云 E家访评价历史
     */
    public static final String ICE_EVISIT_LIST = getIceHost() + "evisit/api/past/evaluate";

    /**
     * 彩之云 单条推送
     */
    public static final String ICE_SERVICE_PUSH = getIceHost() + "mallShop/app/home/JPushMsg";

    public static final String ICE_SERVICE_AUTH = getIceHost() + "jqfw/app/auth";


    private static final String ICE_AUTH = getIceHost() + "authms/auth/app";

    /**
     * 彩管家验证支付密码host
     */
    public static final String CP_MOBILE_HOST = "http://cpmobile.colourlife.com";

    /**
     * 彩管家验证支付密码namespace
     */
    public static final String CHECK_PAYPWD = "/1.0/caiRedPaket/checkPayPwd";


    private static String getToken() {
        return COLOR_TOKEN[AppConfig.LAUNCH_MODE];
    }

    private static String getAppID() {
        return COLOR_APPID[AppConfig.LAUNCH_MODE];
    }


    private static String getYouMaiToken() {
        return YOUMAI_TOKEN[AppConfig.LAUNCH_MODE];
    }


    public static String getYouMaiAppID() {
        return YOUMAI_APPID[AppConfig.LAUNCH_MODE];
    }


    /**
     * 红包 secret
     */
    public static String getSecret() {
        return SECRET[AppConfig.LAUNCH_MODE];
    }


    /**
     * 彩管家 sign 通用签名
     *
     * @param ts
     */
    private static String sign(long ts) {
        return AppUtils.md5(getAppID() + ts + getToken() + false);
    }


    /**
     * 有麦 sign 通用签名
     *
     * @param ts
     */
    private static String signYouMai(long ts) {
        return AppUtils.md5(getYouMaiAppID() + ts + getYouMaiToken() + false);
    }


    /**
     * 红包接口签名方法
     *
     * @param params
     * @return
     */
    public static String cpMobileSign(@NonNull ContentValues params, String nameSpace) {
        List<String> list = new ArrayList<>();
        try {
            for (Map.Entry<String, Object> entry : params.valueSet()) {
                String key = entry.getKey(); // name
                String value = entry.getValue().toString(); // value
                list.add(key + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(nameSpace).append("?");

        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            sb.append(str);
            if (i < list.size() - 1) {
                sb.append("&");
            }
        }
        String sign = sb.toString();

        params.put("sign", AppUtils.md5(sign));


        return AppUtils.md5(sb.toString()).toUpperCase();

    }


    /**
     * 有麦 appid 通用签名
     *
     * @param params
     */
    public static void commonYouMaiParams(ContentValues params) {
        long ts = System.currentTimeMillis() / 1000;
        params.put("ts", ts);
        params.put("appID", getYouMaiAppID());
        params.put("sign", signYouMai(ts));
    }


    /**
     * 彩管家 appid 通用签名
     *
     * @param params
     */
    public static void commonParams(ContentValues params, long ts) {
        params.put("ts", ts);
        params.put("appID", getAppID());
        params.put("sign", sign(ts));
    }

    /**
     * 彩管家 appid 通用签名
     *
     * @param params
     */
    public static void commonParams(ContentValues params) {
        long ts = System.currentTimeMillis() / 1000;
        params.put("ts", ts);
        params.put("appID", getAppID());
        params.put("sign", sign(ts));
    }

    /**
     * 彩管家 appid 通用签名
     *
     * @param params
     */
    public static void commonParams(Map<String, Object> params) {
        long ts = System.currentTimeMillis() / 1000;
        params.put("ts", ts);
        params.put("appID", getAppID());
        params.put("sign", sign(ts));
    }


    public static String loadUrl(String fileId) {
        String url = getIceDownload();
        String appId = "colourlife";
        String fileToken = "LOCKW3v23#2";
        long ts = System.currentTimeMillis();

        String sign = AppUtils.md5(fileId + appId + ts + fileToken + false);

        StringBuilder sb = new StringBuilder();
        sb.append(url).append(fileId).append("?");
        sb.append("fileid").append("=").append(fileId).append("&");
        sb.append("ts").append("=").append(ts).append("&");
        sb.append("sign").append("=").append(sign);

        return sb.toString();
    }


    public static void postFileToICE(final File file, final String desPhone, final PostFile postFile) {
        String accessToken = HuxinSdkManager.instance().getAccessToken();
        long expireTime = HuxinSdkManager.instance().getExpireTime();
        long time = System.currentTimeMillis();

        boolean isAuth = false;

        if (expireTime == 0 || TextUtils.isEmpty(accessToken)) {
            isAuth = true;
        } else {
            if (expireTime <= time) {//token过期
                isAuth = true;
            }
        }

        if (isAuth) {
            reqAuth(new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    AuthConfig bean = GsonUtil.parse(response, AuthConfig.class);
                    if (bean != null && bean.isSuccess()) {

                        String token = bean.getContent().getAccessToken();
                        long time = bean.getContent().getExpireTime();

                        HuxinSdkManager.instance().setAccessToken(token);
                        HuxinSdkManager.instance().setExpireTime(time);

                        upLoadFile(file, token, desPhone, postFile);
                    }
                }
            });
        } else {
            upLoadFile(file, accessToken, desPhone, postFile);
        }
    }


    public static void reqAuth(IPostListener listener) {
        String url = ICE_AUTH;

        String appKey = getAppID();
        String token = getToken();
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();

        params.put("corp_uuid", CORP_UUID);
        params.put("app_uuid", appKey);
        params.put("signature", AppUtils.md5(appKey + ts + token));

        params.put("timestamp", ts);

        ColorsConfig.commonParams(params, ts);
        OkHttpConnector.httpPost(url, params, listener);
    }

    private static void upLoadFile(File file, String accessToken, final String desPhone, final PostFile postFile) {
        String url = getIceUpload();

        String fileUploadAccount = HuxinSdkManager.instance().getPhoneNum();
        String fileUploadAppName = "彩之云";

        if (file.exists()) {
            Map<String, Object> params = new HashMap<>();
            params.put("auth_ver", "2.0");
            params.put("access_token", accessToken);
            params.put("fileLength", file.length());
            params.put("fileName", file.getName());
            params.put("fileUploadAccount", fileUploadAccount);
            params.put("fileUploadAppName", fileUploadAppName);
            params.put("file", file);

            ColorsConfig.commonParams(params);

            OkHttpConnector.httpPostMultipart(url, params, new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    UploadResult result = GsonUtil.parse(response, UploadResult.class);
                    if (result != null && result.isSuceess()) {
                        String fileId = result.getContent();
                        if (postFile != null) {
                            postFile.success(fileId, desPhone);
                        }
                    } else {
                        postFile.fail("上传文件服务器出错!!!");
                    }
                }
            });

        }
    }


    public static void reqAccessToken(final AccessToken callback) {
        String accessToken = HuxinSdkManager.instance().getAccessToken();
        long expireTime = HuxinSdkManager.instance().getExpireTime();
        long time = System.currentTimeMillis();

        boolean isAuth = false;

        if (expireTime == 0 || TextUtils.isEmpty(accessToken)) {
            isAuth = true;
        } else {
            if (expireTime <= time) {//token过期
                isAuth = true;
            }
        }

        if (isAuth) {
            reqAuth(new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    AuthConfig bean = GsonUtil.parse(response, AuthConfig.class);
                    if (bean != null && bean.isSuccess()) {

                        String token = bean.getContent().getAccessToken();
                        long time = bean.getContent().getExpireTime();

                        HuxinSdkManager.instance().setAccessToken(token);
                        HuxinSdkManager.instance().setExpireTime(time);

                        callback.OnSuccess(token);

                    }
                }
            });
        } else {
            callback.OnSuccess(accessToken);
        }
    }


    public static void reqPushAuth(IPostListener listener) {
        String url = ColorsConfig.ICE_SERVICE_AUTH;

        String appKey = getAppID();
        String token = getToken();
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();

        params.put("appKey", appKey);
        params.put("signature", AppUtils.md5(appKey + ts + token));

        params.put("timestamp", ts);

        ColorsConfig.commonParams(params, ts);
        OkHttpConnector.httpPost(url, params, listener);
    }

    public interface AccessToken {
        void OnSuccess(String token);
    }
}
