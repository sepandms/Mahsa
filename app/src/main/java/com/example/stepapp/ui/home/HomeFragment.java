package com.example.stepapp.ui.home;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


import com.anychart.enums.HoverMode;
import com.example.stepapp.MainActivity;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;
import com.example.stepapp.ui.profile.ProfileFragment;
import com.example.stepapp.ui.report.DailyFragment;
import com.example.stepapp.ui.report.MonthlyFragment;
import com.example.stepapp.ui.report.WeeklyFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.example.stepapp.ui.home.HomeFragment.mNotifyManager;


public class HomeFragment extends Fragment {
    MaterialButtonToggleGroup materialButtonToggleGroup;
    SwitchMaterial start_stop;

    // Text view and Progress Bar variables
    public TextView stepsCountTextView;
    public ProgressBar stepsCountProgressBar;
    public ProgressBar dailyStepsCountProgressBar;
    public ProgressBar weeklyStepsCountProgressBar;
    public ProgressBar monthlyStepsCountProgressBar;
    public TextView goalTextView;

    // ACC sensors.
    private Sensor mSensorACC;
    private SensorManager mSensorManager;
    private SensorEventListener listener;

    // Step Detector sensor
    private Sensor mSensorStepDetector;

    // Num of steps completed
    static int stepsCompleted = 0;
    static int dailyStepsCompleted = 80;
    static int weeklyStepsCompleted = 80;
    static int monthlyStepsCompleted = 80;
    static int stepsGoal = 100;
    static int dailyStepsGoal = 100;
    static int weeklyStepsGoal = 1000;
    static int monthlyStepsGoal = 10000;

    //TODO 6: Create a constant for the notification channel ID
    public String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    // Create a variable to store the NotificationManager object
    public static NotificationManager mNotifyManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Get the number of steps stored in the current date
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        stepsCompleted = StepAppOpenHelper.loadSingleRecord(getContext(), fDate);


        // Text view & ProgressBars
        goalTextView = (TextView) root.findViewById(R.id.stepsGoal);
        stepsCountTextView = (TextView) root.findViewById(R.id.stepsCount);
        stepsCountProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        stepsCountProgressBar.setMax(dailyStepsGoal);
        dailyStepsCountProgressBar = (ProgressBar) root.findViewById(R.id.dailyProgressBar);
        dailyStepsCountProgressBar.setMax(dailyStepsGoal);
        weeklyStepsCountProgressBar = (ProgressBar) root.findViewById(R.id.weeklyProgressBar);
        weeklyStepsCountProgressBar.setMax(weeklyStepsGoal);
        monthlyStepsCountProgressBar = (ProgressBar) root.findViewById(R.id.monthlyProgressBar);
        monthlyStepsCountProgressBar.setMax(monthlyStepsGoal);


        // Set the Views with the number of stored steps
        Resources res = getResources();
        String goal = String.format(res.getString(R.string.goal), dailyStepsGoal);
        goalTextView.setText(goal);
        stepsCountTextView.setText(String.valueOf(dailyStepsCompleted));
        stepsCountProgressBar.setProgress(dailyStepsCompleted);

        dailyStepsCountProgressBar.setProgress(dailyStepsCompleted);
        weeklyStepsCountProgressBar.setProgress(weeklyStepsCompleted);
        monthlyStepsCountProgressBar.setProgress(monthlyStepsCompleted);

        dailyStepsCountProgressBar.setClickable(true);
        dailyStepsCountProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = DailyFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                System.out.println(fragment);
                FragmentActivity activity = getActivity();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
            }
        });

        weeklyStepsCountProgressBar.setClickable(true);
        weeklyStepsCountProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = WeeklyFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                System.out.println(fragment);
                FragmentActivity activity = getActivity();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
            }
        });

        monthlyStepsCountProgressBar.setClickable(true);
        monthlyStepsCountProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = MonthlyFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                System.out.println(fragment);
                FragmentActivity activity = getActivity();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
            }
        });




        //  Get an instance of the sensor manager.
        mSensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorACC = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // Step detector instance
        mSensorStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // Get an instance of the database
        StepAppOpenHelper databaseOpenHelper = new StepAppOpenHelper(this.getContext());
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        // Get the Builder object
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        // Instantiate the StepCounterListener
        listener = new StepCounterListener(notifyBuilder, database, stepsCountTextView, stepsCountProgressBar);

        // Switch button
        start_stop = (SwitchMaterial) root.findViewById(R.id.switch_start);
        start_stop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Toggle is enabled
                    Toast.makeText(getContext(), "START", Toast.LENGTH_SHORT).show();

                    // Check if the Accelerometer sensor exists
                    if (mSensorACC != null) {

                        // Register the ACC listener
                        mSensorManager.registerListener(listener, mSensorACC, SensorManager.SENSOR_DELAY_NORMAL);
                    } else {
                        Toast.makeText(getContext(), R.string.acc_not_available, Toast.LENGTH_SHORT).show();

                    }

                    // Check if the Step detector sensor exists
                    if (mSensorStepDetector != null) {
                        // Register the ACC listener
                        mSensorManager.registerListener(listener, mSensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);

                    } else {
                        Toast.makeText(getContext(), R.string.step_not_available, Toast.LENGTH_SHORT).show();

                    }

                } else {
                    // Toggle is disabled
                    Toast.makeText(getContext(), "STOP", Toast.LENGTH_SHORT).show();

                    // Unregister the listener
                    mSensorManager.unregisterListener(listener);
                }
            }
        });

        /*
        // Toggle group button
        materialButtonToggleGroup = (MaterialButtonToggleGroup) root.findViewById(R.id.toggleButtonGroup);
        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if (group.getCheckedButtonId() == R.id.toggleStart) {

                    //Place code related to Start button
                    Toast.makeText(getContext(), "START", Toast.LENGTH_SHORT).show();

                    // Check if the Accelerometer sensor exists
                    if (mSensorACC != null) {

                        // Register the ACC listener
                        mSensorManager.registerListener(listener, mSensorACC, SensorManager.SENSOR_DELAY_NORMAL);
                    } else {
                        Toast.makeText(getContext(), R.string.acc_not_available, Toast.LENGTH_SHORT).show();

                    }

                    // Check if the Step detector sensor exists
                    if (mSensorStepDetector != null) {
                        // Register the ACC listener
                        mSensorManager.registerListener(listener, mSensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);

                    } else {
                        Toast.makeText(getContext(), R.string.step_not_available, Toast.LENGTH_SHORT).show();

                    }


                } else if (group.getCheckedButtonId() == R.id.toggleStop) {
                    //Place code related to Stop button
                    Toast.makeText(getContext(), "STOP", Toast.LENGTH_SHORT).show();

                    // Unregister the listener
                    mSensorManager.unregisterListener(listener);
                }
            }
        });
        //////////////////////////////////////
        */
        //Call createNotificationChannel()
        createNotificationChannel();
        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSensorManager.unregisterListener(listener); // unregister the listener
    }


    //Create a createNotificationChannel() method and instantiate the NotificationManager inside the method.
    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager)
                (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            //TODO 7: Create a NotificationChannel


            // TODO 8: Set the characteristics of the notification channel



        }
    }

    // Helper method
    public NotificationCompat.Builder getNotificationBuilder() {

        // Explicit Intent
        Intent notificationIntent = new Intent(getContext(), MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(getContext(),
                StepCounterListener.NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Create and instantiate the notification builder.
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(getContext(), PRIMARY_CHANNEL_ID)

                // TODO 9: Set the text and the icon



                //TODO 11: Set the intent




                // Add priority and defaults to your notification for backward compatibility
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);


        return notifyBuilder;
    }

}


// Sensor event listener
class StepCounterListener<stepsCompleted> implements SensorEventListener {

    // Constant for the notification ID:
    public static int NOTIFICATION_ID =0 ;

    //Get the number of stored steps for the current day
    public int mACCStepCounter = HomeFragment.stepsCompleted;

    ArrayList<Integer> mACCSeries = new ArrayList<Integer>();
    ArrayList<String> mTimeSeries = new ArrayList<String>();

    private int lastXPoint = 1;
    int stepThreshold = 10;

    // Android step detector
    public int mAndroidStepCounter = HomeFragment.stepsCompleted;

    // TextView and Progress Bar
    TextView stepsCountTextView;
    ProgressBar stepsCountProgressBar;

    // SQLite Database
    SQLiteDatabase database;

    public String timestamp;
    public String day;
    public String hour;

    //NotificationBuilder
    NotificationCompat.Builder notificationBuilder;

    // Get the notification builder, database, TextView and ProgressBar as args
    public StepCounterListener(NotificationCompat.Builder nb, SQLiteDatabase db, TextView tv, ProgressBar pb){
        stepsCountTextView = tv;
        stepsCountProgressBar = pb;
        database = db;
        notificationBuilder = nb;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {

            // Case of the ACC
            case Sensor.TYPE_LINEAR_ACCELERATION:

                // Get x,y,z
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Timestamp
                long timeInMillis = System.currentTimeMillis() + (event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000;

                // Convert the timestamp to date
                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String date = jdf.format(timeInMillis);


                // Get the date, the day and the hour
                timestamp = date;
                day = date.substring(0,10);
                hour = date.substring(11,13);

                /// STEP COUNTER ACC ////
                double accMag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

                //Update the Magnitude series
                mACCSeries.add((int) accMag);
                //Update the time series
                mTimeSeries.add(timestamp);

                // Calculate ACC peaks and steps
                peakDetection();

                break;

            // case Step detector
            case Sensor.TYPE_STEP_DETECTOR:

                // Calculate the number of steps
                countSteps(event.values[0], timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }


    public void peakDetection() {
        int windowSize = 20;

        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer, Mladenov et al.
         */
        int highestValX = mACCSeries.size(); // get the length of the series
        if (highestValX - lastXPoint < windowSize) { // if the segment is smaller than the processing window skip it
            return;
        }

        List<Integer> valuesInWindow = mACCSeries.subList(lastXPoint,highestValX);
        List<String> timesInWindow = mTimeSeries.subList(lastXPoint,highestValX);

        lastXPoint = highestValX;

        int forwardSlope = 0;
        int downwardSlope = 0;

        List<Integer> dataPointList = new ArrayList<Integer>();
        List<String> timePointList = new ArrayList<String>();


        for (int p =0; p < valuesInWindow.size(); p++){
            dataPointList.add(valuesInWindow.get(p)); // ACC Magnitude data points
            timePointList.add(timesInWindow.get(p)); // Timestamps
        }

        for (int i = 0; i < dataPointList.size(); i++) {
            if (i == 0) {
            }
            else if (i < dataPointList.size() - 1) {
                forwardSlope = dataPointList.get(i + 1) - dataPointList.get(i);
                downwardSlope = dataPointList.get(i)- dataPointList.get(i - 1);

                if (forwardSlope < 0 && downwardSlope > 0 && dataPointList.get(i) > stepThreshold ) {

                    // Update the number of steps
                    mACCStepCounter += 1;

                    // TODO 10: Send a notification when the goal is reached



                    // Update the TextView and the ProgressBar
                    stepsCountTextView.setText(String.valueOf(mACCStepCounter));
                    stepsCountProgressBar.setProgress(mACCStepCounter);

                    // Insert the data in the database
                    ContentValues values = new ContentValues();
                    values.put(StepAppOpenHelper.KEY_TIMESTAMP, timePointList.get(i));
                    values.put(StepAppOpenHelper.KEY_DAY, day);
                    values.put(StepAppOpenHelper.KEY_HOUR, hour);
                    database.insert(StepAppOpenHelper.TABLE_NAME, null, values);
                }

            }
        }
    }


    // Calculate the number of steps from the step detector
    private void countSteps(float step, String timestamp) {

        //Step count
        mAndroidStepCounter += (int) step;
        Log.d("NUM STEPS ANDROID", "Num.steps: " + String.valueOf(mAndroidStepCounter));

        // Update TextView and ProgressBar
        stepsCountTextView.setText(String.valueOf(mAndroidStepCounter));
        stepsCountProgressBar.setProgress(mAndroidStepCounter);

        // Insert the data in the database
        ContentValues values = new ContentValues();
        values.put(StepAppOpenHelper.KEY_TIMESTAMP, timestamp);
        values.put(StepAppOpenHelper.KEY_DAY, day);
        values.put(StepAppOpenHelper.KEY_HOUR, hour);
        database.insert(StepAppOpenHelper.TABLE_NAME, null, values);
    }

}

