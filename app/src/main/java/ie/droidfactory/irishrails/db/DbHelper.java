package ie.droidfactory.irishrails.db;

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
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rail.db";
    private static final List<String> DATABASE_CREATE_SQL = new ArrayList<>();
    private static final String TABLE_FAV_STATIONS="fav_stations";
    private static final String TABLE_ALL_STATIONS="all_stations";
    private static final String TABLE_ALL_TRAINS="all_trains";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private final static String[] ALL_TABLES = {TABLE_FAV_STATIONS, TABLE_ALL_STATIONS, TABLE_ALL_TRAINS};

    static {
        DATABASE_CREATE_SQL.add("create table "+TABLE_ALL_STATIONS+" (id integer primary key autoincrement," +
                " stationDescription string, stationCode string, stationId string, alias string," +
                " stationType string, stationLat float, stationLng float )");

        DATABASE_CREATE_SQL.add("create table "+TABLE_FAV_STATIONS+" (id integer primary key autoincrement," +
                " stationId string )");

        DATABASE_CREATE_SQL.add("create table "+TABLE_ALL_TRAINS+" (id integer primary key autoincrement," +
                " stationId string )");
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String s: DATABASE_CREATE_SQL){
            Log.d(TAG, "create table: "+s);
            sqLiteDatabase.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Database updating...");
        for(String drop: ALL_TABLES){
            db.execSQL(DROP_TABLE+ drop);
            Log.d(TAG, "Table " + drop + " updated from ver." + oldVersion + " to ver." + newVersion);
        }
        Log.d(TAG, "All data is lost.");

        onCreate(db);
    }

    public static int getDbVersion() {
        return DATABASE_VERSION;
    }

    public static String getDbName() {
        return DATABASE_NAME;
    }

    public static String getTableFavStations() {
        return TABLE_FAV_STATIONS;
    }

    public static String getTableAllStations() {
        return TABLE_ALL_STATIONS;
    }

    public static String getTableAllTrains() {
        return TABLE_ALL_TRAINS;
    }
}
