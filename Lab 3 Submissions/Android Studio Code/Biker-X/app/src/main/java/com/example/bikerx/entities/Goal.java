package com.example.bikerx.entities;

public class Goal {
    private double distance;
    private long duration;

    public Goal() {
        this.distance = 0;
        this.duration = 0;
    }

    public Goal(double distance, long duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
