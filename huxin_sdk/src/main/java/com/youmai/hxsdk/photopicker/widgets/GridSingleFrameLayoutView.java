package com.youmai.hxsdk.photopicker.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GridSingleFrameLayoutView extends FrameLayout implements Checkable {
//	private TextView tv_name;
//	private TextView tv_body;
	private boolean mChecked;
private ImageView view;
	

	public GridSingleFrameLayoutView(Context context) {
		super(context);
		initView(context);
	}

	public GridSingleFrameLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		
		
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		mChecked = checked;
//		setBackgroundColor(checked ? Color.parseColor("#ff7630"):Color.parseColor("#ffffff"));
		view = (ImageView) getChildAt(getChildCount()-1);
		if (checked) {
			view.setVisibility(View.VISIBLE);
		}else {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public void toggle() {
		setChecked(!mChecked); 
	}

}
