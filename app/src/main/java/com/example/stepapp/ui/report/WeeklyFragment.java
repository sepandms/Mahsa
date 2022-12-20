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

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class WeeklyFragment extends Fragment{

    public TextView stepsCountTextView;
    public ProgressBar weeklyStepsCountProgressBar;
    public TextView goalTextView;

    static int weeklyStepsCompleted = 0;
    static int weeklyStepsGoal = 1000;

    AnyChartView anyChartView;

    Date cDate = new Date();
    String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
    String day = fDate.substring(8,10);
    String month = fDate.substring(5,7);
    String year = fDate.substring(0,4);
    int w = getWeekNumber(Integer.valueOf(year),Integer.valueOf(month), Integer.valueOf(day));
    String week = Integer.toString(w);
    //String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    public Map<String, Integer> stepsByDays = null;


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

        // Create column chart
        anyChartView = root.findViewById(R.id.weekDaysChart);
        anyChartView.setProgressBar(root.findViewById(R.id.loadingBar));

        Cartesian cartesian = createColumnChart();
        anyChartView.setBackgroundColor("#00000000");
        anyChartView.setChart(cartesian);

        return root;
    }

    /**
     * Utility function to create the column chart
     *
     * @return Cartesian: cartesian with column chart and data
     */
    public Cartesian createColumnChart(){
        //***** Read data from SQLiteDatabase *********/
        // Get the map with hours and number of steps for today from the database and initialize it to variable stepsByHour
        stepsByDays = StepAppOpenHelper.loadStepsByWeekDay(getContext(), week, year);

        // Creating a new map that contains hours of the day from 0 to 24 and number of steps during each hour set to 0
        Map<String, Integer> graph_map = new TreeMap<>();

        ArrayList<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("1 - Mon");
        daysOfWeek.add("2 - Tue");
        daysOfWeek.add("3 - Wed");
        daysOfWeek.add("4 - Thu");
        daysOfWeek.add("5 - Fri");
        daysOfWeek.add("6 - Sat");
        daysOfWeek.add("7 - Sun");


        for (int i = 0; i < 7; i++) {
            System.out.println(daysOfWeek.get(i));
            graph_map.put(daysOfWeek.get(i), 0);
        }

        System.out.println(graph_map);
        // Replace the number of steps for each hour in graph_map with the number of steps read from the database
        graph_map.putAll(stepsByDays);

        System.out.println(graph_map);


        //***** Create column chart using AnyChart library *********/
        // 1. Create and get the cartesian coordinate system for column chart
        Cartesian cartesian = AnyChart.column();


        // 2. Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));


        // 3. Add the data to column chart and get the columns
        Column column = cartesian.column(data);

        //***** Modify the UI of the chart *********/
        // Change the color of column chart and its border
        column.fill("#1EB980");
        column.stroke("#1EB980");

        // Modify column chart tooltip properties
        column.tooltip()
                .titleFormat("At day: {%X}")
                .format("{%Value}{groupsSeparator: } Steps")
                .anchor(Anchor.RIGHT_TOP)
                .position(Position.RIGHT_TOP)
                .offsetX(0d)
                .offsetY(5);

        // Modify the UI of the cartesian
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.yScale().minimum(0);
        cartesian.yAxis(0).title("Number of steps");
        cartesian.xAxis(0).title("Days");
        cartesian.background().fill("#00000000");
        cartesian.animation(true);

        return cartesian;
    }
}
