package com.youmai.hxsdk.packet;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.entity.red.RedPacketHistoryDetail;
import com.youmai.hxsdk.fragment.PacketReceiveHistoryFragment;
import com.youmai.hxsdk.fragment.PacketSendHistoryFragment;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.TimeUtils;

import java.text.ParseException;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketHistoryActivity extends SdkBaseActivity implements View.OnClickListener, OnDateSetListener {

    public static final String TAG = RedPacketHistoryActivity.class.getSimpleName();

    private String date;

    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;

    private ImageView img_head;
    private TextView tv_name;
    private TextView tv_red_title;
    private TextView tv_money;

    private TextView tv_info;
    private TextView tv_status;


    private TextView tv_send_money;
    private TextView tv_send_count;

    private TextView tv_receive_money;
    private TextView tv_receive_count;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet_history);
        date = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.YEAR_MONTH_FORMAT);
        initView();
        setupViewPager();
        loadRedPacket();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("利是记录");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText(date);


        img_head = (ImageView) findViewById(R.id.img_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_red_title = (TextView) findViewById(R.id.tv_red_title);
        tv_money = (TextView) findViewById(R.id.tv_money);

        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_status = (TextView) findViewById(R.id.tv_status);

        tv_send_money = (TextView) findViewById(R.id.tv_send_money);
        tv_send_count = (TextView) findViewById(R.id.tv_send_count);

        tv_receive_money = (TextView) findViewById(R.id.tv_receive_money);
        tv_receive_count = (TextView) findViewById(R.id.tv_receive_count);

        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);

    }


    private void loadRedPacket() {
        HuxinSdkManager.instance().redReceivePacketDetail(date, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPacketHistoryDetail bean = GsonUtil.parse(response, RedPacketHistoryDetail.class);
                if (bean != null && bean.isSuccess()) {
                    String count = bean.getContent().getNumberTotal();

                    String format1 = getResources().getString(R.string.receive_red_packet_count);
                    tv_send_count.setText(String.format(format1, count));

                    double total = bean.getContent().getMoneyTotal();
                    //String format2 = getResources().getString(R.string.red_packet_unit2);
                    //tv_send_money.setText(String.format(format2, String.valueOf(total)));
                    tv_send_money.setText(String.valueOf(total));
                }
            }
        });

        HuxinSdkManager.instance().redSendPacketDetail(date, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPacketHistoryDetail bean = GsonUtil.parse(response, RedPacketHistoryDetail.class);
                if (bean != null && bean.isSuccess()) {
                    String count = bean.getContent().getNumberTotal();

                    String format1 = getResources().getString(R.string.send_red_packet_count);
                    tv_receive_count.setText(String.format(format1, count));

                    double total = bean.getContent().getMoneyTotal();
                    //String format2 = getResources().getString(R.string.red_packet_unit2);
                    //tv_receive_money.setText(String.format(format2, String.valueOf(total)));
                    tv_receive_money.setText(String.valueOf(total));
                }
            }
        });


    }


    private void setupViewPager() {
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void datePickerDialog() {
        long tenDays = 10L * 1000 * 60 * 60 * 24;
        long twoYears = 2L * 365 * 1000 * 60 * 60 * 24;
        long curTime = System.currentTimeMillis();
        long minTime = System.currentTimeMillis();
        try {
            minTime = TimeUtils.parseDate("201806", TimeUtils.YEAR_MONTH_FORMAT).getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TimePickerDialog dialog = new TimePickerDialog.Builder()
                .setType(Type.YEAR_MONTH)
                .setCallBack(this)
                .setWheelItemTextSize(14)
                .setTitleStringId("请选择日期")
                .setCurrentMillseconds(curTime)
                .setMinMillseconds(minTime - tenDays)
                .setMaxMillseconds(curTime + twoYears)
                .setThemeColor(ContextCompat.getColor(this, R.color.red_package_colorPrimary))
                .setWheelItemTextNormalColor(ContextCompat.getColor(this, R.color.hxs_color_gray))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(this, R.color.red_package_colorAccent))
                .build();
        dialog.show(getSupportFragmentManager(), "year_month");

    }


    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String time = TimeUtils.getTime(millseconds, TimeUtils.YEAR_MONTH_FORMAT);
        if (!date.equals(time)) {
            date = time;
            tv_right.setText(date);
            setupViewPager();
            loadRedPacket();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {
            datePickerDialog();
        }
    }

    public String getDate() {
        return date;
    }

    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitle;


        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabTitle = getResources().getStringArray(R.array.red_title);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;

            switch (arg0) {
                case 0:
                    ft = new PacketReceiveHistoryFragment();
                    break;
                case 1:
                    ft = new PacketSendHistoryFragment();
                    break;
                default:
                    break;
            }
            return ft;
        }


        @Override
        public int getCount() {
            return tabTitle.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

    }
}
