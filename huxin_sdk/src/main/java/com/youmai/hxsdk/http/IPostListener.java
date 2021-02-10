package com.youmai.hxsdk.http;


/**
 * Htpp连接地址，参数，返回数据管理接口
 */
public interface IPostListener {

    /**
     * 传回的数据处理
     *
     * @param response
     */

    void httpReqResult(final String response);

}
