package com.easytravel.easytravel;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.easytravel.easytravel.adapters.UpcomingTripsAdapter;
import com.easytravel.easytravel.models.UpcomingTrip;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment {

	private ArrayList<UpcomingTrip> upcomingTrips;
	private String accessToken;
	private UpcomingTripsAdapter adapter;
	boolean loadingMore = false;
	private GetUpcomingTrips getUpcomingTrips;
	private int page = 0;
	private int counter = 0;
	private ListView listView;
	private View footerView;
	
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
		
//		getUpcomingTrips = new GetUpcomingTrips();
//		getUpcomingTrips.execute(page);
		
		footerView = ((LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listview_footer, null, false);
		
		listView = (ListView) rootView.findViewById(R.id.upcoming_trips_list);
		
		listView.addFooterView(footerView);
		
		adapter = new UpcomingTripsAdapter(getActivity(), upcomingTrips);
		listView.setAdapter(adapter);
		
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// what is the bottom item that is visible
				int lastInScreen = firstVisibleItem + visibleItemCount;
				// is the bottom item visible & not loading more already ? Load
				// more !
				
				if ((lastInScreen == totalItemCount) && !(loadingMore)) {
					//upcomingTrips = new ArrayList<UpcomingTrips>();
					page++;
					getUpcomingTrips = new GetUpcomingTrips();
					getUpcomingTrips.execute(page);
					
					
//					Thread thread = new Thread(null, loadMoreListItems);
//					thread.start();
				}
				
//				if ((lastInScreen == totalItemCount) && !(loadingMore)) {
//					Toast.makeText(getActivity(), "Loading more...", Toast.LENGTH_SHORT);
//					
////					Thread thread = new Thread(null, loadMoreListItems);
////					thread.start();
//				}
			}
		});

		return rootView;
	}
	
//	private Runnable loadMoreListItems = new Runnable() {
//		@Override
//		public void run() {
//			// Set flag so we cant load new items 2 at the same time
//			loadingMore = true;
//			// Reset the array that holds the new items
//			upcomingTrips = new ArrayList<UpcomingTrips>();
//			// Simulate a delay, delete this on a production environment!
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//			}
//			// Get 10 new listitems
//			for (int i = 0; i < 10; i++) {
//				// Fill the item with some bogus information
//				Calendar c = Calendar.getInstance();
//
//				//clickCounter++;
//				
////				listItemsOriginal.add(String.valueOf(clickCounter) + "Date: "
////						+ (c.get(Calendar.MONTH) + 1) + "/"
////						+ c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR));
//				
////				upcomingTrips.add(String.valueOf(clickCounter) + "Date: "
////						+ (c.get(Calendar.MONTH) + 1) + "/"
////						+ c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR));
//				// +1 day
//				c.add(Calendar.DATE, 1);
//			}
//			// Done! now continue on the UI thread
//			runOnUiThread(returnRes);
//		}
//	};
//
//	private Runnable returnRes = new Runnable() {
//		@Override
//		public void run() {
//			// Loop thru the new items and add them to the adapter
//			if (listItems != null && listItems.size() > 0) {
//				for (int i = 0; i < listItems.size(); i++)
//					adapter.add(listItems.get(i));
//			}
//			// Update the Application title
//			setTitle("Neverending List with "
//					+ String.valueOf(adapter.getCount()) + " items");
//			// Tell to the adapter that changes have been made, this will cause
//			// the list to refresh
//			adapter.notifyDataSetChanged();
//			// Done loading more.
//			loadingMore = false;
//		}
//	};
	
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
                	
                	counter++;
                	
			    	upcomingTrips.add(new UpcomingTrip(String.valueOf(counter) + " - " + c.getString("id"), 
			    			c.getString("driverId"), 
			    			c.getString("driverName"), 
			    			c.getString("from"), 
			    			c.getString("to"), 
			    			c.getString("departureDate"), 
			    			c.getInt("numberOfFreeSeats"), 
			    			c.getBoolean("isMine")));
				}
                
//                String c2 = c.getString("driverName");
//                String c3 = c.getString("from");
//                String c4 = c.getString("to");
//                String c5 = c.getString("departureDate");
//                String c6 = String.valueOf(c.getInt("numberOfFreeSeats"));
//                String c7 = String.valueOf(c.getBoolean("isMine"));
//                
//                Log.d("D1", c.getString("id"));
//                Log.d("D1", c.getString("driverId"));
//                Log.d("D1", c.getString("driverName"));
//                Log.d("D1", c.getString("from"));
//                Log.d("D1", c.getString("to"));
//                Log.d("D1", c.getString("departureDate"));
//                Log.d("D1", String.valueOf(c.getInt("numberOfFreeSeats")));
//                Log.d("D1", String.valueOf(c.getBoolean("isMine")));
                
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
