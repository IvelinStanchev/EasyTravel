package com.easytravel.easytravel.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easytravel.easytravel.R;
import com.easytravel.easytravel.models.UpcomingTrip;

public class UpcomingTripsAdapter extends BaseAdapter {

	private ArrayList<UpcomingTrip> upcomingTrips;
	private LayoutInflater upcomingInf;

	public UpcomingTripsAdapter(Context c, ArrayList<UpcomingTrip> theTrips) {
		upcomingTrips = theTrips;
		upcomingInf = LayoutInflater.from(c);
	}

	public void add(UpcomingTrip upcomingTrip)
	{
		upcomingTrips.add(upcomingTrip);
	    notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return upcomingTrips.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// map to song layout
		LinearLayout upcomingTripsLay = (LinearLayout) upcomingInf.inflate(R.layout.upcoming_trips,
				parent, false);
		// get title and artist views
//		TextView id = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_id);
//		TextView driverId = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_driverId);
		TextView driverName = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_driverName);
		TextView from = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_from);
		TextView to = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_to);
		TextView departureDate = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_departureDate);
		TextView numberOfFreeSeats = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_numberOfFreeSeats);
//		TextView isMine = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_isMine);
		// get song using position
		UpcomingTrip currentUpcomingTrip = upcomingTrips.get(position);
		// get title and artist strings
//		id.setText(currentUpcomingTrip.getId());
//		driverId.setText(currentUpcomingTrip.getDriverId());
		driverName.setText("Driver: " + currentUpcomingTrip.getDriverName());
		from.setText("From: " + currentUpcomingTrip.getFromCity());
		to.setText("To: " + currentUpcomingTrip.getToCity());
		departureDate.setText("Departure date: " + currentUpcomingTrip.getDepartureDate());
		numberOfFreeSeats.setText("Free seats: " + currentUpcomingTrip.getNumberOfFreeSeats());
		//isMine.setText(currentUpcomingTrip.isMine());
		// set position as tag
		upcomingTripsLay.setTag(position);
		return upcomingTripsLay;
	}
}
