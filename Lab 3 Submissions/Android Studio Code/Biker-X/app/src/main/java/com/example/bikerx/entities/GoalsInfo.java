package com.example.bikerx.entities;

/**
 *GoalsInfo class to store the goals data of the user
 *
 */
public class GoalsInfo {
    /**
     * monthlyDurationInHours monthly cycling goal (in hours) set by user
     */
    private int monthlyDurationInHours;
    /**
     * monthlyDistanceInKm: monthly distance cycling goals (in km) set by user
     */
    private int monthlyDistanceInKm;

    public GoalsInfo(){

    }

    /**
     * get and set methods for the goals (monthlyDurationInHours and monthlyDistanceInKm)
     *
     */
    public int getMonthlyDurationInHours() {
        return monthlyDurationInHours;
    }

    public void setMonthlyDurationInHours(int monthlyDurationInHours) {
        this.monthlyDurationInHours = monthlyDurationInHours;
    }

    public int getMonthlyDistanceInKm() {
        return monthlyDistanceInKm;
    }

    public void setMonthlyDistanceInKm(int monthlyDistanceInKm) {
        this.monthlyDistanceInKm = monthlyDistanceInKm;
    }


}
