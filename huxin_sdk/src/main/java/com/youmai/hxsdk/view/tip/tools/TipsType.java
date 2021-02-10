package com.youmai.hxsdk.view.tip.tools;

import com.youmai.hxsdk.view.tip.bean.TipBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fylder on 2017/11/21.
 */

public class TipsType {

    public static String TIP_DELETE = "delete";//删除
    public static String TIP_COPY = "copy";//复制
    public static String TIP_COLLECT = "collect";//收藏
    public static String TIP_FORWARD = "forward";//转发
    public static String TIP_TURN_TEXT = "turn_text";//转文字
    public static String TIP_READ = "read";//朗读
    public static String TIP_REMIND = "remind";//提醒
    public static String TIP_MORE = "more";//更多
    public static String TIP_EMO_KEEP = "keep";//收藏表情

    //所有
    public static List<TipBean> getAllType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("更多", TIP_MORE));
        beanList.add(new TipBean("删除", TIP_DELETE));
        beanList.add(new TipBean("收藏", TIP_COLLECT));
        //beanList.add(new TipBean("转发", TIP_FORWARD));
        beanList.add(new TipBean("复制", TIP_COPY));
        beanList.add(new TipBean("朗读", TIP_READ));
        beanList.add(new TipBean("转文字", TIP_TURN_TEXT));
        beanList.add(new TipBean("提醒", TIP_REMIND));
        return beanList;
    }

    //对方文本
    public static List<TipBean> getTextType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("删除", TIP_DELETE));
        //beanList.add(new TipBean("转发", TIP_FORWARD));
        beanList.add(new TipBean("复制", TIP_COPY));
        return beanList;
    }

    //己方文本
    public static List<TipBean> getMyselfTextType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("删除", TIP_DELETE));
        //beanList.add(new TipBean("转发", TIP_FORWARD));
        beanList.add(new TipBean("复制", TIP_COPY));
        return beanList;
    }

    //右边自己发送表情
    public static List<TipBean> getSelfEmotionType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("更多", TIP_MORE));
        beanList.add(new TipBean("删除", TIP_DELETE));
        beanList.add(new TipBean("转发", TIP_FORWARD));
        return beanList;
    }

    //左边接受到的表情
    public static List<TipBean> getRecEmotionType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("删除", TIP_DELETE));
        //beanList.add(new TipBean("转发", TIP_FORWARD));
        return beanList;
    }

    //语音
    public static List<TipBean> getVoiceType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("删除", TIP_DELETE));
        //beanList.add(new TipBean("转发", TIP_FORWARD));
        return beanList;
    }


    //备注
    public static List<TipBean> getRemarkType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("更多", TIP_MORE));
        beanList.add(new TipBean("删除", TIP_DELETE));
        beanList.add(new TipBean("提醒", TIP_REMIND));
        return beanList;
    }

    //图片、视频、位置、文件、名片
    public static List<TipBean> getOtherType() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("删除", TIP_DELETE));
        //beanList.add(new TipBean("转发", TIP_FORWARD));
        return beanList;
    }

}
