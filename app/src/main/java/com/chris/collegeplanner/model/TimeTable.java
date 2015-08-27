package com.chris.collegeplanner.model;

/**
 * Created by Chris on 22/02/2015.
 */
public class TimeTable {

    int TimeTableID;
    String image;

    public TimeTable(int projectID, String image) {
        this.TimeTableID = projectID;
        this.image = image;
    }

    public TimeTable() {

    }

    public int getTimeTableID() {
        return TimeTableID;
    }

    public void setTimeTableID(int timeTableID) {
        this.TimeTableID = timeTableID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
