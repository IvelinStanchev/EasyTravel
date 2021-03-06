package com.easytravel.easytravel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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

import com.easytravel.easytravel.progressactivities.LoggingInActivity;
import com.easytravel.easytravel.services.SubscribeService;

public class LoginActivity extends Activity implements OnClickListener {

	private static final String NO_INTERNET_CONNECTION_MESSAGE = "No Internet Connection!";
	private static final String LOGIN_URL = "http://spa2014.bgcoder.com/api/users/login";
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_BAD_REQUEST = 400;
	private static final String SUCCESSFULLY_LOGGED_IN_MESSAGE = "Successfully logged in!";
	private static final String ERROR_MESSAGE = "Wrong email or password!";

	private TextView mRegister;
	private EditText mEmail;
	private EditText mPassword;
	private Button mLogin;
	private String mAccessToken;
	private String mExpirationDate;
	private InternetConnection mInternetConnection;
	private static Map<String, Integer> sMonthsValues = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_form);

		mRegister = (TextView) findViewById(R.id.tv_registerLogin);
		mEmail = (EditText) findViewById(R.id.et_emailLogin);
		mPassword = (EditText) findViewById(R.id.et_passwordLogin);
		mLogin = (Button) findViewById(R.id.btn_login);

		mLogin.setOnClickListener(this);

		mInternetConnection = new InternetConnection(this);

		if (mInternetConnection.isNetworkAvailable()) {
			if (getIntent().getBooleanExtra("EXIT", false)) {
				System.exit(0);
				finish();
			}

			if (!getIntent().getBooleanExtra("Logout", false)) {
				initilizeMonths();

				if (!isAccessTokenExpired()) {
					if (mAccessToken == null || mExpirationDate == null) {

						SharedPreferences preferences = PreferenceManager
								.getDefaultSharedPreferences(this);

						mAccessToken = preferences
								.getString("access_token", "");
					}

					if (!mAccessToken.equalsIgnoreCase("")) {

						Intent i = new Intent(LoginActivity.this,
								HomeActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("access_token", mAccessToken);
						startActivity(i);
					}
				}

				if (getIntent().getStringExtra("email") != null
						&& getIntent().getStringExtra("password") != null) {
					mEmail.setText(getIntent().getStringExtra("email"));
					mPassword.setText(getIntent().getStringExtra("password"));
				}
			}

		} else {
			Toast.makeText(LoginActivity.this, NO_INTERNET_CONNECTION_MESSAGE,
					Toast.LENGTH_LONG).show();
		}
	}

	private void initilizeMonths() {
		sMonthsValues.put("Jan", 1);
		sMonthsValues.put("Feb", 2);
		sMonthsValues.put("Mar", 3);
		sMonthsValues.put("Apr", 4);
		sMonthsValues.put("May", 5);
		sMonthsValues.put("Jun", 6);
		sMonthsValues.put("Jul", 7);
		sMonthsValues.put("Aug", 8);
		sMonthsValues.put("Sep", 9);
		sMonthsValues.put("Oct", 10);
		sMonthsValues.put("Nov", 11);
		sMonthsValues.put("Dec", 12);
	}

	private boolean isAccessTokenExpired() {
		boolean isExpired = false;

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		mAccessToken = preferences.getString("access_token", "");
		mExpirationDate = preferences.getString(".expires", "");
		if (!mAccessToken.equalsIgnoreCase("")
				&& !mExpirationDate.equalsIgnoreCase("")) {
			Calendar calendar = Calendar.getInstance();

			int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int currentMonth = calendar.get(Calendar.MONTH) + 1;
			int currentYear = calendar.get(Calendar.YEAR);

			String[] expirationDateAsArray = mExpirationDate.split(" ");

			int expirationDayOfMonth = Integer
					.parseInt(expirationDateAsArray[1].trim());
			int expirationMonth = sMonthsValues.get(expirationDateAsArray[2]
					.trim());
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
			if (mInternetConnection.isNetworkAvailable()) {
				Intent i = new Intent(LoginActivity.this,
						LoggingInActivity.class);
				startActivity(i);

				new Login().execute(String.valueOf(mEmail.getText()),
						String.valueOf(mPassword.getText()));
			} else {
				Toast.makeText(LoginActivity.this,
						NO_INTERNET_CONNECTION_MESSAGE, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	private class Login extends AsyncTask<String, Void, HttpResponse> {

		protected HttpResponse doInBackground(String... params) {
			String email = params[0];
			String password = params[1];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(LOGIN_URL);
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
			super.onPostExecute(response);

			String responseFinal = "";
			String accessToken = null;
			String expirationDate = null;

			HttpEntity entity = response.getEntity();

			try {
				responseFinal = EntityUtils.toString(entity);
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
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

			if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK
					&& accessToken != null) {
				Toast.makeText(LoginActivity.this, SUCCESSFULLY_LOGGED_IN_MESSAGE,
						Toast.LENGTH_SHORT).show();

				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(LoginActivity.this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("access_token", accessToken);
				editor.putString(".expires", expirationDate);
				editor.apply();

				Intent i = new Intent(LoginActivity.this, HomeActivity.class);
				i.putExtra("access_token", accessToken);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);

			} else if (response.getStatusLine().getStatusCode() == STATUS_CODE_BAD_REQUEST
					|| accessToken == null) {
				Toast.makeText(LoginActivity.this, ERROR_MESSAGE,
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
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
