package homebrew.fuyuri_seiji.org.conditionalreminder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by sbbg on 5/2/2016 AD.
 */
public class DefaultItemTouchHelper extends ItemTouchHelper.Callback {

    private final DefaultItemTouchHelper.DefaultItemTouchHelperInterface adapter;

    public DefaultItemTouchHelper( DefaultItemTouchHelper.DefaultItemTouchHelperInterface adapter_in ) { adapter = adapter_in; }

    @Override
    public int getMovementFlags( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder ) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }


    @Override
    public boolean onMove( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target ) {
        adapter.onItemMoved( viewHolder.getAdapterPosition(), target.getAdapterPosition() );
        return false;
    }

    @Override
    public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction ) {
        adapter.onItemDismissed( viewHolder.getAdapterPosition() );
    }

    public interface DefaultItemTouchHelperInterface {
        boolean onItemMoved( int index_ori, int index_new );
        void onItemDismissed( int index );
    }

    public interface OnStartDraggingInterface {
        void onStartDragging( RecyclerView.ViewHolder viewHolder );
    }
}
