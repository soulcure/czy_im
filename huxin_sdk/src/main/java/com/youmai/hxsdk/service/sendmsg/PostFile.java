package com.youmai.hxsdk.service.sendmsg;

/**
 * Created by fylder on 2017/11/14.
 */

public interface PostFile {

    void success(String fid, String desPhone);

    void fail(String msg);
}
