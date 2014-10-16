package com.easytravel.easytravel;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easytravel.easytravel.sqlite.DBPref;

@SuppressLint("NewApi")
public class SubscribeFragment extends Fragment {

	private static final String DRIVERS_URL = "http://spa2014.bgcoder.com/api/drivers";
	private static final String ALREADY_SUBSCRIBED_MESSAGE = "You have already subscribed that user!";
	private static final String SUCCESSFULLY_SUBSCRIBED_MESSAGE = "Successfully subscribed!";

	private String mAccessToken;
	private TextView mDriverName;
	private TextView mDriverUpcomingTrips;
	private TextView mDriverAllTrips;
	private Button mSubscribe;
	private String mNameOfDriver;
	private String mUpcomingTripsCount;
	private String mAllTripsCount;
	private ProgressBar mLoadingInfo;
	private InternetConnection mInternetConnection;

	public SubscribeFragment() {
	}
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_subscribe,
				container, false);

		mInternetConnection = new InternetConnection(getActivity());

		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new SubscribeFragment());
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		}

		Bundle bundle = this.getArguments();
		String driverId = bundle.getString("driver_id");

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		mAccessToken = preferences.getString("access_token", "");

		mLoadingInfo = (ProgressBar) rootView.findViewById(R.id.pb_subscribe);
		mDriverName = (TextView) rootView.findViewById(R.id.tv_subscribe_name);
		mDriverUpcomingTrips = (TextView) rootView
				.findViewById(R.id.tv_subscribe_upcomingTrips);
		mDriverAllTrips = (TextView) rootView
				.findViewById(R.id.tv_subscribe_totalTrips);
		mSubscribe = (Button) rootView.findViewById(R.id.btn_subscribe);

		mSubscribe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mInternetConnection.isNetworkAvailable()) {
					Fragment fragment = new NoInternetConnectionFragment(
							new SubscribeFragment());
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				} else {

					boolean hasUserWithTheSameName = false;

					DBPref pref = new DBPref(getActivity()
							.getApplicationContext());
					Cursor c = pref.getValues();

					if (c.moveToFirst()) {
						do {
							String currentDriverName = "Driver name: "
									+ c.getString(c
											.getColumnIndex("driver_name"));
							String driverNameToCompare = String
									.valueOf(mDriverName.getText());
							if (currentDriverName.equals(driverNameToCompare)) {
								hasUserWithTheSameName = true;
							}
						} while (c.moveToNext());
					}
					c.close();
					if (hasUserWithTheSameName) {
						Toast.makeText(getActivity(),
								ALREADY_SUBSCRIBED_MESSAGE, Toast.LENGTH_SHORT)
								.show();
					} else {
						pref.addRecord(mNameOfDriver, mUpcomingTripsCount,
								mAllTripsCount);

						pref.close();

						Toast.makeText(getActivity(),
								SUCCESSFULLY_SUBSCRIBED_MESSAGE,
								Toast.LENGTH_SHORT).show();
					}

					pref.close();

					Fragment fragment = new HomeFragment(false);

					if (fragment != null) {
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					}
				}
			}
		});

		new GetDriverDetails().execute(driverId, mAccessToken);

		return rootView;
	}

	private class GetDriverDetails extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String driverId = params[0];
			String accessToken = params[1];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(DRIVERS_URL + "/" + driverId);
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
				mNameOfDriver = obj.getString("name");
				mUpcomingTripsCount = String.valueOf(obj
						.getInt("numberOfUpcomingTrips"));
				mAllTripsCount = String.valueOf(obj
						.getInt("numberOfTotalTrips"));

				mLoadingInfo.setVisibility(View.INVISIBLE);
				mDriverName.setText("Driver name: " + mNameOfDriver);
				mDriverUpcomingTrips.setText("Upcoming trips: "
						+ mUpcomingTripsCount);
				mDriverAllTrips.setText("All trips: " + mAllTripsCount);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
