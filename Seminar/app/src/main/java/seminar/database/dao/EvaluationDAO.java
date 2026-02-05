package seminar.database.dao;

import seminar.database.DatabaseManager;
import seminar.models.Evaluation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDAO {

  // CREATE
  public boolean createEvaluation(Evaluation evaluation) {
    String sql = "INSERT INTO evaluations (evaluator_assignment_id, submission_id, " +
        "problem_clarity_score, methodology_score, results_score, presentation_score, " +
        "total_score, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      pstmt.setInt(1, evaluation.getEvaluatorAssignmentId());
      pstmt.setInt(2, evaluation.getSubmissionId());

      if (evaluation.getProblemClarityScore() != null) {
        pstmt.setInt(3, evaluation.getProblemClarityScore());
      } else {
        pstmt.setNull(3, Types.INTEGER);
      }

      if (evaluation.getMethodologyScore() != null) {
        pstmt.setInt(4, evaluation.getMethodologyScore());
      } else {
        pstmt.setNull(4, Types.INTEGER);
      }

      if (evaluation.getResultsScore() != null) {
        pstmt.setInt(5, evaluation.getResultsScore());
      } else {
        pstmt.setNull(5, Types.INTEGER);
      }

      if (evaluation.getPresentationScore() != null) {
        pstmt.setInt(6, evaluation.getPresentationScore());
      } else {
        pstmt.setNull(6, Types.INTEGER);
      }

      if (evaluation.getTotalScore() != null) {
        pstmt.setInt(7, evaluation.getTotalScore());
      } else {
        pstmt.setNull(7, Types.INTEGER);
      }

      pstmt.setString(8, evaluation.getComments());

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            evaluation.setEvaluationId(generatedKeys.getInt(1));
          }
        }
        return true;
      }
      return false;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // READ - Get by ID
  public Evaluation getEvaluationById(int evaluationId) {
    String sql = "SELECT * FROM evaluations WHERE evaluation_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, evaluationId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToEvaluation(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all evaluations
  public List<Evaluation> getAllEvaluations() {
    String sql = "SELECT * FROM evaluations";
    List<Evaluation> evaluations = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        evaluations.add(mapResultSetToEvaluation(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return evaluations;
  }

  // READ - Get evaluations by submission
  public List<Evaluation> getEvaluationsBySubmission(int submissionId) {
    String sql = "SELECT * FROM evaluations WHERE submission_id = ?";
    List<Evaluation> evaluations = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, submissionId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        evaluations.add(mapResultSetToEvaluation(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return evaluations;
  }

  // READ - Get evaluations by evaluator assignment
  public List<Evaluation> getEvaluationsByAssignment(int assignmentId) {
    String sql = "SELECT * FROM evaluations WHERE evaluator_assignment_id = ?";
    List<Evaluation> evaluations = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, assignmentId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        evaluations.add(mapResultSetToEvaluation(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return evaluations;
  }

  // READ - Get average score for submission
  public Double getAverageScoreForSubmission(int submissionId) {
    String sql = "SELECT AVG(total_score) as avg_score FROM evaluations " +
        "WHERE submission_id = ? AND total_score IS NOT NULL";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, submissionId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        double avgScore = rs.getDouble("avg_score");
        return rs.wasNull() ? null : avgScore;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // UPDATE
  public boolean updateEvaluation(Evaluation evaluation) {
    String sql = "UPDATE evaluations SET evaluator_assignment_id = ?, submission_id = ?, " +
        "problem_clarity_score = ?, methodology_score = ?, results_score = ?, " +
        "presentation_score = ?, total_score = ?, comments = ? WHERE evaluation_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, evaluation.getEvaluatorAssignmentId());
      pstmt.setInt(2, evaluation.getSubmissionId());

      if (evaluation.getProblemClarityScore() != null) {
        pstmt.setInt(3, evaluation.getProblemClarityScore());
      } else {
        pstmt.setNull(3, Types.INTEGER);
      }

      if (evaluation.getMethodologyScore() != null) {
        pstmt.setInt(4, evaluation.getMethodologyScore());
      } else {
        pstmt.setNull(4, Types.INTEGER);
      }

      if (evaluation.getResultsScore() != null) {
        pstmt.setInt(5, evaluation.getResultsScore());
      } else {
        pstmt.setNull(5, Types.INTEGER);
      }

      if (evaluation.getPresentationScore() != null) {
        pstmt.setInt(6, evaluation.getPresentationScore());
      } else {
        pstmt.setNull(6, Types.INTEGER);
      }

      if (evaluation.getTotalScore() != null) {
        pstmt.setInt(7, evaluation.getTotalScore());
      } else {
        pstmt.setNull(7, Types.INTEGER);
      }

      pstmt.setString(8, evaluation.getComments());
      pstmt.setInt(9, evaluation.getEvaluationId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteEvaluation(int evaluationId) {
    String sql = "DELETE FROM evaluations WHERE evaluation_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, evaluationId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Helper method
  private Evaluation mapResultSetToEvaluation(ResultSet rs) throws SQLException {
    Integer problemClarityScore = rs.getObject("problem_clarity_score") != null ? rs.getInt("problem_clarity_score")
        : null;
    Integer methodologyScore = rs.getObject("methodology_score") != null ? rs.getInt("methodology_score") : null;
    Integer resultsScore = rs.getObject("results_score") != null ? rs.getInt("results_score") : null;
    Integer presentationScore = rs.getObject("presentation_score") != null ? rs.getInt("presentation_score") : null;
    Integer totalScore = rs.getObject("total_score") != null ? rs.getInt("total_score") : null;

    return new Evaluation(
        rs.getInt("evaluation_id"),
        rs.getInt("evaluator_assignment_id"),
        rs.getInt("submission_id"),
        problemClarityScore,
        methodologyScore,
        resultsScore,
        presentationScore,
        totalScore,
        rs.getString("comments"));
  }
}