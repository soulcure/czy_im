package com.youmai.hxsdk;

import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.proto.YouMaiBuddy;

import java.util.List;

public class ProtoCallback {

    /**
     * 好友列表回调
     */
    public interface ContactListener {
        void result(List<ContactBean> list);
    }

    public interface AddFriendListener {
        void result(YouMaiBuddy.IMOptBuddyRsp ack);
    }


    public interface ModifyNickName {
        void result(YouMaiBuddy.IMModiNickNameRsp ack);
    }


    public interface ModifyAvatar {
        void result(YouMaiBuddy.IMChangeAvatarRsp ack);
    }


    public interface BuddyNotify {
        void result(YouMaiBuddy.IMOptBuddyNotify notify);
    }

    public interface UserInfo {
        void result(ContactBean contactBean);
    }

    public interface CacheMsgCallBack {
        void result(List<ExCacheMsgBean> data);
    }


}
