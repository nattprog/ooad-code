package seminar.models;

import java.util.Date;

public class Seminar {
    private Integer seminarId;
    String title;
    String description;
    String location;
    private Date startTime;
    private Date endTime;

    // Constructor for creating new seminar (no ID yet)
    public Seminar(String title, String description, String location, Date startTime, Date endTime) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor for loading from database (with ID)
    public Seminar(Integer seminarId, String title, String description, String location, Date startTime, Date endTime) {
        this.seminarId = seminarId;
        this.title = title;
        this.description = description;
        this.location = location;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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