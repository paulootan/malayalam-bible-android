package com.jeesmon.malayalambible;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseAdapter {
	private Context context;
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;

	public DataBaseAdapter(Context context) {
		this.context = context;
	}

	public DataBaseAdapter open() throws SQLException {
		dbHelper = new DataBaseHelper(context);
		database = dbHelper.getDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}
	
	public Cursor fetchAllBooks() {
		return database.query("books", new String[] { "book_id", "MalayalamShortName", "num_chptr", "EnglishShortName"}, 
				null, null, null,
				null, null);
	}
	
	public Cursor fetchChapter(int bookId, int chapterId, String table) {
		return database.query(table, new String[] { "verse_id", "verse_text"}, 
				"book_id = ? AND chapter_id = ?", new String[]{bookId + "", chapterId + ""}, null,
				null, null);
	}	
}
