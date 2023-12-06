package com.depauw.locallocator.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "locallocator.db";
    private static final int DB_VERSION = 1;
    private static DBHelper myInstance;

    public static final String TABLE_BORDER = "Border";
    public static final String COL_BORDER_STATE = "State";
    public static final String COL_BORDER_LOCAL = "Local";
    public static final String COL_BORDER_LATITUDE = "Latitude";
    public static final String COL_BORDER_LONGITUDE = "Longitude";
    public static final String COL_BORDER_CLOCKWISE_ORDER = "ClockwiseOrder";

    public static final String TABLE_WORKSITE = "Worksite";
    public static final String COL_WORKSITE_NAME = "Name";
    public static final String COL_WORKSITE_STATE = "State";
    public static final String COL_WORKSITE_LOCAL = "Local";
    public static final String COL_WORKSITE_INFORMATION = "Information";
    public static final String COL_WORKSITE_LATITUDE = "Latitude";
    public static final String COL_WORKSITE_LONGITUDE = "Longitude";

    public static final String TABLE_WEBSITE = "Website";
    public static final String COL_WEBSITE_STATE = "State";
    public static final String COL_WEBSITE_LOCAL = "Local";
    public static final String COL_WEBSITE_WEBSITE = "Website";

    public static final String TABLE_LOCAL = "Local";
    public static final String COL_LOCAL_IDENTIFIER = "Identifier";
    public static final String COL_LOCAL_STATE = "State";
    public static final String COL_LOCAL_FOUNDED = "Founded";
    public static final String COL_LOCAL_PHONE = "Phone";
    public static final String COL_LOCAL_ADDRESS = "Address";

    private DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance(Context c) {
        if(myInstance == null) {
            myInstance = new DBHelper(c);
        }
        return myInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_BORDER + " (" +
                COL_BORDER_STATE + " TEXT NOT NULL," +
                COL_BORDER_LOCAL + " TEXT NOT NULL," +
                COL_BORDER_LATITUDE + " REAL NOT NULL," +
                COL_BORDER_LONGITUDE + " REAL NOT NULL," +
                COL_BORDER_CLOCKWISE_ORDER + " INTEGER NOT NULL " +
                ")";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<BorderVertex> getBordersByLocal(int searchLocal) {
        SQLiteDatabase db = getReadableDatabase();
        String searchQuery = String.format("SELECT * FROM %s WHERE %s = %s ORDER BY %s ASC",
                                            TABLE_BORDER,
                                            COL_BORDER_LOCAL,
                                            searchLocal,
                                            COL_BORDER_CLOCKWISE_ORDER);

        Cursor cursor = db.rawQuery(searchQuery, null);
        int idx_latitude = cursor.getColumnIndex(COL_BORDER_LATITUDE);
        int idx_longitude = cursor.getColumnIndex(COL_BORDER_LONGITUDE);

        ArrayList<BorderVertex> borderVertices = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                float latitude = cursor.getFloat(idx_latitude);
                float longitude = cursor.getFloat(idx_longitude);

                BorderVertex bv = new BorderVertex(latitude, longitude);
                borderVertices.add(bv);
            }while (cursor.moveToNext());
            db.close();
            return borderVertices;
        }
        else {
            db.close();
            return null;
        }
    }

    public String getWebsiteByLocal(int searchLocal) {
        SQLiteDatabase db = getReadableDatabase();
        String searchQuery = String.format("SELECT * FROM %s WHERE %s = %s",
                                            TABLE_WEBSITE,
                                            COL_WEBSITE_LOCAL,
                                            searchLocal);

        Cursor cursor = db.rawQuery(searchQuery, null);
        int idx_website = cursor.getColumnIndex(COL_WEBSITE_WEBSITE);
        if(cursor.moveToFirst()) {
            String websiteUrl = cursor.getString(idx_website);
            db.close();
            return websiteUrl;
        }
        return "";
    }

    public ArrayList<Local> getLocals(){
        SQLiteDatabase db = getReadableDatabase();
        String searchQuery = String.format("SELECT * FROM %s",
                TABLE_LOCAL);

        Cursor cursor = db.rawQuery(searchQuery, null);
        int idx_identifier = cursor.getColumnIndex(COL_LOCAL_IDENTIFIER);
        int idx_founded = cursor.getColumnIndex(COL_LOCAL_FOUNDED);
        int idx_phone = cursor.getColumnIndex(COL_LOCAL_PHONE);
        int idx_address = cursor.getColumnIndex(COL_LOCAL_ADDRESS);

        ArrayList<Local> locals = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                int identifier = cursor.getInt(idx_identifier);
                int founded = cursor.getInt(idx_founded);
                String phone = cursor.getString(idx_phone);
                String address = cursor.getString(idx_address);

                Local l = new Local(identifier, founded, phone, address);
                locals.add(l);
            }while (cursor.moveToNext());
            db.close();
            return locals;
        }
        else {
            db.close();
            return null;
        }
    }

    public ArrayList<Worksite> getWorksites() {
        SQLiteDatabase db = getReadableDatabase();
        String searchQuery = String.format("SELECT * FROM %s",
                TABLE_WORKSITE);

        Cursor cursor = db.rawQuery(searchQuery, null);
        int idx_name = cursor.getColumnIndex(COL_WORKSITE_NAME);
        int idx_state = cursor.getColumnIndex(COL_WORKSITE_STATE);
        int idx_information = cursor.getColumnIndex(COL_WORKSITE_INFORMATION);
        int idx_local = cursor.getColumnIndex(COL_WORKSITE_LOCAL);
        int idx_latitude = cursor.getColumnIndex(COL_WORKSITE_LATITUDE);
        int idx_longitude = cursor.getColumnIndex(COL_WORKSITE_LONGITUDE);

        ArrayList<Worksite> worksites = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(idx_name);
                String state = cursor.getString(idx_state);
                String information = cursor.getString(idx_information);
                int local = cursor.getInt(idx_local);
                float latitude = cursor.getFloat(idx_latitude);
                float longitude = cursor.getFloat(idx_longitude);

                Worksite worksite = new Worksite(name, state, information, local, latitude, longitude);
                worksites.add(worksite);
            }while (cursor.moveToNext());
            db.close();
            return worksites;
        } else {
            db.close();
            return null;
        }
    }
}
