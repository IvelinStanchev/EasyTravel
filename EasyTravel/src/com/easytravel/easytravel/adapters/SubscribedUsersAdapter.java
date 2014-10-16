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
import com.easytravel.easytravel.models.SubscribedUser;

public class SubscribedUsersAdapter extends BaseAdapter {

	private ArrayList<SubscribedUser> mSubscribedUsers;
	private LayoutInflater mSubscribedInf;

	public SubscribedUsersAdapter(Context c, ArrayList<SubscribedUser> theUsers) {
		mSubscribedUsers = theUsers;
		mSubscribedInf = LayoutInflater.from(c);
	}

	public void add(SubscribedUser subscribedUser)
	{
		mSubscribedUsers.add(subscribedUser);
	    notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mSubscribedUsers.size();
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
		LinearLayout subscribedUsersLay = (LinearLayout) mSubscribedInf.inflate(R.layout.subscribed_users,
				parent, false);;
		TextView driverName = (TextView) subscribedUsersLay.findViewById(R.id.tv_subscribedUsers_driverName);
		TextView upcomingTripsCount = (TextView) subscribedUsersLay.findViewById(R.id.tv_subscribedUsers_upcomingTripsCount);
		TextView allTripsCount = (TextView) subscribedUsersLay.findViewById(R.id.tv_subscribedUsers_allTripsCount);
		SubscribedUser currentSubscribedUser = mSubscribedUsers.get(position);
		
		driverName.setText("Driver: " + currentSubscribedUser.getDriverName());
		upcomingTripsCount.setText("Upcoming trips: " + currentSubscribedUser.getUpcomingTripsCount());
		allTripsCount.setText("All trips: " + currentSubscribedUser.getAllTripsCount());

		subscribedUsersLay.setTag(position);
		return subscribedUsersLay;
	}
}
