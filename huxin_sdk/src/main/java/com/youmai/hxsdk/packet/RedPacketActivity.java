package com.youmai.hxsdk.packet;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.dialog.HxPayPasswordDialog;
import com.youmai.hxsdk.entity.red.RedPackageList;
import com.youmai.hxsdk.entity.red.SendRedPacketResult;
import com.youmai.hxsdk.entity.red.StandardRedPackage;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.ToastUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketActivity extends SdkBaseActivity implements View.OnClickListener {

    public static final String TAG = RedPacketActivity.class.getSimpleName();

    public static final String TARGET_ID = "target_id";
    public static final String TARGET_NAME = "target_name";
    public static final String TARGET_AVATAR = "target_avatar";


    private Context mContext;
    private TextView tv_error;
    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;

    private TextView tv_value;

    private ImageView img_head;
    private TextView tv_name;
    private AppCompatEditText et_money;
    private TextView tv_money;
    private AppCompatEditText et_msg;

    private Button btn_commit;
    private String uuid;
    private String name;
    private String avatar;

    private double moneyMax;
    private double money;
    private String pano;

    private String moneyStr;

    private StandardRedPackage.ContentBean.FixedConfigBean fixedConfig;
    //private StandardRedPackage.ContentBean.RandomConfigBean randomConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet);
        mContext = this;

        uuid = getIntent().getStringExtra(TARGET_ID);
        name = getIntent().getStringExtra(TARGET_NAME);
        avatar = getIntent().getStringExtra(TARGET_AVATAR);

        initView();
        loadRedPacket();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        img_head = (ImageView) findViewById(R.id.img_head);
        int size = getResources().getDimensionPixelOffset(R.dimen.red_head);
        Glide.with(this).load(avatar)
                .apply(new RequestOptions()
                        .transform(new GlideRoundTransform())
                        .override(size, size)
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(img_head);

        tv_error = (TextView) findViewById(R.id.tv_error);
        tv_value = (TextView) findViewById(R.id.tv_value);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(name);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("彩利是");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("利是记录");
        tv_right.setOnClickListener(this);

        tv_money = (TextView) findViewById(R.id.tv_money);
        et_msg = (AppCompatEditText) findViewById(R.id.et_msg);

        et_money = (AppCompatEditText) findViewById(R.id.et_money);
        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    money = Double.parseDouble(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    money = 0;
                }
                if (money > moneyMax) {
                    tv_error.setVisibility(View.VISIBLE);
                    btn_commit.setEnabled(false);
                    return;
                } else if (money == 0) {
                    btn_commit.setEnabled(false);
                    tv_money.setText("");
                    return;
                } else {
                    btn_commit.setEnabled(true);
                    tv_error.setVisibility(View.INVISIBLE);
                }

                DecimalFormat format = new DecimalFormat("0.00");
                moneyStr = format.format(money);

                //moneyStr = s.toString();
                tv_money.setText(moneyStr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);
        btn_commit.setEnabled(false);
    }


    private void loadRedPacket() {
        HuxinSdkManager.instance().reqRedPackageStandardConfig(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                StandardRedPackage bean = GsonUtil.parse(response, StandardRedPackage.class);
                if (bean != null) {
                    if (bean.isSuccess()) {
                        fixedConfig = bean.getContent().getFixedConfig();
                    } else {
                        Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        HuxinSdkManager.instance().reqRedPackageList(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPackageList bean = GsonUtil.parse(response, RedPackageList.class);
                if (bean != null) {
                    if (bean.isSuccess()) {
                        List<RedPackageList.ContentBean> list = bean.getContent();

                        RedPackageList.ContentBean contentBean = null;

                        if (!ListUtils.isEmpty(list)) {
                            contentBean = list.get(0);
                        }

                        if (contentBean != null) {
                            pano = contentBean.getPano();
                            String balance = contentBean.getBalance();
                            tv_value.setText(balance);

                            try {
                                moneyMax = Double.parseDouble(balance);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }


                    } else {
                        Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showMsg(response);
                }
            }
        });
    }

    private void showMsg(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.optInt("code");
            if (code != 0) {
                ToastUtil.showBottomToast(mContext, jsonObject.optString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_commit) {

            if (pano == null) {
                Toast.makeText(mContext, "查询饭票数据发生错误", Toast.LENGTH_SHORT).show();
                return;
            } else if (fixedConfig == null) {
                Toast.makeText(mContext, "利是配置接口失败，暂不支持发利是，请退出重试", Toast.LENGTH_SHORT).show();
                return;
            } else if (money == 0) {
                Toast.makeText(mContext, "请添加利是金额", Toast.LENGTH_SHORT).show();
                return;
            } else if (money > fixedConfig.getMoneyMax()) {
                String tips = "超过利是最大金额" + fixedConfig.getMoneyMax() + "请重新设置";
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                return;
            } else if (money < fixedConfig.getMoneyMin()) {
                String tips = "小于利是最小金额" + fixedConfig.getMoneyMin() + "请重新设置";
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                return;
            } else if (money > moneyMax) {
                Toast.makeText(mContext, "超过了您的利是余额，请重新设置", Toast.LENGTH_SHORT).show();
                return;
            }

            final HxPayPasswordDialog dialog = new HxPayPasswordDialog(this);
            dialog.setOnFinishInput(new HxPayPasswordDialog.OnPasswordInputFinish() {
                @Override
                public void inputFinish() {

                    String remark = et_msg.getText().toString().trim();

                    if (TextUtils.isEmpty(remark)) {
                        remark = et_msg.getHint().toString().trim();
                    }

                    String password = dialog.getStrPassword();
                    final String title = remark;

                    showProgressDialog();
                    HuxinSdkManager.instance().reqSendSingleRedPackage(money, title, pano, password, new IGetListener() {
                        @Override
                        public void httpReqResult(String response) {
                            SendRedPacketResult bean = GsonUtil.parse(response, SendRedPacketResult.class);
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    dialog.dismiss();
                                    String redUuid = bean.getContent().getLishiUuid();
                                    Intent intent = new Intent();
                                    intent.putExtra("value", String.valueOf(money));
                                    intent.putExtra("redTitle", title);
                                    intent.putExtra("redUuid", redUuid);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                } else if (bean.getCode() == 503) {
                                    dialog.pwError();
                                } else {
                                    Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            dismissProgressDialog();

                        }
                    });
                }

                @Override
                public void forgetPassWord() {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("温馨提示");
                    builder.setMessage("请前往我的页面找回支付密码");
                    builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
            });
            dialog.show();
            dialog.setMoney(moneyStr);
        } else if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {
            startActivity(new Intent(this, RedPacketHistoryActivity.class));
        }
    }
}
