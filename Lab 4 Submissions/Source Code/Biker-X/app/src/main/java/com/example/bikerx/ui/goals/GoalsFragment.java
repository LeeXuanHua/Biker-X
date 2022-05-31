package com.example.bikerx.ui.goals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikerx.MainActivity;
import com.example.bikerx.databinding.GoalsFragmentBinding;

import java.util.HashMap;

/**
 * Displays UI for user to track and set their cycling goals. Uses GoalsViewModel for backend management.
 */
public class GoalsFragment extends Fragment {
    private GoalsFragmentBinding binding;
    private GoalsViewModel viewModel;
    private String userId;

    /**
     * Initialises goals fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = GoalsFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userId = ((MainActivity)getActivity()).getUserId();
        displayGoalsData();
        displayHistoryData();
        bindButtons();
    }

    /**
     * This method dictates the logic of the buttons in the fragment.
     */
    private void bindButtons() {
        //update monthlydistance goals to database and reflect the updated value on the UI
        binding.SubmitDistanceGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String distanceInKm = binding.distanceGoalInput.getText().toString();

                //check for valid input
                boolean valid = isValidDistance(distanceInKm);
                if (!valid) {
                    showInvalidInputWarning();
                } else {
                    submitNewDistance(distanceInKm);
                }
            }
        });

        //update MonthlyTime goals to database and reflect the updated value on the UI
        binding.SubmitTimeGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeInHours = binding.timeGoalInput.getText().toString();

                //check for valid input
                boolean valid = isValidTime(timeInHours);
                if (!valid) {
                    showInvalidInputWarning();
                } else {
                    submitNewTime(timeInHours);

                }
            }
        });
    }

    /**Checks to see if user's distance input is valid.
     * @param input input The user's input in String format.
     * @return Returns a boolean based on whether the input is a valid distance.
     */
    private boolean isValidDistance(String input) {
        if (isValidNumber(input)) {
            float inputFloat = Float.parseFloat(input);
            if (inputFloat <= 9999) return true;
        }
        return false;
    }

    /**Checks to see if user's time input is valid.
     * @param input input The user's input in String format.
     * @return Returns a boolean based on whether the input is a valid time.
     */
    private boolean isValidTime(String input) {
        if (isValidNumber(input)) {
            float inputFloat = Float.parseFloat(input);
            if (inputFloat <= 744) return true;
        }
        return false;
    }

    /**Helper method to check if user input for goals is a valid number.
     * @param input The user's input in String format.
     * @return Returns a boolean based on whether the input is a valid number.
     */
    private boolean isValidNumber(String input) {
        try {
            Float.parseFloat(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates UI and Firestore with new distance goal data.
     * @param distanceInKm New distance goal. Represented in kilometres.
     */
    private void submitNewDistance(String distanceInKm) {
        float inputFloat = Float.parseFloat(distanceInKm);
        int roundedInput = Math.round(inputFloat);

        //binding.MonthlyGoalsProgressBar.setProgress(Integer.parseInt(binding.distanceDetailsFloat.getText().toString()));
        String percentage = calPercentage(binding.MonthlyGoalsProgressBar.getProgress(),binding.MonthlyGoalsProgressBar.getMax());
        binding.GoalsPercentage.setText(percentage + "%");
        binding.MonthlyDistanceGoal.setText(String.valueOf(roundedInput));
        long duration = Long.parseLong(binding.MonthlyTimeGoal.getText().toString())* 3600 * 1000;
        Goal newGoal = new Goal((double) roundedInput, duration);
        viewModel.updateGoal(userId, newGoal);
    }

    /**
     * Updates UI and Firestore with new time goal data.
     * @param timeInHours New time goal. Represented in hours.
     */
    private void submitNewTime(String timeInHours) {
        Float timeFloat = Float.parseFloat(timeInHours);
        int roundedTime = Math.round(timeFloat);

        //binding.timeProgressBar.setProgress(Integer.parseInt(binding.chronometer.getText().toString()));
        binding.MonthlyTimeGoal.setText(String.valueOf(roundedTime));

        long duration = (long) roundedTime * 3600 * 1000;
        Goal newGoal = new Goal(Double.parseDouble(binding.MonthlyDistanceGoal.getText().toString()), duration);
        viewModel.updateGoal(userId, newGoal);
    }

    /**
     * Helper method to display warning when user submits invalid input.
     */
    private void showInvalidInputWarning() {
        Toast.makeText(getActivity(), "Error: Please use values between 0 - 9999 for distance, and between 0 - 744 for time.", Toast.LENGTH_LONG).show();
    }

    /**
     * update and display MonthlyDistanceGoal and MonthlyTimeGoal data on the UI
     * update and display MonthlyDistanceGoal data on the progress bar
     */
    private void displayGoalsData() {
        MutableLiveData<Goal> goal = viewModel.fetchGoals(userId);
        goal.observe(getViewLifecycleOwner(), new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                if (goal != null) {
                    updateGoalsUi(goal);
                }
            }
        });
    }

    /**
     * Helper method to update UI based on new goals data.
     * @param goal Object containing goal data.
     */
    private void updateGoalsUi(Goal goal) {
        binding.MonthlyDistanceGoal.setText(String.format("%d", (int)goal.getDistance()));
        binding.MonthlyTimeGoal.setText(String.format("%d",(int) (goal.getDuration() / 3600000)));

        binding.MonthlyGoalsProgressBar.setMax((int) goal.getDistance());
        String percentage = calPercentage(binding.MonthlyGoalsProgressBar.getProgress(),binding.MonthlyGoalsProgressBar.getMax());
        binding.GoalsPercentage.setText(percentage + "%");
    }

    /**
     * update and display progress bar based on Cycling History data
     */
    private void displayHistoryData() {
        viewModel.fetchCyclingHistory(userId);
        MutableLiveData<HashMap<String, Object>> cyclingHistory = viewModel.calculateMonthlyData(this);
        cyclingHistory.observe(getViewLifecycleOwner(), new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null) {
                    updateHistoryUi(hashMap);
                }
            }
        });
    }

    /**
     * Helper method to update UI based on new history data.
     * @param history Object containing history data.
     */
    private void updateHistoryUi(HashMap<String, Object> history) {
        Double monthDistance = (Double)history.get("monthDistance");
        binding.MonthlyGoalsProgressBar.setProgress(monthDistance.intValue());
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
