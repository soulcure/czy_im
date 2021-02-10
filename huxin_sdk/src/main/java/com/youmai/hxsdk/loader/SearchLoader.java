package com.youmai.hxsdk.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.DuoYinZi;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2017/8/7.
 */
public class SearchLoader extends AsyncTaskLoader {

    private final String TAG = SearchLoader.class.getSimpleName();

    private List<ContactBean> contactList;
    private String mQuery;
    private List<SearchContactBean> allList;

    public SearchLoader(Context context, List<ContactBean> list) {
        super(context);
        contactList = list;
    }

    public void setQuery(String str) {
        this.mQuery = str;
    }

    @Override
    public List<SearchContactBean> loadInBackground() {
        List<SearchContactBean> resList = new ArrayList<>();

        if (ListUtils.isEmpty(allList)) {
            allList = searchContactsList(contactList);
        }

        if (TextUtils.isEmpty(mQuery)) {
            return allList;
        }

        String finalQuery = mQuery;
        String queryUpper = mQuery.toUpperCase();

        for (SearchContactBean bean : allList) {
            int searchType = SearchContactBean.SEARCH_TYPE_NONE;
            //全拼搜索
            int[] findResult = new int[2];

            if (bean.getDisplayName().contains(mQuery)) {
                searchType = SearchContactBean.SEARCH_TYPE_NAME;
                finalQuery = mQuery;
            } else if (bean.getSimplepinyin().contains(queryUpper)) {
                searchType = SearchContactBean.SEARCH_TYPE_SIMPLE_SPELL;
                finalQuery = queryUpper;
            } else if (bean.getWholePinyin().contains(queryUpper)) {

                List<String> indexPinyin = bean.getIndexPinyin();
                for (String pinyin : indexPinyin) {
                    boolean b = pinyin.startsWith(queryUpper);//每个汉字拼音
                    boolean c = queryUpper.startsWith(pinyin);
                    Log.v(TAG, "pinyin: " + pinyin);
                    if (b || c) {
                        searchType = SearchContactBean.SEARCH_TYPE_WHOLE_SPECL;
                        break;
                    }
                }
                finalQuery = queryUpper;

            } else if (bean.getDuoYinzi().find(queryUpper, findResult)) {
                searchType = SearchContactBean.SEARCH_TYPE_WHOLE_SPECL;
                finalQuery = queryUpper;
                bean.setWholePinYinFindIndex(findResult);
            }


            if (searchType != SearchContactBean.SEARCH_TYPE_NONE) {
                bean.setSearchKey(finalQuery);
                bean.setSearchType(searchType);

                //通讯录显示时不拷贝下一级，节省数据
                SearchContactBean searchBean = new SearchContactBean(bean, false);
                resList.add(searchBean);
            }
        }
        return resList;
    }


    /**
     * 搜索联系人
     *
     * @param list
     * @return
     */
    private List<SearchContactBean> searchContactsList(List<ContactBean> list) {
        List<SearchContactBean> contactList = new ArrayList<>();
        for (ContactBean item : list) {
            String hanzi = item.getDisplayName();
            StringBuilder ch = new StringBuilder();
            StringBuilder pinyin = new StringBuilder();
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合

            for (int j = 0; j < hanzi.length(); j++) {
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            SearchContactBean contact = new SearchContactBean();
            contact.setIconUrl(item.getAvatar());
            contact.setUsername(item.getUserName());
            contact.setUuid(item.getUuid());
            contact.setDisplayName(hanzi);
            contact.setWholePinyin(pinyin.toString());
            contact.setSimplepinyin(ch.toString());
            contact.setIndexPinyin(chStr);

            DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
            contact.setDuoYinzi(duoYinZi);

            contactList.add(contact);
        }
        return contactList;
    }


}