package com.youmai.hxsdk.group;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.stickyheader.StickyHeaderAdapter;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yw on 2018/4/13.
 */
public class SearchContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<SearchContactAdapter.HeaderHolder> {

    public enum TYPE {
        ORGANIZATION_TYPE, DEPARTMENT_TYPE, COLLECT_TYPE, CONTACT_TYPE
    }

    public static final int mIndexForCollect = 1;
    public static final int mIndexForContact = 5;

    private Map<Integer, ContactBean> mCacheMap;
    private Map<String, ContactBean> mTotalMap = new HashMap<>();
    private Map<String, ContactBean> groupMap = new HashMap<>();

    private Context mContext;
    private int mCollectIndex = 6;
    private ItemEventListener itemEventListener;
    private final List<CNPinyin<ContactBean>> cnPinyinList;

    public SearchContactAdapter(Context context, List<CNPinyin<ContactBean>> cnPinyinList,
                                int collectIndex, ItemEventListener listener) {
        this.mContext = context.getApplicationContext();
        this.cnPinyinList = cnPinyinList;
        this.mCollectIndex = collectIndex;
        this.itemEventListener = listener;

        if (mCollectIndex == mIndexForCollect) {
            mCacheMap = new HashMap<>(cnPinyinList.size());
        }
    }

    public Map<Integer, ContactBean> getCacheMap() {
        return mCacheMap;
    }

    //刷Adapter
    public void setCacheMap(Map<String, ContactBean> map) {
        this.mTotalMap = map;
        notifyDataSetChanged();
    }

    //不刷Adapter
    public void setMap(Map<String, ContactBean> map) {
        this.mTotalMap = map;
    }

    public void setGroupMap(Map<String, ContactBean> map) {
        this.groupMap = map;
    }

    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ContactBean contact = cnPinyinList.get(position).data;
        return contact.getUiType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE.ORGANIZATION_TYPE.ordinal()) {
            return new OrganizeHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contacts_fragment_item, parent, false));
        } else if (viewType == TYPE.DEPARTMENT_TYPE.ordinal()) {
            return new DepartHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contacts_fragment_item, parent, false));
        } else if (viewType == TYPE.COLLECT_TYPE.ordinal()) {
            return new CollectHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.collect_fragment_item, parent, false));
        } else {
            return new ContactHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contacts_fragment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ContactBean contact = cnPinyinList.get(position).data;

        if (holder instanceof OrganizeHolder) {
            ((OrganizeHolder) holder).tv_name.setText(contact.getDisplayName().substring(9));

            int icon = defaultIcon(2);
            ((OrganizeHolder) holder).iv_header.setImageResource(icon);
            ((OrganizeHolder) holder).cb_collect.setVisibility(View.GONE);
        } else if (holder instanceof DepartHolder) {
            ((DepartHolder) holder).tv_name.setText(contact.getDisplayName().substring(9));

            int icon = defaultIcon(1);
            ((DepartHolder) holder).iv_header.setImageResource(icon);
            ((DepartHolder) holder).cb_collect.setVisibility(View.GONE);

        } else if (holder instanceof CollectHolder) {
            ((CollectHolder) holder).tv_name.setText(contact.getDisplayName().substring(9));
        } else if (holder instanceof ContactHolder) {
            ((ContactHolder) holder).tv_name.setText(contact.getDisplayName());

            ((ContactHolder) holder).cb_collect.setVisibility(View.VISIBLE);

            if (null != groupMap && null != groupMap.get(contact.getUuid())) {
                ((ContactHolder) holder).cb_collect.setButtonDrawable(R.drawable.contact_select_def);
            } else {
                ((ContactHolder) holder).cb_collect.setButtonDrawable(R.drawable.contacts_select_selector);
                if (mTotalMap.get(contact.getUuid()) != null) {
                    ((ContactHolder) holder).cb_collect.setChecked(true);
                } else {
                    ((ContactHolder) holder).cb_collect.setChecked(false);
                }
            }

            int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);
            try {
                Glide.with(mContext)
                        .load(contact.getAvatar())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .override(size, size)
                                .transform(new GlideRoundTransform())
                                .placeholder(R.drawable.color_default_header)
                                .error(R.drawable.color_default_header))
                        .into(((ContactHolder) holder).iv_header);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact.getUiType() == SearchContactAdapter.TYPE.ORGANIZATION_TYPE.ordinal()
                        || contact.getUiType() == SearchContactAdapter.TYPE.DEPARTMENT_TYPE.ordinal()) {
                    if (null != itemEventListener) {
                        itemEventListener.onItemClick(position, contact);
                    }
                } else if (contact.getUiType() == SearchContactAdapter.TYPE.CONTACT_TYPE.ordinal()) {
                    if (((ContactHolder) holder).cb_collect.isChecked()) {
                        mCacheMap.remove(position);
                        ((ContactHolder) holder).cb_collect.setChecked(false);
                    } else {
                        mCacheMap.put(position, contact);
                        ((ContactHolder) holder).cb_collect.setChecked(true);
                    }
                    Intent intent = new Intent(AddContactsCreateGroupActivity.BROADCAST_FILTER);
                    intent.putExtra(AddContactsCreateGroupActivity.ACTION, AddContactsCreateGroupActivity.ADAPTER_CONTACT);
                    intent.putExtra("bean", contact);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    if (null != itemEventListener) {
                        itemEventListener.onItemClick(position, contact);
                        itemEventListener.collectCount(mCacheMap.size());
                    }
                }
            }
        });
    }


    @Override
    public long getHeaderId(int childAdapterPosition) {
        if (childAdapterPosition <= mCollectIndex) {
            return '↑';
        } else {
            return cnPinyinList.get(childAdapterPosition).getFirstChar();
        }
    }

    @Override
    public boolean specialIndex(int childAdapterPosition) {
        CNPinyin<ContactBean> contactCNPinyin = cnPinyinList.get(childAdapterPosition);
        return contactCNPinyin.getHeaderFilter().contains(contactCNPinyin.data.getDisplayName());
    }

    @Override
    public String index(int childAdapterPosition) {
        return cnPinyinList.get(childAdapterPosition).getFirstChar() + "";
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder holder, int childAdapterPosition) {
        holder.tv_header.setText(String.valueOf(cnPinyinList.get(childAdapterPosition).getFirstChar()));
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header, parent, false));
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView tv_header;

        public HeaderHolder(View itemView) {
            super(itemView);
            tv_header = itemView.findViewById(R.id.tv_header);
        }
    }

    public class OrganizeHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        public OrganizeHolder(View itemView) {
            super(itemView);
            iv_header = itemView.findViewById(R.id.iv_header);
            tv_name = itemView.findViewById(R.id.tv_name);
            cb_collect = itemView.findViewById(R.id.cb_collect);
        }
    }

    public class DepartHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        public DepartHolder(View itemView) {
            super(itemView);
            iv_header = itemView.findViewById(R.id.iv_header);
            tv_name = itemView.findViewById(R.id.tv_name);
            cb_collect = itemView.findViewById(R.id.cb_collect);
        }
    }


    public class CollectHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;

        public CollectHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        public ContactHolder(View itemView) {
            super(itemView);
            iv_header = itemView.findViewById(R.id.iv_header);
            tv_name = itemView.findViewById(R.id.tv_name);
            cb_collect = itemView.findViewById(R.id.cb_collect);
        }
    }

    public interface ItemEventListener {
        void onItemClick(int pos, ContactBean contact);

        void onLongClick(int pos);

        void collectCount(int count);
    }

    /**
     * 默认功能的头像
     *
     * @param position
     * @return
     */
    int defaultIcon(int position) {
        int icon = -1;
        switch (position) {
            case 1:
                icon = R.drawable.contacts_department;
                break;
            case 2:
                icon = R.drawable.contacts_org;
                break;
            case 3:
                icon = R.drawable.contacts_phone_list;
                break;
            case 4:
                icon = R.drawable.contacts_groupchat;
                break;
        }
        return icon;
    }


}
