package com.color.czy;

import android.content.Context;

import com.color.czy.entity.UserData;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.StreamUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestData {


    public static List<ContactBean> contactList(Context context) {
        List<ContactBean> contactList = new ArrayList<>();

        InputStream inputStream = null;
        String json = null;
        try {

            if (AppConfig.LAUNCH_MODE == 0) {
                inputStream = context.getAssets().open("contact.txt");
            } else if (AppConfig.LAUNCH_MODE == 1) {
                inputStream = context.getAssets().open("contact_beta.txt");
            } else {
                inputStream = context.getAssets().open("contact_online.txt");
            }

            json = StreamUtils.readStream(inputStream);
        } catch (IOException e) {
            //log the exception
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.optJSONArray("content");
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                ContactBean contact = new ContactBean();
                contact.setUuid(item.optString("uuid"));
                contact.setNickName(item.optString("nick_name"));
                contact.setAvatar(item.optString("portrait"));
                contact.setMobile(item.optString("mobile"));
                contact.setUserId(item.optString("user_id"));
                contact.setUserName(item.optString("name"));
                contact.setSex(item.optString("gender"));
                contact.setOrgName(item.optString("community_name"));
                contact.setOrgId(item.optString("community_uuid"));

                contactList.add(contact);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return contactList;
    }


    public static List<ContactBean> contactList1(Context context) {
        List<ContactBean> contactList = new ArrayList<>();

        String[] names = context.getResources().getStringArray(R.array.mobile); //获取
        String[] uuid = context.getResources().getStringArray(R.array.uuid);
        String[] avatar = context.getResources().getStringArray(R.array.avatar);
        String[] gender = context.getResources().getStringArray(R.array.gender);
        String[] nickName = context.getResources().getStringArray(R.array.nick_name);

        for (int i = 0; i < names.length; i++) {
            ContactBean contact = new ContactBean();
            String hanzi = nickName[i];
            contact.setRealName(hanzi);
            contact.setUserName(names[i]);
            contact.setAvatar(avatar[i]);
            contact.setUuid(uuid[i]);
            contact.setSex(gender[i]);

            StringBuilder pinyin = new StringBuilder();
            StringBuilder ch = new StringBuilder();
            for (int j = 0; j < hanzi.length(); j++) {
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
            }
            contact.setPinyin(pinyin.toString());
            contact.setSimplePinyin(ch.toString());
            contactList.add(contact);
        }


        return contactList;
    }


    public static UserData getUserInfo(Context context, String fileName) {
        InputStream inputStream = null;
        String json = null;
        try {
            inputStream = context.getAssets().open(fileName);
            json = StreamUtils.readStream(inputStream);
        } catch (IOException e) {
            //log the exception
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        UserData userData = GsonUtil.parse(json, UserData.class);
        return userData;
    }


    public static ContactBean addHeadItem(String hanzi) {
        ContactBean contact = new ContactBean();
        StringBuffer pinyin = new StringBuffer();
        StringBuffer ch = new StringBuffer();
        List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
        for (int j = 0; j < hanzi.length(); j++) {
            pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
            ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
            chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
        }

        contact.setRealName(hanzi);
        contact.setPinyin(pinyin.toString());
        contact.setSimplePinyin(ch.toString());
        return contact;
    }
}
