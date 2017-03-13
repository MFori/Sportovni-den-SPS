package cz.sps_pi.sportovni_den.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Martin Forejt on 21.01.2017.
 * forejt.martin97@gmail.com
 * <p>
 * RecyclerView class for setting inside fragment which is inside swipeRefreshLayout
 */
public class ResultRecyclerView extends RecyclerView {

    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * @param context             Context
     * @param swipeRefreshLayout1 layout of fragment parent
     */
    public ResultRecyclerView(Context context, final SwipeRefreshLayout swipeRefreshLayout1) {
        super(context);
        this.swipeRefreshLayout = swipeRefreshLayout1;
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (swipeRefreshLayout.isRefreshing()) return;
                if (listIsAtTop()) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    /**
     * Is scrolled at top
     *
     * @return is?
     */
    private boolean listIsAtTop() {
        if (getChildAt(0) == null) return true;
        if (((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0)
            return true;
        return getChildAt(0).getTop() == 0;
    }

    /**
     * On recycler view visibility change
     */
    public void reVisible() {
        if (!listIsAtTop()) {
            swipeRefreshLayout.setEnabled(false);
        } else {
            swipeRefreshLayout.setEnabled(true);
        }
    }

}
