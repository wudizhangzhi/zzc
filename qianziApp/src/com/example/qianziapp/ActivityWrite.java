package com.example.qianziapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.codec.net.URLCodec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.example.qianziapp.view.DrawPic;
import com.example.qianziapp.view.DrawView;

import Decoder.BASE64Encoder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import uk.co.senab.photoview.PhotoView;

@SuppressLint("NewApi")
public class ActivityWrite extends Activity implements OnClickListener {
	Button btn_choose, btn_saveupload, btn_clear;
	ToggleButton btn_toggle;
	DrawView drawView;
	PhotoView photoView;


	public static final String URL = "http://192.168.1.196/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write);

		initView();
	}

	@SuppressLint("NewApi")
	private void initView() {
		drawView = (DrawView) findViewById(R.id.drawview_write);
		photoView = (PhotoView) findViewById(R.id.photo);

		btn_choose = (Button) findViewById(R.id.btn_choose);
		btn_saveupload = (Button) findViewById(R.id.btn_saveupload);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_toggle = (ToggleButton) findViewById(R.id.toggleButton1);

		// btn_saveupload.setClickable(false);

		btn_choose.setOnClickListener(this);
		btn_saveupload.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_toggle.setChecked(true);
		btn_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String text = "";
				if (isChecked) {
					text = "手写打开";
					drawView.bringToFront();
					drawView.setVisibility(View.VISIBLE);
					drawView.setEnabled(true);
					photoView.setEnabled(false);
				} else {
					text = "手写关闭";
					drawView.setVisibility(View.GONE);
					drawView.setEnabled(false);
					photoView.setEnabled(true);
				}
				Toast.makeText(ActivityWrite.this, text, Toast.LENGTH_SHORT).show();
				//

			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_choose:
			Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 0);
			break;
		case R.id.btn_saveupload:// 保存并连接服务器上传
			SaveTask task = new SaveTask();
			task.execute();
			break;
		case R.id.btn_clear:
			// drawpic.clear();
			// drawView.clear();
			// drawView.setImageAlpha(255);
			// drawView.setBackgroundColor(Color.TRANSPARENT);
			// drawView.invalidate();
			// drawView.invalidateDrawable(drawView.getDrawable());
			// drawView.setImageResource(0);
			// drawView.setImageResource(android.R.color.transparent);
			drawView.clear();
			break;
		default:
			break;
		}
	}

	Uri uri;// 选中图片的uri

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// // 设置保存按钮可按
			// btn_saveupload.setClickable(true);
			uri = data.getData();
			// drawpic.ImageShowURIPic(uri);
			try {
				photoView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 保存的异步线程
	 * 
	 * @author Administrator
	 *
	 */
	class SaveTask extends AsyncTask<Void, Void, String> {

		FileOutputStream fos = null;

		@Override
		protected String doInBackground(Void... params) {
			String post = "";
			// 判断sd卡是否存在可用
			String state = Environment.getExternalStorageState();
			if (!state.equals(Environment.MEDIA_MOUNTED)) {
				return "sd卡未准备好";
			}
			// 利用时间生成文件名
			Calendar calendar = Calendar.getInstance();

			Calendar c = Calendar.getInstance();
			String name = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH)
					+ c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + ".png";
			Log.i("文件名", name);
			// 保存文件
			File file = new File(Environment.getExternalStorageDirectory(), name);
			// 合并图片
			Bitmap b1 = drawView.getPathBitmap();//手写层
			photoView.setDrawingCacheEnabled(true);
			// Bitmap b2 = photoView.getDrawingCache();
			Bitmap b2 = null;//图片层
			try {
				if (getContentResolver()!=null) {
					b2 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				}
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			// 保存至文件
			composeBitmap(b1, b2).compress(CompressFormat.PNG, 50, fos);
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 上传
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL);
			NameValuePair pair1 = new BasicNameValuePair("filename", "name");
			NameValuePair pair2 = new BasicNameValuePair("fileValue", encodeBase64File(file));
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(pair1);
			pairs.add(pair2);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					JSONObject jsonObj = new JSONObject(getResultFromHttpResponse(httpResponse));
					int resultCode = jsonObj.getInt("status");
					// TODO 对于回馈做出反应
					Log.i("resultStatus", "resultStatus:" + resultCode);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "上传失败:" + e.getMessage();
			}
			return "文件：" + name + "保存上传成功";
		}

		@Override
		protected void onPostExecute(String text) {
			super.onPostExecute(text);
			Toast.makeText(ActivityWrite.this, text, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 合并bitmap
	 * 
	 * @param b1：上层
	 * @param b2：底层
	 */
	private Bitmap composeBitmap(Bitmap b1, Bitmap b2) {
		if (b2!=null) {
			Bitmap b = Bitmap.createBitmap(b2.getWidth(), b2.getHeight(), b2.getConfig());
			Canvas canvas = new Canvas(b);
			Paint paint = new Paint();
			paint.setAlpha(255);

			canvas.drawBitmap(b2, new Matrix(), paint);
			canvas.drawBitmap(b1, new Matrix(), paint);
			return b;
		}else {
			return b1;
		}
	}

	/**
	 * 将文件转码为base64字符串
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String encodeBase64File(File file) {
		FileInputStream fis = null;
		byte[] buffer = null;
		try {
			fis = new FileInputStream(file);
			buffer = new byte[(int) file.length()];
			fis.read(buffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new BASE64Encoder().encode(buffer);
	}

	/**
	 * 处理服务器回复得到内容
	 * 
	 * @param httpresponse
	 * @return
	 */
	private String getResultFromHttpResponse(HttpResponse httpresponse) {
		String result = "";
		HttpEntity entity = httpresponse.getEntity();
		InputStream is = null;
		try {
			is = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			result = reader.readLine();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}
