package apen.recyclerviewloadmore.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import apen.recyclerviewloadmore.R;

/**
 * Created by 002 on 2017/4/7.
 */

public class PullLoadMoreRecyclerView extends LinearLayout {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    //    是否下拉刷新  下拉加载
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private LinearLayout mFooterView;
    private LinearLayout loadMoreLaout;
    private boolean pullRefreshEnable = true;
    private boolean pushRefreshEnable = true;
    private PullLoadMoreListener mPullLoadMoreListener;
    private boolean hashMore = true;
    private TextView loadMoreText;

    public PullLoadMoreRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public PullLoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private Context mContext;

    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.pull_loadmore_layout, null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
//        设置进度动画的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutOnRefresh(this));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        定义是否绘制滚动条
        mRecyclerView.setVerticalScrollBarEnabled(true);
//        adapter的改变是否影响RecyclerView的大小
        mRecyclerView.setHasFixedSize(true);
//       默认动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        滑动监听
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScroll(this));
//        事件监听
        mRecyclerView.setOnTouchListener(new onTouchRecyclerView());
//       上拉加载更多要显示的布局
        mFooterView = (LinearLayout) view.findViewById(R.id.footerView);

        loadMoreLaout = (LinearLayout) view.findViewById(R.id.loadMoreLayout);
        loadMoreText = (TextView) view.findViewById(R.id.loadMoreText);

        mFooterView.setVisibility(View.GONE);

        this.addView(view);

    }


    /**
     * 设置RecyclerView管理器
     * LinearLayoutManager
     */
    public void setLinearLayout() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * GridLayoutManager
     */
    public void setGridLayout(int spanCount) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCount);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    /**
     * StaggeredGridLayoutManager
     */
    public void setStaggeredLayout(int spanCount) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }

    /**
     * 获取RecyclerView管理器
     *
     * @return
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
    /**
     * 设置RecyclerView动画
     *
     * @param animator
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    /**
     * 设置RecyclerView分割线
     *
     * @param itemDecoration
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * @param itemDecoration
     * @param index
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecyclerView.addItemDecoration(itemDecoration, index);
    }

    /**
     * 设置RecyclerView滑动到第一位
     */
    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * RecyclerView设置适配器
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    /**
     * 设置是否可以下拉刷新
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        pullRefreshEnable = enable;
        setSwipeRefreshEnable(enable);
    }

    public boolean getPullRefreshEnable() {
        return pullRefreshEnable;
    }

    /**
     * 设置是否可以下拉刷新（SwipeRefresh）
     *
     * @param enable
     */
    public void setSwipeRefreshEnable(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    /**
     * get
     */
    public boolean getSwipeRefreshEnable() {
        return mSwipeRefreshLayout.isEnabled();
    }

    /**
     * 设置进度动画的颜色
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(int... colorResIds) {
        mSwipeRefreshLayout.setColorSchemeResources(colorResIds);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    /**
     * 设置下拉刷新是否可见
     * @param isRefreshing
     */
    public void setRefreshing(final boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (pullRefreshEnable) {
                    mSwipeRefreshLayout.setRefreshing(isRefreshing);
                }
            }
        });
    }

    /**
     * Solve IndexOutOfBoundsException exception
     */
    public class onTouchRecyclerView implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return isRefresh || isLoadMore;
        }
    }

    /**
     * 设置是否可以上拉加载更多
     *
     * @param pushRefreshEnable
     */
    public void setPushRefreshEnable(boolean pushRefreshEnable) {
        this.pushRefreshEnable = pushRefreshEnable;
    }

    public boolean getPushRefreshEnable() {
        return pushRefreshEnable;
    }

    /**
     * 获取上拉加载更多的view
     *
     * @return
     */
    public LinearLayout getFooterViewLayout() {
        return loadMoreLaout;
    }


    public void setFooterViewText(CharSequence text) {
        loadMoreText.setText(text);
    }
    public void setFooterViewText(int resid) {
        loadMoreText.setText(resid);
    }


    public void setFooterViewTextColor(int color) {
        loadMoreText.setTextColor(ContextCompat.getColor(mContext, color));
    }

    /**
     * 设置上拉加载更多view的背景颜色
     *
     * @param color
     */
    public void setFooterViewBackgroundColor(int color) {
        loadMoreLaout.setBackgroundColor(ContextCompat.getColor(mContext, color));
    }

    /**
     *下拉刷新
     */
    public void refresh() {
        if (mPullLoadMoreListener != null){
            mPullLoadMoreListener.onRefresh();
        }
    }

    /**
     * 上拉加载更多
     */
    public void loadMore(){
        if (mPullLoadMoreListener != null && hashMore){
            mFooterView
                    .animate()
                    .translationY(0)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mFooterView.setVisibility(View.VISIBLE);
                        }
                    }).start();
            invalidate();
            mPullLoadMoreListener.onLoadMore();
        }
    }


    public void setPullLoadMoreCompleted(){
        isRefresh = false;
        setRefreshing(false);

        isLoadMore = false;
        mFooterView.animate()
                .translationY(mFooterView.getHeight())
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }

    public boolean isLoadMore(){
        return isLoadMore;
    }

    public void setIsLoadMore(boolean isLoadMore){
        this.isLoadMore = isLoadMore;
    }

    public boolean isRefresh(){
        return isRefresh;
    }

    public void setIsRefresh(boolean isRefresh){
        this.isRefresh = isRefresh;
    }

    public boolean isHasMore(){
        return hashMore;
    }

    public void setOnPullLoadMoreListener(PullLoadMoreListener listener) {
        mPullLoadMoreListener = listener;
    }
    //    自定义接口 实现 下拉刷新 上拉加载
    public interface PullLoadMoreListener {

        void onRefresh();

        void onLoadMore();

    }


}
