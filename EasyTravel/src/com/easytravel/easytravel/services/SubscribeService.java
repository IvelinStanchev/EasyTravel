package com.easytravel.easytravel.services;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.easytravel.easytravel.HomeActivity;
import com.easytravel.easytravel.InternetConnection;
import com.easytravel.easytravel.R;
import com.easytravel.easytravel.models.User;
import com.easytravel.easytravel.servicetask.CheckForSubscribedUsersChanges;
import com.easytravel.easytravel.sqlite.DBPref;

public class SubscribeService extends Service {

	public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds

	private String mDriverName;
	private Handler mHandler = new Handler();
	private Timer mTimer = null;
	private InternetConnection mInternetConnection;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mInternetConnection = new InternetConnection(getApplicationContext());
		
		return startId;
	};

	@Override
	public void onCreate() {
		mInternetConnection = new InternetConnection(getApplicationContext());
		
		if (mInternetConnection.isNetworkAvailable()) {
			if (mTimer != null) {
				mTimer.cancel();
			} else {
				mTimer = new Timer();
			}
			
			mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0,
					NOTIFY_INTERVAL);
		}
		
		
	}

	class TimeDisplayTimerTask extends TimerTask {

		@Override
		public void run() {

			mHandler.post(new Runnable() {

				@Override
				public void run() {

					new Thread(new Runnable() {
						public void run() {
							if (mInternetConnection.isNetworkAvailable()) {
								ArrayList<User> subscribedUsers = new ArrayList<User>();
								DBPref pref = new DBPref(
										getApplicationContext());
								Cursor c = pref.getValues();

								if (c.moveToFirst()) {
									do {
										String currentDriverName = c.getString(c
												.getColumnIndex("driver_name"));
										String currentDriverUpcomingTripsCount = c.getString(c
												.getColumnIndex("driver_upcoming_trips"));
										String currentDriverAllTripsCount = c.getString(c
												.getColumnIndex("driver_all_trips"));

										subscribedUsers
												.add(new User(
														currentDriverName,
														currentDriverUpcomingTripsCount,
														currentDriverAllTripsCount, null));
									} while (c.moveToNext());
								}
								c.close();
								pref.close();

								if (subscribedUsers != null
										&& subscribedUsers.size() > 0) {
									SharedPreferences preferences = PreferenceManager
											.getDefaultSharedPreferences(getApplicationContext());

									String accessToken = preferences.getString(
											"access_token", "");

									if (accessToken != null
											&& accessToken != "") {
										for (int i = 0; i < subscribedUsers
												.size(); i++) {
											try {
												mDriverName = subscribedUsers
														.get(i).getDriverName();
												String lastAllTripsCount = subscribedUsers
														.get(i)
														.getAllTripsCount();
												ArrayList<String> result = new CheckForSubscribedUsersChanges()
														.execute(
																subscribedUsers
																		.get(i)
																		.getDriverName(),
																accessToken)
														.get();
												String currentUpcomingTripsCount = result
														.get(0);
												String currentAllTripsCount = result
														.get(1);

												if (!lastAllTripsCount
														.equals(currentAllTripsCount)) {

													runOnUiThread(new Runnable() {

														@SuppressLint("NewApi")
														@Override
														public void run() {
															Uri soundUri = RingtoneManager
																	.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

															Intent intent = new Intent(
																	getApplicationContext(),
																	HomeActivity.class);

															intent.putExtra(
																	"service",
																	true);
															intent.putExtra(
																	"driverName",
																	mDriverName);

															PendingIntent pIntent = PendingIntent
																	.getActivity(
																			getApplicationContext(),
																			0,
																			intent,
																			0);

															Notification mNotification = new Notification.Builder(
																	getApplicationContext())

																	.setContentTitle(
																			"New Trip!")

																	.setContentText(
																			mDriverName
																					+ " will start a new trip!")

																	.setContentIntent(
																			pIntent)

																	.setSmallIcon(
																			R.drawable.ic_car)

																	.setSound(
																			soundUri)

																	.build();

															NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

															mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

															notificationManager
																	.notify(0,
																			mNotification);

														}
													});

													// Updating the database
													DBPref subscribedUsersDb = new DBPref(
															getApplicationContext());
													subscribedUsersDb
															.updateRecord(
																	i + 1,
																	subscribedUsers
																			.get(i)
																			.getDriverName(),
																	currentUpcomingTripsCount,
																	currentAllTripsCount);

													subscribedUsersDb.close();
												}
											} catch (InterruptedException e) {
												e.printStackTrace();
											} catch (ExecutionException e) {
												e.printStackTrace();
											}
										}
									}
								}
							}
						}
					}).start();
				}
			});
		}

		private void runOnUiThread(Runnable runnable) {
			mHandler.post(runnable);
		}

	}
}