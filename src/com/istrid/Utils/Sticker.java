package com.istrid.Utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Sticker {
	

	public static byte[] ConvertDrawableToByteArray(Bitmap imageBitmap) {
		
	    ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream();
	    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageByteStream);
	    return imageByteStream.toByteArray();
	    
	}
	
	public static byte[] ConvertDrawableToByteArray(Drawable drawableResource) {
	    Bitmap imageBitmap = ((BitmapDrawable) drawableResource).getBitmap();
	    return ConvertDrawableToByteArray(imageBitmap);
	}
	
	public static Bitmap CreateBitmapFromBlob( byte[] data ){
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public static Sticker createSticker( String name, String desc, byte[] data ){
		Sticker s = new Sticker();
		s.Name = name;
		s.Description = desc;
		s.image = data;
		
		return s;
	}
	
	public int 	  Id;
	public String Name;
	public String Description;
	public byte[] image;
	
	public static String ColID   		= "ID";
	public static String ColName 		= "NAME";
	public static String ColDescription = "DESCRIPTION";
	public static String ColImage 		= "IMAGE";
}
