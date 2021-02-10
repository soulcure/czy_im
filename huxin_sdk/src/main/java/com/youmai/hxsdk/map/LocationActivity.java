package com.youmai.hxsdk.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.adapter.DividerItemDecoration;
import com.youmai.hxsdk.config.AppConfig;

import java.util.ArrayList;
import java.util.List;


public class LocationActivity extends SdkBaseActivity implements
        LocationSource, AMapLocationListener, OnCameraChangeListener,
        OnMarkerClickListener, OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener {

    public static final String DST_UUID = "dst_uuid";
    public static final String FROM_TO_IM = "from_to_im";
    public static final String FROM_TO_CARD = "from_to_card";

    // UI
    private AMap aMap;
    private MapView mapView;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    private ImageView btn_cancel;
    private TextView btn_send;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;// 坐标转物理地址
    private AMapLocation amapLocation;

    // Logic
    private Marker marker;
    private String dstUuid;

    private AutoCompleteTextView et_search;
    private ImageView img_cancel;

    private String city = "";
    private String newText;

    private boolean isFirstInput = true;
    private boolean isSearchSelect = false;

    Inputtips.InputtipsListener inputTipsListener = new Inputtips.InputtipsListener() {
        @Override
        public void onGetInputtips(List<Tip> list, int rCode) {
            if (rCode == 1000) {// 正确返回

                List<SearchTips> dataList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    SearchTips data = new SearchTips(list.get(i));
                    dataList.add(data);
                }

                LocationAdapter aAdapter = new LocationAdapter(mContext, R.layout.item_gaode_location_autotext, dataList);
                aAdapter.setKeyword(newText);

                et_search.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
                if (isFirstInput) {
                    isFirstInput = false;
                    et_search.showDropDown();
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_location);
        dstUuid = getIntent().getStringExtra(DST_UUID);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        setUpMap();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new SearchAdapter(mContext);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layout.getOrientation()));

        recyclerView.setAdapter(adapter);
        img_cancel = (ImageView) findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                img_cancel.setVisibility(View.GONE);
            }
        });

        et_search = (AutoCompleteTextView) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newText = s.toString().trim();
                if (newText.length() > 0) {
                    InputtipsQuery inputQuery = new InputtipsQuery(newText, city);
                    Inputtips inputTips = new Inputtips(mContext, inputQuery);
                    inputQuery.setCityLimit(true);
                    inputTips.setInputtipsListener(inputTipsListener);
                    inputTips.requestInputtipsAsyn();

                    img_cancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSearchSelect = true;
                SearchTips selected = (SearchTips) parent.getItemAtPosition(position);
                searchPoi(selected);

                et_search.setText("");
                img_cancel.setVisibility(View.GONE);

            }
        });


        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKey(et_search);
                finish();
            }
        });
        btn_send = (TextView) findViewById(R.id.btn_send);

        boolean isUserByCard = getIntent().getBooleanExtra(FROM_TO_CARD, false);
        if (isUserByCard) {
            btn_send.setText(R.string.hx_select);
        }
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    PoiItem poiItem = adapter.getSelectPoiItem();
                    handle4Im(poiItem);
                }
            }
        });

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }


    /**
     * 设置amap属性
     */
    private void setUpMap() {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16f));
        aMap.setLocationSource(this);// 设置定位监听

        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.getUiSettings().setZoomGesturesEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setAllGesturesEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        // 事件监听
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器

        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        activate(mListener);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        this.amapLocation = amapLocation;

        if (marker != null) {
            return;
        }
        LatLng curLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(curLatlng);

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16f));

        String title = amapLocation.getAddress();
        markerOption.title(title);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.hx_icon_location));
        marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();

        city = amapLocation.getCity();
        aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器


        if (mListener != null) {
            mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
        }
    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this.mContext);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if (marker != null) {
            marker.setPosition(cameraPosition.target);
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLonPoint latLonPoint = new LatLonPoint(
                cameraPosition.target.latitude, cameraPosition.target.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
        //searchPoi(latLonPoint);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                if (marker != null && !isSearchSelect) {
                    marker.setTitle(result.getRegeocodeAddress()
                            .getFormatAddress());
                    marker.showInfoWindow();
                }

                List<PoiItem> list = result.getRegeocodeAddress().getPois();
                adapter.setList(list);

                isSearchSelect = false;
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        final ArrayList<PoiItem> list = poiResult.getPois();
        adapter.setList(list);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 处理IM的返回结果.
     *
     * @param poiItem
     */
    private void handle4Im(PoiItem poiItem) {
        double longitude = 0;
        double latitude = 0;
        String address = "";

        if (poiItem != null) {
            LatLonPoint point = poiItem.getLatLonPoint();
            longitude = point.getLongitude();
            latitude = point.getLatitude();
            address = poiItem.getTitle() + ":" + poiItem.getSnippet();
        } else if (marker != null) {
            longitude = marker.getPosition().longitude;// 经度
            latitude = marker.getPosition().latitude;// 纬度
            address = marker.getTitle();// 地址
        }


        int zoomLevel = (int) aMap.getCameraPosition().zoom;

        //http://restapi.amap.com/v3/staticmap?location=113.481485,39.990464&zoom=10&size=750*300&markers=mid,,A:116.481485,39.990464&key=ee95e52bf08006f63fd29bcfbcf21df0
        final String url = "http://restapi.amap.com/v3/staticmap?location="
                + longitude + "," + latitude + "&zoom=" + zoomLevel
                + "&size=720*550&traffic=1&markers=mid,0xff0000,A:" + longitude
                + "," + latitude + "&key=" + AppConfig.staticMapKey;

        Intent it = new Intent();
        it.putExtra("url", url);
        it.putExtra("longitude", longitude);
        it.putExtra("latitude", latitude);
        it.putExtra("zoom_level", zoomLevel);
        it.putExtra("address", address);
        setResult(Activity.RESULT_OK, it);
        finish();
    }


    /**
     * 搜索附近
     *
     * @param latLonPoint
     */
    private void searchPoi(LatLonPoint latLonPoint) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        String code = amapLocation.getCityCode();
        PoiSearch.Query query = new PoiSearch.Query("", "商务住宅|道路附属设施|地名地址信息|公共设施|生活服务|餐饮服务|购物服务", code);

        // keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
        //共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）

        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查第一页
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 200));//设置周边搜索的中心点以及区域

        poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器

        poiSearch.searchPOIAsyn();//开始搜索

    }

    /**
     * POI查询
     *
     * @param result
     */
    private void searchPoi(SearchTips result) {
        LatLonPoint point = result.getPoint();
        if (point != null) {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
            if (marker != null) {
                marker.setTitle(result.getName());
                marker.setPosition(latLng);
                marker.showInfoWindow();
            }
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            hideSoftKey(et_search);
        }

    }


    private void hideSoftKey(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void moveMarker(PoiItem poiItem) {
        if (marker != null) {
            marker.setTitle(poiItem.getTitle());
            LatLng curLatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            marker.setPosition(curLatlng);
            marker.showInfoWindow();
        }
    }
}
