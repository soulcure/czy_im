package com.color.czy.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.color.czy.R;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.charservice.ServiceMsgNotifyActivity;
import com.youmai.hxsdk.fragment.ContactsFragment;
import com.color.czy.fragment.EmptyFragment;
import com.color.czy.fragment.MsgListFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.youmai.hxsdk.fragment.GroupListFragment;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.utils.AppUtils;

import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView img_scan;
    private TextView tv_title;
    private ImageView img_add;

    private ViewPager mViewPager;
    private BottomNavigationViewEx navigation;
    //private TabLayout mTabLayout;
    private QBadgeView badgeView;
    private QBadgeView badgeServiceView;

    private TabFragmentPagerAdapter mAdapter;
    private FragmentManager fragmentManager;


    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initTitle();
        initView();
        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshUnReadCount();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
    }


    public void refreshUnReadCount() {
        int unreadCount = HuxinSdkManager.instance().unreadBuddyAndCommMessage();
        if (unreadCount > 0) {
            badgeView.setBadgeNumber(unreadCount);
        } else {
            badgeView.hide(true);
        }

        int unreadServiceCount = HuxinSdkManager.instance().unreadServiceManagerMessage();
        if (unreadServiceCount > 0) {
            badgeServiceView.setBadgeNumber(unreadServiceCount);
        } else {
            badgeServiceView.hide(true);
        }

    }

    private void initTitle() {

    }


    private void initView() {
        img_scan = (ImageView) findViewById(R.id.img_scan);
        tv_title = (TextView) findViewById(R.id.tv_title);
        img_add = (ImageView) findViewById(R.id.img_add);

        img_scan.setOnClickListener(this);
        img_add.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);

        badgeView = new QBadgeView(mContext);
        badgeView.bindTarget(navigation.getBottomNavigationItemView(0));
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.END);
        badgeView.setBadgeTextSize(10f, true);
        badgeView.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
        badgeView.setGravityOffset(25, 2, true);
        badgeView.setBadgePadding(5, true);
        badgeView.setShowShadow(false);
        badgeView.hide(false);

        badgeServiceView = new QBadgeView(mContext);
        badgeServiceView.bindTarget(img_add);
        badgeServiceView.setBadgeGravity(Gravity.BOTTOM | Gravity.END);
        badgeServiceView.setBadgeTextSize(8f, true);
        badgeServiceView.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
        badgeServiceView.setGravityOffset(0, 0, true);
        badgeServiceView.setBadgePadding(1, true);
        badgeServiceView.setShowShadow(false);
        badgeServiceView.hide(false);


        //mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        fragmentManager = getSupportFragmentManager();
        mAdapter = new TabFragmentPagerAdapter(fragmentManager, navigation.getItemCount());

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText(navigation.getMenu().getItem(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigation.enableShiftingMode(false); //取消切换动画
        navigation.enableItemShiftingMode(false); //取消文字
        navigation.enableAnimation(false);  //取消选中动画
        navigation.setupWithViewPager(mViewPager);

        navigation.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        if (AppUtils.isRepeatClick()) {
                            if (mAdapter.getItem(0) instanceof MsgListFragment) {
                                MsgListFragment fragment = (MsgListFragment) mAdapter.instantiateItem(mViewPager, 0);
                                fragment.scrollToNextUnRead();
                            }
                        }
                    }
                });

        mViewPager.setCurrentItem(0);
        tv_title.setText(navigation.getMenu().getItem(0).getTitle());

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_scan:
                Toast.makeText(this, "扫一扫", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_add:
                HuxinSdkManager.instance().entryServiceManager(this);
                break;

        }
    }


    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private int mCount;

        private TabFragmentPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            mCount = count;
        }


        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;
            switch (arg0) {
                case 0:
                    ft = new MsgListFragment();
                    break;
                case 1:
                    ft = ContactsFragment.newInstance(false);
                    break;
                case 2:
                    ft = GroupListFragment.newInstance(YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT);
                    break;
                case 3:
                    ft = GroupListFragment.newInstance(YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY);
                    break;
                case 4:
                    ft = new EmptyFragment();
                    break;
                default:
                    break;
            }

            return ft;
        }


        @Override
        public int getCount() {
            return mCount;
        }

    }


}
