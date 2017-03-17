package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbbg on 5/4/2016 AD.
 */
public class RemindDBManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CondtionalReminders.db";
    public static final String REMIND_TABLE_NAME = "reminds";
    public static final String REMIND_ID = "remind_id";
    public static final String REMIND_TODO = "remind_todo";
    public static final String REMIND_LOCATION_NAME = "location_name";
    public static final String REMIND_LOCATION_ADDRESS = "location_address";
    public static final String REMIND_CENTER_LONGITUDE = "center_longitude";
    public static final String REMIND_CENTER_LATITUDE = "center_latitude";
    public static final String REMIND_DISTANCE = "distance";
    public static final String REMIND_CLOSER_OR_FURTHER = "futher_or_close";

    public static final String CREATE_STATEMENT = "CREATE TABLE " + REMIND_TABLE_NAME + " ( " +
            REMIND_ID + " INTEGER PRIMARY KEY, " +
            REMIND_TODO + " TEXT, " +
            REMIND_LOCATION_NAME + " TEXT, " +
            REMIND_LOCATION_ADDRESS + " TEXT, " +
            REMIND_CENTER_LATITUDE + " FLOAT, " +
            REMIND_CENTER_LONGITUDE + " FLOAT, " +
            REMIND_DISTANCE + " INTEGER, " +
            REMIND_CLOSER_OR_FURTHER + " INTEGER )";

    public RemindDBManager( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    public long insertRemind( Remind remind_in ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put( REMIND_TODO, remind_in.todo );
        cv.put( REMIND_LOCATION_NAME, remind_in.location_name );
        cv.put( REMIND_LOCATION_ADDRESS, remind_in.location_address );
        cv.put( REMIND_CENTER_LATITUDE, remind_in.center.getLatitude() );
        cv.put( REMIND_CENTER_LONGITUDE, remind_in.center.getLongitude() );
        cv.put( REMIND_DISTANCE, remind_in.distance );
        cv.put( REMIND_CLOSER_OR_FURTHER, remind_in.closer_or_further );
        return db.insert( REMIND_TABLE_NAME, null, cv );
    }

    public int delete( long id ) {
        SQLiteDatabase db = getWritableDatabase();
       // String[] whereArgs = new String[] { String.valueOf( id ) };
        return db.delete( REMIND_TABLE_NAME, REMIND_ID + "=" + String.valueOf( id ), null  );
    }

    public List<Remind> getAllReminds() {
        List<Remind> remindsList = new ArrayList<Remind>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + REMIND_TABLE_NAME, null );

        if ( cursor.moveToFirst() ) {
            do {
                Location center = new Location( "" );
                center.setLatitude( cursor.getFloat( cursor.getColumnIndex( REMIND_CENTER_LATITUDE ) ) );
                center.setLongitude( cursor.getFloat( cursor.getColumnIndex( REMIND_CENTER_LONGITUDE ) ) );
                Remind remind = new Remind( cursor.getLong( cursor.getColumnIndex( REMIND_ID ) ),
                        cursor.getString( cursor.getColumnIndex( REMIND_TODO ) ),
                        cursor.getString( cursor.getColumnIndex( REMIND_LOCATION_NAME ) ),
                        cursor.getString( cursor.getColumnIndex( REMIND_LOCATION_ADDRESS ) ),
                        center,
                        cursor.getInt( cursor.getColumnIndex( REMIND_DISTANCE ) ),
                        cursor.getInt( cursor.getColumnIndex( REMIND_CLOSER_OR_FURTHER ) ), null, null );
                //Log.d( "remindDB", remind.todo + " " + remind.location_name );
                remindsList.add( remind );
            } while (cursor.moveToNext());
        }
        return remindsList;
    }
    // ============================= Basic DB helper callbacks ============================
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_STATEMENT );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + REMIND_TABLE_NAME );
        onCreate( db );
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade( db, oldVersion, newVersion );
    }
}
