package com.youmai.hxsdk.group;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.14 14:53
 * 描述：
 */
public class DeleteContactAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ContactBean> mGroupList;

    private Map<String, ContactBean> mGroupMap = new HashMap<>();

    public DeleteContactAdapter(Context context) {
        this.mContext = context;
    }

    public void setGroupList(List<ContactBean> list) {
        this.mGroupList = list;
        notifyDataSetChanged();
    }

    public void deleteMessage(int position) {
        mGroupList.remove(position);
        notifyDataSetChanged();
    }

    public void setGroupMap(Map<String, ContactBean> map) {
        this.mGroupMap = map;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mGroupList == null ? 0 : mGroupList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.group_delete_item_layout, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final GroupViewHolder itemHolder = (GroupViewHolder) holder;
        final ContactBean contact = mGroupList.get(position);

        itemHolder.tv_name.setText(contact.getDisplayName());
        itemHolder.cb_collect.setButtonDrawable(R.drawable.contacts_select_selector);
        if (mGroupMap.get(contact.getUuid()) != null) {
            itemHolder.cb_collect.setChecked(true);
        } else {
            itemHolder.cb_collect.setChecked(false);
        }

        String url = contact.getAvatar();
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(60, 60)
                        .transform(new GlideRoundTransform())
                        .placeholder(com.youmai.hxsdk.R.drawable.color_default_header)
                        .error(com.youmai.hxsdk.R.drawable.color_default_header))
                .into(itemHolder.iv_header);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, contact);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mGroupMap && null == mGroupMap.get(contact.getUuid())) {
                    mGroupMap.put(contact.getUuid(), contact);
                    itemHolder.cb_collect.setChecked(true);
                } else {
                    mGroupMap.remove(contact.getUuid());
                    itemHolder.cb_collect.setChecked(false);
                }
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(position, contact);
                }
            }
        });

    }

    protected class GroupViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_collect;
        ImageView iv_header;
        TextView tv_name;

        public GroupViewHolder(View itemView) {
            super(itemView);
            cb_collect = itemView.findViewById(R.id.cb_collect);
            iv_header = itemView.findViewById(R.id.iv_contact_header);
            tv_name = itemView.findViewById(R.id.tv_contact_name);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ContactBean bean);
    }

}
