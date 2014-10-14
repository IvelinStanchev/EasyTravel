package com.easytravel.easytravel;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class SwipeGestureListener extends SimpleOnGestureListener implements OnTouchListener {
	Context context;
	GestureDetector gDetector;
	static final int SWIPE_MIN_DISTANCE = 20;
	static final int SWIPE_MAX_OFF_PATH = 250;
	static final int SWIPE_THRESHOLD_VELOCITY = 200;
	ListView listView;

	public SwipeGestureListener() {
		super();
	}

	public SwipeGestureListener(Context context, ListView listView) {
		this(context, null, listView);
	}

	public SwipeGestureListener(Context context, GestureDetector gDetector, ListView listView) {
		this.listView = listView;
		if (gDetector == null)
			gDetector = new GestureDetector(context, this);

		this.context = context;
		this.gDetector = gDetector;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		final int position = listView.pointToPosition(Math.round(e1.getX()),
				Math.round(e1.getY()));

		// String currentTrip = listView.getItemAtPosition(position);
		// Get the current trip info and make a toast

		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
			if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
					|| Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
				return false;
			}
			/*
			 * if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
			 * Toast.makeText(getActivity(), "bottomToTop" + currentTrip,
			 * Toast.LENGTH_SHORT).show(); } else if (e2.getY() - e1.getY() >
			 * SWIPE_MIN_DISTANCE) { Toast.makeText(getActivity(),
			 * "topToBottom  " + currentTrip, Toast.LENGTH_SHORT) .show(); }
			 */
		} else {
			if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
				return false;
			}
			/*
			 * if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
			 * Toast.makeText(getActivity(), "swipe RightToLeft " + currentTrip,
			 * 5000).show(); } else if (e2.getX() - e1.getX() >
			 * SWIPE_MIN_DISTANCE) { Toast.makeText(getActivity(),
			 * "swipe LeftToright  " + currentTrip, 5000).show(); }
			 */
		}

		return super.onFling(e1, e2, velocityX, velocityY);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return gDetector.onTouchEvent(event);
	}

	public GestureDetector getDetector() {
		return gDetector;
	}

}
