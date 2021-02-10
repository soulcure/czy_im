package com.color.czy.entity;

public class User {

    /**
     * code : 0
     * message : success
     * content : {"id":2507615,"uuid":"61909269-1fcd-4a29-aef0-ab1aabd1934b","mobile":"13000000000","email":"","state":0,"is_deleted":1,"nick_name":"             可爱多","name":"             可爱多","portrait":"2017/12/31/12/572059551.jpg","gender":2,"real_name":""}
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
         * id : 2507615
         * uuid : 61909269-1fcd-4a29-aef0-ab1aabd1934b
         * mobile : 13000000000
         * email :
         * state : 0
         * is_deleted : 1
         * nick_name :              可爱多
         * name :              可爱多
         * portrait : 2017/12/31/12/572059551.jpg
         * gender : 2
         * real_name :
         */

        private int id;
        private String uuid;
        private String mobile;
        private String email;
        private int state;   //邀请关系（不用）
        private int is_deleted;  //（不用）
        private String nick_name;  //昵称
        private String name;  //姓名 （不用）
        private String portrait;  //头像
        private int gender;   //性别
        private String real_name; //真实姓名 （不用）

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(int is_deleted) {
            this.is_deleted = is_deleted;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }
    }
}
