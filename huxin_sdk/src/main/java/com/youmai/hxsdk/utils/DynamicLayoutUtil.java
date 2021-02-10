package com.youmai.hxsdk.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * 动态布局工具类
 * 
 * @Title：
 * @Description：
 * @date
 * @author 黄卫峰
 * @doc:
 * @version 1.0
 */
public class DynamicLayoutUtil {
	private static Bitmap image = null;
	/**
	 * 父线性动态布局
	 * 
	 * @param context
	 * @return
	 */
	public static LinearLayout ParentLinearLayout(Context context) {
		LinearLayout mParentLayout = new LinearLayout(context);
		mParentLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams mLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}

	/**
	 * 线性布局
	 *
	 * @param context
	 *            上下文
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param orientation
	 *            方向（linearlayout）
	 * @param gravity
	 * @param visibility
	 * @return
	 */
	public static LinearLayout StandardLinearLayout(Context context, int width,
			int height, int orientation, int gravity, int visibility, int weight) {
		LinearLayout mParentLayout = new LinearLayout(context);

		mParentLayout.setOrientation(orientation);
		mParentLayout.setGravity(gravity);
		mParentLayout.setVisibility(visibility);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.weight = weight;

		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}

	/**
	 * 线性布局
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param orientation
	 * @param gravity
	 * @param visibility
	 * @param weight
	 * @param paddingLeft
	 * @param paddingTop
	 * @param paddingRight
	 * @param paddingBottom
	 * @return
	 */
	public static LinearLayout StandardLinearLayout(Context context, int width,
			int height, int orientation, int gravity, int visibility,
			int weight, String BGColor, int paddingLeft, int paddingTop,
			int paddingRight, int paddingBottom) {
		LinearLayout mParentLayout = new LinearLayout(context);

		mParentLayout.setOrientation(orientation);
		mParentLayout.setGravity(gravity);
		mParentLayout.setVisibility(visibility);
		if (!TextUtils.isEmpty(BGColor)) {
			mParentLayout.setBackgroundColor(Color.parseColor(BGColor));
		}

		mParentLayout.setPadding(paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.weight = weight;

		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}

	/**
	 * 线性布局
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param orientation
	 * @param visibility
	 * @return
	 */
	public static LinearLayout StandardLinearLayout(Context context, int width,
			int height, int orientation, int visibility) {
		LinearLayout mParentLayout = new LinearLayout(context);
		mParentLayout.setOrientation(orientation);
		mParentLayout.setVisibility(visibility);
		LayoutParams mLayoutParams = new LayoutParams(width, height);
		mParentLayout.setLayoutParams(mLayoutParams);

		return mParentLayout;
	}

	/**
	 * 线性布局
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param orientation
	 * @param gravity
	 * @param normal
	 * @param select
	 * @param left
	 *            marginLeft
	 * @param top
	 *            marginTop
	 * @param right
	 *            marginRight
	 * @param bottom
	 *            marginBottom
	 * @return
	 */
	public static LinearLayout StandardLinearLayout(Context context, int width,
			int height, int orientation, int gravity, BitmapDrawable normal,
			BitmapDrawable select, int left, int top, int right, int bottom) {
		LinearLayout mParentLayout = new LinearLayout(context);
		mParentLayout.setOrientation(orientation);
		mParentLayout.setGravity(gravity);

		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.setMargins(left, top, right, bottom);
		mParentLayout.setLayoutParams(mLayoutParams);
		if (!(normal == null)) {
			mParentLayout.setBackgroundDrawable(addStateDrawableStandard(
					context, normal, select, select));
		}

		return mParentLayout;
	}

	/**
	 * 线性布局
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param orientation
	 * @param backgroundColor
	 * @param gravity
	 * @param visibility
	 * @param clickable
	 * @param left
	 *            paddingLeft
	 * @param top
	 *            padding top
	 * @param right
	 *            padding right
	 * @param bottom
	 *            padding bottom
	 * @return
	 */
	public static LinearLayout DetailSetLinearLayout(Context context,
			int width, int height, int orientation, String backgroundColor,
			int gravity, int visibility, boolean clickable, int left, int top,
			int right, int bottom) {
		LinearLayout mParentLayout = new LinearLayout(context);
		mParentLayout.setBackgroundColor(Color.parseColor(backgroundColor));
		mParentLayout.setGravity(gravity);
		mParentLayout.setOrientation(orientation);
		mParentLayout.setVisibility(visibility);
		mParentLayout.setClickable(clickable);//
		mParentLayout.setPadding(left, top, right, bottom);
		LayoutParams mLayoutParams = new LayoutParams(width, height);

		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}

	/**
	 * 相对布局
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param backgroundColor
	 * @param parentPostion1
	 *            addrule
	 * @param parentPostion2
	 *            addrule
	 * @return
	 */
	public static RelativeLayout StandardRelativeLayout(Context context,
			int width, int height, String backgroundColor, int parentPostion1,
			int parentPostion2) {
		RelativeLayout mParentLayout = new RelativeLayout(context);
		mParentLayout.setBackgroundColor(Color.parseColor(backgroundColor));

		RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(
				width, height);
		// mLayoutParams.addRule(parentPostion1);
		// mLayoutParams.addRule(parentPostion2);
		// mLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		// mLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}

	/**
	 * 帧布局
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param gravity
	 * @param clickable
	 * @return
	 */
	public static FrameLayout standardFrameLayout(Context context, int width,
			int height, int gravity, int weight, boolean clickable) {
		FrameLayout mParentLayout = new FrameLayout(context);
		mParentLayout.setClickable(clickable);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.weight = weight;
		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}

	/**
	 * 创建listView(framelayout)
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param clickable
	 * @param selector
	 * @return
	 */
	public static ListView standardListView(Context context, int width,
			int height, boolean clickable, int selector) {
		ListView mParentLayout = new ListView(context);
		mParentLayout.setClickable(clickable);
		mParentLayout.setDivider(null);
		mParentLayout.setSelector(new ColorDrawable(selector));
		FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(
				width, height);

		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}
	/**创建listView(LinearLayout)
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param clickable
	 * @param selector
	 * @param weight
	 * @return
	 */
	public static ListView standardListView(Context context, int width,
			int height, boolean clickable, int selector,int weight) {
		ListView mParentLayout = new ListView(context);
		mParentLayout.setClickable(clickable);
		mParentLayout.setDivider(null);
		mParentLayout.setSelector(new ColorDrawable(selector));
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.weight = weight;
		mParentLayout.setLayoutParams(mLayoutParams);
		return mParentLayout;
	}

	/**
	 * 创建gridview
	 * @param context
	 * @param width
	 * @param height
	 * @param numColumns
	 * @param selector
	 * @param horizontalSpacing
	 * @param verticalSpacing
	 * @return
	 */
	public static GridView createGridView(Context context, int width,
			int height,int numColumns,Drawable selector,int horizontalSpacing,int verticalSpacing){
		GridView gridView = new GridView(context);
		gridView.setHorizontalSpacing(horizontalSpacing);
		gridView.setNumColumns(numColumns);
		gridView.setVerticalSpacing(verticalSpacing);
//		gridView.setSelector(selector);
		FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(
				width, height);
		gridView.setLayoutParams(mLayoutParams);
		return gridView;

	}

	/**
	 * 创建textView 在relativeLayout下
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param text
	 * @param TextColor
	 * @param textSize
	 * @param gravity
	 * @param singleLine
	 * @param parentPostion1
	 * @param left
	 * @param right
	 * @return
	 */
	public static TextView StandardTextView(Context context, int width,
			int height, String text, String TextColor, int textSize,
			int gravity, Boolean singleLine, int parentPostion1, int left,
			int right) {
		TextView mTextView = new TextView(context);
		mTextView.setText(text);
		mTextView.setTextColor(Color.parseColor(TextColor));
		mTextView.setTextSize(textSize);
		mTextView.setGravity(gravity);
		mTextView.setEllipsize(TruncateAt.MIDDLE);
		mTextView.setSingleLine(singleLine);

		RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(
				width, height);
		mLayoutParams.addRule(parentPostion1);
		// mLayoutParams.addRule(parentPostion2);
		// mLayoutParams.addRule(parentPostion3);
		mLayoutParams.setMargins(left, 0, right, 0);
		mTextView.setLayoutParams(mLayoutParams);
		return mTextView;
	}

	/**
	 * 创建textview于LinearLayout中
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param text
	 * @param TextColor
	 * @param textSize
	 * @param gravity
	 * @param weight
	 * @param singleLine
	 * @param paddingLeft
	 * @param paddingTop
	 * @param paddingRight
	 * @param paddingBottom
	 * @return
	 */
	public static TextView StandardTextView(Context context, int width,
			int height, String text, String TextColor, int textSize,
			int gravity, int weight, Boolean singleLine, int paddingLeft,
			int paddingTop, int paddingRight, int paddingBottom) {
		TextView mTextView = new TextView(context);
		mTextView.setText(text);
		mTextView.setTextColor(Color.parseColor(TextColor));
		mTextView.setTextSize(textSize);
		mTextView.setGravity(gravity);
		mTextView.setEllipsize(TruncateAt.MIDDLE);
		mTextView.setSingleLine(singleLine);
		mTextView.setPadding(paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.weight = weight;
		mTextView.setLayoutParams(mLayoutParams);
		return mTextView;
	}
	/**
	 * 创建textview于LinearLayout中
	 *
	 * @param context
	 * @param width
	 * @param height
	 * @param text
	 * @param TextColor
	 * @param textSize
	 * @param gravity
	 * @param weight
	 * @param singleLine
	 * @param paddingLeft
	 * @param paddingTop
	 * @param paddingRight
	 * @param paddingBottom
	 * @param marginLeft
	 * @param marginTop
	 * @param marginRight
	 * @param marginBottom
	 * @return
	 */
	public static TextView StandardTextView(Context context, int width,
			int height, String text, String TextColor, int textSize,
			int gravity, int weight, Boolean singleLine, int paddingLeft,
			int paddingTop, int paddingRight, int paddingBottom,int marginLeft,
			int marginTop, int marginRight, int marginBottom) {
		TextView mTextView = new TextView(context);
		mTextView.setText(text);
		mTextView.setTextColor(Color.parseColor(TextColor));
		mTextView.setTextSize(textSize);
		mTextView.setGravity(gravity);
		mTextView.setEllipsize(TruncateAt.MIDDLE);
		mTextView.setSingleLine(singleLine);
		mTextView.setPadding(paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
		mLayoutParams.weight = weight;
		mTextView.setLayoutParams(mLayoutParams);
		return mTextView;
	}

	/**
	 * CreateImageView
	 *
	 * @param context
	 * @param parentRelativeLayout
	 * @param width
	 * @param height
	 * @param normal
	 *            imageView 无动作时显示的图片
	 * @param select
	 *            imageView 按下时显示的图片
	 * @param parentPostion1
	 *            在ParentView 的位置1
	 * @param parentPostion2
	 *            在ParentView 的位置1
	 * @param marginLeft
	 */
	public static ImageView standardImageView(Context context,
			RelativeLayout parentRelativeLayout, int width, int height,
			ScaleType scaleType, BitmapDrawable normal, BitmapDrawable select,
			int parentPostion1, int parentPostion2, int marginLeft,
			int marginTop, int marginRight, int marginBottom) {
		ImageView mImageView = new ImageView(context);
		mImageView.setScaleType(scaleType);
		// mImageView.setImageBitmap(getImageFromAssetsFile(context,
		// ImageName));
		// StateListDrawable mstaStateListDrawable = addStateDrawable(context,
		// normal, select, select);
		mImageView.setBackgroundDrawable(addStateDrawableStandard(context,
				normal, select, select));
		RelativeLayout.LayoutParams mImageViewParams = new RelativeLayout.LayoutParams(
				width, height);
		mImageViewParams.addRule(parentPostion1);
		mImageViewParams.addRule(parentPostion2);
		mImageViewParams.addRule(RelativeLayout.CENTER_VERTICAL);

		mImageViewParams.setMargins(marginLeft, marginTop, marginRight,
				marginBottom);
		parentRelativeLayout.addView(mImageView, mImageViewParams);
		return mImageView;
	}

	/**
	 * 创建imageview
	 *
	 * @param context
	 * @param parentLinearLayout
	 * @param width
	 * @param height
	 * @param visibility
	 * @param ImageName
	 * @param paddingLeft
	 * @param paddingTop
	 * @param paddingRight
	 * @param paddingBottom
	 * @param marginLeft
	 * @param marginLTop
	 * @param marginRight
	 * @param marginBottom
	 * @return
	 */
	public static ImageView standardImageView(Context context,
			LinearLayout parentLinearLayout, int width, int height,
			int visibility, String ImageName, int paddingLeft, int paddingTop,
			int paddingRight, int paddingBottom, int marginLeft,
			int marginLTop, int marginRight, int marginBottom) {
		ImageView mImageView = new ImageView(context);
		mImageView.setImageBitmap(getImageFromAssetsFileNoClick(context,
				ImageName));
		mImageView.setPadding(paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		mImageView.setVisibility(visibility);
		LinearLayout.LayoutParams mImageViewParams = new LinearLayout.LayoutParams(
				width, height);

		mImageViewParams.setMargins(marginLeft, marginLTop, marginRight,
				marginBottom);

		parentLinearLayout.addView(mImageView, mImageViewParams);
		return mImageView;
	}


	/**
	 * 创建imageview
	 * @param context
	 * @param parentFrameLayout
	 * @param width
	 * @param height
	 * @param visibility
	 * @param ImageName
	 * @param gravity
	 * @param paddingLeft
	 * @param paddingTop
	 * @param paddingRight
	 * @param paddingBottom
	 * @param marginLeft
	 * @param marginLTop
	 * @param marginRight
	 * @param marginBottom
	 * @return
	 */
	public static ImageView standardImageView(Context context,
			FrameLayout parentFrameLayout, int width, int height,
			int visibility, String ImageName,int gravity, int  paddingLeft, int paddingTop,
			int paddingRight, int paddingBottom, int marginLeft,
			int marginLTop, int marginRight, int marginBottom) {
		ImageView mImageView = new ImageView(context);
		mImageView.setImageBitmap(getImageFromAssetsFileNoClick(context,
				ImageName));
		mImageView.setPadding(paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		mImageView.setVisibility(visibility);
		FrameLayout.LayoutParams mImageViewParams = new FrameLayout.LayoutParams(
				width, height);
		mImageViewParams.gravity = gravity;
		mImageViewParams.setMargins(marginLeft, marginLTop, marginRight,
				marginBottom);

		parentFrameLayout.addView(mImageView, mImageViewParams);
		return mImageView;
	}

	/**
	 * 创建Imageview (点击效果为shape)
	 *
	 * @param context
	 * @param parentLinearLayout
	 * @param width
	 * @param height
	 * @param visibility
	 * @param ImageName
	 * @param normal
	 * @param select
	 * @param focused
	 * @param paddingLeft
	 * @param paddingTop
	 * @param paddingRight
	 * @param paddingBottom
	 * @return
	 */
	public static ImageView standardImageView(Context context,
			LinearLayout parentLinearLayout, int width, int height,
			int visibility, String ImageName, Drawable normal, Drawable select,
			Drawable focused, int paddingLeft, int paddingTop,
			int paddingRight, int paddingBottom) {
		ImageView mImageView = new ImageView(context);
		mImageView.setImageBitmap(getImageFromAssetsFileNoClick(context,
				ImageName));
		mImageView.setPadding(paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		mImageView.setVisibility(visibility);
		LinearLayout.LayoutParams mImageViewParams = new LinearLayout.LayoutParams(
				width, height);

		// mImageViewParams.setMargins(marginLeft, 0, 0, 0);
		mImageView.setBackgroundDrawable(addStateDrawableShape(context, normal,
				select, focused));
		parentLinearLayout.addView(mImageView, mImageViewParams);
		return mImageView;
	}

	/**
	 * imageView在线性布局中
	 *
	 * @param context
	 * @param parentLinearLayout
	 * @param width
	 * @param height
	 * @param gravity
	 * @param normal
	 * @param select
	 * @return
	 */
	public static ImageView standardImageView(Context context,
			LinearLayout parentLinearLayout, int width, int height,
			int gravity, BitmapDrawable normal, BitmapDrawable select,
			int marginLeft, int marginLTop, int marginRight, int marginBottom) {
		ImageView mImageView = new ImageView(context);

		LinearLayout.LayoutParams mImageViewParams = new LinearLayout.LayoutParams(
				width, height);
		mImageViewParams.gravity = gravity;
		mImageViewParams.setMargins(marginLeft, marginLTop, marginRight,
				marginBottom);
		mImageView.setBackgroundDrawable(addStateDrawableStandard(context,
				normal, select, select));
		parentLinearLayout.addView(mImageView, mImageViewParams);
		return mImageView;
	}

	/**
	 * imageView在线性布局中
	 *
	 * @param context
	 * @param parentLinearLayout
	 * @param width
	 * @param height
	 * @param gravity
	 * @param normal
	 * @param select
	 * @param focus
	 * @return
	 */
	public static ImageView standardImageViewForHuxin(Context context,
			LinearLayout parentLinearLayout, int width, int height,
			int gravity, BitmapDrawable normal, BitmapDrawable select,
			BitmapDrawable focus, int marginLeft, int marginLTop,
			int marginRight, int marginBottom) {
		ImageView mImageView = new ImageView(context);

		LinearLayout.LayoutParams mImageViewParams = new LinearLayout.LayoutParams(
				width, height);
		mImageViewParams.gravity = gravity;
		mImageViewParams.setMargins(marginLeft, marginLTop, marginRight,
				marginBottom);
		mImageView.setBackgroundDrawable(addStateDrawable(context, normal,
				select, focus));
		parentLinearLayout.addView(mImageView, mImageViewParams);
		return mImageView;
	}

	/**
	 * Button在线性布局中
	 * @param context
	 * @param width
	 * @param height
	 * @param text
	 * @param color
	 * @param size
	 * @param visibility
	 * @param normal
	 * @param select
	 * @param focus
	 * @param marginLeft
	 * @param marginLTop
	 * @param marginRight
	 * @param marginBottom
	 * @return
	 */
	public static Button standardButton(Context context,
			 int width, int height,String text,int color,int size,int visibility,
			 BitmapDrawable normal, BitmapDrawable select,
			BitmapDrawable focus, int marginLeft, int marginLTop,
			int marginRight, int marginBottom) {
		Button mButton = new Button(context);
		mButton.setText(text);
		mButton.setTextColor(color);
		mButton.setTextSize(size);
		mButton.setVisibility(visibility);
		LinearLayout.LayoutParams mButtonParams = new LinearLayout.LayoutParams(
				width, height);
//		mButtonParams.gravity = gravity;
		mButtonParams.setMargins(marginLeft, marginLTop, marginRight,
				marginBottom);
		mButton.setBackgroundDrawable(addStateDrawableStandard(context, normal,
				select, focus));
		mButton.setLayoutParams(mButtonParams);
		return mButton;
	}

	/**
	 * 线性布局中创建EditText
	 * 
	 * @param context
	 * @param width
	 * @param height
	 * @param gravity
	 * @param weight
	 * @param inputType
	 * @param ems
	 * @param textSize
	 * @param singleLine
	 * @param background
	 * @param paddingLeft
	 * @param paddingTop
	 * @param paddingRight
	 * @param paddingBottom
	 * @param marginLeft
	 * @param marginTop
	 * @param marginRight
	 * @param marginBottom
	 * @return
	 */
	public static EditText standardEditText(Context context, int width,
			int height, int gravity, int weight, int inputType, int ems,
			int textSize, boolean singleLine, Drawable background,
			int paddingLeft, int paddingTop, int paddingRight,
			int paddingBottom, int marginLeft, int marginTop, int marginRight,
			int marginBottom) {
		EditText mEditText = new EditText(context);
		mEditText.setInputType(inputType);
		mEditText.setEms(ems);
		mEditText.setSingleLine(singleLine);
		mEditText.setTextSize(textSize);
		mEditText.setPadding(paddingLeft, paddingTop, paddingRight,
				paddingBottom);
		mEditText.setBackgroundDrawable(background);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				width, height);
		mLayoutParams.weight = weight;
		mLayoutParams.gravity = gravity;
		mLayoutParams.setMargins(marginLeft, marginTop, marginRight,
				marginBottom);
		mEditText.setLayoutParams(mLayoutParams);
		return mEditText;
	}

	/**
	 * 在帧布局中创建ListControllerView
	 * 
	 * @param context
	 * @param width
	 * @param height
	 * @param gravity
	 * @param visibility
	 * @return
	 */
	// public static ListControllerView stardardListControllerView(
	// Context context, int width, int height, int gravity, int visibility) {
	// ListControllerView mlControllerView = new ListControllerView(context);
	// FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(
	// width, height);
	// mLayoutParams.gravity = gravity;
	//
	// mlControllerView.setLayoutParams(mLayoutParams);
	// mlControllerView.setVisibility(visibility);
	// return mlControllerView;
	// }

	/**
	 * dp to px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (int) (dipValue * (scale / 160) + 0.5f);
	}

	/**
	 * px to dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (int) ((pxValue * 160) / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		// final float fontScale =
		// context.getResources().getDisplayMetrics().scaledDensity;
		// return (int) (spValue * fontScale / 2 + 0.5f);
		// final float fontScale =
		// context.getResources().getDisplayMetrics().scaledDensity;
		// System.out.println("++++++++++scaledDensity++++++++++++++"+fontScale);
		// return (int) (spValue * fontScale / 1.5 - 6);
		int size = 14;
		final float fontScale = screenScaleValue(context);
		if (fontScale >= 480) {
			size = (int) (spValue * fontScale / 400 + 0.5f);
		} else {
			size = (int) (spValue * fontScale / 320 + 0.5f);
		}

		return size;
	}

	// /**
	// * 将px值转换为dip或dp值，保证尺寸大小不变
	// *
	// * @param pxValue
	// * @param scale
	// * （DisplayMetrics类中属性density）
	// * @return
	// */
	// public static int px2dip(Context context, float pxValue) {
	// final float scale = context.getResources().getDisplayMetrics().density;
	// return (int) (pxValue / scale + 0.5f);
	// }
	//

	/*
	 * 从Assets中读取图片src没有点击效果
	 */
	public static Bitmap getImageFromAssetsFileNoClick(Context context,
			String fileName) {
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
			// float scale = screenScaleValue(context);
			// image = Bitmap.createScaledBitmap(image, (int) (image.getWidth()
			// * (scale / 160) + 0.5f), (int) (image.getHeight()
			// * (scale / 160) + 0.5f), true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;

	}

	/*
	 * 从Assets中读取图片src没有点击效果
	 */
	public static Bitmap getImageFromAssetsDpi(Context context, String fileName) {
		Bitmap image = null;

		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
			float scale = screenScaleValue(context);
			if (scale >= 480) {
				image = Bitmap.createScaledBitmap(image,
						(int) (image.getWidth() * (scale / 320)),
						(int) (image.getHeight() * (scale / 320)), true);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;

	}
	/*
	 * 从Assets中读取图片src没有点击效果
	 */
	public static Drawable getNineImageFromAssets(Context context, String fileName) {
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] chunk = image.getNinePatchChunk();
		boolean bResult = NinePatch.isNinePatchChunk(chunk);
		NinePatchDrawable patchy = new NinePatchDrawable(image, chunk,new Rect(), null);
		
		return patchy;
		
	}

	/**
	 * 创建Selector
	 * 
	 * @param context
	 * @param normal
	 * @param select
	 * @param Focused
	 * @return
	 */
	private static StateListDrawable addStateDrawable(Context context,
			BitmapDrawable normal, BitmapDrawable select, BitmapDrawable Focused) {
		StateListDrawable sd = new StateListDrawable();
		// Drawable normal = idNormal == -1 ? null :
		// context.getResources().getDrawable(idNormal);
		// Drawable pressed = idPressed == -1 ? null :
		// context.getResources().getDrawable(idPressed);
		// Drawable focus = idFocused == -1 ? null :
		// context.getResources().getDrawable(idFocused);
		// 注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
		// 所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
		int selected = android.R.attr.state_selected;
		int enabled = android.R.attr.state_enabled;
		int focused = android.R.attr.state_focused;
		int pressed = android.R.attr.state_pressed;
		sd.addState(new int[] { enabled, selected }, normal);
		sd.addState(new int[] { enabled, -selected }, select);
		sd.addState(new int[] { -enabled }, Focused);
		// sd.addState(new int[] { android.R.attr.state_focused }, Focused);
		// sd.addState(new int[] { android.R.attr.state_pressed }, select);
		// sd.addState(new int[] { android.R.attr.state_enabled }, select);
		sd.addState(new int[] {}, select);
		return sd;

	}

	/**
	 * @param context
	 *            上下文对象
	 * @param pressd
	 *            按下时显示的图片名称
	 * @param focused
	 *            选中时显示的图片名称
	 * @param unenable
	 *            不能点击时的图片名称
	 * @param nomal
	 *            正常状态时的图片名称
	 * @return
	 */
	public static StateListDrawable getStateDrawable(Context context,
			String pressd, String focused, String unenable, String nomal) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_pressed },
				getDrawableFromAssets(context, pressd));
		sd.addState(new int[] { android.R.attr.state_focused },
				getDrawableFromAssets(context, focused));
		sd.addState(new int[] { -android.R.attr.state_enabled },
				getDrawableFromAssets(context, unenable));
		sd.addState(new int[] {}, getDrawableFromAssets(context, nomal));
		return sd;
	}

	/**
	 * @param
	 *
	 * @param pressd
	 *            按下时显示的图片
	 * @param focused
	 *            选中时显示的图片
	 * @param unenable
	 *            不能点击时的图片
	 * @param nomal
	 *            正常状态时的图片
	 * @return
	 */
	public static StateListDrawable getStateDrawable(Drawable pressd,
			Drawable focused, Drawable unenable, Drawable nomal) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_pressed }, pressd);
		sd.addState(new int[] { android.R.attr.state_focused }, focused);
		sd.addState(new int[] { -android.R.attr.state_enabled }, unenable);
		sd.addState(new int[] {}, nomal);
		return sd;
	}
	
	
	 /**
	  * 设置不同状态时其文字颜色。 
	  * @param normal 
	  * @param pressed
	  * @param focused
	  * @param
	  * @return
	  */
    public static  ColorStateList createColorStateList(String normal, String pressed, String focused) {
    	int c_pressed = Color.parseColor(pressed);
    	int c_focused = Color.parseColor(focused);
    	int c_normal = Color.parseColor(normal);
        int[] colors = new int[] { c_pressed, c_focused, c_normal, c_focused, c_normal };  
        int[][] states = new int[5][];  
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };  
        states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };  
        states[2] = new int[] { android.R.attr.state_enabled };  
        states[3] = new int[] { android.R.attr.state_focused };  
        states[4] = new int[] {};  
        ColorStateList colorList = new ColorStateList(states, colors);  
        return colorList;  
    }  

	/**
	 * 创建Selector
	 * 
	 * @param context
	 * @param normal
	 * @param select
	 * @param Focused
	 * @return
	 */
	public static StateListDrawable addStateDrawableStandard(Context context,
			BitmapDrawable normal, BitmapDrawable select, BitmapDrawable Focused) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_enabled,
				android.R.attr.state_focused }, Focused);
		sd.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, select);
		sd.addState(new int[] { android.R.attr.state_focused }, Focused);
		sd.addState(new int[] { android.R.attr.state_pressed }, select);
		sd.addState(new int[] { android.R.attr.state_enabled }, normal);
		sd.addState(new int[] {}, normal);
		
		return sd;

	}

	/**
	 * 创建selector shape 方式
	 * 
	 * @param context
	 * @param normal
	 * @param select
	 * @param Focused
	 * @return
	 */
	public static StateListDrawable addStateDrawableShape(Context context,
			Drawable normal, Drawable select, Drawable Focused) {
		StateListDrawable sd = new StateListDrawable();
		// Drawable normal = idNormal == -1 ? null :
		// context.getResources().getDrawable(idNormal);
		// Drawable pressed = idPressed == -1 ? null :
		// context.getResources().getDrawable(idPressed);
		// Drawable focus = idFocused == -1 ? null :
		// context.getResources().getDrawable(idFocused);
		// 注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
		// 所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
		sd.addState(new int[] { android.R.attr.state_enabled,
				android.R.attr.state_focused }, Focused);
		sd.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, select);
		sd.addState(new int[] { android.R.attr.state_focused }, Focused);
		sd.addState(new int[] { android.R.attr.state_pressed }, select);
		sd.addState(new int[] { android.R.attr.state_enabled }, normal);
		sd.addState(new int[] {}, normal);
		return sd;

	}
	public static StateListDrawable addStateDrawableToCheckBox(
			Drawable checked, Drawable unchecked) {
		StateListDrawable sd = new StateListDrawable();
		
		
		sd.addState(new int[] { android.R.attr.state_checked }, checked);
	
		sd.addState(new int[] {}, unchecked);
		return sd;
		
	}

	/**
	 * 创建 Shape
	 * 
	 * @param strokeWidth
	 * @param strokeColor
	 * @param solidColor
	 * @param roundRadius
	 * @return
	 */
	public static GradientDrawable createShape(int strokeWidth,
			String strokeColor, String solidColor, int roundRadius) {
		// int strokeWidth = 5; // 3dp 边框宽度
		// int roundRadius = 15; // 8dp 圆角半径
		// int strokeColor = Color.parseColor("#2E3135");//边框颜色
		// int solidColor = Color.parseColor("#DFDFE0");//内部填充颜色
		GradientDrawable gd = new GradientDrawable();// 创建drawable
		gd.setColor(Color.parseColor(solidColor));
		gd.setCornerRadius(roundRadius);
		if (null != strokeColor)
			gd.setStroke(strokeWidth, Color.parseColor(strokeColor));
		return gd;
	}
	
	public static GradientDrawable createShape(int strokeWidth,
			String strokeColor, String solidColor, int roundRadius,int shape) {
		GradientDrawable gd = new GradientDrawable();// 创建drawable
		gd.setColor(Color.parseColor(solidColor));
		gd.setShape(shape);
		gd.setCornerRadius(roundRadius);
		if (null != strokeColor)
			gd.setStroke(strokeWidth, Color.parseColor(strokeColor));
		return gd;
	}
	
	public static GradientDrawable createCircleShape(int strokeWidth,
			String strokeColor, String solidColor, int roundRadius,int radius) {
		GradientDrawable gd = new GradientDrawable();// 创建drawable
		gd.setShape(GradientDrawable.OVAL);
		gd.setColor(Color.parseColor(solidColor));
//		gd.setCornerRadius(roundRadius);
		if (null != strokeColor)
			gd.setStroke(strokeWidth, Color.parseColor(strokeColor));
		gd.setSize(radius, radius);
		return gd;
	}
	
	
	
	public static GradientDrawable createShapeToRect(
			 String solidColor,int gradient ) {
		// int strokeWidth = 5; // 3dp 边框宽度
		// int roundRadius = 15; // 8dp 圆角半径
		// int strokeColor = Color.parseColor("#2E3135");//边框颜色
		// int solidColor = Color.parseColor("#DFDFE0");//内部填充颜色
		GradientDrawable gd = new GradientDrawable();// 创建drawable
		gd.setColor(Color.parseColor(solidColor));
		gd.setGradientType(gradient);
//		gd.setGradientType(GradientDrawable.RECTANGLE);
		
		return gd;
	}
	public static GradientDrawable createShapeToRect(
			String solidColor,int gradient,int strokeWidth,
			String strokeColor) {
		// int strokeWidth = 5; // 3dp 边框宽度
		// int roundRadius = 15; // 8dp 圆角半径
		// int strokeColor = Color.parseColor("#2E3135");//边框颜色
		// int solidColor = Color.parseColor("#DFDFE0");//内部填充颜色
		GradientDrawable gd = new GradientDrawable();// 创建drawable
		gd.setColor(Color.parseColor(solidColor));
		gd.setGradientType(gradient);
		
		if (null != strokeColor)
			gd.setStroke(strokeWidth, Color.parseColor(strokeColor));
		
		return gd;
	}
	
	
	
	public static float screenScaleValue(Context context){

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.densityDpi;
	}
	
	/**
	 * 创建旋转动画
	 * @param fromDegrees
	 * @param toDegrees
	 * @param pivotXValue
	 * @param pivotYValue
	 * @return
	 */
	public static RotateAnimation createRotateAnim(float fromDegrees, float toDegrees,  float pivotXValue,  float pivotYValue){
		
		RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,  pivotXValue,  pivotYValue);
		return rotateAnimation;
		
	}
	
	

	/**
	 * 从assets目录下面获取drawable
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static Drawable getDrawableFromAssets(Context context, String name) {
		Drawable drawable =null;
		try {
			InputStream stream = context.getAssets().open(name);
			drawable = Drawable.createFromStream(stream, name);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}

	/**
	 * 从assets目录下面获取drawable
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static Bitmap getBitmapFromAssets(Context context, String name) {
		Bitmap bitmap=null;
		try {
			InputStream stream = context.getAssets().open(name);
			bitmap = BitmapFactory.decodeStream(stream);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	
	public static Drawable getBgDrawable(){
		Drawable drawable = new ColorDrawable(Color.WHITE);
		Drawable press = new ColorDrawable(Color.parseColor("#e6e6e6"));
		return getStateDrawable(press, press, press, drawable);
	}

}
