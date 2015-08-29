package com.chris.collegeplanner.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chris on 03/02/2015.
 */

public class Project {

    private int _id;
    private String projectSubject;
    private String projectType;
    private String projectTitle;
    private String projectWorth;
    private Date projectDueDate;
    private String projectDetails;
    private String projectEmail;

    public void setProjectDueDate(Date projectDueDate) {
        this.projectDueDate = projectDueDate;
    }

    public String getProjectEmail() {
        return projectEmail;
    }

    public void setProjectEmail(String projectEmail) {
        this.projectEmail = projectEmail;
    }

    public Project(int _id, String projectSubject, String projectType, String projectTitle, String projectWorth, Date projectDueDate, String projectDetails, String projectEmail) {
        this._id = _id;
        this.projectSubject = projectSubject;
        this.projectType = projectType;
        this.projectTitle = projectTitle;
        this.projectWorth = projectWorth;
        this.projectDueDate = projectDueDate;
        this.projectDetails = projectDetails;
        this.projectEmail = projectEmail;
    }

    public Project(){

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    @Override
    public String toString() {
        return "Project{" +
                "_id=" + _id +
                ", projectSubject='" + projectSubject + '\'' +
                ", projectType='" + projectType + '\'' +
                ", projectTitle='" + projectTitle + '\'' +
                ", projectWorth='" + projectWorth + '\'' +
                ", projectDueDate=" + projectDueDate +
                ", projectDetails='" + projectDetails + '\'' +
                ", projectEmail='" + projectEmail + '\'' +
                '}';
    }
}
