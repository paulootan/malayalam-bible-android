package com.jeesmon.malayalambible.service;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

public class FontService {

	private static FontService Obj;

	private int currentapiVersion = 0;
	private String custom_font = null;
	private String font_path = null;

	private FontService() {

	}

	public static FontService getInstance() {
		if (Obj == null) {
			Obj = new FontService();
		}
		return Obj;
	}

	public Typeface getTypeface(AssetManager mgr) {

		Typeface tf = null;
		currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Log.d("tag", "currentapi version" + currentapiVersion);
		/* checking current api version and assigns font based on it */
		if (currentapiVersion >= 16) {
			custom_font = "NotoSansMalayalam.ttf";
			Log.d("tag", "notosans");
		}
		if (currentapiVersion < 16) {
			custom_font = "AnjaliOldLipi.ttf";
			Log.d("tag", "anjali..");
		}
		font_path = "fonts/" + custom_font;
		tf = Typeface.createFromAsset(mgr, font_path);
		return tf;
	}
}
