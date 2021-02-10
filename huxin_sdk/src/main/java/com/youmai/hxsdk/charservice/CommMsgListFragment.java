package com.youmai.hxsdk.charservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.MessageAdapter;
import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页-沟通
 * A simple {@link Fragment} subclass.
 */
public class CommMsgListFragment extends Fragment implements IMMsgCallback {

    private final String TAG = CommMsgListFragment.class.getSimpleName();


    //重新登录或重新连接
    private static final int RELOGIN_HUXIN_SERVER = 100;

    //progress加载完成显示进度
    private static final int LOAD_PROGRESS_DISMISS = 200;

    private XRecyclerView recyclerView;
    private MessageAdapter mMessageAdapter;

    private ProgressDialog mProgressDialog;
    private Handler mHandler;

    // 换号登录需要去判断改变
    private LinearLayoutManager mLinearLayoutManager;

    private List<Integer> unReadListPosition = new ArrayList<>();
    private int curPostion;

    // 空页面 start
    private LinearLayout mEmptyView;
    private EmptyRecyclerViewDataObserver mEmpty = new EmptyRecyclerViewDataObserver();
    // 空页面 end

    private TextView tv_error;


    private static class MsgHandler extends Handler {
        WeakReference<CommMsgListFragment> weakReference;

        public MsgHandler(CommMsgListFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CommMsgListFragment fragment = weakReference.get();
            if (fragment == null || fragment.getActivity() == null
                    || fragment.getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case RELOGIN_HUXIN_SERVER:
                    HuxinSdkManager.instance().imReconnect();
                    break;
                case LOAD_PROGRESS_DISMISS:
                    if (fragment.mProgressDialog != null) {
                        fragment.mProgressDialog.dismiss();
                        fragment.mProgressDialog = null;
                    }

                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HuxinSdkManager.instance().chatMsgFromCache(this, 2,
                new ProtoCallback.CacheMsgCallBack() {
                    @Override
                    public void result(List<ExCacheMsgBean> data) {
                        mMessageAdapter.changeMessageList(data);
                        initUnreadList();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_owner_msg_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View rootView) {
        mHandler = new MsgHandler(this);

        tv_error = rootView.findViewById(R.id.tv_error);
        tv_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HuxinSdkManager.instance().reLoginQuiet();
            }
        });

        recyclerView = (XRecyclerView) rootView.findViewById(R.id.message_refresh_recycler);

        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageAdapter = new MessageAdapter(getActivity());
        mEmptyView = (LinearLayout) rootView.findViewById(R.id.message_empty_view);
        recyclerView.setAdapter(mMessageAdapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                mHandler.removeMessages(RELOGIN_HUXIN_SERVER);
                mHandler.sendEmptyMessageDelayed(RELOGIN_HUXIN_SERVER, 1000);
                recyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                // load more data here
            }
        });
        recyclerView.setLoadingMoreEnabled(false);

        mMessageAdapter.registerAdapterDataObserver(mEmpty);

        mMessageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExCacheMsgBean bean, int position) {
                if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_SINGLE) {//单聊
                    String uuid = bean.getTargetUuid();
                    String nickName = bean.getDisplayName();
                    String avatar = bean.getTargetAvatar();
                    String userName = bean.getTargetUserName();
                    String mobile = bean.getSenderMobile();

                    HuxinSdkManager.instance().entryChatSingle(getContext(), uuid, nickName, avatar, userName, mobile);

                } else if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_GROUP) {   //群聊

                    int groupId = bean.getGroupId();
                    int groupType = bean.getGroupType();
                    String groupName = bean.getDisplayName();
                    HuxinSdkManager.instance().entryChatGroup(getContext(), groupId, groupType, groupName);
                }
            }
        });

        mMessageAdapter.setOnLongItemClickListener(new MessageAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, ExCacheMsgBean bean) {
                if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_SINGLE
                        || bean.getUiType() == MessageAdapter.ADAPTER_TYPE_GROUP) {
                    delPopUp(v, bean);
                }
            }
        });

        setLoginStatus();

    }


    private void setLoginStatus() {
        HuxinSdkManager.LoginStatusListener listener = new HuxinSdkManager.LoginStatusListener() {
            @Override
            public void onKickOut() {
                if (tv_error != null) {
                    tv_error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReLoginSuccess() {
                if (tv_error != null) {
                    tv_error.setVisibility(View.GONE);
                }
            }
        };
        HuxinSdkManager.instance().setLoginStatusListener(listener);
    }


    @Override
    public void onResume() {
        super.onResume();
        HuxinSdkManager.instance().setImMsgCallback(this);

        if (HuxinSdkManager.instance().isKicked()) {
            tv_error.setVisibility(View.VISIBLE);
        } else {
            tv_error.setVisibility(View.GONE);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        HuxinSdkManager.instance().removeImMsgCallback(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mMessageAdapter != null) {
            mMessageAdapter.unregisterAdapterDataObserver(mEmpty);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {//处理dismiss 时崩溃
            mHandler.removeMessages(LOAD_PROGRESS_DISMISS);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    private void delPopUp(View v, ExCacheMsgBean bean) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hx_im_del_lay, null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v, 400, 0);
        TextView tv_del = (TextView) view.findViewById(R.id.tv_del);

        final String targetUuid = bean.getTargetUuid();

        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delMsgChat(targetUuid);
                popupWindow.dismiss();
            }
        });
    }

    public void delMsgChat(String targetUuid) {
        CacheMsgHelper.instance().deleteAllMsg(getActivity(), targetUuid);
        //去掉未读消息计数
        IMMsgManager.instance().removeBadge(targetUuid);
        mMessageAdapter.deleteMessage(targetUuid);
    }


    private void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(msg);
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            try {
                mProgressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dismissProgress(final String msg) {
        dismissProgress(msg, 1000);
    }

    private void dismissProgress(final String msg, int delayed) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                if (msg != null) {
                    mProgressDialog.setMessage(msg);
                }
                mHandler.sendEmptyMessageDelayed(LOAD_PROGRESS_DISMISS, delayed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * IM消息的入口回调
     *
     * @param imComingMsg
     */
    @Override
    public void onBuddyMsgCallback(CacheMsgBean imComingMsg) {

    }

    @Override
    public void onCommunityMsgCallback(CacheMsgBean imComingMsg) {
        if (imComingMsg != null) {
            ExCacheMsgBean bean = new ExCacheMsgBean(imComingMsg);

            String targetId = bean.getTargetUuid();

            boolean isTop = HuxinSdkManager.instance().getMsgTop(targetId);
            if (isTop) {
                bean.setTop(true);
            }

            bean.setDisplayName(imComingMsg.getTargetName());
            mMessageAdapter.addTop(bean);


            initUnreadList();
        }
    }

    @Override
    public void onOwnerMsgCallback(CacheMsgBean imComingMsg) {

    }


    private class EmptyRecyclerViewDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    }

    private void checkIfEmpty() {
        if (mMessageAdapter != null) {
            int count = mMessageAdapter.getItemCount();
            //正常状态
            if (count == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }


    private void initUnreadList() {
        List<ExCacheMsgBean> list = mMessageAdapter.getMsgList();
        unReadListPosition.clear();
        for (int i = 0; i < list.size(); i++) {
            ExCacheMsgBean item = list.get(i);
            int count = IMMsgManager.instance().getBadeCount(item.getTargetUuid());
            if (count > 0 && !unReadListPosition.contains(i)) {
                unReadListPosition.add(i);
            }
        }
    }

    public void scrollToNextUnRead() {
        if (curPostion < unReadListPosition.size()) {
            int index = unReadListPosition.get(curPostion) + 1;
            mLinearLayoutManager.scrollToPositionWithOffset(index, 0);
            curPostion++;
        } else {
            curPostion = 0;
        }
    }
}
