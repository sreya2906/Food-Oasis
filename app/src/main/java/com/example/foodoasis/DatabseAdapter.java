package com.example.foodoasis;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabseAdapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Locations";

    private static final String LOCATION_INFO_TABLE = "location_table";
    private static final String KEY_LOCATION_ID = "location_id";
    private static final String KEY_LOCATION_PLACENAME = "location_name";
    private static final String KEY_LOCATION_WEBSITE = "location_website";
    private static final String KEY_LOCATION_PHONENO = "location_phoneno";
    private static final String KEY_LOCATION_LATITUDE = "location_latitude";
    private static final String KEY_LOCATION_LONGITUDE = "location_longitude";


    public DatabseAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryLocation = "CREATE TABLE " + LOCATION_INFO_TABLE + " (" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LOCATION_PLACENAME + " TEXT, " +
                KEY_LOCATION_WEBSITE + " TEXT, " +
                KEY_LOCATION_PHONENO + " TEXT, " +
                KEY_LOCATION_LATITUDE + " TEXT, " +
                KEY_LOCATION_LONGITUDE + " TEXT " + ")";

        try {
            db.execSQL(queryLocation);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String queryLocation = "CREATE TABLE " + LOCATION_INFO_TABLE + " (" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LOCATION_PLACENAME + " TEXT, " +
                KEY_LOCATION_WEBSITE + " TEXT, " +
                KEY_LOCATION_PHONENO + " TEXT, " +
                KEY_LOCATION_LATITUDE + " TEXT, " +
                KEY_LOCATION_LONGITUDE + " TEXT " + ")";

        try {
            db.execSQL(queryLocation);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        }
    }

    public void addLocation(FavoritesPlaces favoritesPlaces) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO  location_table(location_name,location_website,location_phoneno,location_latitude,location_longitude) values ('" +
                favoritesPlaces.getPlaceName() + "', '" +
                favoritesPlaces.getWebsite() + "', '" +
                favoritesPlaces.getPhoneNumber() + "', '" +
                favoritesPlaces.getLongitude() + "', '" +
                favoritesPlaces.getLongitude() + "')";
        Log.e("query", query);

        try {
            db.execSQL(query);
            Log.e("Sucessfully", "inserted");
        } catch (SQLException e) {
            Log.e("Error", e.getMessage() + "");
        }
        db.close();
    }

    public ArrayList<FavoritesPlaces> getDetails() {
        ArrayList<FavoritesPlaces> LocationsList = new ArrayList<FavoritesPlaces>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM location_table";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FavoritesPlaces favoritesPlaces = new FavoritesPlaces();
                favoritesPlaces.setLocation_id(Integer.parseInt(cursor.getString(0)));
                favoritesPlaces.setPlaceName(cursor.getString(1));
                favoritesPlaces.setWebsite(cursor.getString(2));
                favoritesPlaces.setPhoneNumber(cursor.getString(3));

                LocationsList.add(favoritesPlaces);

            } while (cursor.moveToNext());
        }
        return LocationsList;
    }

    public int checkLocation(String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT count(*) FROM location_table where location_latitude='" + latitude + "' and location_longitude='" + longitude + "'";
        Log.e("query",query);

        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(0) == "1") {
                        return 1;
                    } else {
                        Log.e("Already", "exists");

                        return 0;

                    }

                } while (cursor.moveToNext());
            } else {
                return 0;
            }
        } catch (SQLException e) {
            Log.e("error", e.getMessage() + "");
            return 0;
        }


    }

    public void deleteLocation(int location_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM location_table WHERE location_id=" + location_id;

        Log.d("query", query);
        db.execSQL(query);
        db.close();
    }
}
