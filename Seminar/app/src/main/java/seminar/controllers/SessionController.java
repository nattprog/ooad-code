package seminar.controllers;

import seminar.database.dao.SessionDAO;
import seminar.database.dao.TimeSlotDAO;
import seminar.models.Session;
import seminar.models.enums.PresentationType;
import java.util.Date;
import java.util.List;

public class SessionController {
    private SessionDAO sessionDAO;
    private TimeSlotDAO timeSlotDAO;

    public SessionController() {
        this.sessionDAO = new SessionDAO();
        this.timeSlotDAO = new TimeSlotDAO();
    }

    // Create session (with auto-generated time slots)
    public boolean createSession(int seminarId, PresentationType presentationType,
            int timeSlotsCount, int timeSlotsDuration,
            Date startTime, Date endTime) {
        if (!validateSession(seminarId, timeSlotsCount, timeSlotsDuration, startTime, endTime)) {
            return false;
        }

        Session session = new Session(seminarId, presentationType, timeSlotsCount,
                timeSlotsDuration, startTime, endTime);

        // Create session
        boolean sessionCreated = sessionDAO.createSession(session);

        if (sessionCreated) {
            // Auto-generate time slots
            return timeSlotDAO.generateTimeSlotsForSession(
                    session.getSessionId(), startTime, timeSlotsCount, timeSlotsDuration);
        }

        return false;
    }

    // Get session by ID
    public Session getSessionById(int sessionId) {
        return sessionDAO.getSessionById(sessionId);
    }

    // Get all sessions
    public List<Session> getAllSessions() {
        return sessionDAO.getAllSessions();
    }

    // Get sessions by seminar
    public List<Session> getSessionsBySeminar(int seminarId) {
        return sessionDAO.getSessionsBySeminar(seminarId);
    }

    // Get sessions by presentation type
    public List<Session> getSessionsByPresentationType(PresentationType type) {
        return sessionDAO.getSessionsByPresentationType(type);
    }

    // Update session
    public boolean updateSession(Session session) {
        if (!validateSession(session.getSeminarId(), session.getTimeSlotsCount(),
                session.getTimeSlotsDuration(), session.getStartTime(),
                session.getEndTime())) {
            return false;
        }

        // Delete old time slots
        timeSlotDAO.deleteTimeSlotsBySession(session.getSessionId());

        // Update session
        boolean updated = sessionDAO.updateSession(session);

        if (updated) {
            // Regenerate time slots
            return timeSlotDAO.generateTimeSlotsForSession(
                    session.getSessionId(), session.getStartTime(),
                    session.getTimeSlotsCount(), session.getTimeSlotsDuration());
        }

        return false;
    }

    // Delete session
    public boolean deleteSession(int sessionId) {
        return sessionDAO.deleteSession(sessionId);
    }

    // Validation
    private boolean validateSession(int seminarId, int timeSlotsCount,
            int timeSlotsDuration, Date startTime, Date endTime) {
        if (seminarId <= 0) {
            return false;
        }
        if (timeSlotsCount <= 0) {
            return false;
        }
        if (timeSlotsDuration != 15 && timeSlotsDuration != 30 &&
                timeSlotsDuration != 45 && timeSlotsDuration != 60) {
            return false;
        }
        if (startTime == null || endTime == null) {
            return false;
        }
        if (endTime.before(startTime)) {
            return false;
        }
        return true;
    }
}