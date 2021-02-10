package com.youmai.hxsdk.entity.red;

public class ShareRedPackage {


    /**
     * code : 0
     * message : SUCC
     * content : {"title":"给你发了一个利是","description":"大吉大利，今晚吃鸡","image":"http://5173996.s21i-5.faiusr.com/4/ABUIABAEGAAgvNXVpgUowrLBnQQwhAI4bg!160x160.png"}
     */

    public boolean isSuccess() {
        return code == 0;
    }

    private int code;
    private String message;
    private ContentBean content;

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
         * title : 给你发了一个利是
         * description : 大吉大利，今晚吃鸡
         * image : http://5173996.s21i-5.faiusr.com/4/ABUIABAEGAAgvNXVpgUowrLBnQQwhAI4bg!160x160.png
         */

        private String title;
        private String description;
        private String image;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
