package com.easytravel.easytravel.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UpcomingTrip implements Parcelable {
	private String id;
	private String driverId;
	private String driverName;
	private String fromCity;
	private String toCity;
	private String numberOfFreeSeats;
	private String departureDate;
	private String isMine;

	// private List<String> passengers;

	public UpcomingTrip(String id, String driverId, String driverName,
			String fromCity, String toCity, String departureDate,
			String numberOfFreeSeats, String isMine) {
		this.id = id;
		this.driverId = driverId;
		this.driverName = driverName;
		this.fromCity = fromCity;
		this.toCity = toCity;
		this.numberOfFreeSeats = numberOfFreeSeats;
		this.departureDate = departureDate;
		this.isMine = isMine;
		// this.passengers = passengers;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public String getNumberOfFreeSeats() {
		return this.numberOfFreeSeats;
	}

	public void setNumberOfFreeSeats(String numberOfFreeSeats) {
		this.numberOfFreeSeats = numberOfFreeSeats;
	}

	public String getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}

	public String isMine() {
		return isMine;
	}

	public void setMine(String isMine) {
		this.isMine = isMine;
	}

	public UpcomingTrip(Parcel in) {
		String[] data = new String[8];
		in.readStringArray(data);

		this.id = data[0];
		this.driverId = data[1];
		this.driverName = data[2];
		this.fromCity = data[3];
		this.toCity = data[4];
		this.departureDate = data[5];
		this.numberOfFreeSeats = data[6];
		this.isMine = data[7];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.id, this.driverId, this.driverName,
				this.fromCity, this.toCity, this.departureDate, 
				this.numberOfFreeSeats, this.isMine });
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
