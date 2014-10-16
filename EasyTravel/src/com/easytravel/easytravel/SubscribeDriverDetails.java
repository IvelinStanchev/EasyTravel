package com.easytravel.easytravel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.opengl.Visibility;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easytravel.easytravel.sqlite.DBPref;

@SuppressLint("NewApi")
public class SubscribeDriverDetails extends Fragment {

	private static final String DRIVERS_URL = "http://spa2014.bgcoder.com/api/drivers";

	private String mAccessToken;
	private String mDriverName;
	private String mDriverId;
	private TextView mNameOfDriver;
	private TextView mFromTown;
	private TextView mToTown;
	private TextView mDepartureDate;
	private TextView mNumberOfFreeSeats;
	private ProgressBar mLoadingItem;
	private InternetConnection mInternetConnection;

	public SubscribeDriverDetails(String driverName) {
		this.mDriverName = driverName;
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.fragment_subscribed_from_notification, container,
				false);

		mInternetConnection = new InternetConnection(getActivity());

		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new SubscribeDriverDetails(mDriverName));
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {

			mNameOfDriver = (TextView) rootView
					.findViewById(R.id.tv_driver_name_notification);
			mFromTown = (TextView) rootView
					.findViewById(R.id.tv_from_town_notification);
			mToTown = (TextView) rootView
					.findViewById(R.id.tv_to_town_notification);
			mDepartureDate = (TextView) rootView
					.findViewById(R.id.tv_departureDate_notification);
			mNumberOfFreeSeats = (TextView) rootView
					.findViewById(R.id.tv_numberOfFreeSeats_notification);
			mLoadingItem = (ProgressBar) rootView
					.findViewById(R.id.pg_notification);

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			mAccessToken = preferences.getString("access_token", "");

			new GetDriverId().execute(mDriverName);
		}

		return rootView;
	}

	private class GetDriverId extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String nameOfDriver = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					DRIVERS_URL + "/?page=1&username="
							+ nameOfDriver);
			httpget.setHeader("Authorization", "Bearer " + mAccessToken);

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

			JSONArray array;
			try {
				array = new JSONArray(result);

				JSONObject obj = array.getJSONObject(0);

				mDriverId = obj.getString("id");

				new GetDriverDetails().execute(mDriverId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private class GetDriverDetails extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String driverId = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(DRIVERS_URL + "/" + driverId);
			httpget.setHeader("Authorization", "Bearer " + mAccessToken);

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

			JSONArray trips;
			try {
				JSONObject jsonObj = new JSONObject(result);

				trips = jsonObj.getJSONArray("trips");

				JSONObject tripObject = trips.getJSONObject(trips.length() - 1);

				mLoadingItem.setVisibility(View.INVISIBLE);

				String fromTownInfo = String.valueOf(tripObject
						.getString("from"));
				String toTownInfo = String.valueOf(tripObject.getString("to"));
				String departureDateInfo = String.valueOf(tripObject
						.getString("departureDate"));
				String numberOfFreeSeatsInfo = String.valueOf(tripObject
						.getInt("numberOfFreeSeats"));
				mNameOfDriver.setText("Driver name: " + mDriverName);
				mFromTown.setText("Starting town: " + fromTownInfo);
				mToTown.setText("Ending town: " + toTownInfo);
				mDepartureDate.setText("Departure date: " + departureDateInfo);
				mNumberOfFreeSeats.setText("Free seats: "
						+ numberOfFreeSeatsInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
