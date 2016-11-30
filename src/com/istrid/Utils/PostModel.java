/**
 * 
 */
package com.istrid.Utils;

import android.net.Uri;

/**
 * @author Adolfo Miguel Iglesias
 *
 */
public class PostModel {

	private Uri uri;
	private String message;
	
	public PostModel(Uri uri, String message) {
		super();
		this.uri = uri;
		this.message = message;
	}
	public Uri getUri() {
		return uri;
	}
	public void setUri(Uri uri) {
		this.uri = uri;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
