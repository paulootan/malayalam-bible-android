/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2011 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.jeesmon.malayalambible.providers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.util.Log;

import com.jeesmon.malayalambible.model.items.BookmarkItem;

public class BookmarksProviderWrapper {
	private static final Uri BOOKMARKS_URI = Uri.parse("content://" + MalayalamBibleBookmarksContentProvider.AUTHORITY + "/" + MalayalamBibleBookmarksContentProvider.BOOKMARKS_TABLE);
	
	private static String[] sBookmarksProjection = new String[] { Browser.BookmarkColumns._ID,
        Browser.BookmarkColumns.TITLE,
        Browser.BookmarkColumns.URL,
        Browser.BookmarkColumns.VISITS,
        Browser.BookmarkColumns.DATE,
        Browser.BookmarkColumns.CREATED,
        Browser.BookmarkColumns.BOOKMARK,
        Browser.BookmarkColumns.FAVICON };		

	/**
	 * Bookmarks management.
	 */
	/**
	 * Get a Cursor on the whole content of the history/bookmarks database.
	 * @param contentResolver The content resolver.
	 * @return A Cursor.
	 * @see Cursor
	 */
	public static Cursor getAllRecords(ContentResolver contentResolver) {
		return contentResolver.query(BOOKMARKS_URI, sBookmarksProjection, null, null, null);
	}
	
	public static Cursor getBookmarks(ContentResolver contentResolver, int sortMode) {
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";

		String orderClause;		
    	switch (sortMode) {
    	case 0:
    		orderClause = Browser.BookmarkColumns.VISITS + " DESC, " + Browser.BookmarkColumns.TITLE + " COLLATE NOCASE";
    		break;
    	case 1:
    		orderClause = Browser.BookmarkColumns.TITLE + " COLLATE NOCASE";
    		break;
    	case 2:
    		orderClause = Browser.BookmarkColumns.CREATED + " DESC";
    		break;    	
    	default:
    		orderClause = Browser.BookmarkColumns.TITLE + " COLLATE NOCASE";
    		break;
    	}

		return contentResolver.query(BOOKMARKS_URI, sBookmarksProjection, whereClause, null, orderClause);
	}
	
	/**
	 * Get a list of most visited bookmarks items, limited in size.
	 * @param contentResolver The content resolver.
	 * @param limit The size limit.
	 * @return A list of BookmarkItem.
	 */
	public static List<BookmarkItem> getBookmarksWithLimit(ContentResolver contentResolver, int limit) {
		List<BookmarkItem> result = new ArrayList<BookmarkItem>();
		
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC";
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.FAVICON };
				
		Cursor cursor = contentResolver.query(BOOKMARKS_URI, colums, whereClause, null, orderClause);
		
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int columnId = cursor.getColumnIndex(Browser.BookmarkColumns._ID);
				int columnTitle = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
				
				int count = 0;
				while (!cursor.isAfterLast() &&
						(count < limit)) {
					
					BookmarkItem item = new BookmarkItem(
							cursor.getLong(columnId),
							cursor.getString(columnTitle),
							cursor.getString(columnUrl));
					
					result.add(item);
					
					count++;
					cursor.moveToNext();
				}
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	public static BookmarkItem getBookmarkById(ContentResolver contentResolver, long id) {
		BookmarkItem result = null;
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;
		
		Cursor c = contentResolver.query(BOOKMARKS_URI, sBookmarksProjection, whereClause, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				String title = c.getString(c.getColumnIndex(Browser.BookmarkColumns.TITLE));
                String url = c.getString(c.getColumnIndex(Browser.BookmarkColumns.URL));
                result = new BookmarkItem(id, title, url);
			}
			
			c.close();
		}
		
		return result;
	}
	
	public static void deleteBookmark(ContentResolver contentResolver, long id) {
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;
        
		Cursor c = contentResolver.query(BOOKMARKS_URI, sBookmarksProjection, whereClause, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				if (c.getInt(c.getColumnIndex(Browser.BookmarkColumns.BOOKMARK)) == 1) {
					if (c.getInt(c.getColumnIndex(Browser.BookmarkColumns.VISITS)) > 0) {
						
						// If this record has been visited, keep it in history, but remove its bookmark flag.
                        ContentValues values = new ContentValues();
                        values.put(Browser.BookmarkColumns.BOOKMARK, 0);
                        values.putNull(Browser.BookmarkColumns.CREATED);
                        
                        contentResolver.update(BOOKMARKS_URI, values, whereClause, null);

					} else {
						// never visited, it can be deleted.
						contentResolver.delete(BOOKMARKS_URI, whereClause, null);
					}
				}
			}
			
			c.close();
		}
	}
	
	/**
	 * Modify a bookmark/history record. If an id is provided, it look for it and update its values. If not, values will be inserted.
	 * If no id is provided, it look for a record with the given url. It found, its values are updated. If not, values will be inserted.
	 * @param contentResolver The content resolver.
	 * @param id The record id to look for.
	 * @param title The record title.
	 * @param url The record url.
	 * @param isBookmark If True, the record will be a bookmark.
	 */
	public static void setAsBookmark(ContentResolver contentResolver, long id, String title, String url, boolean isBookmark) {

		boolean bookmarkExist = false;

		if (id != -1) {
			String[] colums = new String[] { Browser.BookmarkColumns._ID };
			String whereClause = Browser.BookmarkColumns._ID + " = " + id;

			Cursor cursor = contentResolver.query(BOOKMARKS_URI, colums, whereClause, null, null);
			bookmarkExist = (cursor != null) && (cursor.moveToFirst());
		} else {
			String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.CREATED };
			String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\"";
			String orderClause = Browser.BookmarkColumns.CREATED + " DESC";

			Cursor cursor = contentResolver.query(BOOKMARKS_URI, colums, whereClause, null, orderClause);
			bookmarkExist = (cursor != null) && (cursor.moveToFirst());
			if (bookmarkExist) {
				try {
					long dateLong = cursor.getLong(cursor.getColumnIndex(Browser.BookmarkColumns.CREATED));
					Date date = new Date(dateLong);
					Calendar cal = Calendar.getInstance();
					int m = cal.get(Calendar.MONTH);
					int d = cal.get(Calendar.DAY_OF_MONTH);
					int y = cal.get(Calendar.YEAR);
					cal.setTime(date);
					if(cal.get(Calendar.DAY_OF_MONTH) != d || cal.get(Calendar.MONTH) != m || cal.get(Calendar.YEAR) != y) {
						bookmarkExist = false;
					}
					else {
						id = cursor.getLong(cursor.getColumnIndex(Browser.BookmarkColumns._ID));
					}
				}
				catch(Exception e) {
					bookmarkExist = false;
				}
			}
		}

		ContentValues values = new ContentValues();
		if (title != null) {
			values.put(Browser.BookmarkColumns.TITLE, title);
		}

		if (url != null) {
			values.put(Browser.BookmarkColumns.URL, url);
		}

		if (isBookmark) {
			values.put(Browser.BookmarkColumns.BOOKMARK, 1);
			values.put(Browser.BookmarkColumns.CREATED, new Date().getTime());
		} else {
			values.put(Browser.BookmarkColumns.BOOKMARK, 0);
		}

		if (bookmarkExist) {                                    
			contentResolver.update(BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);
		} else {                        
			contentResolver.insert(BOOKMARKS_URI, values);
		}
	}
	
	public static void toggleBookmark(ContentResolver contentResolver, long id, boolean bookmark) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID };
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;

		Cursor cursor = contentResolver.query(BOOKMARKS_URI, colums, whereClause, null, null);
		boolean recordExists = (cursor != null) && (cursor.moveToFirst());
		
		if (recordExists) {
			ContentValues values = new ContentValues();
			
			values.put(Browser.BookmarkColumns.BOOKMARK, bookmark);
			if (bookmark) {
				values.put(Browser.BookmarkColumns.CREATED, new Date().getTime());
			} else {
				values.putNull(Browser.BookmarkColumns.CREATED);
			}
			
			contentResolver.update(BOOKMARKS_URI, values, whereClause, null);
		}
	}
	
	public static Cursor getHistory(ContentResolver contentResolver) {
		String whereClause = Browser.BookmarkColumns.VISITS + " > 0";
        String orderClause = Browser.BookmarkColumns.DATE + " DESC";

        return contentResolver.query(BOOKMARKS_URI, sBookmarksProjection, whereClause, null, orderClause);
	}
	
	/**
	 * Delete an history record, e.g. reset the visited count and visited date if its a bookmark, or delete it if not.
	 * @param contentResolver The content resolver.
	 * @param id The history id.
	 */
	public static void deleteHistoryRecord(ContentResolver contentResolver, long id) {		
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;

		Cursor cursor = contentResolver.query(BOOKMARKS_URI, sBookmarksProjection, whereClause, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				if (cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.BOOKMARK)) == 1) {
					// The record is a bookmark, so we cannot delete it. Instead, reset its visited count and last visited date.
					ContentValues values = new ContentValues();
					values.put(Browser.BookmarkColumns.VISITS, 0);
					values.putNull(Browser.BookmarkColumns.DATE);

					contentResolver.update(BOOKMARKS_URI, values, whereClause, null);
				} else {
					// The record is not a bookmark, we can delete it.
					contentResolver.delete(BOOKMARKS_URI, whereClause, null);
				}
			}

			cursor.close();
		}
	}

	
	/**
	 * Update the history: visit count and last visited date.
	 * @param contentResolver The content resolver.
	 * @param title The title.
	 * @param url The url.
	 * @param originalUrl The original url 
	 */
	public static void updateHistory(ContentResolver contentResolver, String title, String url, String originalUrl) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.BOOKMARK, Browser.BookmarkColumns.VISITS };
		String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\" OR " + Browser.BookmarkColumns.URL + " = \"" + originalUrl + "\"";

		Cursor cursor = contentResolver.query(BOOKMARKS_URI, colums, whereClause, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {

				long id = cursor.getLong(cursor.getColumnIndex(Browser.BookmarkColumns._ID));
				int visits = cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.VISITS)) + 1;

				ContentValues values = new ContentValues();

				// If its not a bookmark, we can update the title. If we were doing it on bookmarks, we would override the title choosen by the user.
				if (cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.BOOKMARK)) != 1) {
					values.put(Browser.BookmarkColumns.TITLE, title);
				}

				values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
				values.put(Browser.BookmarkColumns.VISITS, visits);

				contentResolver.update(BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);

			} else {
				ContentValues values = new ContentValues();
				values.put(Browser.BookmarkColumns.TITLE, title);
				values.put(Browser.BookmarkColumns.URL, url);
				values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
				values.put(Browser.BookmarkColumns.VISITS, 1);
				values.put(Browser.BookmarkColumns.BOOKMARK, 0);

				contentResolver.insert(BOOKMARKS_URI, values);
			}               

			cursor.close();
		}
	}
	
	/**
	 * Remove from history values prior to now minus the number of days defined in preferences.
	 * Only delete history items, not bookmarks.
	 * @param contentResolver The content resolver.
	 */
	public static void truncateHistory(ContentResolver contentResolver, String prefHistorySize) {
		int historySize;
		try {
			historySize = Integer.parseInt(prefHistorySize);
		} catch (NumberFormatException e) {
			historySize = 90;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());          
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DAY_OF_YEAR, - historySize);

		String whereClause = "(" + Browser.BookmarkColumns.BOOKMARK + " = 0 OR " + Browser.BookmarkColumns.BOOKMARK + " IS NULL) AND " + Browser.BookmarkColumns.DATE + " < " + c.getTimeInMillis();
		
		try {
			contentResolver.delete(BOOKMARKS_URI, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("BookmarksProviderWrapper", "Unable to truncate history: " + e.getMessage());
		}
	}
    
	/**
	 * Update the favicon in history/bookmarks database.
	 * @param currentActivity The current acitivity.
	 * @param url The url.
	 * @param originalUrl The original url.
	 * @param favicon The favicon.
	 */
	public static void updateFavicon(Activity currentActivity, String url, String originalUrl, Bitmap favicon) {
		String whereClause;
		
		if (!url.equals(originalUrl)) {
			whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\" OR " + Browser.BookmarkColumns.URL + " = \"" + originalUrl + "\"";
		} else {
			whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\"";
		}

		//BitmapDrawable icon = ApplicationUtils.getNormalizedFaviconForBookmarks(currentActivity, favicon);
		BitmapDrawable icon = new BitmapDrawable(favicon);

		ByteArrayOutputStream os = new ByteArrayOutputStream();         
		icon.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, os);

		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.FAVICON, os.toByteArray());
		
		// Hack: Starting from Honeycomb, simple update of the favicon through an error, it need another field to update correctly...
		if (Build.VERSION.SDK_INT >= 11) {
			values.put(Browser.BookmarkColumns.URL, url);
		}

		try {
			currentActivity.getContentResolver().update(BOOKMARKS_URI, values, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("BookmarksProviderWrapper", "Unable to update favicon: " + e.getMessage());
		}
	}
	
	/**
	 * Clear the history/bookmarks table.
	 * @param contentResolver The content resolver.
	 * @param clearHistory If true, history items will be cleared.
	 * @param clearBookmarks If true, bookmarked items will be cleared.
	 */
	public static void clearHistoryAndOrBookmarks(ContentResolver contentResolver, boolean clearHistory, boolean clearBookmarks) {
		
		if (!clearHistory && !clearBookmarks) {
			return;
		}
		
		String whereClause = null;
		if (clearHistory && clearBookmarks) {
			whereClause = null;
		} else if (clearHistory) {
			whereClause = "(" + Browser.BookmarkColumns.BOOKMARK + " = 0) OR (" + Browser.BookmarkColumns.BOOKMARK + " IS NULL)";
		} else if (clearBookmarks) {
			whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		}
		
		contentResolver.delete(BOOKMARKS_URI, whereClause, null);		
	}
	
	/**
	 * Insert a full record in history/bookmarks database.
	 * @param contentResolver The content resolver.
	 * @param title The record title.
	 * @param url The record url.
	 * @param visits The record visit count.
	 * @param date The record last visit date.
	 * @param created The record bookmark creation date.
	 * @param bookmark The bookmark flag.
	 */
	public static void insertRawRecord(ContentResolver contentResolver, String title, String url, int visits, long date, long created, int bookmark) {
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.URL, url);
		values.put(Browser.BookmarkColumns.VISITS, visits);
		
		if (date > 0) {
			values.put(Browser.BookmarkColumns.DATE, date);
		} else {
			values.putNull(Browser.BookmarkColumns.DATE);
		}
		
		if (created > 0) {
			values.put(Browser.BookmarkColumns.CREATED, created);
		} else {
			values.putNull(Browser.BookmarkColumns.CREATED);
		}
		
		if (bookmark > 0) {
			values.put(Browser.BookmarkColumns.BOOKMARK, 1);
		} else {
			values.put(Browser.BookmarkColumns.BOOKMARK, 0);
		}
		
		contentResolver.insert(BOOKMARKS_URI, values);
	}
}
