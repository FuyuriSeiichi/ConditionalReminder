package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class EditCondsActivity extends AppCompatActivity {
    String LOGTAG = new String( "EditCondsActivity::" );
    EditCondsListFragment fragment;
    RecyclerView recyclerview_Conds;
    Remind remind;
    //CondsAdapter adapter;
    RemindDBManager remind_db;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Intent intent = getIntent();
        super.onCreate( savedInstanceState );
    //    adapter = new CondsAdapter( this );
        setContentView( R.layout.activity_edit_conds );
        remind_db = new RemindDBManager( this );
        remind = new Remind();
        Location location = new Location( "" );
        location.setLatitude( intent.getDoubleExtra( "latitude", 0.0 ) );
        remind.setLocationCenter( location );
        remind.setLocationName( intent.getStringExtra( "name" ) );
        floatingActionMenuVisibility();
       //ActionBar actionbar = this.getSupportActionBar();
        if ( savedInstanceState == null ) {
            fragment = new EditCondsListFragment();
            Bundle args = new Bundle();
            args.putDouble( "latitude", intent.getDoubleExtra( "latitude", 0.0 ) );
            args.putDouble( "longitude", intent.getDoubleExtra( "longitude", 0.0 ) );
            args.putString( "name", intent.getStringExtra( "name" ) );
            fragment.setArguments( args );
            getFragmentManager().beginTransaction().add( R.id.layout_recyclerview, fragment ).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu( menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.edit_conds, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        super.onOptionsItemSelected( item );
        switch (item.getItemId()) {
            case R.id.action_save:
                // User chose the "Settings" item, show the app settings UI...
                //remind_db.insertRemind( new Remind() );
                Log.d( LOGTAG, "SAVE TRIGGER" );
                saveRemind();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveRemind() {
        remind_db.insertRemind( fragment.adapter.getConfiguredRemind() );
        Toast text = Toast.makeText( this, R.string.remind_saved, Toast.LENGTH_SHORT );
        text.show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void floatingActionMenuVisibility() {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                viewGroup.getWindowVisibleDisplayFrame(r);
                int screenHeight = viewGroup.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if ( keypadHeight > screenHeight * 0.15 ) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    FloatingActionsMenu fam = (FloatingActionsMenu)findViewById( R.id.fam_edit_conds );
                    fam.setVisibility( View.GONE );
                }
                else {
                    Log.d( "KEYBOARD", "HIDE" );
                    FloatingActionsMenu fam = (FloatingActionsMenu)findViewById( R.id.fam_edit_conds );
                    fam.setVisibility( View.VISIBLE );
                }
            }
        });
    }
}
