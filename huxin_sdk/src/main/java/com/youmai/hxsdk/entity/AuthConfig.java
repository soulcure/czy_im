package com.youmai.hxsdk.entity;

public class AuthConfig {

    /**
     * code : 0
     * message :
     * content : {"expireTime":1530345966546,"accessToken":"cb61cb108cd6422dbc118e290cbb3007","corpUuid":"a8c58297436f433787725a94f780a3c9","appUuid":"ICECZYIM-XE17-EZE5-TGLX-59FCF8D4PW6K","serviceUuid":""}
     */

    private int code;
    private String message;
    private ContentBean content;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * expireTime : 1530345966546
         * accessToken : cb61cb108cd6422dbc118e290cbb3007
         * corpUuid : a8c58297436f433787725a94f780a3c9
         * appUuid : ICECZYIM-XE17-EZE5-TGLX-59FCF8D4PW6K
         * serviceUuid :
         */

        private long expireTime;
        private String accessToken;
        private String corpUuid;
        private String appUuid;
        private String serviceUuid;

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getCorpUuid() {
            return corpUuid;
        }

        public void setCorpUuid(String corpUuid) {
            this.corpUuid = corpUuid;
        }

        public String getAppUuid() {
            return appUuid;
        }

        public void setAppUuid(String appUuid) {
            this.appUuid = appUuid;
        }

        public String getServiceUuid() {
            return serviceUuid;
        }

        public void setServiceUuid(String serviceUuid) {
            this.serviceUuid = serviceUuid;
        }
    }
}
