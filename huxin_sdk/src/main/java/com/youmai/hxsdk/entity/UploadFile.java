package com.youmai.hxsdk.entity;

/**
 * Created by colin on 2016/7/21.
 */
public class UploadFile extends RespBaseBean {


    /**
     * fileid : 68713
     */

    private DBean d;


    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        private String fileid;

        public String getFileid() {
            return fileid;
        }

        public void setFileid(String fileid) {
            this.fileid = fileid;
        }
    }
}
