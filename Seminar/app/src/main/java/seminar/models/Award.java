package seminar.models;

import seminar.models.enums.AwardType;

public class Award {
    private Integer awardId;
    private Integer seminarId;
    private Integer submissionId;
    private AwardType awardType;

    // Constructor for creating new award (no ID yet)
    public Award(Integer seminarId, Integer submissionId, AwardType awardType) {
        this.seminarId = seminarId;
        this.submissionId = submissionId; // possibly might have to create this without submission yet ? anyway we ball
        this.awardType = awardType;
    }

    // Constructor for loading from database (with ID)
    public Award(Integer awardId, Integer seminarId, Integer submissionId, AwardType awardType) {
        this.awardId = awardId;
        this.seminarId = seminarId;
        this.submissionId = submissionId;
        this.awardType = awardType;
    }

    // Getters and Setters
    public Integer getAwardId() {
        return awardId;
    }

    public void setAwardId(Integer awardId) {
        this.awardId = awardId;
    }

    public Integer getSeminarId() {
        return seminarId;
    }

    public void setSeminarId(Integer seminarId) {
        this.seminarId = seminarId;
    }

    public Integer getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Integer submissionId) {
        this.submissionId = submissionId;
    }

    public AwardType getAwardType() {
        return awardType;
    }

    public void setAwardType(AwardType awardType) {
        this.awardType = awardType;
    }

    @Override
    public String toString() {
        return "Award{" +
                "awardId=" + awardId +
                ", awardType=" + awardType +
                ", submissionId=" + submissionId +
                '}';
    }
}