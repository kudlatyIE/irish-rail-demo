package ie.droidfactory.irishrails.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kudlaty on 2017-10-13.
 */

public class DbRailSource {

    private final static String TAG = DbRailSource.class.getSimpleName();

    private String[] columnsFavTable = {FAV_ID, FAV_STATION_ID};
    private final static String FAV_ID="id", FAV_STATION_ID="stationId";
    private final static String SELECT_FAV_ID = FAV_STATION_ID+ " LIKE ?";

    private Context context;
    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public DbRailSource(Context context){
        this.context=context;
        this.dbHelper = new DbHelper(context);
    }

    public void open(){
        this.database = dbHelper.getWritableDatabase();
    }
    public void close(){
        this.dbHelper.close();
    }

    public String getDbName(){
        return context.getDatabasePath(dbHelper.getDatabaseName()).getName();
    }

    public int addFavStation(String stationId){
        ContentValues values = new ContentValues();
        values.put(FAV_STATION_ID, stationId);
        return (int) database.insert(DbHelper.getTableFavStations(), null, values);
    }

    public List<String> getFavoritiesStationIds(){

        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(DbHelper.getTableFavStations(),columnsFavTable, null, null,null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            list.add(cursor.getString(cursor.getColumnIndex(FAV_STATION_ID)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public int  deleteFavId(String stationId){
        String[] IDs = {stationId};
        return database.delete(DbHelper.getTableFavStations(), SELECT_FAV_ID, IDs );
    }
}
