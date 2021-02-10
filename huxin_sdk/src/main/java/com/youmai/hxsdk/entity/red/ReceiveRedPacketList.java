package com.youmai.hxsdk.entity.red;

import java.util.List;

public class ReceiveRedPacketList {


    /**
     * code : 0
     * message : SUCC
     * content : [{"senderNickname":"敖国跃","senderMobile":"","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=aoguoyue","receiveTime":"2018-06-22 18:45:10","receiveMoney":0.01,"isBest":1,"lsType":1,"lishiUuid":"e45ba29dfe6c4810adedea0eca177309"},{"senderNickname":"李深","senderMobile":"178****2373","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=lishen01","receiveTime":"2018-06-22 18:44:25","receiveMoney":1,"isBest":1,"lsType":1,"lishiUuid":"b3bc093a193b43b1ab32a4a193bfabcd"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 18:20:28","receiveMoney":1,"isBest":1,"lsType":1,"lishiUuid":"99985d3ff194491ca56c0620bf9eb770"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 18:14:21","receiveMoney":1.47,"isBest":1,"lsType":2,"lishiUuid":"866788525ebe4e719b38ed1350693cd8"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 18:05:16","receiveMoney":0.87,"isBest":0,"lsType":2,"lishiUuid":"86d66740fca540ce81b27d19e003d016"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 17:46:38","receiveMoney":1.52,"isBest":1,"lsType":2,"lishiUuid":"7fcccdaf28714b76a80abbeb58e047d1"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 17:30:09","receiveMoney":0.84,"isBest":0,"lsType":2,"lishiUuid":"66ea97ba1aca44d79db0a6a3c862cb98"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 17:20:30","receiveMoney":0.87,"isBest":0,"lsType":2,"lishiUuid":"8c4f3cbebf3b4cafa5238b6fe1c59500"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 17:15:25","receiveMoney":0.04,"isBest":0,"lsType":2,"lishiUuid":"773055b409874ad6a07e700727dbb20f"},{"senderNickname":"刘洪浩","senderMobile":"133****6774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","receiveTime":"2018-06-22 17:14:47","receiveMoney":0.6,"isBest":0,"lsType":2,"lishiUuid":"e8d49b6643f648c09c6a6c209c0d33d4"}]
     */

    private int code;
    private String message;
    private List<ContentBean> content;

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

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * senderNickname : 敖国跃
         * senderMobile :
         * senderHeadImgUrl : http://avatar.ice.colourlife.com/avatar?uid=aoguoyue
         * receiveTime : 2018-06-22 18:45:10
         * receiveMoney : 0.01
         * isBest : 1
         * lsType : 1
         * lishiUuid : e45ba29dfe6c4810adedea0eca177309
         */

        private String senderNickname;
        private String senderMobile;
        private String senderHeadImgUrl;
        private String receiveTime;
        private double receiveMoney;
        private int isBest;
        private int lsType;
        private String lishiUuid;

        public String getSenderNickname() {
            return senderNickname;
        }

        public void setSenderNickname(String senderNickname) {
            this.senderNickname = senderNickname;
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

        public String getReceiveTime() {
            return receiveTime;
        }

        public void setReceiveTime(String receiveTime) {
            this.receiveTime = receiveTime;
        }

        public double getReceiveMoney() {
            return receiveMoney;
        }

        public void setReceiveMoney(double receiveMoney) {
            this.receiveMoney = receiveMoney;
        }

        public int getIsBest() {
            return isBest;
        }

        public void setIsBest(int isBest) {
            this.isBest = isBest;
        }

        public int getLsType() {
            return lsType;
        }

        public void setLsType(int lsType) {
            this.lsType = lsType;
        }

        public String getLishiUuid() {
            return lishiUuid;
        }

        public void setLishiUuid(String lishiUuid) {
            this.lishiUuid = lishiUuid;
        }
    }
}
