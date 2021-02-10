package com.youmai.hxsdk.charservice;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.dialog.HxScoreDialog;
import com.youmai.hxsdk.entity.ScoreResult;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.GsonUtil;

import java.lang.reflect.Method;

public class ScoreActivity extends SdkBaseActivity implements CompoundButton.OnCheckedChangeListener {


    private ImageView img_head;
    private SimpleRatingBar ratingBar;


    private Button btnCommit;
    private EditText etMsg;
    private TextView tv_ask;
    private TextView tv_result;

    private CheckBox excellent;
    private CheckBox fine;
    private CheckBox fast;
    private CheckBox other;

    private String scoreMsg = "";
    private String scoreChoice = "";
    private String rate = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        initView();

        /*if (isHUAWEI()) {
            registerNavigationBarObserver();

            if (checkDeviceHasNavigationBar(this)) {
                ViewGroup.LayoutParams params = etMsg.getLayoutParams();
                params.height = mContext.getResources().getDimensionPixelOffset(R.dimen.score_edit_height)
                        - getNavigationBarHeight(mContext);
                etMsg.setLayoutParams(params);
            } else {
                ViewGroup.LayoutParams params = etMsg.getLayoutParams();
                params.height = mContext.getResources().getDimensionPixelOffset(R.dimen.score_edit_height);
                etMsg.setLayoutParams(params);
            }
        }*/

        initListener();
    }


    private void initView() {
        String dstAvatar = getIntent().getStringExtra(IMOwnerActivity.DST_AVATAR);
        String dstNickName = getIntent().getStringExtra(IMOwnerActivity.DST_NAME);

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("评价");

        tv_ask = (TextView) findViewById(R.id.tv_ask);
        String format = getResources().getString(R.string.hx_im_server_ack);
        tv_ask.setText(String.format(format, dstNickName));

        tv_result = (TextView) findViewById(R.id.tv_result);


        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("记录");
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ScoreHistoryActivity.class));
            }
        });


        img_head = findViewById(R.id.img_head);
        int size = mContext.getResources().getDimensionPixelOffset(R.dimen.red_head);
        Glide.with(this).load(dstAvatar)
                .apply(new RequestOptions()
                        .circleCrop()
                        .override(size, size)
                        .placeholder(R.drawable.ic_service_header)
                        .error(R.drawable.ic_service_header)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(img_head);


        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 1.0) {
                    tv_result.setText(R.string.very_dissatisfied);
                    //etMsg.setHint(R.string.dissatisfied_hint);

                    if (!rate.equals("1")) {
                        rate = "1";
                        excellent.setText(R.string.solve_nothing);
                        fine.setText(R.string.not_reached);
                        fast.setText(R.string.slow);

                        clearChecked();
                        if (TextUtils.isEmpty(scoreMsg)) {
                            btnCommit.setEnabled(false);
                        } else {
                            btnCommit.setEnabled(true);
                        }
                    }


                } else if (rating <= 2.0) {
                    tv_result.setText(R.string.dissatisfied);
                    //etMsg.setHint(R.string.dissatisfied_hint);
                    if (!rate.equals("2")) {
                        rate = "2";
                        excellent.setText(R.string.solve_nothing);
                        fine.setText(R.string.not_reached);
                        fast.setText(R.string.slow);

                        clearChecked();
                        if (TextUtils.isEmpty(scoreMsg)) {
                            btnCommit.setEnabled(false);
                        } else {
                            btnCommit.setEnabled(true);
                        }
                    }


                } else if (rating <= 3.0) {
                    tv_result.setText(R.string.general);
                    //etMsg.setHint(R.string.satisfied_hint);
                    if (!rate.equals("3")) {
                        rate = "3";
                        excellent.setText(R.string.profession);
                        fine.setText(R.string.solve_everything);
                        fast.setText(R.string.fast);

                        clearChecked();
                        if (TextUtils.isEmpty(scoreMsg)) {
                            btnCommit.setEnabled(false);
                        } else {
                            btnCommit.setEnabled(true);
                        }
                    }


                } else if (rating <= 4.0) {
                    tv_result.setText(R.string.satisfied);
                    //etMsg.setHint(R.string.satisfied_hint);
                    if (!rate.equals("4")) {
                        rate = "4";
                        excellent.setText(R.string.profession);
                        fine.setText(R.string.solve_everything);
                        fast.setText(R.string.fast);

                        clearChecked();
                        if (TextUtils.isEmpty(scoreMsg)) {
                            btnCommit.setEnabled(false);
                        } else {
                            btnCommit.setEnabled(true);
                        }
                    }


                } else if (rating <= 5.0) {
                    tv_result.setText(R.string.very_satisfied);
                    //etMsg.setHint(R.string.satisfied_hint);
                    if (!rate.equals("5")) {
                        rate = "5";
                        excellent.setText(R.string.profession);
                        fine.setText(R.string.solve_everything);
                        fast.setText(R.string.fast);

                        clearChecked();
                        if (TextUtils.isEmpty(scoreMsg)) {
                            btnCommit.setEnabled(false);
                        } else {
                            btnCommit.setEnabled(true);
                        }
                    }
                }

            }
        });


        excellent = findViewById(R.id.excellent);
        fine = findViewById(R.id.fine);
        fast = findViewById(R.id.fast);
        other = findViewById(R.id.other);


        etMsg = findViewById(R.id.et_msg);
        etMsg.setHint(R.string.satisfied_hint);
        etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                scoreMsg = s.toString();
                if (TextUtils.isEmpty(scoreMsg)) {
                    btnCommit.setEnabled(false);
                } else {
                    btnCommit.setEnabled(true);
                }
            }
        });


        btnCommit = findViewById(R.id.btn_commit);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceScore();
            }
        });

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            btnCommit.setEnabled(true);
        } else {
            boolean isCheck = excellent.isChecked() || fine.isChecked() || fast.isChecked() || other.isChecked();
            if (!TextUtils.isEmpty(scoreMsg)
                    || isCheck) {
                btnCommit.setEnabled(true);
            } else {
                btnCommit.setEnabled(false);
            }
        }
    }

    private void initListener() {
        excellent.setOnCheckedChangeListener(this);
        fine.setOnCheckedChangeListener(this);
        fast.setOnCheckedChangeListener(this);
        other.setOnCheckedChangeListener(this);
    }

    private void clearChecked() {
        scoreChoice = "";
        excellent.setChecked(false);
        fine.setChecked(false);
        fast.setChecked(false);
        other.setChecked(false);
    }

    private void serviceScore() {
        scoreChoice = "";
        if (excellent.isChecked()) {
            scoreChoice += excellent.getText().toString() + ",";
        }
        if (fine.isChecked()) {
            scoreChoice += fine.getText().toString() + ",";
        }
        if (fast.isChecked()) {
            scoreChoice += fast.getText().toString() + ",";
        }
        if (other.isChecked()) {
            scoreChoice += other.getText().toString() + ",";
        }


        if (TextUtils.isEmpty(scoreMsg) && TextUtils.isEmpty(scoreChoice)) {
            Toast.makeText(this, "至少要选择一个快捷评价", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rate.equals("0")) {
            Toast.makeText(this, "请打一个评分", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        ColorsConfig.reqAccessToken(new ColorsConfig.AccessToken() {
            @Override
            public void OnSuccess(String token) {
                scoreToICE(token);
            }
        });
    }


    private void scoreToICE(String accessToken) {
        String url = ColorsConfig.ICE_EVISIT;

        String oa_username = HuxinSdkManager.instance().getServiceUserName();
        String uuid = HuxinSdkManager.instance().getUserId();
        String community_uuid = HuxinSdkManager.instance().getOrgId();
        String address = HuxinSdkManager.instance().getOrgName();

        String content = "";
        if (!TextUtils.isEmpty(scoreChoice) && !TextUtils.isEmpty(scoreMsg)) {
            content = scoreChoice + scoreMsg;
        } else if (!TextUtils.isEmpty(scoreChoice) && TextUtils.isEmpty(scoreMsg)) {
            content = scoreChoice.substring(0, scoreChoice.length() - 1);
        } else if (TextUtils.isEmpty(scoreChoice) && !TextUtils.isEmpty(scoreMsg)) {
            content = scoreMsg;
        }


        ContentValues params = new ContentValues();
        params.put("access_token", accessToken);
        params.put("level", rate);
        params.put("content", content);
        params.put("oa_username", oa_username);  //客户经理OA
        params.put("czy_id", uuid);  //用户ID
        params.put("community_uuid", community_uuid);  //小区UUID
        params.put("address", address);  //address

        ColorsConfig.commonParams(params);
        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                dismissProgressDialog();
                ScoreResult bean = GsonUtil.parse(response, ScoreResult.class);
                if (bean != null && bean.isSuccess()) {
                    HxScoreDialog.Builder builder = new HxScoreDialog.Builder(mContext);
                    final HxScoreDialog dialog = builder.create();
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                                finish();
                            }
                        }
                    }, 3000);
                } else {
                    Toast.makeText(mContext, "评价失败", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    /**
     * 是否是华为
     */
    public static boolean isHUAWEI() {
        return Build.MANUFACTURER.equals("HUAWEI");
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }


    public static int getNavigationBarHeight(Context context) {
        int height = -1;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    private void registerNavigationBarObserver() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getContentResolver().registerContentObserver(Settings.System.getUriFor
                    ("navigationbar_is_min"), true, mNavigationBarObserver);
        } else {
            getContentResolver().registerContentObserver(Settings.Global.getUriFor
                    ("navigationbar_is_min"), true, mNavigationBarObserver);
        }
    }

    private ContentObserver mNavigationBarObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            int navigationBarIsMin = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                navigationBarIsMin = Settings.System.getInt(getContentResolver(),
                        "navigationbar_is_min", 0);
            } else {
                navigationBarIsMin = Settings.Global.getInt(getContentResolver(),
                        "navigationbar_is_min", 0);
            }

            if (navigationBarIsMin == 1) {
                //导航键隐藏了
                ViewGroup.LayoutParams params = etMsg.getLayoutParams();
                params.height = mContext.getResources().getDimensionPixelOffset(R.dimen.score_edit_height);
                etMsg.setLayoutParams(params);

            } else {
                //导航键显示了
                ViewGroup.LayoutParams params = etMsg.getLayoutParams();
                params.height = mContext.getResources().getDimensionPixelOffset(R.dimen.score_edit_height)
                        - getNavigationBarHeight(mContext);
                etMsg.setLayoutParams(params);
            }
        }
    };

}
