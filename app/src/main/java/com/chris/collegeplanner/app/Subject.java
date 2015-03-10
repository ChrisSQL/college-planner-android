package com.chris.collegeplanner.app;

/**
 * Created by Chris on 09/03/2015.
 */
public class Subject {

    private String subjectName;

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public Subject() {
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
