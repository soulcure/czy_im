package com.youmai.hxsdk.group.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.23 19:16
 * 描述：
 */
public class GroupDetailAdapter extends RecyclerView.Adapter {

    public enum TYPE {
        DEFAULT, ADD_MEMBER, DEL_MEMBER
    }

    private Context mContext;
    private List<ContactBean> mDataList;

    private ItemEventListener itemEventListener;

    /**
     * @param context
     * @param listener
     */
    public GroupDetailAdapter(Context context, ItemEventListener listener) {
        mContext = context;
        itemEventListener = listener;
        mDataList = new ArrayList<>();

        ContactBean add = new ContactBean();
        add.setUiType(TYPE.ADD_MEMBER.ordinal());
        mDataList.add(add);

    }

    public GroupDetailAdapter(Context context, List<ContactBean> list, ItemEventListener listener) {
        mContext = context;
        itemEventListener = listener;
        mDataList = new ArrayList<>();
        mDataList.addAll(list);
    }


    public void addList(List<ContactBean> list) {
        int index = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getUiType() != TYPE.DEFAULT.ordinal()) {
                index = i;
                break;
            }
        }
        mDataList.addAll(index, list);

        notifyDataSetChanged();
    }


    public void addList(List<ContactBean> list, boolean isGroupOwner) {

        if (isGroupOwner) {
            ContactBean del = new ContactBean();
            del.setUiType(TYPE.DEL_MEMBER.ordinal());
            mDataList.add(del);
        }

        int index = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getUiType() != 0) {
                index = i;
                break;
            }
        }
        mDataList.addAll(index, list);

        notifyDataSetChanged();
    }


    public void setIsGroupOwner(boolean isGroupOwner) {
        if (isGroupOwner) {
            boolean isAdd = true;
            for (ContactBean item : mDataList) {
                if (item.getUiType() == TYPE.DEL_MEMBER.ordinal()) {
                    isAdd = false;
                    break;
                }
            }
            if (isAdd) {
                ContactBean del = new ContactBean();
                del.setUiType(TYPE.DEL_MEMBER.ordinal());
                mDataList.add(del);
            }
        } else {
            for (ContactBean item : mDataList) {
                if (item.getUiType() == TYPE.DEL_MEMBER.ordinal()) {
                    mDataList.remove(item);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void removeList(List<ContactBean> list) {
        mDataList.removeAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ContactBean bean = mDataList.get(position);
        return bean.getUiType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE.ADD_MEMBER.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hx_im_group_add_item, parent, false);
            return new AddItemHolder(view);
        } else if (viewType == TYPE.DEL_MEMBER.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hx_im_group_del_item, parent, false);
            return new DelItemHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hx_im_group_item, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ContactBean contact = mDataList.get(position);

        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.tv_name.setText(contact.getDisplayName());

            String url = contact.getAvatar();

            int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .override(size, size)
                            .transform(new GlideRoundTransform())
                            .placeholder(R.drawable.color_default_header)
                            .error(R.drawable.color_default_header))
                    .into(itemHolder.iv_header);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemEventListener) {
                    itemEventListener.onItemClick(position, contact);
                }
            }
        });
    }

    private class AddItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_add;

        AddItemHolder(View itemView) {
            super(itemView);
            iv_add = itemView.findViewById(R.id.iv_add);
        }
    }


    private class DelItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_del;

        DelItemHolder(View itemView) {
            super(itemView);
            iv_del = itemView.findViewById(R.id.iv_del);
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private ImageView iv_header;

        ItemHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_item_name);
            iv_header = itemView.findViewById(R.id.iv_item_header);
        }
    }

    public interface ItemEventListener {
        void onItemClick(int pos, ContactBean contact);
    }
}
