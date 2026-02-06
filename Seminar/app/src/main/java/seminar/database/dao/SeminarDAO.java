package seminar.database.dao;

import seminar.database.DatabaseManager;
import seminar.models.Seminar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeminarDAO {

  // CREATE
  public boolean createSeminar(Seminar seminar) {
    String sql = "INSERT INTO seminars (title , description, location, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, seminar.getTitle());
      pstmt.setString(2, seminar.getDescription());
      pstmt.setString(3, seminar.getLocation());
      pstmt.setTimestamp(4, new Timestamp(seminar.getStartTime().getTime()));
      pstmt.setTimestamp(5, new Timestamp(seminar.getEndTime().getTime()));

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            seminar.setSeminarId(generatedKeys.getInt(1));
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
  public Seminar getSeminarById(int seminarId) {
    String sql = "SELECT * FROM seminars WHERE seminar_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, seminarId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToSeminar(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all seminars
  public List<Seminar> getAllSeminars() {
    String sql = "SELECT * FROM seminars ORDER BY start_time DESC";
    List<Seminar> seminars = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);

      while (rs.next()) {
        seminars.add(mapResultSetToSeminar(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return seminars;
  }

  // READ - Get upcoming seminars
  public List<Seminar> getUpcomingSeminars() {
    String sql = "SELECT * FROM seminars WHERE start_time > CURRENT_TIMESTAMP ORDER BY start_time";
    List<Seminar> seminars = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        seminars.add(mapResultSetToSeminar(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return seminars;
  }

  // UPDATE
  public boolean updateSeminar(Seminar seminar) {
    String sql = "UPDATE seminars SET start_time = ?, end_time = ? WHERE seminar_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);

      pstmt.setTimestamp(1, new Timestamp(seminar.getStartTime().getTime()));
      pstmt.setTimestamp(2, new Timestamp(seminar.getEndTime().getTime()));
      pstmt.setInt(3, seminar.getSeminarId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteSeminar(int seminarId) {
    String sql = "DELETE FROM seminars WHERE seminar_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, seminarId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Helper method
  private Seminar mapResultSetToSeminar(ResultSet rs) throws SQLException {
    return new Seminar(
        rs.getInt("seminar_id"),
        rs.getString("title"),
        rs.getString("description"),
        rs.getString("location"),
        new java.util.Date(rs.getTimestamp("start_time").getTime()),
        new java.util.Date(rs.getTimestamp("end_time").getTime()));
  }
}