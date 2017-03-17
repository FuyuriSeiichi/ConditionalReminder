package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sbbg on 5/2/2016 AD.
 */
public class EditCondsListFragment extends Fragment implements DefaultItemTouchHelper.OnStartDraggingInterface {

    private ItemTouchHelper itemTouchHelper;
    public CondsAdapter adapter;

    public EditCondsListFragment() {}
    
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        return new RecyclerView( container.getContext() );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FOR FAKE/TEST
        //CondsAdapter adapter = new CondsAdapter( getActivity() );
        adapter = new CondsAdapter( getActivity(), getArguments().getString( "name" ),
                getArguments().getDouble( "latitude" ), getArguments().getDouble( "longitude" ) );


        RecyclerView recyclerView = (RecyclerView)view;
        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );

        ItemTouchHelper.Callback callback = new DefaultItemTouchHelper( adapter );
        itemTouchHelper = new ItemTouchHelper( callback );
        itemTouchHelper.attachToRecyclerView( recyclerView );
    }

    @Override
    public void onStartDragging( RecyclerView.ViewHolder viewHolder ) {}
}
