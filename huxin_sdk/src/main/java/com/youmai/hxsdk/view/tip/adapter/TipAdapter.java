package com.youmai.hxsdk.view.tip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.view.tip.bean.TipBean;

import java.util.List;


/**
 * Created by fylder on 2017/11/21.
 */

public class TipAdapter extends RecyclerView.Adapter {

    Context context;
    List<TipBean> tips;

    OnClickListener listener;

    public TipAdapter(Context context, List<TipBean> tips) {
        this.context = context;
        this.tips = tips;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.hx_item_tip_lay, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TipViewHolder viewHolder = (TipViewHolder) holder;
        final TipBean data = tips.get(position);
        viewHolder.tipText.setText(data.getName());
        viewHolder.tipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(data.getType());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tips == null ? 0 : tips.size();
    }

    class TipViewHolder extends RecyclerView.ViewHolder {

        TextView tipText;

        public TipViewHolder(View itemView) {
            super(itemView);
            tipText = (TextView) itemView.findViewById(R.id.item_tip_text);
        }
    }

    public interface OnClickListener {
        void onClick(String text);
    }
}
