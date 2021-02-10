
package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.packet.RedPacketHistoryActivity;
import com.youmai.hxsdk.packet.SendRedPackageRecordAdapter;
import com.youmai.hxsdk.entity.red.SendRedPacketList;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.List;


public class PacketSendHistoryFragment extends Fragment {
    private static final String TAG = PacketSendHistoryFragment.class.getSimpleName();


    private XRecyclerView mRecyclerView;
    private SendRedPackageRecordAdapter mAdapter;

    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_package_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        reqDate();
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SendRedPackageRecordAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setPullRefreshEnabled(false);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                reqDate();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }


    private void reqDate() {
        if (getActivity() instanceof RedPacketHistoryActivity) {
            RedPacketHistoryActivity act = (RedPacketHistoryActivity) getActivity();
            String time = act.getDate();
            HuxinSdkManager.instance().redSendPacketList(time, page, new IGetListener() {
                @Override
                public void httpReqResult(String response) {
                    SendRedPacketList bean = GsonUtil.parse(response, SendRedPacketList.class);
                    if (bean != null && bean.isSuccess()) {
                        List<SendRedPacketList.ContentBean> list = bean.getContent();
                        if (ListUtils.isEmpty(list)) {
                            mRecyclerView.setLoadingMoreEnabled(false);
                        } else {
                            mAdapter.setList(list);
                            page++;
                        }
                        mRecyclerView.loadMoreComplete();
                    }

                }
            });
        }
    }


}
