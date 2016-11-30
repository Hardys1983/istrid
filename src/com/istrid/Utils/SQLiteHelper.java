package com.istrid.Utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public final String Groups   = "stickerGroups";
	public final String Stickers = "stickers";
	
	public SQLiteHelper(Context context) {
		super(context, "istrid_database", null, 1);
	}

	public SQLiteHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
 		
		String sql  = "Drop table if exists '" + Stickers + "';";
		db.execSQL(sql);
		
		sql = "Drop table if exists '" + Groups + "';";
 	    db.execSQL(sql);

 	    sql = "CREATE TABLE '" + this.Groups + "' (nid INTEGER PRIMARY KEY, "  +
 				                                "author TEXT NOT NULL, "       +
 				                                "title TEXT NOT NULL, "        +
 				                                "updated DATETIME NOT NULL, "  +
 				                                "img1 BLOB, "        		   +
 										        "img2 BLOB);";

 		db.execSQL(sql);
 		
 		sql = "CREATE TABLE '" + this.Stickers + "'(id INTEGER PRIMARY KEY AUTOINCREMENT, nid INTEGER NOT NULL, image BLOB);";
		db.execSQL(sql);
	}
	
	
	public void addFakeImagesToGroupsAndImages(byte[][] images){
		SQLiteDatabase db = null;
		LinkedList<Integer> groupsIds = new LinkedList<Integer>();
		LinkedList<Integer> imagesIds = new LinkedList<Integer>();

		//querying NIDs
		{
			db = getWritableDatabase();
			if( db != null ){
				String query = "select nid from '" + Groups + "';";
				Cursor cursor = db.rawQuery(query, null );
				
				if( cursor != null && cursor.moveToFirst() ) {
					do{
						int nid = cursor.getInt(0);
						groupsIds.add( nid );
					}while( cursor.moveToNext() );
				}
				cursor.close();
				
				//images
				query = "select id from '" + Stickers + "';";
				cursor = db.rawQuery(query, null );
				
				if( cursor != null && cursor.moveToFirst() ) {
					do{
						int id = cursor.getInt(0);
						imagesIds.add( id );
					}while( cursor.moveToNext() );
				}
				cursor.close();
			}
		}
		
		//modifying
		{
			Iterator<?> iterNIDs = groupsIds.iterator();
			
			Random random = new Random();
			while( iterNIDs.hasNext() ){
				Integer nid = (Integer)iterNIDs.next();
				
				int firstImage  = random.nextInt(images.length / 2);
				int secondImage = images.length/2 + random.nextInt(images.length / 2);
				
				ContentValues values = new ContentValues();
				values.put("img1", images[firstImage]);
				values.put("img2", images[secondImage]);

				String where = "nid=?";
				String[] whereArgs = new String[] {String.valueOf(nid)};
				
				db.update(Groups, values, where, whereArgs);
			}
			
			//images
			Iterator<?> iterImgIds = imagesIds.iterator();
			
			while( iterImgIds.hasNext() ){
				Integer imgId = (Integer)iterImgIds.next();
				
				int index = random.nextInt(images.length / 2);
				
				ContentValues values = new ContentValues();
				values.put("image", images[index]);

				String where = "id=?";
				String[] whereArgs = new String[] {String.valueOf(imgId)};
				
				db.update(Stickers, values, where, whereArgs);
			}
			db.close();
		}
		//modifico cada grupo con imagenes aleatorias
	}
	
	public boolean addGroup( Group group ){
		
		Group findGroup = this.getGroupDescriptions(group.nid);

		boolean groupModified = false;
		if( findGroup == null ){
			ContentValues values = new ContentValues();
			
			values.put(StickersDownloader.Nid	 , group.nid);
			values.put(StickersDownloader.Author , group.author);
			values.put(StickersDownloader.Title	 , group.title);
			values.put(StickersDownloader.Updated, group.updated);
			values.put("img1"					 , group.img1);
			values.put("img2"					 , group.img2);
	
			SQLiteDatabase db = getWritableDatabase();
			if(db != null ){
				db.insert(Groups, null, values);
				db.close();
				groupModified = true;
			}
		}else if( findGroup.updated.compareTo(group.updated) < 0 ){
			ContentValues values = new ContentValues();

			values.put(StickersDownloader.Author , group.author);
			values.put(StickersDownloader.Title	 , group.title);
			values.put(StickersDownloader.Updated, group.updated);
			values.put("img1"					 , group.img1);
			values.put("img2"					 , group.img2);
			
			String where = "nid=?";
			String[] whereArgs = new String[] {String.valueOf(group.nid)};

			this.eraseStickers(group.nid);
			
			SQLiteDatabase db = getWritableDatabase();
			if(db != null ){
				db.update(Groups, values, where, whereArgs);
				db.close();
				groupModified = true;
			}
		}
		return groupModified;
	}
	
	public void addImageToGroup(int nid, byte[] image){
		SQLiteDatabase db = getWritableDatabase();
		if( db != null ){
			ContentValues values = new ContentValues();
			
			values.put(StickersDownloader.Nid	 , nid);
			values.put("image"					 , image);

			db.insert(Stickers, null, values);
			db.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		onCreate(arg0);
	}

	public LinkedList<Sticker> getStickers(int nid) {
		LinkedList<Sticker> stickerList = new LinkedList<Sticker>();
		SQLiteDatabase db = getReadableDatabase();
		
		if( db != null ){
			String query = "select id, image from '" + Stickers + "' where nid=" + nid + ";";
			Cursor cursor = db.rawQuery(query, null );
			if( cursor != null && cursor.moveToFirst() ) {
				do{
					Sticker sticker = new Sticker();
					sticker.Id = cursor.getInt(0);
					sticker.image = cursor.getBlob(1);
					stickerList.add( sticker );
				}while( cursor.moveToNext() );
			}
			cursor.close();
			db.close();
		}
		return stickerList;
	}
	
	public LinkedList<Group> getGroups(){
		
		LinkedList<Group> groups = new LinkedList<Group>();
		SQLiteDatabase db = getReadableDatabase();
		
		if (db != null) {
			String query = "SELECT DISTINCT nid, title, img1, img2 FROM '" + Groups + "';";
			Cursor cursor = db.rawQuery(query, null);
			
			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
				do {
					Group group = new Group();
					
					group.nid   = cursor.getInt(0);
					group.title = cursor.getString(1);
					group.img1  = cursor.getBlob(2);
					group.img2  = cursor.getBlob(3);

					groups.add(group);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
		}
		return groups;
	}

	public void eraseStickers(int nid){
		SQLiteDatabase db = getWritableDatabase();
		if( db != null ){
			String where = "nid=?";
			String[] whereArgs = new String[] {String.valueOf(nid)};

			db.delete(Stickers, where, whereArgs);
			db.close();
		}
	}
	
	public void eraseGroup(int nid) {
		SQLiteDatabase db = getWritableDatabase();
		if( db != null ){
			String where = "nid=?";
			String[] whereArgs = new String[] {String.valueOf(nid)};

			db.delete(Stickers, where, whereArgs);
			db.delete(Groups, where, whereArgs);
			
			db.close();
		}
	}

	public Group getGroup(int index) {
		
		SQLiteDatabase db = getReadableDatabase();
		Group group = null;
		
		if (db != null) {
			String query = "SELECT nid, title, img1, img2 FROM '" + Groups + "' limit 1 " + " offset " + index + ";";
			Cursor cursor = db.rawQuery(query, null);
			
			if( cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() ){
				group = new Group();
				
				group.nid   = cursor.getInt(0);
				group.title = cursor.getString(1);
				group.img1  = cursor.getBlob(2);
				group.img2  = cursor.getBlob(3);
			}
			if( cursor != null ){
				cursor.close();
			}
			db.close();
		}
		return group;
	}
	
	public Group getGroupDescriptions(int nid) {
		
		SQLiteDatabase db = getReadableDatabase();
		Group group = null;
		
		if (db != null) {
			String query = "SELECT author, updated, title FROM '" + Groups + "' WHERE nid=" + nid + ";";
			Cursor cursor = db.rawQuery(query, null);
			
			if( cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() ){
				group = new Group();
				
				group.nid     = nid;
				group.author  = cursor.getString(0);
				group.updated = cursor.getString(1);
				group.title   = cursor.getString(2);
			}
			
			if(cursor != null ){
				cursor.close();
			}
			
			db.close();
		}
		return group;
	}

	public int getGroupCount() {
		SQLiteDatabase db = getReadableDatabase();
		
		int rowCount = 0;
		if( db != null ){
			String query = "select count(*) from '" + Groups + "';";
			Cursor cursor = db.rawQuery(query, null);
			
			if( cursor.moveToFirst() ){
				rowCount = cursor.getInt(0);
			}
			
			cursor.close();
			db.close();
		}
		return rowCount;
	}

	public Sticker getSticker(int imageId) {
		SQLiteDatabase db = getReadableDatabase();
		Sticker sticker = null;
		
		if (db != null) {
			String query = "SELECT id, image FROM '" + Stickers + "' WHERE id=" + imageId + ";";
			Cursor cursor = db.rawQuery(query, null);
			
			if( cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() ){
				sticker = new Sticker();
				
				sticker.Id     = cursor.getInt(0);
				sticker.image  = cursor.getBlob(1);
			}
			
			if(cursor != null ){
				cursor.close();
			}
			db.close();
		}
		return sticker;
	}
}
