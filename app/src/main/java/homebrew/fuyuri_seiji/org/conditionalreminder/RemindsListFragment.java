package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sbbg on 5/6/2016 AD.
 */
public class RemindsListFragment extends Fragment {

    private static String LOGTAG = "RemindsListFragment";

    private removeNotifyInterface removeReceiver;

    private RemindsAdapter adapter;

    public RemindsListFragment() {}

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        return new RecyclerView( container.getContext() );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        removeReceiver = new () {
//            @Override
//            public void remove( int index ) {
//                getActivity().runOnUiThread( new Runnable() {
//                    public void run() {
//                        //do your modifications here
//
//                        // for example
//                        adapter.add(new Object());
//                        adapter.notifyDataSetChanged()
//                    }
//
//                );
//            }
//        }
        adapter = new RemindsAdapter( getActivity(), new RemoveNotifierToUI() );
       // final RemindsAdapter adapter = new RemindsAdapter( getActivity() );


        RecyclerView recyclerview_reminds = (RecyclerView)view;
        recyclerview_reminds.setHasFixedSize( true );
        recyclerview_reminds.setAdapter( adapter );
        recyclerview_reminds.setLayoutManager( new LinearLayoutManager( getActivity() ) );

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                //new ItemTouchHelper.SimpleCallback( ItemTouchHelper.DOWN|ItemTouchHelper.UP, ItemTouchHelper.RIGHT ) {
                new ItemTouchHelper.SimpleCallback( 0, ItemTouchHelper.RIGHT ) {  // Disable dragging!!
                    public boolean onMove( RecyclerView recyclerView,
                                                    RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target ) {
//                        final int fromPos = viewHolder.getAdapterPosition();
//                        final int toPos = target.getAdapterPosition();
//                        adapter.swap( fromPos, toPos );
//                        return true;// true if moved, false otherwise
                        return false;
                    }

                    public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction ) {
                        // remove from adapter
                        adapter.remove( viewHolder.getAdapterPosition() );
                        Log.d( LOGTAG, "DISMISS NO." + viewHolder.getAdapterPosition() );
                    }
                } );
        itemTouchHelper.attachToRecyclerView( recyclerview_reminds );
    }

    public interface removeNotifyInterface {
        public void remove( int index );
    }

    public class RemoveNotifierToUI implements RemindsListFragment.removeNotifyInterface {
        @Override
        public void remove( int index ) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    //do your modifications here
                    // for example
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

}
