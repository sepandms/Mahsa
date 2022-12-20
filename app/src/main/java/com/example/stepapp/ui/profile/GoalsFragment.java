package com.example.stepapp.ui.profile;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.editor.Step;
import com.example.stepapp.R;
import com.example.stepapp.StepAppOpenHelper;

public class GoalsFragment extends Fragment {

    EditText dailyGoalInput;
    EditText weeklyGoalInput;
    EditText monthlyGoalInput;

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
        //dailyGoalInput.setHint(dailyStepsGoal);
        dailyGoalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        });

        weeklyGoalInput = (EditText) root.findViewById(R.id.weeklyGoalInput);
        //weeklyGoalInput.setHint(weeklyStepsGoal);
        weeklyGoalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        });

        monthlyGoalInput = (EditText) root.findViewById(R.id.monthlyGoalInput);
        //monthlyGoalInput.setHint(monthlyStepsGoal);
        monthlyGoalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        });



        return root;
    }
}
