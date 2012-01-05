package com.jeesmon.malayalambible;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {
	protected boolean _active = true;
	protected int _splashTime = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ThemeUtils.setTheme(Preference.getInstance(this).getTheme());
		
		setTheme(ThemeUtils.getThemeResource());
		setContentView(R.layout.splash);
		
		final Context context = this;
		
		Thread splashTread = new Thread() {
	        @Override
	        public void run() {
	            try {
	                int waited = 0;
	                while(_active && (waited < _splashTime)) {
	                    sleep(100);
	                    if(_active) {
	                        waited += 100;
	                    }
	                }
	            } catch(InterruptedException e) {
	                // do nothing
	            } finally {
	            	//check DB and create one if necessary
	            	new DataBaseHelper(context);
	            	//load book names
	            	Utils.setRenderingFix(Preference.getInstance(context).getRendering());
	            	Utils.getBooks();
	            	
	                finish();
	                startActivity(new Intent(context, ChapterViewActivity.class));
	            }
	        }
	    };
	    splashTread.start();
	}
}
