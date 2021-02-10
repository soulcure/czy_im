package com.youmai.hxsdk.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.red.GrabRedPacketResult;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.im.cache.CacheMsgRedPackage;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.GsonUtil;

/*
 * 自定义圆角的dialog
 */
public class HxRedPacketDialog extends Dialog implements View.OnClickListener {

    private boolean isSinglePacket;

    private CacheMsgBean uiBean;
    private String name;
    private String avatar;
    private String value;
    private String title;
    private int redStatus;
    private String redUuid;
    private String remark;
    private String groupName;
    boolean isGroup;

    private int type;
    private ObjectAnimator anim;

    private int status;  //利是状态：-1已过期 ,0未拆开 ,1未领完 ,2已撤回 ,3已退款 ,4已领完
    private int canOpen; //是否可以开这个利是：0否1是
    private int isGrabbed; //用户是否已抢到了该利是：0否1是

    private RelativeLayout rel_packet_bg;

    private ImageView iv_avatar;
    private ImageView iv_open;

    private TextView tv_name;
    private TextView tv_msg;
    private TextView tv_title;
    private TextView tv_info;
    private TextView tv_detail;


    private double moneyDraw;
    private long startTime;

    private OnRedPacketListener mListener;

    public interface OnRedPacketListener {
        void onCloseClick();

        void onOpenClick(double moneyDraw);
    }


    public static class Builder {
        private Context context;
        private CacheMsgBean uiBean;
        private String redUuid;
        private String remark;
        private int status;
        private int canOpen;
        private int isGrabbed;
        private boolean isSinglePacket;
        private int type;
        private String groupName;
        boolean isGroup;

        private OnRedPacketListener mListener;

        public HxRedPacketDialog builder() {
            return new HxRedPacketDialog(this);
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUiBean(CacheMsgBean uiBean) {
            this.uiBean = uiBean;
            return this;
        }

        public Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public Builder setRedUuid(String redUuid) {
            this.redUuid = redUuid;
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setCanOpen(int canOpen) {
            this.canOpen = canOpen;
            return this;
        }

        public Builder setIsGrabbed(int isGrabbed) {
            this.isGrabbed = isGrabbed;
            return this;
        }

        public Builder setSinglePacket(boolean singlePacket) {
            isSinglePacket = singlePacket;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setGroup(boolean group) {
            this.isGroup = group;
            return this;
        }

        public Builder setGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public void setListener(OnRedPacketListener listener) {
            this.mListener = listener;
        }
    }


    public HxRedPacketDialog(Builder builder) {
        super(builder.context, R.style.red_packet_dialog);
        uiBean = builder.uiBean;
        remark = builder.remark;
        redUuid = builder.redUuid;
        status = builder.status;
        canOpen = builder.canOpen;
        isGrabbed = builder.isGrabbed;
        isSinglePacket = builder.isSinglePacket;
        type = builder.type;
        groupName = builder.groupName;
        isGroup = builder.isGroup;
        mListener = builder.mListener;

        CacheMsgRedPackage redPackage = (CacheMsgRedPackage) uiBean.getJsonBodyObj();
        name = uiBean.getSenderRealName();
        avatar = uiBean.getSenderAvatar();

        value = redPackage.getValue();
        title = redPackage.getRedTitle();
        redStatus = redPackage.getStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_red_packet);
        setDialogFeature();
        initView();
        loadRedPacket();
    }

    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);
    }


    private void initView() {
        iv_avatar = findViewById(R.id.iv_avatar);
        tv_name = findViewById(R.id.tv_name);
        tv_msg = findViewById(R.id.tv_msg);
        iv_open = findViewById(R.id.iv_open);

        rel_packet_bg = findViewById(R.id.rel_packet_bg);
        tv_title = findViewById(R.id.tv_title);
        tv_info = findViewById(R.id.tv_info);
        tv_detail = findViewById(R.id.tv_detail);
        tv_detail.setOnClickListener(this);
        if (type == 2) {
            tv_detail.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.iv_close).setOnClickListener(this);
        iv_open.setOnClickListener(this);

        int size = getContext().getResources().getDimensionPixelOffset(R.dimen.red_head);
        RequestOptions options = new RequestOptions()
                .override(size, size)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);


        try {
            Glide.with(getContext())
                    .load(avatar)
                    .apply(options)
                    .into(iv_avatar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_name.setText(name);
        tv_msg.setText(remark);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.iv_open) {
            reqOpenPacket(redUuid);
        } else if (id == R.id.tv_detail) {
            if (mListener != null) {
                mListener.onOpenClick(moneyDraw);
            }
            dismiss();
        }
    }


    private void startAnim() {
        if (anim == null) {
            anim = ObjectAnimator.ofFloat(iv_open, "rotationY", 0f, 360f);
        }
        anim.setDuration(1500);
        anim.setRepeatCount(ValueAnimator.INFINITE);//无限循环

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (mListener != null) {
                    mListener.onOpenClick(moneyDraw);
                }
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }


    private void stopAnim() {
        anim.cancel();
    }


    private void loadRedPacket() {
        if (status == -1) { //利是状态：-1已过期 ,0未拆开 ,1未领完 ,2已撤回 ,3已退款 ,4已领完
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(R.string.red_packet_overdue);

            tv_info.setVisibility(View.VISIBLE);
            tv_info.setText(R.string.red_packet_overdue_info);

            iv_open.setVisibility(View.INVISIBLE);
        } else if (status == 0 || status == 1) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(R.string.red_packet_can_open);
        } else if (status == 2) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(R.string.red_packet_cancel);
        } else if (status == 3) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(R.string.red_packet_back_money);
        } else if (status == 4) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(R.string.red_packet_is_open_group);

            rel_packet_bg.setBackgroundResource(R.drawable.red_packet_gone);
            iv_open.setVisibility(View.INVISIBLE);
            if (isSinglePacket) {
                if (mListener != null) {
                    mListener.onOpenClick(moneyDraw);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 1000);
            }
        }

    }

    private void reqOpenPacket(final String redUuid) {
        if (status == 0 || status == 1) {  //0未拆开 ,1未领完
            if (canOpen == 1) {  //可以开这个利是
                if (isGrabbed == 0) { //没有抢到了该利是
                    HuxinSdkManager.instance().grabRedPackage(redUuid, new IGetListener() {
                        @Override
                        public void httpReqResult(String response) {
                            final GrabRedPacketResult bean = GsonUtil.parse(response, GrabRedPacketResult.class);
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    moneyDraw = bean.getContent().getMoneyDraw();

                                    long time = System.currentTimeMillis() - startTime;
                                    if (time < 1500) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                openPacket(bean);
                                            }
                                        }, 1500 - time);
                                    } else {
                                        openPacket(bean);
                                    }
                                } else {
                                    Toast.makeText(getContext(), bean.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    startTime = System.currentTimeMillis();
                    startAnim();
                } else if (isGrabbed == 1) {  ////抢到了该利是
                    if (mListener != null) {
                        mListener.onOpenClick(moneyDraw);
                    }
                    dismiss();
                }
            }
        }
    }


    private void openPacket(GrabRedPacketResult bean) {
        stopAnim();
        double moneyDraw = bean.getContent().getMoneyDraw();

        final CacheMsgRedPackage redPackage = (CacheMsgRedPackage) uiBean.getJsonBodyObj();
        redPackage.setIsGrabbed(1);
        redPackage.setCanOpen(0);
        redPackage.setValue(String.valueOf(moneyDraw));
        uiBean.setJsonBodyObj(redPackage);

        long id = uiBean.getId();
        if (!uiBean.isRightUI()) {
            uiBean.setMsgType(CacheMsgBean.OPEN_REDPACKET);
            Intent intent = new Intent(getContext(), SendMsgService.class);
            intent.putExtra("isGroup", isGroup);
            intent.putExtra("groupName", groupName);
            intent.putExtra("id", id);
            intent.putExtra("data", uiBean);
            intent.putExtra("data_from", SendMsgService.FROM_IM);
            getContext().startService(intent);
        }
    }


}