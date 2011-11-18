package com.jeesmon.malayalambible;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChaptersActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.chapters);
	    
	    Resources res = getResources();
		Typeface tf = Typeface.createFromAsset(getAssets(),
				res.getString(R.string.font_name));
		
		final Activity activity = this;
	    ImageView back = (ImageView) findViewById(R.id.backButton);
	    back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		});
		
		TextView tv = (TextView) findViewById(R.id.chapters);
		tv.setTypeface(tf);
		tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.chapters)));

	    Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	        Book book = (Book) extras.getSerializable("com.jeesmon.malayalambible.Book");
	        
	        tv = (TextView) findViewById(R.id.heading);
			tv.setTypeface(tf);
			tv.setText(ComplexCharacterMapper.fix(book.getName()));
	        
	        GridView gridview = (GridView) findViewById(R.id.gridview);
	        gridview.setAdapter(new ChapterButtonAdapter(this, book));
	    }
	}
}
