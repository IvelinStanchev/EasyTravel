package com.easytravel.easytravel.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DBPref extends DBHelper{

	private static final String KEY_ROWID = "_id";
	
	public DBPref(Context context) {
		super(context);
	}

	public void addRecord(String driverName, String upcomingTrips, String allTrips){
		ContentValues cv = new ContentValues();
		cv.put("driver_name", driverName);
		cv.put("driver_upcoming_trips", upcomingTrips);
		cv.put("driver_all_trips", allTrips);
		this.db.insert("subscribed_users", null, cv);
	}
	
	public void updateRecord(int rowId, String driverName, String upcomingTrips, String allTrips){
		ContentValues cv = new ContentValues();
		cv.put("driver_name", driverName);
		cv.put("driver_upcoming_trips", upcomingTrips);
		cv.put("driver_all_trips", allTrips);
		
		this.db.update("subscribed_users", cv, KEY_ROWID + "=" + rowId, null);
	}
	
	public Cursor getValues(){
		return this.db.query("subscribed_users", 
				new String[]{ "driver_name", "driver_upcoming_trips", "driver_all_trips" }, 
				null, null, null, null, null);
	}
}
