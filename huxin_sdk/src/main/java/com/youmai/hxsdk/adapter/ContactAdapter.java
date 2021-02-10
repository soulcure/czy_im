package com.youmai.hxsdk.adapter;

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
import com.youmai.hxsdk.stickyheader.StickyHeaderAdapter;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw on 2018/4/13.
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<ContactAdapter.HeaderHolder> {

    private Context mContext;
    private boolean isShowCheck;
    private final List<CNPinyin<ContactBean>> cnPinyinList;
    private ArrayList<ContactBean> checkList;
    private ItemEventListener itemEventListener;

    public ContactAdapter(Context context, List<CNPinyin<ContactBean>> cnPinyinList,
                          boolean isShowCheck, ArrayList<ContactBean> checkList,
                          ItemEventListener listener) {
        this.mContext = context;
        this.cnPinyinList = cnPinyinList;
        this.isShowCheck = isShowCheck;
        this.checkList = checkList;
        this.itemEventListener = listener;
    }


    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ContactBean contact = cnPinyinList.get(position).data;
        final ContactHolder contactHolder = (ContactHolder) holder;
        final boolean isDefaultCheck = isDefaultCheck(contact.getUuid());

        if (isShowCheck) {
            contactHolder.cb_collect.setVisibility(View.VISIBLE);

            if (isDefaultCheck) {
                contactHolder.cb_collect.setButtonDrawable(R.drawable.contact_select_def);
            } else {
                contactHolder.cb_collect.setButtonDrawable(R.drawable.contacts_select_selector);
            }

        } else {
            contactHolder.cb_collect.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(contact.getAvatar())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .transform(new GlideRoundTransform())
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header))
                .into(contactHolder.iv_header);

        contactHolder.tv_name.setText(contact.getDisplayName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowCheck) {
                    if (contactHolder.cb_collect.isChecked()) {
                        contactHolder.cb_collect.setChecked(false);
                    } else {
                        contactHolder.cb_collect.setChecked(true);
                    }
                }

                if (null != itemEventListener && !isDefaultCheck) {
                    itemEventListener.onItemClick(contact);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != itemEventListener) {
                    itemEventListener.onLongClick(contact);
                }
                return true;
            }
        });
    }


    private boolean isDefaultCheck(String uuid) {
        boolean check = false;
        if (!ListUtils.isEmpty(checkList)) {
            for (ContactBean item : checkList) {
                if (item.getUuid().equals(uuid)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public long getHeaderId(int childAdapterPosition) {
        return cnPinyinList.get(childAdapterPosition).getFirstChar();
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
        void onItemClick(ContactBean contact);

        void onLongClick(ContactBean contact);
    }

}
