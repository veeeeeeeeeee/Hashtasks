package com.monash.vietthang0705.hashtasks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by vietthang.0705 on 30-Apr-16.
 */
public class MainFragment extends Fragment {

    FragmentTabHost mTabHost;

    private static MainFragment _instance;
    private static boolean instanceExist = false;

    public FragmentTabHost getTabHost() {
        return mTabHost;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.main_fragment);

        /**
         * TODO:
         * create Java class for Daily, Weekly, Monthly views
         * create layout for each views
         */

        Bundle b1 = new Bundle();
        b1.putInt("Arg for Frag1", 1);
        mTabHost.addTab(mTabHost.newTabSpec("Daily").setIndicator("Daily"), DailyTabFragment.class, b1);

        Bundle b2 = new Bundle();
        b2.putInt("Arg for Frag2", 2);
        mTabHost.addTab(mTabHost.newTabSpec("Weekly").setIndicator("Weekly"), WeeklyTabFragment.class, b2);

        Bundle b3 = new Bundle();
        b3.putInt("Arg for Frag3", 3);
        mTabHost.addTab(mTabHost.newTabSpec("Monthly").setIndicator("Monthly"), MonthlyTabFragment.class, b3);

        mTabHost.setCurrentTabByTag("Daily");

        return mTabHost;
    }

    public static MainFragment getInstance() {
        if (!instanceExist)
            return newInstance();
        else return _instance;
    }

    public static MainFragment newInstance() {
        _instance = new MainFragment();
        Bundle b = new Bundle();

        _instance.setArguments(b);
        instanceExist = true;
        return _instance;
    }
}
