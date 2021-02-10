package com.youmai.hxsdk.view.full;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.CommonUtils;

import java.util.List;


/**
 * 作者：create by YW
 * 日期：2016.08.10 16:49
 * 描述：
 */
public class MapViewUtil implements LocationSource, AMapLocationListener, AMap.OnMarkerClickListener {

    private MapView mapView;
    private AMap aMap;
    private Marker marker;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LatLng latLng;
    private AMapLocation aMapLocation;//暂时没用到

    private final static float zoom = 16f;
    private Context mContext;

    public MapViewUtil(Context context, MapView map) {
        mContext = context;
        mapView = map;
    }

    public void onCreate(Bundle bundle) {
        mapView.onCreate(bundle);

        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            aMap.setLocationSource(this);// 设置定位监听

            aMap.getUiSettings().setScaleControlsEnabled(true);
            aMap.getUiSettings().setZoomControlsEnabled(true);
            aMap.getUiSettings().setZoomGesturesEnabled(true);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            aMap.getUiSettings().setAllGesturesEnabled(true);
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

            aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        }
    }


    public void setLocation(double latitude, double longitude) {
        try {
            aMap.setLocationSource(null);
            latLng = new LatLng(latitude, longitude);
            addMarkersToMap(latLng);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void toDaoHang(double latitude, double longitude) {
        if (latitude == 0 || longitude == 0) {
            return;
        }

        if (CommonUtils.isInstalled(mContext, "com.autonavi.minimap")) {// 判断是否安装了高德地图
            try {
                // 尝试调用高德地图
                // dev 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
                Intent intent = new Intent(
                        "android.intent.action.VIEW",
                        android.net.Uri
                                .parse("androidamap://navi?sourceApplication=huxin&lat="
                                        + latitude
                                        + "&lon="
                                        + longitude
                                        + "&dev=0&style=2"));
                intent.setPackage("com.autonavi.minimap");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);

            } catch (Exception e) {
                return;
            }
            return;
        } else if (CommonUtils.isInstalled(mContext, "com.baidu.BaiduMap")) {// 判断是否安装了百度地图
            // 坐标需要转换，不然不对，目前没转换
            try {
                // 尝试调用百度地图
                double toLoc[] = gcjToBd09(longitude,
                        latitude);

                StringBuilder loc = new StringBuilder();
                loc.append("intent://map/direction?origin=latlng:");
                loc.append(latitude);
                loc.append(",");
                loc.append(longitude);
                loc.append("|name:我的位置");
                loc.append("&destination=latlng:");
                loc.append(toLoc[1]);// .latitude);
                loc.append(",");
                loc.append(toLoc[0]);// .longitude);
                loc.append("|name:目标位置");
                loc.append("&mode=driving");
                loc.append("&src=" + mContext.getPackageName());
                loc.append("#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                Intent intent = Intent.parseUri(loc.toString(), 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            } catch (Exception e) {
                return;
            }
            return;
        }

    }


    private Location getLastKnownLocation() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            try {
                l = lm.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private double[] gcjToBd09(double longitude, double latitude) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = longitude, y = latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065; // longitude
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                this.aMapLocation = amapLocation;
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mLocationOption.setInterval(-1);//间隔时间为-1，只定位一次
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }


    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng) {
        if (latLng != null) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.title("我在这里");
            markerOption.draggable(true);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(
                    mContext.getResources(), R.drawable.hx_icon_location)));
            aMap.clear();//清除缓存的marker数据
            marker = aMap.addMarker(markerOption);
            marker.showInfoWindow();

            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            return;
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (aMap != null) {
            jumpPoint(marker);
        }
        return false;
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker) {
        final LatLng latLng = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        Point startPoint = proj.toScreenLocation(latLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * latLng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * latLng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                aMap.invalidate();// 刷新地图
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

}
