package seminar_manager.models;

import java.util.Date;

import seminar_manager.models.enums.UserRole;

public class Evaluator extends User {
  private String evaluatorId;

  // Constructor for creating new evaluator (no User ID yet)
  public Evaluator(String username, String password, String fullName, String email, String evaluatorId) {
    super(username, password, fullName, email, UserRole.EVALUATOR);
    this.evaluatorId = evaluatorId;
  }

  // Constructor for loading from database (with User ID)
  public Evaluator(Integer userId, String username, String password, String fullName,
      String email, Date createdAt, String evaluatorId) {
    super(userId, username, password, fullName, email, UserRole.EVALUATOR, createdAt);
    this.evaluatorId = evaluatorId;
  }

  // Getters and Setters
  public String getEvaluatorId() {
    return evaluatorId;
  }

  public void setEvaluatorId(String evaluatorId) {
    this.evaluatorId = evaluatorId;
  }

  @Override
  public String toString() {
    return "Evaluator{" +
        "evaluatorId='" + evaluatorId + '\'' +
        ", userId=" + getUserId() +
        ", fullName='" + getFullName() + '\'' +
        '}';
  }
}