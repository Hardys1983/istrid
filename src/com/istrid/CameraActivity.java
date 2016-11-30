package com.istrid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.istrid.Utils.Sticker;

public class CameraActivity extends Activity implements OnClickListener {

	private static final int PICTURE_RESULT = 0;
	private ImageButton information;
	private ImageButton cameraShutter;

	Camera mCamera;
	CameraPreview mPreview;
	FrameLayout mCameraPreview;
	Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                			 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);

		information = (ImageButton) findViewById(R.id.information);
		information.setOnClickListener(this);

		cameraShutter = (ImageButton) findViewById(R.id.camera_shutter);
		cameraShutter.setOnClickListener(this);

		// mCamera = CameraPreview.getCameraInstance();
		// mCameraPreview = (FrameLayout)findViewById(R.id.camera_preview);
		// mCameraPreview.addView(mPreview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.information:
			Intent intentInformation = new Intent(CameraActivity.this,
					InformationActivity.class);
			startActivity(intentInformation);
			break;
		case R.id.camera_shutter:
			callCamera();
			break;
		}
	}

	// @Override
	// void onClick(View view){
	// mCamera.takePicture(null, null, mPicture);
	// }
	// private PictureCallback mPicture = new PictureCallback() {
	// @Override
	// public void onPictureTaken(byte[] data, Camera camera) {
	// Intent intent = new Intent(CameraActivity.this, PictureActivity.class);
	// intent.putExtra("imageArray", data);
	// startActivity(intent);
	// finish();
	// }
	// };

	private void callCamera() {
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		startActivityForResult(intent, PICTURE_RESULT);

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "New Picture");
		values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
		imageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, PICTURE_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (PICTURE_RESULT == requestCode) {
			if (requestCode == PICTURE_RESULT) {
				if (resultCode == Activity.RESULT_OK) {
					
//					Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//					Bundle bundle = new Bundle();
//					bundle.putParcelable("background", bitmap);
					
//					Matrix matrix = new Matrix();
//				    matrix.postRotate(90.0f);
//				    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				      
//					Intent intent = new Intent(CameraActivity.this, PictureActivity.class);
//					intent.putExtras(bundle);
//					startActivity(intent);
//					finish();

					try {
						Intent intent = new Intent(CameraActivity.this,PictureActivity.class);
						intent.putExtra("imageUriBG", imageUri.toString());
						startActivity(intent);
						finish();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
