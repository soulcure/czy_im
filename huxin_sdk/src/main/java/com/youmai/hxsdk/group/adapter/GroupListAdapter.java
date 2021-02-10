package com.youmai.hxsdk.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.view.group.TeamHeadView;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者：create by YW
 * 日期：2018.04.14 14:53
 * 描述：
 */
public class GroupListAdapter extends RecyclerView.Adapter {

    public static final int ADAPTER_TYPE_HEADER = 1;
    public static final int ADAPTER_TYPE_NORMAL = 2;

    private final int HEADER_COUNT = 1;
    private final int ITEM_NORMAL = 2;

    private int mAdapterType = ADAPTER_TYPE_NORMAL;

    private Context mContext;
    private int groupType;
    private List<GroupInfoBean> mGroupList;

    public GroupListAdapter(Context context, int groupType) {
        this.mContext = context;
        this.groupType = groupType;
        mGroupList = new ArrayList<>();
    }

    public void setGroupList(@NonNull List<GroupInfoBean> list) {
        for (GroupInfoBean item : list) {
            if (item.getGroupType() == groupType) {
                mGroupList.add(item);
            }
        }

        notifyDataSetChanged();
    }

    public void exitGroupById(int groupId) {
        for (GroupInfoBean item : mGroupList) {
            if (item.getGroup_id() == groupId) {
                mGroupList.remove(item);
                break;
            }
        }

        notifyDataSetChanged();
    }


    public List<GroupInfoBean> getMessageList() {
        return mGroupList;
    }

    public void deleteMessage(int position) {
        mGroupList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mAdapterType == ADAPTER_TYPE_NORMAL) {
            return ITEM_NORMAL;
        } else {
            if (position >= 0 && position < getHeaderCount()) {
                return HEADER_COUNT;
            } else {
                return ITEM_NORMAL;
            }
        }
    }


    @Override
    public int getItemCount() {
        int count = mGroupList == null ? 0 : mGroupList.size();
        if (mAdapterType == ADAPTER_TYPE_HEADER) {
            return count + HEADER_COUNT;
        }
        return count;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == ITEM_NORMAL) {
            View view = inflater.inflate(R.layout.group_item_layout, parent, false);
            GroupViewHolder viewItem = new GroupViewHolder(view);
            return viewItem;
        } else {
            View view = inflater.inflate(R.layout.message_list_item_header_search, parent, false);
            GroupViewSearchItem viewItem = new GroupViewSearchItem(view);
            return viewItem;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof GroupViewSearchItem) {
            GroupViewSearchItem viewHeader = (GroupViewSearchItem) holder;
            viewHeader.header_item.setTag(position);
            viewHeader.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(null);
                    }
                }
            });
        } else {
            int currPos = position - getHeaderCount(); //实际的位置
            final GroupViewHolder itemView = (GroupViewHolder) holder;

            final GroupInfoBean ben = mGroupList.get(currPos);

            String displayName = ben.getGroup_name();
            int groupType = ben.getGroupType();
            if (1 == groupType) {
                itemView.message_icon.setImageResource(R.drawable.contacts_communitychat);
            } else {
                itemView.message_icon.setImageResource(R.drawable.contacts_groupchat);
            }
            boolean contains = displayName.contains(ColorsConfig.GROUP_DEFAULT_NAME);
            if (contains) {
                displayName = displayName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
            }

            itemView.message_name.setText(displayName);

            /*int groupId = ben.getGroup_id();
            GroupInfoHelper.OnResultCallBack callBack = new GroupInfoHelper.OnResultCallBack() {
                @Override
                public void onMembers(List<GroupAndMember> list) {
                    if (!ListUtils.isEmpty(list)) {
                        List<String> headUrl = new ArrayList<>();
                        for (GroupAndMember item : list) {
                            String avatar = Contants.URl.HEAD_ICON_URL + "avatar?uid=" + item.getUser_name();
                            headUrl.add(avatar);
                        }

                        itemView.message_icon.displayImage(headUrl);
                        itemView.message_icon.load();
                    }
                }
            };

            GroupInfoHelper.instance().toQueryByGroupId(mContext, groupId, callBack);*/

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        if (getHeaderCount() == HEADER_COUNT) {
                            mOnItemClickListener.onItemClick(null);
                        } else {
                            mOnItemClickListener.onItemClick(ben);
                        }
                    }
                }
            });
        }
    }


    public int getHeaderCount() {
        if (mAdapterType == ADAPTER_TYPE_HEADER) {
            return HEADER_COUNT;
        }
        return 0;
    }

    public class GroupViewSearchItem extends RecyclerView.ViewHolder {
        LinearLayout header_item;

        public GroupViewSearchItem(View itemView) {
            super(itemView);
            header_item = (LinearLayout) itemView.findViewById(R.id.list_item_header_search_root);
        }
    }

    protected class GroupViewHolder extends RecyclerView.ViewHolder {
        TeamHeadView message_icon;
        ImageView message_callBtn;
        TextView message_name, message_time;
        RelativeLayout message_item;

        public GroupViewHolder(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            message_icon = (TeamHeadView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnLongItemClickListener;

    public void setAdapterType(int type) {
        this.mAdapterType = type;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(GroupInfoBean bean);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
