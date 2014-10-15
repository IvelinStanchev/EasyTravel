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

	private String bearer;
	private String tripId;
	private Button btn_JoinTrip;
	private TextView et_TripData;
	private HttpUriRequest request;
	private Context context;

	public String getBearer() {
		return bearer;
	}

	public void setBearer(String bearer) {
		this.bearer = bearer;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public Button getBtn_JoinTrip() {
		return btn_JoinTrip;
	}

	public void setBtn_JoinTrip(Button btn_JoinTrip) {
		this.btn_JoinTrip = btn_JoinTrip;
	}

	public TextView getEt_TripData() {
		return et_TripData;
	}

	public void setEt_TripData(TextView et_TripData) {
		this.et_TripData = et_TripData;
	}

	public HttpUriRequest getRequest() {
		return request;
	}

	public void setRequest(HttpUriRequest request) {
		this.request = request;
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
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
		// TODO Auto-generated method stub
		super.onPostExecute(response);

		StringBuilder str = new StringBuilder();

		try {
			Log.d("D1", "produljavame");
			InputStream is = response.getEntity().getContent();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String chunk = null;

			while ((chunk = br.readLine()) != null) {
				str.append(chunk);
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jsnobject;
		try {
			jsnobject = new JSONObject(str.toString());

			if (response.getStatusLine().getStatusCode() == 200) {

				Toast.makeText(this.getContext(), "Trip joined successfully!",
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
				
				/*JSONObject passengers = jsnobject.getJSONObject("passengers");
				
				tripData.append("Passengers: ");
			    Iterator<String> iter = passengers.keys();
			    
			    while(iter.hasNext()){
			        String key = (String)iter.next();
			        String passengerName = passengers.getString(key);
			        tripData.append(passengerName + ", ");
			    }*/
			    
				et_TripData.setText(tripData.toString());

			} else if (response.getStatusLine().getStatusCode() == 400) {
				String message = jsnobject.getString("message");

				et_TripData.setText(message);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		btn_JoinTrip.setVisibility(View.INVISIBLE);
		et_TripData.setVisibility(View.VISIBLE);
	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		this.request.setHeader("Authorization", bearer);
		this.request.setHeader("Content-type", "application/json");

		try {
			HttpResponse response = httpClient.execute(this.request);

			return response;
		} catch (Exception e) {
			Log.d("D1", e.toString());
		}

		return null;
	}
}
