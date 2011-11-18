package com.jeesmon.malayalambible;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class MalayalamBibleActivity extends Activity {
	private Context context = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		context = this;
		
		ImageView info = (ImageView) findViewById(R.id.infoButton);
	    info.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent infoView = new Intent(context, InfoActivity.class);
				startActivity(infoView);
			}
		});
		
		Resources res = getResources();
		Typeface tf = Typeface.createFromAsset(getAssets(),
				res.getString(R.string.font_name));
		
		TextView tv = (TextView) findViewById(R.id.heading);
		tv.setTypeface(tf);
		tv.setText(ComplexCharacterMapper.fix(res.getString(R.string.books)));
		
		TableLayout tl = (TableLayout) findViewById(R.id.booksLayout);
		TableRow tr;
		TextView t;
		ImageView img;

		DataBaseAdapter adapter = new DataBaseAdapter(this);
		adapter.open();

		Cursor cursor = adapter.fetchAllBooks();
		startManagingCursor(cursor);

		cursor.moveToFirst();
		int c = 0;
		final List<Book> books = new ArrayList<Book>();
		while (!cursor.isAfterLast()) {
			c++;
			if (c == 1) {
				tr = new TableRow(this);
				tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				tr.setBackgroundResource(R.layout.cell_shape_header);
				tr.setPadding(5, 5, 5, 0);

				t = new TextView(this);
				t.setTypeface(tf);
				t.setText(R.string.oldtestament);
				t.setTextColor(Color.BLACK);
				t.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				tr.addView(t);
				tl.addView(tr, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			} else if (c == 40) {
				tr = new TableRow(this);
				tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				tr.setBackgroundResource(R.layout.cell_shape_header);
				tr.setPadding(5, 5, 5, 0);

				t = new TextView(this);
				t.setTypeface(tf);
				t.setText(R.string.newtestament);
				t.setTextColor(Color.BLACK);
				t.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				tr.addView(t);
				tl.addView(tr, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}
			
			int bookId = cursor.getInt(0);
			String bookName = cursor.getString(1);
			int numOfChapters = cursor.getInt(2);
			
			books.add(new Book(bookId, bookName, numOfChapters));

			tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			tr.setPadding(5, 5, 5, 0);
			tr.setBackgroundResource(R.layout.cell_shape);
			tr.setClickable(true);
			tr.setId(c);
			tr.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent chaptersView = new Intent(context, ChaptersActivity.class);
					chaptersView.putExtra("com.jeesmon.malayalambible.Book", books.get(v.getId() - 1));
				    startActivity(chaptersView);
				}
			});
			t = new TextView(this);
			t.setTypeface(tf);
			t.setText(ComplexCharacterMapper.fix(bookName));
			t.setTextColor(Color.BLACK);
			t.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			tr.addView(t);
			
			img = new ImageView(this);
			img.setImageDrawable(res.getDrawable(R.drawable.ic_arrow));
			img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			img.setPadding(0, 0, 10, 0);
			
			tr.addView(img);

			tl.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			cursor.moveToNext();
		}

		cursor.close();
		adapter.close();
	}
}