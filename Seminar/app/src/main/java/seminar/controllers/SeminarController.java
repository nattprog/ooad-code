package seminar.controllers;

import seminar.database.dao.SeminarDAO;
import seminar.models.Seminar;
import java.util.Date;
import java.util.List;

public class SeminarController {
    private SeminarDAO seminarDAO;

    public SeminarController() {
        this.seminarDAO = new SeminarDAO();
    }

    // Create seminar
    public boolean createSeminar(String title, String description, String location,
            Date startTime, Date endTime) {
        if (!validateSeminar(title, description, location, startTime, endTime)) {
            return false;
        }

        Seminar seminar = new Seminar(title, description, location, startTime, endTime);
        return seminarDAO.createSeminar(seminar);
    }

    // Get seminar by ID
    public Seminar getSeminarById(int seminarId) {
        return seminarDAO.getSeminarById(seminarId);
    }

    // Get all seminars
    public List<Seminar> getAllSeminars() {
        return seminarDAO.getAllSeminars();
    }

    // Get upcoming seminars
    public List<Seminar> getUpcomingSeminars() {
        return seminarDAO.getUpcomingSeminars();
    }

    // Get upcoming seminars (limited)
    public List<Seminar> getUpcomingSeminars(int limit) {
        List<Seminar> seminars = seminarDAO.getUpcomingSeminars();
        if (seminars.size() > limit) {
            return seminars.subList(0, limit);
        }
        return seminars;
    }

    // Update seminar
    public boolean updateSeminar(Seminar seminar) {
        if (!validateSeminar(seminar.getTitle(), seminar.getDescription(),
                seminar.getLocation(), seminar.getStartTime(), seminar.getEndTime())) {
            return false;
        }
        return seminarDAO.updateSeminar(seminar);
    }

    // Delete seminar
    public boolean deleteSeminar(int seminarId) {
        return seminarDAO.deleteSeminar(seminarId);
    }

    // Validation
    private boolean validateSeminar(String title, String description, String location,
            Date startTime, Date endTime) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        if (description == null || description.trim().isEmpty()) {
            return false;
        }
        if (location == null || location.trim().isEmpty()) {
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