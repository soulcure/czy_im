package com.color.czy.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.color.czy.CZYApplication;
import com.color.czy.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ServiceInfo;
import com.youmai.hxsdk.UserInfo;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.ScreenUtils;


@Route(path = APath.RE_LOGIN)
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText ed_name;
    private AppCompatEditText ed_nick;
    private int position;
    private CZYApplication app;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkPermission();
        app = (CZYApplication) getApplication();
        initView();

        float dpi = ScreenUtils.getWidthDPI(this);
        Toast.makeText(this, "dpi:" + dpi, Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView() {
        ed_name = findViewById(R.id.ed_name);
        ed_name.setText(((CZYApplication) getApplication()).getMobile().get(position));

        ed_nick = findViewById(R.id.ed_nick);
        ed_nick.setText(app.getNickName().get(position));

        MaterialSpinner spinner = findViewById(R.id.spinner);
        spinner.setItems(app.getNickName());
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int pos, long id, String item) {
                position = pos;
                ed_name.setText(app.getMobile().get(pos));
                ed_nick.setText(app.getNickName().get(pos));
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {

            UserInfo userInfo = new UserInfo();
            userInfo.setUuid(app.getUuid().get(position));
            userInfo.setUserId(app.getUserId().get(position));
            userInfo.setAvatar(app.getAvatar().get(position));
            userInfo.setPhoneNum(app.getMobile().get(position));
            userInfo.setSex(app.getGender().get(position));
            userInfo.setUserName(app.getUserName().get(position));
            userInfo.setNickName(app.getNickName().get(position));
            userInfo.setRealName(app.getNickName().get(position));
            userInfo.setOrgId(app.getCommunityUuid().get(position));
            userInfo.setOrgName(app.getCommunityName().get(position));

            HuxinSdkManager.instance().setUserInfo(userInfo);


            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setUuid("1279a783-0fec-4bae-8972-b69c15ac5f15");
            serviceInfo.setAvatar("https://www.bing.com/th?id=OIP.UUkBYTFvywDIKy1GI5uDOAAAAA&w=200&h=199&c=7&o=5&pid=1.7");
            serviceInfo.setPhoneNum("18164198823");
            serviceInfo.setSex("1");
            serviceInfo.setUserName("3040876");
            serviceInfo.setNickName("何湘辉");
            serviceInfo.setRealName("何湘辉");
            HuxinSdkManager.instance().setServiceInfo(serviceInfo);


            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, Activity.RESULT_FIRST_USER);
        }

    }
}
