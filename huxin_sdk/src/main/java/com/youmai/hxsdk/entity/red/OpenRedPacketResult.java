package com.youmai.hxsdk.entity.red;

public class OpenRedPacketResult {


    /**
     * code : 0
     * message : SUCC
     * content : {"uuid":"2c385089a2da4ab4a4440260e9c072b1","status":0,"isGrabbed":0,"lsType":1,"senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","blessing":"大吉大利，开开心心！","isSelfOwner":0,"canOpen":1}
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
         * uuid : 2c385089a2da4ab4a4440260e9c072b1
         * status : 0
         * isGrabbed : 0
         * lsType : 1
         * senderName : 陈琼瑶
         * senderMobile : 18664923439
         * senderHeadImgUrl : http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao
         * blessing : 大吉大利，开开心心！
         * isSelfOwner : 0
         * canOpen : 1
         */

        private String uuid;
        private int status;
        private int isGrabbed;
        private int lsType;
        private String senderName;
        private String senderMobile;
        private String senderHeadImgUrl;
        private String blessing;
        private int isSelfOwner;
        private int canOpen;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIsGrabbed() {
            return isGrabbed;
        }

        public void setIsGrabbed(int isGrabbed) {
            this.isGrabbed = isGrabbed;
        }

        public int getLsType() {
            return lsType;
        }

        public void setLsType(int lsType) {
            this.lsType = lsType;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getSenderMobile() {
            return senderMobile;
        }

        public void setSenderMobile(String senderMobile) {
            this.senderMobile = senderMobile;
        }

        public String getSenderHeadImgUrl() {
            return senderHeadImgUrl;
        }

        public void setSenderHeadImgUrl(String senderHeadImgUrl) {
            this.senderHeadImgUrl = senderHeadImgUrl;
        }

        public String getBlessing() {
            return blessing;
        }

        public void setBlessing(String blessing) {
            this.blessing = blessing;
        }

        public int getIsSelfOwner() {
            return isSelfOwner;
        }

        public void setIsSelfOwner(int isSelfOwner) {
            this.isSelfOwner = isSelfOwner;
        }

        public int getCanOpen() {
            return canOpen;
        }

        public void setCanOpen(int canOpen) {
            this.canOpen = canOpen;
        }
    }
}
