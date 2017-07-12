package com.android.slide.activity.mainactivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.slide.widget.LeFragmentSlidePagerAdapter;
import com.android.slide.widget.LeSlide;
import com.android.slide.widget.LeSlideConfig;
import com.android.slide.widget.LeSlideInterface;
import com.android.slide.widget.LeSlidePager;
import com.android.slide.widget.LeSlidePagerAdapter;


public class ScreenSlideActivity extends FragmentActivity {
    private static final int NUM_PAGES = 5;

    private LeSlidePager mPager;
    private LeSlidePagerAdapter mPagerAdapter;
    private LeSlideInterface slideInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        LeSlideConfig config = new LeSlideConfig.Builder().edge(true).build();
        slideInterface = LeSlide.attach(this, config);

        mPager = (LeSlidePager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
//        mPager.setAdapter(mPagerAdapter);
        mPager.setFragmentManager(getFragmentManager());
        mPager.push(ScreenSlidePageFragment.create(mPager.getCurrentItem()));
        mPager.setOnPageChangeListener(new LeSlidePager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions
                invalidateOptionsMenu();
                if (mPager.getCurrentItem() == 0) {
                    slideInterface.unlock();
                }
                Log.e("gnodss", "page selected: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.e("gnodss", "page state: " + state);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
//                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                mPager.pop();
                return true;

            case R.id.action_next:
//                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                mPager.push(ScreenSlidePageFragment.create(mPager.getCurrentItem() + 1));
                slideInterface.lock();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ScreenSlidePagerAdapter extends LeFragmentSlidePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() > 0) {
            mPager.pop();
            return;
        } else {
            slideInterface.onBackPressed();
        }
//        super.onBackPressed();
    }
}
