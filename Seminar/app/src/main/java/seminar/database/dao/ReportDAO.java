package seminar.database.dao;

import seminar.database.DatabaseManager;
import seminar.models.Report;
import seminar.models.enums.ReportType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
  // CREATE
  public boolean createReport(Report report) {
    String sql = "INSERT INTO reports (seminar_id, report_type, generated_at, file_path, report_content) " +
        "VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      pstmt.setInt(1, report.getSeminarId());
      pstmt.setString(2, report.getReportType().name());
      pstmt.setTimestamp(3, new Timestamp(report.getGeneratedAt().getTime()));
      pstmt.setString(4, report.getFilePath());
      pstmt.setString(5, report.getReportContent());

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            report.setReportId(generatedKeys.getInt(1));
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
  public Report getReportById(int reportId) {
    String sql = "SELECT * FROM reports WHERE report_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, reportId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToReport(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all reports
  public List<Report> getAllReports() {
    String sql = "SELECT * FROM reports ORDER BY generated_at DESC";
    List<Report> reports = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        reports.add(mapResultSetToReport(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return reports;
  }

  // READ - Get reports by seminar
  public List<Report> getReportsBySeminar(int seminarId) {
    String sql = "SELECT * FROM reports WHERE seminar_id = ? ORDER BY generated_at DESC";
    List<Report> reports = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, seminarId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        reports.add(mapResultSetToReport(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return reports;
  }

  // READ - Get reports by type
  public List<Report> getReportsByType(ReportType type) {
    String sql = "SELECT * FROM reports WHERE report_type = ? ORDER BY generated_at DESC";
    List<Report> reports = new ArrayList<>();

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, type.name());
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        reports.add(mapResultSetToReport(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return reports;
  }

  // UPDATE
  public boolean updateReport(Report report) {
    String sql = "UPDATE reports SET seminar_id = ?, report_type = ?, file_path = ?, " +
        "report_content = ? WHERE report_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, report.getSeminarId());
      pstmt.setString(2, report.getReportType().name());
      pstmt.setString(3, report.getFilePath());
      pstmt.setString(4, report.getReportContent());
      pstmt.setInt(5, report.getReportId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // DELETE
  public boolean deleteReport(int reportId) {
    String sql = "DELETE FROM reports WHERE report_id = ?";

    try (Connection conn = DatabaseManager.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, reportId);
      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Helper method
  private Report mapResultSetToReport(ResultSet rs) throws SQLException {
    return new Report(
        rs.getInt("report_id"),
        rs.getInt("seminar_id"),
        ReportType.valueOf(rs.getString("report_type")),
        new java.util.Date(rs.getTimestamp("generated_at").getTime()),
        rs.getString("file_path"),
        rs.getString("report_content"));
  }
}