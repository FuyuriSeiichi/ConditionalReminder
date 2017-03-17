package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.location.Location;

import java.util.Date;

/**
 * Created by sbbg on 5/1/2016 AD.
 */
public class Remind {

    public Remind( String label_in, String location_name_in, Location center_in ) {
        this.init();
        center = new Location( center_in );
        location_name = location_name_in;
        todo = label_in;
    }

    // ATTENTION: ID IS IGNORED during SAVE(db.insert), but only used in DELETE
    public Remind( long id, String label_in, String location_name_in, String addr_in,
                   Location center_in, int distance_in, int c_or_f_in , Date dateSince_in, Date dateUntil_in ) {
        this.init();
        this.id = id;
        todo = label_in;
        location_name = location_name_in;
        location_address = addr_in;
        center = new Location( center_in );
        distance = distance_in;
        closer_or_further = c_or_f_in;
        dateSince = dateSince_in;
        dateUntil = dateUntil_in;
    }

    public Remind() {
        this.init();
    }

    private void init() {
        id = 0;
        todo = new String( "test" );
        center = new Location( "" );
        location_address = new String();
        location_name = new String();
        center = new Location( "" );
        center.setLatitude( 0.0d );
        center.setLongitude( 0.0d );
    }

    public void setLocationName( String name ) {
        this.location_name = name;
    }

    public void setLocationCenter( Location loc ) {
        this.center = new Location( loc );
    }

    public long id;
    public String todo;
    public Location center;
    public String location_name;
    public String location_address;
    public int distance;
    public Date dateSince;  // Can be null
    public Date dateUntil;  // Can be null
    public int closer_or_further;  // Only 0/1 are valid values, 0:close (default) 1:further
}
