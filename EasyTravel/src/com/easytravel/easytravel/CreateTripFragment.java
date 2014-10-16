package com.easytravel.easytravel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CreateTripFragment extends Fragment implements OnClickListener {

	private static final String AVAILABLE_SEATS_ERROR = "Invalid number of available seats. Must be between 1 and 254";
	private static final String TRIP_CREATED_SUCCESSFULLY = "Trip created successfully!";
	private static final int MINIMUM_AVAILVABLE_SEATS = 1;
	private static final int MAXIMUM_AVAILVABLE_SEATS = 254;
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_BAD_REQUEST = 400;
	private static final String TRIPS_URL = "http://spa2014.bgcoder.com/api/trips";

	private Spinner mSpinnerForm;
	private Spinner mSpinnerEnd;
	private EditText mEtAvailableSeats;
	private Button mBtnMakeTrip;
	private EditText mEtGetDate;
	private String mAccessToken;
	private InternetConnection mInternetConnection;

	public CreateTripFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_create_trip,
				container, false);

		mInternetConnection = new InternetConnection(getActivity());

		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new CreateTripFragment());
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			mSpinnerForm = (Spinner) rootView
					.findViewById(R.id.spinner_create_town_startTown);
			mSpinnerEnd = (Spinner) rootView
					.findViewById(R.id.spinner_create_town_endTown);

			mEtAvailableSeats = (EditText) rootView
					.findViewById(R.id.et_available_seats);
			mBtnMakeTrip = (Button) rootView.findViewById(R.id.btn_make_trip);
			mEtGetDate = (EditText) rootView.findViewById(R.id.et_get_date);

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			mAccessToken = preferences.getString("access_token", "");

			mBtnMakeTrip.setOnClickListener(this);
		}

		return rootView;
	}

	@Override
	public void onClick(View v) {
		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new CreateTripFragment());
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			CreateTripPost createTrip = new CreateTripPost();

			String fromCity = mSpinnerForm.getSelectedItem().toString();
			String toCity = mSpinnerEnd.getSelectedItem().toString();
			Integer availableSeats = Integer.valueOf(String
					.valueOf(mEtAvailableSeats.getText()));
			String dateOfDeparture = String.valueOf(mEtGetDate.getText());

			if (availableSeats < MINIMUM_AVAILVABLE_SEATS || availableSeats > MAXIMUM_AVAILVABLE_SEATS) {
				Toast.makeText(getActivity(), AVAILABLE_SEATS_ERROR,
						Toast.LENGTH_SHORT).show();
			} else {
				createTrip.execute(fromCity, toCity,
						String.valueOf(availableSeats), dateOfDeparture);
			}
		}
	}

	private class CreateTripPost extends AsyncTask<String, Void, HttpResponse> {

		@Override
		protected void onPostExecute(HttpResponse response) {
			super.onPostExecute(response);

			StringBuilder result = new StringBuilder();
			try {
				InputStream inputStream = response.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				String currentRow = null;

				while ((currentRow = bufferedReader.readLine()) != null) {
					result.append(currentRow);
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				JSONObject jsonbject = new JSONObject(result.toString());

				if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
					Toast.makeText(getActivity(), TRIP_CREATED_SUCCESSFULLY,
							Toast.LENGTH_SHORT).show();

					StringBuilder tripData = new StringBuilder();

					String driverName = jsonbject.getString("driverName");
					tripData.append("Driver name: " + driverName + "\n\n");

					String fromCity = jsonbject.getString("from");
					tripData.append("From city: " + fromCity + "\n\n");

					String toCity = jsonbject.getString("to");
					tripData.append("To city: " + toCity + "\n\n");

					String departureDate = jsonbject.getString("departureDate");
					tripData.append("Departure date: " + departureDate + "\n\n");

					String freeSeats = jsonbject.getString("numberOfFreeSeats");
					tripData.append("Free seats:  " + freeSeats + "\n\n");

					Fragment fragment = new TripDetailFragment();

					Bundle bundle = new Bundle();
					bundle.putString("tripInfo", tripData.toString());
					fragment.setArguments(bundle);

					if (fragment != null) {
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					}

				} else if (response.getStatusLine().getStatusCode() == STATUS_CODE_BAD_REQUEST) {
					Toast.makeText(getActivity(),
							jsonbject.getString("message"), Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected HttpResponse doInBackground(String... params) {
			String fromCity = params[0];
			String toCity = params[1];
			String availableSeats = params[2];
			String dateOfDeparture = params[3];

			JSONObject jobj = new JSONObject();

			try {
				jobj.put("from", fromCity);
				jobj.put("to", toCity);
				jobj.put("availableSeats", availableSeats);
				jobj.put("departureTime", dateOfDeparture);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					TRIPS_URL);

			httppost.setHeader("Authorization", "Bearer " + mAccessToken);
			httppost.setHeader("Content-type", "application/json");

			try {
				httppost.setEntity(new StringEntity(jobj.toString()));
			} catch (UnsupportedEncodingException e1) {
				Log.d("D1", e1.toString());
			}

			try {
				HttpResponse response = httpClient.execute(httppost);

				return response;
			} catch (Exception e) {
				Log.d("D1", e.toString() + e.toString());
			}

			return null;
		}
	}
}