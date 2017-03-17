package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.picasso.Picasso;

/**
 * Singleton class to serve any others.
 * Created by sbbg on 5/10/2016 AD.
 */


public class Utility {
    public static String NOTIFICATION_SWITCH = "notification_switch";

    private Utility() {}

    public static Location LatLngToLocation( LatLng input ) {
        Location new_location = new Location( "" );
        new_location.setLatitude( input.latitude );
        new_location.setLongitude( input.longitude );
        return new_location;
    }

    public static LatLngBounds LatLngBoundsFromCenter( LatLng input, double meter ) {
        double meterBy1Latitude = 111000; // Meter
        double meterBy1Longitude = 110567;
        //  Log.d( "Utility", "Northeast:" + ( input.latitude - meter/meterBy1Latitude ) + ", " + ( input.longitude - meter/meterBy1Longitude ) );
        // Log.d( "Utility", "Southwest:" + ( input.latitude + meter/meterBy1Latitude ) + ", " + ( input.longitude + meter/meterBy1Longitude ) );

        return new LatLngBounds( new LatLng( input.latitude - meter/meterBy1Latitude, input.longitude - meter/meterBy1Longitude ),
                new LatLng( input.latitude + meter/meterBy1Latitude, input.longitude + meter/meterBy1Longitude ) );
    }

    public static void loadMapThumbnail( Context context, ImageView view, LatLng center, int zoom_level ) {
        ///https://maps.googleapis.com/maps/api/staticmap?center=35.844244,139.6655575&zoom=20&size=400x400&key=AIzaSyDdy3ZQg8w-GmDh7Nveun5h6vhd8g2qWIU
        String request = "https://maps.googleapis.com/maps/api/staticmap?center="
                + center.latitude + "," + center.longitude + "&zoom=" + zoom_level
                + "&size=" + view.getMeasuredWidth() + "x" + view.getMeasuredHeight() +
                "&key=" + context.getResources().getString( R.string.google_maps_key );
        Log.d( "loadMapThumbnail:", "REQUEST = " + request );
        Picasso.with( context ).load( request ).into( view );
    }
}
