package com.youmai.hxsdk.view.text;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.view.tip.TipView;
import com.youmai.hxsdk.view.tip.bean.TipBean;
import com.youmai.hxsdk.view.tip.listener.ItemListener;
import com.youmai.hxsdk.view.tip.tools.TipsType;

import java.util.List;

/**
 * Created by fylder on 2017/3/7.
 */

public class CopeTextView extends AppCompatTextView {

    Context mContext;

    private int position;

    OnCopeListener onClickFirst;
    private boolean mIsLongClick = false;

    private boolean canShow = true;
    private boolean deleteShow = true;

    private TipView tipView;
    public float mRawX;
    public float mRawY;

    public CopeTextView(Context context) {
        super(context);
    }

    public CopeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initAttrs(attrs);
        init();
    }

    public CopeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CopeTextView);
        position = typedArray.getInteger(R.styleable.CopeTextView_position, 0);
        typedArray.recycle();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.GONE) {
            if (tipView != null && tipView.isShowing()) {
                tipView.dismiss();
            }
        }
    }

    private void init() {
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (canShow) {
                    mIsLongClick = true;
                    List<TipBean> tips;
                    if (position == 0) {
                        tips = TipsType.getTextType();
                    } else {
                        tips = TipsType.getMyselfTextType();
                    }
                    tipView = new TipView(mContext, tips, mRawX, mRawY);
                    tipView.setListener(new ItemListener() {
                        @Override
                        public void delete() {
                            if (onClickFirst != null) {
                                onClickFirst.delete();//回调任务
                            }
                        }

                        @Override
                        public void copy() {
                            ClipboardManager cpb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cpb != null) {
                                cpb.setPrimaryClip(ClipData.newPlainText(null, getText().toString()));//加入剪贴板
                            }
                            if (onClickFirst != null) {
                                onClickFirst.copeText();
                            }
                        }

                        @Override
                        public void collect() {
                            if (onClickFirst != null) {
                                onClickFirst.collect();
                            }
                        }

                        @Override
                        public void forward() {
                            if (onClickFirst != null) {
                                onClickFirst.forwardText(getText());//回调任务
                            }
                        }

                        @Override
                        public void read() {
                            if (onClickFirst != null) {
                                onClickFirst.read();
                            }
                        }

                        @Override
                        public void remind() {
                            if (onClickFirst != null) {
                                onClickFirst.remind();//回调任务
                            }
                        }

                        @Override
                        public void turnText() {
                            Toast.makeText(mContext, "click:转文字", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void more() {
                            if (onClickFirst != null) {
                                onClickFirst.more();//回调任务
                            }
                        }
                    });
                    tipView.show(CopeTextView.this);

                }
                return true;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRawX = event.getRawX();
                mRawY = event.getRawY();
                if (canShow) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        return mIsLongClick;
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mIsLongClick = false;
                        return false;
                    }
                }
                return false;
            }
        });
    }


    public void setOnClickLis(OnCopeListener l) {
        this.onClickFirst = l;
    }


    public interface OnCopeListener {

        void copeText();

        void forwardText(CharSequence str);

        void collect();

        void read();

        void remind();

        void delete();

        void more();

    }

}