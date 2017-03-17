package homebrew.fuyuri_seiji.org.conditionalreminder;

import homebrew.fuyuri_seiji.org.conditionalreminder.Utility;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Criteria;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private PlaceAutocompleteFragment autocompleteFragment;
    private String LOGTAG = new String( "ConditionalFinder.MapsActivity::" );
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location myLocation;
    private ArrayList<Place> places;
    private Intent intentServiceTrigger;
    // BLOCK if myLocation is not ready !!

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );

        if ( !isMyServiceRunning( LocationRemindService.class ) ) {
            Bundle bundle = new Bundle();
            bundle.putBoolean( LocationRemindService.MSG_TOGGLE_NOTIFICATION, true );
            intentServiceTrigger = new Intent( this, LocationRemindService.class );
            intentServiceTrigger.putExtras( bundle );
            this.startService( intentServiceTrigger );
        }

        places = new ArrayList<Place>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this );

        autocompleteFragment = ( PlaceAutocompleteFragment )
                getFragmentManager().findFragmentById( R.id.place_autocomplete_fragment );
        autocompleteFragment.setOnPlaceSelectedListener( new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected( Place place ) {
                // TODO: ONLY 1 PLACE IS SELECTED HERE !!
                places.add( place );
                // ============= GET distance ( MyLocation, Selected Place ) ===============
                float distances[] = new float[5];
                if ( myLocation != null ) {
                    Location.distanceBetween( myLocation.getLatitude(), myLocation.getLongitude(),
                            place.getLatLng().latitude, place.getLatLng().longitude, distances );
                    Log.d( LOGTAG, "DISTANCE = " + distances[0] );
                }
                // ==========================================================================
                Log.i( LOGTAG, "CENTER:" + place.getLatLng() );
                Log.i( LOGTAG, "Place: " + place.getName() );
                Log.i( LOGTAG, "Place LATLNG: " + place.getLatLng() );
                Location location_place = new Location( "" );
                location_place.setLatitude( place.getLatLng().latitude );
                location_place.setLongitude( place.getLatLng().longitude );
                // Add disabling of AutoCompleteFragment and mask it with a cancel button.
                mMap.addMarker( new MarkerOptions().position( place.getLatLng() ).title( "New place" ).snippet( "" ) );
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i( LOGTAG, "An error occurred: " + status );
            }
        } );
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Enable MyLocation Layer of Google Map
        MoveToMyLocation();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates( locationListener );
        Log.d( LOGTAG, "Location Update STOP" );
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void MoveToMyLocation()
    {
        Log.d( LOGTAG, "Location Update START" );
        mMap.setMyLocationEnabled( true );
        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        locationListener = new MyLocationListener();
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider( criteria, true );

        // Get Current Location
        myLocation = locationManager.getLastKnownLocation( provider );
        if ( myLocation == null ) {
            // NO PREVIOUS LOCATION AVAILABLE!!
            locationManager.requestLocationUpdates( provider, 1000, 0, locationListener );
            // To try best effort(avoid : ENABLE both LocationProvider like these 2 lines:
            //locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 0, locationListener );
            //locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener );
            return;
        }
        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        LatLng latLng = new LatLng( latitude, longitude );
        autocompleteFragment.setBoundsBias( Utility.LatLngBoundsFromCenter( latLng, 100 ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( latLng ) );
    }

    // Button Events:
    public void StartEditCondActivity( View new_cond_button )
    {
        if ( places.isEmpty() ) {
            AlertDialog alertDialog = new AlertDialog.Builder( MapsActivity.this ).create();
            alertDialog.setTitle( getResources().getString( R.string.warning ) );
            alertDialog.setMessage( getResources().getString( R.string.no_location_set ) );
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick( DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else {
            Intent intent = new Intent( this, EditCondsActivity.class );
            intent.putExtra( "name", places.get( 0 ).getName() );
            intent.putExtra( "latitude", places.get( 0 ).getLatLng().latitude );
            intent.putExtra( "longitude", places.get( 0 ).getLatLng().longitude );
            startActivity( intent );
        }
    }

    public LatLng getLocation() {
        return places.get( 0 ).getLatLng();
    }

    public void StartRemindsListActivity( View reminds_list_button )
    {
        Intent intent = new Intent( this, RemindsListActivity.class );
        startActivity( intent );
    }

    public class MyLocationListener implements android.location.LocationListener {
        private final String TAG = MyLocationListener.class.getSimpleName();

        @Override
        public void onLocationChanged( Location location ) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng( latitude, longitude );
            myLocation.setLatitude( latitude );
            myLocation.setLongitude( longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng) );
            // Set Bounds for complement result:
            autocompleteFragment.setBoundsBias( Utility.LatLngBoundsFromCenter( latLng, 100 ) );
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private boolean isMyServiceRunning( Class<?> serviceClass ) {
        ActivityManager manager = (ActivityManager)getSystemService( Context.ACTIVITY_SERVICE );
        for ( ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) ) {
            if ( serviceClass.getName().equals( service.service.getClassName() ) ) {
                return true;
            }
        }
        return false;
    }
}
