package com.example.stepapp.ui.profile;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.editor.Step;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;

public class GoalsFragment extends Fragment {

    EditText dailyGoalInput;
    EditText weeklyGoalInput;
    EditText monthlyGoalInput;

    Button validateButton;

    static int dailyStepsGoal;
    static int weeklyStepsGoal;
    static int monthlyStepsGoal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_goals, container, false);

        StepAppOpenHelper databaseOpenHelper = new StepAppOpenHelper(this.getContext());
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        dailyStepsGoal = StepAppOpenHelper.getDailyGoal(getContext());
        weeklyStepsGoal = StepAppOpenHelper.getWeeklyGoal(getContext());
        monthlyStepsGoal = StepAppOpenHelper.getMonthlyGoal(getContext());


        dailyGoalInput = (EditText) root.findViewById(R.id.dailyGoalInput);
        dailyGoalInput.setHint(String.valueOf(dailyStepsGoal));
        /*dailyGoalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    Integer enteredGoal = Integer.valueOf(dailyGoalInput.getText().toString());
                    System.out.println(enteredGoal);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StepAppOpenHelper.DAILY_GOAL_KEY, enteredGoal);
                    database.update(StepAppOpenHelper.GOALS_TABLE_NAME, contentValues, StepAppOpenHelper.GOALS_KEY_ID + " = " + 1, null);

                }
                return handled;
            }
        });*/

        weeklyGoalInput = (EditText) root.findViewById(R.id.weeklyGoalInput);
        weeklyGoalInput.setHint(String.valueOf(weeklyStepsGoal));
        /*weeklyGoalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    Integer enteredGoal = Integer.valueOf(weeklyGoalInput.getText().toString());
                    System.out.println(enteredGoal);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StepAppOpenHelper.WEEKLY_GOAL_KEY, enteredGoal);
                    database.update(StepAppOpenHelper.GOALS_TABLE_NAME, contentValues, StepAppOpenHelper.GOALS_KEY_ID + " = " + 1, null);

                }
                return handled;
            }
        });*/

        monthlyGoalInput = (EditText) root.findViewById(R.id.monthlyGoalInput);
        monthlyGoalInput.setHint(String.valueOf(monthlyStepsGoal));
        /*monthlyGoalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    Integer enteredGoal = Integer.valueOf(monthlyGoalInput.getText().toString());
                    System.out.println(enteredGoal);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StepAppOpenHelper.MONTHLY_GOAL_KEY, enteredGoal);
                    database.update(StepAppOpenHelper.GOALS_TABLE_NAME, contentValues, StepAppOpenHelper.GOALS_KEY_ID + " = " + 1, null);

                }
                return handled;
            }
        });*/

        validateButton = (Button) root.findViewById(R.id.validateGoals);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dailyGoalInput.getText().toString().isEmpty()){
                    Integer enteredDailyGoal = Integer.valueOf(dailyGoalInput.getText().toString());
                    System.out.println(enteredDailyGoal);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StepAppOpenHelper.DAILY_GOAL_KEY, enteredDailyGoal);
                    database.update(StepAppOpenHelper.GOALS_TABLE_NAME, contentValues, StepAppOpenHelper.GOALS_KEY_ID + " = " + 1, null);
                } else {
                    Toast.makeText(getContext(), "Please Enter the Daily Goal", Toast.LENGTH_SHORT).show();
                }
                if (!weeklyGoalInput.getText().toString().isEmpty()){
                    Integer enteredWeeklyGoal = Integer.valueOf(weeklyGoalInput.getText().toString());
                    System.out.println(enteredWeeklyGoal);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StepAppOpenHelper.WEEKLY_GOAL_KEY, enteredWeeklyGoal);
                    database.update(StepAppOpenHelper.GOALS_TABLE_NAME, contentValues, StepAppOpenHelper.GOALS_KEY_ID + " = " + 1, null);
                } else {
                    Toast.makeText(getContext(), "Please Enter the Weekly Goal", Toast.LENGTH_SHORT).show();
                }
                if (!monthlyGoalInput.getText().toString().isEmpty()){
                    Integer enteredMonthGoal = Integer.valueOf(monthlyGoalInput.getText().toString());
                    System.out.println(enteredMonthGoal);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StepAppOpenHelper.MONTHLY_GOAL_KEY, enteredMonthGoal);
                    database.update(StepAppOpenHelper.GOALS_TABLE_NAME, contentValues, StepAppOpenHelper.GOALS_KEY_ID + " = " + 1, null);
                } else {
                    Toast.makeText(getContext(), "Please Enter the Monthly Goal", Toast.LENGTH_SHORT).show();
                }

                checkGoalConsistency(dailyStepsGoal, weeklyStepsGoal, monthlyStepsGoal);

            }
        });



        return root;
    }

    public void checkGoalConsistency(Integer dayGoal, Integer weekGoal, Integer monthGoal) {
        if (dayGoal * 7 < weekGoal) {
            Toast.makeText(getContext(), R.string.weekly_goal_bigger, Toast.LENGTH_SHORT).show();
        } else if (dayGoal * 30 < monthGoal) {
            Toast.makeText(getContext(), R.string.monthly_goal_bigger, Toast.LENGTH_SHORT).show();
        } else if (dayGoal * 7 > weekGoal) {
            Toast.makeText(getContext(), R.string.weekly_goal_smaller, Toast.LENGTH_SHORT).show();
        } else if (dayGoal * 30 > monthGoal) {
            Toast.makeText(getContext(), R.string.monthly_goal_smaller, Toast.LENGTH_SHORT).show();
        }
    }
}
