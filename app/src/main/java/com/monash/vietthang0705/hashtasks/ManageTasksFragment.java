package com.monash.vietthang0705.hashtasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class ManageTasksFragment extends Fragment {

    private static ManageTasksFragment _instance;
    private static boolean instanceExist = false;

    ReminderManageAdapter adapter;
    ArrayList<Reminder> mngReminders;

    final Calendar startDate = Calendar.getInstance();
    final Calendar endDate = Calendar.getInstance();
    static int statusToggle;
    boolean selectedTagsCondition;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    DatePickerDialog.OnDateSetListener startPick = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker v, int yy, int mm, int dd) {
            startDate.set(Calendar.YEAR, yy);
            startDate.set(Calendar.MONTH, mm);
            startDate.set(Calendar.DAY_OF_MONTH, dd);

            mngViewStartDate.setText(sdf.format(startDate.getTime()));
        }
    };

    DatePickerDialog.OnDateSetListener endPick = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker v, int yy, int mm, int dd) {
            endDate.set(Calendar.YEAR, yy);
            endDate.set(Calendar.MONTH, mm);
            endDate.set(Calendar.DAY_OF_MONTH, dd);

            mngViewEndDate.setText(sdf.format(endDate.getTime()));
        }
    };

    static EditText mngViewTagsFilter;
    static TextView mngViewStartDate;
    static TextView mngViewEndDate;
    static LinearLayout mngViewToggleStatus;
    static ImageView mngViewToggle;
    static TextView mngViewToggleText;
    static TextView mngViewAllSelect;
    static TextView mngViewOneSelect;
    static ImageView mngViewTrigger;

    // for toggling view
    static LinearLayout mngViewToggleFilter;
    static LinearLayout mngViewFilter;
    static boolean toggleFilter;
    static ImageView mngViewToggleIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manage_tasks, container, false);

        mngReminders = new ArrayList<>();
        adapter = new ReminderManageAdapter(getContext(), mngReminders);

        for (int i=0; i<MainActivity.reminders.size(); i++) {
            mngReminders.add(new Reminder());
            mngReminders.set(i, MainActivity.reminders.get(i));
        }
        adapter.notifyDataSetChanged();

        final RecyclerView rvMngReminder = (RecyclerView) v.findViewById(R.id.rv_mng_reminder);
        rvMngReminder.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        rvMngReminder.setLayoutManager(llm);

        final GestureDetector mgd = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return true;
            }
        });

        rvMngReminder.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvMngReminder, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != -1) {
                    Reminder currentReminder = mngReminders.get(position);
                    ViewTaskFragment.refreshReminder(currentReminder, currentReminder.getListId());

                    Log.d("MANAGE", ""+ ViewTaskFragment.current.getTags().get(0));

                    Fragment viewTaskFragment = MainActivity.getViewTaskFragment();
                    View v = viewTaskFragment.getView();

                    MainActivity.getViewPager().setCurrentItem(2);
                    ViewTaskFragment.rePopulateView(viewTaskFragment.getContext(), v);
                }
            }
        }));

        // search/filters functionality

        mngViewTagsFilter = (EditText) v.findViewById(R.id.mng_task_search);
        mngViewStartDate = (TextView) v.findViewById(R.id.mng_start_date);
        mngViewEndDate = (TextView) v.findViewById(R.id.mng_end_date);
        mngViewToggleStatus = (LinearLayout) v.findViewById(R.id.mng_toggle_status);
        mngViewToggleText = (TextView) v.findViewById(R.id.mng_toggle_text);
        mngViewToggle = (ImageView) v.findViewById(R.id.mng_collapse_icon);
        mngViewAllSelect = (TextView) v.findViewById(R.id.mng_all_tags);
        mngViewOneSelect = (TextView) v.findViewById(R.id.mng_one_task);
        mngViewTrigger = (ImageView) v.findViewById(R.id.mng_trigger);
        mngViewToggleFilter = (LinearLayout) v.findViewById(R.id.mng_open);
        mngViewFilter = (LinearLayout) v.findViewById(R.id.mng_filter);
        mngViewToggleIcon = (ImageView) v.findViewById(R.id.mng_toggle_icon);

        startDate.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getFirstDayOfWeek());
        endDate.setTime(startDate.getTime());
        endDate.add(Calendar.DAY_OF_MONTH, 7);
        statusToggle = 0;
        selectedTagsCondition = false; // for all tags, true for one of the tags

        // initialise
        mngViewStartDate.setText(sdf.format(startDate.getTime()));
        mngViewEndDate.setText(sdf.format(endDate.getTime()));

        // date picking
        mngViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), startPick,
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mngViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), endPick,
                        endDate.get(Calendar.YEAR),
                        endDate.get(Calendar.MONTH),
                        endDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mngViewToggleStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusToggle == 0) {// not chosen, switch to 1 (green)
                    mngViewToggleText.setTextColor(Color.parseColor("#6ACA6B"));
                    mngViewToggleIcon.setImageResource(R.drawable.ic_cab_done_holo_green);
                    statusToggle = 1;
                }
                else if (statusToggle == 1) { // completed, switch to -1 (red)
                    mngViewToggleText.setTextColor(Color.parseColor("#C81E1E"));
                    mngViewToggleIcon.setImageResource(R.drawable.ic_clear_mtrl_red);
                    statusToggle = -1;
                }
                else { // not completed, switch to 0 (gray)
                    mngViewToggleText.setTextColor(Color.parseColor("#080808"));
                    mngViewToggleIcon.setImageResource(R.drawable.btn_circle_normal);
                    statusToggle = 0;
                }
            }
        });

        mngViewAllSelect.setTextColor(Color.parseColor("#6ACA6B"));
        mngViewAllSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTagsCondition = false; // all tags
                mngViewAllSelect.setTextColor(Color.parseColor("#6ACA6B"));
                mngViewOneSelect.setTextColor(Color.parseColor("#080808"));
            }
        });

        mngViewOneSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTagsCondition = true; // all tags
                mngViewOneSelect.setTextColor(Color.parseColor("#6ACA6B"));
                mngViewAllSelect.setTextColor(Color.parseColor("#080808"));
            }
        });

        mngViewTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter(v);
            }
        });

        toggleFilter = true; // open
        mngViewToggle.setImageResource(R.drawable.ic_btn_search_up);
        mngViewToggleFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleFilter) {
                    mngViewToggle.setImageResource(R.drawable.ic_btn_search_down);
                    MainActivity.collapse(mngViewFilter);
                }
                else {
                    mngViewToggle.setImageResource(R.drawable.ic_btn_search_up);
                    MainActivity.expand(mngViewFilter);
                }
                toggleFilter = !toggleFilter;
            }
        });

        return v;
    }

    private void filter(View v) {
        /**
         * TODO:
         * loop through mngReminders, weed out:
         *  tags, string proc
         *  start/ endDate
         *  status
         *  all tags
         */

        mngReminders.clear();
        for (int i=0; i<MainActivity.reminders.size(); i++) {
            mngReminders.add(new Reminder());
            mngReminders.set(i, MainActivity.reminders.get(i));
        }

        ArrayList<String> entered;
        String raw = mngViewTagsFilter.getText().toString();

        if (raw.length() == 0)
            entered = new ArrayList<>();
        else entered = new ArrayList<String>(Arrays.asList(raw.split(" ")));

        for (int i=mngReminders.size()-1; i>=0; i--) {
            // filter condition
            Reminder r = mngReminders.get(i);
            //Log.d("MANAGE_FILTER", r.containAll(entered) + " " + r.containOne(entered));

            if (!raw.isEmpty()) {
                if (!selectedTagsCondition) { // all tags
                    if (!r.containAll(entered)) {
                        mngReminders.remove(i);
                        Log.d("FILTER", "remove_tag " + i);
                        continue;
                    }
                } else { // one of the tgs
                    if (!r.containOne(entered)) {
                        mngReminders.remove(i);
                        Log.d("FILTER", "remove_tag " + i);
                        continue;
                    }
                }
            }

            if (statusToggle == -1) { // not completed
                if (r.getStatus()) {
                    Log.d("FILTER", "remove_status " + i);
                    mngReminders.remove(i);
                    continue;
                }
            }
            else if (statusToggle == 1) { // completed
                if (!r.getStatus()) {
                    Log.d("FILTER", "remove_status " + i);
                    mngReminders.remove(i);
                    continue;
                }
            }

            if (r.getDate().compareTo(startDate.getTime()) < 0 ||
                r.getDate().compareTo(endDate.getTime()) > 0) {

                Log.d("FILTER", "remove_date " + i);

                mngReminders.remove(i);
            }
        }
        //Log.d("MANAGE_FILTER", "after filtered " + mngReminders.size());

        adapter.notifyDataSetChanged();
        InputMethodManager imm = (InputMethodManager) MainActivity.getMainContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static ManageTasksFragment getInstance() {
        if (!instanceExist)
            return newInstance();
        else return _instance;
    }

    public static ManageTasksFragment newInstance() {
        ManageTasksFragment _instance = new ManageTasksFragment();
        Bundle b = new Bundle();

        _instance.setArguments(b);
        instanceExist = true;
        return _instance;
    }
}
