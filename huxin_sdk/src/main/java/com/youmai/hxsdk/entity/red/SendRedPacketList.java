package com.youmai.hxsdk.entity.red;

import java.util.List;

public class SendRedPacketList {

    /**
     * code : 0
     * message : SUCC
     * content : [{"uuid":"286973b3966e4321bf536a8cdaa15df2","lsType":2,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderMobile":"18118748201","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 18:55:47","timeAllowWithdraw":"2018-06-25 19:55:47","blessing":"HK旅途赶紧婆婆剧透具图","moneyTotal":0.5,"numberTotal":5,"status":1,"moneyDraw":"0.25","numberDraw":2},{"uuid":"89ac0ca012434923910e97676169f868","lsType":1,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderMobile":"18118748201","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 17:21:59","timeAllowWithdraw":"2018-06-25 18:21:59","blessing":"hi儿咯7k7kpls好哦是进去你平时喜欢一个人嗖嗖嗖马坡山景区","moneyTotal":0.1,"numberTotal":1,"status":4,"moneyDraw":"0.1","numberDraw":1},{"uuid":"1e0df5c3567a47e4be0161b304b9ab04","lsType":1,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderMobile":"18118748201","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 17:15:59","timeAllowWithdraw":"2018-06-25 18:15:59","blessing":"嗨一下人做说切转切！和密码跟你这手速宿舍破苏州雅思培训！？。，1472586690","moneyTotal":0.1,"numberTotal":1,"status":4,"moneyDraw":"0.1","numberDraw":1},{"uuid":"61bffec98b8246489f6e48411299424d","lsType":1,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderMobile":"18118748201","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 16:18:34","timeAllowWithdraw":"2018-06-25 17:18:34","blessing":"各具特色去！呵护破几件事旅途理解拓展兔兔","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"aeaed9af0b2641e6a741680d160cc419","lsType":2,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderMobile":"18118748201","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 16:16:58","timeAllowWithdraw":"2018-06-25 17:16:58","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"1e7caf8740ce47e48dd002db4c4d7b07","lsType":2,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderMobile":"18118748201","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 15:25:45","timeAllowWithdraw":"2018-06-25 16:25:45","blessing":"会没收人做和给你！。。，","moneyTotal":1,"numberTotal":5,"status":1,"moneyDraw":"0.52","numberDraw":2},{"uuid":"83ef9a55d87148edb4a481c5b470c1dd","lsType":1,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 15:17:12","timeAllowWithdraw":"2018-06-25 16:17:12","blessing":"大吉大利，开开心心！","moneyTotal":500,"numberTotal":1,"status":4,"moneyDraw":"500.0","numberDraw":1},{"uuid":"8d1348f88d2841d2b09275416494a74f","lsType":1,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 15:05:14","timeAllowWithdraw":"2018-06-25 16:05:14","blessing":"大吉大利，开开心心！","moneyTotal":0.1,"numberTotal":1,"status":0,"moneyDraw":"0.0","numberDraw":0},{"uuid":"b6e97c320ff94c96b6bf1cf0a12840a3","lsType":1,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 15:04:46","timeAllowWithdraw":"2018-06-25 16:04:46","blessing":"看似圣彼得堡国立大学教授认为！扫激动激动一下自己去看看有没有合适","moneyTotal":0.1,"numberTotal":1,"status":0,"moneyDraw":"0.0","numberDraw":0},{"uuid":"01c28eb20ab44cf685ea4d3dbb70feae","lsType":1,"senderUserUuid":"651fca50-42c4-4cba-aa0d-c2ada686eb84","senderName":"叶永刚","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=yeyonggang","sendTime":"2018-06-25 14:59:03","timeAllowWithdraw":"2018-06-25 15:59:03","blessing":"大吉大利，开开心心！","moneyTotal":0.1,"numberTotal":1,"status":4,"moneyDraw":"0.1","numberDraw":1}]
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
         * uuid : 286973b3966e4321bf536a8cdaa15df2
         * lsType : 2
         * senderUserUuid : 651fca50-42c4-4cba-aa0d-c2ada686eb84
         * senderName : 叶永刚
         * senderMobile : 18118748201
         * senderHeadImgUrl : http://avatar.ice.colourlife.com/avatar?uid=yeyonggang
         * sendTime : 2018-06-25 18:55:47
         * timeAllowWithdraw : 2018-06-25 19:55:47
         * blessing : HK旅途赶紧婆婆剧透具图
         * moneyTotal : 0.5
         * numberTotal : 5
         * status : 1
         * moneyDraw : 0.25
         * numberDraw : 2
         */

        private String uuid;
        private int lsType;
        private String senderUserUuid;
        private String senderName;
        private String senderMobile;
        private String senderHeadImgUrl;
        private String sendTime;
        private String timeAllowWithdraw;
        private String blessing;
        private double moneyTotal;
        private int numberTotal;
        private int status;
        private String moneyDraw;
        private int numberDraw;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getLsType() {
            return lsType;
        }

        public void setLsType(int lsType) {
            this.lsType = lsType;
        }

        public String getSenderUserUuid() {
            return senderUserUuid;
        }

        public void setSenderUserUuid(String senderUserUuid) {
            this.senderUserUuid = senderUserUuid;
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

        public String getSendTime() {
            return sendTime;
        }

        public void setSendTime(String sendTime) {
            this.sendTime = sendTime;
        }

        public String getTimeAllowWithdraw() {
            return timeAllowWithdraw;
        }

        public void setTimeAllowWithdraw(String timeAllowWithdraw) {
            this.timeAllowWithdraw = timeAllowWithdraw;
        }

        public String getBlessing() {
            return blessing;
        }

        public void setBlessing(String blessing) {
            this.blessing = blessing;
        }

        public double getMoneyTotal() {
            return moneyTotal;
        }

        public void setMoneyTotal(double moneyTotal) {
            this.moneyTotal = moneyTotal;
        }

        public int getNumberTotal() {
            return numberTotal;
        }

        public void setNumberTotal(int numberTotal) {
            this.numberTotal = numberTotal;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMoneyDraw() {
            return moneyDraw;
        }

        public void setMoneyDraw(String moneyDraw) {
            this.moneyDraw = moneyDraw;
        }

        public int getNumberDraw() {
            return numberDraw;
        }

        public void setNumberDraw(int numberDraw) {
            this.numberDraw = numberDraw;
        }
    }
}
