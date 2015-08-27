package com.chris.collegeplanner.model;

/**
 * Created by Chris on 07/03/2015.
 */
public class College {

    private int collegeId;
    private String collegeName;

    public College(int collegeId, String collegeName) {
        this.collegeId = collegeId;
        this.collegeName = collegeName;
    }

    public College() {

    }

    public int getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(int collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {

        this.collegeName = collegeName;
    }
}
