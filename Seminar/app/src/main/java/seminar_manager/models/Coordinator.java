package seminar_manager.models;

import java.util.Date;

import seminar_manager.models.enums.UserRole;

public class Coordinator extends User {
    private String coordinatorId;

    // Constructor for creating new coordinator (no User ID yet)
    public Coordinator(String username, String password, String fullName, String email, String coordinatorId) {
        super(username, password, fullName, email, UserRole.COORDINATOR);
        this.coordinatorId = coordinatorId;
    }

    // Constructor for loading from database (with User ID)
    public Coordinator(Integer userId, String username, String password, String fullName,
            String email, Date createdAt, String coordinatorId) {
        super(userId, username, password, fullName, email, UserRole.COORDINATOR, createdAt);
        this.coordinatorId = coordinatorId;
    }

    // Getters and Setters
    public String getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(String coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    @Override
    public String toString() {
        return "Coordinator{" +
                "coordinatorId='" + coordinatorId + '\'' +
                ", userId=" + getUserId() +
                ", fullName='" + getFullName() + '\'' +
                '}';
    }
}