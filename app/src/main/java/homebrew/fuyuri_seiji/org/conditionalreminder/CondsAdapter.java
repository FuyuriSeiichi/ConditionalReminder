package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by sbbg on 5/1/2016 AD.
 */
public class CondsAdapter extends RecyclerView.Adapter<CondsAdapter.CondViewHolder>
        implements DefaultItemTouchHelper.DefaultItemTouchHelperInterface {
    static private String LOGTAG = "CondsAdapter";
    Context context;
    private String location_name;
    private Location location;
    private int closer_or_further;
    private int distance;
    private String todo;
    private ArrayList<String> condsList;

    public CondsAdapter( Context context ) {
     //   mItems.addAll( Arrays.asList( context.getResources().getStringArray( R.array.dummy_items ) ) );
        condsList = new ArrayList<String>( Arrays.asList( context.getResources().getStringArray( R.array.dummies ) ) );
    }

    public CondsAdapter( Context context, String location_name_in, double latitude, double longitude ) {
        this.context = context;
        location_name = location_name_in;
        location = new Location( "" );
        location.setLatitude( latitude );
        location.setLongitude( longitude );
        condsList = new ArrayList<String>();
        condsList.add( this.location_name );
    }

    public Remind getConfiguredRemind()
    {
        Remind remind = new Remind( todo, this.location_name, this.location );
        // Set closer_or_further
        remind.closer_or_further = closer_or_further;
        // Set distance
        remind.distance = distance;
        return remind;
    }

    @Override
    public CondViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from( context );

        View viewCond = inflater.inflate( R.layout.card_geographical_cond, parent, false );
        CondViewHolder viewHolder = new CondViewHolder( viewCond );
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( final CondViewHolder holder, int position ) {
        ViewTreeObserver viewTreeObserver = holder.imageView.getViewTreeObserver();
        holder.textView.setText( condsList.get( position ) );
        holder.edittext_todo.addTextChangedListener( new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                todo = s.toString();
            }
        } );
        holder.edittext_distance.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {}
            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
                if ( !charSequence.toString().isEmpty() )
                    distance = Integer.parseInt( charSequence.toString() );
                else
                    distance = 0;
            }
            @Override
            public void afterTextChanged( Editable editable ) {}
        } );
        holder.radiogroup_c_or_f.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
//                RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
//                boolean isChecked = checkedRadioButton.isChecked();
//                if ( isChecked )
//                {
//                }
                //holder.radiogroup_c_or_f.getId()
                int radioButtonID = rGroup.getCheckedRadioButtonId();
                View radioButton = rGroup.findViewById(radioButtonID);
                closer_or_further = rGroup.indexOfChild(radioButton);
            }
        } );

        if ( viewTreeObserver.isAlive() ) {
            viewTreeObserver.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    holder.imageView.getViewTreeObserver().removeOnGlobalLayoutListener( this );
                    float viewWidth, viewHeight;
                    viewWidth = holder.imageView.getWidth();
                    viewHeight = holder.imageView.getHeight();
                    Utility.loadMapThumbnail( context, holder.imageView,
                            new LatLng( location.getLatitude(), location.getLongitude() ), 18 );
                }
            } );
        }
    }

    @Override
    public int getItemCount() {
        return condsList.size();
    }

    @Override
    public boolean onItemMoved( int index_ori, int index_new ) {
        if ( index_ori < index_new ) {
            for ( int i = index_ori; i < index_new; i++ ) {
                Collections.swap( condsList, i, i + 1 );
            }
        } else {
            for ( int i = index_ori; i > index_new; i-- ) {
                Collections.swap( condsList, i, i - 1 );
            }
        }
        notifyItemMoved( index_ori, index_new );
        Log.d( LOGTAG, "ITEM SWITCHED from " + index_ori + " to " + index_new );
        return true;
    }

    @Override
    public void onItemDismissed( int index ) {
        condsList.remove( index );
        //notifyItemChanged( index );  // BUGGY UPSTREAM !? when index = 0, it will be FUXKED!!
        notifyDataSetChanged();
        Log.d( LOGTAG, "ITEM DISMISSED: index=" + index );
    }

    public static class CondViewHolder extends RecyclerView.ViewHolder
   //         implements ItemTouchHelperViewHolder
    {
        public final TextView textView;
        public final ImageView imageView;
        public RadioGroup radiogroup_c_or_f;
        public EditText edittext_todo;
        public EditText edittext_distance;

        public CondViewHolder( View itemView ) {
            super( itemView );
            textView = (TextView) itemView.findViewById( R.id.textview_location );
            imageView = (ImageView) itemView.findViewById( R.id.imageview_thumbnail );
            radiogroup_c_or_f = (RadioGroup)itemView.findViewById( R.id.radiogroup_closer_or_further );
            edittext_todo = (EditText)itemView.findViewById( R.id.edittext_todo );
            edittext_distance = (EditText)itemView.findViewById( R.id.edittext_distance );
        }
    }
}
