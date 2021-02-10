package com.youmai.hxsdk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.group.adapter.GroupListAdapter;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:54
 * 描述：群聊列表
 */
public class GroupListFragment extends Fragment {

    public static final String GROUPTYPE = "groupType";

    public static final String GROUP_ID = "groupId";
    public static final String GROUP_EXIT = "group.exit";

    private Context mContext;
    private XRecyclerView mRefreshRecyclerView;
    private GroupListAdapter mAdapter;
    private List<GroupInfoBean> mGroupList;
    private View group_empty_view;

    private LocalBroadcastManager localBroadcastManager;
    private LocalMsgReceiver mLocalMsgReceiver;

    private int groupType = YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT_VALUE;

    private EmptyRecyclerViewDataObserver mEmptyRvDataObserver = new EmptyRecyclerViewDataObserver();


    /**
     * 消息广播
     */
    private class LocalMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (GROUP_EXIT.equals(action)) {
                int groupId = intent.getIntExtra("groupId", 0);
                if (groupId != 0) {
                    mAdapter.exitGroupById(groupId);
                }
            }

        }
    }

    public static GroupListFragment newInstance(YouMaiBasic.GroupType groupType) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(GROUPTYPE, groupType.getNumber());
        fragment.setArguments(bundle);
        return fragment;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            groupType = bundle.getInt(GROUPTYPE, YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT_VALUE);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        mLocalMsgReceiver = new LocalMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GROUP_EXIT);
        localBroadcastManager.registerReceiver(mLocalMsgReceiver, filter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_group_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initView(view);
        initData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.unregisterAdapterDataObserver(mEmptyRvDataObserver);
        }
        localBroadcastManager.unregisterReceiver(mLocalMsgReceiver);
        localBroadcastManager = null;
    }

    private void initView(View view) {

        group_empty_view = view.findViewById(R.id.group_empty_view);

        mRefreshRecyclerView = (XRecyclerView) view.findViewById(R.id.group_xrv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRefreshRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GroupListAdapter(getContext(), groupType);
        mRefreshRecyclerView.setAdapter(mAdapter);
        mRefreshRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                mRefreshRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                // load more data here
            }
        });
        mRefreshRecyclerView.setLoadingMoreEnabled(false);
        mRefreshRecyclerView.setRefreshProgressStyle(AVLoadingIndicatorView.BallRotate);
        mAdapter.registerAdapterDataObserver(mEmptyRvDataObserver);

        mAdapter.setOnItemClickListener(new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupInfoBean bean) {

                Intent intent = new Intent(mContext, IMGroupActivity.class);
                intent.putExtra(IMGroupActivity.DST_NAME, bean.getGroup_name());
                intent.putExtra(IMGroupActivity.DST_UUID, bean.getGroup_id());
                intent.putExtra(IMGroupActivity.GROUP_TYPE, bean.getGroupType());
                intent.putExtra(IMGroupActivity.GROUP_INFO, bean);
                startActivity(intent);

            }
        });

        mAdapter.setOnLongItemClickListener(new GroupListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                ToastUtil.showToast(mContext, "删除成功：" + position);
                GroupInfoBean group = mAdapter.getMessageList().get(position);
                mAdapter.deleteMessage(position);
                //去掉未读消息计数
            }
        });

    }


    private Long findEntityId(int groupId, List<GroupInfoBean> cacheList) {
        Long id = null;
        if (cacheList != null && cacheList.size() > 0) {
            for (GroupInfoBean item : cacheList) {
                if (item.getGroup_id() == groupId) {
                    id = item.getId();
                    break;
                }
            }
        }
        return id;
    }

    private void initData() {
        final List<GroupInfoBean> cacheList = GroupInfoHelper.instance().toQueryGroupList(getContext());
        List<YouMaiGroup.GroupItem> list = new ArrayList<>();

        //不使用缓存
        /*try {
            if (cacheList != null && cacheList.size() > 0) {
                for (GroupInfoBean item : cacheList) {
                    YouMaiGroup.GroupItem.Builder builder = YouMaiGroup.GroupItem.newBuilder();
                    builder.setGroupId(item.getGroup_id());
                    builder.setInfoUpdateTime(item.getInfo_update_time());
                    builder.setGroupType(YouMaiBasic.GroupType.valueOf(item.getGroupType()));
                    list.add(builder.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        HuxinSdkManager.instance().reqGroupList(list, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupListRsp ack = YouMaiGroup.GroupListRsp.parseFrom(pduBase.body);
                    List<YouMaiGroup.GroupInfo> changeList = ack.getGroupInfoListList();

                    List<GroupInfoBean> list = new ArrayList<>();
                    if (!ListUtils.isEmpty(changeList)) {
                        for (YouMaiGroup.GroupInfo item : changeList) {
                            if (item.getGroupType() == YouMaiBasic.GroupType.valueOf(groupType)) {
                                GroupInfoBean bean = new GroupInfoBean();

                                bean.setId(findEntityId(item.getGroupId(), cacheList));
                                bean.setGroup_id(item.getGroupId());
                                bean.setGroup_name(item.getGroupName());
                                bean.setOwner_id(item.getOwnerId());
                                bean.setGroup_avatar(item.getGroupAvatar());
                                bean.setTopic(item.getTopic());
                                bean.setInfo_update_time(item.getInfoUpdateTime());
                                bean.setGroup_member_count(item.getGroupMemberCount());
                                bean.setGroupType(item.getGroupType().getNumber());
                                list.add(bean);
                            }
                        }

                        GroupInfoHelper.instance().insertOrUpdate(mContext, list);
                    }
                    mGroupList = list;

                    /*List<Integer> delList = ack.getDeleteGroupIdListList();
                    if (!ListUtils.isEmpty(delList)) {
                        for (Integer item : delList) {
                            GroupInfoHelper.instance().delGroupInfo(mContext, item);
                        }
                    }

                    if (ListUtils.isEmpty(changeList) && ListUtils.isEmpty(delList)) {
                        mGroupList = cacheList;
                    } else {
                        mGroupList = GroupInfoHelper.instance().toQueryGroupList(mContext);
                    }*/

                    mAdapter.setGroupList(mGroupList);

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void emptyList() {
        if (ListUtils.isEmpty(mGroupList)) {
            group_empty_view.setVisibility(View.VISIBLE);
        } else {
            group_empty_view.setVisibility(View.GONE);
        }
    }


    /**
     * 监听RecyclerView的数据变化
     */
    private class EmptyRecyclerViewDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            emptyList();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            emptyList();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            emptyList();
        }
    }


}
