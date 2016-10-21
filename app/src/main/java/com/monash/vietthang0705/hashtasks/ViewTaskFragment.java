package com.monash.vietthang0705.hashtasks;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class ViewTaskFragment extends Fragment {
    private static ViewTaskFragment _instance;
    private static boolean instanceExist = false;

    // manage current Reminder being showed
    public static boolean isExist;
    public static int currentId;
    public static Reminder current;

    static boolean marked;

    // view elements
    static RelativeLayout itemViewCheck; // rStatus
    static ImageView itemViewMark; // tick
    static TextView itemViewMarkText;

    static LinearLayout itemViewTags; // rTags
    static EditText itemViewAddField; // add into rTags
    static Button itemViewAddButton; // invoke adding
    static LinearLayout itemViewAdd; // to disappear
    static ImageView itemViewAddIcon;
    static LinearLayout itemViewToggle; // invoke disappearing itemViewAdd

    static boolean addOpen;

    static GoogleMap googleMap;
    static MapView itemViewMap;
    static EditText itemViewDate; // rDate
    static EditText itemViewStart; // rStart
    static EditText itemViewFinish; // rFinish
    static EditText itemViewDesc; // rDesc

    static Button itemViewSave;
    static Button itemViewDel;

    // DatePicker for input Date
    Calendar picker;
    DatePickerDialog.OnDateSetListener datePick = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker v, int yy, int mm, int dd) {
            picker.set(Calendar.YEAR, yy);
            picker.set(Calendar.MONTH, mm);
            picker.set(Calendar.DAY_OF_MONTH, dd);

            itemViewDate.setText(Reminder.df2.format(picker.getTime()));
            current.setDate(picker.getTime());
        }
    };

    // TimePicker
    TimePickerDialog.OnTimeSetListener startPick = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            picker.set(Calendar.HOUR, hourOfDay);
            picker.set(Calendar.MINUTE, minute);

            itemViewStart.setText(Reminder.tf.format(picker.getTime()));

            Calendar tmp = Calendar.getInstance();
            tmp.set(Calendar.HOUR, hourOfDay);
            tmp.set(Calendar.MINUTE, minute);
            current.setStart(tmp);
        }
    };

    TimePickerDialog.OnTimeSetListener finishPick = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            picker.set(Calendar.HOUR, hourOfDay);
            picker.set(Calendar.MINUTE, minute);

            itemViewFinish.setText(Reminder.tf.format(picker.getTime()));

            Calendar tmp = Calendar.getInstance();
            tmp.set(Calendar.HOUR, hourOfDay);
            tmp.set(Calendar.MINUTE, minute);
            current.setFinish(tmp);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isExist = false;
        currentId = -1;
        current = new Reminder();

        picker = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_task_fragment, container, false);

        itemViewCheck = (RelativeLayout) v.findViewById(R.id.view_item_check);
        itemViewMark = (ImageView) v.findViewById(R.id.view_item_mark);
        itemViewMarkText = (TextView) v.findViewById(R.id.view_item_mark_text);

        itemViewMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!marked) {
                    itemViewMark.setImageResource(R.drawable.ic_cab_done_holo_dark);
                    itemViewMarkText.setText("Completed");
                    itemViewMarkText.setTextColor(Color.WHITE);
                    itemViewCheck.setBackgroundColor(Color.rgb(106, 202, 107));
                }
                else {
                    itemViewMark.setImageResource(R.drawable.btn_circle_normal);
                    itemViewMarkText.setText("Mark as Complete");
                    itemViewMarkText.setTextColor(Color.parseColor("#808080"));
                    itemViewCheck.setBackgroundColor(Color.WHITE);
                }
                marked = !marked;
                current.setStatus(marked);
            }
        });

        itemViewTags = (LinearLayout) v.findViewById(R.id.view_item_tag_list);
        itemViewDate = (EditText) v.findViewById(R.id.view_item_field_date);
        itemViewStart = (EditText) v.findViewById(R.id.view_item_field_start);
        itemViewFinish = (EditText) v.findViewById(R.id.view_item_field_finish);
        itemViewDesc = (EditText) v.findViewById(R.id.view_item_desc);

        itemViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), datePick,
                        picker.get(Calendar.YEAR),
                        picker.get(Calendar.MONTH),
                        picker.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        itemViewStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getContext(), startPick,
                        picker.get(Calendar.HOUR),
                        picker.get(Calendar.MINUTE),
                        true).show();
            }
        });

        itemViewFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getContext(), finishPick,
                        picker.get(Calendar.HOUR),
                        picker.get(Calendar.MINUTE),
                        true).show();
            }
        });

        itemViewDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                itemViewDesc.setText("");
            }
        });

        itemViewTags = (LinearLayout) v.findViewById(R.id.view_item_tag_list);

        final GestureDetector mgd = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return true;
            }
        });

        itemViewTags.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mgd.onTouchEvent(event)) {
                    int x = Math.round(event.getX());

                    for (int i=0; i<itemViewTags.getChildCount(); i++) {
                        TextView c = (TextView) itemViewTags.getChildAt(i);
                        if (x > c.getLeft() && x < c.getRight()) {
                            itemViewTags.removeView(c);
                            Log.d("VIEW_TOUCH", c.getText().toString());
                            break;
                        }
                    }
                }

                return true;
            }
        });

        itemViewAddField = (EditText) v.findViewById(R.id.view_item_add_field);
        itemViewAddButton = (Button) v.findViewById(R.id.view_item_add_button);

        itemViewAddButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inp = itemViewAddField.getText().toString();
                if (!inp.isEmpty()) {
                    TextView t = new TextView(getContext());

                    LinearLayout.LayoutParams style = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    style.setMargins(10, 0, 10, 0);

                    t.setPadding(10, 10, 10, 10);
                    t.setLayoutParams(style);
                    String tmp = "#" + inp;
                    t.setText(tmp);
                    t.setTextColor(Color.WHITE);
                    t.setBackground(getResources().getDrawable(R.drawable.tag_shape));

                    itemViewTags.addView(t);
                }

                itemViewAddField.setText("");

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        itemViewSave = (Button) v.findViewById(R.id.view_item_save_task);

        itemViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // current.addTag(inp);
                ArrayList<String> addedTags = new ArrayList<>();
                for (int i=0; i<itemViewTags.getChildCount(); i++) {
                    TextView t = (TextView) itemViewTags.getChildAt(i);

                    String tmp = t.getText().toString().substring(1);
                    addedTags.add(tmp);
                }

                current.setTags(addedTags);
                current.setDesc(itemViewDesc.getText().toString());

                MainActivity.reminders.set(currentId, current);
                Collections.sort(MainActivity.reminders);
                MainActivity.refreshListItemId();
                MainActivity.adapter.notifyDataSetChanged();
                MainActivity.dbHelper.updateReminder(current.getId(), current);
                MainActivity.getViewPager().setCurrentItem(1);

                Log.d("VIEW_SAVE", ""+MainActivity.reminders.get(currentId).getStatus());
            }
        });

        itemViewDel = (Button) v.findViewById(R.id.view_item_del_task);

        itemViewDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete entry")
                        .setMessage("Delete this #task?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete code
                                MainActivity.reminders.remove(currentId);
                                Collections.sort(MainActivity.reminders);
                                MainActivity.refreshListItemId();
                                MainActivity.adapter.notifyDataSetChanged();
                                MainActivity.dbHelper.deleteReminder(current.getId());
                                MainActivity.getViewPager().setCurrentItem(1);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        itemViewAdd = (LinearLayout) v.findViewById(R.id.view_item_add_container);
        itemViewToggle = (LinearLayout) v.findViewById(R.id.view_item_toggle_container);
        itemViewAddIcon = (ImageView) v.findViewById(R.id.view_item_toggle_add);
        addOpen = true;

        itemViewToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addOpen) {
                    itemViewAddIcon.setImageResource(R.drawable.ic_find_next_holo_light);
                    MainActivity.collapse(itemViewAdd);
                }
                else {
                    itemViewAddIcon.setImageResource(R.drawable.ic_find_previous_holo_light);
                    MainActivity.expand(itemViewAdd);
                }
                addOpen = !addOpen;
            }
        });

        itemViewMap = (MapView) v.findViewById(R.id.view_item_map);
        itemViewMap.onCreate(savedInstanceState);

        return v;
    }

    public static ViewTaskFragment getInstance() {
        if (!instanceExist)
            return newInstance();
        else return _instance;
    }

    public static ViewTaskFragment newInstance() {
        _instance = new ViewTaskFragment();
        Bundle b = new Bundle();

        _instance.setArguments(b);
        instanceExist = true;

        return _instance;
    }

    // invoked by DailyTabFragment
    public static void rePopulateView(Context context, View v) {
        // map
        itemViewMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coord = current.getLoc();
                googleMap.addMarker(new MarkerOptions().position(coord));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 15));
                itemViewMap.onResume();
            }
        });

        // tags list
        if (itemViewTags.getChildCount() > 0)
            itemViewTags.removeAllViews();

        ArrayList<String> tags = current.getTags();
        for (int i=0; i<tags.size(); i++) {
            TextView t = new TextView(MainActivity.getMainContext());

            LinearLayout.LayoutParams style = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            style.setMargins(10, 0, 10, 0);

            t.setPadding(10, 10, 10, 10);
            t.setLayoutParams(style);
            String tmp = "#" + tags.get(i);
            t.setText(tmp);
            t.setTextColor(Color.WHITE);
            t.setBackground(MainActivity.getMainContext().getResources().getDrawable(R.drawable.tag_shape));

            itemViewTags.addView(t);
        }

        // date
        String dateString = Reminder.df2.format(current.getDate());
        itemViewDate.setText(dateString);

        // start & finish
        String startString = Reminder.tf.format(current.getStart().getTime());
        itemViewStart.setText(startString);

        String finishString = Reminder.tf.format(current.getFinish().getTime());
        itemViewFinish.setText(finishString);

        String descString = current.getDesc();
        itemViewDesc.setText(descString);

        marked = current.getStatus();
        if (!marked) {
            itemViewMark.setImageResource(R.drawable.btn_circle_normal);
            itemViewMarkText.setText("Mark as Complete");
            itemViewMarkText.setTextColor(Color.parseColor("#808080"));
            itemViewCheck.setBackgroundColor(Color.WHITE);
        }
        else {
            itemViewMark.setImageResource(R.drawable.ic_cab_done_holo_dark);
            itemViewMarkText.setText("Completed");
            itemViewMarkText.setTextColor(Color.WHITE);
            itemViewCheck.setBackgroundColor(Color.rgb(106, 202, 107));
        }
    }

    public static void refreshReminder(Reminder _r, int _rId) {
        current = _r;
        currentId = _rId;
        isExist = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        itemViewMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        itemViewMap.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        itemViewMap.onLowMemory();
    }
}

//testView.setText("View Task " + MainActivity.currentReminder);
