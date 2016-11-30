package com.istrid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.istrid.R;
import com.istrid.Utils.Group;
import com.istrid.Utils.SQLiteHelper;
import com.istrid.Utils.Sticker;


public class StickerListAdapter extends BaseAdapter {

	private Context context;
	private SQLiteHelper sqlHelper;

	public StickerListAdapter(Context _context) {
		context = _context;
		sqlHelper = new SQLiteHelper(_context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sqlHelper.getGroupCount();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return sqlHelper.getGroup(index);
	}

	@Override
	public long getItemId(int index) {
		return sqlHelper.getGroup(index).nid;
	}

	@Override
	public View getView(int index, View arg1, ViewGroup arg2) {
		
		if(arg1 == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.sticker_list_view, arg2,false);
        }

		Group group = sqlHelper.getGroup(index);
		
        TextView groupName = (TextView)arg1.findViewById(R.id.group_name);
        groupName.setText(group.title);
        
        ImageView img1 = (ImageView)arg1.findViewById(R.id.sticker_first);
        ImageView img2 = (ImageView)arg1.findViewById(R.id.sticker_second);

        if(group.img1 != null){
        	Bitmap bmp = Sticker.CreateBitmapFromBlob(group.img1);
        	bmp = Bitmap.createBitmap(bmp, 0, 0, 85, 85);
        	img1.setImageBitmap(bmp);
        }
        
        if(group.img2 != null){
        	Bitmap bmp = Sticker.CreateBitmapFromBlob(group.img2);
        	bmp = Bitmap.createBitmap(bmp, 0, 0, 85, 85);
        	img2.setImageBitmap(bmp);
        }

        ImageButton delete = (ImageButton)arg1.findViewById(R.id.delete_group);
        class EraseClick implements OnClickListener{
			private int nid = -1;
			
			public EraseClick(int nid) {
				this.nid = nid;
			}
        	@Override
			public void onClick(View view) {
				sqlHelper.eraseGroup(nid);
		        notifyDataSetChanged();
        	}
		}
        delete.setOnClickListener(new EraseClick(group.nid));
        return arg1;
	}
}

/*
public class StickerListAdapter extends BaseAdapter {
	
	private Context context;
	private LinkedList<Group> list;
	private SQLiteHelper sqlHelper;

	public StickerListAdapter(Context _context) {
		context = _context;
		
		sqlHelper = new SQLiteHelper(_context);
		
		update();
	}

	private void update() {
		// TODO Auto-generated method stub
		list = sqlHelper.getGroups();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		if(arg1 == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.stickers_list_group, arg2,false);
        }

        TextView groupName = (TextView)arg1.findViewById(R.id.group_name);
        groupName.setText(list.get(arg0).title);
        
        ImageView img1 = (ImageView)arg1.findViewById(R.id.sticker_first);
        ImageView img2 = (ImageView)arg1.findViewById(R.id.sticker_second);
        
        img1.setImageBitmap(Sticker.CreateBitmapFromBlob(list.get(arg0).img1));
        img2.setImageBitmap(Sticker.CreateBitmapFromBlob(list.get(arg0).img2));

        ImageButton delete = (ImageButton)arg1.findViewById(R.id.delete_group);
        
        class EraseClick implements OnClickListener{
			private int nid = -1;
			
			public EraseClick(int nid) {
				this.nid = nid;
			}
        	@Override
			public void onClick(View view) {
				sqlHelper.eraseGroup(nid);
				update();
		        notifyDataSetChanged();
        	}
		}
        delete.setOnClickListener(new EraseClick(list.get(arg0).nid));
        return arg1;
	}
}*/
