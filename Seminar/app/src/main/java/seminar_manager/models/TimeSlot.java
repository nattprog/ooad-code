package seminar_manager.models;

import java.util.Date;

public class TimeSlot {
  private Integer timeSlotId;
  private Integer sessionId;
  private Integer submissionId;
  private Date startTime;
  private Date endTime;

  // Constructor for creating new timeslot (no ID yet)
  public TimeSlot(Integer sessionId, Date startTime, Date endTime) {
    this.sessionId = sessionId;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  // Constructor for loading from database (with ID)
  public TimeSlot(Integer timeSlotId, Integer sessionId, Integer submissionId,
      Date startTime, Date endTime) {
    this.timeSlotId = timeSlotId;
    this.sessionId = sessionId;
    this.submissionId = submissionId;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  // Getters and Setters
  public Integer getTimeSlotId() {
    return timeSlotId;
  }

  public void setTimeSlotId(Integer timeSlotId) {
    this.timeSlotId = timeSlotId;
  }

  public Integer getSessionId() {
    return sessionId;
  }

  public void setSessionId(Integer sessionId) {
    this.sessionId = sessionId;
  }

  public Integer getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(Integer submissionId) {
    this.submissionId = submissionId;
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
    return "TimeSlot{" +
        "timeSlotId=" + timeSlotId +
        ", sessionId=" + sessionId +
        ", submissionId=" + submissionId +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        '}';
  }
}