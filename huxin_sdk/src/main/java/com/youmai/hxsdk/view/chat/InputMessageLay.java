package com.youmai.hxsdk.view.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.dialog.HxRecordDialog;
import com.youmai.hxsdk.utils.AnimatorUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.view.chat.emoticon.EmoticonLayout;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonBean;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonSetBean;
import com.youmai.hxsdk.view.chat.emoticon.db.EmoticonDBHelper;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonsKeyboardBuilder;
import com.youmai.hxsdk.view.chat.emoticon.view.EmoticonsToolBarView;
import com.youmai.hxsdk.view.chat.utils.DisplayUtils;
import com.youmai.hxsdk.view.chat.utils.Utils;
import com.youmai.hxsdk.view.chat.utils.VoiceUtils;
import com.youmai.hxsdk.view.chat.view.AutoHeightLayout;
import com.youmai.hxsdk.view.chat.view.HadEditText;
import com.youmai.hxsdk.view.chat.view.RecordingView;

import java.util.ArrayList;
import java.util.Date;

/**
 * 聊天输入框
 * 包含文字、表情、语音及其它更多工具
 * Created by fylder on 2017/2/27.
 */

public class InputMessageLay extends AutoHeightLayout implements View.OnClickListener, EmoticonsToolBarView.OnToolBarItemClickListener {

    private static final String TAG = InputMessageLay.class.getName();

    public static final int FUNC_DEFAULT = 0;
    public static final int FUNC_MORE = 1;
    public static final int FUNC_VOICE = 2;
    public static final int FUNC_EMOTICON = 3;
    public int mChildViewPosition = -1; //显示哪个子视图+

    public static final int STATE_EMOTICON = 200;
    public static final int STATE_MORE = 201;
    public static final int STATE_VOICE = 202;
    public static final int STATE_EMTY = 400;

    protected int mState = STATE_EMTY;

    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_CAMERA = 2;
    public static final int TYPE_LOCATION = 3;
    public static final int TYPE_FILE = 4;
    public static final int TYPE_RED_PACKET = 5;

    private static final int VOICE_TIME = 60;//录音时长,默认60秒
    private static final int VOICE_LAST_TIME = 10;//录音剩余时长,默认10秒

    private RelativeLayout footerLay;//显示内容
    private RelativeLayout msgLay;
    private HadEditText msgEdit;//信息输入
    private RelativeLayout msgDefaultLay;
    private TextView voiceAction;//按住说话
    private ImageView voiceImg;
    private ImageView moreImg;
    private ImageView emotionImg;

    private ImageView sendBtn;

    private View moreChatView;
    private View mainLay;
    private View moreLay;
    private ImageView forwardImg;
    private ImageView garbageImg;


    private Context mContext;
    private boolean moreLocationHidden = false;//更多视图的功能隐藏，区分沟通和问题反馈的输入
    private boolean moreFileHidden = false;//更多视图的功能隐藏，区分沟通和问题反馈的输入
    private boolean moreCardHidden = false;//更多视图的功能隐藏，区分沟通和问题反馈的输入

    private boolean mIsMultimediaVisibility = true;

    private boolean isCanHidden = true;//是否允许隐藏

    private KeyBoardBarViewListener mKeyBoardBarViewListener;


    //*******Record**********
    private Handler mHandler;
    private long recordStartTime;

    private boolean isCancel = false;
    private boolean isTimeOut = false;

    private boolean isRecording = true;
    private MediaRecorder mMediaRecorder;


    private class VoiceOnTouchListener implements OnTouchListener {

        float x, y;
        int flag = -1;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                boolean isPermission = true;
                if (mKeyBoardBarViewListener != null) {
                    isPermission = mKeyBoardBarViewListener.onClickVoice();
                }

                x = event.getX();
                y = event.getY();

                if (isPermission) {
                    flag = VoiceUtils.getInstance().startRecord();//开始录音 判断录音权限是否打开
                    LogUtils.e("YW", "down flag: " + flag);
                    if (flag != -1) {
                        hxTipDialog.setRecording(1);
                        hxTipDialog.show();
                    }
                } else {
                    flag = -1;
                }


                isCancel = false;
                isTimeOut = false;
                if (flag == -1) {
                    recordState(false, false);
                    Toast.makeText(mContext, R.string.hx_voice_tip_error2, Toast.LENGTH_SHORT).show();
                    VoiceUtils.getInstance().stopRecord();
                    hxTipDialog.cancel();
                } else if (flag == 1) {
                    //开始录音
                    recordShow2();//录音动画效果
                    recordState(true, false);
                } else if (flag == 2) {
                    recordState(false, false);
                    VoiceUtils.getInstance().stopRecord();
                    hxTipDialog.cancel();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                recordState(false, false);
                if (hxTipDialog != null && hxTipDialog.isShowing()) {
                    hxTipDialog.dismiss();
                }
                if (isRecording) {
                    isRecording = false;
                    voiceEnd2(false);
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                int moveTop = 60;//40dp的范围
                float y1;
                y1 = event.getY();
                //是否超时
                if (!isTimeOut) {
                    //超出范围
                    if (Math.abs(y1 - y) > DisplayUtils.dp2px(mContext, moveTop)) {
                        if (!isCancel) {
                            isCancel = true;
                            if (isRecording) {
                                hxTipDialog.setCancel();
                                recordState(true, true);
                            }
                        }
                    } else {
                        if (isCancel && isRecording) {
                            //恢复
                            isCancel = false;
                            recordState(true, false);
                        }
                    }
                }
            }
            return true;
        }

    }


    public InputMessageLay(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getAttrs(context, attrs);
        EmoticonHandler.getInstance(mContext).loadEmoticonsToMemory();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.im_keyboard_lay, this);
        initView();

    }


    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputMessageLay);
        moreLocationHidden = typedArray.getBoolean(R.styleable.InputMessageLay_more_location_hidden, false);//更多工具中是否要显示地图
        moreFileHidden = typedArray.getBoolean(R.styleable.InputMessageLay_more_file_hidden, false);//更多工具中是否要显示文件
        moreCardHidden = typedArray.getBoolean(R.styleable.InputMessageLay_more_card_hidden, false);//更多工具中是否要显示名片
        typedArray.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    private void initView() {
        msgLay = (RelativeLayout) findViewById(R.id.keyboard_msg_lay);
        msgEdit = (HadEditText) findViewById(R.id.keyboard_msg);
        msgDefaultLay = (RelativeLayout) findViewById(R.id.keyboard_msg_default_lay);
        voiceAction = (TextView) findViewById(R.id.keyboard_msg_default_up_down);
        voiceImg = (ImageView) findViewById(R.id.keyboard_voice);
        emotionImg = (ImageView) findViewById(R.id.keyboard_emotion);
        moreImg = (ImageView) findViewById(R.id.keyboard_more);
        sendBtn = (ImageView) findViewById(R.id.keyboard_send_btn);
        footerLay = (RelativeLayout) findViewById(R.id.keyboard_footer_lay);
        mainLay = findViewById(R.id.keyboard_main_lay);
        moreLay = findViewById(R.id.keyboard_more_lay);
        forwardImg = (ImageView) findViewById(R.id.keyboard_more_forward_img);
        garbageImg = (ImageView) findViewById(R.id.keyboard_more_garbage_img);

        moreImg.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        voiceImg.setOnClickListener(this);
        emotionImg.setOnClickListener(this);
        forwardImg.setOnClickListener(this);
        garbageImg.setOnClickListener(this);

        setAutoHeightLayoutView(footerLay);
        initLay();//初始化显示输入框
        record();//按住说话
        initEdit();
        initTools();//语音、表情、更多的工具子视图
    }


    //    boolean isVoice = false;
    boolean isVoiceShow = false;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.keyboard_more) {
            // 更多
            mState = STATE_MORE;
            show(FUNC_MORE);
            String moreType = (String) moreImg.getContentDescription();
            if (mContext.getString(R.string.bar_more_type).equals(moreType)) {
                //切换关闭状态，展示菜单界面
                setEditableState(false);
                setMoreState(false);
                Utils.closeSoftKeyboard(mContext);
            } else {
                //切换更多状态，收起菜单界面，弹出软键盘
                setMoreState(true);
                setEditableState(true);
                Utils.openSoftKeyboard(msgEdit);
                show(FUNC_DEFAULT);
            }

            if (voiceImg.getContentDescription().equals(mContext.getString(R.string.bar_voice_open_type))) {
                setVoiceState(true);
                setEditableState(false);
            }
            setEmotionState(true);
            showAutoView();
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
            }
        } else if (id == R.id.keyboard_send_btn) {
            //发送
            String msg = msgEdit.getText().toString().trim();
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.onKeyBoardSendMsg(msg);
            }
            msgEdit.setText("");
            setMoreState(true);
        } else if (id == R.id.keyboard_voice) {
            //语音
            mState = STATE_VOICE;
            show(FUNC_DEFAULT);//默认布局

            // 切换显示
            setEmotionState(true);
            setMoreState(true);
            String voiceType = (String) voiceImg.getContentDescription();
            if (mContext.getString(R.string.bar_voice_open_type).equals(voiceType)) {
                //当前显示voice,此操作为切换回键盘
                isVoiceShow = true;
                setVoiceState(true);
                setEditableState(true);
                showAutoView();
                Utils.openSoftKeyboard(msgEdit);
            } else {
                //当前显示键盘,此操作为切换回voice
                setVoiceState(false);
                setEditableState(false);
                hideAutoView();
                Utils.closeSoftKeyboard(mContext);
            }
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
            }
        } else if (id == R.id.keyboard_emotion) {
            // 表情
            mState = STATE_EMOTICON;
            show(FUNC_EMOTICON);
            String emotionType = (String) emotionImg.getContentDescription();
            if (mContext.getString(R.string.bar_emotion_type).equals(emotionType)) {
                //显示表情视图，图标切换键盘状态
                setEmotionState(false);
                setEditableState(false);
                Utils.closeSoftKeyboard(mContext);
            } else {
                //显示键盘视图，图标切换表情状态
                setEmotionState(true);
                setEditableState(true);
                Utils.openSoftKeyboard(msgEdit);
                show(FUNC_DEFAULT);
            }
            setMoreState(true);
            setVoiceState(true);
            showAutoView();
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
            }
        } else if (id == R.id.keyboard_more_forward_img) {
            //转发
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.onMoreForward();
            }
        } else if (id == R.id.keyboard_more_garbage_img) {
            //垃圾删除
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.onMoreGarbage();
            }
        }
    }

    /**
     * 输入框
     */
    private void initEdit() {

        msgEdit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!msgEdit.isFocused()) {
                    msgEdit.setFocusable(true);
                    msgEdit.setFocusableInTouchMode(true);
                }
                return false;
            }
        });

        msgEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.onScroll();
                }
            }
        });
        msgEdit.setOnTextChangedInterface(new HadEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0, boolean isAt) {
                String str = arg0.toString();
                if (TextUtils.isEmpty(str)) {
                    if (mIsMultimediaVisibility) {
                        sendBtn.setVisibility(GONE);
                        moreImg.setVisibility(VISIBLE);
                    } else {
                        sendBtn.setImageResource(R.drawable.hx_im_bar_send);
                    }
                } else if (str.trim().endsWith("@")) {
                    if (isAt) {
                        if (mKeyBoardBarViewListener != null) {
                            mKeyBoardBarViewListener.onGroupAt();
                        }
                    } else {
                        if (mKeyBoardBarViewListener != null) {
                            mKeyBoardBarViewListener.onGroupAtDel();
                        }
                    }
                } else {// -> 发送
                    if (mIsMultimediaVisibility) {
                        sendBtn.setVisibility(VISIBLE);
                        moreImg.setVisibility(GONE);
                        sendBtn.setImageResource(R.drawable.hx_im_bar_send);
                    } else {
                        sendBtn.setImageResource(R.drawable.hx_im_bar_send);
                    }
                }
            }
        });
    }

    /**
     * 设置输入框的状态
     *
     * @param b 设置激活状态
     */
    public void setEditableState(boolean b) {
        if (b) {
            String emotionType = (String) emotionImg.getContentDescription();
            if (!mContext.getString(R.string.bar_emotion_keyboard_type).equals(emotionType)) {
                show(FUNC_DEFAULT);
            }
            msgEdit.setFocusable(true);
            msgEdit.setFocusableInTouchMode(true);
            msgEdit.requestFocus();
            msgLay.setBackgroundResource(R.drawable.hx_im_bar_chat_bg);
        } else {
            msgEdit.setFocusable(false);
            msgEdit.setFocusableInTouchMode(false);
            msgLay.setBackgroundResource(R.drawable.hx_im_bar_chat_bg);
        }
    }


    public void addEditText(String name) {
        String msg = msgEdit.getText().toString().trim();
        if (!msg.contains(name)) {
            msg = msg + name + " ";
            msgEdit.setText(msg);
        }
    }


    /**
     * 设置更多工具的状态
     */
    public void setMoreState(boolean isOpen) {
        if (isOpen) {
            moreImg.setImageResource(R.drawable.hx_im_bar_open);
            moreImg.setContentDescription(mContext.getString(R.string.bar_more_type));
        } else {
            moreImg.setImageResource(R.drawable.hx_im_bar_close);
            moreImg.setContentDescription(mContext.getString(R.string.bar_more_close_type));
        }

    }

    private void setEmotionState(boolean isEmotion) {
        if (isEmotion) {
            emotionImg.setImageResource(R.drawable.hx_im_bar_emotion);
            emotionImg.setContentDescription(mContext.getString(R.string.bar_emotion_type));
        } else {
            emotionImg.setImageResource(R.drawable.hx_im_bar_keyboard);
            emotionImg.setContentDescription(mContext.getString(R.string.bar_emotion_keyboard_type));
        }
    }

    private void setVoiceState(boolean isVoiceState) {
        if (isVoiceState) {
            voiceImg.setImageResource(R.drawable.hx_im_bar_voice_normal);
            voiceImg.setContentDescription(mContext.getString(R.string.bar_voice_close_type));
            msgDefaultLay.setVisibility(GONE);
            msgLay.setVisibility(VISIBLE);
        } else {
//            voiceImg.setImageResource(R.drawable.hx_im_bar_voice_press);
            voiceImg.setImageResource(R.drawable.hx_im_bar_keyboard);
            voiceImg.setContentDescription(mContext.getString(R.string.bar_voice_open_type));
            msgDefaultLay.setVisibility(VISIBLE);
            msgLay.setVisibility(GONE);
        }
    }

    private void recordState(boolean isRecording, boolean isCancel) {
        if (isRecording) {
            if (isCancel) {
                voiceAction.setText(R.string.hx_voice_tip_normal2);
            } else {
                voiceAction.setText(R.string.hx_voice_tip_normal3);
            }
            voiceAction.setTextColor(ContextCompat.getColor(mContext, R.color.hx_main_color));
            msgDefaultLay.setBackgroundResource(R.drawable.hx_im_bar_chat_press_bg);
        } else {
            voiceAction.setText(R.string.hx_voice_tip_normal);
            voiceAction.setTextColor(ContextCompat.getColor(mContext, R.color.hx_im_recording_text));
            msgDefaultLay.setBackgroundResource(R.drawable.hx_im_bar_chat_bg);
        }
    }


    public boolean keyHasFocus() {
        boolean hasFocus = false;
        if (msgEdit != null) {
            hasFocus = msgEdit.hasFocus();
        }
        return hasFocus;
    }

    public void setCanHidden(boolean b) {
        isCanHidden = b;
    }

    /**
     * 添加子布局
     *
     * @param view
     */
    public void add(View view) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        footerLay.addView(view, params);
    }

    /**
     * 显示那块布局
     *
     * @param position
     */
    public void show(int position) {

        int childCount = footerLay.getChildCount();
        if (position < childCount) {
            for (int i = 0; i < childCount; i++) {
                if (i == position) {
                    footerLay.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition = i;
                } else {
                    footerLay.getChildAt(i).setVisibility(GONE);
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (footerLay != null && footerLay.isShown()) {
                    hideAutoView();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void hideAutoView() {
        super.hideAutoView();
        //关闭工具栏布局，恢复图标状态
        setMoreState(true);
        setEmotionState(true);
//        setVoiceState(true);
    }

    @Override
    public void OnSoftKeyboardPop(int height) {
        super.OnSoftKeyboardPop(height);
        mState = STATE_EMTY;//恢复状态
        setMoreState(true);
        setEmotionState(true);
        setVoiceState(true);

        if (mKeyBoardBarViewListener != null) {
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
        }
    }

    @Override
    public void OnSoftKeyboardClose(int height) {
        super.OnSoftKeyboardClose(height);
        if (mState == STATE_MORE) {//弹出更多，键盘消失
//            mState = STATE_EMTY;//恢复状态
            mState = STATE_MORE;//恢复状态
        } else if (mState == STATE_VOICE) {
            mState = STATE_EMTY;
        } else if (mState == STATE_EMOTICON) {
            mState = STATE_EMOTICON;
        } else {
            if (isCanHidden) {
                hideAutoView();
                setEditableState(false);
            }
        }

        if (mKeyBoardBarViewListener != null) {
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
        }
    }


    @Override
    public void onSoftChangeHeight(int height) {
        super.onSoftChangeHeight(height);
    }


    /**
     * 语音、表情、更多的工具子视图
     */
    private void initTools() {
        mHandler = new Handler(Looper.getMainLooper());
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        moreChatView = inflater.inflate(R.layout.im_chat_more_lay, null);
        initMoreLay(moreChatView);
        View voiceView = inflater.inflate(R.layout.im_chat_voice_lay, null);

        add(moreChatView);
        add(voiceView);
        initEmoticons();

        show(FUNC_DEFAULT);//默认初始化为空界面
    }

    /**
     * 添加表情视图
     */
    public void initEmoticons() {
        EmoticonLayout layout = new EmoticonLayout(mContext);
        layout.setListener(new EmoticonLayout.OnEmoticonListener() {
            @Override
            public void onEmoticonItemClicked(EmoticonBean bean) {
                if (msgEdit != null) {
                    msgEdit.setFocusable(true);
                    msgEdit.setFocusableInTouchMode(true);
                    msgEdit.requestFocus();

                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        msgEdit.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    }

                    int index = msgEdit.getSelectionStart();
                    Editable editable = msgEdit.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getTag());
                    } else {
                        editable.insert(index, bean.getTag());
                    }
                }
            }
        });
        EmoticonsKeyboardBuilder builder = getBuilder(mContext);
        layout.setContents(builder, 0);
        add(layout);
    }

    /**
     * 刷新表情视图数据
     */
    public void refreshEmotion() {
        EmoticonLayout layout = (EmoticonLayout) footerLay.getChildAt(FUNC_EMOTICON);
        EmoticonsKeyboardBuilder builder = getBuilder(mContext);
        layout.refreshContents(builder);
    }


    private EmoticonsKeyboardBuilder getBuilder(Context context) {
        if (context == null) {
            throw new RuntimeException(" Context is null, cannot create db helper");
        }
        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSet();
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder().setEmoticonSetBeanList(mEmoticonSetBeanList).build();
    }

    HxRecordDialog hxTipDialog;

    /**
     * 按住说话
     */
    private void record() {

        hxTipDialog = new HxRecordDialog(mContext);

        msgDefaultLay.setOnTouchListener(new VoiceOnTouchListener());
    }


    //录音结束
    private void voiceEnd2(boolean isTimeout) {
        if (!isTimeout) {
            if (hxTipDialog != null && hxTipDialog.isShowing()) {
                hxTipDialog.dismiss();
            }
        }
        if (VoiceUtils.getInstance().isRuning()) {
            String audioPath = VoiceUtils.getInstance().stopRecord();//停止录音
            long recordStopTime = new Date().getTime();

            long t = recordStopTime - recordStartTime;

            if (!TextUtils.isEmpty(audioPath)) {
                if (audioPath.equals(VoiceUtils.ERROR)) {
                    LogUtils.w(TAG, mContext.getString(R.string.hx_voice_tip_error2));
                } else {
                    if (500 < t && t < 1000) {
                        //少于一秒录音
                        hxTipDialog.setLessTime();
                        hxTipDialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (hxTipDialog != null && hxTipDialog.isShowing() && hxTipDialog.isLess()) {
                                    hxTipDialog.dismiss();
                                }
                            }
                        }, 1500);
                    } else if (t >= 1000 && t <= VOICE_TIME * 1000) {
                        sendRecord(audioPath, t);
                    } else if (t > VOICE_TIME * 1000) {
                        //超时录音
                        sendRecord(audioPath, t);
                    }
                }
            } else if (audioPath.equals("")) {
                LogUtils.e("voice", mContext.getString(R.string.hx_voice_tip_error));
                //Toast.makeText(mContext, R.string.hx_voice_tip_error, Toast.LENGTH_SHORT).show();
            } else {
                LogUtils.w(TAG, "计算时间有误");
            }
        }
    }

    //发送语音
    void sendRecord(String audioPath, long t) {
        //正常录音
        if (isCancel) {
            //放弃录音
            Toast.makeText(mContext, R.string.hx_voice_tip_cancel, Toast.LENGTH_SHORT).show();
            VoiceUtils.getInstance().delete(audioPath);
            //mKeyBoardBarViewListener.onKeyBoardVoice("取消录音");
        } else {
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener.onKeyBoardVoice(audioPath, (int) (t / 1000));
            }
        }

    }

    /**
     * 录音动态显示
     * <p/>
     * 50ms refresh
     * <p/>
     * 在run里注意mMediaRecorder的取值，
     */
    private void recordShow2() {
        recordStartTime = new Date().getTime();
        isRecording = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMediaRecorder = VoiceUtils.getInstance().getmMediaRecorder();
                if (mMediaRecorder != null) {
                    int i = mMediaRecorder.getMaxAmplitude();
                    int radius = (int) (Math.log10(Math.max(1, i - 500)));
//                    Log.w("voice", "录音大小radius:" + radius);
//                    recordingView.animateRadius(radius);

                    long t = new Date().getTime() - recordStartTime;
                    Message message = new Message();
                    message.obj = (int) (t / 1000);
                    message.arg1 = radius;
                    boolean isUpdate = true;//是否继续更新
                    //判断录音时间

                    if (t >= VOICE_TIME * 1000) {
                        isUpdate = false;
                        isTimeOut = true;
                        message.what = 2;
                    } else {
                        message.what = 1;
                    }
                    handler.sendMessage(message);//更新录音时间
                    if (isUpdate) {
                        mHandler.postDelayed(this, RecordingView.DTIME);
                    }
                } else {
//                    recordingView.animateRadius(0);
                }
            }
        });
    }

    /**
     * 刷新时间UI
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                // 更新时间
                int rT = (int) msg.obj;
                int radius = msg.arg1;
                int t = VOICE_TIME - rT;
                if (!isCancel) {
                    if (t < VOICE_LAST_TIME) {
                        hxTipDialog.setRemainingTime(t);
                    } else {
                        hxTipDialog.setRecording(radius);
                    }
                }
//                recordTimeText.setText(String.format(mContext.getString(R.string.voice_time_tip), rT));
            } else if (msg.what == 2) {
                // 超时，重置时间
//                recordTimeText.setText(String.format(mContext.getString(R.string.voice_time_tip), 0));
                hxTipDialog.setLongTime();
                voiceEnd2(true);
            }
            return false;
        }
    });

    private void initMoreLay(View view) {
        if (moreLocationHidden) {
            moreChatView.findViewById(R.id.item_chat_more_location).setVisibility(GONE);
        } else {
            moreChatView.findViewById(R.id.item_chat_more_location).setVisibility(VISIBLE);
        }
        if (moreFileHidden) {
            moreChatView.findViewById(R.id.item_chat_more_file).setVisibility(GONE);
        } else {
            moreChatView.findViewById(R.id.item_chat_more_file).setVisibility(VISIBLE);
        }
        if (moreCardHidden) {
            moreChatView.findViewById(R.id.item_chat_more_card).setVisibility(GONE);
        } else {
            moreChatView.findViewById(R.id.item_chat_more_card).setVisibility(VISIBLE);
        }
        view.findViewById(R.id.item_chat_more_photo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mKeyBoardBarViewListener != null) {
                    //相册
                    mKeyBoardBarViewListener.onKeyBoardMore(TYPE_PHOTO);
                }
            }
        });
        view.findViewById(R.id.item_chat_more_camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mKeyBoardBarViewListener != null) {
                    //相机
                    mKeyBoardBarViewListener.onKeyBoardMore(TYPE_CAMERA);
                }
            }
        });
        view.findViewById(R.id.item_chat_more_location).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mKeyBoardBarViewListener != null) {
                    //位置
                    mKeyBoardBarViewListener.onKeyBoardMore(TYPE_LOCATION);
                }
            }
        });
        view.findViewById(R.id.item_chat_more_file).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mKeyBoardBarViewListener != null) {
                    //文件
                    mKeyBoardBarViewListener.onKeyBoardMore(TYPE_FILE);
                }
            }
        });
        view.findViewById(R.id.item_chat_more_card).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mKeyBoardBarViewListener != null) {
                    //分享名片
                    mKeyBoardBarViewListener.onKeyBoardMore(TYPE_RED_PACKET);
                }
            }
        });

    }

    public void initLay() {
        mainLay.setVisibility(VISIBLE);
        moreLay.setVisibility(GONE);
    }

    public void hide() {
        mainLay.setVisibility(GONE);
        moreLay.setVisibility(GONE);
        footerLay.setVisibility(GONE);
        moreImg.setImageResource(R.drawable.hx_im_bar_open);
        Utils.closeSoftKeyboard(mContext);
    }

    /**
     * 切换输入框和更多操作
     *
     * @param isShowMore
     */
    public void changeMoreLay(boolean isShowMore) {
        if (isShowMore) {
            mainLay.setVisibility(GONE);
            moreLay.setVisibility(INVISIBLE);
            AnimatorUtils.moveFromDown(moreLay);
            changeMoreAction(true);
        } else {
            moreLay.setVisibility(GONE);
        }
    }


    /**
     * 是否可点击
     *
     * @param isCanClick
     */
    public void changeMoreAction(boolean isCanClick) {
        forwardImg.setClickable(isCanClick);
        garbageImg.setClickable(isCanClick);
        if (isCanClick) {
            forwardImg.setImageResource(R.drawable.ic_forward_svg);
            garbageImg.setImageResource(R.drawable.ic_garbage_svg);
        } else {
            forwardImg.setImageResource(R.drawable.ic_forward_gray_svg);
            garbageImg.setImageResource(R.drawable.ic_garbage_gray_svg);
        }
    }


    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) {
        this.mKeyBoardBarViewListener = l;
    }

    @Override
    public void onToolBarItemClick(int position) {

    }

    public interface KeyBoardBarViewListener {

        void OnKeyBoardStateChange(int state, int height);

        /**
         * 录音文件
         *
         * @param voiceFile 录音文件的路径
         * @param time      录音时间
         */
        void onKeyBoardVoice(String voiceFile, int time);

        /**
         * 点击发送事件反馈
         *
         * @param msg 输入的内容
         */
        void onKeyBoardSendMsg(String msg);

        /**
         * 点击添加表情
         */
        void onKeyBoardAddEmotion();

        /**
         * 点击发送表情
         *
         * @param content    表情文本内容
         * @param refContent 表情图片资源
         */
        void onKeyBoardEmotion(String content, int refContent);

        void onKeyBoardMore(int type);

        boolean onClickVoice();

        /**
         * 点击批量的转发
         */
        void onMoreForward();

        /**
         * 点击批量的垃圾
         */
        void onMoreGarbage();

        /**
         * 点击退出驾驶模式
         */
        void onDrivingExit();

        void onGroupAt();

        void onGroupAtDel();

        void onScroll();

    }
}