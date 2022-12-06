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


public class MonthlyFragment extends Fragment {

    public TextView stepsCountTextView;
    public ProgressBar monthlyStepsCountProgressBar;
    public TextView goalTextView;

    static int monthlyStepsCompleted = 0;
    static int monthlyStepsGoal = 10000;

    AnyChartView anyChartView;

    Date cDate = new Date();
    String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_monthly, container, false);

        // Get the number of steps stored in the current date
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        String day = fDate.substring(8,10);
        String month = fDate.substring(5,7);
        String year = fDate.substring(0,4);

        monthlyStepsCompleted = StepAppOpenHelper.loadMonthSingleRecord(getContext(), month, year);
        System.out.println(monthlyStepsCompleted);

        // Text view & ProgressBars
        goalTextView = (TextView) root.findViewById(R.id.stepsGoal);
        stepsCountTextView = (TextView) root.findViewById(R.id.stepsCount);
        monthlyStepsCountProgressBar = (ProgressBar) root.findViewById(R.id.monthlyProgressBar);
        monthlyStepsCountProgressBar.setMax(monthlyStepsGoal);

        // Set the Views with the number of stored steps
        Resources res = getResources();
        String goal = String.format(res.getString(R.string.goal), monthlyStepsGoal);
        goalTextView.setText(goal);
        stepsCountTextView.setText(String.valueOf(monthlyStepsCompleted));
        monthlyStepsCountProgressBar.setProgress(monthlyStepsCompleted);

        return root;

    }
}
