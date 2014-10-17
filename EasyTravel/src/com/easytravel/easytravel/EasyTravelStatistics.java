package com.easytravel.easytravel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class EasyTravelStatistics extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new CustomView(this));
	}

	// Overriding the default view to get to the canvas and the onDraw() logic.
	@SuppressLint("DrawAllocation")
	private class CustomView extends View {

		private final int width;
		private final int height;
		private final int X_RIGHT;
		private final int X_LEFT;
		private final int Y_TOP;
		private final int Y_BOTTOM;
		private final float DENSITY;
		private final int DASH_WIDTH = 5;
		private final String[] statsLabels = new String[] { "Trips", "Ended",
				"Users", "Drivers" };
		private String mTrips = "";
		private String mFinishedTrips = "";
		private String mUsers = "";
		private String mDrivers = "";

		private Paint paint;
		private Path path;

		public CustomView(Context context) {
			super(context);
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			mTrips = preferences.getString("stats_trips", null);
			mFinishedTrips = preferences
					.getString("stats_finished_trips", null);
			mUsers = preferences.getString("stats_users", null);
			mDrivers = preferences.getString("stats_drivers", null);

			DisplayMetrics metrics = getBaseContext().getResources()
					.getDisplayMetrics();
			width = metrics.widthPixels;
			height = metrics.heightPixels;

			DENSITY = getResources().getDisplayMetrics().density;
			X_LEFT = (int) (DENSITY * 50);
			X_RIGHT = (width - (int) (DENSITY * 10));
			Y_TOP = (int) (DENSITY * 70);
			Y_BOTTOM = (height - (int) (DENSITY * 150));
			this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.save(Canvas.MATRIX_SAVE_FLAG);

			canvas.drawColor(Color.rgb(130, 150, 200));

			Typeface tf;
			drawHeading(canvas);

			tf = Typeface.create("Helvetica", Typeface.BOLD);
			this.paint.setTypeface(tf);
			this.paint.setAntiAlias(false);
			this.paint.setShadowLayer(0, 0, 0, 0);
			this.paint.setColor(Color.BLACK);
			drawXandYaxis(canvas);

			drawYAxisWithParameters(canvas);

			drawChart(canvas);

			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			canvas.drawBitmap(bitmap, toDP(270), toDP(16), this.paint);

			canvas.restore();
		}

		private void drawHeading(Canvas canvas) {
			this.paint.setColor(Color.WHITE);
			this.paint.setAntiAlias(true);
			this.paint.setStyle(Paint.Style.FILL);
			this.paint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
			this.paint.setStrokeWidth(5);

			this.paint.setTextSize(toDP(22));

			Typeface tf = Typeface.create("Georgia", Typeface.BOLD);
			this.paint.setTypeface(tf);
			canvas.drawText("EASY TRAVEL", toDP(5), toDP(26), this.paint);
			canvas.drawText("STATS CHART", toDP(35), toDP(53), this.paint);
		}

		private void drawChart(Canvas canvas) {
			int xPartForLabels = (X_RIGHT - X_LEFT) / 4;
			int xPartForGraphBorders = xPartForLabels / 5;

			this.paint.setTextSize(toDP(19));

			final float[] tripsData = new float[] {
					(float) Integer.valueOf(mTrips),
					(float) Integer.valueOf(mFinishedTrips),
					(float) Integer.valueOf(mUsers),
					(float) Integer.valueOf(mDrivers) };

			for (int i = 1; i <= 4; i++) {
				this.paint.setColor(Color.BLACK);
				canvas.drawLine(X_LEFT + (i * xPartForLabels), Y_BOTTOM
						+ toDP(15), X_LEFT + (i * xPartForLabels), Y_BOTTOM
						- toDP(10), this.paint);

				this.path = new Path();

				int currentXPoint = X_LEFT + (xPartForLabels * (i - 1));

				float ratio = tripsData[i - 1] / 2000;
				float currentYTopPoint = Y_BOTTOM
						- ((Y_BOTTOM - Y_TOP) * ratio);
				Log.d("d5",
						String.valueOf(currentYTopPoint + " top-" + Y_TOP
								+ " bottom-" + Y_BOTTOM + " ratio-" + ratio));

				this.path
						.moveTo(currentXPoint + xPartForGraphBorders, Y_BOTTOM);
				this.path.lineTo(currentXPoint + xPartForGraphBorders,
						currentYTopPoint);
				this.path.lineTo((currentXPoint + xPartForLabels)
						- xPartForGraphBorders, currentYTopPoint);
				this.path.lineTo((currentXPoint + xPartForLabels)
						- xPartForGraphBorders, Y_BOTTOM);

				this.path.close();
				this.paint.setColor(Color.RED);
				canvas.drawPath(this.path, this.paint);

				this.paint.setColor(Color.rgb(0, 128, 128));
				canvas.drawText(String.valueOf((int) tripsData[i - 1]),
						currentXPoint + xPartForGraphBorders, currentYTopPoint
								- toDP(10), this.paint);

				this.paint.setColor(Color.rgb(0, 0, 128));
				canvas.drawText(statsLabels[i - 1], X_LEFT + toDP(10)
						+ ((i - 1) * xPartForLabels), Y_BOTTOM + toDP(35),
						this.paint);
			}
		}

		private void drawYAxisWithParameters(Canvas canvas) {
			int yPart = (Y_BOTTOM - Y_TOP) / 20;
			this.paint.setTextSize(toDP(15));
			for (int i = 1; i <= 20; i++) {
				int currentDashWidth = DASH_WIDTH;
				if (i % 5 == 0) {
					currentDashWidth *= 2;
					canvas.drawText(String.valueOf(i * 100), 0, Y_BOTTOM
							- (i * yPart), this.paint);
				}
				canvas.drawLine(X_LEFT - toDP(currentDashWidth), Y_BOTTOM
						- (i * yPart), X_LEFT + toDP(currentDashWidth),
						Y_BOTTOM - (i * yPart), this.paint);
			}
		}

		private void drawXandYaxis(Canvas canvas) {
			canvas.drawLine(X_LEFT, Y_TOP, X_LEFT, Y_BOTTOM, this.paint);
			canvas.drawLine(X_LEFT, Y_BOTTOM, X_RIGHT, Y_BOTTOM, this.paint);
		}

		private int toDP(int px) {
			return (int) (this.DENSITY * px);
		}

	}
}
