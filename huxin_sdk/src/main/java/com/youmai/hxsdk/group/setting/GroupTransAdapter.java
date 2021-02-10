package com.youmai.hxsdk.group.setting;

import android.content.Context;
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

import java.util.List;

/**
 * Created by yw on 2018/4/13.
 */
public class GroupTransAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<GroupTransAdapter.HeaderHolder> {

    enum TYPE {
        SEARCH, DEFAULT
    }

    private Context mContext;
    private int mCollectIndex = 0;
    private ItemEventListener itemEventListener;
    private final List<CNPinyin<ContactBean>> cnPinyinList;

    public GroupTransAdapter(Context context, List<CNPinyin<ContactBean>> cnPinyinList, ItemEventListener listener) {
        this.mContext = context.getApplicationContext();
        this.cnPinyinList = cnPinyinList;
        this.itemEventListener = listener;
    }

    private int selectPos = -1;

    public void setSelected(int position) {
        this.selectPos = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*int type;
        if (position == 0) {
            type = TYPE.SEARCH.ordinal();
        } else {
            type = TYPE.DEFAULT.ordinal();
        }*/
        int type = TYPE.DEFAULT.ordinal();
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*if (viewType == TYPE.SEARCH.ordinal()) {
            return new SearchHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.global_list_item_header_search, parent, false));
        } else {
            return new ContactHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_contact_item, parent, false));
        }*/
        return new ContactHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ContactBean contact = cnPinyinList.get(position).data;

        if (holder instanceof ContactHolder) {
            final ContactHolder contactHolder = (ContactHolder) holder;
            try {
                int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);
                Glide.with(mContext)
                        .load(contact.getAvatar())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .transform(new GlideRoundTransform())
                                .override(size, size)
                                .error(R.drawable.color_default_header))
                        .into(contactHolder.iv_header);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (selectPos == position) {
                contactHolder.cb_collect.setChecked(true);
            } else {
                contactHolder.cb_collect.setChecked(false);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemEventListener) {
                        itemEventListener.onItemClick(position, contact);
                    }
                }
            });

            if (contact.getNickName().startsWith("↑##@@**") && position <= mCollectIndex) {
                contactHolder.tv_name.setText(contact.getDisplayName().substring(9));
            } else {
                contactHolder.tv_name.setText(contact.getDisplayName());
            }

        } else if (holder instanceof SearchHolder) {
            //搜索框不处理
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemEventListener) {
                        itemEventListener.onItemClick(holder.getAdapterPosition(), contact);
                    }
                }
            });
        }

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

    public class SearchHolder extends RecyclerView.ViewHolder {
        public SearchHolder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView tv_header;

        public HeaderHolder(View itemView) {
            super(itemView);
            tv_header = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        public ContactHolder(View itemView) {
            super(itemView);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            cb_collect = itemView.findViewById(R.id.cb_collect);
        }
    }

    public interface ItemEventListener {
        void onItemClick(int pos, ContactBean contact);
    }

}
