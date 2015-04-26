package com.stone.pulltorefresh;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stone.pulltorefresh.adapter.ImgAdapter;
import com.stone.pulltorefresh.model.Photo;
import com.stone.pulltorefresh.util.FinalValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class MyLvPullPhotoActivity extends Activity {

    private PullToRefreshListView mPullRefreshListView;

    static final int CLEAR_MEMORY_CACHE = 0;
    static final int CLEAR_DISK_CACHE = 1;

    private ImgAdapter mAdapter;
    private LinkedList<Photo> mPhotoList;
    private List<String> mUrlList;
    private int mPageNum = 0; //页码
    private int mPageItem = 5; //每页显示的个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lv_pull_photo);

        mPhotoList = new LinkedList<Photo>();
        mUrlList = FinalValue.getImgUrls();
        loadPhotosData(++mPageNum);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mAdapter = new ImgAdapter(this, mPhotoList);
        mPullRefreshListView.setAdapter(mAdapter);

        /*
        Mode.BOTH：同时支持上拉下拉
        Mode.PULL_FROM_START：只支持下拉Pulling Down
        Mode.PULL_FROM_END：只支持上拉Pulling Up
         */
//        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);//也可以使用自定义属性

        mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
        mPullRefreshListView.getLoadingLayoutProxy().setReleaseLabel("释放后开始加载");

        //如果想上拉、下拉刷新的时候 做一样的操作，那就用OnRefreshListener，上拉下拉的时候都调用
       /* mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                System.out.println("onRefresh 刷新");

                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel  最后更新于
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新于" + label);
                new GetDataTask().execute();
            }

        });*/
        //如果想上拉、下拉做不一样的的操作
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                System.out.println("onPullDownToRefresh 加载最新");
                mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("到顶了-下拉刷新");
                updateLastTimeLabel(refreshView);
                new GetNewestDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                System.out.println("onPullUpToRefresh 加载更多");
                mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("到底了-上拉加载更多");
                updateLastTimeLabel(refreshView);
                new GetMoreDataTask().execute();
            }
        });

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);
    }

    private void updateLastTimeLabel(PullToRefreshBase<ListView> refreshView) {
        String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        // Update the LastUpdatedLabel  最后更新于
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新于" + label);
    }


    private boolean loadPhotosData(int pageNum) {
        Photo photo;
        /*
        模拟分页请求，一次请求一页，
         */
        int startItem = (pageNum - 1) * mPageItem; //开始item
        if (startItem >= mUrlList.size()) return false;

        int lastItem = startItem + mPageItem > mUrlList.size() ? mUrlList.size() : startItem + mPageItem; //结束item
        for (; startItem < lastItem; startItem++) {
            String url = mUrlList.get(startItem);
            photo = new Photo("desc" + url.substring(url.lastIndexOf("/") + 1), url);
            mPhotoList.addLast(photo);
        }
        return  true;
    }

    static int newest = 0;
    private boolean loadNewestPhotosData() {
        if (new Random().nextBoolean()) return false;
        mPhotoList.addFirst(new Photo("最新的" + (++newest), mUrlList.get(0)));
        return true;
    }

    /**
     * 加载更多
     */
    private class GetMoreDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return loadPhotosData(++mPageNum);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) return;

            mAdapter.notifyDataSetChanged();
            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    /**
     * 加载最新
     */
    private class GetNewestDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return loadNewestPhotosData();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { //如果没有最新
                Toast.makeText(getApplicationContext(), "当前已经最新了", Toast.LENGTH_SHORT).show();
            } else {
                mAdapter.notifyDataSetChanged();
            }

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().stop();
//        ImageLoader.getInstance().destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CLEAR_MEMORY_CACHE, 0, "清除内存缓存");
        menu.add(0, CLEAR_DISK_CACHE, 1, "清静磁盘缓存");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CLEAR_MEMORY_CACHE:
                ImageLoader.getInstance().clearMemoryCache();
                break;
            case CLEAR_DISK_CACHE:
                ImageLoader.getInstance().clearDiskCache();
                break;
        }
        return true;
    }
}
