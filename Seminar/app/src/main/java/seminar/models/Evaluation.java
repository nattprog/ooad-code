package seminar.models;

public class Evaluation {
  private Integer evaluationId;
  private Integer evaluatorAssignmentId;
  private Integer submissionId;
  private Integer problemClarityScore;
  private Integer methodologyScore;
  private Integer resultsScore;
  private Integer presentationScore;
  private Integer totalScore;
  private String comments;

  // Constructor for creating new evaluation (no ID yet)
  public Evaluation(Integer evaluatorAssignmentId, Integer submissionId) {
    this.evaluatorAssignmentId = evaluatorAssignmentId;
    this.submissionId = submissionId;
  }

  // Constructor for loading from database (with ID)
  public Evaluation(Integer evaluationId, Integer evaluatorAssignmentId, Integer submissionId,
      Integer problemClarityScore, Integer methodologyScore, Integer resultsScore,
      Integer presentationScore, Integer totalScore, String comments) {
    this.evaluationId = evaluationId;
    this.evaluatorAssignmentId = evaluatorAssignmentId;
    this.submissionId = submissionId;
    this.problemClarityScore = problemClarityScore;
    this.methodologyScore = methodologyScore;
    this.resultsScore = resultsScore;
    this.presentationScore = presentationScore;
    this.totalScore = totalScore;
    this.comments = comments;
  }

  // Getters and Setters
  public Integer getEvaluationId() {
    return evaluationId;
  }

  public void setEvaluationId(Integer evaluationId) {
    this.evaluationId = evaluationId;
  }

  public Integer getEvaluatorAssignmentId() {
    return evaluatorAssignmentId;
  }

  public void setEvaluatorAssignmentId(Integer evaluatorAssignmentId) {
    this.evaluatorAssignmentId = evaluatorAssignmentId;
  }

  public Integer getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(Integer submissionId) {
    this.submissionId = submissionId;
  }

  public Integer getProblemClarityScore() {
    return problemClarityScore;
  }

  public void setProblemClarityScore(Integer problemClarityScore) {
    this.problemClarityScore = problemClarityScore;
    updateTotalScore();
  }

  public Integer getMethodologyScore() {
    return methodologyScore;
  }

  public void setMethodologyScore(Integer methodologyScore) {
    this.methodologyScore = methodologyScore;
    updateTotalScore();
  }

  public Integer getResultsScore() {
    return resultsScore;
  }

  public void setResultsScore(Integer resultsScore) {
    this.resultsScore = resultsScore;
    updateTotalScore();
  }

  public Integer getPresentationScore() {
    return presentationScore;
  }

  public void setPresentationScore(Integer presentationScore) {
    this.presentationScore = presentationScore;
    updateTotalScore();
  }

  public Integer getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(Integer totalScore) {
    this.totalScore = totalScore;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  // Helper method to calculate total score
  private void updateTotalScore() {
    if (problemClarityScore != null && methodologyScore != null &&
        resultsScore != null && presentationScore != null) {
      this.totalScore = problemClarityScore + methodologyScore + resultsScore + presentationScore;
    }
  }

  @Override
  public String toString() {
    return "Evaluation{" +
        "evaluationId=" + evaluationId +
        ", submissionId=" + submissionId +
        ", totalScore=" + totalScore +
        '}';
  }
}