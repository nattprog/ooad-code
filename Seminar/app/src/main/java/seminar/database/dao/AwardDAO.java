package seminar.database.dao;

import seminar.database.DatabaseManager;
import seminar.models.Award;
import seminar.models.enums.AwardType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AwardDAO {

  // CREATE
  public boolean createAward(Award award) {
    String sql = "INSERT INTO awards (seminar_id, submission_id, award_type) VALUES (?, ?, ?)";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      pstmt.setInt(1, award.getSeminarId());
      pstmt.setInt(2, award.getSubmissionId());
      pstmt.setString(3, award.getAwardType().name());

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            award.setAwardId(generatedKeys.getInt(1));
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
  public Award getAwardById(int awardId) {
    String sql = "SELECT * FROM awards WHERE award_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, awardId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToAward(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all awards
  public List<Award> getAllAwards() {
    String sql = "SELECT * FROM awards";
    List<Award> awards = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        awards.add(mapResultSetToAward(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return awards;
  }

  // READ - Get awards by seminar
  public List<Award> getAwardsBySeminar(int seminarId) {
    String sql = "SELECT * FROM awards WHERE seminar_id = ?";
    List<Award> awards = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, seminarId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        awards.add(mapResultSetToAward(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return awards;
  }

  // READ - Get awards by submission
  public List<Award> getAwardsBySubmission(int submissionId) {
    String sql = "SELECT * FROM awards WHERE submission_id = ?";
    List<Award> awards = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, submissionId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        awards.add(mapResultSetToAward(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return awards;
  }

  // READ - Get awards by type
  public List<Award> getAwardsByType(AwardType type) {
    String sql = "SELECT * FROM awards WHERE award_type = ?";
    List<Award> awards = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, type.name());
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        awards.add(mapResultSetToAward(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return awards;
  }

  // UPDATE
  public boolean updateAward(Award award) {
    String sql = "UPDATE awards SET seminar_id = ?, submission_id = ?, award_type = ? " +
        "WHERE award_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, award.getSeminarId());
      pstmt.setInt(2, award.getSubmissionId());
      pstmt.setString(3, award.getAwardType().name());
      pstmt.setInt(4, award.getAwardId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteAward(int awardId) {
    String sql = "DELETE FROM awards WHERE award_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, awardId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Helper method
  private Award mapResultSetToAward(ResultSet rs) throws SQLException {
    return new Award(
        rs.getInt("award_id"),
        rs.getInt("seminar_id"),
        rs.getInt("submission_id"),
        AwardType.valueOf(rs.getString("award_type")));
  }
}