package seminar.models;

public class EvaluatorAssignment {
  private Integer evaluatorAssignmentId;
  private Integer sessionId;
  private Integer evaluatorId;

  // Constructor for creating new assignment (no ID yet)
  public EvaluatorAssignment(Integer sessionId, Integer evaluatorId) {
    this.sessionId = sessionId;
    this.evaluatorId = evaluatorId;
  }

  // Constructor for loading from database (with ID)
  public EvaluatorAssignment(Integer evaluatorAssignmentId, Integer sessionId, Integer evaluatorId) {
    this.evaluatorAssignmentId = evaluatorAssignmentId;
    this.sessionId = sessionId;
    this.evaluatorId = evaluatorId;
  }

  // Getters and Setters
  public Integer getEvaluatorAssignmentId() {
    return evaluatorAssignmentId;
  }

  public void setEvaluatorAssignmentId(Integer evaluatorAssignmentId) {
    this.evaluatorAssignmentId = evaluatorAssignmentId;
  }

  public Integer getSessionId() {
    return sessionId;
  }

  public void setSessionId(Integer sessionId) {
    this.sessionId = sessionId;
  }

  public Integer getEvaluatorId() {
    return evaluatorId;
  }

  public void setEvaluatorId(Integer evaluatorId) {
    this.evaluatorId = evaluatorId;
  }

  @Override
  public String toString() {
    return "EvaluatorAssignment{" +
        "evaluatorAssignmentId=" + evaluatorAssignmentId +
        ", sessionId=" + sessionId +
        ", evaluatorId=" + evaluatorId +
        '}';
  }
}