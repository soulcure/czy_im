package com.youmai.hxsdk.entity;

/**
 * Created by colin on 2016/7/21.
 */
public class ScoreResult {

    /**
     * code : 0
     * message : success
     * content :
     * contentEncrypt :
     */

    private int code;
    private String message;
    private String content;
    private String contentEncrypt;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }
}
