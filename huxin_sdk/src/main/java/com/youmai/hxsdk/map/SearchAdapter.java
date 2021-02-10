package com.youmai.hxsdk.map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.youmai.hxsdk.R;

import java.util.ArrayList;
import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PoiItem> mList;

    private CompoundButton mCheckBox;  //保存上一次处于checked状态的控件
    private int mPosition = 0;  //保存上一次处于checked状态的控件位置

    public SearchAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<PoiItem> list) {
        mList.clear();
        mPosition = -1;
        mList = list;
        notifyDataSetChanged();
    }


    public PoiItem getSelectPoiItem() {
        if (mPosition >= 0 && mPosition < mList.size()) {
            return mList.get(mPosition);
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.hx_area_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cbSelect = (CheckBox) v.findViewById(R.id.checkbox);
                cbSelect.setChecked(true);
            }
        });
        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        PoiItem item = mList.get(position);
        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        TextView tv_snippet = ((TextViewHolder) viewHolder).tv_snippet;

        final String title = item.getTitle();
        String snippet = item.getSnippet();

        tv_title.setText(title);
        tv_snippet.setText(snippet);

        CheckBox cbSelect = ((TextViewHolder) viewHolder).cbSelect;
        cbSelect.setTag(position);

        if (mPosition == position) {
            cbSelect.setChecked(true);
        } else {
            cbSelect.setChecked(false);
        }
    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_snippet;
        CheckBox cbSelect;

        private TextViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_snippet = (TextView) itemView.findViewById(R.id.tv_snippet);
            cbSelect = (CheckBox) itemView.findViewById(R.id.checkbox);
            cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        mPosition = (int) buttonView.getTag();
                        if (mCheckBox != null && !mCheckBox.equals(buttonView)) {
                            mCheckBox.setChecked(false);
                        }
                        mCheckBox = buttonView;
                        if (mContext instanceof LocationActivity) {
                            LocationActivity act = (LocationActivity) mContext;
                            act.moveMarker(getSelectPoiItem());
                        }

                    }

                }
            });
        }

    }


}

