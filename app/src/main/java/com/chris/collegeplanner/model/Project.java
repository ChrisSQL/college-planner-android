package com.chris.collegeplanner.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chris on 03/02/2015.
 */

public class Project {

    private int projectID;
    private String projectSubject;
    private String projectType;
    private String projectTitle;
    private String projectWorth;
    private Date projectDueDate;
    private String projectDetails;

    public Project(int projectID, String projectSubject, String projectType, String projectTitle, String projectWorth, Date projectDueDate, String projectDetails) {
        this.projectID = projectID;
        this.projectSubject = projectSubject;
        this.projectType = projectType;
        this.projectTitle = projectTitle;
        this.projectWorth = projectWorth;
        this.projectDueDate = projectDueDate;
        this.projectDetails = projectDetails;
    }

    public Project(){

    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getProjectSubject() {
        return projectSubject;
    }

    public void setProjectSubject(String projectSubject) {
        this.projectSubject = projectSubject;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectWorth() {
        return projectWorth;
    }

    public void setProjectWorth(String projectWorth) {
        this.projectWorth = projectWorth;
    }

    public Date getProjectDueDate() {
        return projectDueDate;
    }

    public void setProjectDueDate(String projectDueDate) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(projectDueDate);

        this.projectDueDate = date;
    }

    public String getProjectDetails() {
        return projectDetails;
    }

    public void setProjectDetails(String projectDetails) {
        this.projectDetails = projectDetails;
    }
}
