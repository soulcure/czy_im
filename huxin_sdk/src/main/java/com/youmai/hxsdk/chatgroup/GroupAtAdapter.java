package com.youmai.hxsdk.chatgroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.PinYinUtils;

import java.util.List;

/**
 * Created by yw on 2018/4/13.
 */
public class GroupAtAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ItemEventListener itemEventListener;
    private List<SearchContactBean> cnPinyinList;

    public interface ItemEventListener {
        void onItemClick(SearchContactBean contact);
    }

    public GroupAtAdapter(Context context, List<SearchContactBean> cnPinyinList, ItemEventListener listener) {
        this.mContext = context.getApplicationContext();
        this.cnPinyinList = cnPinyinList;
        this.itemEventListener = listener;
    }

    @Override
    public int getItemCount() {
        return cnPinyinList == null ? 0 : cnPinyinList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final SearchContactBean model = cnPinyinList.get(position);

        final ContactHolder contactHolder = (ContactHolder) holder;


        contactHolder.cb_collect.setVisibility(View.GONE);
        contactHolder.tv_name.setText(model.getDisplayName());

        try {
            int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);
            Glide.with(mContext)
                    .load(model.getIconUrl())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .transform(new GlideRoundTransform())
                            .override(size, size)
                            .placeholder(R.drawable.color_default_header)
                            .error(R.drawable.color_default_header))
                    .into(contactHolder.iv_header);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String queryString = model.getSearchKey();
        int highlightColor = ContextCompat.getColor(mContext, R.color.hxs_color_green3);
        switch (model.getSearchType()) {
            case SearchContactBean.SEARCH_TYPE_NUMBER:
                //setTVColor(model.getPhoneNum(), queryString, contactHolder.search_info);
                break;
            case SearchContactBean.SEARCH_TYPE_INFO:
                //setTVColor(model.getInfo(), queryString, contactHolder.search_info);
                break;
            case SearchContactBean.SEARCH_TYPE_NAME:
                setTVColor(model.getDisplayName(), queryString, contactHolder.tv_name);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_SPELL:
                if (queryString.matches("[a-zA-Z]+")) {
                    if (model.getSimplepinyin().contains(queryString)) {
                        int a = model.getSimplepinyin().indexOf(queryString);
                        int b = a + queryString.length();
                        SpannableStringBuilder builder = new SpannableStringBuilder(model.getDisplayName());
                        builder.setSpan(new ForegroundColorSpan(highlightColor),
                                a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        contactHolder.tv_name.setText(builder);
                    }
                }
                break;
            //全拼的高亮暂不支持
            case SearchContactBean.SEARCH_TYPE_WHOLE_SPECL:
                int[] findIndex = model.getWholePinYinFindIndex();

                String displayName = model.getDisplayName();
                SpannableStringBuilder builder = new SpannableStringBuilder(displayName);

                List<Integer> integers = PinYinUtils.match2(displayName, queryString);
                int start = 0;
                int end = 0;

                if (!ListUtils.isEmpty(integers)) {
                    start = integers.get(0);
                    end = integers.get(integers.size() - 1);
                }

                builder.setSpan(new ForegroundColorSpan(highlightColor),
                        start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                contactHolder.tv_name.setText(builder);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_T9:
                if (model.getSimpleT9().contains(queryString)) {
                    int a = model.getSimpleT9().indexOf(queryString);
                    int b = a + queryString.length();
                    SpannableStringBuilder build = new SpannableStringBuilder(model.getDisplayName());
                    build.setSpan(new ForegroundColorSpan(highlightColor),
                            a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    contactHolder.tv_name.setText(build);
                }
                break;
            default:
                break;
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemEventListener) {
                    itemEventListener.onItemClick(model);
                }
            }
        });

    }

    private void setTVColor(String str, String subString, TextView tv) {
        int highlightColor = ContextCompat.getColor(mContext, R.color.hxs_color_green3);
        int a = str.indexOf(subString);
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        if (a != -1) {
            int b = a + subString.length();
            builder.setSpan(new ForegroundColorSpan(highlightColor),
                    a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(builder);
    }


    class ContactHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        ContactHolder(View itemView) {
            super(itemView);
            iv_header = itemView.findViewById(R.id.iv_header);
            tv_name = itemView.findViewById(R.id.tv_name);
            cb_collect = itemView.findViewById(R.id.cb_collect);
        }
    }


}
