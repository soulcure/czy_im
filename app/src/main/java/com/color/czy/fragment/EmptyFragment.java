package com.color.czy.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.color.czy.CZYApplication;
import com.color.czy.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.entity.AuthConfig;
import com.color.czy.entity.User;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.TimeUtils;

import java.util.List;


public class EmptyFragment extends Fragment implements View.OnClickListener {

    String accessToken;
    int position;
    private List<String> uuid;
    private TextView tv_login;
    private CZYApplication app;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        app = (CZYApplication) context.getApplicationContext();
        uuid = app.getUuid();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {
        tv_login = view.findViewById(R.id.tv_login);
        tv_login.setText("登录用户："
                + HuxinSdkManager.instance().getPhoneNum()
                + HuxinSdkManager.instance().getDisplayName());

        view.findViewById(R.id.btn_test1).setOnClickListener(this);
        view.findViewById(R.id.btn_test2).setOnClickListener(this);

        view.findViewById(R.id.btn_add).setOnClickListener(this);
        view.findViewById(R.id.btn_agree).setOnClickListener(this);
        view.findViewById(R.id.btn_del_buddy).setOnClickListener(this);

        view.findViewById(R.id.btn_proto).setOnClickListener(this);
        view.findViewById(R.id.btn_upload).setOnClickListener(this);
        view.findViewById(R.id.btn_del).setOnClickListener(this);

        MaterialSpinner spinner = view.findViewById(R.id.spinner);
        spinner.setItems(app.getNickName());
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int pos, long id, String item) {
                position = pos;
            }
        });


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_add) {
            addFriend();
        } else if (id == R.id.btn_agree) {
            addFriendAgree();
        } else if (id == R.id.btn_del_buddy) {
            delBuddy();
        } else if (id == R.id.btn_test1) {
            auth();
        } else if (id == R.id.btn_test2) {
            reqUserInfoByUuid();
        } else if (id == R.id.btn_proto) {
            getUserInfo();
        } else if (id == R.id.btn_upload) {
            HuxinSdkManager.instance().uploadUserInfo();
        } else if (id == R.id.btn_del) {
            //long time = 1531267200L * 1000;
            //long time = 1531407600L * 1000;
            //long time = 1531494000L * 1000;
            //long time = 1528840799L * 1000;
            //String timeStr = TimeUtils.dateFormat(time);
            //Toast.makeText(getContext(), "time=" + timeStr, Toast.LENGTH_SHORT).show();

            HuxinSdkManager.instance().entryChatService(getContext());
        }


    }


    private void addFriend() {
        String dstUuid = uuid.get(position);
        String remark = "我是谁";

        ProtoCallback.AddFriendListener listener = new ProtoCallback.AddFriendListener() {
            @Override
            public void result(YouMaiBuddy.IMOptBuddyRsp ack) {
                if (isAdded()) {
                    if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_OK) {
                        Toast.makeText(getContext(), "添加好友请求成功", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_REQUESTING) {
                        Toast.makeText(getContext(), "重复添加好友请求成功", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_BUILT) {
                        Toast.makeText(getContext(), "已经是好友", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BLACKLIST) {
                        Toast.makeText(getContext(), "黑名单", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_NOT_BUDDY) {
                        Toast.makeText(getContext(), "非好友", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_EXPIRED) {
                        Toast.makeText(getContext(), "好友请求已过期", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_OTHER_ERROR) {
                        Toast.makeText(getContext(), "请求错误", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_READD) {
                        Toast.makeText(getContext(), "删除后重新添加", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        HuxinSdkManager.instance().addFriend(dstUuid,
                YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_REQ,
                remark, listener);
    }


    private void addFriendAgree() {
        String dstUuid = uuid.get(position);
        String remark = "我同意";

        ProtoCallback.AddFriendListener listener = new ProtoCallback.AddFriendListener() {
            @Override
            public void result(YouMaiBuddy.IMOptBuddyRsp ack) {
                if (isAdded()) {
                    if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_OK) {
                        Toast.makeText(getContext(), "添加好友请求成功", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_REQUESTING) {
                        Toast.makeText(getContext(), "重复添加好友请求成功", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_BUILT) {
                        Toast.makeText(getContext(), "已经是好友了", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BLACKLIST) {
                        Toast.makeText(getContext(), "黑名单", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "出现错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        HuxinSdkManager.instance().addFriend(dstUuid,
                YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_AGREE,
                remark, listener);
    }


    private void delBuddy() {
        final String dstUuid = uuid.get(position);
        String remark = "我删除";

        ProtoCallback.AddFriendListener listener = new ProtoCallback.AddFriendListener() {
            @Override
            public void result(YouMaiBuddy.IMOptBuddyRsp ack) {
                if (isAdded()) {
                    if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_OK) {
                        CacheMsgHelper.instance().deleteAllMsg(getContext(), dstUuid);
                        Toast.makeText(getContext(), "删除好友请求成功", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_REQUESTING) {
                        Toast.makeText(getContext(), "重复删除好友请求成功", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BUDDY_BUILT) {
                        Toast.makeText(getContext(), "已经是好友了", Toast.LENGTH_SHORT).show();
                    } else if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_BLACKLIST) {
                        Toast.makeText(getContext(), "黑名单", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "出现错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        HuxinSdkManager.instance().addFriend(dstUuid,
                YouMaiBuddy.BuddyOptType.BUDDY_OPT_DEL,
                remark, listener);
    }


    private void auth() {
        ColorsConfig.reqAuth(new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                AuthConfig bean = GsonUtil.parse(response, AuthConfig.class);
                if (isAdded() && bean != null && bean.isSuccess()) {
                    accessToken = bean.getContent().getAccessToken();
                }
            }
        });
    }

    private void reqUserInfoByUuid() {
        final int debug = 0;  //测试环境
        String URL[] = new String[]{"https://openapi-test.colourlife.com/v1/", "https://openapi.colourlife.com/v1/"}; //host
        String COLOR_APPID[] = new String[]{"ICECZYIM-XE17-EZE5-TGLX-59FCF8D4PW6K", "ICEYOUMAI-631E-4ED8-968D-F0A6F82DBCA7"};  //appkey
        String COLOR_TOKEN[] = new String[]{"exKSzQyuWGoctaTZTblK", "033047E9A6DD7E94E2D2"};  //token

        String url = URL[debug] + "czyuser/czy/userInfoByUuid";

        String token = COLOR_TOKEN[debug];
        String appKey = COLOR_APPID[debug];
        long ts = System.currentTimeMillis() / 1000;
        String uuid = HuxinSdkManager.instance().getUuid();

        ContentValues params = new ContentValues();

        params.put("uuid", uuid);
        params.put("access_token", accessToken);

        params.put("ts", ts);
        params.put("appID", appKey);
        params.put("sign", AppUtils.md5(appKey + ts + token + false));
        HttpConnector.httpGet(url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                User bean = GsonUtil.parse(response, User.class);
                if (isAdded() && bean != null && bean.isSuccess()) {

                }
            }
        });

    }


    private void getUserInfo() {

        String uuid = "a0f56168-3059-4863-af17-eb5fe2e5cd01";

        HuxinSdkManager.instance().reqUserInfo(uuid, new ProtoCallback.UserInfo() {
            @Override
            public void result(ContactBean contactBean) {
                if (isAdded()) {
                    Log.v("test", "nickName=" + contactBean.getNickName());
                }
            }
        });


    }
}
