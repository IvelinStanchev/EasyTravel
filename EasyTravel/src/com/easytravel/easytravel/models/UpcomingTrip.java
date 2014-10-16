package com.easytravel.easytravel.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UpcomingTrip implements Parcelable {
	private String mId;
	private String mDriverId;
	private String mDriverName;
	private String mFromCity;
	private String mToCity;
	private String mNumberOfFreeSeats;
	private String mDepartureDate;
	private String mIsMine;

	public UpcomingTrip(String id, String driverId, String driverName,
			String fromCity, String toCity, String departureDate,
			String numberOfFreeSeats, String isMine) {
		this.mId = id;
		this.mDriverId = driverId;
		this.mDriverName = driverName;
		this.mFromCity = fromCity;
		this.mToCity = toCity;
		this.mNumberOfFreeSeats = numberOfFreeSeats;
		this.mDepartureDate = departureDate;
		this.mIsMine = isMine;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
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

	public String getFromCity() {
		return mFromCity;
	}

	public void setFromCity(String fromCity) {
		this.mFromCity = fromCity;
	}

	public String getToCity() {
		return mToCity;
	}

	public void setToCity(String toCity) {
		this.mToCity = toCity;
	}

	public String getNumberOfFreeSeats() {
		return this.mNumberOfFreeSeats;
	}

	public void setNumberOfFreeSeats(String numberOfFreeSeats) {
		this.mNumberOfFreeSeats = numberOfFreeSeats;
	}

	public String getDepartureDate() {
		return mDepartureDate;
	}

	public void setDepartureDate(String departureDate) {
		this.mDepartureDate = departureDate;
	}

	public String isMine() {
		return mIsMine;
	}

	public void setMine(String isMine) {
		this.mIsMine = isMine;
	}

	public UpcomingTrip(Parcel in) {
		String[] data = new String[8];
		in.readStringArray(data);

		this.mId = data[0];
		this.mDriverId = data[1];
		this.mDriverName = data[2];
		this.mFromCity = data[3];
		this.mToCity = data[4];
		this.mDepartureDate = data[5];
		this.mNumberOfFreeSeats = data[6];
		this.mIsMine = data[7];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.mId, this.mDriverId, this.mDriverName,
				this.mFromCity, this.mToCity, this.mDepartureDate, 
				this.mNumberOfFreeSeats, this.mIsMine });
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
