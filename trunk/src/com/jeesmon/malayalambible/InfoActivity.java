package com.jeesmon.malayalambible;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

public class InfoActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.info);
	    
	    final Activity activity = this;
	    ImageView back = (ImageView) findViewById(R.id.backButton);
	    back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		});
	    
	    WebView webView = (WebView) findViewById(R.id.webView);	    
	    webView.loadUrl("file:///android_asset/info.html");
	}
}


