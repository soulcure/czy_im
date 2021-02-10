package com.youmai.hxsdk.entity.red;

import java.util.List;

public class RedPackageList {


    /**
     * code : 0
     * message :
     * content : [{"atid":2,"name":"彩集饭票","pano":"9f22bdb6934141ecb7e5a4506958a51b","desc":"由彩生活集团发放","pano_type":1,"community_uuid":"","balance":"50"}]
     * contentEncrypt :
     */

    private int code;
    private String message;
    private String contentEncrypt;
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

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * atid : 2
         * name : 彩集饭票
         * pano : 9f22bdb6934141ecb7e5a4506958a51b
         * desc : 由彩生活集团发放
         * pano_type : 1
         * community_uuid :
         * balance : 50
         */

        private int atid;
        private String name;
        private String pano;
        private String desc;
        private int pano_type;
        private String community_uuid;
        private String balance;

        public int getAtid() {
            return atid;
        }

        public void setAtid(int atid) {
            this.atid = atid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPano() {
            return pano;
        }

        public void setPano(String pano) {
            this.pano = pano;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getPano_type() {
            return pano_type;
        }

        public void setPano_type(int pano_type) {
            this.pano_type = pano_type;
        }

        public String getCommunity_uuid() {
            return community_uuid;
        }

        public void setCommunity_uuid(String community_uuid) {
            this.community_uuid = community_uuid;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }
}
