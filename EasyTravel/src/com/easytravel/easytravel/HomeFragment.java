package com.easytravel.easytravel;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.easytravel.easytravel.adapters.UpcomingTripsAdapter;
import com.easytravel.easytravel.models.UpcomingTrip;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment implements SensorEventListener,
		LocationListener {

	private final static int POST_DELAYED = 350;
	private final static int ITEMS_PER_PAGE = 10;
	private final static int UPDATE_INTERVAL = 100;
	private final static int SHAKING_SPEED_BOOST = 10000;
	private final static String TRIPS_URL = "http://spa2014.bgcoder.com/api/trips";
	private static final String SUCCESSFULLY_JOINED_TRIP_MESSAGE = "Successfully joined a trip!";
	private static final String ERROR_JOINING_TRIP_MESSAGE = "Already part of that trip!";
	private static final int STATUS_CODE_OK = 200;
	private static final String GOOGLE_GEOCODING_API_KEY = "AIzaSyCj7Dr0HZKRcMnIBuLm2HIWDMXKek1UtTA";
	private static final int SHAKE_THRESHOLD = 600;

	private ArrayList<UpcomingTrip> mUpcomingTrips;
	private ArrayList<UpcomingTrip> mUpcomingTripsForBundle;
	private String mAccessToken;
	private UpcomingTripsAdapter mAdapter;
	boolean mLoadingMore = false;
	private int mPage = 0;
	private ListView mListView;
	private View mFooterView;
	private int mDoubleClickListener = 0;
	private LocationManager mLocationManager;
	private InternetConnection mInternetConnection;

	// variables for shaking function
	private SensorManager mSenSensorManager;
	private Sensor mSenAccelerometer;
	private long mLastUpdate;
	private float mLastX, mLastY, mLastZ;

	private SwipeGestureListener mGestureListener;
	private boolean mHasLocation = false;
	private String mCurrentTown;

	public HomeFragment() {
	}

	public HomeFragment(boolean hasLocation) {
		this.mHasLocation = hasLocation;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		mSenSensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		mSenAccelerometer = mSenSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSenSensorManager.registerListener(this, mSenAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		
		mInternetConnection = new InternetConnection(getActivity());

		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment;
			
			if (mHasLocation) {
				fragment =  new NoInternetConnectionFragment(
						new HomeFragment(true));
			}
			else{
				fragment =  new NoInternetConnectionFragment(
						new HomeFragment(false));
			}
			
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			mLocationManager = (LocationManager) getActivity()
					.getSystemService(Context.LOCATION_SERVICE);

			if (mHasLocation) {
				getCurrentCoordinates();
			}



			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			mAccessToken = preferences.getString("access_token", "");

			mUpcomingTrips = new ArrayList<UpcomingTrip>();
			mUpcomingTripsForBundle = new ArrayList<UpcomingTrip>();

			mFooterView = ((LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.listview_footer, null, false);

			mListView = (ListView) rootView
					.findViewById(R.id.upcoming_trips_list);

			mListView.addFooterView(mFooterView);

			if (savedInstanceState != null) {
				ArrayList<UpcomingTrip> savedItems = savedInstanceState
						.getParcelableArrayList("array");
				boolean hasLocationBundle = savedInstanceState
						.getBoolean("hasLocation");

				mHasLocation = hasLocationBundle;

				if (mHasLocation) {
					getCurrentCoordinates();
				}

				mUpcomingTrips = savedItems;
				mUpcomingTripsForBundle = savedItems;
				mPage = mUpcomingTripsForBundle.size() / ITEMS_PER_PAGE;
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
						Fragment fragment;
						
						if (mHasLocation) {
							fragment =  new NoInternetConnectionFragment(
									new HomeFragment(true));
						}
						else{
							fragment =  new NoInternetConnectionFragment(
									new HomeFragment(false));
						}
						
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
					mDoubleClickListener++;
					Handler handler = new Handler();
					Runnable r = new Runnable() {

						@Override
						public void run() {
							mDoubleClickListener = 0;
						}
					};

					if (mDoubleClickListener == 1) {
						// Single click
						handler.postDelayed(r, POST_DELAYED);
					} else if (mDoubleClickListener == 2) {
						if (!mInternetConnection.isNetworkAvailable()) {
							Fragment fragment = new NoInternetConnectionFragment(
									new FilterTripsFragment());
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment)
									.commit();
						} else {
							// Double click
							mDoubleClickListener = 0;
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
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						}
					}
				}
			});

			mListView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					int lastInScreen = firstVisibleItem + visibleItemCount;
					if ((lastInScreen == totalItemCount) && !(mLoadingMore)) {
						mPage++;

						if (mHasLocation) {
							if (mCurrentTown != null) {
								new GetUpcomingTripsAtMyLocation()
										.execute(mPage);
							}

						} else {
							new GetUpcomingTrips().execute(mPage);
						}
					}
				}
			});
		}

		return rootView;
	}

	private void getCurrentCoordinates() {
		Location location = mLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (!(location != null && location.getTime() > Calendar.getInstance()
				.getTimeInMillis() - 2 * 60 * 1000)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}

		new GetLocation().execute(String.valueOf(location.getLatitude()),
				String.valueOf(location.getLongitude()));
	}

	private class JoinTrip extends AsyncTask<String, Void, HttpResponse> {

		@Override
		protected HttpResponse doInBackground(String... params) {

			String tripId = params[0];
			String accessToken = params[1];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut httpput = new HttpPut(TRIPS_URL + "/" + tripId);
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("array", mUpcomingTripsForBundle);
		outState.putBoolean("hasLocation", mHasLocation);
	}

	private class GetUpcomingTrips extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {

			mLoadingMore = true;

			int page = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(TRIPS_URL + "?page=" + page);
			httpget.setHeader("Content-Type", "application/json");
			httpget.setHeader("Authorization", "Bearer " + mAccessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONArray jsonObj = new JSONArray(responseFinal);

				mUpcomingTrips = new ArrayList<UpcomingTrip>();

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

			loadItemsInAdapter();
		}
	}

	private void loadItemsInAdapter() {
		if (mUpcomingTrips != null && mUpcomingTrips.size() > 0) {
			for (int i = 0; i < mUpcomingTrips.size(); i++) {
				mAdapter.add(mUpcomingTrips.get(i));
			}
			mLoadingMore = false;
		}

		if (mUpcomingTrips.size() == 0) {
			Toast.makeText(getActivity(), "No more items!", Toast.LENGTH_SHORT)
					.show();
			mListView.removeFooterView(mFooterView);
		}

		mAdapter.notifyDataSetChanged();
	}

	private class GetUpcomingTripsAtMyLocation extends
			AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			mLoadingMore = true;

			mPage = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(TRIPS_URL + "?page=" + mPage
					+ "&from=" + mCurrentTown);
			httpget.setHeader("Content-Type", "application/json");
			httpget.setHeader("Authorization", "Bearer " + mAccessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONArray jsonObj = new JSONArray(responseFinal);

				mUpcomingTrips = new ArrayList<UpcomingTrip>();

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

			loadItemsInAdapter();
		}
	}

	private class GetLocation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String latitude = params[0];
			String longitude = params[1];

			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpGet httppost = new HttpGet(
					"https://maps.googleapis.com/maps/api/geocode/json?latlng="
							+ latitude + "," + longitude + "&key="
							+ GOOGLE_GEOCODING_API_KEY);
			httppost.setHeader("Content-type", "application/json");

			try {
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONObject jsonObj = new JSONObject(responseFinal);

				JSONArray results = jsonObj.getJSONArray("results");

				JSONObject c = results.getJSONObject(0);
				JSONArray address = c.getJSONArray("address_components");
				JSONObject addressComponents = address.getJSONObject(2);
				String town = addressComponents.getString("long_name");

				return town;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			mCurrentTown = result;
			mUpcomingTrips = new ArrayList<UpcomingTrip>();
			mUpcomingTripsForBundle = new ArrayList<UpcomingTrip>();
			new GetUpcomingTripsAtMyLocation().execute(mPage);
		}

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onPause() {
		super.onPause();
		mSenSensorManager.unregisterListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mSenSensorManager.registerListener(this, mSenAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor sensor = sensorEvent.sensor;

		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = sensorEvent.values[0];
			float y = sensorEvent.values[1];
			float z = sensorEvent.values[2];

			long currentTime = System.currentTimeMillis();
			if ((currentTime - mLastUpdate) > UPDATE_INTERVAL) {
				long timeDifference = (currentTime - mLastUpdate);
				mLastUpdate = currentTime;

				float speed = Math.abs(x + y + z - mLastX - mLastY - mLastZ)
						/ timeDifference * SHAKING_SPEED_BOOST;

				if (speed > SHAKE_THRESHOLD) {
					mUpcomingTrips = new ArrayList<UpcomingTrip>();
					mUpcomingTripsForBundle = new ArrayList<UpcomingTrip>();
					mPage = 0;
					mLoadingMore = false;
					mAdapter.clear();
					mAdapter.notifyDataSetChanged();
				}

				mLastX = x;
				mLastY = y;
				mLastZ = z;
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
