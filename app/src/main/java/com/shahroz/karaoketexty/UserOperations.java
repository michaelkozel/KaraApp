package com.shahroz.karaoketexty;

import java.util.ArrayList;
import java.util.List;

import com.shahroz.karaoketexty.Database.Dabaproprties;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserOperations extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";


    private static final String DATABASE_NAME = "User.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public static final String DATABASE_CREATE =
            "CREATE TABLE " + Dabaproprties.SONG + " (" +
                    Dabaproprties.SONG_ID + " INTEGER PRIMARY KEY," +
                    Dabaproprties.SONG_NAME + TEXT_TYPE + COMMA_SEP +
                    Dabaproprties.SONG_INTERPRET + TEXT_TYPE + COMMA_SEP +
                    Dabaproprties.SONG_TEXT + TEXT_TYPE +
                    " );";


    public UserOperations(Context context) {
        super(context, Dabaproprties.SONG, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + Dabaproprties.SONG);
        onCreate(db);
    }


    public void insert(UserOperations uop, String name, String interpret, String text) {
        SQLiteDatabase database = uop.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(Dabaproprties.SONG_NAME, name);
        cv.put(Dabaproprties.SONG_INTERPRET, interpret);
        cv.put(Dabaproprties.SONG_TEXT, text);


        database.insert(Dabaproprties.SONG, null, cv);

    }


    public Cursor read(UserOperations uop) {
        SQLiteDatabase database = uop.getReadableDatabase();
        String[] columns = {Dabaproprties.SONG_ID, Dabaproprties.SONG_NAME, Dabaproprties.SONG_INTERPRET, Dabaproprties.SONG_TEXT};
        Cursor cursor = database.query(Dabaproprties.SONG, columns, null, null, null, null, null);
        return cursor;
    }


    public void delete(UserOperations uop, String name, String interpret) {
        String selection = Dabaproprties.SONG_NAME + " LIKE ? AND " + Dabaproprties.SONG_INTERPRET + " LIKE ?";
        String args[] = {name, interpret};
        SQLiteDatabase database = uop.getWritableDatabase();
        database.delete(Dabaproprties.SONG, selection, args);
    }

}
