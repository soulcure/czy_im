package com.youmai.hxsdk.chatgroup;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.loader.SearchLoader;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者：create by YW
 * 日期：2018.04.19 18:24
 * 描述：群主转让列表
 */
public class GroupAtSelectActivity extends AppCompatActivity implements
        GroupAtAdapter.ItemEventListener {

    private static final String TAG = GroupAtSelectActivity.class.getName();


    private final int GLOBAL_SEARCH_LOADER_ID = 1;

    private Context mContext;
    private GroupAtAdapter mAdapter;
    private GroupInfoBean mGroupInfo;
    private int mGroupId;

    private SearchView searchView;

    private SearchLoader mLoader;

    private ArrayList<SearchContactBean> resultList = new ArrayList<>();

    private List<ContactBean> groupList = new ArrayList<>();

    private LoaderManager.LoaderCallbacks<List<SearchContactBean>> callback = new LoaderManager.LoaderCallbacks<List<SearchContactBean>>() {
        @NonNull
        @Override
        public Loader<List<SearchContactBean>> onCreateLoader(int id, @Nullable Bundle args) {
            Log.d(TAG, "onCreateLoader");
            mLoader = new SearchLoader(mContext, groupList);
            return mLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<SearchContactBean>> loader, List<SearchContactBean> data) {
            resultList.clear();
            resultList.addAll(data);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<SearchContactBean>> loader) {
            resultList.clear();
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_group_at_layout);
        mContext = this;
        mGroupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);
        mGroupId = getIntent().getIntExtra(IMGroupActivity.GROUP_ID, -1);
        groupList = getIntent().getParcelableArrayListExtra(IMGroupActivity.GROUP_MEMBER);

        if (null == mGroupInfo) {
            mGroupInfo = GroupInfoHelper.instance().toQueryByGroupId(this, mGroupId);
        }

        initTitle();
        initView();

        if (ListUtils.isEmpty(groupList) && mGroupId > 0) {
            reqGroupMembers(mGroupId);
        } else {
            for (ContactBean item : groupList) {
                if (item.getUuid().equals(HuxinSdkManager.instance().getUuid())) {
                    groupList.remove(item);
                    break;
                }
            }

            getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
        }

    }

    private void initTitle() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("选择提醒的人");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.hx_color_black));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        mAdapter = new GroupAtAdapter(this, resultList, this);
        recyclerView.setAdapter(mAdapter);

    }


    private void reqGroupMembers(int groupId) {
        groupList.clear();
        HuxinSdkManager.instance().reqGroupMember(groupId, new ProtoCallback.ContactListener() {
            @Override
            public void result(List<ContactBean> list) {
                //groupList.addAll(list);
                for (ContactBean item : list) {
                    if (!item.getUuid().equals(HuxinSdkManager.instance().getUuid())) {
                        groupList.add(item);
                    }
                }
                getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint("搜索");
            searchView.setIconifiedByDefault(false);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mLoader.setQuery(newText);
                    mLoader.forceLoad();
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();
        if (id == R.id.action_search) {
            return false;
        }*/
        return super.onOptionsItemSelected(item);
    }


    /**
     * item点击
     *
     * @param contact
     */
    @Override
    public void onItemClick(SearchContactBean contact) {
        hideSoftKey();
        Intent intent = new Intent();
        intent.putExtra("contact", contact);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void hideSoftKey() {
        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
