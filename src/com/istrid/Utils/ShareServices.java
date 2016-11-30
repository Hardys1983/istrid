/**
 * 
 */
package com.istrid.Utils;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * @author Adolfo Miguel Iglesias
 *
 */
public class ShareServices {
	
	public enum Target{
		facebook, twitter, mail
	}
	
	public final String FACEBOOK = "facebook";
	public final String TWITTER  = "twitter";
	public final String MAIL = "com.android.email";

	protected PackageManager  packageManager; 
	
	public ShareServices(PackageManager packageManager) {
		super();
		this.packageManager = packageManager;
	}	

	public Intent prePublish(PostModel post, Target target){
		
		if(post == null){
			throw new RuntimeException("Post not be null");
		}
	
		boolean res = false;
	    boolean uriImg = false;
	    
	   Intent share = new Intent(Intent.ACTION_SEND);
	   share.setType("image/*");
	   Intent chooserIntent = null ;
	   
	   List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
	   if (!resInfo.isEmpty()) {
		   
		   Intent intent = null;
		   
		   for (ResolveInfo resolveInfo : resInfo) {
			   
			   String packageName = resolveInfo.activityInfo.packageName;
			  intent = prepareIntent(packageName, target, post);
			    if(intent !=null){
			    	break;  	
			    }
		   }
		    chooserIntent = Intent.createChooser(intent, "Compartiendo...");
		    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intent);
	        return chooserIntent;
	   }
	   
	   return chooserIntent;
	}
	
	private Intent prepareIntent(String packageName, Target target, PostModel post){
			Intent intent = null;
		  // intent.setType("text/plain");
		   
		 
		if(packageName.contains(FACEBOOK)  && target == Target.facebook){
			intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("image/*");
			intent = publishPhoto(packageName, post,intent);
			
		}else if(packageName.contains(TWITTER)  && target == Target.twitter){
			intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("image/*");
			intent = publishPhoto(packageName, post,intent);
			
		}else if(packageName.contains(MAIL)  && target == Target.mail){
			intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("image/*");
			intent = publishPhoto(packageName, post, intent);
		}
		
		return intent;
	}
	
	
	private Intent publishPhoto(String packageName, PostModel post, Intent intent) {
		
		if(post.getUri() != null){
			 intent.putExtra(Intent.EXTRA_STREAM, post.getUri());
			 intent.putExtra( android.content.Intent.EXTRA_TEXT, post.getMessage());
			 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		 }else{
			 throw new RuntimeException("Uri not be null");
		 }
		
		intent.setPackage(packageName);
		
		return intent;
	}
	
	public PackageManager getPackageManager() {
		return packageManager;
	}
}
