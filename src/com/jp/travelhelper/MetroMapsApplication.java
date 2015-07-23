package com.jp.travelhelper;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MetroMapsApplication extends Application {

	private Tracker mTracker = null;

	// The following line should be changed to include the correct property id.
	private static final String MY_FLURRY_APIKEY = "GM8ZY7JWZ5PXQSP4B49Z";

	synchronized public Tracker getTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			mTracker = analytics.newTracker(R.xml.analytics);
		}

		return mTracker;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// configure Flurry
		// FlurryAgent.setLogEnabled(false);
		// init Flurry
		// FlurryAgent.init(this, MY_FLURRY_APIKEY);
	}

}
