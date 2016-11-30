package com.istrid;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.istrid.Utils.SQLiteHelper;
import com.istrid.Utils.StickersDownloader;
import com.istrid.adapters.StickerListAdapter;

public class UpdateStickersActivity extends Activity implements OnClickListener {
	
	ImageButton exitActivity;
	ImageButton updateFromService;
	ListView stickersList;
	StickerListAdapter stickersListAdapter;
	StickersDownloader stickersDownloader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
   			 				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_update_stickers);
		
		exitActivity = (ImageButton) findViewById(R.id.cancel_update);
		updateFromService = (ImageButton) findViewById(R.id.update_stickers);
		stickersList = (ListView) findViewById(R.id.sticker_update_list);
		
		exitActivity.setOnClickListener(this);
		updateFromService.setOnClickListener(this);
		
		stickersList.setAdapter(stickersListAdapter = new StickerListAdapter(this));
		updateFromService.setOnClickListener(this);		
		//stickersListAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_stickers, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.cancel_update:
				finish();
			break;
			case R.id.update_stickers:
				update();
			break;
		}
	}

	void process(){
		SQLiteHelper sqlHelper = new SQLiteHelper(this);
		stickersDownloader = new StickersDownloader(sqlHelper, this, stickersListAdapter, getAssets());
		stickersDownloader.execute("http://istrid.net/props/all/json");
	}
	
	void update(){
		if( stickersDownloader == null || stickersDownloader.getStatus() != Status.RUNNING ){
			process();
		}
	}
}