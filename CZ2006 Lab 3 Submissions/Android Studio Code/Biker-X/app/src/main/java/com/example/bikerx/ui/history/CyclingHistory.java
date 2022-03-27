package com.example.bikerx.ui.history;

/**
 * Class to store cycling history data.
 */
public class CyclingHistory {
    /**
     * Date and time when cycling session ended. Stored as a String.
     */
    private String date;
    /**
     * Total distance travelled during the cycling session. Stored as a String.
     */
    private String formattedDistance;
    /**
     * Total duration of the cycling session.
     */
    private long duration;

    public CyclingHistory(String date, String formattedDistance, long duration) {
        this.date = date;
        this.formattedDistance = formattedDistance;
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormattedDistance() {
        return formattedDistance;
    }

    public void setFormattedDistance(String formattedDistance) {
        this.formattedDistance = formattedDistance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
