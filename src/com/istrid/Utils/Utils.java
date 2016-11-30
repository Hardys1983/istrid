package com.istrid.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Utils {
	
	public static Bitmap loadBitmapFromView(View v) {
	     Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);                
	     Canvas c = new Canvas(b);
	     v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
	     v.draw(c);
	     return b;
	}
	
	public static String getFilePathFromContentUri(Uri selectedImageUri, ContentResolver contentResolver) {
	    String filePath;
	    String[] filePathColumn = {ImageColumns.DATA};

	    Cursor cursor = contentResolver.query(selectedImageUri, filePathColumn, null, null, null);
	    cursor.moveToFirst();

	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	    filePath = cursor.getString(columnIndex);
	    cursor.close();
	    return filePath;
	}
	
	public static boolean isSdReadable() {

		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = true;
			Log.i("isSdReadable", "External storage card is readable.");
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Log.i("isSdReadable", "External storage card is readable.");
			mExternalStorageAvailable = true;
		} else {
			mExternalStorageAvailable = false;
		}
		return mExternalStorageAvailable;
	}

	public static Bitmap loadBitmapFromSD(Context context, String filePath){
		
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filePath;
		Bitmap thumbnail = null;

		try {
			if ( Utils.isSdReadable() == true) {
					thumbnail = BitmapFactory.decodeFile(fullPath);
				}
			} catch (Exception e) {
				Log.e("getThumbnail() on external storage", e.getMessage());
			}

		// If no file on external storage, look in internal storage
		if (thumbnail == null) {
			try {
				File file = context.getFileStreamPath(filePath);
				FileInputStream fi = new FileInputStream(file);
				thumbnail = BitmapFactory.decodeStream(fi);
			} catch (Exception ex) {
				Log.e("getThumbnail() on internal storage", ex.getMessage());
			}
		}
		return thumbnail;
	}
	
	public static boolean saveBitmapToSD(Context context, Bitmap image, String fileName) {
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		try {
			File dir = new File(fullPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			OutputStream fOut = null;
			File file = new File(fullPath, fileName);
			file.createNewFile();
			fOut = new FileOutputStream(file);
	
			image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
	
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
	
			return true;
		} catch (Exception e) {
			Log.e("saveToExternalStorage()", e.getMessage());
			return false;
		}
	}
	
	public static boolean saveImageToInternalStorage(Context context, Bitmap image, String localFilePath) {

		try {
				FileOutputStream fos = context.openFileOutput(localFilePath, Context.MODE_PRIVATE);
				image.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
				return true;
		} catch (Exception e) {
					Log.e("saveToInternalStorage()", e.getMessage());
				return false;
		}
	}
	
	public static Bitmap getImageFromInternalStorage(Context context, String localFilePath){
		 
		try {
			FileInputStream inputStream = context.openFileInput(localFilePath);
			int size = inputStream.available();
			if(size > 0){
				byte[] data = new byte[size];
				inputStream.read(data, 0, size);
				inputStream.close();
				
				return BitmapFactory.decodeByteArray(data, 0, data.length);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
