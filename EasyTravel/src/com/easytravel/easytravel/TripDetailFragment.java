package com.easytravel.easytravel;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("NewApi")
public class TripDetailFragment extends Fragment {

	private TextView mTripDetail;
	private InternetConnection mInternetConnection;

	public TripDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_trip_detail,
				container, false);

		mInternetConnection = new InternetConnection(getActivity());

		if (!mInternetConnection.isNetworkAvailable()) {
			Fragment fragment = new NoInternetConnectionFragment(
					new TripDetailFragment());
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			mTripDetail = (TextView) rootView.findViewById(R.id.tv_tripDetail);

			Bundle bundle = this.getArguments();
			String tripInfo = bundle.getString("tripInfo");

			mTripDetail.setText(tripInfo);
		}

		return rootView;
	}
}
