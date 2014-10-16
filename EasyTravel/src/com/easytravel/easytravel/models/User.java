package com.easytravel.easytravel.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	private String mDriverName;
	private String mUpcomingTripsCount;
	private String mAllTripsCount;
	private String mDriverId;
	
	public User(String driverName, String upcomingTripsCount, String allTripsCount, String driverId){
		this.mDriverName = driverName;
		this.mUpcomingTripsCount = upcomingTripsCount;
		this.mAllTripsCount = allTripsCount;
		this.mDriverId = driverId;
	}

	public String getDriverId() {
		return mDriverId;
	}
	
	public void setDriverId(String driverId) {
		this.mDriverId = driverId;
	}
	
	public String getDriverName() {
		return mDriverName;
	}

	public void setDriverName(String driverName) {
		this.mDriverName = driverName;
	}

	public String getUpcomingTripsCount() {
		return mUpcomingTripsCount;
	}

	public void setUpcomingTripsCount(String upcomingTripsCount) {
		this.mUpcomingTripsCount = upcomingTripsCount;
	}

	public String getAllTripsCount() {
		return mAllTripsCount;
	}

	public void setAllTripsCount(String allTripsCount) {
		this.mAllTripsCount = allTripsCount;
	}
	
	public void UpcomingTrip(Parcel in) {
		String[] data = new String[4];
		in.readStringArray(data);

		this.mDriverName = data[0];
		this.mUpcomingTripsCount = data[1];
		this.mAllTripsCount = data[2];
		this.mDriverId = data[3];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.mDriverName, this.mUpcomingTripsCount, this.mAllTripsCount, this.mDriverId });
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public UpcomingTrip createFromParcel(Parcel in) {
			return new UpcomingTrip(in);
		}

		public UpcomingTrip[] newArray(int size) {
			return new UpcomingTrip[size];
		}
	};
}
