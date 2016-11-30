package com.istrid.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.istrid.adapters.StickerListAdapter;

public class StickersDownloader extends AsyncTask<String, Integer, Void> {
	
	SQLiteHelper sqlHelper = null;
	AssetManager assetManager = null;
	
	StickerListAdapter adapter;
	
	static public final String MainObject = "nodes";

	//Group
	static public final String Group  = "node";
	
	//Data for persist
	static public final String Author = "Author";									//Autor del grupo
	static public final String Nid = "Nid"; 										//Identificador del grupo
	static public final String Updated = "Updated"; 								//Fecha de ultima actualizacion del grupo
	static public final String Title = "Title";										//Titulo del Grupo
	
	//image content
	static public final String Field_props_images_fid_1 = "field_props_images_fid_1"; //Array con las 2 imagenes de muestra del grupo
	static public final String Props = "Props"; 									  //Objeto con la lista de imagenes del grupo
	
	private ProgressDialog progressDialog;
	private Context context;
	
	public StickersDownloader(SQLiteHelper sqlHelper, Context context, StickerListAdapter adapter, AssetManager assetManager){
		this.sqlHelper = sqlHelper;
		this.assetManager = assetManager;
		this.adapter = adapter;
		this.context = context;
	}
	
	
	private String convertToYYYMMDD(String updateTime){
		
		StringTokenizer dateAndTime = new StringTokenizer(updateTime, " ");
		
		String date = dateAndTime.nextToken();
		String hour = dateAndTime.nextToken();
		
		StringTokenizer ymd = new StringTokenizer(date, "-");
		String[] items = new String[ ymd.countTokens() ];
		
		int i = 0;
		while( ymd.hasMoreTokens() ){
			items[i++] = ymd.nextToken();
		}

		return items[2] + "-" + items[0] + "-" + items[1] + " " + hour;
	}
	
	public void readAllNodes(JSONObject root){
		try {
			JSONArray mainObject = (JSONArray) root.getJSONArray(MainObject);

			for( int i = 0; i < mainObject.length(); ++i ){
				JSONObject node = ((JSONObject) mainObject.get(i)).getJSONObject(Group);
				
				Group group = new Group();
				
				group.nid     = node.getInt(Nid);
				group.author  = node.getString(Author);
				group.updated = node.getString(Updated); //tengo que procesar esta fecha para poner el anno al principio
				group.title   = node.getString(Title);
				group.updated = convertToYYYMMDD(group.updated);
				
				JSONObject mainImages 	= node.getJSONObject( Field_props_images_fid_1 );
				
				String imageURL1 	= "";
				String imageURL2 	= "";
				
				Iterator<?> keys = mainImages.keys();

		        //aqui deben haber solo 2 keys
				if( keys.hasNext() ){
		            //firstImage
					String key = (String)keys.next();
					imageURL1 = mainImages.getString(key);
		            
		            if( keys.hasNext() ){
		            	key = (String)keys.next();
		            	imageURL2 = mainImages.getString(key);
		            }
		            
		            //descarga de las imagenes y almacenamiento del grupo en la BD
		            group.img1 = ImageDownloader.download(imageURL1);
		            group.img2 = ImageDownloader.download(imageURL2);
				}
				
				if( sqlHelper.addGroup( group ) == true ){

					JSONObject allImages  = node.getJSONObject( Props );
					Iterator<?> iterator = allImages.keys();
 					
					while( iterator.hasNext() ){
						String key = (String)iterator.next();
						String imageURL = allImages.getString(key);
						
						byte[] image = ImageDownloader.download( imageURL );
						sqlHelper.addImageToGroup(group.nid, image);
					}					 
					publishProgress( group.nid );
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject testReadFromFile() throws IOException, JSONException{

		InputStream istream = assetManager.open("json");
		
		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(istream, "UTF-8"), 8);
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
		        sb.append(line + "\n");
		    }
		    reader.close();
		    istream.close();

		    int jsonBeginning = sb.indexOf("{");
		    return new JSONObject( sb.substring(jsonBeginning).toString() );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch ( JSONException e){
			e.printStackTrace();
		}
		catch( IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject readJSONObject( String jsonURL ){
		
		DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpGet httppost = new HttpGet(jsonURL);

		InputStream inputStream = null;
		
		try {
		    HttpResponse response = httpclient.execute(httppost);           
		    HttpEntity entity = response.getEntity();

		    inputStream = entity.getContent();
		    
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
		        sb.append(line + "\n");
		    }
		    //eliminando cualquier caracter que este delante del comienzo del JSONObject principal
		    int jsonBeginning = sb.indexOf("{");
		    return new JSONObject( sb.substring(jsonBeginning).toString() );
		} catch (Exception e) { 
			Log.d("StickersDownloader", "failed to download json");
		}
		finally {
		    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
		}
		return null;
	}

	@Override
	protected Void doInBackground(String... arg0) {
		JSONObject root = null; 
		try {
			if( Constants.local == true ){
				root = testReadFromFile();
			}else{
				root = readJSONObject(arg0[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		readAllNodes(root);
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Downloading sticker groups");
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
	    progressDialog.show();
	}
	
	@Override
	protected void onPostExecute(Void result) {
		adapter.notifyDataSetChanged();
		progressDialog.dismiss();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
         //setProgressPercent(progress[0]);
	}
}
