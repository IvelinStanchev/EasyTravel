package com.easytravel.easytravel;

import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easytravel.easytravel.sqlite.DBPref;

@SuppressLint("NewApi")
public class SubscribeFragment extends Fragment {

	private String accessToken;
	private TextView driverName;
	private TextView driverUpcomingTrips;
	private TextView driverAllTrips;
	private Button subscribe;
	private String nameOfDriver;
	private String upcomingTripsCount;
	private String allTripsCount;

	public SubscribeFragment() {
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_subscribe,
				container, false);

		Bundle bundle = this.getArguments();
		String driverId = bundle.getString("driver_id");

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		accessToken = preferences.getString("access_token", "");

		driverName = (TextView) rootView.findViewById(R.id.tv_subscribe_name);
		driverUpcomingTrips = (TextView) rootView
				.findViewById(R.id.tv_subscribe_upcomingTrips);
		driverAllTrips = (TextView) rootView
				.findViewById(R.id.tv_subscribe_totalTrips);
		subscribe = (Button) rootView.findViewById(R.id.btn_subscribe);

		subscribe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean hasUserWithTheSameName = false;

				DBPref pref = new DBPref(getActivity().getApplicationContext());
				Cursor c = pref.getValues();

				if (c.moveToFirst()) {
					do {
						String currentDriverName = c.getString(c
								.getColumnIndex("driver_name"));
						if (currentDriverName.equals(driverName.getText()
								.toString())) {
							hasUserWithTheSameName = true;
						}
					} while (c.moveToNext());
				}
				c.close();
				if (hasUserWithTheSameName) {
					Toast.makeText(getActivity(),
							"You have already subscribed that user!",
							Toast.LENGTH_SHORT).show();
				} else {
					pref.addRecord(nameOfDriver, upcomingTripsCount,
							allTripsCount);

					pref.close();

					Toast.makeText(getActivity(), "Successfully subscribed!",
							Toast.LENGTH_SHORT).show();
				}

				pref.close();

				Fragment fragment = new HomeFragment(false);

				if (fragment != null) {
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				}
			}
		});

		new GetDriverDetails().execute(driverId, accessToken);

		return rootView;
	}

	private class GetDriverDetails extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String driverId = params[0];
			String accessToken = params[1];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://spa2014.bgcoder.com/api/drivers/" + driverId);
			httpget.setHeader("Authorization", "Bearer " + accessToken);

			try {
				HttpResponse response = httpClient.execute(httpget);

				HttpEntity entity = response.getEntity();

				String responseFinal = EntityUtils.toString(entity);

				return responseFinal;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			JSONObject obj;
			try {
				obj = new JSONObject(result);
				nameOfDriver = obj.getString("name");
				upcomingTripsCount = String.valueOf(obj
						.getInt("numberOfUpcomingTrips"));
				allTripsCount = String
						.valueOf(obj.getInt("numberOfTotalTrips"));

				driverName.setText("Driver name: " + nameOfDriver);
				driverUpcomingTrips.setText("Upcoming trips: "
						+ upcomingTripsCount);
				driverAllTrips.setText("All trips: " + allTripsCount);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
