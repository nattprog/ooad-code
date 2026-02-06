package seminar_manager.views.evaluator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import seminar_manager.views.LoginFrame;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EvaluatorHomeFrame extends JFrame {
    private AuthController authController;
    private SeminarController seminarController;
    private SessionController sessionController;
    private EvaluatorAssignmentController evaluatorAssignmentController;

    private Evaluator currentEvaluator;
    private JPanel upcomingSessionsPanel;
    private JPanel upcomingSeminarsPanel;

    public EvaluatorHomeFrame(AuthController authController) {
        this.authController = authController;
        this.seminarController = new SeminarController();
        this.sessionController = new SessionController();
        this.evaluatorAssignmentController = new EvaluatorAssignmentController();
        this.currentEvaluator = authController.getCurrentEvaluator();

        initComponents();
        loadData();

        setTitle("Evaluator Home - Seminar Management System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Evaluator Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + currentEvaluator.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(userLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Upcoming Sessions Panel
        upcomingSessionsPanel = new JPanel(new BorderLayout());
        upcomingSessionsPanel.setBorder(BorderFactory.createTitledBorder("My Upcoming Sessions"));
        mainPanel.add(upcomingSessionsPanel);

        // Upcoming Seminars Panel
        upcomingSeminarsPanel = new JPanel(new BorderLayout());
        upcomingSeminarsPanel.setBorder(BorderFactory.createTitledBorder("Upcoming Seminars (My Assignments)"));
        mainPanel.add(upcomingSeminarsPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton profileButton = new JButton("Profile");
        profileButton.addActionListener(e -> new EvaluatorProfileFrame(authController));
        navPanel.add(profileButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            authController.logout();
            new LoginFrame();
            dispose();
        });
        navPanel.add(logoutButton);

        add(navPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        loadUpcomingSessions();
        loadUpcomingSeminars();
    }

    private void loadUpcomingSessions() {
        upcomingSessionsPanel.removeAll();

        // Get all evaluator assignments for this evaluator
        List<EvaluatorAssignment> assignments = evaluatorAssignmentController
                .getAssignmentsByEvaluatorUserId(currentEvaluator.getUserId());

        // Get sessions and sort by start time
        List<Session> sessions = new ArrayList<>();
        for (EvaluatorAssignment assignment : assignments) {
            Session session = sessionController.getSessionById(assignment.getSessionId());
            if (session != null && session.getStartTime().after(new java.util.Date())) {
                sessions.add(session);
            }
        }

        Collections.sort(sessions, Comparator.comparing(Session::getStartTime));

        if (!sessions.isEmpty()) {
            // Limit to 5 closest sessions
            List<Session> limitedSessions = sessions.size() > 5 ? sessions.subList(0, 5) : sessions;

            JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (Session session : limitedSessions) {
                JPanel sessionPanel = new JPanel(new BorderLayout(5, 5));
                sessionPanel.setBorder(BorderFactory.createEtchedBorder());

                Seminar seminar = seminarController.getSeminarById(session.getSeminarId());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1));
                infoPanel.add(new JLabel("Seminar: " + (seminar != null ? seminar.getTitle() : "N/A")));
                infoPanel.add(new JLabel("Type: " + session.getPresentationType()));
                infoPanel.add(new JLabel("Time: " + session.getStartTime()));

                JButton viewButton = new JButton("View");
                viewButton.addActionListener(
                        e -> new EvaluatorSessionDetailFrame(authController, session.getSessionId()));

                sessionPanel.add(infoPanel, BorderLayout.CENTER);
                sessionPanel.add(viewButton, BorderLayout.EAST);

                listPanel.add(sessionPanel);
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            upcomingSessionsPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            upcomingSessionsPanel.add(new JLabel("No upcoming sessions", SwingConstants.CENTER),
                    BorderLayout.CENTER);
        }

        upcomingSessionsPanel.revalidate();
        upcomingSessionsPanel.repaint();
    }

    private void loadUpcomingSeminars() {
        upcomingSeminarsPanel.removeAll();

        // Get all evaluator assignments
        List<EvaluatorAssignment> assignments = evaluatorAssignmentController
                .getAssignmentsByEvaluatorUserId(currentEvaluator.getUserId());

        // Get unique seminars from sessions
        List<Seminar> seminars = new ArrayList<>();
        for (EvaluatorAssignment assignment : assignments) {
            Session session = sessionController.getSessionById(assignment.getSessionId());
            if (session != null) {
                Seminar seminar = seminarController.getSeminarById(session.getSeminarId());
                if (seminar != null && seminar.getStartTime().after(new java.util.Date())) {
                    if (!seminars.contains(seminar)) {
                        seminars.add(seminar);
                    }
                }
            }
        }

        Collections.sort(seminars, Comparator.comparing(Seminar::getStartTime));

        if (!seminars.isEmpty()) {
            JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (Seminar seminar : seminars) {
                JPanel seminarPanel = new JPanel(new BorderLayout(5, 5));
                seminarPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1));
                infoPanel.add(new JLabel(seminar.getTitle()));
                infoPanel.add(new JLabel(seminar.getStartTime().toString()));

                JButton viewButton = new JButton("View");
                viewButton.addActionListener(
                        e -> new EvaluatorSeminarDetailFrame(authController, seminar.getSeminarId()));

                seminarPanel.add(infoPanel, BorderLayout.CENTER);
                seminarPanel.add(viewButton, BorderLayout.EAST);

                listPanel.add(seminarPanel);
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            upcomingSeminarsPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            upcomingSeminarsPanel.add(new JLabel("No upcoming seminars", SwingConstants.CENTER),
                    BorderLayout.CENTER);
        }

        upcomingSeminarsPanel.revalidate();
        upcomingSeminarsPanel.repaint();
    }
}