package com.youmai.hxsdk.entity;


/**
 * Created by colin on 2018/3/21.
 */

public class UploadResult {


    private int code;
    private String message;
    private String content;

    public boolean isSuceess() {
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


}
