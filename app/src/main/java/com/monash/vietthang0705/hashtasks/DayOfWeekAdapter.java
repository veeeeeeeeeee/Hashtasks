package com.monash.vietthang0705.hashtasks;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by vietthang.0705 on 11-Jun-16.
 */
public class DayOfWeekAdapter extends RecyclerView.Adapter<DayOfWeekAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Reminder> todayReminders;
    private int dayOfWeek;

    public DayOfWeekAdapter(Context _context, ArrayList<Reminder> _reminderList, int _dayOfWeek) {
        context = _context;
        todayReminders = _reminderList;
        dayOfWeek = _dayOfWeek;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstTag;
        TextView secondTag;

        public ViewHolder(View itemView) {
            super(itemView);

            firstTag = (TextView) itemView.findViewById(R.id.tag_first);
            secondTag = (TextView) itemView.findViewById(R.id.tag_second);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int pos) {
        Reminder r = todayReminders.get(pos);

        TextView firstTagView = vHolder.firstTag;
        TextView secondTagView = vHolder.secondTag;
        firstTagView.setText("");
        secondTagView.setText("");

        if (!r.getTags().isEmpty()) {
            String firstTag = "#" + r.getTags().get(0);
            firstTagView.setText(firstTag);
        }
        else {
            firstTagView.setVisibility(View.INVISIBLE);
        }

        if (r.getTags().size() > 1) {
            String secondTag = "#" + r.getTags().get(1);
            secondTagView.setText(secondTag);
        }
        else {
            secondTagView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context contet = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View dayOfWeekView = inflater.inflate(R.layout.week_tab_fragment_item, parent, false);

        ViewHolder vHolder = new ViewHolder(dayOfWeekView);
        return vHolder;
    }

    @Override
    public int getItemCount() {
        return todayReminders.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
