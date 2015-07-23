package com.jp.travelhelper.rater;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jp.travelhelper.Constants;
import com.jp.travelhelper.R;

public class AppRater {

	private static final String GOOGLE_PLAY_URL = "market://details?id=";

	private static final String SAMSUNG_APPS_URL = "samsungapps://ProductDetail/";

	private static final String AMAZON_APPS_URL = "http://www.amazon.com/gp/mas/dl/android?p=";

	private static final String APP_PNAME = "com.jp.travelhelper";

	private final static int DAYS_UNTIL_PROMPT = 3;
	private final static int LAUNCHES_UNTIL_PROMPT = 7;

	public static void appLaunched(Context context) {

		SharedPreferences prefs = context.getSharedPreferences("apprater", 0);
		if (prefs.getBoolean("dontshowagain", false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch
					+ (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				showRateDialog(context, editor);
			}
		}

		editor.commit();
	}

	public static void showRateDialog(final Context mContext,
			final SharedPreferences.Editor editor) {
		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.apprater);

		String appName = mContext.getString(R.string.app_name);
		dialog.setTitle(mContext.getString(R.string.rate, appName));

		TextView tv = (TextView) dialog.findViewById(R.id.rate_text);

		tv.setText(mContext.getString(R.string.rate_message, appName));

		Button b1 = (Button) dialog.findViewById(R.id.btn_rate_yes);

		b1.setText(mContext.getString(R.string.rate, appName));

		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String uri = GOOGLE_PLAY_URL;
				if (Constants.BUILT_FOR == Constants.SAMSUNG) {
					uri = SAMSUNG_APPS_URL;
				} else {
					if (Constants.BUILT_FOR == Constants.AMAZON) {
						uri = AMAZON_APPS_URL;
					}
				}

				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(uri + APP_PNAME)));
				dialog.dismiss();
			}
		});

		Button b2 = (Button) dialog.findViewById(R.id.btn_rate_later);

		b2.setText(mContext.getString(R.string.rate_later));
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button b3 = (Button) dialog.findViewById(R.id.btn_rate_no);
		b3.setText(mContext.getString(R.string.rate_no));
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}