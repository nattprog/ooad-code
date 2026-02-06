package seminar.controllers;

import seminar.database.dao.EvaluatorAssignmentDAO;
import seminar.database.dao.EvaluationDAO;
import seminar.database.dao.SubmissionDAO;
import seminar.models.EvaluatorAssignment;
import seminar.models.Evaluation;
import seminar.models.Submission;
import java.util.List;

public class EvaluatorAssignmentController {
  private EvaluatorAssignmentDAO evaluatorAssignmentDAO;
  private EvaluationDAO evaluationDAO;
  private SubmissionDAO submissionDAO;

  public EvaluatorAssignmentController() {
    this.evaluatorAssignmentDAO = new EvaluatorAssignmentDAO();
    this.evaluationDAO = new EvaluationDAO();
    this.submissionDAO = new SubmissionDAO();
  }

  // Create evaluator assignment (with auto-created evaluations)
  public boolean createEvaluatorAssignment(int sessionId, int evaluatorUserId) {
    // Check if already assigned
    if (evaluatorAssignmentDAO.isEvaluatorAssignedToSession(sessionId, evaluatorUserId)) {
      return false;
    }

    // Create assignment
    EvaluatorAssignment assignment = new EvaluatorAssignment(sessionId, evaluatorUserId);
    boolean created = evaluatorAssignmentDAO.createEvaluatorAssignment(assignment);

    if (created) {
      // Auto-create evaluations for all submissions in the session
      List<Submission> submissions = submissionDAO.getSubmissionsBySession(sessionId);
      for (Submission submission : submissions) {
        Evaluation evaluation = new Evaluation(
            assignment.getEvaluatorAssignmentId(),
            submission.getSubmissionId());
        evaluationDAO.createEvaluation(evaluation);
      }
      return true;
    }

    return false;
  }

  // Get evaluator assignment by ID
  public EvaluatorAssignment getEvaluatorAssignmentById(int assignmentId) {
    return evaluatorAssignmentDAO.getEvaluatorAssignmentById(assignmentId);
  }

  // Get all evaluator assignments
  public List<EvaluatorAssignment> getAllEvaluatorAssignments() {
    return evaluatorAssignmentDAO.getAllEvaluatorAssignments();
  }

  // Get assignments by session
  public List<EvaluatorAssignment> getAssignmentsBySession(int sessionId) {
    return evaluatorAssignmentDAO.getAssignmentsBySession(sessionId);
  }

  // Get assignments by evaluator user ID
  public List<EvaluatorAssignment> getAssignmentsByEvaluatorUserId(int evaluatorUserId) {
    return evaluatorAssignmentDAO.getAssignmentsByEvaluatorUserId(evaluatorUserId);
  }

  // Check if evaluator is assigned to session
  public boolean isEvaluatorAssignedToSession(int sessionId, int evaluatorUserId) {
    return evaluatorAssignmentDAO.isEvaluatorAssignedToSession(sessionId, evaluatorUserId);
  }

  // Delete evaluator assignment
  public boolean deleteEvaluatorAssignment(int assignmentId) {
    return evaluatorAssignmentDAO.deleteEvaluatorAssignment(assignmentId);
  }

  // Remove evaluator from session
  public boolean removeEvaluatorFromSession(int sessionId, int evaluatorUserId) {
    return evaluatorAssignmentDAO.removeEvaluatorFromSession(sessionId, evaluatorUserId);
  }
}