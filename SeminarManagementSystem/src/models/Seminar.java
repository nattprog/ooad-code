package models;

import java.util.Date;

public class Seminar {
    private Integer seminarId;
    private Date startTime;
    private Date endTime;

    // Constructor for creating new seminar (no ID yet)
    public Seminar(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor for loading from database (with ID)
    public Seminar(Integer seminarId, Date startTime, Date endTime) {
        this.seminarId = seminarId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Integer getSeminarId() {
        return seminarId;
    }

    public void setSeminarId(Integer seminarId) {
        this.seminarId = seminarId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Seminar{" +
                "seminarId=" + seminarId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}