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
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint.Join;
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
public class HomeFragment extends Fragment {

	private ArrayList<UpcomingTrip> upcomingTrips;
	private ArrayList<UpcomingTrip> upcomingTripsForBundle;
	private String accessToken;
	private UpcomingTripsAdapter adapter;
	boolean loadingMore = false;
	private GetUpcomingTrips getUpcomingTrips;
	private int page = 0;
	private int counter = 0;
	private ListView listView;
	private View footerView;
	private int doubleClickListener = 0;
	
	public HomeFragment() {
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		accessToken = preferences.getString("access_token", "");
		
		upcomingTrips = new ArrayList<UpcomingTrip>();
		upcomingTripsForBundle = new ArrayList<UpcomingTrip>();
		
		footerView = ((LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listview_footer, null, false);
		
		listView = (ListView) rootView.findViewById(R.id.upcoming_trips_list);
		
		listView.addFooterView(footerView);
		
		if (savedInstanceState != null) {
			ArrayList<UpcomingTrip> savedItems = savedInstanceState.getParcelableArrayList("array");
			upcomingTrips = savedItems;
			upcomingTripsForBundle = savedItems;
			counter = upcomingTrips.size();
			page = upcomingTripsForBundle.size() / 10;
		}
		
		adapter = new UpcomingTripsAdapter(getActivity(), upcomingTrips);
		listView.setAdapter(adapter);
		
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
								bundle.putString("driver_id", item.getDriverId());
								fragment.setArguments(bundle);
								
								if (fragment != null) {
									FragmentManager fragmentManager = getFragmentManager();
									fragmentManager.beginTransaction()
											.replace(R.id.frame_container, fragment).commit();
								}
								
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
				
				return false;
			}
			
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				doubleClickListener++;
		        Handler handler = new Handler();
		        Runnable r = new Runnable() {

		            @Override
		            public void run() {
		            	doubleClickListener = 0;
		            }
		        };

		        if (doubleClickListener == 1) {
		            //Single click
		            handler.postDelayed(r, 350);
		        } else if (doubleClickListener == 2) {
		            //Double click
		        	doubleClickListener = 0;
		        	final UpcomingTrip doubleClickedUpcomingTrip = upcomingTripsForBundle.get(position);
		        	
		        	
		        	new AlertDialog.Builder(getActivity())
					.setTitle("Join trip")
					.setMessage("Join to that trip?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									
									JoinTrip joinTrip = new JoinTrip();
									joinTrip.execute(doubleClickedUpcomingTrip.getId());
									
									
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).setIcon(android.R.drawable.ic_dialog_alert).show();
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
					getUpcomingTrips = new GetUpcomingTrips();
					getUpcomingTrips.execute(page);
				}
			}
		});

		return rootView;
	}
	
	private class JoinTrip extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			
			String tripId = params[0];
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut httpput = new HttpPut(
					"http://spa2014.bgcoder.com/api/trips/" + tripId);
			httpput.setHeader("Authorization", "Bearer " + accessToken);

			try {
				HttpResponse response = httpClient.execute(httpput);

				HttpEntity entity = response.getEntity();

			    String responseFinal = EntityUtils.toString(entity);
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("array", upcomingTripsForBundle);
	}
	
	private class GetUpcomingTrips extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... params) {
			
			loadingMore = true;
			
			int page = params[0];
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
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
                //JSONArray trips = jsonObj.getJSONArray(responseFinal);
                
			    upcomingTrips = new ArrayList<UpcomingTrip>();
			    
                
			    //String address = c.getString("formatted_address");
				
			    
			    
                for (int i = 0; i < 10; i++) {
                	JSONObject c = jsonObj.getJSONObject(i);
                	
                	UpcomingTrip currentUpcomingTrip = new UpcomingTrip(c.getString("id"), 
			    			c.getString("driverId"), 
			    			c.getString("driverName"), 
			    			c.getString("from"), 
			    			c.getString("to"), 
			    			c.getString("departureDate"), 
			    			c.getString("numberOfFreeSeats"), 
			    			c.getString("isMine"));
                	
                	upcomingTripsForBundle.add(currentUpcomingTrip);
                	
			    	upcomingTrips.add(currentUpcomingTrip);
				}
                
				String pesho;
				pesho = "asdf";
				
				//return response;
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
				Toast.makeText(getActivity(), "No more items!", Toast.LENGTH_SHORT).show();
				listView.removeFooterView(footerView);
			}
			
			adapter.notifyDataSetChanged();
			
		}
	}
}
