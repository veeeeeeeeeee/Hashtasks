package com.monash.vietthang0705.hashtasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddReminder extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //private GoogleApiClient mGoogleApiClient;
    public static final int PLACE_PICKER_REQUEST = 2;

    private Reminder r;

    LinearLayout itemAddTags;
    EditText itemAddTagField;
    EditText itemAddDate;
    EditText itemAddStart;
    EditText itemAddFinish;
    EditText itemAddLocation;
    EditText itemAddDesc;

    // DatePicker for input Date
    Calendar picker;
    DatePickerDialog.OnDateSetListener datePick = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker v, int yy, int mm, int dd) {
            picker.set(Calendar.YEAR, yy);
            picker.set(Calendar.MONTH, mm);
            picker.set(Calendar.DAY_OF_MONTH, dd);

            itemAddDate.setText(Reminder.df2.format(picker.getTime()));
            r.setDate(picker.getTime());
        }
    };

    public void pickDate(View v) {
        new DatePickerDialog(AddReminder.this, datePick,
                picker.get(Calendar.YEAR),
                picker.get(Calendar.MONTH),
                picker.get(Calendar.DAY_OF_MONTH)).show();
    }

    // TimePicker
    TimePickerDialog.OnTimeSetListener startPick = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            picker.set(Calendar.HOUR, hourOfDay);
            picker.set(Calendar.MINUTE, minute);

            itemAddStart.setText(Reminder.tf.format(picker.getTime()));

            Calendar tmp = Calendar.getInstance();
            tmp.set(Calendar.HOUR, hourOfDay);
            tmp.set(Calendar.MINUTE, minute);
            r.setStart(tmp);
        }
    };

    public void pickStart(View v) {
        new TimePickerDialog(AddReminder.this, startPick,
                picker.get(Calendar.HOUR),
                picker.get(Calendar.MINUTE),
                true).show();
    }

    TimePickerDialog.OnTimeSetListener finishPick = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            picker.set(Calendar.HOUR, hourOfDay);
            picker.set(Calendar.MINUTE, minute);

            itemAddFinish.setText(Reminder.tf.format(picker.getTime()));

            Calendar tmp = Calendar.getInstance();
            tmp.set(Calendar.HOUR, hourOfDay);
            tmp.set(Calendar.MINUTE, minute);
            r.setFinish(tmp);
        }
    };

    public void pickFinish(View v) {
        new TimePickerDialog(AddReminder.this, finishPick,
                picker.get(Calendar.HOUR),
                picker.get(Calendar.MINUTE),
                true).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        r = new Reminder();
        picker = Calendar.getInstance();

        //mGoogleApiClient = new GoogleApiClient.
        //        Builder(this).
        //        addApi(Places.GEO_DATA_API).
        //        addApi(Places.PLACE_DETECTION_API).
        //        enableAutoManage(this, this).
        //        build();

        itemAddTagField = (EditText) findViewById(R.id.add_item_add_field);
        itemAddTags = (LinearLayout) findViewById(R.id.add_item_tag_list);
        itemAddDate = (EditText) findViewById(R.id.add_item_field_date);
        itemAddStart = (EditText) findViewById(R.id.add_item_field_start);
        itemAddFinish = (EditText) findViewById(R.id.add_item_field_finish);
        itemAddLocation = (EditText) findViewById(R.id.add_item_location);
        itemAddDesc = (EditText) findViewById(R.id.add_item_desc);

        // set default values for EditText
        itemAddDate.setText(Reminder.df.format(r.getDate()));
        itemAddStart.setText(Reminder.tf.format(r.getStart().getTime()));
        itemAddFinish.setText(Reminder.tf.format(r.getFinish().getTime()));
        itemAddLocation.setText("Pick Location for the event");
        itemAddDesc.setText(r.getDesc());

        itemAddDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (itemAddDesc.getText().toString().equals("Description"));
                    itemAddDesc.setText("");
            }
        });

        final GestureDetector mgd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return true;
            }
        });

        itemAddTags.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mgd.onTouchEvent(event)) {
                    int x = Math.round(event.getX());

                    for (int i=0; i<itemAddTags.getChildCount(); i++) {
                        TextView c = (TextView) itemAddTags.getChildAt(i);
                        if (x > c.getLeft() && x < c.getRight()) {
                            itemAddTags.removeView(c);
                            break;
                        }
                    }
                }

                return true;
            }
        });
    }

    public void pickLocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                itemAddLocation.setText(place.getAddress());
                r.setLoc(place.getLatLng());
            }
        }
    }

    public void addTag(View v) {
        String inp = itemAddTagField.getText().toString();
        if (!inp.isEmpty()) {

            TextView t = new TextView(this);

            LinearLayout.LayoutParams style = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            style.setMargins(10, 0, 10, 0);

            t.setPadding(10, 10, 10, 10);
            t.setLayoutParams(style);
            t.setText("#" + inp);
            t.setTextColor(Color.WHITE);
            t.setBackground(getResources().getDrawable(R.drawable.tag_shape));

            itemAddTags.addView(t);
        }

        itemAddTagField.setText("");

        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void saveTask(View v) {
        /**
         * TODO:
         * pass parcel r back via intent
         */

        ArrayList<String> addedTags = new ArrayList<>();
        for (int i=0; i<itemAddTags.getChildCount(); i++) {
            TextView t = (TextView) itemAddTags.getChildAt(i);

            String tmp = t.getText().toString().substring(1);
            addedTags.add(tmp);
        }

        r.setTags(addedTags);
        r.setDesc(itemAddDesc.getText().toString());

        Intent i = new Intent();
        i.putExtra("result", r);

        i.putExtra("lat", r.getLoc().latitude);
        i.putExtra("lng", r.getLoc().longitude);

        setResult(RESULT_OK, i);

        finish();
    }

    public void resetFields(View v) {
        // TODO: call clear text on all editText, clear tags view
        if (itemAddTags.getChildCount() > 0) {
            itemAddTags.removeAllViews();
        }

        itemAddTagField.setText("");
        itemAddDate.setText("");
        itemAddStart.setText("");
        itemAddFinish.setText("");
        itemAddLocation.setText("");
        itemAddDesc.setText("");
    }

    @Override
    public void onConnectionFailed(ConnectionResult res) {
        /**
         * TODO:
         * pop up a message
         */

        Toast t = new Toast(getApplicationContext());
        t.setText("Cannot connect to Google Map service");
        t.show();
    }
}
