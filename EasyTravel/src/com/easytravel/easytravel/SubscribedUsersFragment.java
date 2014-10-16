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

import com.easytravel.easytravel.adapters.UsersAdapter;
import com.easytravel.easytravel.adapters.UpcomingTripsAdapter;
import com.easytravel.easytravel.models.User;
import com.easytravel.easytravel.sqlite.DBPref;

@SuppressLint("NewApi")
public class SubscribedUsersFragment extends Fragment {

	private static final String NO_SUBSCRIBED_DRIVERS_MESSAGE = "No subscribed drivers!";
	
	private ArrayList<User> mSubscribedUsers;
	private ArrayList<User> mSubscribedUsersForBundle;
	private UsersAdapter mAdapter;
	private ListView mListView;
	private InternetConnection mInternetConnection;

	public SubscribedUsersFragment() {
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_subscribed_users,
				container, false);

		mInternetConnection = new InternetConnection(getActivity());

		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new SubscribedUsersFragment());
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			mSubscribedUsers = new ArrayList<User>();
			mSubscribedUsersForBundle = new ArrayList<User>();

			mListView = (ListView) rootView
					.findViewById(R.id.subscribed_users_list);

			if (savedInstanceState != null) {
				ArrayList<User> savedItems = savedInstanceState
						.getParcelableArrayList("array");
				mSubscribedUsers = savedItems;
				mSubscribedUsersForBundle = savedItems;
			}

			mAdapter = new UsersAdapter(getActivity(), mSubscribedUsers);
			mListView.setAdapter(mAdapter);

			getAllSubscribedUsers();
		}

		return rootView;
	}

	public void getAllSubscribedUsers() {
		mSubscribedUsers = new ArrayList<User>();
		DBPref pref = new DBPref(getActivity().getApplicationContext());
		Cursor c = pref.getValues();

		if (c.moveToFirst()) {
			do {
				String currentDriverName = c.getString(c
						.getColumnIndex("driver_name"));
				String currentDriverUpcomingTripsCount = c.getString(c
						.getColumnIndex("driver_upcoming_trips"));
				String currentDriverAllTripsCount = c.getString(c
						.getColumnIndex("driver_all_trips"));

				mSubscribedUsers.add(new User(currentDriverName,
						currentDriverUpcomingTripsCount,
						currentDriverAllTripsCount, null));
			} while (c.moveToNext());
		}
		c.close();
		pref.close();

		loadListItems();

		if (mSubscribedUsers.size() == 0) {
			Fragment fragment = new HomeFragment(false);

			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			}

			Toast.makeText(getActivity(), NO_SUBSCRIBED_DRIVERS_MESSAGE,
					Toast.LENGTH_SHORT).show();
		}

		mAdapter.notifyDataSetChanged();
	}

	private void loadListItems(){
		if (mSubscribedUsers != null && mSubscribedUsers.size() > 0) {
			for (int i = 0; i < mSubscribedUsers.size(); i++){
				mAdapter.add(mSubscribedUsers.get(i));
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("array", mSubscribedUsersForBundle);
	}
}
