package seminar_manager.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import seminar_manager.database.DatabaseManager;
import seminar_manager.models.EvaluatorAssignment;

public class EvaluatorAssignmentDAO {

  // CREATE
  public boolean createEvaluatorAssignment(EvaluatorAssignment assignment) {
    String sql = "INSERT INTO evaluator_assignments (session_id, evaluator_user_id) VALUES (?, ?)";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      pstmt.setInt(1, assignment.getSessionId());
      pstmt.setInt(2, assignment.getEvaluatorId());

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            assignment.setEvaluatorAssignmentId(generatedKeys.getInt(1));
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
  public EvaluatorAssignment getEvaluatorAssignmentById(int assignmentId) {
    String sql = "SELECT * FROM evaluator_assignments WHERE evaluator_assignment_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, assignmentId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToEvaluatorAssignment(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all assignments
  public List<EvaluatorAssignment> getAllEvaluatorAssignments() {
    String sql = "SELECT * FROM evaluator_assignments";
    List<EvaluatorAssignment> assignments = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        assignments.add(mapResultSetToEvaluatorAssignment(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return assignments;
  }

  // READ - Get assignments by session
  public List<EvaluatorAssignment> getAssignmentsBySession(int sessionId) {
    String sql = "SELECT * FROM evaluator_assignments WHERE session_id = ?";
    List<EvaluatorAssignment> assignments = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, sessionId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        assignments.add(mapResultSetToEvaluatorAssignment(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return assignments;
  }

  // READ - Get assignments by evaluator (using evaluator_user_id)
  public List<EvaluatorAssignment> getAssignmentsByEvaluatorUserId(int evaluatorUserId) {
    String sql = "SELECT * FROM evaluator_assignments WHERE evaluator_user_id = ?";
    List<EvaluatorAssignment> assignments = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, evaluatorUserId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        assignments.add(mapResultSetToEvaluatorAssignment(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return assignments;
  }

  // READ - Check if evaluator is assigned to session
  public boolean isEvaluatorAssignedToSession(int sessionId, int evaluatorUserId) {
    String sql = "SELECT COUNT(*) FROM evaluator_assignments " +
        "WHERE session_id = ? AND evaluator_user_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, sessionId);
      pstmt.setInt(2, evaluatorUserId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getInt(1) > 0;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // UPDATE
  public boolean updateEvaluatorAssignment(EvaluatorAssignment assignment) {
    String sql = "UPDATE evaluator_assignments SET session_id = ?, evaluator_user_id = ? " +
        "WHERE evaluator_assignment_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, assignment.getSessionId());
      pstmt.setInt(2, assignment.getEvaluatorId());
      pstmt.setInt(3, assignment.getEvaluatorAssignmentId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteEvaluatorAssignment(int assignmentId) {
    String sql = "DELETE FROM evaluator_assignments WHERE evaluator_assignment_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, assignmentId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE - Remove evaluator from session
  public boolean removeEvaluatorFromSession(int sessionId, int evaluatorUserId) {
    String sql = "DELETE FROM evaluator_assignments WHERE session_id = ? AND evaluator_user_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, sessionId);
      pstmt.setInt(2, evaluatorUserId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Helper method
  private EvaluatorAssignment mapResultSetToEvaluatorAssignment(ResultSet rs) throws SQLException {
    return new EvaluatorAssignment(
        rs.getInt("evaluator_assignment_id"),
        rs.getInt("session_id"),
        rs.getInt("evaluator_user_id"));
  }
}