package com.easytravel.easytravel;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class SwipeGestureListener extends SimpleOnGestureListener implements
		OnTouchListener {
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private GestureDetector mGDetector;
	private ListView mListView;

	public SwipeGestureListener() {
		super();
	}

	public SwipeGestureListener(Context context, ListView listView) {
		this(context, null, listView);
	}

	public SwipeGestureListener(Context context, GestureDetector gDetector,
			ListView listView) {
		this.mListView = listView;
		if (gDetector == null)
			gDetector = new GestureDetector(context, this);

		this.mGDetector = gDetector;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		final int position = mListView.pointToPosition(Math.round(e1.getX()),
				Math.round(e1.getY()));

		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
			if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
					|| Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
				return false;
			}
		} else {
			if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
				return false;
			}
		}

		return super.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return mGDetector.onTouchEvent(event);
	}

	public GestureDetector getDetector() {
		return mGDetector;
	}

}
