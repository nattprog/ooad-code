package seminar.database.dao;

import seminar.database.DatabaseManager;
import seminar.models.TimeSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class TimeSlotDAO {

  // CREATE
  public boolean createTimeSlot(TimeSlot timeSlot) {
    String sql = "INSERT INTO time_slots (session_id, submission_id, start_time, end_time) " +
        "VALUES (?, ?, ?, ?)";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setInt(1, timeSlot.getSessionId());

      if (timeSlot.getSubmissionId() != null) {
        pstmt.setInt(2, timeSlot.getSubmissionId());
      } else {
        pstmt.setNull(2, Types.INTEGER);
      }

      pstmt.setTimestamp(3, new Timestamp(timeSlot.getStartTime().getTime()));
      pstmt.setTimestamp(4, new Timestamp(timeSlot.getEndTime().getTime()));

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            timeSlot.setTimeSlotId(generatedKeys.getInt(1));
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

  // CREATE - Generate time slots for a session
  public boolean generateTimeSlotsForSession(int sessionId, java.util.Date startTime,
      int count, int durationMinutes) {
    Connection conn = null;
    try {
      conn = DatabaseManager.getInstance().getConnection();
      conn.setAutoCommit(false);

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(startTime);

      for (int i = 0; i < count; i++) {
        java.util.Date slotStart = calendar.getTime();
        calendar.add(Calendar.MINUTE, durationMinutes);
        java.util.Date slotEnd = calendar.getTime();

        TimeSlot timeSlot = new TimeSlot(sessionId, slotStart, slotEnd);

        String sql = "INSERT INTO time_slots (session_id, start_time, end_time) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, sessionId);
        pstmt.setTimestamp(2, new Timestamp(slotStart.getTime()));
        pstmt.setTimestamp(3, new Timestamp(slotEnd.getTime()));
        pstmt.executeUpdate();
        pstmt.close();
      }

      conn.commit();
      return true;

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

  // READ - Get by ID
  public TimeSlot getTimeSlotById(int timeSlotId) {
    String sql = "SELECT * FROM time_slots WHERE time_slot_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, timeSlotId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToTimeSlot(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all time slots
  public List<TimeSlot> getAllTimeSlots() {
    String sql = "SELECT * FROM time_slots ORDER BY start_time";
    List<TimeSlot> timeSlots = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        timeSlots.add(mapResultSetToTimeSlot(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return timeSlots;
  }

  // READ - Get time slots by session
  public List<TimeSlot> getTimeSlotsBySession(int sessionId) {
    String sql = "SELECT * FROM time_slots WHERE session_id = ? ORDER BY start_time";
    List<TimeSlot> timeSlots = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, sessionId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        timeSlots.add(mapResultSetToTimeSlot(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return timeSlots;
  }

  // READ - Get available (unassigned) time slots by session
  public List<TimeSlot> getAvailableTimeSlotsBySession(int sessionId) {
    String sql = "SELECT * FROM time_slots WHERE session_id = ? AND submission_id IS NULL " +
        "ORDER BY start_time";
    List<TimeSlot> timeSlots = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, sessionId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        timeSlots.add(mapResultSetToTimeSlot(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return timeSlots;
  }

  // READ - Get time slot by submission
  public TimeSlot getTimeSlotBySubmission(int submissionId) {
    String sql = "SELECT * FROM time_slots WHERE submission_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, submissionId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToTimeSlot(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // UPDATE
  public boolean updateTimeSlot(TimeSlot timeSlot) {
    String sql = "UPDATE time_slots SET session_id = ?, submission_id = ?, " +
        "start_time = ?, end_time = ? WHERE time_slot_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, timeSlot.getSessionId());

      if (timeSlot.getSubmissionId() != null) {
        pstmt.setInt(2, timeSlot.getSubmissionId());
      } else {
        pstmt.setNull(2, Types.INTEGER);
      }

      pstmt.setTimestamp(3, new Timestamp(timeSlot.getStartTime().getTime()));
      pstmt.setTimestamp(4, new Timestamp(timeSlot.getEndTime().getTime()));
      pstmt.setInt(5, timeSlot.getTimeSlotId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // UPDATE - Assign submission to time slot
  public boolean assignSubmissionToTimeSlot(int timeSlotId, int submissionId) {
    String sql = "UPDATE time_slots SET submission_id = ? WHERE time_slot_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, submissionId);
      pstmt.setInt(2, timeSlotId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // UPDATE - Unassign submission from time slot
  public boolean unassignSubmissionFromTimeSlot(int timeSlotId) {
    String sql = "UPDATE time_slots SET submission_id = NULL WHERE time_slot_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, timeSlotId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteTimeSlot(int timeSlotId) {
    String sql = "DELETE FROM time_slots WHERE time_slot_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, timeSlotId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE - Delete all time slots for a session
  public boolean deleteTimeSlotsBySession(int sessionId) {
    String sql = "DELETE FROM time_slots WHERE session_id = ?";

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
  private TimeSlot mapResultSetToTimeSlot(ResultSet rs) throws SQLException {
    Integer submissionId = rs.getObject("submission_id") != null ? rs.getInt("submission_id") : null;

    return new TimeSlot(
        rs.getInt("time_slot_id"),
        rs.getInt("session_id"),
        submissionId,
        new java.util.Date(rs.getTimestamp("start_time").getTime()),
        new java.util.Date(rs.getTimestamp("end_time").getTime()));
  }
}