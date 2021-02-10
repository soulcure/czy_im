package com.youmai.hxsdk.entity.red;

public class RedPacketHistoryDetail {

    /**
     * code : 0
     * message : SUCC
     * content : {"numberTotal":"6","moneyTotal":12.97,"nickname":"陈琼瑶","mobile":"18664923439","headImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao"}
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
         * numberTotal : 6
         * moneyTotal : 12.97
         * nickname : 陈琼瑶
         * mobile : 18664923439
         * headImgUrl : http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao
         */

        private String numberTotal;
        private double moneyTotal;
        private String nickname;
        private String mobile;
        private String headImgUrl;

        public String getNumberTotal() {
            return numberTotal;
        }

        public void setNumberTotal(String numberTotal) {
            this.numberTotal = numberTotal;
        }

        public double getMoneyTotal() {
            return moneyTotal;
        }

        public void setMoneyTotal(double moneyTotal) {
            this.moneyTotal = moneyTotal;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }
    }
}
