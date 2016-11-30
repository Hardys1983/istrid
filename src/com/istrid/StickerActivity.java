package com.istrid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

import com.istrid.adapters.StickerSelectionAdapter;

public class StickerActivity extends Activity {
	
	GridView gridview;
	ImageButton findMoreStickers;
	ImageButton accept;
	int selectedImageId = -1;
	int imageHeight;
	int imageWidth;
	String backGround;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//TODO: Se debe mostrar un progress dialog porque se demora algo al cargar las imagenes.
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
   			 				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_sticker);
		
//		findMoreStickers = (ImageButton) findViewById(R.id.find_more_stickers);
//		findMoreStickers.setOnClickListener(this);
		
//		accept = (ImageButton) findViewById(R.id.select_stickers_accept);
//		accept.setOnClickListener(this);	
	      
		int nid = getIntent().getIntExtra("nid", -1);
		backGround = getIntent().getStringExtra("imageUri");
		
		if( nid != -1 ){
			StickerSelectionAdapter adapter = new StickerSelectionAdapter(getBaseContext(), nid);

		    gridview = (GridView) findViewById(R.id.stickersGridView);
		    gridview.setAdapter( adapter );
		    
		    gridview.setOnItemClickListener(new OnItemClickListener() {
		          public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		        	  String imageId = v.getTag().toString();
		        	  selectedImageId = Integer.parseInt(imageId);
		        	  imageWidth = v.getWidth();
		        	  imageHeight = v.getHeight();
		        	  
		        	  Intent editIntent = new Intent(StickerActivity.this, EditStickerActivity.class);
						
		        	  editIntent.putExtra("selectedImageId", selectedImageId );
		        	  editIntent.putExtra("height", imageHeight );
		        	  editIntent.putExtra("width", imageWidth );
		        	  editIntent.putExtra("imageUri", backGround);
					
		        	  startActivity(editIntent);
		        	  finish();
		          }
		      });
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sticker, menu);
		return true;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) {
			Intent editIntent = new Intent(StickerActivity.this, PictureActivity.class);
			editIntent.putExtra("imageUri", backGround);
			startActivity(editIntent);
			finish();
        }
        return super.onKeyDown(keyCode, event);
    }

//	@Override
//	public void onClick(View view) {
//		switch(view.getId()) {
////			case R.id.find_more_stickers:{
////				Intent intent = new Intent(StickerActivity.this, UpdateStickersActivity.class);
////				startActivity(intent);
////				finish();
////				break;
////			}
//			case R.id.select_stickers_accept:{
//				if( selectedImageId != -1 ){
//					Intent editIntent = new Intent(StickerActivity.this, EditStickerActivity.class);
//					
//					editIntent.putExtra("selectedImageId", selectedImageId );
//					editIntent.putExtra("height", imageHeight );
//					editIntent.putExtra("width", imageWidth );
//					editIntent.putExtra("imageArray", backGround);
//					
//					startActivity(editIntent);
//					//finish();
//				}
//				break;
//			}
//		}
//	}
}
