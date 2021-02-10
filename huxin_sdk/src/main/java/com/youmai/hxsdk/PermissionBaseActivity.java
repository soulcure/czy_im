package com.youmai.hxsdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.StringUtils;


/**
 * 呼信 sdk 权限申请基类
 * Created by colin on 2016/12/1.
 */


public class PermissionBaseActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    public Context mContext;
    public String mPermissionContent;


    String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAMERA};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }


    /**
     * 在该声明周期,检查权限申请情况
     */
    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(this, getString(R.string.phone_permission_content),
                    Manifest.permission.READ_PHONE_STATE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(this, getString(R.string.sdcard_permission_content),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }


    /**
     * 请求权限
     *
     * @param act        权限提示界面
     * @param content    权限提示用户内容信息
     * @param permission 权限
     */
    public void requestPermissions(Activity act, String content, final String permission) {
        if (ContextCompat.checkSelfPermission(act, permission)
                != PackageManager.PERMISSION_GRANTED) {
            mPermissionContent = content;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                    permission)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle(getString(R.string.permission_title))
                        .setMessage(content);

                builder.setPositiveButton(getString(R.string.hx_confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                ActivityCompat.requestPermissions(PermissionBaseActivity.this,
                                        new String[]{permission},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                arg0.dismiss();
                            }
                        });

                builder.setNegativeButton(getString(R.string.hx_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                                arg0.dismiss();
                            }
                        });
                builder.show();

            } else {
                ActivityCompat.requestPermissions(act,
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }


//    /**
//     * 请求权限检查完后回调的结果
//     *
//     * @param requestCode  .
//     * @param permissions  所请求的权限
//     * @param grantResults .
//     */
//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    showPermissionDialog(mContext);
//                }
//            }
//            break;
//        }
//
//    }


    /**
     * 显示登录提示框
     *
     * @param context
     */
    private void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (StringUtils.isEmpty(mPermissionContent)) {
            mPermissionContent = getString(R.string.permission_content);
        }

        builder.setTitle(context.getString(R.string.permission_title))
                .setMessage(mPermissionContent);

        builder.setPositiveButton(context.getString(R.string.hx_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AppUtils.startAppSettings(mContext);
                        arg0.dismiss();
                    }
                });

        builder.setNegativeButton(context.getString(R.string.hx_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        arg0.dismiss();
                    }
                });
        builder.show();
    }


}
