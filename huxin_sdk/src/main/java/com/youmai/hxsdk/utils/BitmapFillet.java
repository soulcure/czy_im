package com.youmai.hxsdk.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.youmai.hxsdk.proto.YouMaiMsg;

public class BitmapFillet {

	public static final int CORNER_NONE = 0;
	public static final int CORNER_TOP_LEFT = 1;
	public static final int CORNER_TOP_RIGHT = 1 << 1;
	public static final int CORNER_BOTTOM_LEFT = 1 << 2;
	public static final int CORNER_BOTTOM_RIGHT = 1 << 3;
	public static final int CORNER_ALL = CORNER_TOP_LEFT | CORNER_TOP_RIGHT
			| CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;
	public static final int CORNER_TOP = CORNER_TOP_LEFT | CORNER_TOP_RIGHT;
	public static final int CORNER_BOTTOM = CORNER_BOTTOM_LEFT
			| CORNER_BOTTOM_RIGHT;
	public static final int CORNER_LEFT = CORNER_TOP_LEFT | CORNER_BOTTOM_LEFT;
	public static final int CORNER_RIGHT = CORNER_TOP_RIGHT
			| CORNER_BOTTOM_RIGHT;

	private static final int OFFSET = 25;//尖角宽
	private static final int SHARP_CORNER = 90;//尖角位置坐标
	private static final int MARGIN_SHAPE_CORNER = 20;//偏移量

	public static Bitmap drawRound(Bitmap bitmap, int roundPx, int corners) {
		try {
			final int width = bitmap.getWidth();
			final int height = bitmap.getHeight();

			Bitmap paintingBoard = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(paintingBoard);
			canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT,
					Color.TRANSPARENT, Color.TRANSPARENT);

			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);

			final RectF rectF = new RectF(0, 0, width, height);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			int notRoundedCorners = corners ^ CORNER_ALL;
			if ((notRoundedCorners & CORNER_TOP_LEFT) != 0) {
				clipTopLeft(canvas, paint, roundPx, width, height);
			}
			if ((notRoundedCorners & CORNER_TOP_RIGHT) != 0) {
				clipTopRight(canvas, paint, roundPx, width, height);
			}
			if ((notRoundedCorners & CORNER_BOTTOM_LEFT) != 0) {
				clipBottomLeft(canvas, paint, roundPx, width, height);
			}
			if ((notRoundedCorners & CORNER_BOTTOM_RIGHT) != 0) {
				clipBottomRight(canvas, paint, roundPx, width, height);
			}
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			final Rect src = new Rect(0, 0, width, height);
			final Rect dst = src;
			canvas.drawBitmap(bitmap, src, dst, paint);
			return paintingBoard;
		} catch (Exception exp) {
			return bitmap;
		}
	}

	private static void clipTopLeft(final Canvas canvas, final Paint paint,
									int offset, int width, int height) {
		final Rect block = new Rect(0, 0, offset, offset);
		canvas.drawRect(block, paint);
	}

	private static void clipTopRight(final Canvas canvas, final Paint paint,
									 int offset, int width, int height) {
		final Rect block = new Rect(width - offset, 0, width, offset);
		canvas.drawRect(block, paint);
	}

	private static void clipBottomLeft(final Canvas canvas, final Paint paint,
									   int offset, int width, int height) {
		final Rect block = new Rect(0, height - offset, offset, height);
		canvas.drawRect(block, paint);
	}

	private static void clipBottomRight(final Canvas canvas, final Paint paint,
										int offset, int width, int height) {
		final Rect block = new Rect(width - offset, height - offset, width,
				height);
		canvas.drawRect(block, paint);
	}

	/**
	 * 绘制成微信聊天效果
	 *
	 * @param bitmapImg
	 * @param roundPx
	 * @param direct    左边 : 1  右边：0
	 * @return
	 */
	public static Bitmap canvasTriangle(Bitmap bitmapImg, int roundPx, int direct, int msgType) {
		if (roundPx <= 0) {
			roundPx = 15;
		}
		Bitmap output = Bitmap.createBitmap(bitmapImg.getWidth(),
				bitmapImg.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xFFFFFFFF;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmapImg.getWidth(),
				bitmapImg.getHeight());
		paint.setAntiAlias(true);
		canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT,
				Color.TRANSPARENT, Color.TRANSPARENT);
		paint.setColor(color);
		// 右边
		if (direct == 0) {

			final RectF rectF = new RectF(0, 0, bitmapImg.getWidth() - OFFSET,
					bitmapImg.getHeight());
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			Path path = new Path();
			path.moveTo(bitmapImg.getWidth() - OFFSET, SHARP_CORNER - MARGIN_SHAPE_CORNER);
			path.lineTo(bitmapImg.getWidth(), SHARP_CORNER);
			path.lineTo(bitmapImg.getWidth() - OFFSET, SHARP_CORNER + MARGIN_SHAPE_CORNER);
			path.lineTo(bitmapImg.getWidth() - OFFSET, SHARP_CORNER - MARGIN_SHAPE_CORNER);
			canvas.drawPath(path, paint);
		}
		// 左边
		if (direct == 1) {

			final RectF rectF = new RectF(OFFSET, 0, bitmapImg.getWidth(),
					bitmapImg.getHeight());
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			Path path = new Path();
			path.moveTo(OFFSET, SHARP_CORNER - MARGIN_SHAPE_CORNER);
			path.lineTo(0, SHARP_CORNER);
			path.lineTo(OFFSET, SHARP_CORNER + MARGIN_SHAPE_CORNER);
			path.lineTo(OFFSET, SHARP_CORNER - MARGIN_SHAPE_CORNER);
			canvas.drawPath(path, paint);
		}

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmapImg, rect, rect, paint);

		if (direct == 0) {
			Paint arcPaint;
			Paint linePaint;
			Paint sharpPaint;
			if (msgType == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE) { // 图片类型
				arcPaint = new Paint();
				arcPaint.setAntiAlias(true);
				arcPaint.setStrokeWidth(3);
				arcPaint.setStyle(Paint.Style.STROKE);
				arcPaint.setColor(0xFFdcdcdc);

				linePaint = new Paint();
				linePaint.setAntiAlias(true);
				linePaint.setStrokeWidth(5);
				linePaint.setStyle(Paint.Style.STROKE);
				linePaint.setColor(0xFFdcdcdc);

				sharpPaint = new Paint();
				sharpPaint.setAntiAlias(true);
				sharpPaint.setStrokeWidth(2);
				sharpPaint.setStyle(Paint.Style.STROKE);
				sharpPaint.setColor(0xFFdcdcdc);
			} else {
				arcPaint = new Paint();
				arcPaint.setAntiAlias(true);
				arcPaint.setStrokeWidth(3);
				arcPaint.setStyle(Paint.Style.STROKE);
				arcPaint.setColor(0xFF39a6ff);

				linePaint = new Paint();
				linePaint.setAntiAlias(true);
				linePaint.setStrokeWidth(5);
				linePaint.setStyle(Paint.Style.STROKE);
				linePaint.setColor(0xFF39a6ff);

				sharpPaint = new Paint();
				sharpPaint.setAntiAlias(true);
				sharpPaint.setStrokeWidth(2);
				sharpPaint.setStyle(Paint.Style.STROKE);
				sharpPaint.setColor(0xFF39a6ff);
			}

			canvas.drawArc( //左上
					new RectF(
							0,
							0,
							roundPx * 2,
							roundPx * 2),
					180, 90, false, arcPaint);
			canvas.drawArc( //右上
					new RectF(
							bitmapImg.getWidth() - OFFSET - roundPx * 2,
							0,
							bitmapImg.getWidth() - OFFSET,
							roundPx * 2),
					270, 90, false, arcPaint);

			canvas.drawArc( //右下
					new RectF(
							bitmapImg.getWidth() - OFFSET - roundPx * 2,
							bitmapImg.getHeight() - roundPx * 2,
							bitmapImg.getWidth() - OFFSET,
							bitmapImg.getHeight()),
					0, 90, false, arcPaint);

			canvas.drawArc( //左下
					new RectF(
							0,
							bitmapImg.getHeight() - roundPx * 2,
							roundPx * 2,
							bitmapImg.getHeight()),
					90, 90, false, arcPaint);

			Path path = new Path();

			path.moveTo(bitmapImg.getWidth() - OFFSET - roundPx + 3, bitmapImg.getHeight());//下边
			path.lineTo(roundPx - 3, bitmapImg.getHeight());

			path.moveTo(0, bitmapImg.getHeight() - roundPx + 3);//左边
			path.lineTo(0, roundPx - 3);

			path.moveTo(roundPx - 3, 0);//上边 左-3右+3
			path.lineTo(bitmapImg.getWidth() - OFFSET - roundPx + 3, 0);

			canvas.drawPath(path, linePaint);

			path.moveTo(bitmapImg.getWidth() - OFFSET, roundPx);//右上
			path.lineTo(bitmapImg.getWidth() - OFFSET, SHARP_CORNER - MARGIN_SHAPE_CORNER);

			path.moveTo(bitmapImg.getWidth() - OFFSET, SHARP_CORNER - MARGIN_SHAPE_CORNER);//右下
			path.lineTo(bitmapImg.getWidth(), SHARP_CORNER);
			path.lineTo(bitmapImg.getWidth() - OFFSET, SHARP_CORNER + MARGIN_SHAPE_CORNER);
			path.lineTo(bitmapImg.getWidth() - OFFSET, bitmapImg.getHeight() - roundPx);

			canvas.drawPath(path, sharpPaint);
		}

		if (direct == 1) {

			Paint arcPaint = new Paint();
			arcPaint.setAntiAlias(true);
			arcPaint.setStrokeWidth(3);
			arcPaint.setStyle(Paint.Style.STROKE);
			arcPaint.setColor(0xFFdcdcdc);

			Paint linePaint = new Paint();
			linePaint.setAntiAlias(true);
			linePaint.setStrokeWidth(5);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setColor(0xFFdcdcdc);

			Paint sharpPaint = new Paint();
			sharpPaint.setAntiAlias(true);
			sharpPaint.setStrokeWidth(2);
			sharpPaint.setStyle(Paint.Style.STROKE);
			sharpPaint.setColor(0xFFdcdcdc);

			canvas.drawArc( //左上
					new RectF(
							OFFSET,
							0,
							OFFSET + roundPx * 2,
							roundPx * 2),
					180, 90, false, arcPaint);
			canvas.drawArc( //右上
					new RectF(
							bitmapImg.getWidth() - roundPx * 2,
							0,
							bitmapImg.getWidth(),
							roundPx * 2),
					270, 90, false, arcPaint);

			canvas.drawArc( //右下
					new RectF(
							bitmapImg.getWidth() - roundPx * 2,
							bitmapImg.getHeight() - roundPx * 2,
							bitmapImg.getWidth(),
							bitmapImg.getHeight()),
					0, 90, false, arcPaint);

			canvas.drawArc( //左下
					new RectF(
							OFFSET,
							bitmapImg.getHeight() - roundPx * 2,
							OFFSET + roundPx * 2,
							bitmapImg.getHeight()),
					90, 90, false, arcPaint);

			Path path = new Path();

			path.moveTo(bitmapImg.getWidth() - roundPx + 3, bitmapImg.getHeight());//下边
			path.lineTo(OFFSET + roundPx - 3, bitmapImg.getHeight());

			path.moveTo(bitmapImg.getWidth(), bitmapImg.getHeight() - roundPx);//右边
			path.lineTo(bitmapImg.getWidth(), roundPx);

			path.moveTo(OFFSET + roundPx - 3, 0);//上边 左-3右+3
			path.lineTo(bitmapImg.getWidth() - roundPx + 3, 0);

			canvas.drawPath(path, linePaint);

			path.moveTo(OFFSET, roundPx);//左边
			path.lineTo(OFFSET, SHARP_CORNER - MARGIN_SHAPE_CORNER);
			path.lineTo(0, SHARP_CORNER);
			path.lineTo(OFFSET, SHARP_CORNER + MARGIN_SHAPE_CORNER);
			path.lineTo(OFFSET, bitmapImg.getHeight() - roundPx);

			canvas.drawPath(path, sharpPaint);
		}

		return output;
	}

}
