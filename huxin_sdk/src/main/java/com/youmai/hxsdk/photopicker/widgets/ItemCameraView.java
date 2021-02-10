package com.youmai.hxsdk.photopicker.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.DisplayUtil;

public class ItemCameraView extends LinearLayout{

	private Context context;
	
	public ItemCameraView(Context context) {
		super(context);
		this.context = context;
		initView();
	}
	
	private void initView(){
		setGravity(Gravity.CENTER);
		setOrientation(VERTICAL);
		
		ImageView imageView = new ImageView(context);
		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.hx_pp_ic_camera));
		addView(imageView);
		
		TextView tv = new TextView(context);
		tv.setGravity(Gravity.CENTER);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.topMargin = DisplayUtil.dip2px(context, 8);
		tv.setTextSize(Color.parseColor("#e0e0e0"));
		tv.setText(R.string.hx_sdk_media_take_photo);
		addView(tv,params);
	}

}
