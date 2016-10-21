package com.monash.vietthang0705.hashtasks;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_REMINDER_REQUEST = 1;

    private static ManageTasksFragment manageTasksFragment;
    private static MainFragment mainFragment;
    private static ViewTaskFragment viewTaskFragment;

    private static Context context;
    public static Context getMainContext() {
        return MainActivity.context;
    }

    public static ViewTaskFragment getViewTaskFragment() {
        return viewTaskFragment;
    }
    public static MainFragment getMainFragment() {
        return mainFragment;
    }

    public static ArrayList<Reminder> reminders;
    public static ReminderMainAdapter adapter;
    public static DatabaseHelper dbHelper;

    public static ArrayList<Reminder> mng_reminders;


    /**
     * TODO:
     * Override action menu
     * onClick calling Add Activity
     * onResult
     */


    // ADD button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    public static SectionsPagerAdapter getPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;
    public static ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        dbHelper = new DatabaseHelper(getApplicationContext());
        reminders = dbHelper.getReminders();
        adapter = new ReminderMainAdapter(getApplicationContext(), reminders);

        Log.d("MAIN_ONCREATE", "dbHelper "+ dbHelper.getReminders().size());
        // result: dbHelper.addReminder is working

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public int currentListItem;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

            Window w = getWindow();
            switch (position) {
                case 0:
                    return ManageTasksFragment.newInstance();
                case 1:
                    return MainFragment.newInstance();
                case 2:
                    return ViewTaskFragment.newInstance();
                default:
                    return MainFragment.newInstance();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int pos) {
            Fragment generated = (Fragment) super.instantiateItem(container, pos);

            switch (pos) {
                case 0:
                    manageTasksFragment = (ManageTasksFragment) generated;
                    break;
                case 1:
                    mainFragment = (MainFragment) generated;
                    break;
                case 2:
                    viewTaskFragment = (ViewTaskFragment) generated;
                    break;
            }

            return generated;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    // ADD button onClick
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent i = new Intent(this, AddReminder.class);
            startActivityForResult(i, ADD_REMINDER_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REMINDER_REQUEST) {
            /**
             * TODO:
             * get parcel r from AddReminder
             * add into list
             * notify adapter
             * add into dB
             */
            if (resultCode == RESULT_OK) {
                Reminder r = data.getParcelableExtra("result");
                r.setListId(reminders.size());

                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);
                r.setLoc(new LatLng(lat, lng));

                reminders.add(r);
                Collections.sort(reminders);
                refreshListItemId();
                dbHelper.addReminder(r);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public static void refreshListItemId() {
        for (int i=0; i<reminders.size(); i++) {
            reminders.get(i).listId = i;
        }
        dbHelper.lastItem = reminders.size();
    }


    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
