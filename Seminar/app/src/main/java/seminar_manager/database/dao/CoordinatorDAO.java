package seminar_manager.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import seminar_manager.database.DatabaseManager;
import seminar_manager.models.Coordinator;

public class CoordinatorDAO {
  private UserDAO userDAO = new UserDAO();

  // CREATE
  public boolean createCoordinator(Coordinator coordinator) {
    Connection conn = null;
    try {
      conn = DatabaseManager.getInstance().getConnection();
      conn.setAutoCommit(false);

      // Create user record
      boolean userCreated = userDAO.createUser(coordinator);
      if (!userCreated) {
        conn.rollback();
        return false;
      }

      // Create coordinator record
      String sql = "INSERT INTO coordinators (coordinator_id, user_id) VALUES (?, ?)";
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, coordinator.getCoordinatorId());
      pstmt.setInt(2, coordinator.getUserId());

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

  // READ - Get by coordinator ID
  public Coordinator getCoordinatorByCoordinatorId(String coordinatorId) {
    String sql = "SELECT u.*, c.coordinator_id FROM users u " +
        "JOIN coordinators c ON u.user_id = c.user_id " +
        "WHERE c.coordinator_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, coordinatorId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToCoordinator(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get by user ID
  public Coordinator getCoordinatorByUserId(int userId) {
    String sql = "SELECT u.*, c.coordinator_id FROM users u " +
        "JOIN coordinators c ON u.user_id = c.user_id " +
        "WHERE u.user_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToCoordinator(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all coordinators
  public List<Coordinator> getAllCoordinators() {
    String sql = "SELECT u.*, c.coordinator_id FROM users u " +
        "JOIN coordinators c ON u.user_id = c.user_id " +
        "ORDER BY u.full_name";
    List<Coordinator> coordinators = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        coordinators.add(mapResultSetToCoordinator(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return coordinators;
  }

  // UPDATE
  public boolean updateCoordinator(Coordinator coordinator) {
    return userDAO.updateUser(coordinator);
  }

  // DELETE
  public boolean deleteCoordinator(String coordinatorId) {
    Coordinator coordinator = getCoordinatorByCoordinatorId(coordinatorId);
    if (coordinator != null) {
      return userDAO.deleteUser(coordinator.getUserId());
    }
    return false;
  }

  // Helper method
  private Coordinator mapResultSetToCoordinator(ResultSet rs) throws SQLException {
    return new Coordinator(
        rs.getInt("user_id"),
        rs.getString("username"),
        rs.getString("password"),
        rs.getString("full_name"),
        rs.getString("email"),
        new java.util.Date(rs.getTimestamp("created_at").getTime()),
        rs.getString("coordinator_id"));
  }
}