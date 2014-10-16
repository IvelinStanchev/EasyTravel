package com.easytravel.easytravel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

class TripDetail extends AsyncTask<Void, Void, HttpResponse> {

	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_BAD_REQUEST = 400;
	private static final String SUCCESSFULLY_JOINED_TRIP_MESSAGE = "Trip joined successfully!";
	
	private String mAccessToken;
	private String mTripId;
	private Button mJoinTrip;
	private TextView mTripData;
	private HttpUriRequest mRequest;
	private Context mContext;

	public String getBearer() {
		return mAccessToken;
	}

	public void setBearer(String bearer) {
		this.mAccessToken = bearer;
	}

	public String getTripId() {
		return mTripId;
	}

	public void setTripId(String tripId) {
		this.mTripId = tripId;
	}

	public Button getBtn_JoinTrip() {
		return mJoinTrip;
	}

	public void setBtn_JoinTrip(Button joinTrip) {
		this.mJoinTrip = joinTrip;
	}

	public TextView getEt_TripData() {
		return mTripData;
	}

	public void setEt_TripData(TextView tripData) {
		this.mTripData = tripData;
	}

	public HttpUriRequest getRequest() {
		return mRequest;
	}

	public void setRequest(HttpUriRequest request) {
		this.mRequest = request;
	}
	
	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public TripDetail(String bearer, String tripId, Button btn_JoinTrip,
			TextView et_TripData, HttpUriRequest req, Context context) {
		this.setBearer(bearer);
		this.setTripId(tripId);
		this.setBtn_JoinTrip(btn_JoinTrip);
		this.setEt_TripData(et_TripData);
		this.setRequest(req);
		this.setContext(context);
	}

	@Override
	protected void onPostExecute(HttpResponse response) {
		super.onPostExecute(response);

		StringBuilder result = new StringBuilder();

		try {
			InputStream inputStream = response.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String currentLine = null;

			while ((currentLine = bufferedReader.readLine()) != null) {
				result.append(currentLine);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsnobject;
		try {
			jsnobject = new JSONObject(result.toString());

			if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {

				Toast.makeText(this.getContext(), SUCCESSFULLY_JOINED_TRIP_MESSAGE,
						Toast.LENGTH_SHORT).show();

				StringBuilder tripData = new StringBuilder();

				String driverName = jsnobject.getString("driverName");
				tripData.append("Driver name: " + driverName + "\n\n");

				String fromCity = jsnobject.getString("from");
				tripData.append("From city: " + fromCity + "\n\n");

				String toCity = jsnobject.getString("to");
				tripData.append("To city: " + toCity + "\n\n");

				String departureDate = jsnobject.getString("departureDate");
				tripData.append("Departure date: " + departureDate + "\n\n");

				String freeSeats = jsnobject.getString("numberOfFreeSeats");
				tripData.append("Free seats:  " + freeSeats + "\n\n");
			    
				mTripData.setText(tripData.toString());

			} else if (response.getStatusLine().getStatusCode() == STATUS_CODE_BAD_REQUEST) {
				String message = jsnobject.getString("message");

				mTripData.setText(message);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		mJoinTrip.setVisibility(View.INVISIBLE);
		mTripData.setVisibility(View.VISIBLE);
	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		this.mRequest.setHeader("Authorization", mAccessToken);
		this.mRequest.setHeader("Content-type", "application/json");

		try {
			HttpResponse response = httpClient.execute(this.mRequest);

			return response;
		} catch (Exception e) {
			Log.d("D1", e.toString());
		}

		return null;
	}
}
