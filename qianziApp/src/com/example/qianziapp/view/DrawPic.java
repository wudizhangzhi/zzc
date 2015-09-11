package com.example.qianziapp.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class DrawPic extends ImageView {

	int clr_bg, clr_fg;
	Paint mPaint;
	Path path;
	Bitmap cacheBitmap;
	Canvas cacheCanvas;
//	Bitmap imagebitmap;

	ShapeDrawable sharedrawable;
	Context mContext;
	
	int width;
	int height;
	
	public DrawPic(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext=context;
		
		//获取view的高宽
		ViewTreeObserver vto=this.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				DrawPic.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
			}
		});
		
		
		
		clr_bg = Color.WHITE;
		clr_fg = Color.CYAN;

		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(3);
		mPaint.setColor(clr_fg);
//		mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		path = new Path();

//		imagebitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.applog);
	
		cacheBitmap = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		uriBitmap = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
//		cacheCanvas.drawBitmap(cacheBitmap, null, null);
//		image.setBounds(0, 0, 180, 180);
//		image.draw(cacheCanvas);


	}
	
	public DrawPic(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (uriBitmap!=null) {
			canvas.drawBitmap(uriBitmap, new Matrix(),null);
		}
		Log.i("大小", canvas.getWidth()	+";"+canvas.getHeight());
		canvas.drawBitmap(cacheBitmap, 0, 0,null);
		canvas.drawPath(path, mPaint);
	}

	float curX;
	float curY;
	boolean isMoving = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			curX = x;
			curY = y;
			path.moveTo(curX, curY);
			isMoving = true;
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(curX, curY, x, y);
			curX = x;
			curY = y;

			isMoving = true;

			break;
		case MotionEvent.ACTION_UP:
			cacheCanvas.drawPath(path, mPaint);
			path.reset();
			isMoving = false;
			break;

		default:
			break;
		}
		invalidate();
		return true;
	}
	
	Bitmap uriBitmap=null;
	Canvas uriCanvas=null;
	public void ImageShowURIPic(Uri uri){
		// 创建bitmapfactory的option
					BitmapFactory.Options option = new Options();
					// 获得view的尺寸
					int width_iv = getWidth();
					int height_iv = getHeight();
					// 设置获取位图的尺寸
					option.inJustDecodeBounds = true;
					
					try {
						uriBitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, option);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					// 获取缩放比例
					int wRatio = (int) Math.ceil(option.outHeight / ((float) width_iv));
					int hRatio = (int) Math.ceil(option.outWidth / (float) height_iv);
					if (wRatio > 1 || hRatio > 1) {
						if (wRatio > hRatio) {
							option.inSampleSize = wRatio;
						} else {
							option.inSampleSize = hRatio;
						}
					}

					// 对于图像进行真正的解码
					option.inJustDecodeBounds = false;
					try {
						uriBitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, option);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					// 创建位图的bitmap
					Bitmap alteredBitmap = Bitmap.createBitmap(uriBitmap.getWidth(), uriBitmap.getHeight(), uriBitmap.getConfig());
//					cacheBitmap = Bitmap.createBitmap(uriBitmap.getWidth(), uriBitmap.getHeight(), uriBitmap.getConfig());
					// 设置画笔
//					mPaint = new Paint();
//					paint.setStrokeWidth(3);
//					paint.setColor(Color.BLUE);
					// 设置位图
					uriCanvas= new Canvas(alteredBitmap);
					Matrix matrix = new Matrix();
					uriCanvas.drawBitmap(uriBitmap, matrix, null);
					// 给图像设置bitmap
//					this.setImageBitmap(uriBitmap);
					invalidate();
	}
	
	public void saveToFile(String filename) throws FileNotFoundException{
		File file=new File(filename);
		if (file.exists()) {
			throw new RuntimeException("文件:"+filename+"已经存在");
		}
		//TODO 合并bitmap
		Bitmap mergeBitmap=Bitmap.createBitmap(uriBitmap.getWidth(), uriBitmap.getHeight(), uriBitmap.getConfig());
		Canvas c=new Canvas(mergeBitmap);
		c.drawBitmap(uriBitmap, new Matrix(), mPaint);
		c.drawBitmap(cacheBitmap, 0, 0,mPaint);
		
		FileOutputStream fos=new FileOutputStream(file);
		mergeBitmap.compress(CompressFormat.PNG, 50, fos);
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clear(){
//		cacheCanvas.drawColor(Color.WHITE);
		if (uriBitmap!=null) {
			cacheCanvas.drawBitmap(uriBitmap, new Matrix(), null);
			if (uriCanvas!=null) {
				uriCanvas.drawBitmap(uriBitmap, new Matrix(),null);
			}
		}else {
			cacheCanvas.drawColor(clr_bg);
		}
		path.reset();
		invalidate();
	}
}
