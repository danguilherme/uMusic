/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ventura.musicexplorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ventura.musicexplorer.lyrics.provider.LyricProvider;
import com.ventura.musicexplorer.lyrics.provider.LyricProviderFactory;
import com.ventura.musicexplorer.lyrics.provider.LyricProviders;

public class ProvidersDbAdapter {

	public static final String KEY_NAME = "name";
	public static final String KEY_URL = "url";
	public static final String KEY_CODE = "code";
	public static final String KEY_ROWID = "_id";

	private static final String TAG = "ProvidersDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "create table providers (_id integer primary key autoincrement, "
			+ "name text not null, url text not null, code text not null);";

	private static final String DATABASE_NAME = "lf_data";
	private static final String DATABASE_TABLE = "providers";
	private static final int DATABASE_VERSION = 3;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS "
					+ ProvidersDbAdapter.DATABASE_TABLE);
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public ProvidersDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the providers database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public ProvidersDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new provider using the name, url and code provided. If the
	 * provider is successfully created return the new rowId for that one,
	 * otherwise return a -1 to indicate failure.
	 * 
	 * @param title
	 *            the name of the note
	 * @param body
	 *            the url of the note
	 * @param code
	 *            the enum type of the provider
	 * @return rowId or -1 if failed
	 */
	public long createProvider(String name, String url, LyricProviders code) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_URL, url);
		initialValues.put(KEY_CODE, code.toString());

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Create a new provider using the provider instance and code provided. If
	 * the provider is successfully created return the new rowId for that one,
	 * otherwise return a -1 to indicate failure.
	 * 
	 * @param title
	 *            the name of the note
	 * @param body
	 *            the url of the note
	 * @param code
	 *            the enum type of the provider
	 * @return rowId or -1 if failed
	 */
	public long createProvider(LyricProvider provider, LyricProviders code) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, provider.getName());
		initialValues.put(KEY_URL, provider.getUrl());
		initialValues.put(KEY_CODE, code.toString());

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Return a Cursor over the list of all providers in the database
	 * 
	 * @return Cursor over all providers
	 */
	public Cursor fetchAllProviders() {

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME,
				KEY_URL, KEY_CODE }, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the provider that matches the given rowId
	 * 
	 * @param rowId
	 *            id of provider to retrieve
	 * @return Cursor positioned to matching provider, if found
	 * @throws SQLException
	 *             if provider could not be found/retrieved
	 */
	public Cursor fetchProvider(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME,
				KEY_URL, KEY_CODE }, KEY_ROWID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Return a Cursor positioned at the provider that matches the given rowId
	 * 
	 * @param code
	 *            code of provider to retrieve
	 * @return Cursor positioned to matching provider, if found
	 * @throws Exception
	 */
	public LyricProvider fetchProvider(LyricProviders code) throws Exception {
		LyricProvider lyricProvider = null;

		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_URL, KEY_CODE }, KEY_CODE + "='"
				+ code.toString() + "'", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			int idColumnIndex = mCursor.getColumnIndex(KEY_ROWID);
			int nameColumnIndex = mCursor.getColumnIndex(KEY_NAME);
			int urlColumnIndex = mCursor.getColumnIndex(KEY_URL);
			int codeColumnIndex = mCursor.getColumnIndex(KEY_CODE);

			lyricProvider = LyricProviderFactory.getLyricProvider(code);
			lyricProvider.setId(mCursor.getInt(idColumnIndex));
			lyricProvider.setName(mCursor.getString(nameColumnIndex));
			lyricProvider.setUrl(mCursor.getString(urlColumnIndex));
			lyricProvider.setCode(Enum.valueOf(LyricProviders.class,
					mCursor.getString(codeColumnIndex)));
		}

		return lyricProvider;
	}
}
