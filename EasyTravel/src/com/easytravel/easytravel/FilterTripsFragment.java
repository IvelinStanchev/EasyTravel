package com.easytravel.easytravel;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.easytravel.easytravel.adapters.UpcomingTripsAdapter;
import com.easytravel.easytravel.models.UpcomingTrip;

@SuppressLint("NewApi")
public class FilterTripsFragment extends Fragment {

	private static final String END_OF_LIST_MESSAGE = "No more items!";
	private static final String SUCCESSFULLY_JOINED_TRIP_MESSAGE = "Successfully joined a trip!";
	private static final String ERROR_JOINING_TRIP_MESSAGE = "Already part of that trip!";
	private static final int POST_DELAYED = 350;
	private static final int STATUS_CODE_OK = 200;
	private static final String TRIPS_URL = "http://spa2014.bgcoder.com/api/trips";

	private Button mFilter;
	private Spinner mFromTown;
	private Spinner mToTown;
	private String mFromTownString;
	private String mToTownString;
	private int mPage = 1;
	private int mDoubleClickListenerCounter = 0;
	private String mAccessToken;
	private ArrayList<UpcomingTrip> mUpcomingTrips;
	private ArrayList<UpcomingTrip> mUpcomingTripsForBundle;
	private boolean mLoadingMore;
	private UpcomingTripsAdapter mAdapter;
	private ListView mListView;
	private SwipeGestureListener mGestureListener;
	private InternetConnection mInternetConnection;

	public FilterTripsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_filter_trips,
				container, false);

		mInternetConnection = new InternetConnection(getActivity());

		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new FilterTripsFragment());
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			mFilter = (Button) rootView.findViewById(R.id.btn_filter);
			mFromTown = (Spinner) rootView.findViewById(R.id.spinner_startTown);
			mToTown = (Spinner) rootView.findViewById(R.id.spinner_endTown);

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			mAccessToken = preferences.getString("access_token", "");

			mUpcomingTrips = new ArrayList<UpcomingTrip>();
			mUpcomingTripsForBundle = new ArrayList<UpcomingTrip>();

			mListView = (ListView) rootView.findViewById(R.id.list_view_filter);

			if (savedInstanceState != null) {
				ArrayList<UpcomingTrip> savedItems = savedInstanceState
						.getParcelableArrayList("array");
				mUpcomingTrips = savedItems;
				mUpcomingTripsForBundle = savedItems;
			}

			mAdapter = new UpcomingTripsAdapter(getActivity(), mUpcomingTrips);
			mListView.setAdapter(mAdapter);

			mGestureListener = new SwipeGestureListener(getActivity(),
					mListView);
			mListView.setOnTouchListener(mGestureListener);

			mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					if (!mInternetConnection.isNetworkAvailable()) {
						Fragment fragment = new NoInternetConnectionFragment(
								new FilterTripsFragment());
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					} else {

						final UpcomingTrip item = mUpcomingTripsForBundle
								.get(position);

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

			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mDoubleClickListenerCounter++;
					Handler handler = new Handler();
					Runnable r = new Runnable() {

						@Override
						public void run() {
							mDoubleClickListenerCounter = 0;
						}
					};

					if (mDoubleClickListenerCounter == 1) {
						// Single click
						handler.postDelayed(r, POST_DELAYED);
					} else if (mDoubleClickListenerCounter == 2) {
						if (!mInternetConnection.isNetworkAvailable()) {
							Fragment fragment = new NoInternetConnectionFragment(
									new FilterTripsFragment());
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment)
									.commit();
						} else {
							if (!mInternetConnection.isNetworkAvailable()) {
								Fragment fragment = new NoInternetConnectionFragment(
										new FilterTripsFragment());
								FragmentManager fragmentManager = getFragmentManager();
								fragmentManager
										.beginTransaction()
										.replace(R.id.frame_container, fragment)
										.commit();
							} else {
								// Double click
								mDoubleClickListenerCounter = 0;
								final UpcomingTrip doubleClickedUpcomingTrip = mUpcomingTripsForBundle
										.get(position);

								new AlertDialog.Builder(getActivity())
										.setTitle("Join trip")
										.setMessage("Join to that trip?")
										.setPositiveButton(
												android.R.string.yes,
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {

														new JoinTrip().execute(
																doubleClickedUpcomingTrip
																		.getId(),
																mAccessToken);
													}
												})
										.setNegativeButton(
												android.R.string.no,
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														// do nothing
													}
												})
										.setIcon(
												android.R.drawable.ic_dialog_alert)
										.show();
							}
						}
					}
				}
			});

			mFilter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mInternetConnection.isNetworkAvailable()) {
						Fragment fragment = new NoInternetConnectionFragment(
								new FilterTripsFragment());
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					} else {
						mFromTownString = mFromTown.getSelectedItem()
								.toString();
						mToTownString = mToTown.getSelectedItem().toString();

						mAdapter.clear();

						mLoadingMore = false;

						Toast.makeText(getActivity(), "Loading trips...",
								Toast.LENGTH_LONG).show();

						mUpcomingTrips = new ArrayList<UpcomingTrip>();
						mUpcomingTripsForBundle = new ArrayList<UpcomingTrip>();

						new GetUpcomingTripsFromTownToTown().execute(mPage);
					}
				}
			});
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("array", mUpcomingTripsForBundle);
	}

	private class GetUpcomingTripsFromTownToTown extends
			AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			mLoadingMore = true;
			mPage = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(
					TRIPS_URL + "?page=" + mPage
							+ "&from=" + mFromTownString + "&to="
							+ mToTownString);
			httpget.setHeader("Content-Type", "application/json");
			httpget.setHeader("Authorization", "Bearer " + mAccessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONArray jsonObj = new JSONArray(responseFinal);

				for (int i = 0; i < jsonObj.length(); i++) {
					JSONObject c = jsonObj.getJSONObject(i);

					UpcomingTrip currentUpcomingTrip = new UpcomingTrip(
							c.getString("id"), c.getString("driverId"),
							c.getString("driverName"), c.getString("from"),
							c.getString("to"), c.getString("departureDate"),
							c.getString("numberOfFreeSeats"),
							c.getString("isMine"));

					mUpcomingTripsForBundle.add(currentUpcomingTrip);

					mUpcomingTrips.add(currentUpcomingTrip);
				}
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (mUpcomingTrips != null && mUpcomingTrips.size() > 0) {
				for (int i = 0; i < mUpcomingTrips.size(); i++){
					mAdapter.add(mUpcomingTrips.get(i));
				}
				mLoadingMore = false;
			}

			if (mUpcomingTrips.size() == 0) {
				Toast.makeText(getActivity(), END_OF_LIST_MESSAGE,
						Toast.LENGTH_SHORT).show();

			}

			mAdapter.notifyDataSetChanged();
		}
	}

	private class JoinTrip extends AsyncTask<String, Void, HttpResponse> {

		@Override
		protected HttpResponse doInBackground(String... params) {

			String tripId = params[0];
			String accessToken = params[1];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut httpput = new HttpPut(
					TRIPS_URL + tripId);
			httpput.setHeader("Authorization", "Bearer " + accessToken);

			try {
				HttpResponse response = httpClient.execute(httpput);

				return response;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			super.onPostExecute(response);

			if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
				Toast.makeText(getActivity(), SUCCESSFULLY_JOINED_TRIP_MESSAGE,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), ERROR_JOINING_TRIP_MESSAGE,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
