package com.istrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.istrid.Utils.SQLiteHelper;
import com.istrid.Utils.Sticker;
import com.istrid.multitouch.MoveGestureDetector;
import com.istrid.multitouch.RotateGestureDetector;



public class EditStickerActivity extends Activity implements android.view.View.OnClickListener {
	
	private ImageButton cancel;
	private ImageButton accept;
	
	FrameLayout frameTransformations;
	//ScaleGestureDetector mScaleDetector ;
	BoxedImageView currentImage;
	
	String imageURI = "";
	
	private ScaleGestureDetector mScaleDetector;
	private RotateGestureDetector mRotateDetector;
	private MoveGestureDetector mMoveDetector;
	
	int 	firstPointerID;
	boolean inScaling = false;
	
	PointF StartPT = new PointF();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
   			 				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_edit_sticker);
	
		cancel = (ImageButton) findViewById(R.id.edit_sticker_cancel);
		cancel.setOnClickListener(this);
		
		accept = (ImageButton) findViewById(R.id.edit_sticker_accept);
		accept.setOnClickListener(this);
		
		frameTransformations = (FrameLayout)findViewById(R.id.edit_picture);
		
		int selectedImageId = getIntent().getIntExtra("selectedImageId", -1);
		int width  = getIntent().getIntExtra("height", -1);
		int height = getIntent().getIntExtra("width", -1);
		
		if( selectedImageId != -1 && width != -1 && height != -1){
			imageURI = getIntent().getStringExtra("imageUri");
			Uri uri = Uri.parse(imageURI);
			Bitmap bmp = null;
			try {
				bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if( bmp != null ){
				ImageView bg = new ImageView(getBaseContext());
				bg.setImageBitmap(bmp);
				frameTransformations.addView(bg);
			}

			SQLiteHelper sqlhelper = new SQLiteHelper(this);
			Sticker sticker = sqlhelper.getSticker( selectedImageId );
			
			addBitmapToFrame(Sticker.CreateBitmapFromBlob(sticker.image), width, height);
		}
		
		//Begin New Stuff
			mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
			mRotateDetector = new RotateGestureDetector(getApplicationContext(), new RotateListener());
			mMoveDetector = new MoveGestureDetector(getApplicationContext(), new MoveListener());
		//End New Stuff
	}
	
	private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
		@Override
		public boolean onMove(MoveGestureDetector detector) {
			PointF dp = detector.getFocusDelta();
			
			currentImage.setX((int)(currentImage.getX() + dp.x ));
			currentImage.setY((int)(currentImage.getY() + dp.y ));

			return true;
		}
	}	
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			currentImage.setScaleX(currentImage.getScaleX() * detector.getScaleFactor());
			currentImage.setScaleY(currentImage.getScaleY() * detector.getScaleFactor());
			return true;
		}
	}
	
	private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			float angle = -detector.getRotationDegreesDelta();
			currentImage.setRotation(currentImage.getRotation() + angle);
			return true;
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		
		mScaleDetector.onTouchEvent(event);
        mRotateDetector.onTouchEvent(event);
        mMoveDetector.onTouchEvent(event);
		
        return true;
	}

	private void addBitmapToFrame(Bitmap bmp, int width, int height){
		
		currentImage = new BoxedImageView(this);
		currentImage.paintRounds = true;
		
		currentImage.setImageBitmap(bmp);
		currentImage.setBackgroundColor(Color.TRANSPARENT);
		currentImage.setScaleType(ScaleType.MATRIX);
	    currentImage.setAdjustViewBounds(true);	
		currentImage.setScaleType(ScaleType.FIT_XY);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(	width, height, Gravity.NO_GRAVITY);
		
		params.topMargin  = height;
		params.leftMargin = width; 
				
		frameTransformations.addView(currentImage, params);
	}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_sticker, menu);
		return true;
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	backToFirst();
        }
        return super.onKeyDown(keyCode, event);
    }
    
	private void backToFirst() {
		Intent intent = new Intent(EditStickerActivity.this, PictureActivity.class);
		intent.putExtra("imageUri", imageURI);
		startActivity(intent);
		finish();
	}

	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.edit_sticker_cancel:{
				backToFirst();
				break;
			}
			case R.id.edit_sticker_accept:{
				byte[] bitmap = getBackground();
				Bitmap bmp = Sticker.CreateBitmapFromBlob(bitmap);
				imageURI = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "New Bitmap", "Istrid");

				Intent pictureIntent = new Intent(EditStickerActivity.this, PictureActivity.class);
				pictureIntent.putExtra("imageUri", imageURI);
				startActivity(pictureIntent);
				finish();
				break;
			}
		}
	}

	private byte[] getBackground() {
		try{
			currentImage.paintRounds = false;
			frameTransformations.setDrawingCacheEnabled(true);
			frameTransformations.setDrawingCacheQuality(100);
			frameTransformations.buildDrawingCache();
			Bitmap bitmap = Bitmap.createBitmap( frameTransformations.getDrawingCache() );
			frameTransformations.destroyDrawingCache();
			return Sticker.ConvertDrawableToByteArray(bitmap);
		}
		catch (final Exception ex) { 
			Log.e("JAVA_DEBUGGING", "Exception while creating save file!"); 
			ex.printStackTrace(); 
		}
		return null;
	}
}
