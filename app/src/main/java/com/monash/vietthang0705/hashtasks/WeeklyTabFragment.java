package com.monash.vietthang0705.hashtasks;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class WeeklyTabFragment extends Fragment {
    SimpleDateFormat standardDF = new SimpleDateFormat("dd/MM/yyyy");

    TextView startDate;
    TextView endDate;

    final Calendar weekStart = Calendar.getInstance();
    final Calendar weekEnd = Calendar.getInstance();

    ArrayList< ArrayList<Reminder> > weekReminders;
    ArrayList<DayOfWeekAdapter> weekAdapter;
    ArrayList<RecyclerView> weekView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.week_tab_fragment, container, false);

        /**
         * TODO:
         * inflate everything in the xml
         * get data from reminders array
         * add onClick
         * change data + adapter on Click
         */


        ImageView prevButton = (ImageView) v.findViewById(R.id.week_prev);
        ImageView nextButton = (ImageView) v.findViewById(R.id.week_next);
        startDate = (TextView) v.findViewById(R.id.week_start_date);
        endDate = (TextView) v.findViewById(R.id.week_end_date);

        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getFirstDayOfWeek());
        weekEnd.setTime(weekStart.getTime());
        weekEnd.add(Calendar.DAY_OF_MONTH, 7);

        refreshHeader();

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekStart.add(Calendar.DAY_OF_MONTH, -7);
                weekEnd.add(Calendar.DAY_OF_MONTH, -7);

                // refresh
                refreshHeader();
                refreshReminders();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekStart.add(Calendar.DAY_OF_MONTH, 7);
                weekEnd.add(Calendar.DAY_OF_MONTH, 7);

                // refresh
                refreshHeader();
                refreshReminders();
            }
        });

        // actual tasks within the week
        init_weekReminders(v);

        return v;
    }

    private void init_weekReminders(View v) {
        weekReminders = new ArrayList<>();
        weekAdapter = new ArrayList<>();
        weekView = new ArrayList<>();

        weekView.add((RecyclerView) v.findViewById(R.id.week_tasks_sun));
        weekView.add((RecyclerView) v.findViewById(R.id.week_tasks_mon));
        weekView.add((RecyclerView) v.findViewById(R.id.week_tasks_tue));
        weekView.add((RecyclerView) v.findViewById(R.id.week_tasks_wed));
        weekView.add((RecyclerView) v.findViewById(R.id.week_tasks_thu));
        weekView.add((RecyclerView) v.findViewById(R.id.week_tasks_fri));
        weekView.add((RecyclerView) v.findViewById(R.id.week_tasks_sat));

        for (int i=0; i<7; i++) {
            weekReminders.add(new ArrayList<Reminder>());
            weekAdapter.add(new DayOfWeekAdapter(getContext(), weekReminders.get(i), i));
            weekView.get(i).setAdapter(weekAdapter.get(i));

            final int cur = i;
            weekView.get(i).addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    /**
                     * TODO:
                     * same with scrollTo in month
                     */

                    MainFragment.getInstance().getTabHost().setCurrentTabByTag("Daily");
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    int id = rv.getChildAdapterPosition(child);

                    if (id < 0)
                        return false;

                    for (int j=0; j<MainActivity.reminders.size(); j++) {
                        if (MainActivity.reminders.get(j).getId() == weekReminders.get(cur).get(id).getId()) {
                            DailyTabFragment.rvReminders.getLayoutManager().scrollToPosition(j);
                            Log.d("WEEKLY", "touched " + j);
                            break;
                        }
                    }

                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
        }

        refreshReminders();
    }

    private void refreshReminders() {
        for (int i=0; i<7; i++) {
            weekReminders.get(i).clear();
            weekAdapter.get(i).notifyDataSetChanged();
        }

        for (int i=0; i<MainActivity.reminders.size(); i++) {
            Reminder r = MainActivity.reminders.get(i);
            if (r.getDate().compareTo(weekStart.getTime()) >= 0 &&
                r.getDate().compareTo(weekEnd.getTime()) <= 0) {

                weekReminders.get(r.getDate().getDay()).add(r);
                Log.d("WEEKLY", "start: " + weekStart.getTime());
                Log.d("WEEKLY", "this: " + r.getDate());
                Log.d("WEEKLY", "end: " + weekEnd.getTime());
            }
        }

        for (int i=0; i<7; i++) {
            weekAdapter.get(i).notifyDataSetChanged();
        }
    }

    private void refreshHeader() {
        startDate.setText(standardDF.format(weekStart.getTime()));
        endDate.setText(standardDF.format(weekEnd.getTime()));
    }
}
