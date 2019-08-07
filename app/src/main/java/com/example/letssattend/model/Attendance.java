package com.example.letssattend.model;

public class Attendance {
    String date;
    String time;
    String userid;
    String name;
    public Attendance(){

    }
    public Attendance(String date, String time, String userid, String name, String course, String date_userid) {
        this.date = date;
        this.time = time;
        this.userid = userid;
        this.name = name;
        this.course = course;
        this.date_userid = date_userid;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", course='" + course + '\'' +
                ", date_userid='" + date_userid + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDate_userid() {
        return date_userid;
    }

    public void setDate_userid(String date_userid) {
        this.date_userid = date_userid;
    }

    String course;
    String date_userid;

}
