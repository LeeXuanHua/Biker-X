package com.example.bikerx.ui.home;

import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikerx.R;
import com.example.bikerx.databinding.RecommendationsRowBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter class that inherits RecyclerView.Adapter and implements Filterable
 * This adapter is used to display routes and search routes in HomeFragment
 */
public class HomeRecommendationsAdapter extends RecyclerView.Adapter<HomeRecommendationsAdapter.MyViewHolder> implements Filterable {

    private List<Route> routeList;
    private List<Route> filteredRouteList;
    private MyViewHolder.HomeRouteListener mHomeRouteListener;

    /**
     * Class for UI elements in routes
     * Implements onClickListener to listen for clicks
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RecommendationsRowBinding binding;
        HomeRouteListener homeRouteListener;

        public MyViewHolder(RecommendationsRowBinding b, HomeRouteListener homeRouteListener){
            super(b.getRoot());
            binding = b;
            this.homeRouteListener = homeRouteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            homeRouteListener.homeRouteClick(getBindingAdapterPosition());
        }

        public interface HomeRouteListener{
            void homeRouteClick(int position);
        }
    }

    /**
     * Constructor for adapter
     * @param routeList List of Route class to be displayed
     * @param homeRouteListener listener to listen for clicks
     */
    public HomeRecommendationsAdapter(List<Route> routeList, MyViewHolder.HomeRouteListener homeRouteListener) {
        this.routeList = routeList;
        this.filteredRouteList = routeList;
        this.mHomeRouteListener = homeRouteListener;
    }


    @NonNull
    @Override
    //inflating the view
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(RecommendationsRowBinding.inflate(LayoutInflater.from(parent.getContext())), mHomeRouteListener);
    }

    /**
     * Override onBindViewHolder and binds UI elements to recycler view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Route r = filteredRouteList.get(position);
        Double avgRatings = getAverageRating(r.getRatings());
        holder.binding.routeRating.setText(String.format("(%.1f)", avgRatings));
        holder.binding.ratingBar.setRating(avgRatings.floatValue());
        holder.binding.routeName.setText(r.getRouteName());
        TypedArray routeFlags = holder.binding.getRoot().getResources().obtainTypedArray(R.array.route_flags);
        holder.binding.routeImg.setImageResource(routeFlags.getResourceId(r.getImageId(), 0));
    }

    /**
     * Get the size of list of routes
     * @return the size of the list of routes
     */
    @Override
    public int getItemCount() {
        return filteredRouteList.size();
    }

    /**
     * Calculate the average rating of a route
     * @param ratings Complete list of ratings submitted for this route
     * @return the calculation for average rating
     */
    private Double getAverageRating(ArrayList<HashMap<String, Object>> ratings) {
        Double total = 0.0;
        if (ratings != null) {
            for (HashMap<String, Object> rating: ratings) {
                total += (Double)rating.get("rating");
            }
            return total/ratings.size();
        }
        return 0.0;
    }

    /**
     * Override getFilter and filters routes based on input query
     * @return list of routes that corresponds to input query
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredRouteList = routeList;
                } else {
                    List<Route> filteredList = new ArrayList<>();
                    for (Route route : routeList) {
                        if (route.getRouteName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(route);
                        }
                    }
                    filteredRouteList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredRouteList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredRouteList = (ArrayList<Route>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}