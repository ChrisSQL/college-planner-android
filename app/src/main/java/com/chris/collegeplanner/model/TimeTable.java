package com.chris.collegeplanner.model;

/**
 * Created by Chris on 22/02/2015.
 */
public class TimeTable {

    int _id;
    String TimetableURL;

    public TimeTable(int _id, String timetableURL) {
        this._id = _id;
        TimetableURL = timetableURL;
    }

    public TimeTable() {


    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTimetableURL() {
        return TimetableURL;
    }

    public void setTimetableURL(String timetableURL) {
        TimetableURL = timetableURL;
    }
}
