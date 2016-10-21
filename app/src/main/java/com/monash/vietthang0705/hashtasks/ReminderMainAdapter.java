package com.monash.vietthang0705.hashtasks;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class ReminderMainAdapter extends RecyclerView.Adapter<ReminderMainAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Reminder> reminderList;
    public static int count = 0;

    /**
     * TODO:
     * For Reminder class and xml view:
     *  - look into Calendar APIs
     *  - Modify Reminder class according to the APIs
     *  - Save into dB, show into View
     *
     * @param _context
     * @param _reminderList
     */

    public ReminderMainAdapter(Context _context, ArrayList<Reminder> _reminderList) {
        context = _context;
        reminderList = _reminderList;
    }

    /**
     * TODO:
     * only change inside ViewHolder class and onBindViewHolder
     * ViewHolder:
     *  all var member representing item view xml
     *  constructor
     * onBind:
     *  take reminderList[i] and give ViewHolder data
     */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout itemMain;
        TextView itemDate;
        ImageView itemCheckIcon;
        ImageView itemChevron;
        TextView itemTime;
        NestedHorizontalScrollView itemScrollTags;
        LinearLayout itemTags;

        public ViewHolder(View itemView) {
            super(itemView);

            itemMain = (RelativeLayout) itemView.findViewById(R.id.item_main);
            itemDate = (TextView) itemView.findViewById(R.id.item_date);
            itemCheckIcon = (ImageView) itemView.findViewById(R.id.item_check_icon);
            itemChevron = (ImageView) itemView.findViewById(R.id.item_chevron);
            itemTime = (TextView) itemView.findViewById(R.id.item_time);
            itemScrollTags = (NestedHorizontalScrollView) itemView.findViewById(R.id.scroll_tags);
            itemTags = (LinearLayout) itemView.findViewById(R.id.item_tag_list);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int pos) {
        Reminder r = reminderList.get(pos);

        String textView;

        // itemDate
        TextView dateView = vHolder.itemDate;

        textView = Reminder.df.format(r.getDate());
        dateView.setText(textView); // listId if for testing purposes

        if (pos > 0) {
            if (reminderList.get(pos-1).getDate().compareTo(r.getDate()) == 0) {
                Log.d("MAIN_SAMEDATE", reminderList.get(pos-1).getDate() + " " + r.getDate());
                dateView.setVisibility(View.INVISIBLE);
                dateView.setHeight(0);
            }
        }

        // itemTime
        TextView timeView = vHolder.itemTime;
        textView = Reminder.tf.format(r.getStart().getTime()) + " - " + r.tf.format(r.getFinish().getTime());

        timeView.setText(textView);

        // itemTags
        LinearLayout tagsView = vHolder.itemTags;
        /*
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        break;
                }

                return true;
            }
        */

        if (tagsView.getChildCount() > 0)
            tagsView.removeAllViews();

        ArrayList<String> tagString = r.getTags();
        for (int i=0; i<tagString.size(); i++) {
            TextView t = new TextView(this.context);

            LinearLayout.LayoutParams style = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            style.setMargins(10, 0, 10, 0);

            t.setPadding(10, 10, 10, 10);
            t.setLayoutParams(style);
            String tmp = "#" + tagString.get(i);
            t.setText(tmp);
            t.setTextColor(Color.WHITE);
            t.setBackground(MainActivity.getMainContext().getResources().getDrawable(R.drawable.tag_shape));

            tagsView.addView(t);
        }

        boolean marked = r.getStatus();
        Log.d("ONCREATE", ""+ r.getStatus());

        RelativeLayout mainView = vHolder.itemMain;
        ImageView iconView = vHolder.itemCheckIcon;
        ImageView chevronView = vHolder.itemChevron;

        if (!marked) {
            iconView.setImageResource(R.drawable.btn_circle_normal);
            timeView.setTextColor(Color.parseColor("#808080"));
            chevronView.setImageResource(R.drawable.ic_btn_search_go);
            mainView.setBackgroundColor(Color.WHITE);
        }
        else {
            iconView.setImageResource(R.drawable.ic_cab_done_holo_dark);
            timeView.setTextColor(Color.WHITE);
            chevronView.setImageResource(R.drawable.ic_btn_search_go_dark);
            mainView.setBackgroundColor(Color.rgb(106, 202, 107));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View rView = inflater.inflate(R.layout.reminder_main_item, parent, false);

        ViewHolder vHolder = new ViewHolder(rView);
        return vHolder;
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
