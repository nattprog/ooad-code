package seminar_manager.models;

import java.util.Date;

import seminar_manager.models.enums.PresentationType;
import seminar_manager.models.enums.SubmissionStatus;

public class Submission {
  private Integer submissionId;
  private Integer seminarId;
  private Integer sessionId;
  private Integer studentId;
  private String researchTitle;
  private String researchAbstract;
  private String supervisorName;
  private PresentationType presentationType;
  private SubmissionStatus status;
  private Date submittedAt;

  // Constructor for creating new submission (no ID yet)
  public Submission(Integer seminarId, Integer studentId, String researchTitle,
      String researchAbstract, String supervisorName, PresentationType presentationType) {
    this.seminarId = seminarId;
    this.studentId = studentId;
    this.researchTitle = researchTitle;
    this.researchAbstract = researchAbstract;
    this.supervisorName = supervisorName;
    this.presentationType = presentationType;
    this.status = SubmissionStatus.PENDING;
    this.submittedAt = new Date();
  }

  // Constructor for loading from database (with ID)
  public Submission(Integer submissionId, Integer seminarId, Integer sessionId, Integer studentId,
      String researchTitle, String researchAbstract, String supervisorName,
      PresentationType presentationType, SubmissionStatus status, Date submittedAt) {
    this.submissionId = submissionId;
    this.seminarId = seminarId;
    this.sessionId = sessionId;
    this.studentId = studentId;
    this.researchTitle = researchTitle;
    this.researchAbstract = researchAbstract;
    this.supervisorName = supervisorName;
    this.presentationType = presentationType;
    this.status = status;
    this.submittedAt = submittedAt;
  }

  // Getters and Setters
  public Integer getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(Integer submissionId) {
    this.submissionId = submissionId;
  }

  public Integer getSeminarId() {
    return seminarId;
  }

  public void setSeminarId(Integer seminarId) {
    this.seminarId = seminarId;
  }

  public Integer getSessionId() {
    return sessionId;
  }

  public void setSessionId(Integer sessionId) {
    this.sessionId = sessionId;
  }

  public Integer getStudentId() {
    return studentId;
  }

  public void setStudentId(Integer studentId) {
    this.studentId = studentId;
  }

  public String getResearchTitle() {
    return researchTitle;
  }

  public void setResearchTitle(String researchTitle) {
    this.researchTitle = researchTitle;
  }

  public String getResearchAbstract() {
    return researchAbstract;
  }

  public void setResearchAbstract(String researchAbstract) {
    this.researchAbstract = researchAbstract;
  }

  public String getSupervisorName() {
    return supervisorName;
  }

  public void setSupervisorName(String supervisorName) {
    this.supervisorName = supervisorName;
  }

  public PresentationType getPresentationType() {
    return presentationType;
  }

  public void setPresentationType(PresentationType presentationType) {
    this.presentationType = presentationType;
  }

  public SubmissionStatus getStatus() {
    return status;
  }

  public void setStatus(SubmissionStatus status) {
    this.status = status;
  }

  public Date getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(Date submittedAt) {
    this.submittedAt = submittedAt;
  }

  @Override
  public String toString() {
    return "Submission{" +
        "submissionId=" + submissionId +
        ", researchTitle='" + researchTitle + '\'' +
        ", status=" + status +
        '}';
  }
}