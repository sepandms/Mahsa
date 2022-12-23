package com.example.stepapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StepAppOpenHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "stepapp";

    public static final String TABLE_NAME = "num_steps";
    public static final String KEY_ID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_DAY = "day";
    public static final String KEY_WEEK = "week";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";


    public static final String GOALS_TABLE_NAME = "goals";
    public static final String GOALS_KEY_ID = "id";
    public static final String DAILY_GOAL_KEY = "daily_goal";
    public static final String WEEKLY_GOAL_KEY = "weekly_goal";
    public static final String MONTHLY_GOAL_KEY = "monthly_goal";

    // Default SQL for creating a table in a database
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, " + KEY_YEAR + " TEXT, " + KEY_MONTH + " TEXT, " + KEY_WEEK + " TEXT, " + KEY_DAY + " TEXT, " + KEY_HOUR + " TEXT, "
            + KEY_TIMESTAMP + " TEXT);";

    public static final String CREATE_GOALS_TABLE_SQL = "CREATE TABLE " + GOALS_TABLE_NAME + " (" +
            GOALS_KEY_ID + " INTEGER PRIMARY KEY, " + DAILY_GOAL_KEY + " INTEGER, " + WEEKLY_GOAL_KEY + " INTEGER, " + MONTHLY_GOAL_KEY + " INTEGER);";


    // The constructor
    public StepAppOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
        db.execSQL(CREATE_GOALS_TABLE_SQL);
        insertDefaultGoals(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GOALS_TABLE_NAME);
        System.out.println("UPDATE");
        onCreate(db);
    }

    /**
     * Utility function to fetch the daily goal
     */
    public static Integer getDailyGoal(Context context){
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] columns = new String [] {StepAppOpenHelper.DAILY_GOAL_KEY};
        Cursor cursor = database.query(StepAppOpenHelper.GOALS_TABLE_NAME, columns, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * Utility function to fetch the weekly goal
     */
    public static Integer getWeeklyGoal(Context context){
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] columns = new String [] {StepAppOpenHelper.WEEKLY_GOAL_KEY};
        Cursor cursor = database.query(StepAppOpenHelper.GOALS_TABLE_NAME, columns, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    /**
     * Utility function to fetch the monthly goal
     */
    public static Integer getMonthlyGoal(Context context){
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] columns = new String [] {StepAppOpenHelper.MONTHLY_GOAL_KEY};
        Cursor cursor = database.query(StepAppOpenHelper.GOALS_TABLE_NAME, columns, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * Insertion of the goals per default (only at application launch)
     */
    public void insertDefaultGoals(SQLiteDatabase db) {
        //StepAppOpenHelper databaseOpenHelper = new StepAppOpenHelper(this.getContext());
        //SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StepAppOpenHelper.DAILY_GOAL_KEY, 7000);
        values.put(StepAppOpenHelper.WEEKLY_GOAL_KEY, 49000);
        values.put(StepAppOpenHelper.MONTHLY_GOAL_KEY, 210000);

        db.insert(StepAppOpenHelper.GOALS_TABLE_NAME, null, values);
    }

    /**
     * Utility function to load all records in the database
     *
     * @param context: application context
     */
    public static void loadRecords(Context context){
        List<String> dates = new LinkedList<String>();
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String [] columns = new String [] {StepAppOpenHelper.KEY_TIMESTAMP};
        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, columns, null, null, StepAppOpenHelper.KEY_TIMESTAMP,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Log.d("STORED TIMESTAMPS: ", String.valueOf(dates));
    }

    /**
     * Utility function to delete all records from the data base
     *
     * @param context: application context
     */
    public static void deleteRecords(Context context){
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int numberDeletedRecords =0;

        numberDeletedRecords = database.delete(StepAppOpenHelper.TABLE_NAME, null, null);
        database.close();

        // display the number of deleted records with a Toast message
        Toast.makeText(context,"Deleted " + String.valueOf(numberDeletedRecords) + " steps",Toast.LENGTH_LONG).show();
    }

    /**
     * Utility function to load records from a single day
     *
     * @param context: application context
     * @param date: today's date
     * @return numSteps: an integer value with the number of records in the database
     */
    //
    public static Integer loadDaySingleRecord(Context context, String date){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String where = StepAppOpenHelper.KEY_DAY + " = ?";
        String [] whereArgs = { date };

        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS TODAY: ", String.valueOf(numSteps));
        return numSteps;
    }

    /**
     * Utility function to load records from a single week
     *
     * @param context: application context
     * @param week: current week number
     * @return numSteps: an integer value with the number of records in the database for the current week
     */
    public static Integer loadWeekSingleRecord(Context context, String week, String year){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();


        String where = StepAppOpenHelper.KEY_WEEK + " = ?" + " AND " + StepAppOpenHelper.KEY_YEAR + " = ?";
        String [] whereArgs = { week, year };

        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS THIS MONTH: ", String.valueOf(numSteps));
        return numSteps;
    }

    /**
     * Utility function to load records from a single month
     *
     * @param context: application context
     * @param month: current month
     * @return numSteps: an integer value with the number of records in the database for the current month
     */
    public static Integer loadMonthSingleRecord(Context context, String month, String year){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();


        String where = StepAppOpenHelper.KEY_MONTH + " = ?" + " AND " + StepAppOpenHelper.KEY_YEAR + " = ?";
        String [] whereArgs = { month, year };

        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS THIS MONTH: ", String.valueOf(numSteps));
        return numSteps;
    }

    /**
     * Utility function to get the number of steps by hour for current date
     *
     * @param context: application context
     * @param date: today's date
     * @return map: map with key-value pairs hour->number of steps
     */
    //
    public static Map<Integer, Integer> loadStepsByHour(Context context, String date){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<Integer, Integer>  map = new HashMap<> ();

        // 2. Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT hour, COUNT(*)  FROM num_steps " +
                "WHERE day = ? GROUP BY hour ORDER BY  hour ASC ", new String [] {date});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            Integer tmpKey = Integer.parseInt(cursor.getString(0));
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }

    /**
     * Utility function to get the number of steps for each day of the month
     */
    public static Map<Integer, Integer> loadStepsByMonthDay(Context context, String month, String year){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<Integer, Integer>  map = new HashMap<> ();

        // 2. Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT day, COUNT(*)  FROM num_steps " +
                "WHERE month = ? AND year = ? GROUP BY day ORDER BY day ASC ", new String [] {month, year});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            Integer tmpKey = Integer.parseInt(cursor.getString(0).substring(8,10));
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }

    /**
     * Utility function to get the number of steps by day in a week
     */
    public static Map<String, Integer> loadStepsByWeekDay(Context context, String week, String year){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<String, Integer>  map = new HashMap<> ();

        // 2. Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT day, COUNT(*)  FROM num_steps " +
                "WHERE week = ? AND year = ? GROUP BY day ORDER BY day ASC ", new String [] {week, year});

        // 4. Iterate over returned elements on the cursor

        ArrayList<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("1 - Mon");
        daysOfWeek.add("2 - Tue");
        daysOfWeek.add("3 - Wed");
        daysOfWeek.add("4 - Thu");
        daysOfWeek.add("5 - Fri");
        daysOfWeek.add("6 - Sat");
        daysOfWeek.add("7 - Sun");



        cursor.moveToFirst();
        Integer tmpValue = null;
        for (int index=0; index < 7; index++){
            LocalDate localDate = LocalDate.of(Integer.valueOf(year),Integer.parseInt(cursor.getString(0).substring(5,7)), Integer.parseInt(cursor.getString(0).substring(8,10)));
            DayOfWeek dayOfWeek = DayOfWeek.from(localDate);
            int val = dayOfWeek.get(ChronoField.DAY_OF_WEEK);
            System.out.println(cursor.getCount());
            System.out.println("COMPARE");
            System.out.println(val);
            System.out.println(index);

            String tmpKey = daysOfWeek.get(index);
            if (index+1 == val){
                tmpValue = Integer.parseInt(cursor.getString(1));
                if (cursor.isLast()){
                    map.put(tmpKey, tmpValue);
                    break;
                }
                cursor.moveToNext();
            } else {
                tmpValue = 0;
            }

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            //cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }

    /**
     * Utility function to get the number of steps for each week of the month
     */
    public static Map<String, Integer> loadStepsByMonthWeek(Context context, String month, String year){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<String, Integer>  map = new HashMap<> ();

        // 2. Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT week, COUNT(*)  FROM num_steps " +
                "WHERE month = ? AND year = ? GROUP BY week ORDER BY week ASC ", new String [] {month, year});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            String tmpKey = "W" + String.valueOf(index+1);
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }




}









