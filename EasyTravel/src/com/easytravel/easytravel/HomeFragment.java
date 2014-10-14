package com.easytravel.easytravel;

import java.io.InputStream;
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

	private ArrayList<UpcomingTrip> upcomingTrips;
	private ArrayList<UpcomingTrip> upcomingTripsForBundle;
	private String accessToken;
	private UpcomingTripsAdapter adapter;
	boolean loadingMore = false;
	private int page = 0;
	private ListView listView;
	private View footerView;
	private int doubleClickListener = 0;
	private LocationManager lm;

	// shaking
	private SensorManager senSensorManager;
	private Sensor senAccelerometer;
	private long lastUpdate;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 600;

	private SwipeGestureListener gestureListener;

	private boolean hasLocation = false;
	private String currentTown;

	public HomeFragment(boolean hasLocation) {
		this.hasLocation = hasLocation;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		if (hasLocation) {
			Location location = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (!(location != null && location.getTime() > Calendar
					.getInstance().getTimeInMillis() - 2 * 60 * 1000)) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
						0, this);
				Toast.makeText(getActivity(), "In the second if",
						Toast.LENGTH_SHORT).show();
			}

			new GetLocation().execute(String.valueOf(location.getLatitude()),
					String.valueOf(location.getLongitude()));

		}

		senSensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		senAccelerometer = senSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		senSensorManager.registerListener(this, senAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		accessToken = preferences.getString("access_token", "");

		upcomingTrips = new ArrayList<UpcomingTrip>();
		upcomingTripsForBundle = new ArrayList<UpcomingTrip>();

		footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listview_footer, null, false);

		listView = (ListView) rootView.findViewById(R.id.upcoming_trips_list);

		listView.addFooterView(footerView);

		if (savedInstanceState != null) {
			ArrayList<UpcomingTrip> savedItems = savedInstanceState
					.getParcelableArrayList("array");
			upcomingTrips = savedItems;
			upcomingTripsForBundle = savedItems;
			page = upcomingTripsForBundle.size() / 10;
		}

		adapter = new UpcomingTripsAdapter(getActivity(), upcomingTrips);
		listView.setAdapter(adapter);

		gestureListener = new SwipeGestureListener(getActivity(), listView);
		listView.setOnTouchListener(gestureListener);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final UpcomingTrip item = upcomingTripsForBundle.get(position);

				new AlertDialog.Builder(getActivity())
						.setTitle("Subscribe to that user ?")
						.setMessage("Subscribe to that user ?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
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
															fragment).commit();
										}

									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								}).setIcon(android.R.drawable.ic_dialog_alert)
						.show();

				return false;
			}

		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				doubleClickListener++;
				Handler handler = new Handler();
				Runnable r = new Runnable() {

					@Override
					public void run() {
						doubleClickListener = 0;
					}
				};

				if (doubleClickListener == 1) {
					// Single click
					handler.postDelayed(r, 350);
				} else if (doubleClickListener == 2) {
					// Double click
					doubleClickListener = 0;
					final UpcomingTrip doubleClickedUpcomingTrip = upcomingTripsForBundle
							.get(position);

					new AlertDialog.Builder(getActivity())
							.setTitle("Join trip")
							.setMessage("Join to that trip?")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											new JoinTrip().execute(
													doubleClickedUpcomingTrip
															.getId(),
													accessToken);
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
							.setIcon(android.R.drawable.ic_dialog_alert).show();
				}
			}
		});

		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if ((lastInScreen == totalItemCount) && !(loadingMore)) {
					page++;
					
					if (hasLocation) {
						new GetUpcomingTripsAtMyLocation().execute(page);
					}
					else{
						new GetUpcomingTrips().execute(page);
					}
				}
			}
		});

		return rootView;
	}

	private class JoinTrip extends AsyncTask<String, Void, HttpResponse> {

		@Override
		protected HttpResponse doInBackground(String... params) {

			String tripId = params[0];
			String accessToken = params[1];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut httpput = new HttpPut(
					"http://spa2014.bgcoder.com/api/trips/" + tripId);
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

			if (response.getStatusLine().getStatusCode() == 200) {
				Toast.makeText(getActivity(), "Successfully joined a trip!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "Already part of that trip!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("array", upcomingTripsForBundle);
	}

	private class GetUpcomingTrips extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {

			loadingMore = true;

			int page = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();

			if (hasLocation) {

			}

			HttpGet httpget = new HttpGet(
					"http://spa2014.bgcoder.com/api/trips?page=" + page);
			httpget.setHeader("Content-Type", "application/json");
			httpget.setHeader("Authorization", "Bearer " + accessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONArray jsonObj = new JSONArray(responseFinal);

				// Getting JSON Array node
				// JSONArray trips = jsonObj.getJSONArray(responseFinal);

				upcomingTrips = new ArrayList<UpcomingTrip>();

				// String address = c.getString("formatted_address");

				for (int i = 0; i < 10; i++) {
					JSONObject c = jsonObj.getJSONObject(i);

					UpcomingTrip currentUpcomingTrip = new UpcomingTrip(
							c.getString("id"), c.getString("driverId"),
							c.getString("driverName"), c.getString("from"),
							c.getString("to"), c.getString("departureDate"),
							c.getString("numberOfFreeSeats"),
							c.getString("isMine"));

					upcomingTripsForBundle.add(currentUpcomingTrip);

					upcomingTrips.add(currentUpcomingTrip);
				}

				String pesho;
				pesho = "asdf";

				// return response;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (upcomingTrips != null && upcomingTrips.size() > 0) {
				for (int i = 0; i < upcomingTrips.size(); i++)
					adapter.add(upcomingTrips.get(i));
				loadingMore = false;
			}

			if (upcomingTrips.size() == 0) {
				Toast.makeText(getActivity(), "No more items!",
						Toast.LENGTH_SHORT).show();
				listView.removeFooterView(footerView);
			}

			adapter.notifyDataSetChanged();

		}
	}
	
	private class GetUpcomingTripsAtMyLocation extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {

			loadingMore = true;

			int page = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();

			if (hasLocation) {

			}

			HttpGet httpget = new HttpGet(
					"http://spa2014.bgcoder.com/api/trips?page=" + page + "&from=" + currentTown);
			httpget.setHeader("Content-Type", "application/json");
			httpget.setHeader("Authorization", "Bearer " + accessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONArray jsonObj = new JSONArray(responseFinal);

				// Getting JSON Array node
				// JSONArray trips = jsonObj.getJSONArray(responseFinal);

				upcomingTrips = new ArrayList<UpcomingTrip>();

				// String address = c.getString("formatted_address");

				for (int i = 0; i < 10; i++) {
					JSONObject c = jsonObj.getJSONObject(i);

					UpcomingTrip currentUpcomingTrip = new UpcomingTrip(
							c.getString("id"), c.getString("driverId"),
							c.getString("driverName"), c.getString("from"),
							c.getString("to"), c.getString("departureDate"),
							c.getString("numberOfFreeSeats"),
							c.getString("isMine"));

					upcomingTripsForBundle.add(currentUpcomingTrip);

					upcomingTrips.add(currentUpcomingTrip);
				}

				String pesho;
				pesho = "asdf";

				// return response;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (upcomingTrips != null && upcomingTrips.size() > 0) {
				for (int i = 0; i < upcomingTrips.size(); i++)
					adapter.add(upcomingTrips.get(i));
				loadingMore = false;
			}

			if (upcomingTrips.size() == 0) {
				Toast.makeText(getActivity(), "No more items!",
						Toast.LENGTH_SHORT).show();
				listView.removeFooterView(footerView);
			}

			adapter.notifyDataSetChanged();

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
							+ latitude + "," + longitude
							+ "&key=AIzaSyCj7Dr0HZKRcMnIBuLm2HIWDMXKek1UtTA");
			// Depends on your web service
			httppost.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			String result = null;
			try {
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONObject jsonObj = new JSONObject(responseFinal);

				// Getting JSON Array node
				JSONArray results = jsonObj.getJSONArray("results");

				JSONObject c = results.getJSONObject(0);
				JSONArray address = c.getJSONArray("address_components");
				JSONObject addressComponents = address.getJSONObject(2);
				String town = addressComponents.getString("long_name");

				String pesho;
				pesho = "";

				return town;
			} catch (Exception e) {
				// Oops
				// Toast.makeText(MainActivity.this, "afaafa",
				// Toast.LENGTH_SHORT).show();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception squish) {
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			currentTown = result;
		}

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		super.onPause();
		senSensorManager.unregisterListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		senSensorManager.registerListener(this, senAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;

		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = sensorEvent.values[0];
			float y = sensorEvent.values[1];
			float z = sensorEvent.values[2];

			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms.
			if ((curTime - lastUpdate) > 100) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				float speed = Math.abs(x + y + z - last_x - last_y - last_z)
						/ diffTime * 10000;

				if (speed > SHAKE_THRESHOLD) {
					upcomingTrips = new ArrayList<UpcomingTrip>();
					upcomingTripsForBundle = new ArrayList<UpcomingTrip>();
					page = 1;
					loadingMore = false;

					adapter.clear();
					adapter.notifyDataSetChanged();
				}
				last_x = x;
				last_y = y;
				last_z = z;
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
