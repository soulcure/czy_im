package com.youmai.hxsdk.charservice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.ScoreItem;
import com.youmai.hxsdk.utils.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ScoreHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ScoreItem.ContentBean> mList;

    public ScoreHistoryAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }


    public void setList(List<ScoreItem.ContentBean> list) {
        if (list != null) {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_score, parent, false);
        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final ScoreItem.ContentBean item = mList.get(position);

        ImageView img_head = ((TextViewHolder) viewHolder).img_head;
        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        TextView tv_content = ((TextViewHolder) viewHolder).tv_content;
        SimpleRatingBar ratingBar = ((TextViewHolder) viewHolder).ratingBar;

        String url = HuxinSdkManager.instance().getServiceAvatar();
        int size = mContext.getResources().getDimensionPixelOffset(R.dimen.red_head);
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .circleCrop()
                        .override(size, size)
                        .placeholder(R.drawable.ic_service_header)
                        .error(R.drawable.ic_service_header))
                .into(img_head);


        tv_name.setText(item.getName());
        String time = item.getTime_create();
        try {
            Calendar calendar = TimeUtils.parseDate(time, TimeUtils.DEFAULT_DATE_FORMAT);
            String newTime = TimeUtils.MINUTE_FORMAT_DATE.format(calendar.getTime());
            tv_time.setText(newTime);
        } catch (ParseException e) {
            tv_time.setText(time);
        }

        tv_content.setText(item.getContent());
        ratingBar.setRating(item.getLevel());

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        ImageView img_head;
        TextView tv_name;
        TextView tv_time;
        TextView tv_content;
        SimpleRatingBar ratingBar;


        private TextViewHolder(View itemView) {
            super(itemView);
            img_head = itemView.findViewById(R.id.img_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

    }


}

