package com.chris.collegeplanner.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chris on 03/02/2015.
 */

public class Project {

    private int ProjectID;
    private String ProjectSubject;
    private String ProjectType;
    private String ProjectTitle;
    private String ProjectWorth;
    private Date ProjectDueDate;
    private String ProjectDetails;

    public Project(int projectID, String projectSubject, String projectType, String projectTitle, String projectWorth, Date projectDueDate, String projectDetails) {
        ProjectID = projectID;
        ProjectSubject = projectSubject;
        ProjectType = projectType;
        ProjectTitle = projectTitle;
        ProjectWorth = projectWorth;
        ProjectDueDate = projectDueDate;
        ProjectDetails = projectDetails;
    }

    public Project(){

    }

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public String getProjectSubject() {
        return ProjectSubject;
    }

    public void setProjectSubject(String projectSubject) {
        ProjectSubject = projectSubject;
    }

    public String getProjectType() {
        return ProjectType;
    }

    public void setProjectType(String projectType) {
        ProjectType = projectType;
    }

    public String getProjectTitle() {
        return ProjectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        ProjectTitle = projectTitle;
    }

    public String getProjectWorth() {
        return ProjectWorth;
    }

    public void setProjectWorth(String projectWorth) {
        ProjectWorth = projectWorth;
    }

    public Date getProjectDueDate() {
        return ProjectDueDate;
    }

    public void setProjectDueDate(String projectDueDate) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(projectDueDate);

        ProjectDueDate = date;
    }

    public String getProjectDetails() {
        return ProjectDetails;
    }

    public void setProjectDetails(String projectDetails) {
        ProjectDetails = projectDetails;
    }
}
