package com.monash.vietthang0705.hashtasks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Attr;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by vietthang.0705 on 10-Jun-16.
 */
public class CustomCalendarView extends LinearLayout {
    private ArrayList<Date> cells;

    private LinearLayout header;
    private TextView dateDisplay;
    private GridView grid;
    ImageView prev;
    ImageView next;

    private Calendar currentDate = Calendar.getInstance();

    private HashSet<Date> events;

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init_cc(context, attrs);
    }

    public void setEvents(HashSet<Date> _events) {
        events = _events;
    }

    private void init_cc(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        setEvents(null);

        prev = (ImageView) findViewById(R.id.calendar_prev);
        next = (ImageView) findViewById(R.id.calendar_next);
        header = (LinearLayout) findViewById(R.id.calendar_header);
        dateDisplay = (TextView) findViewById(R.id.calendar_date_display);
        grid = (GridView) findViewById(R.id.calendar_grid);

        prev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                update();
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                update();
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View cell, int pos, long id) {
                // switch to dailytab
                // parentView.getItemAtPosition(pos);

                boolean hasEvent = false;
                int rEvent = 0;

                // cells.get(pos);

                Date tapped = cells.get(pos);
                Date rDate = new Date();
                for (int i=0; i<MainActivity.reminders.size(); i++) {
                    rDate = MainActivity.reminders.get(i).getDate();
                    if (tapped.getYear() == rDate.getYear() &&
                        tapped.getMonth() == rDate.getMonth() &&
                        tapped.getDate() == rDate.getDate()) {

                        hasEvent = true;
                        rEvent = i;

                        break;
                    }
                }

                final int rPos = rEvent;
                MainFragment.getInstance().getTabHost().setCurrentTabByTag("Daily");
                if (hasEvent) {
                    DailyTabFragment.rvReminders.getLayoutManager().scrollToPosition(rPos);
                }
            }
        });

        update();
    }

    public void update() {
        cells = new ArrayList<>();
        Calendar c = (Calendar) currentDate.clone();

        c.set(Calendar.DAY_OF_MONTH, 1);
        int firstCell = c.get(Calendar.DAY_OF_WEEK) -1;

        c.add(Calendar.DAY_OF_MONTH, -firstCell);

        while (cells.size() < 42) {
            cells.add(c.getTime());
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        dateDisplay.setText(sdf.format(currentDate.getTime()));
    }

    private class CalendarAdapter extends ArrayAdapter<Date> {
        private HashSet<Date> e;

        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> e) {
            super(context, R.layout.control_calendar_day, days);

            this.e = e;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int pos, View v, ViewGroup parent) {
            Date date = getItem(pos);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();

            Date today = new Date();

            if (v == null) {
                v = inflater.inflate(R.layout.control_calendar_day, parent, false);
            }

            ((TextView) v).setTypeface(null, Typeface.NORMAL);
            ((TextView) v).setTextColor(Color.BLACK);
            if (month != today.getMonth() || year != today.getYear()) {
                ((TextView) v).setTextColor(Color.parseColor("#808080"));
            }

            else if (day == today.getDate()) {
                ((TextView) v).setTypeface(null, Typeface.BOLD);
                ((TextView) v).setTextColor(Color.parseColor("#990000"));
            }

            if (e != null) {
                for (Date event : e) {
                    if (event.getDate() == day &&
                        event.getMonth() == month &&
                        event.getYear() == year) {

                        //v.setBackgroundColor(Color.rgb(100, 194, 223));
                        v.setBackground(getResources().getDrawable(R.drawable.tag_shape));
                        ((TextView) v).setTextColor(Color.WHITE);
                    }
                }
            }

            ((TextView) v).setText(String.valueOf(date.getDate()));

            return v;
        }
    }
}
