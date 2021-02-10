package com.youmai.hxsdk.forward;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.router.APath;

/**
 * Created by fylder on 2017/10/10.
 */
@Route(path = APath.MSG_FORWARD)
public class ForwardSelectActivity extends AppCompatActivity {

    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_forward);

        tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("发送到");
        tv_right = findViewById(R.id.tv_right);
        tv_right.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.forward_select_fragment);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        } else {
            Toast.makeText(this, "fragment is null", Toast.LENGTH_SHORT).show();
        }

    }
}
