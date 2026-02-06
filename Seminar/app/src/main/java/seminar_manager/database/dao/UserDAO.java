package seminar_manager.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import seminar_manager.database.DatabaseManager;
import seminar_manager.models.User;
import seminar_manager.models.enums.UserRole;

public class UserDAO {

  // CREATE
  public boolean createUser(User user) {
    String sql = "INSERT INTO users (username, password, full_name, email, role, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, user.getUsername());
      pstmt.setString(2, user.getPassword());
      pstmt.setString(3, user.getFullName());
      pstmt.setString(4, user.getEmail());
      pstmt.setString(5, user.getRole().name());
      pstmt.setTimestamp(6, new Timestamp(user.getCreatedAt().getTime()));

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            user.setUserId(generatedKeys.getInt(1));
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
  public User getUserById(int userId) {
    String sql = "SELECT * FROM users WHERE user_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToUser(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get by username
  public User getUserByUsername(String username) {
    String sql = "SELECT * FROM users WHERE username = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToUser(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all users
  public List<User> getAllUsers() {
    String sql = "SELECT * FROM users ORDER BY created_at DESC";
    List<User> users = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        users.add(mapResultSetToUser(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

  // READ - Get users by role
  public List<User> getUsersByRole(UserRole role) {
    String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";
    List<User> users = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, role.name());
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        users.add(mapResultSetToUser(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

  // UPDATE
  public boolean updateUser(User user) {
    String sql = "UPDATE users SET username = ?, password = ?, full_name = ?, " +
        "email = ?, role = ? WHERE user_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, user.getUsername());
      pstmt.setString(2, user.getPassword());
      pstmt.setString(3, user.getFullName());
      pstmt.setString(4, user.getEmail());
      pstmt.setString(5, user.getRole().name());
      pstmt.setInt(6, user.getUserId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteUser(int userId) {
    String sql = "DELETE FROM users WHERE user_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, userId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Validate login
  public User validateLogin(String username, String password) {
    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToUser(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // Helper method
  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    return new User(
        rs.getInt("user_id"),
        rs.getString("username"),
        rs.getString("password"),
        rs.getString("full_name"),
        rs.getString("email"),
        UserRole.valueOf(rs.getString("role")),
        new java.util.Date(rs.getTimestamp("created_at").getTime()));
  }
}