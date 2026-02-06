package seminar_manager.controllers;

import java.util.List;

import seminar_manager.database.dao.AwardDAO;
import seminar_manager.models.Award;
import seminar_manager.models.enums.AwardType;

public class AwardController {
    private AwardDAO awardDAO;
    
    public AwardController() {
        this.awardDAO = new AwardDAO();
    }
    
    // Create award
    public boolean createAward(int seminarId, int submissionId, AwardType awardType) {
        Award award = new Award(seminarId, submissionId, awardType);
        return awardDAO.createAward(award);
    }
    
    // Get award by ID
    public Award getAwardById(int awardId) {
        return awardDAO.getAwardById(awardId);
    }
    
    // Get all awards
    public List<Award> getAllAwards() {
        return awardDAO.getAllAwards();
    }
    
    // Get awards by seminar
    public List<Award> getAwardsBySeminar(int seminarId) {
        return awardDAO.getAwardsBySeminar(seminarId);
    }
    
    // Get awards by submission
    public List<Award> getAwardsBySubmission(int submissionId) {
        return awardDAO.getAwardsBySubmission(submissionId);
    }
    
    // Get awards by type
    public List<Award> getAwardsByType(AwardType type) {
        return awardDAO.getAwardsByType(type);
    }
    
    // Update award
    public boolean updateAward(Award award) {
        return awardDAO.updateAward(award);
    }
    
    // Delete award
    public boolean deleteAward(int awardId) {
        return awardDAO.deleteAward(awardId);
    }
}