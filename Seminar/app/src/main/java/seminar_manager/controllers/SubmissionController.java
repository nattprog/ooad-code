package seminar_manager.controllers;

import java.util.List;

import seminar_manager.database.dao.SubmissionDAO;
import seminar_manager.models.Submission;
import seminar_manager.models.enums.PresentationType;
import seminar_manager.models.enums.SubmissionStatus;

public class SubmissionController {
  private SubmissionDAO submissionDAO;

  public SubmissionController() {
    this.submissionDAO = new SubmissionDAO();
  }

  // Create submission
  public boolean createSubmission(int seminarId, int studentUserId, String researchTitle,
      String researchAbstract, String supervisorName,
      PresentationType presentationType) {
    if (!validateSubmission(researchTitle, researchAbstract, supervisorName)) {
      return false;
    }

    Submission submission = new Submission(seminarId, studentUserId, researchTitle,
        researchAbstract, supervisorName, presentationType);
    return submissionDAO.createSubmission(submission);
  }

  // Get submission by ID
  public Submission getSubmissionById(int submissionId) {
    return submissionDAO.getSubmissionById(submissionId);
  }

  // Get all submissions
  public List<Submission> getAllSubmissions() {
    return submissionDAO.getAllSubmissions();
  }

  // Get submissions by student user ID
  public List<Submission> getSubmissionsByStudentUserId(int studentUserId) {
    return submissionDAO.getSubmissionsByStudentUserId(studentUserId);
  }

  // Get submissions by seminar
  public List<Submission> getSubmissionsBySeminar(int seminarId) {
    return submissionDAO.getSubmissionsBySeminar(seminarId);
  }

  // Get submissions by session
  public List<Submission> getSubmissionsBySession(int sessionId) {
    return submissionDAO.getSubmissionsBySession(sessionId);
  }

  // Get unassigned submissions
  public List<Submission> getUnassignedSubmissions(int seminarId) {
    return submissionDAO.getUnassignedSubmissions(seminarId);
  }

  // Get submissions by status
  public List<Submission> getSubmissionsByStatus(SubmissionStatus status) {
    return submissionDAO.getSubmissionsByStatus(status);
  }

  // Update submission
  public boolean updateSubmission(Submission submission) {
    if (!validateSubmission(submission.getResearchTitle(),
        submission.getResearchAbstract(),
        submission.getSupervisorName())) {
      return false;
    }
    return submissionDAO.updateSubmission(submission);
  }

  // Update submission status
  public boolean updateSubmissionStatus(int submissionId, SubmissionStatus status) {
    return submissionDAO.updateStatus(submissionId, status);
  }

  // Assign submission to session
  public boolean assignSubmissionToSession(int submissionId, int sessionId) {
    return submissionDAO.assignToSession(submissionId, sessionId);
  }

  // Delete submission
  public boolean deleteSubmission(int submissionId) {
    return submissionDAO.deleteSubmission(submissionId);
  }

  // Check if student already has submission for seminar
  public boolean hasSubmissionForSeminar(int studentUserId, int seminarId) {
    List<Submission> submissions = submissionDAO.getSubmissionsByStudentUserId(studentUserId);
    for (Submission sub : submissions) {
      if (sub.getSeminarId() == seminarId) {
        return true;
      }
    }
    return false;
  }

  // Validation
  private boolean validateSubmission(String researchTitle, String researchAbstract,
      String supervisorName) {
    if (researchTitle == null || researchTitle.trim().isEmpty()) {
      return false;
    }
    if (researchAbstract == null || researchAbstract.length() < 100) {
      return false;
    }
    if (supervisorName == null || supervisorName.trim().isEmpty()) {
      return false;
    }
    return true;
  }
}