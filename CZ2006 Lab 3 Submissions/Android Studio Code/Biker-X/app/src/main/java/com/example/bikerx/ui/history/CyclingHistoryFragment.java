package com.example.bikerx.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikerx.MainActivity;
import com.example.bikerx.databinding.FragmentHistoryBinding;
import com.example.bikerx.entities.Goal;

import java.util.ArrayList;
import java.util.HashMap;

/**Displays past cycling sessions of the user. Also displays user progress towards their goals set in GoalsFragment.
 *
 */
public class CyclingHistoryFragment extends Fragment {

    private CyclingHistoryViewModel viewModel;
    private FragmentHistoryBinding mBinding;
    private RecyclerView.LayoutManager layoutManager;
    private CyclingHistoryAdapter adapter;
    private String userId;

    /**Initialises CyclingHistoryFragment. The CyclingHistoryViewModel and FragmentHistoryBinding is instantiated here.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CyclingHistoryViewModel.class);

        mBinding = FragmentHistoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    /**Initiates behaviour required of CyclingHistoryFragment. This method is called after onCreateView.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userId = ((MainActivity)getActivity()).getUserId();
        displayGoalsData();
        displayCyclingHistory();
        displayMonthlyData();
        bindButtons();
    }

    /**
     * Fetch and display user Goal data from the viewModel.
     */
    private void displayGoalsData() {//temp
        mBinding.goalsChronometer.setText(getChronometerDisplay(0L));
        viewModel.fetchGoals(userId);
        viewModel.getGoals().observe(getViewLifecycleOwner(), new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                if (goal != null) {
                    mBinding.distanceGoalsFloat.setText(String.format("%.2f", goal.getDistance()));
                    mBinding.distanceProgressBar.setMax((int) goal.getDistance());

                    mBinding.goalsChronometer.setText(getChronometerDisplay(goal.getDuration()));
                    mBinding.timeProgressBar.setMax((int) goal.getDuration());
                }
            }
        });

    }

    /**
     * Fetch and display user monthly history data from the viewModel.
     */
    private void displayMonthlyData() {
        viewModel.calculateMonthlyData(this).observe(getViewLifecycleOwner(), new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                if (hashMap != null) {
                    Double monthDistance = (Double)hashMap.get("monthDistance");
                    mBinding.distanceDetailsFloat.setText(String.format("%.2f", monthDistance));
                    mBinding.distanceProgressBar.setProgress(monthDistance.intValue());

                    long monthDuration = (Long) hashMap.get("monthDuration");
                    mBinding.chronometer.setText(getChronometerDisplay(monthDuration));
                    mBinding.timeProgressBar.setProgress((int) monthDuration);
                }

            }
        });
    }

    /**Helper function to format time (in milliseconds) to Chronometer display.
     * @param monthDuration Time to be formatted.
     * @return Returns a String, representing time formatted as "HHh MMm".
     */
    private String getChronometerDisplay(Long monthDuration) {
        int h = (int) ((monthDuration / 1000) / 3600);
        int m = (int) (((monthDuration / 1000) / 60) % 60);

        String mString = m >= 10 ? Integer.toString(m) : "0"+Integer.toString(m);
        return String.format("%dh %sm", h, mString);
    }

    /**
     * Fetches and displays past cycling sessions from the viewModel.
     * CyclingHistoryAdapter is used to convert ArrayList into a UI element.
     */
    private void displayCyclingHistory() {
        viewModel.fetchCyclingHistory(userId);
        viewModel.getCyclingHistory().observe(getViewLifecycleOwner(), new Observer<ArrayList<CyclingHistory>>() {
            @Override
            public void onChanged(ArrayList<CyclingHistory> cyclingHistory) {
                if (cyclingHistory != null) {
                    adapter = new CyclingHistoryAdapter(cyclingHistory);
                    layoutManager = new LinearLayoutManager(getActivity());
                    mBinding.recyclerView.setLayoutManager(layoutManager);
                    mBinding.recyclerView.setAdapter(adapter);
                } else {
                    mBinding.noHistoryAlert.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * This method sets the logic of the buttons in the UI.
     */
    public void bindButtons() {
        mBinding.editGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = CyclingHistoryFragmentDirections.actionNavigationHistoryToGoalsFragment();
                Navigation.findNavController(v).navigate(action);
            }
        });
    }
}