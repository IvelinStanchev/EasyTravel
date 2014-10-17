package com.easytravel.easytravel;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.easytravel.easytravel.adapters.UsersAdapter;
import com.easytravel.easytravel.models.UpcomingTrip;
import com.easytravel.easytravel.models.User;

@SuppressLint("NewApi")
public class FindPeopleFragment extends Fragment {

	private static final String END_OF_LIST_MESSAGE = "No more items!";
	private static final int STATUS_CODE_OK = 200;
	private static final String DRIVERS_URL = "http://spa2014.bgcoder.com/api/drivers";

	private int mPage = 1;
	private EditText mail;
	private Button findPeople;
	private String mailToSearch;
	private String mAccessToken;
	private ArrayList<User> mUsers;
	private ArrayList<User> mUsersForBundle;
	private ListView mListView;
	private UsersAdapter mAdapter;
	private boolean mIsInProgressOfLoadingUsers = false;
	private InternetConnection mInternetConnection;

	public FindPeopleFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_find_people,
				container, false);

		mInternetConnection = new InternetConnection(getActivity());
		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new FindPeopleFragment());

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} 
		else {
			mail = (EditText) rootView.findViewById(R.id.et_findPeople_mail);
			findPeople = (Button) rootView.findViewById(R.id.btn_findPeople);

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			mAccessToken = preferences.getString("access_token", "");

			mUsers = new ArrayList<User>();
			mUsersForBundle = new ArrayList<User>();

			mListView = (ListView) rootView
					.findViewById(R.id.list_view_find_people);

			if (savedInstanceState != null) {
				ArrayList<User> savedItems = savedInstanceState
						.getParcelableArrayList("array");
				mUsers = savedItems;
				mUsersForBundle = savedItems;
				mIsInProgressOfLoadingUsers = false;
			}

			mAdapter = new UsersAdapter(getActivity(), mUsers);
			mListView.setAdapter(mAdapter);

			findPeople.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mInternetConnection.isNetworkAvailable()) {
						Fragment fragment = new NoInternetConnectionFragment(
								new FindPeopleFragment());
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					} else {
						mailToSearch = String.valueOf(mail.getText());

						Toast.makeText(getActivity(), "Loading users...",
								Toast.LENGTH_LONG).show();

						mUsers = new ArrayList<User>();
						mUsersForBundle = new ArrayList<User>();

						mAdapter.clear();

						if (!mIsInProgressOfLoadingUsers) {
							new FindPeople().execute(mPage);
						}
					}
				}
			});

			mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					if (!mInternetConnection.isNetworkAvailable()) {
						Fragment fragment = new NoInternetConnectionFragment(
								new FindPeopleFragment());
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					} else {

						final User item = mUsersForBundle.get(position);

						new AlertDialog.Builder(getActivity())
								.setTitle("Subscribe to that user ?")
								.setMessage("Subscribe to that user ?")
								.setPositiveButton(android.R.string.yes,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

												Fragment fragment = new SubscribeFragment();

												Bundle bundle = new Bundle();
												bundle.putString("driver_id",
														item.getDriverId());
												fragment.setArguments(bundle);

												if (fragment != null) {
													FragmentManager fragmentManager = getFragmentManager();
													fragmentManager
															.beginTransaction()
															.replace(
																	R.id.frame_container,
																	fragment)
															.commit();
												}

											}
										})
								.setNegativeButton(android.R.string.no,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												// do nothing
											}
										})
								.setIcon(android.R.drawable.ic_dialog_alert)
								.show();
					}

					return false;
				}

			});
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("array", mUsersForBundle);
	}

	private class FindPeople extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			mPage = params[0];

			mIsInProgressOfLoadingUsers = true;

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(DRIVERS_URL + "?page=" + mPage
					+ "&username=" + mailToSearch);
			httpget.setHeader("Content-Type", "application/json");
			httpget.setHeader("Authorization", "Bearer " + mAccessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONArray jsonObj = new JSONArray(responseFinal);

				for (int i = 0; i < jsonObj.length(); i++) {
					JSONObject c = jsonObj.getJSONObject(i);

					User currentUser = new User(c.getString("name"),
							String.valueOf(c.getInt("numberOfUpcomingTrips")),
							String.valueOf(c.getInt("numberOfTotalTrips")),
							String.valueOf(c.getString("id")));

					mUsers.add(currentUser);

					mUsersForBundle.add(currentUser);
				}
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (mUsers != null && mUsers.size() > 0) {
				for (int i = 0; i < mUsers.size(); i++) {
					mAdapter.add(mUsers.get(i));
				}
			}

			if (mUsers.size() == 0) {
				Toast.makeText(getActivity(), END_OF_LIST_MESSAGE,
						Toast.LENGTH_SHORT).show();
			}

			mIsInProgressOfLoadingUsers = false;

			mAdapter.notifyDataSetChanged();
		}
	}
}
