package com.monash.vietthang0705.hashtasks;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class ReminderManageAdapter extends RecyclerView.Adapter<ReminderManageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Reminder> rList;

    public ReminderManageAdapter(Context _context, ArrayList<Reminder> _rList) {
        context = _context;
        rList = _rList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemMngDate;
        LinearLayout itemMngTags;

        public ViewHolder(View itemView) {
            super(itemView);

            itemMngDate = (TextView) itemView.findViewById(R.id.item_mng_date);
            itemMngTags = (LinearLayout) itemView.findViewById(R.id.item_mng_tag_list);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int pos) {
        Reminder r = rList.get(pos);

        String parsed_date;
        TextView mngDateView = vHolder.itemMngDate;

        parsed_date = Reminder.df.format(r.getDate());
        mngDateView.setText(parsed_date);

        LinearLayout mngTagsView = vHolder.itemMngTags;
        if (mngTagsView.getChildCount() > 0) {
            mngTagsView.removeAllViews();
        }

        ArrayList<String> tags = r.getTags();
        for (int i=0; i<tags.size(); i++) {
            TextView t = new TextView(this.context);

            LinearLayout.LayoutParams style = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            style.setMargins(10, 0, 10, 0);

            t.setPadding(10, 10, 10, 10);
            t.setLayoutParams(style);
            String tmp = "#" + tags.get(i);
            t.setText(tmp);
            t.setTextColor(Color.WHITE);
            t.setBackground(MainActivity.getMainContext().getResources().getDrawable(R.drawable.tag_shape));

            mngTagsView.addView(t);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View rMngView = inflater.inflate(R.layout.reminder_manage_item, parent, false);

        ViewHolder vHolder = new ViewHolder(rMngView);
        return vHolder;
    }

    @Override
    public int getItemCount() {
        return rList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
