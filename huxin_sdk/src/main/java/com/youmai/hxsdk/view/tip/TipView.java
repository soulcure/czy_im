package com.youmai.hxsdk.view.tip;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.DisplayUtil;
import com.youmai.hxsdk.view.tip.adapter.TipAdapter;
import com.youmai.hxsdk.view.tip.bean.TipBean;
import com.youmai.hxsdk.view.tip.listener.ItemListener;
import com.youmai.hxsdk.view.tip.tools.TipsType;

import java.util.List;


/**
 * 选择提醒框
 * 设置选择框的内容{@link TipsType}
 * Created by fylder on 2017/11/21.
 */
public class TipView extends PopupWindow {

    private Context context;
    private View contentView;

    private RecyclerView recyclerView;
    private TipAdapter adapter;

    private ItemListener listener;
    private List<TipBean> tips;//选择框里的内容


    private float mRawX;
    private float mRawY;

    public TipView(Context context, List<TipBean> tips, float mRawX, float mRawY) {
        super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View contentView = LayoutInflater.from(context).inflate(R.layout.hx_tip_lay, null, false);
        setContentView(contentView);
        this.context = context;
        this.contentView = contentView;
        this.tips = tips;
        this.mRawX = mRawX;
        this.mRawY = mRawY;
        initView();
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    private void initView() {
        recyclerView = (RecyclerView) contentView.findViewById(R.id.tip_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TipAdapter(context, tips);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new TipAdapter.OnClickListener() {
            @Override
            public void onClick(String text) {

                if (listener != null) {
                    if (TipsType.TIP_DELETE.equals(text)) {
                        listener.delete();
                    } else if (TipsType.TIP_COPY.equals(text)) {
                        listener.copy();
                    } else if (TipsType.TIP_COLLECT.equals(text)) {
                        listener.collect();
                    } else if (TipsType.TIP_FORWARD.equals(text)) {
                        listener.forward();
                    } else if (TipsType.TIP_READ.equals(text)) {
                        listener.read();
                    } else if (TipsType.TIP_REMIND.equals(text)) {
                        listener.remind();
                    } else if (TipsType.TIP_TURN_TEXT.equals(text)) {
                        listener.turnText();
                    } else if (TipsType.TIP_MORE.equals(text)) {
                        listener.more();
                    } else if (TipsType.TIP_EMO_KEEP.equals(text)) {
                        listener.emoKeep();
                    }
                }
                dismiss();
            }
        });
    }

    public void show(View view) {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        update();
        int xOffset2;
        int yOffset2 = (int) mRawY - contentView.getMeasuredHeight() / 2;
        if (mRawX < DisplayUtil.getScreenWidth(context) / 4) {
            //点击的位置在屏幕左边四分之一时，显示在落点的右边
            xOffset2 = (int) mRawX;
        } else {
            xOffset2 = (int) mRawX - contentView.getMeasuredWidth();
        }
        setAnimationStyle(R.style.tip_pop_anim_style);
        showAtLocation(view, Gravity.TOP | Gravity.START, xOffset2, yOffset2);
    }


}
