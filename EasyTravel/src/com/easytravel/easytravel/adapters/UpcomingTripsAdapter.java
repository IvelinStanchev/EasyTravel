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

	private ArrayList<UpcomingTrip> mUpcomingTrips;
	private LayoutInflater mUpcomingInf;

	public UpcomingTripsAdapter(Context c, ArrayList<UpcomingTrip> theTrips) {
		mUpcomingTrips = theTrips;
		mUpcomingInf = LayoutInflater.from(c);
	}

	public void add(UpcomingTrip upcomingTrip)
	{
		mUpcomingTrips.add(upcomingTrip);
	    notifyDataSetChanged();
	}
	
	public void clear(){
		this.mUpcomingTrips.clear();
	}
	
	@Override
	public int getCount() {
		return mUpcomingTrips.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout upcomingTripsLay = (LinearLayout) mUpcomingInf.inflate(R.layout.upcoming_trips,
				parent, false);
		TextView driverName = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_driverName);
		TextView from = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_from);
		TextView to = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_to);
		TextView departureDate = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_departureDate);
		TextView numberOfFreeSeats = (TextView) upcomingTripsLay.findViewById(R.id.tv_upcomingTrip_numberOfFreeSeats);

		UpcomingTrip currentUpcomingTrip = mUpcomingTrips.get(position);
		
		driverName.setText("Driver: " + currentUpcomingTrip.getDriverName());
		from.setText("From: " + currentUpcomingTrip.getFromCity());
		to.setText("To: " + currentUpcomingTrip.getToCity());
		departureDate.setText("Departure date: " + currentUpcomingTrip.getDepartureDate());
		numberOfFreeSeats.setText("Free seats: " + currentUpcomingTrip.getNumberOfFreeSeats());
		
		upcomingTripsLay.setTag(position);
		return upcomingTripsLay;
	}
}
