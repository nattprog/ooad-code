package seminar.database.dao;

import seminar.database.DatabaseManager;
import seminar.models.Evaluator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluatorDAO {
  private UserDAO userDAO = new UserDAO();

  // CREATE
  public boolean createEvaluator(Evaluator evaluator) {
    Connection conn = null;
    try {
      conn = DatabaseManager.getInstance().getConnection();
      conn.setAutoCommit(false);

      // Create user record
      boolean userCreated = userDAO.createUser(evaluator);
      if (!userCreated) {
        conn.rollback();
        return false;
      }

      // Create evaluator record
      String sql = "INSERT INTO evaluators (evaluator_id, user_id) VALUES (?, ?)";
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, evaluator.getEvaluatorId());
      pstmt.setInt(2, evaluator.getUserId());

      int affectedRows = pstmt.executeUpdate();
      pstmt.close();

      if (affectedRows > 0) {
        conn.commit();
        return true;
      } else {
        conn.rollback();
        return false;
      }

    } catch (SQLException e) {
      try {
        if (conn != null)
          conn.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (conn != null)
          conn.setAutoCommit(true);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  // READ - Get by evaluator ID
  public Evaluator getEvaluatorByEvaluatorId(String evaluatorId) {
    String sql = "SELECT u.*, e.evaluator_id FROM users u " +
        "JOIN evaluators e ON u.user_id = e.user_id " +
        "WHERE e.evaluator_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, evaluatorId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToEvaluator(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get by user ID
  public Evaluator getEvaluatorByUserId(int userId) {
    String sql = "SELECT u.*, e.evaluator_id FROM users u " +
        "JOIN evaluators e ON u.user_id = e.user_id " +
        "WHERE u.user_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToEvaluator(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all evaluators
  public List<Evaluator> getAllEvaluators() {
    String sql = "SELECT u.*, e.evaluator_id FROM users u " +
        "JOIN evaluators e ON u.user_id = e.user_id " +
        "ORDER BY u.full_name";
    List<Evaluator> evaluators = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        evaluators.add(mapResultSetToEvaluator(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return evaluators;
  }

  // UPDATE
  public boolean updateEvaluator(Evaluator evaluator) {
    return userDAO.updateUser(evaluator);
  }

  // DELETE
  public boolean deleteEvaluator(String evaluatorId) {
    Evaluator evaluator = getEvaluatorByEvaluatorId(evaluatorId);
    if (evaluator != null) {
      return userDAO.deleteUser(evaluator.getUserId());
    }
    return false;
  }

  // Helper method
  private Evaluator mapResultSetToEvaluator(ResultSet rs) throws SQLException {
    return new Evaluator(
        rs.getInt("user_id"),
        rs.getString("username"),
        rs.getString("password"),
        rs.getString("full_name"),
        rs.getString("email"),
        new java.util.Date(rs.getTimestamp("created_at").getTime()),
        rs.getString("evaluator_id"));
  }
}