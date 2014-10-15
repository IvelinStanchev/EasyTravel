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
	
	Button filter;
	Spinner fromTown;
	Spinner toTown;
	String fromTownString;
	String toTownString;
	private boolean loadingMore = true;
	private int page = 0;
	private String accessToken;
	private int doubleClickListener = 0;
	
	private ArrayList<UpcomingTrip> upcomingTrips;
	private ArrayList<UpcomingTrip> upcomingTripsForBundle;
	
	private UpcomingTripsAdapter adapter;
	private ListView listView;
	private View footerView;
	private SwipeGestureListener gestureListener;
	
	public FilterTripsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_filter_trips, container, false);
         
        filter = (Button) rootView.findViewById(R.id.btn_filter);
        fromTown = (Spinner) rootView.findViewById(R.id.spinner_startTown);
        toTown = (Spinner) rootView.findViewById(R.id.spinner_endTown);
        
        SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		accessToken = preferences.getString("access_token", "");
		
		upcomingTrips = new ArrayList<UpcomingTrip>();
		upcomingTripsForBundle = new ArrayList<UpcomingTrip>();
        
		listView = (ListView) rootView.findViewById(R.id.list_view_filter);
		
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
		
        filter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fromTownString = fromTown.getSelectedItem().toString();
				toTownString = toTown.getSelectedItem().toString();
				
				adapter.clear();
				
				loadingMore = false;
				
				Toast.makeText(getActivity(), "Loading trips...", Toast.LENGTH_LONG).show();
				
				
				upcomingTrips = new ArrayList<UpcomingTrip>();
				upcomingTripsForBundle = new ArrayList<UpcomingTrip>();
				
				new GetUpcomingTripsFromTownToTown().execute(page);
			}
		});
        
        return rootView;
    }
	
	private class GetUpcomingTripsFromTownToTown extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {

			loadingMore = true;
			
			//Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
			Log.d("D1", "Loading...");
			
			page = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(
					"http://spa2014.bgcoder.com/api/trips?page=" + page + "&from=" + fromTownString + "&to=" + toTownString);
			httpget.setHeader("Content-Type", "application/json");
			httpget.setHeader("Authorization", "Bearer " + accessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				JSONArray jsonObj = new JSONArray(responseFinal);

				// Getting JSON Array node
				// JSONArray trips = jsonObj.getJSONArray(responseFinal);

				//upcomingTrips = new ArrayList<UpcomingTrip>();

				// String address = c.getString("formatted_address");

				for (int i = 0; i < jsonObj.length(); i++) {
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
				
			}
			
			String pesho;
			pesho = "asdf";
			
			adapter.notifyDataSetChanged();

		}
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
}
