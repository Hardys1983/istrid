package com.istrid.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ImageDownloader {
	public static byte[] download(String downloadUrl) {
		try {
			if(Constants.local == true ){
				  Rect rect = new Rect(0, 0, 130, 85);

				  Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
				  Canvas canvas = new Canvas(image);

				  Random random = new Random();
				  
				  int[] colors = new int[]{ Color.CYAN, Color.GREEN, Color.GRAY, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.WHITE };

				  Paint paint = new Paint();
				  paint.setColor( colors[ random.nextInt(colors.length - 1) ] );

				  canvas.drawRect(rect, paint);
				  return Sticker.ConvertDrawableToByteArray(image);
			}else{
				DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
				HttpGet httppost = new HttpGet(downloadUrl);
	
			    HttpResponse response = httpclient.execute(httppost); 
			    
			    HttpEntity entity = response.getEntity();
	
			    InputStream inputStream = entity.getContent();
			    ByteArrayOutputStream stream = new ByteArrayOutputStream();
				
			    byte[] buffer = new byte[1024];
				int read = 0;
				while ((read = inputStream.read(buffer)) > 0) {
				   stream.write(buffer, 0, read);
				}
				return stream.toByteArray();
			}
		} catch (IOException io) {
			io.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
