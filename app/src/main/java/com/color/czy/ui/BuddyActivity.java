package com.color.czy.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.color.czy.R;


public class BuddyActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy);

        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView() {
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
    }
}
