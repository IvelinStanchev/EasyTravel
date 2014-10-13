package com.easytravel.easytravel.models;

import java.util.List;

import android.R.integer;

public class UpcomingTrip {
	private String id;
	private String driverId;
	private String driverName;
	private String fromCity;
	private String toCity;
	private int numberOfFreeSeats;
	private String departureDate;
	private boolean isMine;
	//private List<String> passengers;
	
	public UpcomingTrip(String id, String driverId,  String driverName, String fromCity, 
			String toCity, String departureDate, int numberOfFreeSeats, boolean isMine){
		this.id = id;
		this.driverId = driverId;
		this.driverName = driverName;
		this.fromCity = fromCity;
		this.toCity = toCity;
		this.numberOfFreeSeats = numberOfFreeSeats;
		this.departureDate = departureDate;
		this.isMine = isMine;
		//this.passengers = passengers;
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
	
	public int getNumberOfFreeSeats() {
		return this.numberOfFreeSeats;
	}
	
	public void setNumberOfFreeSeats(int numberOfFreeSeats) {
		this.numberOfFreeSeats = numberOfFreeSeats;
	}
	
	public String getDepartureDate() {
		return departureDate;
	}
	
	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}
	
	public boolean isMine() {
		return isMine;
	}
	
	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}
	
//	public List<String> getPassengers() {
//		return passengers;
//	}
//	
//	public void setPassengers(List<String> passengers) {
//		this.passengers = passengers;
//	}
}
