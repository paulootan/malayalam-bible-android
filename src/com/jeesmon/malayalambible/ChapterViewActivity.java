package com.jeesmon.malayalambible;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ChapterViewActivity extends BaseActivity implements IScrollListener {
	private Book book;
	private int chapterId;
	private ProgressDialog dialog;
	
	private DataBaseAdapter adapter;
	private Cursor cursor;
	private Cursor cursorSec;
	
	private static boolean preferenceChanged = false;
	
	private ObservableScrollView oScrollViewOne;
	private ObservableScrollView oScrollViewTwo;
	
	int[] tl1Heights;
	int[] tl2Heights;
	
	public static void setPreferenceChanged(boolean preferenceChanged) {
		ChapterViewActivity.preferenceChanged = preferenceChanged;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setTheme(ThemeUtils.getThemeResource());
	    setContentView(R.layout.chapter);
	    
	    Bundle extras = getIntent().getExtras();
	    if(extras == null) {
	    	ArrayList<Book> books = Utils.getBooks();
	    	Preference pref = Preference.getInstance(this);
	    	int lastBook = pref.getLastBook();
	    	int lastChapter = pref.getLastChapter();
	    	if(lastBook > 0 && lastChapter > 0) {
	    		this.book = books.get(lastBook - 1);
	    		this.chapterId = lastChapter;
	    	}
	    	else {
	    		this.book = books.get(0);
	    		this.chapterId = 1;
	    	}
	    }
	    else {
	        this.book = (Book) extras.getSerializable("com.jeesmon.malayalambible.Book");
	        this.chapterId = extras.getInt("chapterId");	        
	    }
	    
	    preferenceChanged = false;
	    
	    getContent(); 
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(preferenceChanged) {
			preferenceChanged = false;
			getContent();
		}
	}
	
	private void getContent() {
		oScrollViewOne = null;
		oScrollViewTwo = null;
		tl1Heights = null;
		tl2Heights = null;
		
		dialog = ProgressDialog.show(ChapterViewActivity.this, "", "Loading ...");
		new WorkerThread().start();
	}
	
	private void showContent() {
		final Preference pref = Preference.getInstance(this);
		int renderingFix = pref.getRendering();
		float fontSize = pref.getFontSize();
		
		if(pref.getSecLanguage() == Preference.LANG_NONE) {
			showSingleLanguage(renderingFix, fontSize, pref.getLanguage());
		}
		else {
			switch(pref.getLanguageLayout()) {
			case 0:
				showTwoLanguagesVerseByVerse(renderingFix, fontSize, pref.getLanguage(), pref.getSecLanguage());
				break;
			case 1:
				showTwoLanguagesSplit(renderingFix, fontSize, pref.getLanguage(), pref.getSecLanguage(), pref.getLanguageLayout());
				break;
			case 2:
				showTwoLanguagesSplit(renderingFix, fontSize, pref.getLanguage(), pref.getSecLanguage(), pref.getLanguageLayout());
				break;
			}
		}		
	}
	
	private void showSingleLanguage(int renderingFix, float fontSize, int language) {	
		setTheme(ThemeUtils.getThemeResource());
		setContentView(R.layout.chapter);
		
		if(cursor == null || cursor.isClosed()) {
			return;
		}
		
		setupToolbar();
        		
		Resources res = getResources();
		Typeface tf = language == Preference.LANG_MALAYALAM ? Typeface.createFromAsset(getAssets(),
				res.getString(R.string.font_name)) : null;
		
		TextView tv = (TextView) findViewById(R.id.heading);
		if(tf == null) {
			tv.setText(book.getEnglishName());
		}
		else {
			tv.setTypeface(tf);
			tv.setText(book.getName());
		}
		
		tv = (TextView) findViewById(R.id.chapterNumber);
		//tv.setTextSize(fontSize);
		
		if(tf == null) {
			tv.setText(res.getString(R.string.chaptereng) + " " + chapterId);
		}
		else {
			tv.setTypeface(tf);
			tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.chapter), renderingFix) + " " + chapterId);
		}
		
		cursor.moveToFirst();
		
		int rowLayout = R.layout.verserow;
				
		TableLayout tl = (TableLayout) findViewById(R.id.chapterLayout);
		tl.removeAllViews();
		
		LayoutInflater inflater = getLayoutInflater();
		
		while (!cursor.isAfterLast()) {
			int verseId = cursor.getInt(0);
			String verse = tf == null ? cursor.getString(1) : ComplexCharacterMapper.fix(cursor.getString(1), renderingFix);
		
			TableRow tr = (TableRow)inflater.inflate(rowLayout, tl, false);
			TextView t = (TextView) tr.findViewById(R.id.verse);
			
			t.setTextSize(fontSize);
			if(tf != null) {
				t.setTypeface(tf);
			}
			t.setText(verseId > 0 ? verseId + ". " + verse : verse);
			
			tl.addView(tr);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		adapter.close();
	}
	
	private void showTwoLanguagesVerseByVerse(int renderingFix, float fontSize, int language, int secLanguage) {
		setTheme(ThemeUtils.getThemeResource());
		setContentView(R.layout.chapterbothverse);
		
		if(cursor == null || cursor.isClosed()) {
			return;
		}
		
		if(cursorSec == null || cursorSec.isClosed()) {
			return;
		}
		
		setupToolbar();
        		
		Resources res = getResources();
		Typeface tf = Typeface.createFromAsset(getAssets(),
				res.getString(R.string.font_name));
		
		TextView tv = (TextView) findViewById(R.id.heading);
		if(language == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(book.getName());
		}
		else {
			tv.setText(book.getEnglishName());
		}
		
		tv = (TextView) findViewById(R.id.headingSec);
		if(secLanguage == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(book.getName());
		}
		else {
			tv.setText(book.getEnglishName());
		}
		
		tv = (TextView) findViewById(R.id.chapterNumber);
		//tv.setTextSize(fontSize);
		if(language == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.chapter), renderingFix) + " " + chapterId);
		}
		else {
			tv.setText(res.getString(R.string.chaptereng)+ " " + chapterId);
		}
		
		tv = (TextView) findViewById(R.id.chapterNumberSec);
		//tv.setTextSize(fontSize);
		if(secLanguage == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.chapter), renderingFix) + " " + chapterId);
		}
		else {
			tv.setText(res.getString(R.string.chaptereng)+ " " + chapterId);
		}
		
		cursor.moveToFirst();
		cursorSec.moveToFirst();
		
		int rowLayout = R.layout.verserowboth;
				
		TableLayout tl = (TableLayout) findViewById(R.id.chapterLayout);
		tl.removeAllViews();
		
		LayoutInflater inflater = getLayoutInflater();
		
		boolean cursorAtLast = false;
		boolean cursorSecAtLast = false;
		
		int verseId = 0;
		String verse = null;
		int verseSecId = 0;
		String verseSec = null;
		
		while (!cursorAtLast || !cursorSecAtLast) {
			if(!cursorAtLast) {
				verseId = cursor.getInt(0);
				verse = language == Preference.LANG_MALAYALAM ? ComplexCharacterMapper.fix(cursor.getString(1), renderingFix) : cursor.getString(1);
			}
			
			if(!cursorSecAtLast) {
				verseSecId = cursorSec.getInt(0);
				verseSec = secLanguage == Preference.LANG_MALAYALAM ? ComplexCharacterMapper.fix(cursorSec.getString(1), renderingFix) :  cursorSec.getString(1);
			}
		
			TableRow tr = (TableRow)inflater.inflate(rowLayout, tl, false);
			TextView t;
			
			if((!cursorAtLast && verseId <= verseSecId) || cursorSecAtLast) {
				t = (TextView) tr.findViewById(R.id.verse);
				t.setTextSize(fontSize);
				if(language == Preference.LANG_MALAYALAM) {
					t.setTypeface(tf);				
				}
				t.setText(verseId > 0 ? verseId + ". " + verse : verse);
				
				cursorAtLast = !cursor.moveToNext();
			}
			
			if((!cursorSecAtLast && verseSecId <= verseId) || cursorAtLast) {
				t = (TextView) tr.findViewById(R.id.verseSec);
				t.setTextSize(fontSize);
				if(secLanguage == Preference.LANG_MALAYALAM) {
					t.setTypeface(tf);				
				}
				t.setText(verseId > 0 ? verseSecId + ". " + verseSec : verseSec);
				cursorSecAtLast = !cursorSec.moveToNext();
			}
						
			tl.addView(tr);
		}
		
		cursor.close();
		cursorSec.close();

		adapter.close();
	}
	
	private void showTwoLanguagesSplit(int renderingFix, float fontSize, final int language, final int secLanguage, int languageLayout) {
		setTheme(ThemeUtils.getThemeResource());
		if(languageLayout == Preference.LAYOUT_BOTH_SPLIT) {
			setContentView(R.layout.chaptersplitvertical);
		}
		else {
			setContentView(R.layout.chaptersplithorizontal);
		}
		
		if(cursor == null || cursor.isClosed()) {
			return;
		}
		
		setupToolbar();
        		
		Resources res = getResources();
		Typeface tf = Typeface.createFromAsset(getAssets(), res.getString(R.string.font_name));
		
		TextView tv = (TextView) findViewById(R.id.heading);
		if(language == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(book.getName());
		}
		else {
			tv.setText(book.getEnglishName());
		}
		
		tv = (TextView) findViewById(R.id.headingSec);
		if(secLanguage == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(book.getName());
		}
		else {
			tv.setText(book.getEnglishName());
		}
		
		tv = (TextView) findViewById(R.id.chapterNumber);
		//tv.setTextSize(fontSize);
		if(language == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.chapter), renderingFix) + " " + chapterId);
		}
		else {
			tv.setText(res.getString(R.string.chaptereng) + " " + chapterId);
		}
		
		tv = (TextView) findViewById(R.id.chapterNumberSec);
		//tv.setTextSize(fontSize);
		if(secLanguage == Preference.LANG_MALAYALAM) {
			tv.setTypeface(tf);
			tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.chapter), renderingFix) + " " + chapterId);
		}
		else {
			tv.setText(res.getString(R.string.chaptereng) + " " + chapterId);
		}
		
		cursor.moveToFirst();
		cursorSec.moveToFirst();
		
		int rowLayout = R.layout.verserow;
		
		oScrollViewOne = (ObservableScrollView) findViewById(R.id.oScrollViewOne);
		oScrollViewTwo = (ObservableScrollView) findViewById(R.id.oScrollViewTwo);
		
		oScrollViewOne.setScrollViewListener(this);
	    oScrollViewTwo.setScrollViewListener(this);
				
		final TableLayout tl = (TableLayout) findViewById(R.id.chapterLayout);
		tl.removeAllViews();
		
		LayoutInflater inflater = getLayoutInflater();
		
		int lastVerseId = 0;
		boolean needZeroVerse = false;
		TableRow tr = null;
		TextView t = null;
		
		int tl1c = 0;
		int tl2c = 0;
		
		while (!cursor.isAfterLast()) {
			int verseId = cursor.getInt(0);
			
			String verse = language == Preference.LANG_MALAYALAM ? ComplexCharacterMapper.fix(cursor.getString(1), renderingFix) : cursor.getString(1);
		
			tr = (TableRow)inflater.inflate(rowLayout, tl, false);
			t = (TextView) tr.findViewById(R.id.verse);
			
			if(cursor.isFirst()) {
				int v2 = cursorSec.getInt(0);
				if(verseId > 0 && v2 == 0) {
					tl.addView(tr);
					tr = (TableRow)inflater.inflate(rowLayout, tl, false);
					t = (TextView) tr.findViewById(R.id.verse);
					tl1c++;
				}
				else if(verseId == 0 && v2 != 0) {
					needZeroVerse = true;
				}
			}
			
			if(verseId - lastVerseId > 1) {
				for(int i=1; i<= (verseId - lastVerseId - 1); i++) {
					tl.addView(tr);
					tr = (TableRow)inflater.inflate(rowLayout, tl, false);
					t = (TextView) tr.findViewById(R.id.verse);
					tl1c++;
				}
			}
			
			t.setTextSize(fontSize);
			if(language == Preference.LANG_MALAYALAM) {
				t.setTypeface(tf);
			}
			t.setText(verseId > 0 ? verseId + ". " + verse : verse);
			
			tl.addView(tr);
			tl1c++;
			
			lastVerseId = verseId;
			
			cursor.moveToNext();
		}
		
		cursor.close();
		lastVerseId = 0;
		
		ViewTreeObserver tl1Observer = tl.getViewTreeObserver();
		if(tl1Observer.isAlive()) {
			tl1Observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					tl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					tl1Heights = new int[tl.getChildCount()];
					for(int i=0; i<tl.getChildCount(); i++) {
						tl1Heights[i] = ((TableRow)tl.getChildAt(i)).getChildAt(0).getHeight();
					}
				}
			});
		}
				
		final TableLayout tl2 = (TableLayout) findViewById(R.id.chapterLayoutSec);
		tl2.removeAllViews();
		
		while (!cursorSec.isAfterLast()) {
			int verseId = cursorSec.getInt(0);
			String verse = secLanguage == Preference.LANG_MALAYALAM ? ComplexCharacterMapper.fix(cursorSec.getString(1), renderingFix) : cursorSec.getString(1);
		
			tr = (TableRow)inflater.inflate(rowLayout, tl2, false);
			t = (TextView) tr.findViewById(R.id.verse);
			
			if(cursorSec.isFirst() && needZeroVerse) {
				tl2.addView(tr);
				tr = (TableRow)inflater.inflate(rowLayout, tl2, false);
				t = (TextView) tr.findViewById(R.id.verse);
				tl2c++;
			}
			
			if(verseId - lastVerseId > 1) {
				for(int i=1; i<= (verseId - lastVerseId - 1); i++) {
					tl2.addView(tr);
					tr = (TableRow)inflater.inflate(rowLayout, tl2, false);
					t = (TextView) tr.findViewById(R.id.verse);
					tl2c++;
				}
			}
			
			t.setTextSize(fontSize);
			if(secLanguage == Preference.LANG_MALAYALAM) {
				t.setTypeface(tf);
			}
			t.setText(verseId > 0 ? verseId + ". " + verse : verse);
			
			tl2.addView(tr);
			tl2c++;
			
			lastVerseId = verseId;
			
			cursorSec.moveToNext();
		}
		cursorSec.close();
		
		if(tl1c < tl2c) {
			for(int i=1; i<= (tl2c - tl1c); i++) {
				tr = (TableRow)inflater.inflate(rowLayout, tl, false);
				t = (TextView) tr.findViewById(R.id.verse);
				tl.addView(tr);
			}
		}
		else if(tl1c > tl2c) {
			for(int i=1; i<= (tl1c - tl2c); i++) {
				tr = (TableRow)inflater.inflate(rowLayout, tl2, false);
				t = (TextView) tr.findViewById(R.id.verse);
				tl2.addView(tr);
			}
		}
		
		ViewTreeObserver tl2Observer = tl2.getViewTreeObserver();
		if(tl2Observer.isAlive()) {
			tl2Observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					tl2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					
					tl2Heights = new int[tl2.getChildCount()];
					for(int i=0; i<tl2.getChildCount(); i++) {
						tl2Heights[i] = ((TableRow)tl2.getChildAt(i)).getChildAt(0).getHeight();
					}
					
					if(language != secLanguage) {						
						int h1 = 0;
						int h2 = 0;
						int i1 = 0;
						int i2 = 0;
						int l1 = tl1Heights.length;
						int l2 = tl2Heights.length;
						
						while(i1 < l1 && i2 < l2) {
							h1 = tl1Heights[i1];
							h2 = tl2Heights[i2];
							
							if(h1 < h2) {
								((TableRow) tl.getChildAt(i1)).getChildAt(0).getLayoutParams().height = h2;
							}
							else if(h1 > h2) {
								((TableRow) tl2.getChildAt(i2)).getChildAt(0).getLayoutParams().height = h1;
							}
							
							h1 = h2 = 0;
							i1++;
							i2++;
						}
						tl.requestLayout();
						tl2.requestLayout();
					}
				}
			});
		}
		
		adapter.close();
	}

	private void setupToolbar() {
		Button button = (Button) findViewById(R.id.backButton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(ChapterViewActivity.this, MalayalamBibleActivity.class));
			}
		});
		
		button = (Button) findViewById(R.id.chaptersButton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent chaptersView = new Intent(ChapterViewActivity.this, ChaptersActivity.class);
				chaptersView.putExtra("com.jeesmon.malayalambible.Book", book);
				startActivity(chaptersView);
			}
		});
	    
	    Button previous = (Button) findViewById(R.id.prevButton);
        if(this.chapterId > 1 || this.book.getId() > 1) {
        	previous.setVisibility(View.VISIBLE);
        	previous.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				if(chapterId > 1) {
    					chapterId--;
    				}
    				else {
    					book = Utils.getBooks().get(book.getId() - 2);
    					chapterId = book.getChapters();
    				}
    				getContent();
    			}
    		});
        }
        else {
        	previous.setVisibility(View.GONE);
        }
        
        Button next = (Button) findViewById(R.id.nextButton);
        if(this.chapterId < this.book.getChapters() || this.book.getId() < 66 || (this.book.getId() == 66 && this.chapterId < 22)) {
        	next.setVisibility(View.VISIBLE);
        	next.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				if(chapterId < book.getChapters()) {
    					chapterId++;
    				}
    				else {
    					book = Utils.getBooks().get(book.getId());
    					chapterId = 1;
    				}
    				getContent();
    			}
    		});
        }
        else {
        	next.setVisibility(View.GONE);
        }
	}
	
	private class WorkerThread extends Thread {
		@Override
        public void run() {
			Preference pref = Preference.getInstance(ChapterViewActivity.this);
			
			adapter = new DataBaseAdapter(ChapterViewActivity.this);
			adapter.open();
			
			if(cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			
			if(cursorSec != null && !cursorSec.isClosed()) {
				cursorSec.close();
			}
			
			String table = "verses";
			switch(pref.getLanguage()) {
			case Preference.LANG_MALAYALAM:
				table = "verses";
				break;
			case Preference.LANG_ENGLISH:
				table = "verses_kjv";
				break;
			case Preference.LANG_ENGLISH_ASV:
				table = "verses_asv";
				break;
			}
			
			if(pref.getSecLanguage() == Preference.LANG_NONE) {
				cursor = adapter.fetchChapter(book.getId(), chapterId, table);
			}
			else {
				String table2 = "verses";
				switch(pref.getSecLanguage()) {
				case Preference.LANG_MALAYALAM:
					table2 = "verses";
					break;
				case Preference.LANG_ENGLISH:
					table2 = "verses_kjv";
					break;
				case Preference.LANG_ENGLISH_ASV:
					table2 = "verses_asv";
					break;
				}
				
				cursor = adapter.fetchChapter(book.getId(), chapterId, table);
				cursorSec = adapter.fetchChapter(book.getId(), chapterId, table2);
			}
			
			pref.setLastBook(book.getId());
			pref.setLastChapter(chapterId);
			
            handler.sendEmptyMessage(0);
        }

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                runOnUiThread(new Runnable() {
					public void run() {
						showContent();
					}
				});
                dialog.dismiss();
            }
        };
	}

	public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx,
			int oldy) {
		if (scrollView == oScrollViewOne) {
			oScrollViewTwo.scrollTo(x, y);
	    } else if (scrollView == oScrollViewTwo) {
	    	oScrollViewOne.scrollTo(x, y);
	    }
	}
}
