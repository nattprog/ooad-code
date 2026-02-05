package seminar.models;

import seminar.models.enums.PresentationType;
import java.util.Date;

public class Session {
    private Integer sessionId;
    private Integer seminarId;
    private PresentationType presentationType;
    private Integer timeSlotsCount; // should be session endtime - starttime divisors to result in multiples of 15
                                    // (i.e. should only result in nice timeslots of multiples of 15)
    private Integer timeSlotsDuration; // 15, 30, 45, 60 minutes
    private Date startTime; // more than seminar starttime, less than session endtime
    private Date endTime; // more than session starttime, less than seminar endtime

    // Constructor for creating new session (no ID yet)
    public Session(Integer seminarId, PresentationType presentationType, Integer timeSlotsCount,
            Integer timeSlotsDuration, Date startTime, Date endTime) {
        this.seminarId = seminarId;
        this.presentationType = presentationType;
        this.timeSlotsCount = timeSlotsCount;
        this.timeSlotsDuration = timeSlotsDuration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor for loading from database (with ID)
    public Session(Integer sessionId, Integer seminarId, PresentationType presentationType,
            Integer timeSlotsCount, Integer timeSlotsDuration, Date startTime, Date endTime) {
        this.sessionId = sessionId;
        this.seminarId = seminarId;
        this.presentationType = presentationType;
        this.timeSlotsCount = timeSlotsCount;
        this.timeSlotsDuration = timeSlotsDuration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getSeminarId() {
        return seminarId;
    }

    public void setSeminarId(Integer seminarId) {
        this.seminarId = seminarId;
    }

    public PresentationType getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(PresentationType presentationType) {
        this.presentationType = presentationType;
    }

    public Integer getTimeSlotsCount() {
        return timeSlotsCount;
    }

    public void setTimeSlotsCount(Integer timeSlotsCount) {
        this.timeSlotsCount = timeSlotsCount;
    }

    public Integer getTimeSlotsDuration() {
        return timeSlotsDuration;
    }

    public void setTimeSlotsDuration(Integer timeSlotsDuration) {
        this.timeSlotsDuration = timeSlotsDuration;
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
        return "Session{" +
                "sessionId=" + sessionId +
                ", seminarId=" + seminarId +
                ", presentationType=" + presentationType +
                ", timeSlotsCount=" + timeSlotsCount +
                '}';
    }
}