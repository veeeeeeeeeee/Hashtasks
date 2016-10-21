package com.monash.vietthang0705.hashtasks;

import android.gesture.Gesture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class DailyTabFragment extends Fragment {

    public static int cnt = 0;
    public static RecyclerView rvReminders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise stuff before inflating view with onCreateView
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.day_tab_fragment, container, false);
        rvReminders = (RecyclerView) v.findViewById(R.id.rv_reminder);

        Collections.sort(MainActivity.reminders);
        MainActivity.refreshListItemId();

        if (MainActivity.reminders.isEmpty()) {
            ViewTaskFragment.isExist = false;
        }
        else ViewTaskFragment.isExist = true;

        rvReminders.setAdapter(MainActivity.adapter);

        final GestureDetector mgd = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        rvReminders.setLayoutManager(llm);

        rvReminders.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // ArrayList<Reminder> toAdd = Reminder.generateList(10);
                ArrayList<Reminder> toAdd = MainActivity.dbHelper.getReminders();

                int sz = MainActivity.adapter.getItemCount();
                MainActivity.reminders.addAll(toAdd);

                MainActivity.refreshListItemId();
                MainActivity.adapter.notifyItemRangeInserted(sz, MainActivity.reminders.size());
            }
        });

        rvReminders.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            boolean requested = false;
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (mgd.onTouchEvent(e)) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());

                    int current = rv.getChildAdapterPosition(child);
                    if (current != ViewTaskFragment.currentId && current != -1) { // temp resort for holder == null
                        Reminder currentReminder = MainActivity.reminders.get(current);
                        ViewTaskFragment.refreshReminder(currentReminder, current);

                        Fragment viewTaskFragment = MainActivity.getViewTaskFragment();
                        View v = viewTaskFragment.getView();

                        ViewTaskFragment.addOpen = true;
                        ViewTaskFragment.rePopulateView(viewTaskFragment.getContext(), v);
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                requested = disallowIntercept;
                Log.d("DAILY", "requested " + requested);
            }
        });

        return v;
    }

    public void randomiseItem(ArrayList<Reminder> rl) {
        for (int i=0; i<rl.size(); i++) {
            rl.get(i).randomise();
        }
    }
}
