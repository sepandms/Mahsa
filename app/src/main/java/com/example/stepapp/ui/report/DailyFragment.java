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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DailyFragment extends Fragment {

    public TextView stepsCountTextView;
    public ProgressBar dailyStepsCountProgressBar;
    public TextView goalTextView;

    static int dailyStepsCompleted = 0;
    static int dailyStepsGoal = 100;

    AnyChartView anyChartView;

    Date cDate = new Date();
    String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    public Map<Integer, Integer> stepsByHour = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        View root = inflater.inflate(R.layout.fragment_daily, container, false);

        // Get the number of steps stored in the current date
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        dailyStepsCompleted = StepAppOpenHelper.loadDaySingleRecord(getContext(), fDate);

        // Text view & ProgressBars
        goalTextView = (TextView) root.findViewById(R.id.stepsGoal);
        stepsCountTextView = (TextView) root.findViewById(R.id.stepsCount);
        dailyStepsCountProgressBar = (ProgressBar) root.findViewById(R.id.dailyProgressBar);
        dailyStepsCountProgressBar.setMax(dailyStepsGoal);

        // Set the Views with the number of stored steps
        Resources res = getResources();
        String goal = String.format(res.getString(R.string.goal), dailyStepsGoal);
        goalTextView.setText(goal);
        stepsCountTextView.setText(String.valueOf(dailyStepsCompleted));
        dailyStepsCountProgressBar.setProgress(dailyStepsCompleted);


        // Create column chart
        anyChartView = root.findViewById(R.id.hourBarChart);
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
        stepsByHour = StepAppOpenHelper.loadStepsByHour(getContext(), current_time);

        // Creating a new map that contains hours of the day from 0 to 24 and number of steps during each hour set to 0
        Map<Integer, Integer> graph_map = new TreeMap<>();
        for (int i = 0; i < 25; i++) {
            graph_map.put(i, 0);
        }

        // Replace the number of steps for each hour in graph_map with the number of steps read from the database
        graph_map.putAll(stepsByHour);


        //***** Create column chart using AnyChart library *********/
        // 1. Create and get the cartesian coordinate system for column chart
        Cartesian cartesian = AnyChart.column();

        // 2. Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<Integer,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        // 3. Add the data to column chart and get the columns
        Column column = cartesian.column(data);

        //***** Modify the UI of the chart *********/
        // Change the color of column chart and its border
        column.fill("#1EB980");
        column.stroke("#1EB980");

        // Modify column chart tooltip properties
        column.tooltip()
                .titleFormat("At hour: {%X}")
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
        cartesian.xAxis(0).title("Hour");
        cartesian.background().fill("#00000000");
        cartesian.animation(true);

        return cartesian;
    }
}
