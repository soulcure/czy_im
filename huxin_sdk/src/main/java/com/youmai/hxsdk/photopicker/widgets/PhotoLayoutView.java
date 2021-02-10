package com.youmai.hxsdk.photopicker.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.youmai.hxsdk.R;

public class PhotoLayoutView extends GridSingleFrameLayoutView {

	private Context context;
	
	public static final String imageview_photo_tag = "imageview_photo_tag";
	public static final String mask_tag = "mask_tag";
	public static final String checkmark_tag = "checkmark_tag";
	public static final String wrap_layout_tag = "wrap_layout_tag";
	
	public PhotoLayoutView(Context context) {
		super(context);
		this.context = context;
		initView();
	}
	
	private ImageView checkmark;

	private void initView(){
		setTag(wrap_layout_tag);
		SquareImageView imageview_photo = new SquareImageView(context);
		imageview_photo.setId(imageview_photo_tag.hashCode());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		imageview_photo.setScaleType(ScaleType.CENTER_CROP);
		imageview_photo.setBackgroundColor(Color.WHITE);
		addView(imageview_photo,params);
		
		View view = new View(context);
		view.setTag(mask_tag);
		view.setBackgroundColor(Color.parseColor("#22000000"));
		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		addView(view,params);
		
		checkmark = new ImageView(context);
		checkmark.setId(checkmark_tag.hashCode());
		params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		checkmark.setImageDrawable(context.getResources().getDrawable(R.drawable.hx_btn_pic_pressed));
		addView(checkmark,params);
	}

	public ImageView getCheckmark() {
		return checkmark;
	}

}
