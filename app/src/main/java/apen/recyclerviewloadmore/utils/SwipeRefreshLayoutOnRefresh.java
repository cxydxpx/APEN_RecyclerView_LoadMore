package apen.recyclerviewloadmore.utils;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by 002 on 2017/4/7.
 */

public class SwipeRefreshLayoutOnRefresh implements SwipeRefreshLayout.OnRefreshListener {

    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    public SwipeRefreshLayoutOnRefresh(PullLoadMoreRecyclerView pullLoadMoreRecyclerView) {
        this.mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
    }

    @Override
    public void onRefresh() {
        if (!mPullLoadMoreRecyclerView.isRefresh()) {
            mPullLoadMoreRecyclerView.setIsRefresh(true);
            mPullLoadMoreRecyclerView.refresh();
        }
    }
}
