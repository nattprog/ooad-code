package seminar.controllers;

import seminar.database.dao.*;
import seminar.models.*;
import seminar.models.enums.UserRole;

public class AuthController {
    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private EvaluatorDAO evaluatorDAO;
    private CoordinatorDAO coordinatorDAO;
    private User currentUser;

    public AuthController() {
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.evaluatorDAO = new EvaluatorDAO();
        this.coordinatorDAO = new CoordinatorDAO();
    }

    // Register new user
    public boolean register(String username, String password, String fullName,
            String email, UserRole role, String roleId) {
        try {
            switch (role) {
                case STUDENT:
                    Student student = new Student(username, password, fullName, email, roleId);
                    return studentDAO.createStudent(student);

                case EVALUATOR:
                    Evaluator evaluator = new Evaluator(username, password, fullName, email, roleId);
                    return evaluatorDAO.createEvaluator(evaluator);

                case COORDINATOR:
                    Coordinator coordinator = new Coordinator(username, password, fullName, email, roleId);
                    return coordinatorDAO.createCoordinator(coordinator);

                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login
    public User login(String username, String password) {
        User user = userDAO.validateLogin(username, password);
        if (user != null) {
            currentUser = user;
            return user;
        }
        return null;
    }

    // Logout
    public void logout() {
        currentUser = null;
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Get specific user type
    public Student getCurrentStudent() {
        if (currentUser != null && currentUser.getRole() == UserRole.STUDENT) {
            return studentDAO.getStudentByUserId(currentUser.getUserId());
        }
        return null;
    }

    public Evaluator getCurrentEvaluator() {
        if (currentUser != null && currentUser.getRole() == UserRole.EVALUATOR) {
            return evaluatorDAO.getEvaluatorByUserId(currentUser.getUserId());
        }
        return null;
    }

    public Coordinator getCurrentCoordinator() {
        if (currentUser != null && currentUser.getRole() == UserRole.COORDINATOR) {
            return coordinatorDAO.getCoordinatorByUserId(currentUser.getUserId());
        }
        return null;
    }
}