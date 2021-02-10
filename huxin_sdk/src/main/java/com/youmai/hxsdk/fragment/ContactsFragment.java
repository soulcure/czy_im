package com.youmai.hxsdk.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.ContactAdapter;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.group.DeleteContactListActivity;
import com.youmai.hxsdk.stickyheader.StickyHeaderDecoration;
import com.youmai.hxsdk.widget.CharIndexView;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：create by YW
 * 日期：2018.04.03 11:59
 * 描述：
 */
public class ContactsFragment extends Fragment implements ContactAdapter.ItemEventListener {

    public static final String TAG = ContactsFragment.class.getName();

    public static final String SHOW_CHECK = "show_check";
    public static final String CHECK_LIST = "check_list";

    private Context mContext;

    private RecyclerView recyclerView;
    private ContactAdapter adapter;

    private CharIndexView charIndexView;
    private TextView tv_index;

    private ArrayList<CNPinyin<ContactBean>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;

    private boolean isShowCheck;
    private ArrayList<ContactBean> checkList;

    public static ContactsFragment newInstance(boolean isShowCheck) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_CHECK, isShowCheck);
        fragment.setArguments(bundle);
        return fragment;

    }

    public static ContactsFragment newInstance(boolean isShowCheck, ArrayList<ContactBean> list) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_CHECK, isShowCheck);
        bundle.putParcelableArrayList(CHECK_LIST, list);
        fragment.setArguments(bundle);
        return fragment;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isShowCheck = bundle.getBoolean(SHOW_CHECK, false);
            checkList = bundle.getParcelableArrayList(CHECK_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setListener();
        reqContactList();
    }

    /**
     * 初始化
     */
    private void initView(View view) {

        recyclerView = view.findViewById(R.id.rv_main);
        charIndexView = view.findViewById(R.id.iv_main);
        tv_index = view.findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new ContactAdapter(mContext, contactList, isShowCheck, checkList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new StickyHeaderDecoration(adapter));
    }


    private void setListener() {
        charIndexView.setOnCharIndexChangedListener(new CharIndexView.OnCharIndexChangedListener() {
            @Override
            public void onCharIndexChanged(char currentIndex) {
                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i).getFirstChar() == currentIndex) {
                        manager.scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }

            @Override
            public void onCharIndexSelected(String currentIndex) {
                if (currentIndex == null) {
                    tv_index.setVisibility(View.INVISIBLE);
                } else {
                    tv_index.setVisibility(View.VISIBLE);
                    tv_index.setText(currentIndex);
                }
            }
        });
    }


    /**
     * 获取联系人列表（好友列表）
     */
    private void reqContactList() {
        //请求好友列表接口
        HuxinSdkManager.instance().reqContactList(new ProtoCallback.ContactListener() {
            @Override
            public void result(List<ContactBean> list) {

                List<ContactBean> buddyList = new ArrayList<>();
                for (ContactBean item : list) {
                    if (item.getStatus() != 0) {//状态（删除：0；好友：1；拉黑：2）
                        buddyList.add(item);
                    }
                }
                contactSortByPinyin(buddyList);
            }
        });
    }


    /**
     * 拼音排序
     *
     * @param list
     */
    private void contactSortByPinyin(final List<ContactBean> list) {
        subscription = Observable.create(new Observable.OnSubscribe<List<CNPinyin<ContactBean>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<ContactBean>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<CNPinyin<ContactBean>> contactList =
                            CNPinyinFactory.createCNPinyinList(list);
                    Collections.sort(contactList);
                    subscriber.onNext(contactList);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CNPinyin<ContactBean>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<CNPinyin<ContactBean>> cnPinyins) {
                        //回调业务数据
                        contactList.addAll(cnPinyins);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * item点击
     *
     * @param contact
     */
    @Override
    public void onItemClick(ContactBean contact) {
        if (isShowCheck) {
            if (getActivity() instanceof AddContactsCreateGroupActivity) {
                AddContactsCreateGroupActivity act = (AddContactsCreateGroupActivity) getActivity();
                act.updateCacheMap(contact);
            } else if (getActivity() instanceof DeleteContactListActivity) {
                DeleteContactListActivity act = (DeleteContactListActivity) getActivity();
                act.updateCacheMap(contact);
            }
        } else {
            Intent intent = new Intent(mContext, IMConnectionActivity.class);
            intent.putExtra(IMConnectionActivity.DST_UUID, contact.getUuid());
            intent.putExtra(IMConnectionActivity.DST_NAME, contact.getDisplayName());
            intent.putExtra(IMConnectionActivity.DST_AVATAR, contact.getAvatar());
            intent.putExtra(IMConnectionActivity.DST_USERNAME, contact.getUserName());
            intent.putExtra(IMConnectionActivity.DST_PHONE, contact.getMobile());
            startActivity(intent);
        }
    }

    /**
     * item 长按
     */
    @Override
    public void onLongClick(ContactBean contact) {

    }


    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

}
