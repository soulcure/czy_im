package com.youmai.hxsdk.packet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.red.RedPackageDetail;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.List;


public class RedStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RedPackageDetail.ContentBean.PacketListBean> mList;

    public RedStatusAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RedPackageDetail.ContentBean.PacketListBean> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.hx_red_item, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RedPackageDetail.ContentBean.PacketListBean item = mList.get(position);

        ImageView img_head = ((TextViewHolder) viewHolder).img_head;
        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        TextView tv_money = ((TextViewHolder) viewHolder).tv_money;
        TextView tv_best = ((TextViewHolder) viewHolder).tv_best;

        double money = item.getReceiveMoney();
        String name = item.getReceiverNickname();
        String url = item.getReceiverHeadImgUrl();
        String time = item.getReceiveTime();

        if (item.getIsBest() == 1) {
            tv_best.setVisibility(View.VISIBLE);
        } else {
            tv_best.setVisibility(View.INVISIBLE);
        }

        int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(new GlideRoundTransform())
                        .override(size, size)
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header))
                .into(img_head);
        tv_name.setText(name);
        tv_time.setText(time);

        //String format = mContext.getResources().getString(R.string.red_packet_unit2);
        //tv_money.setText(String.format(format, String.valueOf(money)));
        tv_money.setText(String.valueOf(money));

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_name;
        TextView tv_time;
        TextView tv_money;
        TextView tv_best;

        private TextViewHolder(View itemView) {
            super(itemView);
            img_head = (ImageView) itemView.findViewById(R.id.img_head);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_money = (TextView) itemView.findViewById(R.id.tv_money);
            tv_best = (TextView) itemView.findViewById(R.id.tv_best);
        }

    }


}

