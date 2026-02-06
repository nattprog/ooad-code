package seminar.controllers;

import seminar.database.dao.TimeSlotDAO;
import seminar.models.TimeSlot;
import java.util.List;

public class TimeSlotController {
  private TimeSlotDAO timeSlotDAO;

  public TimeSlotController() {
    this.timeSlotDAO = new TimeSlotDAO();
  }

  // Get time slot by ID
  public TimeSlot getTimeSlotById(int timeSlotId) {
    return timeSlotDAO.getTimeSlotById(timeSlotId);
  }

  // Get all time slots
  public List<TimeSlot> getAllTimeSlots() {
    return timeSlotDAO.getAllTimeSlots();
  }

  // Get time slots by session
  public List<TimeSlot> getTimeSlotsBySession(int sessionId) {
    return timeSlotDAO.getTimeSlotsBySession(sessionId);
  }

  // Get available time slots by session
  public List<TimeSlot> getAvailableTimeSlotsBySession(int sessionId) {
    return timeSlotDAO.getAvailableTimeSlotsBySession(sessionId);
  }

  // Get time slot by submission
  public TimeSlot getTimeSlotBySubmission(int submissionId) {
    return timeSlotDAO.getTimeSlotBySubmission(submissionId);
  }

  // Assign submission to time slot
  public boolean assignSubmissionToTimeSlot(int timeSlotId, int submissionId) {
    return timeSlotDAO.assignSubmissionToTimeSlot(timeSlotId, submissionId);
  }

  // Unassign submission from time slot
  public boolean unassignSubmissionFromTimeSlot(int timeSlotId) {
    return timeSlotDAO.unassignSubmissionFromTimeSlot(timeSlotId);
  }

  // Update time slot
  public boolean updateTimeSlot(TimeSlot timeSlot) {
    return timeSlotDAO.updateTimeSlot(timeSlot);
  }
}