package com.example.stepapp.ui.report;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
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

public class HistoricalFragment extends Fragment {

    AnyChartView anyChartViewDays;
    AnyChartView anyChartViewWeeks;
    TextView currentMonthTextView;
    Button previousMonthButton;
    Button nextMonthButton;
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    Date cDate = new Date();
    String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
    String day = fDate.substring(8,10);
    String month = fDate.substring(5,7);
    String year = fDate.substring(0,4);
    //String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    public Map<Integer, Integer> stepsByDays = null;
    public Map<String, Integer> stepsByWeeks = null;
    
    Line steps = null;
    Column column = null;

    Cartesian cartesian = null;
    Cartesian secondCartesian = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_historical, container, false);

        // Create line chart
        anyChartViewDays = root.findViewById(R.id.dayLineChart);
        APIlib.getInstance().setActiveAnyChartView(anyChartViewDays);
        anyChartViewDays.setProgressBar(root.findViewById(R.id.loadingBar));

        cartesian = createLineChart();

        anyChartViewDays.setBackgroundColor("#00000000");
        anyChartViewDays.setChart(cartesian);

        // Create column char
        anyChartViewWeeks = root.findViewById(R.id.weekColumnChart);
        APIlib.getInstance().setActiveAnyChartView(anyChartViewWeeks);
        anyChartViewWeeks.setProgressBar(root.findViewById(R.id.loadingBar2));

        secondCartesian = createColumnChart();

        anyChartViewWeeks.setBackgroundColor("#00000000");
        anyChartViewWeeks.setChart(secondCartesian);

        currentMonthTextView = root.findViewById(R.id.current_month);
        currentMonthTextView.setText(months[Integer.valueOf(month)-1] + " " + year);

        previousMonthButton = root.findViewById(R.id.back);
        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(month) == 1) {
                    month = String.valueOf(12);
                    year = String.valueOf(Integer.valueOf(year)-1);
                } else {
                    month = String.valueOf(Integer.valueOf(month) - 1);
                }
                container.removeView(anyChartViewDays);
                container.removeView(anyChartViewWeeks);
                currentMonthTextView.setText(months[Integer.valueOf(month)-1] + " " + year);
                stepsByDays = null;
                stepsByWeeks = null;
                Cartesian newLineChart = createLineChart();
                Cartesian newColumnChart = createColumnChart();
                anyChartViewDays.setChart(newLineChart);
                anyChartViewWeeks.setChart(newColumnChart);
            }
        });
        nextMonthButton = root.findViewById(R.id.next);
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(month) == 12) {
                    month = String.valueOf(1);
                    year = String.valueOf(Integer.valueOf(year)+1);
                } else {
                    month = String.valueOf(Integer.valueOf(month) + 1);
                }
                currentMonthTextView.setText(months[Integer.valueOf(month)-1] + " " + year);
                stepsByDays = null;
                stepsByWeeks = null;
                cartesian = null;
                secondCartesian = null;
                anyChartViewWeeks.invalidate();
                anyChartViewDays.invalidate();
                cartesian = createLineChart();
                secondCartesian = createColumnChart();

                APIlib.getInstance().setActiveAnyChartView(anyChartViewDays);
                anyChartViewDays.setChart(cartesian);
                APIlib.getInstance().setActiveAnyChartView(anyChartViewWeeks);
                anyChartViewWeeks.setChart(secondCartesian);

                //updateLineChart(cartesian);
                //updateColumnChart(secondCartesian);
            }
        });

        return root;

    }

    public Cartesian createLineChart(){
        //***** Read data from SQLiteDatabase *********/
        // Get the map with hours and number of steps for today from the database and initialize it to variable stepsByHour
        System.out.println("MONTH!!!!!!!!!!");
        System.out.println(month);
        stepsByDays = StepAppOpenHelper.loadStepsByMonthDay(getContext(), month, year);

        // Creating a new map that contains hours of the day from 0 to 24 and number of steps during each hour set to 0
        Map<Integer, Integer> graph_map = new TreeMap<>();
        int limit = 0;

        if (Integer.valueOf(month) == 2) {
            limit = 29;
        } else if (Integer.valueOf(month) == 1 || Integer.valueOf(month) == 3 || Integer.valueOf(month) == 7 ||
                Integer.valueOf(month) == 8 || Integer.valueOf(month) == 10 || Integer.valueOf(month) == 12) {
            limit = 32;
        } else {
            limit = 31;
        }

        for (int i = 1; i < limit; i++) {
            graph_map.put(i, 0);
        }

        System.out.println(graph_map);

        // Replace the number of steps for each hour in graph_map with the number of steps read from the database
        graph_map.putAll(stepsByDays);

        //***** Create column chart using AnyChart library *********/
        // 1. Create and get the cartesian coordinate system for column chart
        Cartesian cartesian = AnyChart.line();

        // 2. Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();
        System.out.println(data);

        for (Map.Entry<Integer,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        System.out.println(data);
        Set set = Set.instantiate();
        set.data(data);
        Mapping seriesMapping = set.mapAs("{ x: 'x', value: 'value' }");
        steps = cartesian.line(seriesMapping);
        steps.stroke("#1EB980");
        steps.name("Steps per day");
        steps.hovered().markers().enabled(true);
        steps.hovered().markers().type(MarkerType.CIRCLE).size(4d);
        steps.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);

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

    public Cartesian createColumnChart(){
        //***** Read data from SQLiteDatabase *********/
        // Get the map with hours and number of steps for today from the database and initialize it to variable stepsByHour
        stepsByWeeks = StepAppOpenHelper.loadStepsByMonthWeek(getContext(), month, year);

        // Creating a new map that contains hours of the day from 0 to 24 and number of steps during each hour set to 0
        Map<String, Integer> graph_map = new TreeMap<>();
        int limit = 0;

        if (Integer.valueOf(month) == 2) {
            limit = 29;
        } else if (Integer.valueOf(month) == 1 || Integer.valueOf(month) == 3 || Integer.valueOf(month) == 7 ||
                Integer.valueOf(month) == 8 || Integer.valueOf(month) == 10 || Integer.valueOf(month) == 12) {
            limit = 32;
        } else {
            limit = 31;
        }

        for (int i = 1; i < 5; i++) {
            graph_map.put("W"+ String.valueOf(i), 0);
        }

        // Replace the number of steps for each hour in graph_map with the number of steps read from the database
        graph_map.putAll(stepsByWeeks);


        //***** Create column chart using AnyChart library *********/
        // 1. Create and get the cartesian coordinate system for column chart
        Cartesian cartesian = AnyChart.column();

        // 2. Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        // 3. Add the data to column chart and get the columns
        column = cartesian.column(data);

        //***** Modify the UI of the chart *********/
        // Change the color of column chart and its border
        column.fill("#1EB980");
        column.stroke("#1EB980");

        // Modify column chart tooltip properties
        column.tooltip()
                .titleFormat("At week: {%X}")
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
        cartesian.xAxis(0).title("weeks");
        cartesian.background().fill("#00000000");
        cartesian.animation(true);

        return cartesian;
    }

    public void updateLineChart(Cartesian chart){
        stepsByDays = StepAppOpenHelper.loadStepsByMonthDay(getContext(), month, year);
        chart = null;

        // Creating a new map that contains hours of the day from 0 to 24 and number of steps during each hour set to 0
        Map<Integer, Integer> graph_map = new TreeMap<>();
        int limit = 0;

        if (Integer.valueOf(month) == 2) {
            limit = 29;
        } else if (Integer.valueOf(month) == 1 || Integer.valueOf(month) == 3 || Integer.valueOf(month) == 7 ||
                Integer.valueOf(month) == 8 || Integer.valueOf(month) == 10 || Integer.valueOf(month) == 12) {
            limit = 32;
        } else {
            limit = 31;
        }

        for (int i = 1; i < limit; i++) {
            graph_map.put(i, 0);
        }

        System.out.println(graph_map);

        // Replace the number of steps for each hour in graph_map with the number of steps read from the database
        graph_map.putAll(stepsByDays);

        List<DataEntry> data = new ArrayList<>();
        System.out.println(data);

        for (Map.Entry<Integer,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        System.out.println(data);
        Set set = Set.instantiate();
        set.data(data);
        Mapping seriesMapping = set.mapAs("{ x: 'x', value: 'value' }");
        steps = chart.line(seriesMapping);
    }

    public void updateColumnChart(Cartesian chart){
        stepsByWeeks = StepAppOpenHelper.loadStepsByMonthWeek(getContext(), month, year);

        // Creating a new map that contains hours of the day from 0 to 24 and number of steps during each hour set to 0
        Map<String, Integer> graph_map = new TreeMap<>();
        int limit = 0;

        if (Integer.valueOf(month) == 2) {
            limit = 29;
        } else if (Integer.valueOf(month) == 1 || Integer.valueOf(month) == 3 || Integer.valueOf(month) == 7 ||
                Integer.valueOf(month) == 8 || Integer.valueOf(month) == 10 || Integer.valueOf(month) == 12) {
            limit = 32;
        } else {
            limit = 31;
        }

        for (int i = 1; i < 5; i++) {
            graph_map.put("W"+ String.valueOf(i), 0);
        }

        // Replace the number of steps for each hour in graph_map with the number of steps read from the database
        graph_map.putAll(stepsByWeeks);

        // 2. Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        // 3. Add the data to column chart and get the columns
        column = chart.column(data);
    }
}
