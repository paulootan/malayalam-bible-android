package com.jeesmon.malayalambible;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.about:
	    	showInfoActivity(this);
	        return true;
	    case R.id.preferences:
	    	showPrefenceActivity(this);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void showInfoActivity(Context context) {
		Intent infoView = new Intent(context, InfoActivity.class);
		startActivity(infoView);
	}
	
	protected void showPrefenceActivity(Context context) {
		Intent prefView = new Intent(context, AppPreferencesActivity.class);
		startActivity(prefView);
	}
}
