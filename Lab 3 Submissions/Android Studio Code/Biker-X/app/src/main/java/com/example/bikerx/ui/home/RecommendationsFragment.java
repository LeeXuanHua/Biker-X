package com.example.bikerx.ui.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;

import com.example.bikerx.R;
import com.example.bikerx.control.DBManager;
import com.example.bikerx.databinding.FragmentHomeBinding;
import com.example.bikerx.databinding.RecommendationsFragmentBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all routes and allows for searching of routes
 */
public class RecommendationsFragment extends Fragment implements HomeRecommendationsAdapter.MyViewHolder.HomeRouteListener {
    private RecommendationsFragmentBinding binding;
    private HomeRecommendationsAdapter adapter;
    private RecommendationsViewModel viewModel;

    private List<Route> routeList;

    boolean isKeyboardShowing = false;

    /**
     * onCreateView initialize viewModel and RecommendationsFragmentBinding
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        binding = RecommendationsFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    /**
     * Call functions required for fragment functionality
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindButton();
        bindSearchBar();
        displayRoutes();
    }

    private void bindButton() {
        binding.ownRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = RecommendationsFragmentDirections.actionRecommendationsFragmentToStartCyclingFragment(null);
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    /**
     * Fetch routes and display routes in recycler view
     */
    private void displayRoutes() {
        viewModel.fetchRoutes();
        viewModel.getRoutes().observe(this, new Observer<ArrayList<Route>>() {
            @Override
            public void onChanged(ArrayList<Route> homeRoutes) {
                if (homeRoutes != null) {
                    routeList = homeRoutes;
                    adapter = new HomeRecommendationsAdapter(homeRoutes, RecommendationsFragment.this);
                    binding.recommendationsRecyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    binding.recommendationsRecyclerView.setLayoutManager(layoutManager);
                    binding.recommendationsRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    /**
     * Navigate to start cycling fragment and pass route ID to fragment after click
     * @param position value of click position
     */
    @Override
    public void homeRouteClick(int position) {
        NavDirections action = RecommendationsFragmentDirections.actionRecommendationsFragmentToStartCyclingFragment(routeList.get(position).getRouteId());
        Navigation.findNavController(this.getView()).navigate(action);
    }

    /**
     * Bind the search bar
     */
    private void bindSearchBar() {
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        binding.getRoot().getWindowVisibleDisplayFrame(r);
                        int screenHeight =  binding.getRoot().getRootView().getHeight();

                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) {
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                binding.ownRouteButton.setVisibility(View.GONE);
                            }
                        }
                        else {
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                binding.ownRouteButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

}