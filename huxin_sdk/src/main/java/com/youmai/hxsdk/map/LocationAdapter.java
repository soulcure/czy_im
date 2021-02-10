package com.youmai.hxsdk.map;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.youmai.hxsdk.R;

import java.util.List;


public class LocationAdapter extends ArrayAdapter<SearchTips> {

    private Context mContext;
    private int resId;
    private String keyword;

    public LocationAdapter(Context context, int resId, List<SearchTips> list) {
        super(context, resId, list);
        mContext = context;
        this.resId = resId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchTips SearchTips = getItem(position);
        ViewHolder holder;

        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(resId, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);

        String name = SearchTips.getName();
        String address = SearchTips.getAddress();

        setTextColor(keyword, name, holder.tv_name);
        setTextColor(keyword, address, holder.tv_address);

        return convertView;
    }


    private void setTextColor(String keyword, String content, TextView view) {
        if (TextUtils.isEmpty(keyword) || TextUtils.isEmpty(content)) {
            view.setText(content);
        } else {
            int start = content.indexOf(keyword);
            if (start == -1) {
                view.setText(content);
            } else {
                int length = keyword.length();
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.hxs_color_blue1)),
                        start, start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                view.setText(style);
            }
        }

    }


    private class ViewHolder {
        TextView tv_name;
        TextView tv_address;
    }

}
