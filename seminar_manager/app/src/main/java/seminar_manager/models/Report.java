package seminar_manager.models;

import java.util.Date;

import seminar_manager.models.enums.ReportType;

public class Report {
    private Integer reportId;
    private Integer seminarId;
    private ReportType reportType;
    private Date generatedAt;
    private String filePath;
    private String reportContent;

    // Constructor for creating new report (no ID yet)
    public Report(Integer seminarId, ReportType reportType, String reportContent) {
        this.seminarId = seminarId;
        this.reportType = reportType;
        this.reportContent = reportContent;
        this.generatedAt = new Date();
    }

    // Constructor for loading from database (with ID)
    public Report(Integer reportId, Integer seminarId, ReportType reportType,
            Date generatedAt, String filePath, String reportContent) {
        this.reportId = reportId;
        this.seminarId = seminarId;
        this.reportType = reportType;
        this.generatedAt = generatedAt;
        this.filePath = filePath;
        this.reportContent = reportContent;
    }

    // Getters and Setters
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getSeminarId() {
        return seminarId;
    }

    public void setSeminarId(Integer seminarId) {
        this.seminarId = seminarId;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", reportType=" + reportType +
                ", generatedAt=" + generatedAt +
                '}';
    }
}