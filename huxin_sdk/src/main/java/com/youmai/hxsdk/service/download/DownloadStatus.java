package com.youmai.hxsdk.service.download;

import com.youmai.hxsdk.service.download.bean.FileQueue;

import java.util.List;



/**
 * Created by fylder on 2017/6/28.
 */

public class DownloadStatus {

    private int status;//1:开始   2:结束    100:通知获取数据  200:返回数据
    private List<FileQueue> datas;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<FileQueue> getDatas() {
        return datas;
    }

    public void setDatas(List<FileQueue> datas) {
        this.datas = datas;
    }
}
