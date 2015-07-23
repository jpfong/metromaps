package com.jp.travelhelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Activity where the metro map is shown.
 * 
 * @author jp
 * 
 */
public class ShowMapActivity extends Activity {

	/**
	 * the id of the map
	 */
	private int id;

	/**
	 * name of the town
	 */
	private String town;

	/**
	 * The link to the assets
	 */
	private static final String FILE_ANDROID_ASSET = "file:///android_asset/";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.show_map_activity);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		// getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
		// R.drawable.icon_title);

		// get data of the activity
		Bundle bundle = this.getIntent().getExtras();

		// get the image name
		id = bundle.getInt("id");

		// activity title
		town = bundle.getString("town");
		// setTitle(getString(R.string.app_name) + " - " + town);
		setTitle(town);

		// the view to display the map that user can mainpulate as he wants
		WebView webView = (WebView) findViewById(R.id.webkit);

		// allows user to zoom in/out the map
		webView.getSettings().setBuiltInZoomControls(true);

		// if (android.os.Build.VERSION.SDK_INT >= 7) {
		webView.getSettings().setLoadWithOverviewMode(true);
		// }

		webView.getSettings().setUseWideViewPort(true);

		String data = "<body>" + "<img src=\"" + id + "\"/></body>";

		webView.loadDataWithBaseURL(FILE_ANDROID_ASSET, data, "text/html",
				"utf-8", null);

	}

	/**
	 * Initiating Menu XML file (menu.xml)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_website:
			String uri3 = Constants.WEBSITE_URL;

			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri3)));
			} catch (Exception e) {
			}

			return true;

		case R.id.menu_get_maps:

			String uri2 = "https://gumroad.com/l/zQAw";

			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri2)));
			} catch (Exception e) {
			}

			return true;

		case R.id.menu_pro:
			String uri = Constants.GOOGLE_PLAY_URL;
			if (Constants.BUILT_FOR == Constants.SAMSUNG) {
				uri = Constants.SAMSUNG_APPS_URL;
			} else {
				if (Constants.BUILT_FOR == Constants.AMAZON) {
					uri = Constants.AMAZON_APPS_URL;
				}
			}

			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri
						+ Constants.PAID_APP_PNAME)));
			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;

		case R.id.menu_share:
			shareMap();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	/**
	 * Share a map.
	 */
	private void shareMap() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			try {
				File externalCacheDir = getExternalCacheDir();
				externalCacheDir.mkdirs();

				InputStream is = getAssets().open(String.valueOf(id));
				OutputStream os;
				// file in the cache
				String string = externalCacheDir.getAbsolutePath()
						+ File.separator + town + ".png";
				os = new FileOutputStream(string);

				BufferedInputStream bis = new BufferedInputStream(is);
				BufferedOutputStream bos = new BufferedOutputStream(os);
				byte[] buf = new byte[1024];

				int n = 0;
				int o = 0;
				while ((n = bis.read(buf, o, buf.length)) != -1) {
					bos.write(buf, 0, n);
				}

				bis.close();
				bos.close();

				// get the file for sharing
				File imageFileToShare = new File(string);

				ContentValues values = new ContentValues(2);
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				values.put(MediaStore.Images.Media.DATA,
						imageFileToShare.getAbsolutePath());
				Uri uri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				Intent theIntent = new Intent(Intent.ACTION_SEND);
				theIntent.setType("image/png");
				theIntent.putExtra(Intent.EXTRA_STREAM, uri);
				// town's name in subject
				theIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, town);
				theIntent.putExtra(
						android.content.Intent.EXTRA_TEXT,
						getString(R.string.share_body,
								getString(R.string.app_name)));

				startActivity(Intent.createChooser(theIntent,
						getString(R.string.share_via)));

			} catch (FileNotFoundException e) {
				// Toast.makeText(this, getString(R.string.no_sd_card),
				// Toast.LENGTH_LONG).show();
				showAlertNoSdCard();
			} catch (IOException e) {
				// Toast.makeText(this, getString(R.string.no_sd_card),
				// Toast.LENGTH_LONG).show();
				showAlertNoSdCard();
			}
		} else {
			// Toast.makeText(this, getString(R.string.no_sd_card),
			// Toast.LENGTH_LONG).show();
			showAlertNoSdCard();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		// Google analytics
		((MetroMapsApplication) getApplication()).getTracker();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		// Google analytics
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	private void showAlertNoSdCard() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle(getString(R.string.error));
		alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

		// set dialog message
		alertDialogBuilder
				.setMessage(getString(R.string.no_sd_card))
				.setCancelable(false)
				.setPositiveButton(getString(android.R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});

		alertDialogBuilder.show();
	}

}
