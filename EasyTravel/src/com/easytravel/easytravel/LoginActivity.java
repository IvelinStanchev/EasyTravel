package com.easytravel.easytravel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private TextView register;
	private EditText email;
	private EditText password;
	private Button login;
	private ProgressBar progressBar;
	private String accessToken;
	private String expirationDate;

	private static Map<String, Integer> map = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_form);

		register = (TextView) findViewById(R.id.tv_registerLogin);
		email = (EditText) findViewById(R.id.et_emailLogin);
		password = (EditText) findViewById(R.id.et_passwordLogin);
		login = (Button) findViewById(R.id.btn_login);
		
		login.setOnClickListener(this);
		
		if (isNetworkAvailable()) {
			if (getIntent().getBooleanExtra("EXIT", false)) {
				System.exit(0);
				finish();
			}

			if (!getIntent().getBooleanExtra("Logout", false)) {
				initilizeMonths();

				if (!isAccessTokenExpired()) {
					if (accessToken == null || expirationDate == null) {

						SharedPreferences preferences = PreferenceManager
								.getDefaultSharedPreferences(this);

						accessToken = preferences.getString("access_token", "");
					}

					if (!accessToken.equalsIgnoreCase("")) {

						Intent i = new Intent(LoginActivity.this,
								HomeActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("access_token", accessToken);
						startActivity(i);
					}
				}

				if (getIntent().getStringExtra("email") != null
						&& getIntent().getStringExtra("password") != null) {
					email.setText(getIntent().getStringExtra("email"));
					password.setText(getIntent().getStringExtra("password"));
				}
			}
			
		}
		else{
			Toast.makeText(LoginActivity.this, "No Internet Connection!",
					Toast.LENGTH_LONG).show();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void initilizeMonths() {
		map.put("Jan", 1);
		map.put("Feb", 2);
		map.put("Mar", 3);
		map.put("Apr", 4);
		map.put("May", 5);
		map.put("Jun", 6);
		map.put("Jul", 7);
		map.put("Aug", 8);
		map.put("Sep", 9);
		map.put("Oct", 10);
		map.put("Nob", 11);
		map.put("DEc", 12);
	}

	private boolean isAccessTokenExpired() {
		boolean isExpired = false;

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		accessToken = preferences.getString("access_token", "");
		expirationDate = preferences.getString(".expires", "");
		if (!accessToken.equalsIgnoreCase("")
				&& !expirationDate.equalsIgnoreCase("")) {
			Calendar calendar = Calendar.getInstance();

			int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int currentMonth = calendar.get(Calendar.MONTH) + 1;
			int currentYear = calendar.get(Calendar.YEAR);

			String[] expirationDateAsArray = expirationDate.split(" ");

			int expirationDayOfMonth = Integer
					.parseInt(expirationDateAsArray[1].trim());
			int expirationMonth = map.get(expirationDateAsArray[2].trim());
			int expirationYear = Integer.parseInt(expirationDateAsArray[3]
					.trim());

			if (currentYear > expirationYear) {
				isExpired = true;
			} else if (currentMonth > expirationMonth) {
				isExpired = true;
			} else if (currentDayOfMonth > expirationDayOfMonth - 2) {
				isExpired = true;
			}
		} else {
			isExpired = true;
		}

		return isExpired;
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("Exit")
				.setMessage("Are you sure you want to exit?")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_login) {
			if (isNetworkAvailable()) {
				Intent i = new Intent(LoginActivity.this, LoggingInActivity.class);
				startActivity(i);

				Login login = new Login();
				login.execute(String.valueOf(email.getText()),
						String.valueOf(password.getText()));
			}
			else{
				Toast.makeText(LoginActivity.this, "No Internet Connection!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private class Login extends AsyncTask<String, Void, HttpResponse> {

		protected HttpResponse doInBackground(String... params) {
			String email = params[0];
			String password = params[1];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://spa2014.bgcoder.com/api/users/login");
			httppost.addHeader("Content-Type", "x-www-form-urlencoded");

			List<NameValuePair> pair = new ArrayList<NameValuePair>();

			pair.add(new BasicNameValuePair("grant_type", "password"));
			pair.add(new BasicNameValuePair("username", email));
			pair.add(new BasicNameValuePair("password", password));

			try {
				httppost.setEntity(new UrlEncodedFormEntity(pair));

				HttpResponse response = httpClient.execute(httppost);

				return response;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(HttpResponse response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);

			String responseFinal = "";
			String accessToken = null;
			String expirationDate = null;

			HttpEntity entity = response.getEntity();

			try {
				responseFinal = EntityUtils.toString(entity);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				JSONObject jsonObj = new JSONObject(responseFinal);

				accessToken = jsonObj.getString("access_token");
				expirationDate = jsonObj.getString(".expires");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (response.getStatusLine().getStatusCode() == 200
					&& accessToken != null) {
				Toast.makeText(LoginActivity.this, "Successfully logged in!",
						Toast.LENGTH_SHORT).show();

				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(LoginActivity.this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("access_token", accessToken);
				editor.putString(".expires", expirationDate);
				editor.apply();

				Intent i = new Intent(LoginActivity.this, HomeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);

			} else if (response.getStatusLine().getStatusCode() == 400
					|| accessToken == null) {
				Toast.makeText(LoginActivity.this, "Wrong email or password!",
						Toast.LENGTH_SHORT).show();

				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		}

	}

	public void moveToRegisterActivity(View v) {
		Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
