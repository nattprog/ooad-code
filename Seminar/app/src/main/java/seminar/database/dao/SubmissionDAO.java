package seminar.database.dao;

import seminar.database.DatabaseManager;
import seminar.models.Submission;
import seminar.models.enums.PresentationType;
import seminar.models.enums.SubmissionStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {

  // CREATE
  public boolean createSubmission(Submission submission) {
    String sql = "INSERT INTO submissions (seminar_id, session_id, student_user_id, " +
        "research_title, research_abstract, supervisor_name, presentation_type, " +
        "status, submitted_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setInt(1, submission.getSeminarId());

      if (submission.getSessionId() != null) {
        pstmt.setInt(2, submission.getSessionId());
      } else {
        pstmt.setNull(2, Types.INTEGER);
      }

      pstmt.setInt(3, submission.getStudentId());
      pstmt.setString(4, submission.getResearchTitle());
      pstmt.setString(5, submission.getResearchAbstract());
      pstmt.setString(6, submission.getSupervisorName());
      pstmt.setString(7, submission.getPresentationType().name());
      pstmt.setString(8, submission.getStatus().name());
      pstmt.setTimestamp(9, new Timestamp(submission.getSubmittedAt().getTime()));

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            submission.setSubmissionId(generatedKeys.getInt(1));
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
  public Submission getSubmissionById(int submissionId) {
    String sql = "SELECT * FROM submissions WHERE submission_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, submissionId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToSubmission(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all submissions
  public List<Submission> getAllSubmissions() {
    String sql = "SELECT * FROM submissions ORDER BY submitted_at DESC";
    List<Submission> submissions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        submissions.add(mapResultSetToSubmission(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return submissions;
  }

  // READ - Get submissions by student (using student_user_id)
  public List<Submission> getSubmissionsByStudentUserId(int studentUserId) {
    String sql = "SELECT * FROM submissions WHERE student_user_id = ? ORDER BY submitted_at DESC";
    List<Submission> submissions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, studentUserId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        submissions.add(mapResultSetToSubmission(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return submissions;
  }

  // READ - Get submissions by seminar
  public List<Submission> getSubmissionsBySeminar(int seminarId) {
    String sql = "SELECT * FROM submissions WHERE seminar_id = ? ORDER BY submitted_at DESC";
    List<Submission> submissions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, seminarId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        submissions.add(mapResultSetToSubmission(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return submissions;
  }

  // READ - Get submissions by session
  public List<Submission> getSubmissionsBySession(int sessionId) {
    String sql = "SELECT * FROM submissions WHERE session_id = ? ORDER BY submitted_at";
    List<Submission> submissions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, sessionId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        submissions.add(mapResultSetToSubmission(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return submissions;
  }

  // READ - Get unassigned submissions
  public List<Submission> getUnassignedSubmissions(int seminarId) {
    String sql = "SELECT * FROM submissions WHERE seminar_id = ? AND session_id IS NULL " +
        "AND status = 'APPROVED' ORDER BY submitted_at";
    List<Submission> submissions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, seminarId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        submissions.add(mapResultSetToSubmission(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return submissions;
  }

  // READ - Get submissions by status
  public List<Submission> getSubmissionsByStatus(SubmissionStatus status) {
    String sql = "SELECT * FROM submissions WHERE status = ? ORDER BY submitted_at DESC";
    List<Submission> submissions = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, status.name());
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        submissions.add(mapResultSetToSubmission(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return submissions;
  }

  // UPDATE
  public boolean updateSubmission(Submission submission) {
    String sql = "UPDATE submissions SET seminar_id = ?, session_id = ?, student_user_id = ?, " +
        "research_title = ?, research_abstract = ?, supervisor_name = ?, " +
        "presentation_type = ?, status = ? WHERE submission_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, submission.getSeminarId());

      if (submission.getSessionId() != null) {
        pstmt.setInt(2, submission.getSessionId());
      } else {
        pstmt.setNull(2, Types.INTEGER);
      }

      pstmt.setInt(3, submission.getStudentId());
      pstmt.setString(4, submission.getResearchTitle());
      pstmt.setString(5, submission.getResearchAbstract());
      pstmt.setString(6, submission.getSupervisorName());
      pstmt.setString(7, submission.getPresentationType().name());
      pstmt.setString(8, submission.getStatus().name());
      pstmt.setInt(9, submission.getSubmissionId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // UPDATE - Assign to session
  public boolean assignToSession(int submissionId, int sessionId) {
    String sql = "UPDATE submissions SET session_id = ? WHERE submission_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, sessionId);
      pstmt.setInt(2, submissionId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // UPDATE - Update status
  public boolean updateStatus(int submissionId, SubmissionStatus status) {
    String sql = "UPDATE submissions SET status = ? WHERE submission_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, status.name());
      pstmt.setInt(2, submissionId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteSubmission(int submissionId) {
    String sql = "DELETE FROM submissions WHERE submission_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, submissionId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Helper method
  private Submission mapResultSetToSubmission(ResultSet rs) throws SQLException {
    Integer sessionId = rs.getObject("session_id") != null ? rs.getInt("session_id") : null;

    return new Submission(
        rs.getInt("submission_id"),
        rs.getInt("seminar_id"),
        sessionId,
        rs.getInt("student_user_id"),
        rs.getString("research_title"),
        rs.getString("research_abstract"),
        rs.getString("supervisor_name"),
        PresentationType.valueOf(rs.getString("presentation_type")),
        SubmissionStatus.valueOf(rs.getString("status")),
        new java.util.Date(rs.getTimestamp("submitted_at").getTime()));
  }
}