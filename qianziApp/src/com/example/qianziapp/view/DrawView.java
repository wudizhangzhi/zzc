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

	Paint mPaint;// ����
	Path path;// ·��
	Bitmap cacheBitmap;// ����λͼ
	Canvas cacheCanvas;// ���滭��

	private int clr_bg, clr_fg;

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);

		clr_bg = Color.WHITE;
		clr_fg = Color.CYAN;
		// ��ʼ������
		mPaint = new Paint();
		// Dither��ͼ��Ķ���������ÿ����ɫֵ�Ե���8λ��ʾʱ����Ӧͼ���������������ʵ���ڿ���ʾ��ɫ�����Ƚϵͣ�����256ɫ��ʱ�����ֽϺõ���ʾЧ����
		mPaint.setDither(true);
		mPaint.setAntiAlias(true);// �����
		mPaint.setStyle(Paint.Style.STROKE);// ������
		mPaint.setStrokeWidth(3);// �������
		mPaint.setAlpha(255);

		path = new Path();
		// ����λͼ�����������ɫ
		cacheBitmap = Bitmap.createBitmap(480, 800, Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
	}

	public DrawView(Context context) {
		super(context);
	}

	/**
	 * invalidateʱ������
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// ������һ�Σ���������
		// canvas.
		// ��Ϊ��ָ̧��������path,������Ҫ�Ȼ����ϴα����״̬
		canvas.drawBitmap(cacheBitmap, 0, 0, null);
		// ����·��
		canvas.drawPath(path, mPaint);
	}

	/**
	 * ��ջ���
	 */
	// TODO ������
	public void clear() {
		// ����
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
			// Ч��һ���ķ���
			// path.lineTo(x, y);
			curX = x;
			curY = y;

			break;
		case MotionEvent.ACTION_UP:
			// ��������״̬
			cacheCanvas.drawPath(path, mPaint);
			// ���û���
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
	 * ����ͼƬ����
	 * 
	 * @param filename
	 */
	public void saveToFile(String filename) throws FileNotFoundException {
		File file = new File(filename);
		if (file.exists()) {
			throw new RuntimeException("�ļ�:" + filename + "�Ѿ�����");
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
