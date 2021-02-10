package com.youmai.hxsdk.packet;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.red.RedPackageDetail;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.GsonUtil;

import java.util.List;
import java.util.Locale;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketDetailActivity extends SdkBaseActivity implements View.OnClickListener {

    public static final String TAG = RedPacketDetailActivity.class.getSimpleName();

    public static final String OPEN_TYPE = "openType";

    public static final String AVATAR = "avatar";
    public static final String NICKNAME = "nickName";
    public static final String VALUE = "value";
    public static final String REDTITLE = "redTitle";
    public static final String REDUUID = "redUuid";
    public static final String MSGBEAN = "msgBean";

    public static final String CANOPEN = "canOpen";
    public static final String TYPE = "type";


    public static final int SINGLE_PACKET = 0;
    public static final int GROUP_PACKET = 1;

    private Context mContext;

    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;

    private ImageView img_head;
    private TextView tv_name;
    private TextView tv_red_title;
    private TextView tv_money;

    private TextView tv_info;
    private TextView tv_status;
    private TextView tv_state;
    private RecyclerView recycler_view;
    private RedStatusAdapter adapter;

    private String avatar;
    private String name;
    private String value;
    private String title;
    private String redUuid;
    private CacheMsgBean uiBean;
    private boolean canOpen;
    private int type;

    private int openType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet_detail);
        mContext = this;
        openType = getIntent().getIntExtra(OPEN_TYPE, SINGLE_PACKET);


        avatar = getIntent().getStringExtra(AVATAR);
        name = getIntent().getStringExtra(NICKNAME);
        value = getIntent().getStringExtra(VALUE);
        title = getIntent().getStringExtra(REDTITLE);
        redUuid = getIntent().getStringExtra(REDUUID);
        uiBean = getIntent().getParcelableExtra(MSGBEAN);
        canOpen = getIntent().getBooleanExtra(CANOPEN, true);
        type = getIntent().getIntExtra(TYPE, 2);

        initView();
        loadRedPacket();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("利是详情");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("利是记录");
        tv_right.setOnClickListener(this);

        img_head = (ImageView) findViewById(R.id.img_head);
        int size = getResources().getDimensionPixelOffset(R.dimen.red_head);
        Glide.with(this).load(avatar)
                .apply(new RequestOptions()
                        .transform(new GlideRoundTransform())
                        .override(size, size)
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(img_head);


        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(name);

        if (type == 2) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_pin);
            tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }

        tv_red_title = (TextView) findViewById(R.id.tv_red_title);
        tv_red_title.setText(title);

        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_money.setText(value);
        try {
            double money = Double.parseDouble(value);
            if (money > 0) {
                tv_money.setVisibility(View.VISIBLE);
            } else {
                tv_money.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

        tv_info = (TextView) findViewById(R.id.tv_info);
        //tv_info.setText(R.string.red_packet_back);
        //tv_info.setTextColor(ContextCompat.getColor(this, R.color.gray));
        if (!canOpen) {
            tv_info.setVisibility(View.INVISIBLE);
        }

        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_state = (TextView) findViewById(R.id.tv_state);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new RedStatusAdapter(this);

        recycler_view.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(adapter);

        /*if (openType == SINGLE_PACKET) {
            tv_status.setVisibility(View.GONE);
            recycler_view.setVisibility(View.GONE);
        }*/

    }


    private void loadRedPacket() {
        HuxinSdkManager.instance().redPackageDetail(redUuid, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPackageDetail bean = GsonUtil.parse(response, RedPackageDetail.class);
                if (bean != null && bean.isSuccess()) {
                    List<RedPackageDetail.ContentBean.PacketListBean> list = bean.getContent().getPacketList();
                    adapter.setList(list);

                    String selfMoney = bean.getContent().getSelfMoneyDraw();
                    tv_money.setText(selfMoney);
                    try {
                        double money = Double.parseDouble(selfMoney);
                        if (money > 0) {
                            tv_money.setVisibility(View.VISIBLE);
                        } else {
                            tv_money.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int total = bean.getContent().getNumberTotal();
                    int draw = bean.getContent().getNumberDraw();
                    String moneyDraw = bean.getContent().getMoneyDraw();

                    double moneyTotal = bean.getContent().getMoneyTotal();
                    String moneyCount = String.format(Locale.getDefault(), "%.2f", moneyTotal);

                    String format = mContext.getResources().getString(R.string.red_status_detail);

                    tv_status.setText(String.format(format, draw, total, moneyDraw, moneyCount));

                    String url = bean.getContent().getSenderHeadImgUrl();
                    int size = getResources().getDimensionPixelOffset(R.dimen.red_head);
                    Glide.with(mContext).load(url)
                            .apply(new RequestOptions()
                                    .transform(new GlideRoundTransform())
                                    .override(size, size)
                                    .placeholder(R.drawable.color_default_header)
                                    .error(R.drawable.color_default_header)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(img_head);
                    tv_red_title.setText(bean.getContent().getBlessing());

                    type = bean.getContent().getLsType();

                    String senderName = bean.getContent().getSenderName();
                    tv_name.setText(senderName);
                    if (type == 2) {
                        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_pin);
                        tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    } else {
                        tv_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }

                    int status = bean.getContent().getStatus();
                    if (status == -1) {
                        tv_state.setVisibility(View.VISIBLE);
                        tv_state.setText("利是已过期。");
                    }

                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {
            startActivity(new Intent(this, RedPacketHistoryActivity.class));
        }
    }
}
