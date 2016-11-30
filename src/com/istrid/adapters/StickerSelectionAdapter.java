package com.istrid.adapters;

import java.util.LinkedList;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.istrid.R;
import com.istrid.Utils.SQLiteHelper;
import com.istrid.Utils.Sticker;

public class StickerSelectionAdapter extends BaseAdapter {
   private Context context;
   private SQLiteHelper sqlHelper;
   private LinkedList<Sticker> stickers;

   // Constructor
   public StickerSelectionAdapter(Context c, int nid) {
      context = c;
      sqlHelper = new SQLiteHelper(context);
      stickers = sqlHelper.getStickers(nid);
   }

   public int getCount() {
	   return stickers.size();
   }

   public Object getItem(int position) {
      return stickers.get(position);
   }

   public long getItemId(int position) {
	   return stickers.get(position).Id;
   }

   // create a new ImageView for each item referenced by the Adapter
   public View getView(int position, View convertView, ViewGroup parent) {
      ImageView imageView;
      if (convertView == null) {
	      imageView = new ImageView(context);
	      imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	      imageView.setBackgroundResource(R.drawable.sticker_solid_bg);
	      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	      imageView.setPadding(10, 10, 10, 10);
      } 
      else{
    	  imageView = (ImageView) convertView;
      }
      
      Sticker sticker = stickers.get(position);
      byte[] image = sticker.image;
      imageView.setImageBitmap( Sticker.CreateBitmapFromBlob(image));
      imageView.setTag( sticker.Id );
      
      return imageView;
   }
}