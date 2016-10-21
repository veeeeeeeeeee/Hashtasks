package com.monash.vietthang0705.hashtasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class MonthlyTabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.month_tab_fragment, container, false);

        CustomCalendarView cv = (CustomCalendarView) v.findViewById(R.id.view_month);

        HashSet<Date> events = new HashSet<>();
        for (int i=0; i<MainActivity.reminders.size(); i++) {
            Reminder r = MainActivity.reminders.get(i);
            events.add(r.getDate());
        }
        cv.setEvents(events);
        cv.update();

        return v;
    }
}
