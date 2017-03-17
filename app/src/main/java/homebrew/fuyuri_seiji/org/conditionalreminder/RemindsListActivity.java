package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class RemindsListActivity extends AppCompatActivity {
    RemindDBManager remind_db;
    Intent reminderServiceIntent;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reminds_list );

        remind_db = new RemindDBManager( this );

        if ( savedInstanceState == null ) {
            RemindsListFragment fragment = new RemindsListFragment();
            getFragmentManager().beginTransaction().add( R.id.layout_remindslist, fragment ).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putBoolean( LocationRemindService.MSG_TOGGLE_NOTIFICATION, false );
        reminderServiceIntent = new Intent( this, LocationRemindService.class );
        reminderServiceIntent.putExtras( bundle );
        this.startService( reminderServiceIntent );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bundle bundle = new Bundle();
        bundle.putBoolean( LocationRemindService.MSG_TOGGLE_NOTIFICATION, true );
        reminderServiceIntent = new Intent( this, LocationRemindService.class );
        reminderServiceIntent.putExtras( bundle );
        this.startService( reminderServiceIntent );
    }
}
