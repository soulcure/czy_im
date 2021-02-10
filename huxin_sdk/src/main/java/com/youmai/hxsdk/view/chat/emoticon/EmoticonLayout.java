package com.youmai.hxsdk.view.chat.emoticon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonBean;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonsKeyboardBuilder;
import com.youmai.hxsdk.view.chat.emoticon.utils.IView;
import com.youmai.hxsdk.view.chat.emoticon.view.EmoticonsPageView;
import com.youmai.hxsdk.view.chat.emoticon.view.EmoticonsToolBarView;
import com.youmai.hxsdk.view.chat.emoticon.view.IndicatorView;


/**
 * EmoticonLayout
 * Created by 90Chris on 2015/11/19.
 */
public class EmoticonLayout extends RelativeLayout implements EmoticonsPageView.OnEmoticonsPageViewListener {
    Context mContext;
    EmoticonsPageView epvContent;
    IndicatorView ivIndicator;
    //EmoticonsToolBarView etvToolBar;

    private int initPage = 0;

    public EmoticonLayout(Context context) {
        super(context);
        mContext = context;
        init(mContext);
    }

    public EmoticonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(mContext);
    }

    public EmoticonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(mContext);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.keyboard_bottom_emoticons, this);
        ivIndicator = (IndicatorView) findViewById(R.id.emoticon_indicator_view);
        epvContent = (EmoticonsPageView) findViewById(R.id.emoticon_page_view);
        //etvToolBar = (EmoticonsToolBarView) findViewById(R.id.emoticon_page_toolbar);
        epvContent.setOnIndicatorListener(this);
        epvContent.setIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                mListener.onEmoticonItemClicked(bean);
            }

            @Override
            public void onPageChangeTo(int position) {
                //etvToolBar.setToolBtnSelect(position);
            }
        });
        /*etvToolBar.setOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(int position) {
                epvContent.setPageSelect(position);
            }
        });*/
    }

    OnEmoticonListener mListener = null;

    public interface OnEmoticonListener {
        void onEmoticonItemClicked(EmoticonBean bean);
    }

    @Override
    public void emoticonsPageViewCountChanged(int count) {
        ivIndicator.setIndicatorCount(count);
    }

    @Override
    public void moveTo(int position) {
        ivIndicator.moveTo(position);
    }

    @Override
    public void moveBy(int oldPosition, int newPosition) {
        ivIndicator.moveTo(newPosition);
    }

    /*public void addToolView(int icon) {
        if (etvToolBar != null && icon > 0) {
            etvToolBar.addData(icon);
        }
    }

    public void addFixedView(View view, boolean isRight) {
        if (etvToolBar != null) {
            etvToolBar.addFixedView(view, isRight);
        }
    }*/

    /**
     * 设置监听事件
     */
    public void setListener(OnEmoticonListener listener) {
        mListener = listener;
    }

    /**
     * @param builder  表情数据
     * @param initPage 初始化页面
     */
    public void setContents(EmoticonsKeyboardBuilder builder, int initPage) {
        epvContent.setEmoticonContents(builder, initPage);
        //etvToolBar.setEmoticonContents(builder, initPage);

        //初始化指示标
        if (builder.builder.getEmoticonSetBeanList() != null && builder.builder.getEmoticonSetBeanList().size() > 0) {
            ivIndicator.setIndicatorCount(epvContent.getPageCount(builder.builder.getEmoticonSetBeanList().get(initPage)));
            ivIndicator.moveTo(0);
        }
    }

    /**
     * @param builder 表情数据
     */
    public void refreshContents(EmoticonsKeyboardBuilder builder) {
        epvContent.setEmoticonContents(builder);
        epvContent.refreshView();
    }

    /**
     * @param color 设置背景颜色
     */
    public void setEmoticonPageBg(int color) {
        epvContent.setBackgroundColor(color);
        ivIndicator.setBackgroundColor(color);
    }
}
