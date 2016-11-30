package com.istrid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.istrid.Utils.Constants;
import com.istrid.Utils.Group;
import com.istrid.Utils.SQLiteHelper;
import com.istrid.Utils.Sticker;
import com.istrid.Utils.Utils;

public class PictureActivity extends Activity  {

	ImageButton findMoreStickers;
	ImageButton backToCamera;
	ImageButton shareWithFacebook;
	ImageButton shareWithTwitter;
	ImageButton shareByEmail;
	
	LinearLayout stickersList;
	FrameLayout mainView;
 	Bitmap bitmap;

 	String imageURI = "";
 	@Override
 	protected void onRestart() {
 		stickersList.removeAllViews();
 		createStickersBar();
 		super.onRestart();
 	}
 	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
   			 				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_picture);
		
		findMoreStickers = (ImageButton) findViewById(R.id.find_more_stickers);
		backToCamera = (ImageButton) findViewById(R.id.back_to_camera);
		
		mainView = (FrameLayout)findViewById(R.id.image_view);

		if( bitmap != null ){
			ImageView bg = new ImageView(getBaseContext());
			bg.setImageBitmap(bitmap);

			mainView.addView(bg);
		}
		else if( getIntent().hasExtra("imageUriBG") == true ){
			imageURI = getIntent().getStringExtra("imageUriBG");
			Uri uri = Uri.parse( imageURI );
			try {
				bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

//				Matrix matrix = new Matrix();
//			    matrix.postRotate(90.0f);
//			    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			    
			    ImageView bg = new ImageView(getBaseContext());
				bg.setImageBitmap(bitmap);

				mainView.addView(bg);
//				imageURI = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "New Bitmap", "Istrid");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if( getIntent().hasExtra("imageUri") == true ){
			imageURI = getIntent().getStringExtra("imageUri");
			Uri uri = Uri.parse( imageURI );
			try {
				bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				ImageView bg = new ImageView(getBaseContext());
				bg.setImageBitmap(bitmap);
				mainView.addView(bg);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			Toast.makeText(this, "Couldn't load background", Toast.LENGTH_LONG).show();
		}

		backToCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent newIntent = new Intent(PictureActivity.this, CameraActivity.class);
				startActivity(newIntent);
				finish();
			}
		});
		findMoreStickers.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent( PictureActivity.this, UpdateStickersActivity.class);
				startActivity(intent);
			}
		});
		
		shareWithTwitter = (ImageButton)findViewById(R.id.share_twitter);
		shareByEmail = (ImageButton)findViewById(R.id.share_mail);
		shareWithFacebook = (ImageButton)findViewById(R.id.share_facebook);
		
		shareWithFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View fbButton) {
				String path = Utils.getFilePathFromContentUri(Uri.parse(imageURI), getContentResolver());
				share("facebook", path);
			}
		});

		shareWithTwitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View fbButton) {
				
				final int threemb = 1024 * 1024 * 3;
				
				if( bitmap.getByteCount() > threemb ){
					float percent = threemb / ((float)bitmap.getByteCount());
					ByteArrayOutputStream out = new ByteArrayOutputStream();
 					bitmap.compress(CompressFormat.PNG, (int) (percent * 100 - 1), out);
 					
 					imageURI = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "New Bitmap", "Istrid");
				}
				String path = Utils.getFilePathFromContentUri(Uri.parse(imageURI), getContentResolver());
				share("com.twitter.android", path);
			}
		});
		
		shareByEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View fbButton) {
				String path = Utils.getFilePathFromContentUri(Uri.parse(imageURI), getContentResolver());
				share("email", path);
			}
		});
		stickersList = (LinearLayout)findViewById(R.id.main_sticker_list);
		createStickersBar();
	}
 
	void share(String nameApp, String imagePath) {
		  try{
		      List<Intent> targetedShareIntents = new ArrayList<Intent>();
		      Intent share = new Intent(android.content.Intent.ACTION_SEND);
		      share.setType("image/jpeg");
		      List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
		      if (!resInfo.isEmpty()){
		          for (ResolveInfo info : resInfo) {
		              Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
		              targetedShare.setType("image/jpeg"); // put here your mime type
		              if (info.activityInfo.packageName.toLowerCase().contains(nameApp) || info.activityInfo.name.toLowerCase().contains(nameApp)) {
		                  targetedShare.putExtra(Intent.EXTRA_SUBJECT, "Sample Photo");
		                  targetedShare.putExtra(Intent.EXTRA_TEXT, "This image is created by IStrid");

		                  File file = new File(imagePath);
		                  if( file.exists() ){
			                  targetedShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			                  targetedShare.setPackage(info.activityInfo.packageName);
			                  targetedShareIntents.add(targetedShare);
		                  }
		              }
		          }
		          Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
		          chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
		          startActivity(chooserIntent);
		      }
		  }
		  catch(Exception e){
			  Toast.makeText(getBaseContext(), "Exception while sending image on " + nameApp + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
		      Log.v("VM","Exception while sending image on " + nameApp + " " +  e.getMessage());
		  }
	}
	
	private void createStickersBar() {

		SQLiteHelper sqlHelper = new SQLiteHelper(this);
		
		LinkedList<Group>	groups = sqlHelper.getGroups();
		Iterator<Group> iterator = groups.iterator();

		while( iterator.hasNext() ){
			Group g = iterator.next();
			
			byte[] img1 = g.img1;
			byte[] img2 = g.img2;
			
			if( img1 == null || img2 == null ){
				sqlHelper.eraseGroup(g.nid);
			}else{
				createSticker(g.nid, img1);
				createSticker(g.nid, img2);
			}
		}
	}
	
	private void createSticker(int nid, byte[] image){
		
		ImageView view = new ImageView( getBaseContext() );
		view.setTag(nid);
		view.setOnClickListener(stickerOnClickListener);

		view.setImageBitmap( Sticker.CreateBitmapFromBlob(image) );
		view.setBackgroundResource( R.drawable.sticker_bg);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( 60, 60);
		params.setMargins(5, 0, 5, 0);

		stickersList.addView(view, params);
	}
	
	
	private OnClickListener stickerOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			
			Intent intent = new Intent(PictureActivity.this, StickerActivity.class);
			
			String nid = arg0.getTag().toString();
			intent.putExtra("nid", Integer.parseInt( nid ));
			intent.putExtra("imageUri", imageURI);

			startActivity(intent);
			finish();
		}	
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.picture, menu);
		return true;
	}
}
