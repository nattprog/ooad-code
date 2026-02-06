package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CoordinatorSeminarDetailFrame extends JFrame {
    private AuthController authController;
    private SeminarController seminarController;
    private SessionController sessionController;
    private ReportController reportController;

    private int seminarId;
    private Seminar seminar;
    private JPanel sessionsPanel;

    public CoordinatorSeminarDetailFrame(AuthController authController, int seminarId) {
        this.authController = authController;
        this.seminarController = new SeminarController();
        this.sessionController = new SessionController();
        this.reportController = new ReportController();
        this.seminarId = seminarId;

        loadSeminar();
        initComponents();

        setTitle("Seminar Details");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadSeminar() {
        this.seminar = seminarController.getSeminarById(seminarId);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Seminar Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Seminar Details Panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Seminar Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int row = 0;
        addDetailRow(detailsPanel, gbc, row++, "Title:", seminar.getTitle());
        addDetailRow(detailsPanel, gbc, row++, "Description:", seminar.getDescription());
        addDetailRow(detailsPanel, gbc, row++, "Location:", seminar.getLocation());
        addDetailRow(detailsPanel, gbc, row++, "Start Time:", dateFormat.format(seminar.getStartTime()));
        addDetailRow(detailsPanel, gbc, row++, "End Time:", dateFormat.format(seminar.getEndTime()));

        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sessions Panel
        sessionsPanel = new JPanel(new BorderLayout());
        sessionsPanel.setBorder(BorderFactory.createTitledBorder("Sessions"));
        loadSessions();
        mainPanel.add(sessionsPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton createSessionButton = new JButton("Create New Session");
        createSessionButton.addActionListener(e -> {
            new CoordinatorCreateSessionFrame(authController, seminarId);
            dispose();
        });
        buttonPanel.add(createSessionButton);

        JButton submissionsButton = new JButton("View Submissions");
        submissionsButton.addActionListener(e -> {
            new CoordinatorSubmissionsFrame(authController, seminarId);
        });
        buttonPanel.add(submissionsButton);

        JButton generateScheduleButton = new JButton("Generate Schedule");
        generateScheduleButton.addActionListener(e -> handleGenerateSchedule());
        buttonPanel.add(generateScheduleButton);

        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(e -> handleGenerateReport());
        buttonPanel.add(generateReportButton);

        JButton deleteButton = new JButton("Delete Seminar");
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(e -> handleDelete());
        buttonPanel.add(deleteButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadSessions() {
        sessionsPanel.removeAll();

        List<Session> sessions = sessionController.getSessionsBySeminar(seminarId);

        if (!sessions.isEmpty()) {
            JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            for (Session session : sessions) {
                JPanel sessionPanel = new JPanel(new BorderLayout(5, 5));
                sessionPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1));
                infoPanel.add(new JLabel("Session ID: " + session.getSessionId()));
                infoPanel.add(new JLabel("Type: " + session.getPresentationType()));
                infoPanel.add(new JLabel("Time: " + session.getStartTime()));
                infoPanel.add(new JLabel("Slots: " + session.getTimeSlotsCount()));

                JButton viewButton = new JButton("View");
                viewButton.addActionListener(e -> {
                    new CoordinatorSessionDetailFrame(authController, session.getSessionId());
                    dispose();
                });

                sessionPanel.add(infoPanel, BorderLayout.CENTER);
                sessionPanel.add(viewButton, BorderLayout.EAST);

                listPanel.add(sessionPanel);
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setPreferredSize(new Dimension(0, 200));
            sessionsPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            sessionsPanel.add(new JLabel("No sessions yet. Click 'Create New Session' to add one.",
                    SwingConstants.CENTER), BorderLayout.CENTER);
        }

        sessionsPanel.revalidate();
        sessionsPanel.repaint();
    }

    private void handleGenerateSchedule() {
        boolean success = reportController.generateSessionScheduleReport(seminarId);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Schedule report generated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to generate schedule report",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleGenerateReport() {
        String[] options = { "Evaluation Summary", "Award List", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this,
                "Select report type:",
                "Generate Report",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        boolean success = false;

        if (choice == 0) {
            success = reportController.generateEvaluationSummaryReport(seminarId);
        } else if (choice == 1) {
            success = reportController.generateAwardListReport(seminarId);
        } else {
            return;
        }

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Report generated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to generate report",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this seminar?\n" +
                        "This will also delete all sessions, submissions, and evaluations.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = seminarController.deleteSeminar(seminarId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Seminar deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new CoordinatorSeminarsFrame(authController);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete seminar",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row,
            String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(new JLabel(value), gbc);
    }
}