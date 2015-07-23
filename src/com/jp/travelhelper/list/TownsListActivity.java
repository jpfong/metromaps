package com.jp.travelhelper.list;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.jp.travelhelper.Constants;
import com.jp.travelhelper.MetroMapsApplication;
import com.jp.travelhelper.R;
import com.jp.travelhelper.ShowMapActivity;
import com.jp.travelhelper.dbhelper.DatabaseHelper;
import com.jp.travelhelper.dbhelper.model.Town;
import com.jp.travelhelper.rater.AppRater;

/**
 * Main screen of the application.
 * 
 * @author jp
 * 
 */
public class TownsListActivity extends ExpandableListActivity {

	private DatabaseHelper databaseHelper;

	private List<String> townsAutocompleteList;

	private SimpleCursorTreeAdapter mAdapter;

	private InterstitialAd exitInterstitial;

	private InterstitialAd openInterstitial;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.towns_list);

		BugSenseHandler.initAndStartSession(this, "ab7941dd");

		// Create the interstitial
		openInterstitial = new InterstitialAd(this);
		openInterstitial.setAdUnitId("ca-app-pub-5041487124204822/1619325900");
		openInterstitial.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				if (openInterstitial.isLoaded()) {
					openInterstitial.show();
				}
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
			}
		});

		// Create ad request
		AdRequest adRequestInterstitial = new AdRequest.Builder().build();

		openInterstitial.loadAd(adRequestInterstitial);

		exitInterstitial = new InterstitialAd(this);
		exitInterstitial.setAdUnitId("ca-app-pub-5041487124204822/1619325900");
		// Create ad request.
		adRequestInterstitial = new AdRequest.Builder().build();

		exitInterstitial.loadAd(adRequestInterstitial);

		// clear cache on open
		clearApplicationData();

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		if (Constants.BUILT_FOR != Constants.NONE) {
			// AppRater.showRateDialog(this, null);
			AppRater.appLaunched(this);
		}

		// final Resources res = getResources();

		// final AssetManager assetManager = getAssets();
		databaseHelper = new DatabaseHelper(this.getApplicationContext());

		// autocompleteview

		townsAutocompleteList = databaseHelper.getAllTownNames();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				townsAutocompleteList);
		final AutoCompleteTextView acTextView = (AutoCompleteTextView) findViewById(R.id.searchTownTextView);
		// the number of letter to type before seeing autocompleteView
		acTextView.setThreshold(1);
		acTextView.setAdapter(adapter);
		// the message used to indicate what the user can do with the textView
		acTextView.setHint(R.string.mess_search_hint);
		acTextView.setSingleLine(true);

		final ImageButton imageButton = (ImageButton) findViewById(R.id.btnSearch);

		imageButton.requestFocus();

		imageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Editable text = acTextView.getEditableText();
				if (text.length() > 0) {
					String string = text.toString();

					// get the town
					Town townByName = databaseHelper.getTownByName(string);
					if (townByName == null) {
						Toast.makeText(TownsListActivity.this,
								R.string.mess_search_not_found,
								Toast.LENGTH_SHORT).show();
					} else {
						showMap(townByName);
					}

				}
			}
		});

		acTextView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// This is the filter, because onkey is called for key up and
				// down
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					return false;
				}
				// press enter
				if (keyCode == KeyEvent.KEYCODE_ENTER) {

					Editable text = acTextView.getEditableText();
					if (text.length() > 0) {

						// image button click is then performed
						imageButton.performClick();
						return true;
					}
				}
				return false;
			}
		});

		acTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				String itemAtPosition = (String) arg0.getItemAtPosition(arg2);

				// get the town
				Town townByName = databaseHelper.getTownByName(itemAtPosition);

				showMap(townByName);

			}
		});

		// expandableListView

		final Cursor countries = databaseHelper.getCountries();

		startManagingCursor(countries);

		final ExpandableListView browseView = (ExpandableListView) findViewById(android.R.id.list);

		// columns from the cursor used in the list
		String[] cursorColumns = new String[] { getString(R.string.country_name) };

		String[] childrenCursorColumns = new String[] { getString(R.string.town_name) };

		mAdapter = new SimpleCursorTreeAdapter(this, countries,
				R.layout.country_exp_row, R.layout.exprow, cursorColumns,
				new int[] { R.id.country_name_exp }, R.layout.exprow,
				R.layout.exprow, childrenCursorColumns,
				new int[] { R.id.twname }) {

			@Override
			public View newGroupView(Context context, Cursor cursor,
					boolean isExpanded, ViewGroup parent) {
				LayoutInflater inflater = LayoutInflater.from(context);
				View view = inflater.inflate(R.layout.country_exp_row, null);
				view.setTag(R.id.country_name_exp,
						view.findViewById(R.id.country_name_exp));
				return view;
			}

			@Override
			public View newChildView(Context context, Cursor cursor,
					boolean isLastChild, ViewGroup parent) {
				LayoutInflater inflater = LayoutInflater.from(context);
				View view = inflater.inflate(R.layout.exprow, null);
				view.setTag(R.id.twname, view.findViewById(R.id.twname));

				CheckBox checkBox = (CheckBox) view.findViewById(R.id.btn_star);

				view.setTag(R.id.btn_star, checkBox);

				return view;
			}

			@Override
			protected void bindGroupView(View view, Context context,
					Cursor cursor, boolean isExpanded) {
				super.bindGroupView(view, context, cursor, isExpanded);

				TextView textView = (TextView) view
						.getTag(R.id.country_name_exp);

				// first columns of the columns listed above
				String countryName = cursor
						.getString(cursor
								.getColumnIndexOrThrow(getString(R.string.country_name)));

				textView.setText(countryName);

				String countryIcon = cursor.getString(cursor
						.getColumnIndex(BaseColumns._ID));

				try {

					InputStream istr = getAssets().open("icons/" + countryIcon);

					Drawable d = Drawable.createFromStream(istr, null);

					Bitmap b = ((BitmapDrawable) d).getBitmap();

					int height = 11;
					// height of favorites icon
					if ("0".equals(countryIcon)) {
						height = 16;
					}
					Bitmap bitmapOrig = Bitmap.createScaledBitmap(b, 16,
							height, false);

					BitmapDrawable bitmapDrawable = new BitmapDrawable(
							getResources(), bitmapOrig);

					textView.setCompoundDrawablesWithIntrinsicBounds(
							bitmapDrawable, null, null, null);

					istr.close();
				} catch (IOException e) {
				}
			}

			@Override
			protected Cursor getChildrenCursor(Cursor groupCursor) {

				int tempGroup = groupCursor.getInt(groupCursor
						.getColumnIndexOrThrow(BaseColumns._ID));

				Cursor townByCountry = null;

				if (tempGroup > 0) {
					townByCountry = databaseHelper.getTownsByCountry(tempGroup);

				} else {
					townByCountry = databaseHelper.getFavoritesTownCountry();
				}

				return townByCountry;

			}

			@Override
			protected void bindChildView(View view, Context context,
					Cursor cursor, boolean isLastChild) {
				super.bindChildView(view, context, cursor, isLastChild);

				TextView textView = (TextView) view.getTag(R.id.twname);

				final String townName = cursor.getString(cursor
						.getColumnIndexOrThrow(getString(R.string.town_name)));

				textView.setText(townName);

				final int favorite = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseHelper.TOWN_FAVORITE));

				final int townId = cursor.getInt(cursor
						.getColumnIndexOrThrow(BaseColumns._ID));

				CheckBox checkBox = (CheckBox) view.getTag(R.id.btn_star);
				checkBox.setChecked(favorite == 1);

				checkBox.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (favorite == 1) {
							databaseHelper.updateTownFavorite(0, townId);

							Toast.makeText(
									TownsListActivity.this,
									getString(
											R.string.mess_remove_bookmark_success,
											townName), Toast.LENGTH_SHORT)
									.show();

						} else {
							databaseHelper.updateTownFavorite(1, townId);

							Toast.makeText(
									TownsListActivity.this,
									getString(
											R.string.mess_add_bookmark_success,
											townName), Toast.LENGTH_SHORT)
									.show();
						}

						Cursor townCountry = databaseHelper.getCountries();

						//
						Cursor currentCursor = mAdapter.getCursor(); // ***
																		// adding
																		// these
																		// lines
						stopManagingCursor(currentCursor); // *** solved the
															// problem
						startManagingCursor(townCountry);
						//

						mAdapter.changeCursor(townCountry);

						mAdapter.notifyDataSetChanged();

						browseView.refreshDrawableState();

					}
				});

			}

		};

		browseView.setAdapter(mAdapter);

		getExpandableListView().setOnChildClickListener(
				new OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {

						// get item by its id
						Town town = databaseHelper.getTownById((int) id);

						// To launch your own activity
						// First declare an intent
						showMap(town);

						return true;
					}

				});

		getExpandableListView().expandGroup(0);

	}

	private void showMap(Town town) {
		final Intent intent = new Intent(TownsListActivity.this,
				ShowMapActivity.class);

		intent.putExtra("town", town.getTownName());
		intent.putExtra("id", town.getId());

		startActivity(intent);
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (exitInterstitial.isLoaded()) {
			exitInterstitial.show();
		}
	}

	/**
	 * On destroy, we close the database and destroy tmp files.
	 */
	protected void onDestroy() {
		super.onDestroy();
		databaseHelper.close();
		clearApplicationData();
	}

	public void clearApplicationData() {
		File cache = getExternalCacheDir();
		if (cache != null) {
			File appDir = new File(cache.getParent());
			if (appDir != null) {
				if (appDir.exists()) {
					String[] children = appDir.list();
					for (String s : children) {
						if (!s.equals("lib")) {
							deleteDir(new File(appDir, s));
						}
					}
				}
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	/**
	 * Initiating Menu XML file (menu.xml)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_more_apps, menu);
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
				// TODO: handle exception
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

		case R.id.menu_more:

			try {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://search?q=pub:\"jpeg\""));
				startActivity(intent);
			} catch (Exception e) {
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

}