package com.easytravel.easytravel;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.easytravel.easytravel.adapters.NavDrawerListAdapter;
import com.easytravel.easytravel.models.NavDrawerItem;
import com.easytravel.easytravel.progressactivities.LoggingOutActivity;
import com.easytravel.easytravel.services.SubscribeService;

public class HomeActivity extends Activity {

	private static final String SUCCESSFULLY_LOGGED_OUT_MESSAGE = "Successfully logged out!";
	private static final String ERROR_LOGGING_OUT_MESSAGE = "An error occurred while logging out!";
	private static final String LOGOUT_URL = "http://spa2014.bgcoder.com/api/users/logout";
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_BAD_REQUEST = 400;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mNavMenuTitles;
	private TypedArray mNavMenuIcons;
	private ArrayList<NavDrawerItem> mNavDrawerItems;
	private NavDrawerListAdapter mAdapter;
	private String mAccessToken;
	private boolean mHasAccessToken;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		if (getIntent().getStringExtra("access_token") != null) {
			mHasAccessToken = true;
			mAccessToken = getIntent().getStringExtra("access_token");
		}

		startService(new Intent(this, SubscribeService.class));

		mTitle = mDrawerTitle = getTitle();

		mNavMenuTitles = getResources()
				.getStringArray(R.array.nav_drawer_items);

		mNavMenuIcons = getResources().obtainTypedArray(
				R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mNavDrawerItems = new ArrayList<NavDrawerItem>();

		// Home
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[0], mNavMenuIcons
				.getResourceId(0, -1)));
		// Create Trip
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[1], mNavMenuIcons
				.getResourceId(1, -1)));
		// Find Trips
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[2], mNavMenuIcons
				.getResourceId(2, -1)));
		// Filter Trips
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[3], mNavMenuIcons
				.getResourceId(3, -1)));
		// Find People
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[4], mNavMenuIcons
				.getResourceId(4, -1)));
		// Subscribed Drivers
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[5], mNavMenuIcons
				.getResourceId(5, -1)));
		// Logout
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[6], mNavMenuIcons
				.getResourceId(6, -1)));

		mNavMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		mAdapter = new NavDrawerListAdapter(getApplicationContext(),
				mNavDrawerItems);
		mDrawerList.setAdapter(mAdapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			displayView(0);
		}

		if (getIntent().getBooleanExtra("service", false)) {
			String driverName = getIntent().getStringExtra("driverName");
			Fragment fragment = new SubscribeDriverDetails(driverName);

			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			}
		}

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
								Intent i = new Intent(getApplicationContext(),
										LoginActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.putExtra("EXIT", true);
								startActivity(i);
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

	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@SuppressLint("NewApi")
	private void displayView(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment(false);
			break;
		case 1:
			fragment = new CreateTripFragment();
			break;
		case 2:
			fragment = new HomeFragment(true);
			break;
		case 3:
			fragment = new FilterTripsFragment();
			break;
		case 4:
			fragment = new FindPeopleFragment();
			break;
		case 5:
			fragment = new SubscribedUsersFragment();
			break;
		case 6:
			askForLoggingOut();
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(mNavMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@SuppressLint("NewApi")
	private void logoutUser() {
		Intent i = new Intent(HomeActivity.this, LoggingOutActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);

		new Logout().execute(mAccessToken);
	}

	private class Logout extends AsyncTask<String, Void, HttpResponse> {

		protected HttpResponse doInBackground(String... params) {
			String accessTokenLogout = params[0];

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(LOGOUT_URL);
			httppost.addHeader("Authorization", "Bearer " + accessTokenLogout);

			try {
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

			if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
				Toast.makeText(HomeActivity.this,
						SUCCESSFULLY_LOGGED_OUT_MESSAGE, Toast.LENGTH_SHORT)
						.show();

				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(HomeActivity.this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("access_token", null);
				editor.putString(".expires", null);
				editor.apply();

				Intent i = new Intent(HomeActivity.this, LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("Logout", true);
				startActivity(i);

			} else if (response.getStatusLine().getStatusCode() == STATUS_CODE_BAD_REQUEST) {
				Toast.makeText(HomeActivity.this, ERROR_LOGGING_OUT_MESSAGE,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void askForLoggingOut() {
		new AlertDialog.Builder(this)
				.setTitle("Logout")
				.setMessage("Are you sure you want to logout?")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								logoutUser();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							@SuppressLint("NewApi")
							public void onClick(DialogInterface dialog,
									int which) {
								Fragment fragment = new HomeFragment(false);

								if (fragment != null) {
									FragmentManager fragmentManager = getFragmentManager();
									fragmentManager
											.beginTransaction()
											.replace(R.id.frame_container,
													fragment).commit();

									mDrawerList.setItemChecked(0, true);
									mDrawerList.setSelection(0);
									setTitle(mNavMenuTitles[0]);
									mDrawerLayout.closeDrawer(mDrawerList);
								}
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}
