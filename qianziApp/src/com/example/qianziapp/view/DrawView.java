package com.example.qianziapp.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.preference.PreferenceManager.OnActivityStopListener;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class DrawView extends ImageView {

	Paint mPaint;// 画笔
	Path path;// 路径
	Bitmap cacheBitmap;// 缓存位图
	Canvas cacheCanvas;// 缓存画布

	private int clr_bg, clr_fg;

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);

		clr_bg = Color.WHITE;
		clr_fg = Color.CYAN;
		// 初始化画笔
		mPaint = new Paint();
		// Dither（图像的抖动处理，当每个颜色值以低于8位表示时，对应图像做抖动处理可以实现在可显示颜色总数比较低（比如256色）时还保持较好的显示效果：
		mPaint.setDither(true);
		mPaint.setAntiAlias(true);// 抗锯齿
		mPaint.setStyle(Paint.Style.STROKE);// 画轮廓
		mPaint.setStrokeWidth(3);// 线条宽度
		mPaint.setAlpha(255);

		path = new Path();
		// 创建位图、画布，绘底色
		cacheBitmap = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
	}

	public DrawView(Context context) {
		super(context);
	}

	/**
	 * invalidate时候会调用
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 绘制上一次，否则不连贯
		// canvas.
		// 因为手指抬起后会重置path,所以需要先绘制上次保存的状态
		canvas.drawBitmap(cacheBitmap, 0, 0, null);
		// 绘制路径
		canvas.drawPath(path, mPaint);
	}

	/**
	 * 清空画布
	 */
	// TODO 如何清空
	public void clear() {
		// 清屏
		Paint p = new Paint();
		p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		cacheCanvas.drawPaint(p);
		// p.setXfermode(new PorterDuffXfermode(Mode.SRC));
		// path.reset();
		// cacheBitmap = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		// cacheCanvas.drawColor(clr_bg);
		// cacheCanvas.drawColor(Color.TRANSPARENT);
		invalidate();
	}

	private float curX, curY;
	public boolean isMoving;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			curX = x;
			curY = y;
			path.moveTo(x, y);
			isMoving = true;
			break;
		case MotionEvent.ACTION_MOVE:
			// if (!isMoving) {
			// break;
			// }
			path.quadTo(curX, curY, x, y);
			// 效果一样的方法
			// path.lineTo(x, y);
			curX = x;
			curY = y;

			break;
		case MotionEvent.ACTION_UP:
			// 保存最后的状态
			cacheCanvas.drawPath(path, mPaint);
			// 重置画笔
			path.reset();
			isMoving = false;
			break;
		default:
			break;
		}
		invalidate();
		return true;
	}

	/**
	 * 保存图片方法
	 * 
	 * @param filename
	 */
	public void saveToFile(String filename) throws FileNotFoundException {
		File file = new File(filename);
		if (file.exists()) {
			throw new RuntimeException("文件:" + filename + "已经存在");
		}
		FileOutputStream fos = new FileOutputStream(new File(filename));
		cacheBitmap.compress(CompressFormat.PNG, 50, fos);
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bitmap getPathBitmap() {
		return cacheBitmap;
	}

}
