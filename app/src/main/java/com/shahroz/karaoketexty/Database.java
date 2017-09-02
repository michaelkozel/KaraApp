package com.shahroz.karaoketexty;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class Database{
	
	
	public Database(){
		
	}
	
	
	
	public abstract class Dabaproprties implements BaseColumns{
		public static final String SONG = "song";
		public static final String SONG_ID = "id";
		public static final String SONG_NAME = "name";
		public static final String SONG_INTERPRET = "interpret";
		public static final String SONG_TEXT = "text";
	}


}
