package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import seminar_manager.views.LoginFrame;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CoordinatorHomeFrame extends JFrame {
    private AuthController authController;
    private SeminarController seminarController;

    private Coordinator currentCoordinator;
    private JPanel upcomingSeminarsPanel;

    public CoordinatorHomeFrame(AuthController authController) {
        this.authController = authController;
        this.seminarController = new SeminarController();
        this.currentCoordinator = authController.getCurrentCoordinator();

        initComponents();
        loadData();

        setTitle("Coordinator Home - Seminar Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Coordinator Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + currentCoordinator.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(userLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Main Content Panel
        upcomingSeminarsPanel = new JPanel(new BorderLayout());
        upcomingSeminarsPanel.setBorder(BorderFactory.createTitledBorder("Upcoming Seminars"));
        add(upcomingSeminarsPanel, BorderLayout.CENTER);

        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton viewSeminarsButton = new JButton("View All Seminars");
        viewSeminarsButton.addActionListener(e -> new CoordinatorSeminarsFrame(authController));
        navPanel.add(viewSeminarsButton);

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
        loadUpcomingSeminars();
    }

    private void loadUpcomingSeminars() {
        upcomingSeminarsPanel.removeAll();

        List<Seminar> seminars = seminarController.getUpcomingSeminars(3);

        if (!seminars.isEmpty()) {
            JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (Seminar seminar : seminars) {
                JPanel seminarPanel = new JPanel(new BorderLayout(5, 5));
                seminarPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1));
                infoPanel.add(new JLabel("Title: " + seminar.getTitle()));
                infoPanel.add(new JLabel("Location: " + seminar.getLocation()));
                infoPanel.add(new JLabel("Date: " + seminar.getStartTime()));

                JButton viewButton = new JButton("View");
                viewButton.addActionListener(
                        e -> new CoordinatorSeminarDetailFrame(authController, seminar.getSeminarId()));

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