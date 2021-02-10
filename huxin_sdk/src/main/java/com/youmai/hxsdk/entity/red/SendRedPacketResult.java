package com.youmai.hxsdk.entity.red;

public class SendRedPacketResult {


    /**
     * code : 0
     * message : SUCC
     * content : {"lishiUuid":"dbae6006645348739ac7d84ed437f434"}
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
         * lishiUuid : dbae6006645348739ac7d84ed437f434
         */

        private String lishiUuid;

        public String getLishiUuid() {
            return lishiUuid;
        }

        public void setLishiUuid(String lishiUuid) {
            this.lishiUuid = lishiUuid;
        }
    }
}
