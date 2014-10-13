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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CreateTripFragment extends Fragment implements OnClickListener {

	private EditText etFromCity;
	private EditText etToCity;
	private EditText etAvailbleSeats;
	private Button btnMakeTrip;
	private EditText etGetDate;
	private String bearer;
	private String accessToken;

	public CreateTripFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_create_trip,
				container, false);

		etFromCity = (EditText) rootView.findViewById(R.id.et_from_city);
		etToCity = (EditText) rootView.findViewById(R.id.et_to_city);
		etAvailbleSeats = (EditText) rootView.findViewById(R.id.et_available_seats);
		btnMakeTrip = (Button) rootView.findViewById(R.id.btn_make_trip);
		etGetDate = (EditText) rootView.findViewById(R.id.et_get_date);

		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		accessToken = preferences.getString("access_token", "");
		//bearer = "Bearer QSCiV2Q0Wum7ODPcmTalbWWathoCvcwRpgnpMvsW5SBABYi3GmtOl6W7lXxT385kqcOd3AbLZ-8TnjiYU_mnWfAlqIdO47hdL3OnQHqPoPcWzCVh6Jsw5Gmif81YCuMab-IxWhsBcymjk75HDSW9Zz4G1N-Zq0uJL982Ve_2MSbT60OnIlMutigIWwukfT_3linpN8M3cO5djIF35BY-brEiEHeFiiL5vzyvbGQ9n19mMH__vMyjHoQKM2bBbJa9ZSsakV9rXKJWlIDN6E-b1BzYn1E_njIjtA8Dw1N2jNSxmdRg5AgYCVqIONfTCr_BY9EaIg8asdv8PnpvmVYavpsSizgSq0txjL8PYbaK_umPBT_4teLhyyJiBhUieW22PYyxKnbpzf--B2W4_VENLMYKTD_iqmdtQfhYHC41TT7cmu6kIRRs8bd6FZHc1fWPEXktGYIgxpc8a5gYANCInoDCL3p-K8tGe2mVfc1j58A";

		btnMakeTrip.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		CreateTripPost login = new CreateTripPost();

		String fromCity = String.valueOf(etFromCity.getText());
		String toCity = String.valueOf(etToCity.getText());
		Integer availableSeats = Integer.valueOf(String.valueOf(etAvailbleSeats
				.getText()));
		String dateOfDeparture = String.valueOf(etGetDate.getText());

		if (availableSeats < 1 || availableSeats > 254) {
			Toast.makeText(
					getActivity(),
					"Invalid number of available seats. Must be between 1 and 254",
					Toast.LENGTH_SHORT).show();
		} else {
			login.execute(fromCity, toCity, String.valueOf(availableSeats),
					dateOfDeparture);
		}
	}
	
	private class CreateTripPost extends AsyncTask<String, Void, HttpResponse> {

		@Override
		protected void onPostExecute(HttpResponse response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				Toast.makeText(getActivity(), "Trip created successfully!",
						Toast.LENGTH_SHORT).show();
				
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
					
					Log.d("D1", str.toString());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					JSONObject jsnobject = new JSONObject(str.toString());
					String id = jsnobject.getString("id");
					Log.d("D1", id);
					
					/*Intent i = new Intent(CreateTrip.this, JoinTrip.class);
					i.putExtra("tripId", id);
					startActivity(i);*/
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			} else if (response.getStatusLine().getStatusCode() == 400) {
				Toast.makeText(getActivity(),
						"Invalid input data!",
						Toast.LENGTH_SHORT).show();
			}
			
			Log.d("D1", "seinttt");
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
					"http://spa2014.bgcoder.com/api/trips");

			httppost.setHeader("Authorization", "Bearer " + accessToken);
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
				Log.d("D1", e.toString());
			}

			return null;
		}
	}

}
