package ie.droidfactory.fragstations.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kudlaty on 02/10/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "rail.db";
    private static final List<String> DATABASE_CREATE_SQL = new ArrayList<>();
    public static final String TABLE_FAV_STATIONS="fav_stations";
    public static final String TABLE_ALL_STATIONS="all_stations";
    public static final String TABLE_ALL_TRAINS="all_trains";

    static {
        DATABASE_CREATE_SQL.add("create table "+TABLE_ALL_STATIONS+" (id integer primary key autoincrement," +
                " stationDescription string, stationCode string, stationId string, alias string," +
                " stationType string, stationLat float, stationLng float )");

        DATABASE_CREATE_SQL.add("create table "+TABLE_FAV_STATIONS+" (id integer primary key autoincrement," +
                " stationId string )");

        DATABASE_CREATE_SQL.add("create table "+TABLE_ALL_TRAINS+" (id integer primary key autoincrement," +
                " stationId string )");
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String s: DATABASE_CREATE_SQL){
            Log.d(TAG, "create table: "+s);
            sqLiteDatabase.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
