package com.easytravel.easytravel.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SubscribedUser implements Parcelable{
	private String driverName;
	private String upcomingTripsCount;
	private String allTripsCount;
	
	public SubscribedUser(String driverName, String upcomingTripsCount, String allTripsCount){
		this.driverName = driverName;
		this.upcomingTripsCount = upcomingTripsCount;
		this.allTripsCount = allTripsCount;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUpcomingTripsCount() {
		return upcomingTripsCount;
	}

	public void setUpcomingTripsCount(String upcomingTripsCount) {
		this.upcomingTripsCount = upcomingTripsCount;
	}

	public String getAllTripsCount() {
		return allTripsCount;
	}

	public void setAllTripsCount(String allTripsCount) {
		this.allTripsCount = allTripsCount;
	}
	
	public void UpcomingTrip(Parcel in) {
		String[] data = new String[3];
		in.readStringArray(data);

		this.driverName = data[0];
		this.upcomingTripsCount = data[1];
		this.allTripsCount = data[2];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.driverName, this.upcomingTripsCount, this.allTripsCount });
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
