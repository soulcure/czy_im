package com.youmai.hxsdk.view.datewheel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.view.datewheel.wheelview.OnWheelChangedListener;
import com.youmai.hxsdk.view.datewheel.wheelview.OnWheelScrollListener;
import com.youmai.hxsdk.view.datewheel.wheelview.WheelView;
import com.youmai.hxsdk.view.datewheel.wheelview.adapter.AbstractWheelTextAdapter1;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 作者：create by YW
 * 日期：2017.02.14 18:24
 * 描述：日期三级联动 Dialog
 */
public class ChangeDateDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private WheelView wvYear;
    private WheelView wvMonth;
    private WheelView wvDay;

    private TextView btnSure;
    private TextView btnCancel;

    private ArrayList<String> array_years = new ArrayList<String>();
    private ArrayList<String> array_months = new ArrayList<String>();
    private ArrayList<String> array_days = new ArrayList<String>();
    private ChangeDateDialog.CalendarTextAdapter mYearAdapter;
    private ChangeDateDialog.CalendarTextAdapter mMonthAdapter;
    private ChangeDateDialog.CalendarTextAdapter mDayAdapter;

    private String month;
    private String day;

    private String currentYear = getYear();
    private String currentMonth = getMonth();
    private String currentDay = getDay();

    private int maxTextSize = 24;
    private int minTextSize = 14;

    private boolean isSetData = false;

    private String selectYear;
    private String selectMonth;
    private String selectDay;

    private static final int minYear = 1900;
    private int curDay = 0;

    private OnBirthListener onBirthListener;

    public ChangeDateDialog(Context context) {
        super(context, R.style.hx_app_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_userinfo_changebirth);
        initView(savedInstanceState);
        setDialogFeature();
    }

    /**
     * 设置对话框特征
     */
    protected void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }


    private void initView(Bundle savedInstanceState) {

        wvYear = (WheelView) findViewById(R.id.wv_birth_year);
        wvMonth = (WheelView) findViewById(R.id.wv_birth_month);
        wvDay = (WheelView) findViewById(R.id.wv_birth_day);
        btnSure = (TextView) findViewById(R.id.btn_info_sure);
        btnCancel = (TextView) findViewById(R.id.btn_info_cancel);

        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (!isSetData) {
            initData();
        }
        initYears();
        mYearAdapter = new CalendarTextAdapter(mContext, array_years, setYear(currentYear), maxTextSize, minTextSize);
        wvYear.setVisibleItems(5);
        wvYear.setViewAdapter(mYearAdapter);
        wvYear.setCurrentItem(setYear(currentYear));

        initMonths(Integer.parseInt(month));
        mMonthAdapter = new CalendarTextAdapter(mContext, array_months, setMonth(currentMonth), maxTextSize, minTextSize);
        wvMonth.setVisibleItems(5);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(setMonth(currentMonth));
        wvMonth.setCyclic(true);

        initDays(Integer.parseInt(day));
        mDayAdapter = new CalendarTextAdapter(mContext, array_days, Integer.parseInt(currentDay) - 1, maxTextSize, minTextSize);
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDayAdapter);
        wvDay.setCurrentItem(Integer.parseInt(currentDay) - 1);
        wvDay.setCyclic(true);

        wvYear.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                selectYear = currentText;
                setTextViewSize(currentText, mYearAdapter);
                currentYear = currentText.replace(mContext.getString(R.string.hx_wheel_year), "");
                setYear(currentYear);

                if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) > Integer.parseInt(getYear())
                        || Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) <= minYear) {
                    outTextViewSize(currentText, mYearAdapter);
                }

                yearControlDay();
            }
        });

        wvYear.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextViewSize(currentText, mYearAdapter);

                if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) >= Integer.parseInt(getYear())) {
                    setYMD(getYear(), getMonth(), getDay());
                } else if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) <= minYear) {
                    setYMD("1900", "1", "1");
                }
            }
        });

        wvMonth.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                selectMonth = currentText;
                setTextViewSize(currentText, mMonthAdapter);
                setMonth(selectMonth.replace(mContext.getString(R.string.hx_wheel_month), ""));
                initDays(Integer.parseInt(day));

                monthControlDay();

                if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) >= Integer.parseInt(getYear())
                        && Integer.parseInt(selectMonth.replace(mContext.getString(R.string.hx_wheel_month), "")) > Integer.parseInt(getMonth())
                        || Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) <= minYear) {
                    outTextViewSize(currentText, mMonthAdapter);
                }

            }
        });

        wvMonth.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextViewSize(currentText, mMonthAdapter);

                if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) >= Integer.parseInt(getYear())
                        && Integer.parseInt(selectMonth.replace(mContext.getString(R.string.hx_wheel_month), "")) >= Integer.parseInt(getMonth())) {
                    setYMD(getYear(), getMonth(), getDay());
                } else if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) <= minYear) {
                    setYMD("1900", "1", "1");
                }
            }
        });

        wvDay.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mDayAdapter.getItemText(wheel.getCurrentItem());
                setTextViewSize(currentText, mDayAdapter);
                selectDay = currentText;
                curDay = wheel.getCurrentItem();

                if ((Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) >= Integer.parseInt(getYear())
                        && Integer.parseInt(selectMonth.replace(mContext.getString(R.string.hx_wheel_month), "")) >= Integer.parseInt(getMonth())
                        && Integer.parseInt(selectDay.replace(mContext.getString(R.string.hx_wheel_day), "")) > Integer.parseInt(getDay()))
                        || Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) <= minYear) {
                    outTextViewSize(currentText, mDayAdapter);
                }
            }
        });

        wvDay.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mDayAdapter.getItemText(wheel.getCurrentItem());
                setTextViewSize(currentText, mDayAdapter);

                if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) >= Integer.parseInt(getYear())
                        && Integer.parseInt(selectMonth.replace(mContext.getString(R.string.hx_wheel_month), "")) >= Integer.parseInt(getMonth())
                        && Integer.parseInt(selectDay.replace(mContext.getString(R.string.hx_wheel_day), "")) >= Integer.parseInt(getDay())) {
                    setYMD(getYear(), getMonth(), getDay());
                } else if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) <= minYear) {
                    setYMD("1900", "1", "1");
                }
            }
        });

    }

    public void initYears() {
        for (int i = 9999; i > 100; i--) {
            array_years.add(i + mContext.getString(R.string.hx_wheel_year));
        }
    }

    public void initMonths(int months) {
        array_months.clear();
        for (int i = 1; i <= 12; i++) {
            array_months.add(i + mContext.getString(R.string.hx_wheel_month));
        }
    }

    public void initDays(int days) {
        array_days.clear();
        days = calDay(selectYear.replace(mContext.getString(R.string.hx_wheel_year), ""),
                selectMonth.replace(mContext.getString(R.string.hx_wheel_month), ""));
        for (int i = 1; i <= days; i++) {
            array_days.add(i + mContext.getString(R.string.hx_wheel_day));
        }
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter1 {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxSize, int minSize) {
            super(context, R.layout.hx_userinfo_birth_year, NO_RESOURCE, currentItem, maxSize, minSize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    public void setBirthdayListener(OnBirthListener onBirthListener) {
        this.onBirthListener = onBirthListener;
    }

    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (onBirthListener != null) {
                onBirthListener.onClick(selectYear, selectMonth, selectDay);
                Log.d("cy", "" + selectYear + "" + selectMonth + "" + selectDay);
            }
        } else if (v == btnCancel) {

        }
        dismiss();

    }

    public interface OnBirthListener {
        void onClick(String year, String month, String day);
    }

    /**
     * 设置字体大小
     *
     * @param currentItemText
     * @param adapter
     */
    public void setTextViewSize(String currentItemText, ChangeDateDialog.CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textView = (TextView) arrayList.get(i);
            currentText = textView.getText().toString();
            if (currentItemText.equals(currentText)) {
                textView.setTextSize(maxTextSize);
            } else {
                textView.setTextSize(minTextSize);
            }
        }
    }

    /**
     * 设置字体大小
     *
     * @param currentItemText
     * @param adapter
     */
    public void outTextViewSize(String currentItemText, ChangeDateDialog.CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textView = (TextView) arrayList.get(i);
            textView.setTextColor(Color.GRAY);
            currentText = textView.getText().toString();
            if (currentItemText.equals(currentText)) {
                textView.setTextSize(maxTextSize);
            } else {
                textView.setTextSize(minTextSize);
            }
        }
    }

    public String getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "";
    }

    public String getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1 + "";
    }

    public String getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE) + "";
    }

    public void initData() {
        setDate(getYear(), getMonth(), getDay());
        this.currentDay = 1 + "";
        this.currentMonth = 1 + "";
    }

    /**
     * 设置年月日
     *
     * @param year
     * @param month
     * @param day
     */
    public void setDate(String year, String month, String day) {
        selectYear = year + mContext.getString(R.string.hx_wheel_year);
        selectMonth = month + mContext.getString(R.string.hx_wheel_month);
        selectDay = day + mContext.getString(R.string.hx_wheel_day);
        isSetData = true;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentDay = day;
        if (year.equals(getYear())) {
            this.month = getMonth();
        } else {
            this.month = 12 + "";
        }
        calDays(year, month);
    }

    /**
     * 设置年份
     *
     * @param year
     */
    public int setYear(String year) {
        int yearIndex = 0;
        if (!year.equals(getYear())) {
            this.month = 12 + "";
        } else {
            this.month = getMonth();
        }
        for (int i = 9999; i > 100; i--) {
            if (i == Integer.parseInt(year)) {
                return yearIndex;
            }
            yearIndex++;
        }
        return yearIndex;
    }

    /**
     * 设置月份
     *
     * @param month
     * @param month
     * @return
     */
    public int setMonth(String month) {
        int monthIndex = 0;
        calDays(currentYear, month);
        for (int i = 1; i < Integer.parseInt(this.month); i++) {
            if (Integer.parseInt(month) == i) {
                return monthIndex;
            } else {
                monthIndex++;
            }
        }
        return monthIndex;
    }

    /**
     * 计算每月多少天
     *
     * @param month
     * @param year
     */
    public void calDays(String year, String month) {
        boolean leaYear = false;
        leaYear = Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0;
        for (int i = 1; i <= 12; i++) {
            switch (Integer.parseInt(month)) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    this.day = 31 + "";
                    break;
                case 2:
                    if (leaYear) {
                        this.day = 29 + "";
                    } else {
                        this.day = 28 + "";
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    this.day = 30 + "";
                    break;
            }
        }
        if (year.equals(getYear()) && month.equals(getMonth())) {
            this.day = getDay();
        }
    }

    /**
     * @param month
     * @param year
     * @return 当月天数
     */
    public int calDay(String year, String month) {
        boolean leaYear = false;
        leaYear = Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0;
        for (int i = 1; i <= 12; i++) {
            switch (Integer.parseInt(month)) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 31;
                case 2:
                    if (leaYear) {
                        return 29;
                    } else {
                        return 28;
                    }
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
            }
        }
        if (year.equals(getYear()) && month.equals(getMonth())) {
            this.day = getDay();
        }
        return Integer.parseInt(day);
    }

    private void setYMD(String y, String m, String d) {
        mYearAdapter = new CalendarTextAdapter(mContext, array_years, setYear(y), maxTextSize, minTextSize);
        wvYear.setVisibleItems(5);
        wvYear.setViewAdapter(mYearAdapter);
        wvYear.setCurrentItem(setYear(y));

        initMonths(Integer.parseInt(month));
        mMonthAdapter = new CalendarTextAdapter(mContext, array_months, setMonth(m), maxTextSize, minTextSize);
        wvMonth.setVisibleItems(5);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(setMonth(m));
        wvMonth.setCyclic(true);

        initDays(calDay(y, m));
        mDayAdapter = new CalendarTextAdapter(mContext, array_days, Integer.parseInt(d) - 1, maxTextSize, minTextSize);
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDayAdapter);
        wvDay.setCurrentItem(Integer.parseInt(d) - 1);
        wvDay.setCyclic(true);
    }

    private void yearControlDay() {
        int calDay = calDay(currentYear, selectMonth.replace(mContext.getString(R.string.hx_wheel_month), ""));
        if ((calDay == 29 && selectMonth.equals("2" + mContext.getString(R.string.hx_wheel_month)))
                || (calDay == 28 && selectMonth.equals("2" + mContext.getString(R.string.hx_wheel_month)))) {
            if (calDay >= 28 && curDay == 28) {
                curDay = 0;
            }
            wvDay.setCurrentItem(curDay);
        } else {
            wvDay.setCurrentItem(curDay);
        }
        String cur = (String) mDayAdapter.getItemText(curDay);
        initDays(calDay(selectYear.replace(mContext.getString(R.string.hx_wheel_year), ""), selectMonth.replace(mContext.getString(R.string.hx_wheel_month), "")));
        mDayAdapter = new CalendarTextAdapter(mContext, array_days, curDay, maxTextSize, minTextSize);
        setTextViewSize(cur, mDayAdapter);
        wvDay.setViewAdapter(mDayAdapter);
    }

    private void monthControlDay() {
        int calDay = calDay(currentYear, selectMonth.replace(mContext.getString(R.string.hx_wheel_month), ""));
        if ((calDay == 29 && selectMonth.equals("2" + mContext.getString(R.string.hx_wheel_month)))
                || (calDay == 28 && selectMonth.equals("2" + mContext.getString(R.string.hx_wheel_month)))) {
            if (curDay > 28) {
                curDay = 0;
            }
            wvDay.setCurrentItem(curDay);
        } else {
            wvDay.setCurrentItem(curDay);
        }
        String cur = (String) mDayAdapter.getItemText(curDay);
        setTextViewSize(cur, mDayAdapter);

        initDays(calDay(selectYear.replace(mContext.getString(R.string.hx_wheel_year), ""), selectMonth.replace(mContext.getString(R.string.hx_wheel_month), "")));
        mDayAdapter = new CalendarTextAdapter(mContext, array_days, curDay, maxTextSize, minTextSize);
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDayAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) > Integer.parseInt(getYear())) {
                setYMD(getYear(), getMonth(), getDay());
            } else if (Integer.parseInt(selectYear.replace(mContext.getString(R.string.hx_wheel_year), "")) <= minYear) {
                setYMD("1900", "1", "1");
            }
        }
    }
}
