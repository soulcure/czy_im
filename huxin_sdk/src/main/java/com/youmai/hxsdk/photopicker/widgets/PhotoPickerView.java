package com.youmai.hxsdk.photopicker.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.youmai.hxsdk.utils.DisplayUtil;

public class PhotoPickerView extends LinearLayout {

	private Context context;
	public static final String photo_gridview_tag = "photo_gridview_tag";

	public PhotoPickerView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	private void initView() {
		setOrientation(VERTICAL);
		setBackgroundColor(Color.WHITE);

		int padding = DisplayUtil.dip2px(context, 4);
		FrameLayout layout = new FrameLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.weight = 1;
		layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
		layout.setPadding(padding, padding, padding, 0);
		addView(layout, params);

		GridView photo_gridview = new GridView(getContext());
		photo_gridview.setNumColumns(3);
		photo_gridview.setTag(photo_gridview_tag);
		photo_gridview.setVerticalSpacing(DisplayUtil.dip2px(context, 4));
		photo_gridview.setHorizontalSpacing(DisplayUtil.dip2px(context, 4));
		photo_gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		photo_gridview.setCacheColorHint(Color.TRANSPARENT);
		photo_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		FrameLayout.LayoutParams fparams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		layout.addView(photo_gridview, fparams);

		PpTabbarView view = new PpTabbarView(context);
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				DisplayUtil.dip2px(context, 60));
		addView(view, params);

	}
}
