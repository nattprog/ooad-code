package seminar_manager.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import seminar_manager.database.DatabaseManager;
import seminar_manager.models.Session;
import seminar_manager.models.enums.PresentationType;

public class SessionDAO {

  // CREATE
  public boolean createSession(Session session) {
    String sql = "INSERT INTO sessions (seminar_id, presentation_type, time_slots_count, " +
        "time_slots_duration, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setInt(1, session.getSeminarId());
      pstmt.setString(2, session.getPresentationType().name());
      pstmt.setInt(3, session.getTimeSlotsCount());
      pstmt.setInt(4, session.getTimeSlotsDuration());
      pstmt.setTimestamp(5, new Timestamp(session.getStartTime().getTime()));
      pstmt.setTimestamp(6, new Timestamp(session.getEndTime().getTime()));

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            session.setSessionId(generatedKeys.getInt(1));
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
  public Session getSessionById(int sessionId) {
    String sql = "SELECT * FROM sessions WHERE session_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, sessionId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToSession(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all sessions
  public List<Session> getAllSessions() {
    String sql = "SELECT * FROM sessions ORDER BY start_time";
    List<Session> sessions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        sessions.add(mapResultSetToSession(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return sessions;
  }

  // READ - Get sessions by seminar
  public List<Session> getSessionsBySeminar(int seminarId) {
    String sql = "SELECT * FROM sessions WHERE seminar_id = ? ORDER BY start_time";
    List<Session> sessions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, seminarId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        sessions.add(mapResultSetToSession(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return sessions;
  }

  // READ - Get sessions by presentation type
  public List<Session> getSessionsByPresentationType(PresentationType type) {
    String sql = "SELECT * FROM sessions WHERE presentation_type = ? ORDER BY start_time";
    List<Session> sessions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, type.name());
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        sessions.add(mapResultSetToSession(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return sessions;
  }

  // UPDATE
  public boolean updateSession(Session session) {
    String sql = "UPDATE sessions SET seminar_id = ?, presentation_type = ?, " +
        "time_slots_count = ?, time_slots_duration = ?, start_time = ?, end_time = ? " +
        "WHERE session_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, session.getSeminarId());
      pstmt.setString(2, session.getPresentationType().name());
      pstmt.setInt(3, session.getTimeSlotsCount());
      pstmt.setInt(4, session.getTimeSlotsDuration());
      pstmt.setTimestamp(5, new Timestamp(session.getStartTime().getTime()));
      pstmt.setTimestamp(6, new Timestamp(session.getEndTime().getTime()));
      pstmt.setInt(7, session.getSessionId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteSession(int sessionId) {
    String sql = "DELETE FROM sessions WHERE session_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, sessionId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Helper method
  private Session mapResultSetToSession(ResultSet rs) throws SQLException {
    return new Session(
        rs.getInt("session_id"),
        rs.getInt("seminar_id"),
        PresentationType.valueOf(rs.getString("presentation_type")),
        rs.getInt("time_slots_count"),
        rs.getInt("time_slots_duration"),
        new java.util.Date(rs.getTimestamp("start_time").getTime()),
        new java.util.Date(rs.getTimestamp("end_time").getTime()));
  }
}