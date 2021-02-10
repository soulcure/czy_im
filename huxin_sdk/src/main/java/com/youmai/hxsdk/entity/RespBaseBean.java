package com.youmai.hxsdk.entity;


import com.youmai.hxsdk.ProtocolCallBack;


public class RespBaseBean {

    protected String s;
    protected String m;

    private static ProtocolCallBack sCallBack;

    public static void setProtocolCallBack(ProtocolCallBack callBack) {
        sCallBack = callBack;
    }

    public static ProtocolCallBack getsCallBack() {
        return sCallBack;
    }

    public boolean isSucess() {
        return isSuccess();
    }


    public boolean isSuccess() {
        boolean res = false;
        if (s != null && s.equals("1")) {
            res = true;
        } else if (s != null && s.equals("-200")) {
            if (sCallBack != null) {
                sCallBack.sessionExpire();
            }
            return false;
        }
        return res;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }
}
