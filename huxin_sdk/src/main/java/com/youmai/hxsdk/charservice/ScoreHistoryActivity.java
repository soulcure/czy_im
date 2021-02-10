package com.youmai.hxsdk.charservice;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.entity.ScoreItem;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.List;

public class ScoreHistoryActivity extends SdkBaseActivity implements View.OnClickListener {

    public static final String TAG = ScoreHistoryActivity.class.getSimpleName();

    private Context mContext;

    private LinearLayout mEmptyView;
    private EmptyRecyclerViewDataObserver mEmpty = new EmptyRecyclerViewDataObserver();

    private XRecyclerView mRecyclerView;
    private ScoreHistoryAdapter mAdapter;
    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_score_history);
        mContext = this;
        initView();
        reqData();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        }
    }


    private void initView() {
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("评价记录");

        ImageView img_right = findViewById(R.id.img_right);
        img_right.setVisibility(View.INVISIBLE);

        mEmptyView = findViewById(R.id.message_empty_view);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ScoreHistoryAdapter(mContext);
        mAdapter.registerAdapterDataObserver(mEmpty);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setPullRefreshEnabled(false);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                reqData();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }


    private void reqData() {
        showProgressDialog();
        ColorsConfig.reqAccessToken(new ColorsConfig.AccessToken() {
            @Override
            public void OnSuccess(String token) {
                HuxinSdkManager.instance().reqScoreHistory(token, new IGetListener() {
                    @Override
                    public void httpReqResult(String response) {
                        ScoreItem bean = GsonUtil.parse(response, ScoreItem.class);
                        if (bean != null && bean.isSuccess()) {
                            List<ScoreItem.ContentBean> list = bean.getContent();
                            if (ListUtils.isEmpty(list)) {
                                mRecyclerView.setLoadingMoreEnabled(false);
                            } else {
                                mAdapter.setList(list);
                                mRecyclerView.setLoadingMoreEnabled(false);//接口不支持分页加载
                                page++;
                            }
                            mRecyclerView.loadMoreComplete();
                        } else {
                            mAdapter.setList(null);
                            mRecyclerView.setLoadingMoreEnabled(false);//接口不支持分页加载
                        }

                        dismissProgressDialog();
                    }
                });
            }
        });

        /*List<ScoreItem.ContentBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ScoreItem.ContentBean item = new ScoreItem.ContentBean();
            item.setName("许专家" + i);
            item.setLevel(i);
            item.setContent("很专业；响应速度快；非常赞，晚上11点联系都可以及时解决问题" + i);
            item.setTime_create("2019.04.01 11:39");
            list.add(item);
        }
        mAdapter.setList(list);
        mRecyclerView.setLoadingMoreEnabled(false);//接口不支持分页加载*/

    }

    private class EmptyRecyclerViewDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    }

    private void checkIfEmpty() {
        if (mAdapter != null) {
            int count = mAdapter.getItemCount();
            //正常状态
            if (count == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }


}
