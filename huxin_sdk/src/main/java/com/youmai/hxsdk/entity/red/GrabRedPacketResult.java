package com.youmai.hxsdk.entity.red;

public class GrabRedPacketResult {


    /**
     * code : 0
     * message : SUCC
     * content : {"uuid":"5c9f35080f054518af375115e3be2615","moneyDraw":1.87}
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
         * uuid : 5c9f35080f054518af375115e3be2615
         * moneyDraw : 1.87
         */

        private String uuid;
        private double moneyDraw;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public double getMoneyDraw() {
            return moneyDraw;
        }

        public void setMoneyDraw(double moneyDraw) {
            this.moneyDraw = moneyDraw;
        }
    }
}
