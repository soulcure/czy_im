package com.youmai.hxsdk.entity.cn;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by srsm
 */

public class SearchContactBean implements Comparable<SearchContactBean>, Parcelable {
    public static final int SEARCH_TYPE_NONE = 0x0000;
    public static final int SEARCH_TYPE_NUMBER = 0x0001;
    public static final int SEARCH_TYPE_NAME = 0x0002;
    public static final int SEARCH_TYPE_SIMPLE_SPELL = 0x0004;
    public static final int SEARCH_TYPE_WHOLE_SPECL = 0x0008;
    public static final int SEARCH_TYPE_SIMPLE_T9 = 0x0010;
    public static final int SEARCH_TYPE_WHOLE_T9 = 0x0020;
    public static final int SEARCH_TYPE_INFO = 0x0040;

    private int contactId; //id
    private String displayName;//姓名
    private String phoneNum; // 电话号码
    private String wholePinyin = "#"; // 全拼
    private String wholeT9;//全拼对应T9
    private String simplepinyin;//简拼
    private String simplepT9;//简拼对应T9
    private String iconUrl;
    private String info; //App 应用信息 url
    private long infoId;
    private String searchKey;
    private int searchType = SEARCH_TYPE_NONE;
    private DuoYinZi mDuoYinzi;
    private int[] wholePinYinFindIndex;
    private SearchContactBean nextSearchContactBean;
    private List<String> indexPinyin;

    //联系人 信息
    private String uuid;
    private String username; //收藏联系人查询员工详情使用

    //app 应用信息
    private String oauthType = "";
    private String developerCode = "";
    private String clientCode = "";

    public SearchContactBean() {

    }

    public SearchContactBean(SearchContactBean bean, boolean hasNext) {
        this.contactId = bean.getContactId();
        this.displayName = bean.getDisplayName();
        this.phoneNum = bean.getPhoneNum();
        this.wholePinyin = bean.getWholePinyin();
        this.wholeT9 = bean.getWholeT9();
        this.simplepinyin = bean.getSimplepinyin();
        this.simplepT9 = bean.getSimpleT9();
        this.iconUrl = bean.getIconUrl();
        this.info = bean.getInfo();
        this.infoId = bean.getInfoId();
        this.searchKey = bean.getSearchKey();
        this.searchType = bean.getSearchType();
        this.mDuoYinzi = bean.getDuoYinzi();
        this.wholePinYinFindIndex = bean.getWholePinYinFindIndex();
        this.indexPinyin = bean.indexPinyin;
        this.uuid = bean.getUuid();
        this.username = bean.getUsername();
        this.oauthType = bean.getOauthType();
        this.developerCode = bean.getDeveloperCode();
        this.clientCode = bean.getClientCode();
        if (hasNext) {
            this.nextSearchContactBean = bean.getNextBean();
        }
    }

    public long getInfoId() {
        return infoId;
    }

    public void setInfoId(long infoId) {
        this.infoId = infoId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWholeT9() {
        return wholeT9;
    }

    public void setWholeT9(String wholeT9) {
        this.wholeT9 = wholeT9;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getWholePinyin() {
        return wholePinyin;
    }

    public void setWholePinyin(String wholePinyin) {
        if (wholePinyin != null) {
            this.wholePinyin = wholePinyin;
        }
    }

    public void setIndexPinyin(List<String> indexPinyin) {
        this.indexPinyin = indexPinyin;
    }

    public List<String> getIndexPinyin() {
        return indexPinyin;
    }

    public String getSimplepinyin() {
        return simplepinyin;
    }

    public void setSimplepinyin(String pinyin) {
        this.simplepinyin = pinyin;
    }

    public String getSimpleT9() {
        return simplepT9;
    }

    public void setSimpleT9(String t9) {
        this.simplepT9 = t9;
    }

    public SearchContactBean getNextBean() {
        return nextSearchContactBean;
    }

    public void setNextBean(SearchContactBean bean) {
        this.nextSearchContactBean = bean;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DuoYinZi getDuoYinzi() {
        return mDuoYinzi;
    }

    public void setDuoYinzi(DuoYinZi duoYinzi) {
        this.mDuoYinzi = duoYinzi;
    }

    public int[] getWholePinYinFindIndex() {
        return wholePinYinFindIndex;
    }

    public void setWholePinYinFindIndex(int[] wholePinYinFindIndex) {
        this.wholePinYinFindIndex = wholePinYinFindIndex;
    }

    public String getOauthType() {
        return oauthType;
    }

    public void setOauthType(String oauthType) {
        this.oauthType = oauthType;
    }

    public String getDeveloperCode() {
        return developerCode;
    }

    public void setDeveloperCode(String developerCode) {
        this.developerCode = developerCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "SearchContactBean{" +
                "contactId=" + contactId +
                ", displayName='" + displayName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", wholePinyin='" + wholePinyin + '\'' +
                ", wholeT9='" + wholeT9 + '\'' +
                ", simplepinyin='" + simplepinyin + '\'' +
                ", simplepT9='" + simplepT9 + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", info='" + info + '\'' +
                ", infoId=" + infoId +
                ", searchKey='" + searchKey + '\'' +
                ", searchType=" + searchType +
                ", mDuoYinzi=" + mDuoYinzi +
                ", wholePinYinFindIndex=" + Arrays.toString(wholePinYinFindIndex) +
                ", nextSearchContactBean=" + nextSearchContactBean +
                ", indexPinyin=" + indexPinyin +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int compareTo(SearchContactBean another) {
        return this.wholePinyin.compareTo(another.getWholePinyin());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.contactId);
        dest.writeString(this.displayName);
        dest.writeString(this.phoneNum);
        dest.writeString(this.wholePinyin);
        dest.writeString(this.wholeT9);
        dest.writeString(this.simplepinyin);
        dest.writeString(this.simplepT9);
        dest.writeString(this.iconUrl);
        dest.writeString(this.info);
        dest.writeLong(this.infoId);
        dest.writeString(this.searchKey);
        dest.writeInt(this.searchType);
        dest.writeParcelable(this.mDuoYinzi, flags);
        dest.writeIntArray(this.wholePinYinFindIndex);
        dest.writeParcelable(this.nextSearchContactBean, flags);
        dest.writeStringList(this.indexPinyin);
        dest.writeString(this.uuid);
        dest.writeString(this.username);
        dest.writeString(this.oauthType);
        dest.writeString(this.developerCode);
        dest.writeString(this.clientCode);
    }

    protected SearchContactBean(Parcel in) {
        this.contactId = in.readInt();
        this.displayName = in.readString();
        this.phoneNum = in.readString();
        this.wholePinyin = in.readString();
        this.wholeT9 = in.readString();
        this.simplepinyin = in.readString();
        this.simplepT9 = in.readString();
        this.iconUrl = in.readString();
        this.info = in.readString();
        this.infoId = in.readLong();
        this.searchKey = in.readString();
        this.searchType = in.readInt();
        this.mDuoYinzi = in.readParcelable(DuoYinZi.class.getClassLoader());
        this.wholePinYinFindIndex = in.createIntArray();
        this.nextSearchContactBean = in.readParcelable(SearchContactBean.class.getClassLoader());
        this.indexPinyin = in.createStringArrayList();
        this.uuid = in.readString();
        this.username = in.readString();
        this.oauthType = in.readString();
        this.developerCode = in.readString();
        this.clientCode = in.readString();
    }

    public static final Creator<SearchContactBean> CREATOR = new Creator<SearchContactBean>() {
        @Override
        public SearchContactBean createFromParcel(Parcel source) {
            return new SearchContactBean(source);
        }

        @Override
        public SearchContactBean[] newArray(int size) {
            return new SearchContactBean[size];
        }
    };
}
