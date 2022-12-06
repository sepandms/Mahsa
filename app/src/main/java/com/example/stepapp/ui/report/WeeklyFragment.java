package com.example.stepapp.ui.report;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChartView;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.Calendar;
import java.util.Date;


public class WeeklyFragment extends Fragment{

    public TextView stepsCountTextView;
    public ProgressBar weeklyStepsCountProgressBar;
    public TextView goalTextView;

    static int weeklyStepsCompleted = 0;
    static int weeklyStepsGoal = 1000;

    AnyChartView anyChartView;

    Date cDate = new Date();
    String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);


    public int getWeekNumber(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);

        LocalDate localDate = LocalDate.of(year, month, day);
        return localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_weekly, container, false);


        // Get the number of steps stored in the current date
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        String day = fDate.substring(8,10);
        String month = fDate.substring(5,7);
        String year = fDate.substring(0,4);
        int w = getWeekNumber(Integer.valueOf(year),Integer.valueOf(month), Integer.valueOf(day));
        String week = Integer.toString(w);

        weeklyStepsCompleted = StepAppOpenHelper.loadWeekSingleRecord(getContext(), week, year);
        System.out.println(weeklyStepsCompleted);

        // Text view & ProgressBars
        goalTextView = (TextView) root.findViewById(R.id.stepsGoal);
        stepsCountTextView = (TextView) root.findViewById(R.id.stepsCount);
        weeklyStepsCountProgressBar = (ProgressBar) root.findViewById(R.id.weeklyProgressBar);
        weeklyStepsCountProgressBar.setMax(weeklyStepsGoal);

        // Set the Views with the number of stored steps
        Resources res = getResources();
        String goal = String.format(res.getString(R.string.goal), weeklyStepsGoal);
        goalTextView.setText(goal);
        stepsCountTextView.setText(String.valueOf(weeklyStepsCompleted));
        weeklyStepsCountProgressBar.setProgress(weeklyStepsCompleted);

        return root;
    }
}
