package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sbbg on 5/14/2016 AD.
 */
//public class LocationRemindService extends Service implements ConnectionCallbacks {
public class LocationRemindService extends Service  {
    public static final int MSG_DISABLE_NOTIFICATION = 0;
    public static final int MSG_ENABLE_NOTIFICATION = 1;
    public static final String MSG_TOGGLE_NOTIFICATION = "TOGGLE_NOTIFICATION";

    private boolean isRunning = false;
    private static final String LOGTAG = "ReminderService";
    private static final int EXPIRE_THRESHOLD = 4 * 60 * 1000; // ms
    private LocationManager locationManager;
   // private Intent intent;
    private RemindLocationListener listener;
    private Location previousBestLocation = null;
    private RemindDBManager db;
    private NotificationCompat.Builder mBuilder;
    private Messenger messenger;
    // Incoming messages Handler
    private boolean isNotificationEnabled = true;  // This is the switch to decide issuing the notification or not. Currently USED by the RemindsListActivity to suppress the notification ability

    ArrayList<Remind> remindsList;

    @Override
    public void onCreate()
    {
        super.onCreate();
        //Log.d( LOGTAG, "LocationRemindServer CREATED!" );
        isRunning = false;
        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        listener = new RemindLocationListener();
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider( criteria, true );
        locationManager.requestLocationUpdates( provider, 4000, 0, listener );
        //intent = new Intent( SERVICE_INIT_ACTION );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d( LOGTAG, "START triggered" );
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        super.onStartCommand(intent, flags, startId);
        if ( intent != null ) {
            Bundle bundle = intent.getExtras();
            if ( bundle != null ) {
                for ( String key : bundle.keySet() ) {
                    Object value = bundle.get( key );
                    Log.d( LOGTAG, String.format( "%s %s (%s)", key,
                            value.toString(), value.getClass().getName() ) );
                }
                isNotificationEnabled = bundle.getBoolean( MSG_TOGGLE_NOTIFICATION );
            }
        }

        if ( isNotificationEnabled ) {
        //    Log.d( LOGTAG, "NOTIFICATION ENABLED" );
            refreshReminds();
        }
        else {
            //   Log.d( LOGTAG, "NOTIFICATION DISABLED" );
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind( Intent intent) {
        return null;
    }

    private void refreshReminds() {
        //Log.d( LOGTAG, "refreshReminds TRIGGERED" );
        db = new RemindDBManager( this );
        //android.os.Debug.waitForDebugger();
        remindsList = new ArrayList<Remind>( db.getAllReminds() );
        db.close();
    }

    public class RemindLocationListener implements LocationListener
    {
        public void onLocationChanged(final Location loc)
        {
            if ( isNotificationEnabled == false )
                return;
            if ( isBetterLocation( loc, previousBestLocation ) ) {
                Log.i( "NEW LOCATION", loc.toString() );
                refreshReminds();
                for ( int k = 0; k < remindsList.size(); k++ ) {
                    Remind i = remindsList.get( k );
                    if ( ( i.closer_or_further == 0 && loc.distanceTo( i.center ) < i.distance ) ||
                            ( i.closer_or_further == 1 && loc.distanceTo( i.center ) > i.distance ) ) {
                        // DO notification
                        // Log.d( LOGTAG, "REMIND TRIGGERED!!" );
                        if ( i.location_name.isEmpty() )
                            showNotification( i.id, i.location_address, i.todo );
                        else
                            showNotification( i.id, i.location_name, i.todo );
                    }
                }
            }
        }

        public void onProviderDisabled( String provider )
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }

        public void onProviderEnabled( String provider )
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged( String provider, int status, Bundle extras ) {
            Log.d( LOGTAG, "status changed " + status );
        }
    }

    private void showNotification( long remind_id, String place, String todo ) {
        // when you need back intent:
        if ( !isNotificationEnabled )  // If the flag is DISABLED!
            return ;                   // do nothing
        
        Intent resultIntent = new Intent( this, RemindsListActivity.class );
//        // Because clicking the notification opens a new ("special") activity, there's
//        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification n  = new Notification.Builder(this)
                .setContentTitle( todo )
                .setContentText( place )
                .setSmallIcon( R.drawable.start_date )
                .setSound( soundUri )
                .setOnlyAlertOnce( true )
                .setAutoCancel( true )
                .setContentIntent( resultPendingIntent ).build();
               // .setAutoCancel( true ).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Long l = remind_id;
        notificationManager.notify( l.intValue(), n );
    }

    protected boolean isBetterLocation( Location location, Location currentBestLocation ) {
        if ( currentBestLocation == null ) {
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > EXPIRE_THRESHOLD;
        boolean isSignificantlyOlder = timeDelta < -(EXPIRE_THRESHOLD);
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if ( isSignificantlyNewer ) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if ( isSignificantlyOlder ) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }
    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    // Message Handler expecting the disable notification bandler from RemindListActivity.
//    class IncomingMsgHandler extends Handler {
//        @Override
//        public void handleMessage( Message msg ) {
//            switch ( msg.what ) {
//                case MSG_DISABLE_NOTIFICATION:
//                    isNotificationEnabled = false;
//                    Toast.makeText(getApplicationContext(), "Notification stopped", Toast.LENGTH_SHORT).show();
//                    break;
//                case MSG_ENABLE_NOTIFICATION:
//                    isNotificationEnabled = true;
//                    Toast.makeText(getApplicationContext(), "Notification started", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//
//        }
//    }
}
