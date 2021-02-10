package com.youmai.hxsdk.packet;

import android.content.Context;
import android.content.Intent;
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
import com.youmai.hxsdk.entity.red.SendRedPacketList;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;


public class SendRedPackageRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SendRedPacketList.ContentBean> mList;

    public SendRedPackageRecordAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }


    public void setList(List<SendRedPacketList.ContentBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_send_history, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final SendRedPacketList.ContentBean item = mList.get(position);

        ImageView img_head = ((TextViewHolder) viewHolder).img_head;
        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        TextView tv_money = ((TextViewHolder) viewHolder).tv_money;
        TextView tv_best = ((TextViewHolder) viewHolder).tv_best;

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, RedPacketDetailActivity.class);
                in.putExtra(RedPacketDetailActivity.OPEN_TYPE, RedPacketDetailActivity.GROUP_PACKET);
                in.putExtra(RedPacketDetailActivity.REDUUID, item.getUuid());
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(in);
            }
        });

        String avatar = item.getSenderHeadImgUrl();
        String sendName = item.getSenderName();
        String time = item.getTimeAllowWithdraw();
        double money = item.getMoneyTotal();
        int type = item.getLsType();
        int status = item.getStatus();

        if (type == 2) {
            img_head.setImageResource(R.drawable.ic_random);
            tv_name.setText(R.string.type_pin);
        } else {
            if (item.getNumberTotal() > 1) {
                img_head.setImageResource(R.drawable.ic_fix);
                tv_name.setText(R.string.type_fix);
            } else {
                img_head.setImageResource(R.drawable.ic_single);
                tv_name.setText(R.string.type_single);
            }
        }

        if (status == 4) {
            String format = mContext.getResources().getString(R.string.red_status_done);
            tv_best.setText(String.format(format, item.getNumberDraw(), item.getNumberTotal()));
        } else if (status == -1) {
            String format = mContext.getResources().getString(R.string.red_status_overdue);
            tv_best.setText(String.format(format, item.getNumberDraw(), item.getNumberTotal()));
        } else {
            String format = mContext.getResources().getString(R.string.red_status);
            tv_best.setText(String.format(format, item.getNumberDraw(), item.getNumberTotal()));
        }

        tv_time.setText(time);

        //String format2 = mContext.getResources().getString(R.string.red_packet_unit2);
        //tv_money.setText(String.format(format2, String.valueOf(money)));
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

