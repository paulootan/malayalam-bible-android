package com.jeesmon.malayalambible;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableRow.LayoutParams;

public class ChapterButtonAdapter extends BaseAdapter {
	private Context mContext = null;
	private Book book = null;
	
	public ChapterButtonAdapter(Context c, Book book) {
        this.mContext = c;
        this.book = book;
    }

	public int getCount() {
		return book == null ? 0 : book.getChapters();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Button button = new Button(mContext);
		button.setId(position + 1);
		button.setText((position + 1) + "");
		button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		button.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent chapterView = new Intent(mContext, ChapterViewActivity.class);
				chapterView.putExtra("com.jeesmon.malayalambible.Book", book);
				chapterView.putExtra("chapterId", v.getId());
				mContext.startActivity(chapterView);
			}
		});
		
		return button;
	}
}
