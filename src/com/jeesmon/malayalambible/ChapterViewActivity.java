package com.jeesmon.malayalambible;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ChapterViewActivity extends Activity {
	private Book book;
	private int chapterId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.chapter);
	    
	    Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	        this.book = (Book) extras.getSerializable("com.jeesmon.malayalambible.Book");
	        this.chapterId = extras.getInt("chapterId");	        
	    }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		getContent();
	}

	private void getContent() {
		setContentView(R.layout.chapter);
		
		final Activity activity = this;
	    ImageView back = (ImageView) findViewById(R.id.backButton);
	    back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		});
	    
	    ImageView previous = (ImageView) findViewById(R.id.prevButton);
        if(this.chapterId > 1) {
        	previous.setVisibility(View.VISIBLE);
        	previous.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				chapterId--;
    				getContent();
    			}
    		});
        }
        else {
        	previous.setVisibility(View.GONE);
        }
        
        ImageView next = (ImageView) findViewById(R.id.nextButton);
        if(this.chapterId < this.book.getChapters()) {
        	next.setVisibility(View.VISIBLE);
        	next.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				chapterId++;
    				getContent();
    			}
    		});
        }
        else {
        	next.setVisibility(View.GONE);
        }
		
		Resources res = getResources();
		Typeface tf = Typeface.createFromAsset(getAssets(),
				res.getString(R.string.font_name));
		
		TextView tv = (TextView) findViewById(R.id.heading);
		tv.setTypeface(tf);
		tv.setText(ComplexCharacterMapper.fix(book.getName()));
		
		tv = (TextView) findViewById(R.id.chapterNumber);
		tv.setTypeface(tf);
		tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.chapter)) + " " + chapterId);
		
		DataBaseAdapter adapter = new DataBaseAdapter(this);
		adapter.open();
		
		Cursor cursor = adapter.fetchChapter(book.getId(), chapterId);
		startManagingCursor(cursor);
		
		cursor.moveToFirst();
		
		TableLayout tl = (TableLayout) findViewById(R.id.chapterLayout);
		tl.removeAllViews();
		
		TableRow tr;
		TextView t;
		
		while (!cursor.isAfterLast()) {
			tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			tr.setPadding(5, 5, 5, 0);
			tr.setBackgroundResource(R.layout.cell_shape);
			
			int verseId = cursor.getInt(0);
			String verse = ComplexCharacterMapper.fix(cursor.getString(1));
			
			t = new TextView(this);
			t.setTypeface(tf);
			t.setText(verseId > 0 ? verseId + ". " + verse : verse);
			t.setTextColor(Color.BLACK);
			t.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			t.setSingleLine(false);
			tr.addView(t);

			tl.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			cursor.moveToNext();
		}
		
		cursor.close();
		adapter.close();
	}
}
