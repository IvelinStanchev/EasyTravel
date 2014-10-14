package com.easytravel.easytravel;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easytravel.easytravel.adapters.SubscribedUsersAdapter;
import com.easytravel.easytravel.adapters.UpcomingTripsAdapter;
import com.easytravel.easytravel.models.SubscribedUser;
import com.easytravel.easytravel.sqlite.DBPref;

@SuppressLint("NewApi")
public class SubscribedUsersFragment extends Fragment {

	private TextView driverName;
	private TextView driverUpcomingTrips;
	private TextView driverAllTrips;
	private ArrayList<SubscribedUser> subscribedUsers;
	private ArrayList<SubscribedUser> subscribedUsersForBundle;
	private SubscribedUsersAdapter adapter;
	private ListView listView;
	
	public SubscribedUsersFragment() {
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_subscribed_users,
				container, false);
		
		subscribedUsers = new ArrayList<SubscribedUser>();
		subscribedUsersForBundle = new ArrayList<SubscribedUser>();
		
		listView = (ListView) rootView.findViewById(R.id.subscribed_users_list);
		
		if (savedInstanceState != null) {
			ArrayList<SubscribedUser> savedItems = savedInstanceState.getParcelableArrayList("array");
			subscribedUsers = savedItems;
			subscribedUsersForBundle = savedItems;
		}
		
		adapter = new SubscribedUsersAdapter(getActivity(), subscribedUsers);
		listView.setAdapter(adapter);
		
		getAllSubscribedUsers();
		
		return rootView;
	}
	
	public void getAllSubscribedUsers(){
		subscribedUsers = new ArrayList<SubscribedUser>();
		DBPref pref = new DBPref(getActivity().getApplicationContext());
		Cursor c = pref.getValues();

		if (c.moveToFirst()) {
			do {
				String currentDriverName = c.getString(c.getColumnIndex("driver_name"));
				String currentDriverUpcomingTripsCount = c.getString(c.getColumnIndex("driver_upcoming_trips"));
				String currentDriverAllTripsCount = c.getString(c.getColumnIndex("driver_all_trips"));
				
				subscribedUsers.add(new SubscribedUser(currentDriverName, 
						currentDriverUpcomingTripsCount, currentDriverAllTripsCount));
			} while (c.moveToNext());
		}
		c.close();
		pref.close();
		
		if (subscribedUsers != null && subscribedUsers.size() > 0) {
			for (int i = 0; i < subscribedUsers.size(); i++)
				adapter.add(subscribedUsers.get(i));
		}
		
		if (subscribedUsers.size() == 0) {
			Fragment fragment = new HomeFragment(false);
			
			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			}
			
			Toast.makeText(getActivity(), "No subscribed drivers!", Toast.LENGTH_SHORT).show();
		}
		
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("array", subscribedUsersForBundle);
	}
}
