package seminar_manager.models;

import java.util.Date;

import seminar_manager.models.enums.UserRole;

public class User {
  private Integer userId;
  private String username;
  private String password;
  private String fullName;
  private String email;
  private UserRole role;
  private Date createdAt;

  // Constructor for creating new user (no ID yet)
  public User(String username, String password, String fullName, String email, UserRole role) {
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.email = email;
    this.role = role;
    this.createdAt = new Date();
  }

  // Constructor for loading from database (with ID)
  public User(Integer userId, String username, String password, String fullName,
      String email, UserRole role, Date createdAt) {
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.email = email;
    this.role = role;
    this.createdAt = createdAt;
  }

  // Getters and Setters
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "User{" +
        "userId=" + userId +
        ", username='" + username + '\'' +
        ", fullName='" + fullName + '\'' +
        ", role=" + role +
        '}';
  }
}