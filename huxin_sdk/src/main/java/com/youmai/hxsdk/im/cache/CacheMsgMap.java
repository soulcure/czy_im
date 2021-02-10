package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:59
 * Description: 地图
 */
public class CacheMsgMap implements Parcelable, JsonFormat<CacheMsgMap> {

    private double longitude;
    private double latitude;
    private int scale;
    private String address;
    private String imgUrl;

    public CacheMsgMap() {
    }

    public double getLongitude() {
        return longitude;
    }

    public CacheMsgMap setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public CacheMsgMap setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public CacheMsgMap setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CacheMsgMap setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public CacheMsgMap setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgMap fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            longitude = jsonObject.optDouble("longitude");
            latitude = jsonObject.optDouble("latitude");
            scale = jsonObject.optInt("scale");
            address = jsonObject.optString("address");
            imgUrl = jsonObject.optString("imgUrl");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeInt(this.scale);
        dest.writeString(this.address);
        dest.writeString(this.imgUrl);
    }

    protected CacheMsgMap(Parcel in) {
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.scale = in.readInt();
        this.address = in.readString();
        this.imgUrl = in.readString();
    }

    public static final Creator<CacheMsgMap> CREATOR = new Creator<CacheMsgMap>() {
        @Override
        public CacheMsgMap createFromParcel(Parcel source) {
            return new CacheMsgMap(source);
        }

        @Override
        public CacheMsgMap[] newArray(int size) {
            return new CacheMsgMap[size];
        }
    };
}
