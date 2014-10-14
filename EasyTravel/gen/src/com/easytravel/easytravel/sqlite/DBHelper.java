package com.easytravel.easytravel.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	static final String DB_NAME = "mydb";
	static final int DB_CURRENT_VERSION = 1;
	protected SQLiteDatabase db;
	
	public DBHelper(Context context){
		super(context, DB_NAME, null, DB_CURRENT_VERSION);
		open();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table subscribed_users (_id integer primary key autoincrement, " + 
						"driver_name text not null, " + 
						"driver_upcoming_trips text not null, " +
						"driver_all_trips text not null);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void open() throws SQLException {
		db = getWritableDatabase();
	}
	
	public void close(){
		db.close();
	}
}
