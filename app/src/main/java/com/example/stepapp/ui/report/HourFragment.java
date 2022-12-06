package com.example.stepapp.ui.report;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HourFragment  extends Fragment {

    public int todaySteps = 0;
    TextView numStepsTextView;
    AnyChartView anyChartView;

    Button shareButton;

    Date cDate = new Date();
    String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    public Map<Integer, Integer> stepsByHour = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        View root = inflater.inflate(R.layout.fragment_hour, container, false);

        // Create column chart
        anyChartView = root.findViewById(R.id.hourBarChart);
        anyChartView.setProgressBar(root.findViewById(R.id.loadingBar));

        Cartesian cartesian = createColumnChart();
        anyChartView.setBackgroundColor("#00000000");
        anyChartView.setChart(cartesian);


        // Add the number of steps in text view
        numStepsTextView = root.findViewById(R.id.numStepsTextView);
        todaySteps = StepAppOpenHelper.loadSingleRecord(getContext(), current_time);
        numStepsTextView.setText(String.valueOf(todaySteps));

        shareButton = root.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 5: Specify what the share button should do when clicked
                // (~ 3 lines of code)


            }
        });

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

    /**
     * Utility generate a bitmap from the chart
     * @param view: AnyChartView
     * @return bitmap:
     */
    private Bitmap generateBitmap(View view) {

        // 1. Create a bitmap with same dimensions as view
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);


        // 2. Create a canvas using bitmap
        Canvas canvas = new Canvas(bitmap);

        // 3. We need to check if view has background image.
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        }
        else {
            canvas.drawColor(Color.TRANSPARENT);
        }

        // 4. Draw the view on the canvas
        view.draw(canvas);

        Log.v("MainActivity", "Bitmap generated successfully");

        // 5. Final bitmap
        return bitmap;
    }

    /**
     * Utility function to save the bitmap as image in external storage
     * @param bitmap: Bitmap
     */
    private void saveImage(Bitmap bitmap) {

        // 1. Create Destination folder in external storage.
        // This will require EXTERNAL STORAGE permission
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File imgDir = new File(externalStorageDirectory.getAbsolutePath());

        // 2. Generate a random file name for image
        String imageName = cDate + ".jpeg";
        File localFile = new File(imgDir, imageName);

        String path = "file://" + externalStorageDirectory.getAbsolutePath();


        // 3. Save the image
        try {
            FileOutputStream fos = new FileOutputStream(localFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE",
                    Uri.fromFile(new File(path))));

            Log.v("MainActivity", "File saved successfully in: " + path);

        } catch (Exception e)  {
            e.printStackTrace();

            Log.v("MainActivity", "File saving failed");

        }
    }

    /**
     * Utility function to share the image with implicit intents
     * @param bitmap: Bitmap
     */
    private void share(Bitmap bitmap) {
        try {
            // TODO 1: Encode and parse the bitmap into a Uri object
            //  (~1 line of code)




            // TODO 2: Create intent, set the type to image, with action send and add image URI as extra
            //  (~5 lines of code)






            // TODO 3: Start the intent and put as title "Share image to ..."
            //  (~1 line of code)



        } catch (Exception e) {
            // TODO 4: Show a toast with message "No implicit intent to handle this action."
            //  (~1 line of code)



        }
    }

    /**
     * Utility function to encode and parse the bitmap into a Uri object
     * @param context: Context
     * @param bitmap: Bitmap
     * @return Uri
     */
    private Uri getImageUri(Context context, Bitmap bitmap) {
        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,
                "Number of steps per hour", null);
        Log.d("URI", "getImageUri: " + String.valueOf(Uri.parse(path)));
        return Uri.parse(path);

    }


}
