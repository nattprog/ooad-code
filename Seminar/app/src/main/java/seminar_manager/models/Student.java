package seminar_manager.models;

import java.util.Date;

import seminar_manager.models.enums.UserRole;

public class Student extends User {
  private String studentId;

  // Constructor for creating new student (no User ID yet)
  public Student(String username, String password, String fullName, String email, String studentId) {
    super(username, password, fullName, email, UserRole.STUDENT);
    this.studentId = studentId;
  }

  // Constructor for loading from database (with User ID)
  public Student(Integer userId, String username, String password, String fullName,
      String email, Date createdAt, String studentId) {
    super(userId, username, password, fullName, email, UserRole.STUDENT, createdAt);
    this.studentId = studentId;
  }

  // Getters and Setters
  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  @Override
  public String toString() {
    return "Student{" +
        "studentId='" + studentId + '\'' +
        ", userId=" + getUserId() +
        ", fullName='" + getFullName() + '\'' +
        '}';
  }
}