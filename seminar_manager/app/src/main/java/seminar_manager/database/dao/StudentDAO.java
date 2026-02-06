package seminar_manager.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import seminar_manager.database.DatabaseManager;
import seminar_manager.models.Student;
import seminar_manager.models.enums.UserRole;

public class StudentDAO {
  private UserDAO userDAO = new UserDAO();

  // CREATE - Creates both User and Student records
  public boolean createStudent(Student student) {
    Connection conn = null;
    try {
      conn = DatabaseManager.getInstance().getConnection();
      conn.setAutoCommit(false); // Start transaction

      // First, create the user record
      boolean userCreated = userDAO.createUser(student);
      if (!userCreated) {
        conn.rollback();
        return false;
      }

      // Then, create the student record
      String sql = "INSERT INTO students (student_id, user_id) VALUES (?, ?)";
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, student.getStudentId());
      pstmt.setInt(2, student.getUserId());

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

  // READ - Get by student ID
  public Student getStudentByStudentId(String studentId) {
    String sql = "SELECT u.*, s.student_id FROM users u " +
        "JOIN students s ON u.user_id = s.user_id " +
        "WHERE s.student_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, studentId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToStudent(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get by user ID
  public Student getStudentByUserId(int userId) {
    String sql = "SELECT u.*, s.student_id FROM users u " +
        "JOIN students s ON u.user_id = s.user_id " +
        "WHERE u.user_id = ?";

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return mapResultSetToStudent(rs);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // READ - Get all students
  public List<Student> getAllStudents() {
    String sql = "SELECT u.*, s.student_id FROM users u " +
        "JOIN students s ON u.user_id = s.user_id " +
        "ORDER BY u.full_name";
    List<Student> students = new ArrayList<>();

    try {
      Connection conn = DatabaseManager.getInstance().getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        students.add(mapResultSetToStudent(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return students;
  }

  // UPDATE
  public boolean updateStudent(Student student) {
    return userDAO.updateUser(student);
  }

  // DELETE
  public boolean deleteStudent(String studentId) {
    // Get user_id first
    Student student = getStudentByStudentId(studentId);
    if (student != null) {
      // Deleting from users will cascade to students table
      return userDAO.deleteUser(student.getUserId());
    }
    return false;
  }

  // Helper method
  private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
    return new Student(
        rs.getInt("user_id"),
        rs.getString("username"),
        rs.getString("password"),
        rs.getString("full_name"),
        rs.getString("email"),
        new java.util.Date(rs.getTimestamp("created_at").getTime()),
        rs.getString("student_id"));
  }
}