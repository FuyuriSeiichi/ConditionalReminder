package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sbbg on 5/6/2016 AD.
 */
public class RemindsAdapter extends RecyclerView.Adapter<RemindsAdapter.RemindViewHolder> {
    ArrayList<Remind> remindList;
    RemindDBManager db;
    Context context;
    RemindsListFragment.RemoveNotifierToUI removeNotifier;

    public RemindsAdapter( Context context_in ) {
        super();
     //   remindList = new ArrayList<String>( Arrays.asList( context.getResources().getStringArray( R.array.dummies ) ) );
        context = context_in;

    }

    public RemindsAdapter( Context context_in, RemindsListFragment.RemoveNotifierToUI notifier_in ) {
        super();
        context = context_in;
        db = new RemindDBManager( context_in );
        remindList = new ArrayList<Remind>( db.getAllReminds() );
        removeNotifier = notifier_in;
    }

    @Override
    public RemindViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from( context );

        android.view.View viewRemind;
        viewRemind = inflater.inflate( R.layout.card_remind_overview, parent, false );
        RemindViewHolder viewHolder = new RemindViewHolder( viewRemind );
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( final RemindViewHolder holder, int pos ) {
        final Remind current = remindList.get( pos );
        holder.imageView.setImageDrawable( null );
        holder.textviewTodo.setText( current.todo );
        if ( current.location_name.isEmpty() )
            holder.textviewLocation.setText( current.location_address );
        else
            holder.textviewLocation.setText( current.location_name );

        holder.imageView.post( new Runnable() {  // New thread to load image for this item when it's displayed.
            @Override
            public void run() {
                    Utility.loadMapThumbnail( context, holder.imageView,
                            new LatLng( current.center.getLatitude(), current.center.getLongitude() ), 18 );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return remindList.size();
    }

//    @Override
//    public boolean onItemMoved( int index_ori, int index_new ) {
//        return false;
//    }
//
//    @Override
//    public void onItemDismissed( int index ) {
//        this.remove( index );
//    }

    public static class RemindViewHolder extends RecyclerView.ViewHolder
            //         implements ItemTouchHelperViewHolder
    {
        public final TextView textviewLocation;
        public final TextView textviewTodo;
        public final ImageView imageView;

        public RemindViewHolder( View itemView ) {
            super( itemView );
            textviewLocation = (TextView) itemView.findViewById( R.id.textview_location_name );
            textviewTodo = (TextView) itemView.findViewById( R.id.textview_todo );
            imageView = (ImageView) itemView.findViewById( R.id.imageview_remind );
        }
    }

    public void swap( int pos1, int pos2 ) {
        if ( pos1 < pos2) {
            for ( int i = pos1; i < pos2; i++ ) {
                Collections.swap( remindList, i, i + 1 );
            }
        } else {
            for ( int i = pos1; i > pos2; i-- ) {
                Collections.swap( remindList, i, i - 1 );
            }
        }
        notifyItemMoved( pos1, pos2 );
    }

    public void remove( int index ) {
        int removed_count = db.delete( remindList.get( index ).id );
        remindList.remove( index );
        //notifyDataSetChanged();
        notifyItemRemoved( index );
        removeNotifier.remove( index );
       // notifyItemRangeChanged( index, remindList.size() );
    }

}
