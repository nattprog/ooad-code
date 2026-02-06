package seminar_manager.controllers;

import java.util.List;

import seminar_manager.database.dao.EvaluationDAO;
import seminar_manager.models.Evaluation;

public class EvaluationController {
    private EvaluationDAO evaluationDAO;

    public EvaluationController() {
        this.evaluationDAO = new EvaluationDAO();
    }

    // Get evaluation by ID
    public Evaluation getEvaluationById(int evaluationId) {
        return evaluationDAO.getEvaluationById(evaluationId);
    }

    // Get all evaluations
    public List<Evaluation> getAllEvaluations() {
        return evaluationDAO.getAllEvaluations();
    }

    // Get evaluations by submission
    public List<Evaluation> getEvaluationsBySubmission(int submissionId) {
        return evaluationDAO.getEvaluationsBySubmission(submissionId);
    }

    // Get evaluations by assignment
    public List<Evaluation> getEvaluationsByAssignment(int assignmentId) {
        return evaluationDAO.getEvaluationsByAssignment(assignmentId);
    }

    // Get average score for submission
    public Double getAverageScoreForSubmission(int submissionId) {
        return evaluationDAO.getAverageScoreForSubmission(submissionId);
    }

    // Update evaluation (fill in scores)
    public boolean updateEvaluation(Evaluation evaluation) {
        if (!validateEvaluation(evaluation)) {
            return false;
        }
        return evaluationDAO.updateEvaluation(evaluation);
    }

    // Validation
    private boolean validateEvaluation(Evaluation evaluation) {
        if (evaluation.getProblemClarityScore() != null) {
            if (evaluation.getProblemClarityScore() < 1 || evaluation.getProblemClarityScore() > 10) {
                return false;
            }
        }
        if (evaluation.getMethodologyScore() != null) {
            if (evaluation.getMethodologyScore() < 1 || evaluation.getMethodologyScore() > 10) {
                return false;
            }
        }
        if (evaluation.getResultsScore() != null) {
            if (evaluation.getResultsScore() < 1 || evaluation.getResultsScore() > 10) {
                return false;
            }
        }
        if (evaluation.getPresentationScore() != null) {
            if (evaluation.getPresentationScore() < 1 || evaluation.getPresentationScore() > 10) {
                return false;
            }
        }
        return true;
    }
}