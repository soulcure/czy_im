package com.youmai.hxsdk.view.refresh;

import android.support.v7.widget.RecyclerView;

/**
 * Created by youmai on 17/2/28.
 */

public interface OnRecyclerScrollListener {
    /**
     * 当RecyclerView的滚动状态被改变，会回调
     *
     * @param recyclerView 滚动状态改变的RecyclerView
     * @param newState     更新的滚动状态.
     *                     SCROLL_STATE_IDLE :静止,没有滚动,
     *                     SCROLL_STATE_DRAGGING :正在被外部拖拽,一般为用户正在用手指滚动
     *                     SCROLL_STATE_SETTLING :自动滚动开始.
     */
    void onStateChanged(RecyclerView recyclerView, int newState);

    /**
     * 当RecyclerView向上滚动，会回调
     *
     * @param recyclerView 滚动的RecyclerView
     * @param dy           垂直滚动的总值
     */

    void onScrollUp(RecyclerView recyclerView, int dy);

    /**
     * 当RecyclerView向下滚动，会回调
     *
     * @param recyclerView 滚动的RecyclerView
     * @param dy           垂直滚动的总值
     */
    void onScrollDown(RecyclerView recyclerView, int dy);

    /**
     * 当RecyclerView向上滚动到顶部，会回调
     */
    void onScrollToTop();

    /**
     * 当RecyclerView向下滚动到底部，会回调
     */
    void onScrollToBottom();
}
