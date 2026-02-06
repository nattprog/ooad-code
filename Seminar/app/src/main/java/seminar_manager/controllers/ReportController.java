package seminar_manager.controllers;

import java.util.Date;
import java.util.List;

import seminar_manager.database.dao.*;
import seminar_manager.models.*;
import seminar_manager.models.enums.ReportType;

import java.text.SimpleDateFormat;

public class ReportController {
  private ReportDAO reportDAO;
  private SeminarDAO seminarDAO;
  private SessionDAO sessionDAO;
  private SubmissionDAO submissionDAO;
  private AwardDAO awardDAO;
  private EvaluationDAO evaluationDAO;

  public ReportController() {
    this.reportDAO = new ReportDAO();
    this.seminarDAO = new SeminarDAO();
    this.sessionDAO = new SessionDAO();
    this.submissionDAO = new SubmissionDAO();
    this.awardDAO = new AwardDAO();
    this.evaluationDAO = new EvaluationDAO();
  }

  // Generate session schedule report
  public boolean generateSessionScheduleReport(int seminarId) {
    Seminar seminar = seminarDAO.getSeminarById(seminarId);
    if (seminar == null) {
      return false;
    }

    List<Session> sessions = sessionDAO.getSessionsBySeminar(seminarId);

    StringBuilder content = new StringBuilder();
    content.append("SESSION SCHEDULE REPORT\n");
    content.append("======================\n\n");
    content.append("Seminar: ").append(seminar.getTitle()).append("\n");
    content.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd").format(seminar.getStartTime())).append("\n");
    content.append("Location: ").append(seminar.getLocation()).append("\n\n");

    for (Session session : sessions) {
      content.append("Session ID: ").append(session.getSessionId()).append("\n");
      content.append("Type: ").append(session.getPresentationType()).append("\n");
      content.append("Time: ").append(new SimpleDateFormat("HH:mm").format(session.getStartTime()))
          .append(" - ").append(new SimpleDateFormat("HH:mm").format(session.getEndTime())).append("\n");
      content.append("Slots: ").append(session.getTimeSlotsCount()).append("\n\n");
    }

    Report report = new Report(seminarId, ReportType.SESSION_SCHEDULE, content.toString());
    return reportDAO.createReport(report);
  }

  // Generate evaluation summary report
  public boolean generateEvaluationSummaryReport(int seminarId) {
    List<Submission> submissions = submissionDAO.getSubmissionsBySeminar(seminarId);

    StringBuilder content = new StringBuilder();
    content.append("EVALUATION SUMMARY REPORT\n");
    content.append("========================\n\n");

    for (Submission submission : submissions) {
      content.append("Submission: ").append(submission.getResearchTitle()).append("\n");
      List<Evaluation> evaluations = evaluationDAO.getEvaluationsBySubmission(submission.getSubmissionId());

      if (!evaluations.isEmpty()) {
        Double avgScore = evaluationDAO.getAverageScoreForSubmission(submission.getSubmissionId());
        content.append("Average Score: ").append(avgScore != null ? String.format("%.2f", avgScore) : "N/A")
            .append("\n");
        content.append("Number of Evaluations: ").append(evaluations.size()).append("\n\n");
      } else {
        content.append("No evaluations yet\n\n");
      }
    }

    Report report = new Report(seminarId, ReportType.EVALUATION_SUMMARY, content.toString());
    return reportDAO.createReport(report);
  }

  // Generate award list report
  public boolean generateAwardListReport(int seminarId) {
    List<Award> awards = awardDAO.getAwardsBySeminar(seminarId);

    StringBuilder content = new StringBuilder();
    content.append("AWARD LIST REPORT\n");
    content.append("================\n\n");

    for (Award award : awards) {
      Submission submission = submissionDAO.getSubmissionById(award.getSubmissionId());
      if (submission != null) {
        content.append("Award: ").append(award.getAwardType()).append("\n");
        content.append("Winner: ").append(submission.getResearchTitle()).append("\n\n");
      }
    }

    Report report = new Report(seminarId, ReportType.AWARD_LIST, content.toString());
    return reportDAO.createReport(report);
  }

  // Get report by ID
  public Report getReportById(int reportId) {
    return reportDAO.getReportById(reportId);
  }

  // Get all reports
  public List<Report> getAllReports() {
    return reportDAO.getAllReports();
  }

  // Get reports by seminar
  public List<Report> getReportsBySeminar(int seminarId) {
    return reportDAO.getReportsBySeminar(seminarId);
  }
}