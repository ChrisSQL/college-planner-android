package com.chris.collegeplanner.model;

import java.util.Date;

/**
 * Created by Chris on 26/08/2015.
 */
public class User {

    int id;
    Date registrationDate;
        private String name, email, password, salt, course;

    public User(String name, String email, String password, String salt, String course, int id, Date registrationDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.course = course;
        this.id = id;
        this.registrationDate = registrationDate;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
