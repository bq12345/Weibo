package com.bq.util;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ReadImage {
	public static Bitmap getImage(final String url) {
		FutureTask<Bitmap> task = new FutureTask<>(new Callable<Bitmap>() {
			@Override
			public Bitmap call() throws Exception {
				HttpGet getMethod = new HttpGet(url);
				HttpParams params = new BasicHttpParams();
				// 连接超时时间
				HttpConnectionParams.setConnectionTimeout(params, 4 * 1000);
				// 传输超时时间
				HttpConnectionParams.setSoTimeout(params, 5 * 1000);
				HttpClient hc = new DefaultHttpClient(params);
				Bitmap bitmap = null;
				HttpResponse response;
				try {
					response = hc.execute(getMethod);
					bitmap = BitmapFactory.decodeStream(response.getEntity()
							.getContent());
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return bitmap;
			}

		});
		new Thread(task).start();
		try {
			return task.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
}
