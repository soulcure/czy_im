package com.youmai.hxsdk.forward;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.data.SortComparator;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;
import com.youmai.hxsdk.view.group.TeamHeadView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;

/**
 * Created by youmai on 17/2/14.
 */

public class ForwardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = ForwardAdapter.class.getSimpleName();

    public static final int ADAPTER_TYPE_SEARCH = 1;  //搜索
    public static final int ADAPTER_TYPE_SINGLE = 3;  //单聊消息
    public static final int ADAPTER_TYPE_GROUP = 4;  //群聊消息


    private Context mContext;
    private List<ExCacheMsgBean> messageList;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnLongItemClickListener;

    public ForwardAdapter(Context context) {
        mContext = context;
        messageList = new ArrayList<>();
    }


    public void changeMessageList(List<ExCacheMsgBean> newList) {
        List<ExCacheMsgBean> oldList = new ArrayList<>();
        for (ExCacheMsgBean item : messageList) {
            if (item.getUiType() == ForwardAdapter.ADAPTER_TYPE_SINGLE
                    || item.getUiType() == ForwardAdapter.ADAPTER_TYPE_GROUP) {
                oldList.add(item);
            }
        }
        messageList.removeAll(oldList);
        messageList.addAll(newList);

        SortComparator comp = new SortComparator();
        Collections.sort(messageList.subList(1, messageList.size()), comp);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == ADAPTER_TYPE_SEARCH) {
            View view = inflater.inflate(R.layout.message_list_item_header_search, parent, false);
            return new MsgItemSearch(view);
        } else if (viewType == ADAPTER_TYPE_SINGLE) {
            View view = inflater.inflate(R.layout.single_message_item_layout, parent, false);
            return new MsgItemChat(view);
        } else if (viewType == ADAPTER_TYPE_GROUP) {
            View view = inflater.inflate(R.layout.group_message_item_layout, parent, false);
            return new MsgItemGroup(view);
        } else {
            View view = inflater.inflate(R.layout.single_message_item_layout, parent, false);
            return new MsgItemChat(view);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ExCacheMsgBean model = messageList.get(position);
        if (holder instanceof MsgItemSearch) {
            MsgItemSearch viewHeader = (MsgItemSearch) holder;
            viewHeader.header_item.setTag(position);

        } else if (holder instanceof MsgItemChat) {
            final MsgItemChat itemView = (MsgItemChat) holder;
            itemView.message_item.setTag(position);
            itemView.message_time.setText(TimeUtils.dateFormat(model.getMsgTime()));

            String displayName = model.getDisplayName();
            boolean contains = displayName.contains(ColorsConfig.GROUP_DEFAULT_NAME);
            if (contains) {
                displayName = displayName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
            }
            itemView.message_name.setText(displayName);

            switch (model.getMsgType()) {
                case CacheMsgBean.SEND_EMOTION:
                case CacheMsgBean.RECEIVE_EMOTION:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_1));
                    break;
                case CacheMsgBean.SEND_TEXT:
                case CacheMsgBean.RECEIVE_TEXT:
                    CacheMsgTxt textM = (CacheMsgTxt) model.getJsonBodyObj();
                    SpannableString msgSpan = new SpannableString(textM.getMsgTxt());
                    msgSpan = EmoticonHandler.getInstance(mContext.getApplicationContext()).getTextFace(
                            textM.getMsgTxt(), msgSpan, 0, Utils.getFontSize(itemView.message_type.getTextSize()));
                    itemView.message_type.setText(msgSpan);
                    break;
                case CacheMsgBean.SEND_IMAGE:
                case CacheMsgBean.RECEIVE_IMAGE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_3));
                    break;
                case CacheMsgBean.SEND_LOCATION:
                case CacheMsgBean.RECEIVE_LOCATION:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_4));
                    break;
                case CacheMsgBean.SEND_VIDEO:
                case CacheMsgBean.RECEIVE_VIDEO:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_5));
                    break;
                case CacheMsgBean.SEND_VOICE:
                case CacheMsgBean.RECEIVE_VOICE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_sounds));
                    break;
                case CacheMsgBean.SEND_FILE:
                case CacheMsgBean.RECEIVE_FILE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_file));
                    break;
                default:
                    itemView.message_type.setText(mContext.getString(R.string.message_type));
            }

            //沟通列表
            int unreadCount = IMMsgManager.instance().getBadeCount(model.getTargetUuid());
            if (unreadCount > 0) {
                if (unreadCount > 99) {
                    itemView.message_status.setBadgeText("...");
                } else {
                    itemView.message_status.setBadgeNumber(unreadCount);
                }
                itemView.message_status.setGravityOffset(0.5f, 0.5f, true);
                itemView.message_status.setBadgePadding(1.0f, true);
                itemView.message_status.setVisibility(View.VISIBLE);
            } else {
                itemView.message_status.setVisibility(View.GONE);
            }

            int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);

            String avatar = model.getTargetAvatar();
            Glide.with(mContext).load(avatar)
                    .apply(new RequestOptions()
                            .transform(new GlideRoundTransform())
                            .override(size, size)
                            .placeholder(R.drawable.color_default_header)
                            .error(R.drawable.color_default_header)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(itemView.message_icon);

        } else if (holder instanceof MsgItemGroup) {
            final MsgItemGroup itemView = (MsgItemGroup) holder;
            itemView.message_item.setTag(position);
            itemView.message_time.setText(TimeUtils.dateFormat(model.getMsgTime()));

            String displayName = model.getDisplayName();
            if (!TextUtils.isEmpty(displayName)
                    && displayName.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                displayName = displayName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
            }
            itemView.message_name.setText(displayName);

            switch (model.getMsgType()) {
                case CacheMsgBean.SEND_EMOTION:
                case CacheMsgBean.RECEIVE_EMOTION:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_1));
                    break;
                case CacheMsgBean.SEND_TEXT:
                case CacheMsgBean.RECEIVE_TEXT:
                    CacheMsgTxt textM = (CacheMsgTxt) model.getJsonBodyObj();
                    SpannableString msgSpan = new SpannableString(textM.getMsgTxt());
                    msgSpan = EmoticonHandler.getInstance(mContext.getApplicationContext()).getTextFace(
                            textM.getMsgTxt(), msgSpan, 0, Utils.getFontSize(itemView.message_type.getTextSize()));
                    itemView.message_type.setText(msgSpan);
                    break;
                case CacheMsgBean.SEND_IMAGE:
                case CacheMsgBean.RECEIVE_IMAGE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_3));
                    break;
                case CacheMsgBean.SEND_LOCATION:
                case CacheMsgBean.RECEIVE_LOCATION:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_4));
                    break;
                case CacheMsgBean.SEND_VIDEO:
                case CacheMsgBean.RECEIVE_VIDEO:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_5));
                    break;
                case CacheMsgBean.SEND_VOICE:
                case CacheMsgBean.RECEIVE_VOICE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_sounds));
                    break;
                case CacheMsgBean.SEND_FILE:
                case CacheMsgBean.RECEIVE_FILE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_file));
                    break;
                case CacheMsgBean.GROUP_MEMBER_CHANGED:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_member_changed));
                    break;
                case CacheMsgBean.GROUP_NAME_CHANGED:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_name_changed));
                    break;
                default:
                    itemView.message_type.setText(mContext.getString(R.string.message_type));
            }

            //沟通列表
            int unreadCount = IMMsgManager.instance().getBadeCount(model.getTargetUuid());
            if (unreadCount > 0) {
                if (unreadCount > 99) {
                    itemView.message_status.setBadgeText("...");
                } else {
                    itemView.message_status.setBadgeNumber(unreadCount);
                }
                itemView.message_status.setGravityOffset(0f, 0f, true);
                itemView.message_status.setBadgePadding(1.0f, true);
                itemView.message_status.setVisibility(View.VISIBLE);
            } else {
                itemView.message_status.setVisibility(View.GONE);
            }

            /*int groupId = model.getGroupId();
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
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(model, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongItemClickListener != null) {
                    mOnLongItemClickListener.onItemLongClick(v, model);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getUiType();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(ExCacheMsgBean bean, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, ExCacheMsgBean bean);
    }

    public class MsgItemSearch extends RecyclerView.ViewHolder {
        LinearLayout header_item;

        public MsgItemSearch(View itemView) {
            super(itemView);
            header_item = (LinearLayout) itemView.findViewById(R.id.list_item_header_search_root);
        }
    }


    protected class MsgItemChat extends RecyclerView.ViewHolder {
        ImageView message_icon, message_callBtn;
        TextView message_name, message_type, message_time;
        QBadgeView message_status;
        RelativeLayout message_item;
        RelativeLayout icon_layout;

        public MsgItemChat(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            icon_layout = (RelativeLayout) itemView.findViewById(R.id.icon_layout);
            message_icon = (ImageView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            message_status = new QBadgeView(mContext);
            message_status.bindTarget(icon_layout);
            message_status.setBadgeGravity(Gravity.TOP | Gravity.END);
            message_status.setBadgeTextSize(10f, true);
            message_status.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
            message_status.setShowShadow(false);
        }
    }

    protected class MsgItemGroup extends RecyclerView.ViewHolder {
        TeamHeadView message_icon;
        ImageView message_callBtn;
        TextView message_name, message_type, message_time;
        QBadgeView message_status;
        RelativeLayout message_item;
        RelativeLayout icon_layout;

        public MsgItemGroup(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            icon_layout = (RelativeLayout) itemView.findViewById(R.id.icon_layout);
            message_icon = (TeamHeadView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            message_status = new QBadgeView(mContext);
            message_status.bindTarget(icon_layout);
            message_status.setBadgeGravity(Gravity.TOP | Gravity.END);
            message_status.setBadgeTextSize(10f, true);
            message_status.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
            message_status.setShowShadow(false);
        }
    }
}
