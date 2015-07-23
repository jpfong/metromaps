package com.jp.travelhelper.dbhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.jp.travelhelper.R;
import com.jp.travelhelper.dbhelper.model.Town;

/**
 * Subclass of the {@link SQLiteOpenHelper} that sets up the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	/** Order in chinese. */
	public static final String TOWN_ZH_ORDER = "zh_order";

	/** Name of the database. */
	public static final String DB_NAME = "MetroMapsDB";

	/** Name of the town table. */
	public static final String TOWN_TABLE = "town";

	/** Name of the town ID. */
	public static final String TOWN_ID = "town_id";

	/** Column's name of the town name. */
	public static final String TOWN_NAME = "name";

	/** Column's name of the favorite. */
	public static final String TOWN_FAVORITE = "favorite";

	/** Column's name of the town country id. */
	public static final String TOWN_COUNTRY_ID = "country_id";

	/** Column's name of the country table. */
	public static final String COUNTRY_TABLE = "country";

	/** Column's name of the country ID. */
	public static final String COUNTRY_ID = "country_id";

	/** Column's name of the country name. */
	public static final String COUNTRY_NAME = "country_name";

	/** Column's name of the country name. */
	public static final String COUNTRY_ORDER = "country_order";

	/** Column's name of the town latitude. */
	public static final String TOWN_LAT = "town_lat";

	/** Column's name of the town longitude. */
	public static final String TOWN_LONG = "town_long";

	/** The manager of the asset. */
	private AssetManager assetManager;

	/** Properties field for SQL queries. */
	private Properties properties;

	/** Ressources of the application */
	private Resources resources;

	/**
	 * Constructor of DatabaseHelper.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 53); // put the same number of the version
											// code

		this.assetManager = context.getAssets();
		this.resources = context.getResources();

		// open property file
		try {
			InputStream inputStream = assetManager
					.open("queries/queries.properties");
			properties = new Properties();
			properties.load(inputStream);
		} catch (IOException e) {
			System.err.println("Failed to open property file");
			e.printStackTrace();
		}
	}

	/**
	 * Get countries.
	 * 
	 * @return countries
	 */
	public Cursor getCountries() {
		SQLiteDatabase db = this.getWritableDatabase();

		StringBuilder buffer = new StringBuilder();

		buffer.append(properties.getProperty("getCountryNameQuery"));

		buffer.append(resources.getString(R.string.country_order));

		Cursor cursor = db.rawQuery(buffer.toString(), null);

		return cursor;
	}

	/**
	 * Get favorites towns with country.
	 * 
	 * @return favorites town.
	 */
	public Cursor getFavoritesTownCountry() {
		SQLiteDatabase db = this.getWritableDatabase();

		StringBuilder builder = new StringBuilder();

		builder.append(properties.getProperty("getFavTownQuery"));
		builder.append(resources.getString(R.string.town_order));

		Cursor cursor = db.rawQuery(builder.toString(), null);

		return cursor;
	}

	/**
	 * Actions performed on the creation of the application.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createDB(db);
	}

	/**
	 * Insert countries into the database.
	 * 
	 * @param db
	 *            the database
	 */
	private void insertCountries(SQLiteDatabase db) {

		ContentValues contentValues = new ContentValues();

		String[] tabColumnNames = new String[] { COUNTRY_ID, COUNTRY_NAME,
				COUNTRY_NAME + "_fr", COUNTRY_NAME + "_zh",
				COUNTRY_NAME + "_tw", COUNTRY_NAME + "_es",
				COUNTRY_NAME + "_pt", COUNTRY_NAME + "_ko",
				COUNTRY_NAME + "_jp", COUNTRY_NAME + "_de", COUNTRY_ORDER,
				COUNTRY_ORDER + "_fr", COUNTRY_ORDER + "_zh",
				COUNTRY_ORDER + "_es", COUNTRY_ORDER + "_pt",
				COUNTRY_ORDER + "_ko", COUNTRY_ORDER + "_jp",
				COUNTRY_ORDER + "_de" };

		try {
			InputStream inputStream = assetManager.open("data/country.csv");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "utf8"));
			// String used to store the lines
			String str;
			// read the file
			while ((str = reader.readLine()) != null) {

				String[] split = str.split(";");

				// contentValues.put(COUNTRY_ID, split[0]);
				// contentValues.put(COUNTRY_NAME, split[1]);
				// contentValues.put(COUNTRY_NAME + "_fr", split[2]);
				// contentValues.put(COUNTRY_NAME + "_zh", split[3]);
				// contentValues.put(COUNTRY_NAME + "_tw", split[4]);
				// contentValues.put(COUNTRY_NAME + "_es", split[5]);
				// contentValues.put(COUNTRY_NAME + "_pt", split[6]);
				// contentValues.put(COUNTRY_NAME + "_ko", split[7]);
				// contentValues.put(COUNTRY_NAME + "_jp", split[8]);
				// contentValues.put(COUNTRY_NAME + "_de", split[9]);
				// contentValues.put(COUNTRY_ORDER, split[10]);
				// contentValues.put(COUNTRY_ORDER + "_fr", split[11]);
				// contentValues.put(COUNTRY_ORDER + "_zh", split[12]);
				// contentValues.put(COUNTRY_ORDER + "_es", split[13]);
				// contentValues.put(COUNTRY_ORDER + "_pt", split[14]);
				// contentValues.put(COUNTRY_ORDER + "_ko", split[15]);
				// contentValues.put(COUNTRY_ORDER + "_jp", split[16]);
				// contentValues.put(COUNTRY_ORDER + "_de", split[17]);

				int i = 0;
				for (String string : tabColumnNames) {
					contentValues.put(string, split[i]);
					i++;
				}

				db.insert(COUNTRY_TABLE, null, contentValues);
			}

			// close streams
			reader.close();
			inputStream.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Insert towns data.
	 * 
	 * @param db
	 *            the database
	 */
	private void insertTowns(SQLiteDatabase db) {

		ContentValues contentValues = new ContentValues();

		String[] tabColumnNames = new String[] { TOWN_ID, TOWN_NAME,
				TOWN_NAME + "_fr", TOWN_NAME + "_zh", TOWN_NAME + "_tw",
				TOWN_ZH_ORDER, TOWN_NAME + "_es", TOWN_NAME + "_pt",
				TOWN_NAME + "_ko", TOWN_NAME + "_jp", TOWN_NAME + "_de",
				TOWN_COUNTRY_ID, TOWN_LAT, TOWN_LONG };

		try {
			InputStream inputStream = assetManager.open("data/town.csv");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "utf8"));
			// String used to store the lines
			String str;
			// read the file
			while ((str = reader.readLine()) != null) {

				String[] split = str.split(";");

				// contentValues.put(TOWN_ID, split[0]);
				// contentValues.put(TOWN_NAME, split[1]);
				// contentValues.put(TOWN_NAME + "_fr", split[2]);
				// contentValues.put(TOWN_NAME + "_zh", split[3]);
				// contentValues.put(TOWN_NAME + "_tw", split[4]);
				// contentValues.put(TOWN_ZH_ORDER, split[5]);
				// contentValues.put(TOWN_NAME + "_es", split[6]);
				// contentValues.put(TOWN_NAME + "_pt", split[7]);
				// contentValues.put(TOWN_NAME + "_ko", split[8]);
				// contentValues.put(TOWN_NAME + "_jp", split[9]);
				// contentValues.put(TOWN_NAME + "_de", split[10]);
				// contentValues.put(TOWN_FAVORITE, 0);// no favorites
				// contentValues.put(TOWN_COUNTRY_ID, split[11]);// countryId

				int i = 0;
				for (String string : tabColumnNames) {
					contentValues.put(string, split[i]);
					i++;
				}

				// contentValues.put(TOWN_ID, split[0]);
				// contentValues.put(TOWN_NAME, split[1]);
				// contentValues.put(TOWN_NAME + "_fr", split[2]);
				// contentValues.put(TOWN_NAME + "_zh", split[3]);
				// contentValues.put(TOWN_NAME + "_tw", split[4]);
				// contentValues.put(TOWN_ZH_ORDER, split[5]);
				// contentValues.put(TOWN_NAME + "_es", split[6]);
				// contentValues.put(TOWN_NAME + "_pt", split[7]);
				// contentValues.put(TOWN_NAME + "_ko", split[8]);
				// contentValues.put(TOWN_NAME + "_jp", split[9]);
				// contentValues.put(TOWN_NAME + "_de", split[10]);
				contentValues.put(TOWN_FAVORITE, 0);// no favorites
				// contentValues.put(TOWN_COUNTRY_ID, split[i]);// countryId

				db.insert(TOWN_TABLE, null, contentValues);

			}

			// close streams
			reader.close();
			inputStream.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Upgrade the database. Keeps user favorites.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Cursor favoritesTownCountry = db.rawQuery(
				properties.getProperty("getFavTownsId"), null);

		List<Integer> list = new ArrayList<Integer>();

		if (favoritesTownCountry.moveToFirst()) {
			do {
				int columnIndex = favoritesTownCountry
						.getColumnIndex(BaseColumns._ID);
				list.add(favoritesTownCountry.getInt(columnIndex));

			} while (favoritesTownCountry.moveToNext());
		}

		db.delete(TOWN_TABLE, null, null);
		db.delete(COUNTRY_TABLE, null, null);

		db.execSQL("DROP TABLE IF EXISTS " + TOWN_TABLE);

		db.execSQL("DROP TABLE IF EXISTS " + COUNTRY_TABLE);

		createDB(db);

		// add all favorites
		ContentValues cv = new ContentValues();
		cv.put(TOWN_FAVORITE, 1);

		String inClause = list.toString();
		inClause = inClause.replace("[", "(");
		inClause = inClause.replace("]", ")");

		db.update(TOWN_TABLE, cv, TOWN_ID + " in " + inClause, null);

	}

	/**
	 * Create the database.
	 * 
	 * @param db
	 *            the database
	 */
	private void createDB(SQLiteDatabase db) {
		String query = properties.getProperty("createCountryTable");
		db.execSQL(query);

		insertCountries(db);

		query = properties.getProperty("createTableTown");
		db.execSQL(query);

		insertTowns(db);

	}

	/**
	 * The action when user mark or unmark a favorite
	 * 
	 * @param favoriteValue
	 *            the value to the favorite (0 no fav, 1 fav)
	 * @param townID
	 *            the id of the town
	 * @return
	 */
	public int updateTownFavorite(int favoriteValue, int townID) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(TOWN_FAVORITE, favoriteValue);
		int update = db.update(TOWN_TABLE, cv, DatabaseHelper.TOWN_ID + "=?",
				new String[] { String.valueOf(townID) });

		db.close();

		return update;
	}

	/**
	 * The string value of a column
	 * 
	 * @param itemAtPosition
	 *            the item at position
	 * @param col
	 *            the column
	 * @return
	 */
	public String getStringColumnValue(Cursor itemAtPosition, String col) {

		int columnIndex2 = itemAtPosition.getColumnIndex(col);
		return itemAtPosition.getString(columnIndex2);

	}

	/**
	 * The int value at the position
	 * 
	 * @param itemAtPosition
	 *            the item at position
	 * @param col
	 *            the column
	 * @return
	 */
	public int getIntColValue(Cursor itemAtPosition, String col) {
		int columnIndex = itemAtPosition.getColumnIndex(col);
		return itemAtPosition.getInt(columnIndex);
	}

	/**
	 * Gets towns of a country.
	 * 
	 * @param countryId
	 *            id of a country
	 * @return Cursor on towns of a country
	 */
	public Cursor getTownsByCountry(int countryId) {
		SQLiteDatabase db = this.getWritableDatabase();

		StringBuilder builder = new StringBuilder();
		builder.append(properties.getProperty("getTownsByCountry"));
		builder.append(resources.getString(R.string.town_order));

		String[] selectionArgs = new String[] { String.valueOf(countryId) };
		Cursor cursor = db.rawQuery(builder.toString(), selectionArgs);

		return cursor;
	}

	/**
	 * Get all town name, according to the locale of the phone.
	 * 
	 * @return list of all towns names
	 */
	public List<String> getAllTownNames() {
		SQLiteDatabase db = this.getWritableDatabase();

		StringBuilder builder = new StringBuilder();
		builder.append(properties.getProperty("getAllTowns"));
		builder.append(resources.getString(R.string.town_order));

		Cursor cursor = db.rawQuery(builder.toString(), null);

		List<String> list = new ArrayList<String>();
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(cursor.getColumnIndex(resources
						.getString(R.string.town_name))));

			} while (cursor.moveToNext());
			Collections.sort(list, Collator.getInstance());
		}

		return list;
	}

	/**
	 * Gets a town by its id.
	 * 
	 * @param townId
	 *            id of the town
	 * @return
	 */
	public Town getTownById(int townId) {
		SQLiteDatabase db = this.getWritableDatabase();

		String[] selectionArgs = new String[] { String.valueOf(townId) };
		Cursor cursor = db.rawQuery(properties.getProperty("getTownById"),
				selectionArgs);

		cursor.moveToFirst();

		String townName = cursor.getString(cursor.getColumnIndex(resources
				.getString(R.string.town_name)));
		int favorite = cursor.getInt(cursor.getColumnIndex(TOWN_FAVORITE));
		int countryId = cursor.getInt(cursor.getColumnIndex(TOWN_COUNTRY_ID));

		// don't modify a parameter!
		int id = townId;
		Town town = new Town(id, townName, favorite, countryId);

		return town;

	}

	public Town getTownByName(String townName) {

		SQLiteDatabase db = this.getWritableDatabase();

		StringBuilder builder = new StringBuilder();
		builder.append(properties.getProperty("getTownByName"));
		builder.append("lower(town.");
		builder.append(resources.getString(R.string.town_name));
		builder.append(") =");
		builder.append("?");

		String trim = townName.toLowerCase(resources.getConfiguration().locale)
				.trim();
		String[] selectionArgs = new String[] { trim };
		Cursor cursor = db.rawQuery(builder.toString(), selectionArgs);

		Town town = null;
		if (cursor.moveToFirst()) {
			String townNameString = cursor.getString(cursor
					.getColumnIndex(resources.getString(R.string.town_name)));
			int favorite = cursor.getInt(cursor.getColumnIndex(TOWN_FAVORITE));
			int countryId = cursor.getInt(cursor
					.getColumnIndex(TOWN_COUNTRY_ID));

			int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			town = new Town(id, townNameString, favorite, countryId);
		}

		return town;
	}

}
