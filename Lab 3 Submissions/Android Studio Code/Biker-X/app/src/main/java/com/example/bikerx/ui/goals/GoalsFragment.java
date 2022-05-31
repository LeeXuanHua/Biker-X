package com.example.bikerx.ui.goals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikerx.MainActivity;
import com.example.bikerx.R;
import com.example.bikerx.entities.Goal;
import com.example.bikerx.entities.GoalsInfo;

import com.example.bikerx.databinding.GoalsFragmentBinding;
import com.example.bikerx.ui.session.CyclingSessionViewModel;
import com.example.bikerx.ui.session.CyclingSessionViewModelFactory;

import java.util.HashMap;


public class GoalsFragment extends Fragment {
    private View myView;

    // creating a variable for our object class
    GoalsInfo goalsInfo;
    private GoalsFragmentBinding mBinding;
    private GoalsViewModel mViewModel;
    private String userId;

    public static GoalsFragment newInstance() {
        return new GoalsFragment();
    }

    /**
     *
     * Initialises goals fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = GoalsFragmentBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        myView = inflater.inflate(R.layout.goals_fragment, container, false);

        // initializing our object class variable.
        goalsInfo = new GoalsInfo();

        /**
         * update monthlydistance goals to database and reflect the updated value on the UI
         */
        mBinding.MonthlyDistanceGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String monthlyDistanceInKm = mBinding.InputMonthlyDistanceGoal.getText().toString();

                /**
                 * check if user did not enter monthly distance goals.
                 */

                if (monthlyDistanceInKm.isEmpty()) {
                    Toast.makeText(getActivity(), "Error: Monthly distance goal not set.", Toast.LENGTH_LONG).show();
                } else {
                    mViewModel.updateDistanceGoal(userId, Integer.parseInt(monthlyDistanceInKm));
                    //update textview
                    mBinding.MonthlyGoalsProgressBar.setMax(Integer.parseInt(monthlyDistanceInKm));
                    String percentage = calPercentage(mBinding.MonthlyGoalsProgressBar.getProgress(),mBinding.MonthlyGoalsProgressBar.getMax());
                    mBinding.GoalsPercentage.setText(percentage + "%");
//                    mBinding.MonthlyDistanceGoal.setText(Integer.parseInt(monthlyDistanceInKm));
                    mBinding.MonthlyDistanceGoal.setText(monthlyDistanceInKm);
                }
            }
        });
        /**
         * update MonthlyTime goals to database and reflect the updated value on the UI
         */
        mBinding.MonthlyTimeGoalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // getting text from our edittext fields.
                String monthlyTimeInHours = mBinding.InputMonthlyTimeGoal.getText().toString();

                // below line is for checking weather edittext fields are empty or not.

                if (monthlyTimeInHours.isEmpty()) {
                    Toast.makeText(getActivity(), "Error: Monthly time goal not set.", Toast.LENGTH_LONG).show();
                } else {
                    mViewModel.updateTimeGoal(userId, Integer.parseInt(monthlyTimeInHours));
                    //update text view
                    mBinding.MonthlyTimeGoal.setText(monthlyTimeInHours);

                }
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userId = ((MainActivity)getActivity()).getUserId();
        mViewModel.getCyclingHistory(userId);
        displayGoalsData();
    }


    /**Helper function to format time (in milliseconds) to Chronometer display.
     * @param monthDuration Time to be formatted.
     * @return Returns a String, representing time formatted as "HHh MMm".
     */
    private String getChronometerDisplay(Long monthDuration) {
        int h = (int) ((monthDuration) / 3600000);
        int m = (int) (((monthDuration) / 60) % 60);

        String mString = m >= 10 ? Integer.toString(m) : "0"+Integer.toString(m);
        return String.format("%dh %sm", h, mString);
    }

    /**
     * update and display MonthlyDistanceGoal and MonthlyTimeGoal achieved data on the chronometer display
     * update and display MonthlyDistanceGoal achieved data on the progress bar
     */
    private void displayGoalsData() {
        mBinding.chronometer.setText(getChronometerDisplay(0L));
        mViewModel.fetchGoals(userId);
        mViewModel.getGoals().observe(getViewLifecycleOwner(), new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                if (goal != null) {
                    //
                    mBinding.MonthlyDistanceGoal.setText(String.format("%d", (int)goal.getDistance()));
                    mBinding.MonthlyTimeGoal.setText(String.format("%d",(int) (goal.getDuration() / 3600000)));

                    mBinding.goalsChronometer.setText(getChronometerDisplay(goal.getDuration()));
                    mBinding.timeProgressBar.setMax((int) (goal.getDuration()/3600000));


                    mBinding.distanceGoalsFloat.setText(String.format("%.2f", goal.getDistance()));
                    mBinding.distanceProgressBar.setMax((int) goal.getDistance());
                    mBinding.MonthlyGoalsProgressBar.setMax((int) goal.getDistance());
                    String percentage = calPercentage(mBinding.MonthlyGoalsProgressBar.getProgress(),mBinding.MonthlyGoalsProgressBar.getMax());
                    mBinding.GoalsPercentage.setText(percentage + "%");
                }
            }
        });

        mViewModel.calculateMonthlyData(this).observe(getViewLifecycleOwner(), new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {

                if (hashMap != null) {
                    Double monthDistance = (Double)hashMap.get("monthDistance");
                    mBinding.distanceDetailsFloat.setText(String.format("%.2f", monthDistance));
                    mBinding.distanceProgressBar.setProgress(monthDistance.intValue());

                    long monthDuration = (Long) hashMap.get("monthDuration");
                    mBinding.chronometer.setText(getChronometerDisplay(monthDuration));
                    mBinding.timeProgressBar.setProgress((int) monthDuration);

                    mBinding.MonthlyGoalsProgressBar.setProgress(monthDistance.intValue());
                }


            }
        });

    }

    /**
     * Helper function to calculate percentage of goals (for distance) achieved by the user
     * @param progress distance cycled
     * @param max goal (in distance) set by user
     * @return percentage of goals (for distance) achieved by the user
     */
    private String calPercentage(int progress, int max){

        return Float.toString(Math.round(((float)progress/(float)max)*100));

    }
}