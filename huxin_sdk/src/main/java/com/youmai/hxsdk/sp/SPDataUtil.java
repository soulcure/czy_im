package com.youmai.hxsdk.sp;

import android.content.Context;
import android.graphics.Point;

import com.youmai.hxsdk.utils.AppUtils;

/**
 * Created by colin on 2016/8/15.
 */
public class SPDataUtil {

    /**
     * SP key值
     */
    public static final String HX_FLOAT_POSITION = "HooXinFloatPosition";
    public static final String HX_HALF_POSITION = "HooXinCallIconPosition";
    public static final String PLAY_INTERACT = "playInteract";//是否关闭摇段子
    public static final String REP_REFRESH = "repRefresh";//代言列表刷新
    public static final String COUPON_REFRESH = "couponRefresh";//券列表刷新
    public static final String VIDEO_REFRESH = "videoRefresh";//视频秀刷新
    public static final String SHOW_GUIDE_SETTING = "isFirstShowGuide";//第一次安装APP的新手引导
    public static final String SHOW_GUIDE_DIAL = "isFirstShowGuideDial";//第一次设置秀拨打电话
    public static final String FIRST_SHOW_CALL = "isFirstShowCall";//第一次弹屏显示手指引导
    public static final String SHOW_GUIDE_MSG_LIST_CALL = "isFirstShowGuideMsgListCall";//第一次沟通列表打电话引导
    public static final String SHOW_GUIDE_CALL_SHOW = "isFirstShowGuideCallShow";//第一次设置通话秀引导
    public static final String SHOW_GUIDE_CARD = "isFirstShowGuideCard";//第一次沟通卡提示引导
    public static final String SHOW_GUIDE_CARD_REMARK = "isFirstShowGuideCardRemark";//第一次沟通卡修改备注提示引导
    public static final String FIRST_SHOW_FID = "firstShowFid";//第一次设置秀时自己的Fid
    public static final String FIRST_SHOW_PFID = "firstShowPFid";//第一次设置秀时自己的PFid
    public static final String HAS_NEW_VERSION = "has_new_version"; //是否有新版本
    public static final String OLD_VERSION = "old_version"; //旧版本号
    public static final String USER_INFO_JSON = "userInfoJson";//缓存用户信息的Json
    public static final String USER_HEADER_NUM = "userHeaderNum";//缓存用户信息的Json
    public static final String FIRST_WELCOME = "firstWelcome";//第一次启动欢迎界面
    public static final String HUBI_SCALE = "hubiscale";//呼币比例
    public static final String CALL_RATE = "call_rate";//通话利率
    public static final String PURSE_GET_CODE_COUNT = "pursegetcodecount";//提现获取验证码次数
    public static final String PURSE_GET_CODE_TIME = "pursegetcodetime";//获取验证时间 用于每日三次获取验证码重置
    public static final String PAY_TYPE = "pay_type";//提现类型，默认初始1：支付宝方式
    public static final String HAS_REP_AWARD = "has_rep_award";//有完成代言奖励

    public static final String TIP_MONEY_MAIN_STEPS = "isTipMoneyMain";//1：赚钱主页引导   2   3   4   5
//    public static final String TIP_MONEY_MAIN_STEPS2 = "isTipMoneySteps2";//进入下载App得呼币引导
//    public static final String TIP_MONEY_MAIN_STEPS3 = "isTipMoneySteps3";//进入我的钱包引导
//    public static final String TIP_MONEY_MAIN_STEPS4 = "isTipMoneySteps4";//进入邀请好友引导
//    public static final String TIP_MONEY_MAIN_STEPS5 = "isTipMoneySteps5";//进入代言广场引导

    public static final String HUXIN_CALL_CONTACT_ID = "huxin_call_contact_id";
//    public static final String HAS_INVITABLE = "has_invitable";// 是否被邀请过

    public static final String SIGN_IN_STATUS = "signInStatus"; //签到状态
    public static final String LUCKY_CACHE_DATA = "luckyCacheData"; //转盘后台数据
    public static final String COMBO_END = "comboEnd";//套餐结束时间
    //    private static final String IS_BIND_WECHAT = "isBindWechat";
//    private static final String WECHAT_NICKNAME = "wechat_nickname";
    private static final String FIRST_RECHARGE_REDPOINT = "firstRechargeRedPoint"; // 第一次话费充值显示红点
    private static final String FIRST_EXTEND_REDPOINT = "firstExtendRedPoint"; // 第一次我要推广显示红点
    private static final String VOIP_DIALOG_TIMESTAMP = "voipDialogTimestamp";
    private static final String FIRST_IS_BIZCARD_PERFECT = "firstIsBizcardPerfect"; //第一次名片信息必填项是否完整

    public static final String REMIND_FIRST_TIP = "remindFirstTip"; //设置第一次提醒
    public static final String VOICE_DIAL_TIP = "voiceDialTip"; //长按语音拨号

    private SPDataUtil() {
        throw new AssertionError();
    }

    public static void setHooXinFloatPosition(Context context, Point point) {
        AppUtils.setStringSharedPreferences(context, HX_FLOAT_POSITION, point.x + "," + point.y);
    }

    public static Point getHooXinFloatPosition(Context context) {
        String str = AppUtils.getStringSharedPreferences(context, HX_FLOAT_POSITION, "100,100");
        try {
            String[] strArray = str.split(",");
            return new Point(Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1]));
        } catch (Exception e) {
            return new Point();
        }
    }

    public static void setHooXinCallMenuPosition(Context context, Point point) {
        AppUtils.setStringSharedPreferences(context, HX_HALF_POSITION, point.x + "," + point.y);
    }

    //半屏
    public static Point getHooXinCallMenuPosition(Context context) {
        String str = AppUtils.getStringSharedPreferences(context, HX_HALF_POSITION, "0, 300");
        try {
            String[] strs = str.split(",");
            return new Point(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
        } catch (Exception e) {
            return new Point(0, 300);
        }

    }


    /***
     * 设置打开或关闭摇段子
     *
     * @param context
     * @param value
     */
    public static void setPlayInteract(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, PLAY_INTERACT, value);
    }


    /**
     * 获取打开或关闭摇段子
     *
     * @param context
     * @return
     */
    public static boolean getPlayInteract(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, PLAY_INTERACT, false);
    }


    /***
     * 代言刷新列表数据
     *
     * @param context
     * @param value
     */
    public static void setRepRefresh(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, REP_REFRESH, value);
    }


    /**
     * 代言刷新列表数据
     *
     * @param context
     * @return
     */
    public static boolean getRepRefresh(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, REP_REFRESH, false);
    }

    /***
     * 券刷新列表数据
     *
     * @param context
     * @param value
     */
    public static void setCouponRefresh(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, COUPON_REFRESH, value);
    }


    /**
     * 券刷新列表数据
     *
     * @param context
     * @return
     */
    public static boolean getCouponRefresh(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, COUPON_REFRESH, false);
    }

    /***
     * 设置完视频秀是否销毁设置秀界面
     *
     * @param context
     * @param value
     */
    public static void setVideoRefresh(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, VIDEO_REFRESH, value);
    }


    /**
     * 设置完视频秀是否销毁设置秀界面
     *
     * @param context
     * @return
     */
    public static boolean getVideoRefresh(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, VIDEO_REFRESH, false);
    }

    /***
     * 第一次安装APP显示秀引导 -- setting
     *
     * @param context
     * @param value
     */
    public static void setIsFirstShowGuide(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, SHOW_GUIDE_SETTING, value);
    }

    /**
     * 第一次安装APP显示秀引导 -- setting
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstShowGuide(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, SHOW_GUIDE_SETTING, false);
    }

    /***
     * 第一次安装APP显示秀引导 -- DIAL
     *
     * @param context
     * @param value
     */
    public static void setIsFirstShowGuideDial(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, SHOW_GUIDE_DIAL, value);
    }

    /**
     * 第一次安装APP显示秀引导 -- DIAL
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstShowGuideDial(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, SHOW_GUIDE_DIAL, false);
    }

    /***
     * 第一次安装电话弹屏引导 -- call
     *
     * @param context
     * @param value
     */
    public static void setIsFirstShowCall(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, FIRST_SHOW_CALL, value);
    }

    /**
     * 第一次安装电话弹屏引导 -- call
     *
     * @param context
     * @return
     */
    public static boolean getIsFirstShowCall(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, FIRST_SHOW_CALL, false);
    }

    /***
     * 第一次设置秀的Fid -- show
     *
     * @param context
     * @param value
     */
    public static void setFirstShowFid(Context context, String value) {
        AppUtils.setStringSharedPreferences(context, FIRST_SHOW_FID, value);
    }

    /**
     * 第一次设置秀的Fid -- show
     *
     * @param context
     * @return
     */
    public static String getFirstShowFid(Context context) {
        return AppUtils.getStringSharedPreferences(context, FIRST_SHOW_FID, "-1");
    }

    /***
     * 第一次设置秀的Fid -- show
     *
     * @param context
     * @param value
     */
    public static void setFirstShowPFid(Context context, String value) {
        AppUtils.setStringSharedPreferences(context, FIRST_SHOW_PFID, value);
    }

    /**
     * 第一次设置秀的Fid -- show
     *
     * @param context
     * @return
     */
    public static String getFirstShowPFid(Context context) {
        return AppUtils.getStringSharedPreferences(context, FIRST_SHOW_PFID, "");
    }

    /***
     * 设置用户个人信息Json
     *
     * @param context
     * @param value
     */
    public static void setUserInfoJson(Context context, String value) {
        AppUtils.setStringSharedPreferences(context, USER_INFO_JSON, value);
    }

    /**
     * 获取用户个人信息Json
     *
     * @param context
     * @return
     */
    public static String getUserInfoJson(Context context) {
        return AppUtils.getStringSharedPreferences(context, USER_INFO_JSON, "");
    }

    /***
     * 保存登录用户个人登录号码
     *
     * @param context
     * @param value
     */
    public static void setHeaderNum(Context context, String value) {
        AppUtils.setStringSharedPreferences(context, USER_HEADER_NUM, value);
    }

    /**
     * 获取登录用户个人登录号码
     *
     * @param context
     * @return
     */
    public static String getHeaderNum(Context context) {
        return AppUtils.getStringSharedPreferences(context, USER_HEADER_NUM, "");
    }

    /**
     * 设置是否第一次使用
     */
    public static void setFirstWelcome(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, FIRST_WELCOME, value);
    }

    /**
     * 获取是否第一次使用的状态
     */
    public static Boolean getFirstWelcome(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, FIRST_WELCOME, true);
    }

    /**
     * 设置沟通卡是否第一次使用
     */
    public static void setFirstCard(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, SHOW_GUIDE_CARD, value);
    }

    /**
     * 获取沟通卡是否第一次使用的状态
     */
    public static Boolean getFirstCard(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, SHOW_GUIDE_CARD, true);
    }

    /**
     * 设置沟通卡修改备注是否第一次使用
     */
    public static void setFirstCardRemark(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, SHOW_GUIDE_CARD_REMARK, value);
    }

    /**
     * 获取沟通卡修改备注是否第一次使用的状态
     */
    public static Boolean getFirstCardRemark(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, SHOW_GUIDE_CARD_REMARK, true);
    }

    /**
     * 保存呼币与人民币的兑换比例
     *
     * @param context
     * @param scale
     */
    public static void setHuiScale(Context context, int scale) {
        AppUtils.setIntSharedPreferences(context, HUBI_SCALE, scale);
    }

    /**
     * 获取呼币与人民币的兑换比例
     *
     * @param context
     * @return
     */
    public static int getHuiScale(Context context) {
        return AppUtils.getIntSharedPreferences(context, HUBI_SCALE, 1000);//默认 1000呼币：1元
    }

    /**
     * 保存通话利率
     */
    public static void setCallRate(Context context, int scale) {
        AppUtils.setIntSharedPreferences(context, CALL_RATE, scale);
    }

    /**
     * 获取通话利率
     */
    public static int getCallRate(Context context) {
        return AppUtils.getIntSharedPreferences(context, CALL_RATE, 10);//默认 10呼币：1分钟
    }


    /**
     * 钱包提现获取验证码次数
     *
     * @param context
     * @param count
     */
    public static void setPurseGetCodeCount(Context context, int count) {
        AppUtils.setIntSharedPreferences(context, PURSE_GET_CODE_COUNT, count);
    }

    /**
     * 钱包提现获取验证码次数
     *
     * @param context
     * @return
     */
    public static int getPurseGetCodeCount(Context context) {
        return AppUtils.getIntSharedPreferences(context, PURSE_GET_CODE_COUNT, 3);//默认3次
    }


    /**
     * 获取验证时间 用于每日三次获取验证码重置
     *
     * @param context
     * @param time
     */
    public static void setPurseGetCodeTime(Context context, long time) {
        AppUtils.setLongSharedPreferences(context, PURSE_GET_CODE_TIME, time);
    }

    /**
     * 获取验证时间 用于每日三次获取验证码重置
     *
     * @param context
     * @return
     */
    public static long getPurseGetCodeTime(Context context) {
        return AppUtils.getLongSharedPreferences(context, PURSE_GET_CODE_TIME, System.currentTimeMillis());//默认3次
    }

    /**
     * 保存提现习惯方式
     */
    public static void setPayType(Context context, int type) {
        AppUtils.setIntSharedPreferences(context, PAY_TYPE, type);
    }

    /**
     * 获取提现习惯方式
     */
    public static int getPayType(Context context) {
        return AppUtils.getIntSharedPreferences(context, PAY_TYPE, 1);
    }

    /**
     * 历史代言加入刷新
     */
    public static void setRepAward(Context context, boolean type) {
        AppUtils.setBooleanSharedPreferences(context, HAS_REP_AWARD, type);
    }

    /**
     * 历史代言加入刷新
     */
    public static boolean getRepAward(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, HAS_REP_AWARD, false);
    }

    /**
     * 获取免费电话回拨电话号码池在本地通讯录的contact_id 用于更新号码池
     *
     * @param context
     * @return
     */
    public static int getHuxinCallContactID(Context context) {
        return AppUtils.getIntSharedPreferences(context, HUXIN_CALL_CONTACT_ID, -1);//默认 -1 不能默认为0  其他都行
    }

    /**
     * 保存 免费电话回拨电话号码池在本地通讯录的contact_id 用于更新号码池
     */
    public static void setHuxinCallContactID(Context context, int contact_id) {
        AppUtils.setIntSharedPreferences(context, HUXIN_CALL_CONTACT_ID, contact_id);
    }
//    /**
//     * 是否被邀请过
//     */
//    public static void setHasInvitable(Context context, boolean type) {
//        AppUtils.setBooleanSharedPreferences(context, HAS_INVITABLE, type);
//    }
//
//    /**
//     * 是否被邀请过
//     */
//    public static boolean getHasInvitable(Context context) {
//        return AppUtils.getBooleanSharedPreferences(context, HAS_INVITABLE, false);
//    }

    /**
     * 签到
     */
    public static String getSignInStatus(Context context) {
        return AppUtils.getStringSharedPreferences(context, SIGN_IN_STATUS, "0");
    }

    /**
     * 签到
     */
    public static void setSignInStatus(Context context, String value) {
        AppUtils.setStringSharedPreferences(context, SIGN_IN_STATUS, value);
    }

    /**
     * 转盘的后台缓存数据
     */
    public static String getLuckyCacheData(Context context) {
        return AppUtils.getStringSharedPreferences(context, LUCKY_CACHE_DATA, "");
    }

    /**
     * 转盘的后台缓存数据
     */
    public static void setLuckyCacheData(Context context, String value) {
        AppUtils.setStringSharedPreferences(context, LUCKY_CACHE_DATA, value);
    }

    /**
     * 购买套餐结束时间
     */
    public static String getComboEnd(Context context) {
        return AppUtils.getStringSharedPreferences(context, COMBO_END, "");
    }

    /**
     * 购买套餐结束时间
     */
    public static void setComboEnd(Context context, String value) {
        AppUtils.setStringSharedPreferences(context, COMBO_END, value);
    }

//    /**
//     * 是否绑定微信
//     */
//    public static boolean getIsBindWechat(Context context) {
//        return AppUtils.getBooleanSharedPreferences(context, IS_BIND_WECHAT, false);
//    }
//
//    /**
//     * 是否绑定微信
//     */
//    public static void setIsBindWechat(Context context, boolean value) {
//        AppUtils.setBooleanSharedPreferences(context, IS_BIND_WECHAT, value);
//    }

//    /**
//     * 绑定微信后的昵称
//     */
//    public static String getWechatNickname(Context context) {
//        return AppUtils.getStringSharedPreferences(context, WECHAT_NICKNAME, "");
//    }
//
//    /**
//     * 绑定微信后的昵称
//     */
//    public static void setWechatNickname(Context context, String value) {
//        AppUtils.setStringSharedPreferences(context, WECHAT_NICKNAME, value);
//    }

    /**
     * 第一次话费充值显示红点
     */
    public static void setFirstRechargeRedpoint(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, FIRST_RECHARGE_REDPOINT, value);
    }

    /**
     * 第一次话费充值显示红点
     */
    public static boolean getFirstRechargeRedpoint(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, FIRST_RECHARGE_REDPOINT, true);
    }

    /**
     * 第一次我要推广显示红点
     */
    public static void setFirstExtendRedpoint(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, FIRST_EXTEND_REDPOINT, value);
    }

    /**
     * 第一次我要推广显示红点
     */
    public static boolean getFirstExtendRedpoint(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, FIRST_EXTEND_REDPOINT, true);
    }

    /**
     * 未充值用户显示对话框的时间戳
     */
    public static void setVoipDialogTimestamp(Context context, int value) {
        AppUtils.setIntSharedPreferences(context, VOIP_DIALOG_TIMESTAMP, value);
    }

    /**
     * 未充值用户显示对话框的时间戳
     */
    public static int getVoipDialogTimestamp(Context context) {
        return AppUtils.getIntSharedPreferences(context, VOIP_DIALOG_TIMESTAMP, 0);
    }

    /**
     * 我的-名片信息必填项是否完善
     */
    public static void setFirstIsBizcardPerfect(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, FIRST_IS_BIZCARD_PERFECT, value);
    }

    /**
     * 我的-名片信息必填项是否完善
     */
    public static boolean getFirstIsBizcardPerfect(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, FIRST_IS_BIZCARD_PERFECT, false);
    }


    /**
     * 提醒 - 是否第一次设置提醒成功
     */
    public static void setFirstRemind(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, REMIND_FIRST_TIP, value);
    }

    /**
     * 提醒 - 是否第一次设置提醒成功
     */
    public static boolean getFirstRemind(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, REMIND_FIRST_TIP, false);
    }

    /**
     * 长按语音拨号
     */
    public static void setVoiceDial(Context context, boolean value) {
        AppUtils.setBooleanSharedPreferences(context, VOICE_DIAL_TIP, value);
    }

    /**
     * 长按语音拨号
     */
    public static boolean getVoiceDial(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, VOICE_DIAL_TIP, false);
    }
}
