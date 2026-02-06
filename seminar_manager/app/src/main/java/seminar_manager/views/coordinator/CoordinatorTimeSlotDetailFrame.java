package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import seminar_manager.models.enums.SubmissionStatus;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CoordinatorTimeSlotDetailFrame extends JFrame {
    private AuthController authController;
    private TimeSlotController timeSlotController;
    private SubmissionController submissionController;
    private SessionController sessionController;

    private int timeSlotId;
    private int sessionId;
    private TimeSlot timeSlot;
    private Session session;
    private JComboBox<String> submissionComboBox;
    private List<Submission> availableSubmissions;

    public CoordinatorTimeSlotDetailFrame(AuthController authController, int timeSlotId, int sessionId) {
        this.authController = authController;
        this.timeSlotController = new TimeSlotController();
        this.submissionController = new SubmissionController();
        this.sessionController = new SessionController();
        this.timeSlotId = timeSlotId;
        this.sessionId = sessionId;

        loadData();
        initComponents();

        setTitle("Time Slot Details");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadData() {
        this.timeSlot = timeSlotController.getTimeSlotById(timeSlotId);
        this.session = sessionController.getSessionById(sessionId);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Time Slot Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Time Slot Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Time Slot Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int row = 0;
        addDetailRow(infoPanel, gbc, row++, "Time Slot ID:", String.valueOf(timeSlot.getTimeSlotId()));
        addDetailRow(infoPanel, gbc, row++, "Start Time:", timeFormat.format(timeSlot.getStartTime()));
        addDetailRow(infoPanel, gbc, row++, "End Time:", timeFormat.format(timeSlot.getEndTime()));

        if (timeSlot.getSubmissionId() != null) {
            Submission currentSubmission = submissionController.getSubmissionById(timeSlot.getSubmissionId());
            if (currentSubmission != null) {
                addDetailRow(infoPanel, gbc, row++, "Current Assignment:",
                        currentSubmission.getResearchTitle());
            }
        } else {
            addDetailRow(infoPanel, gbc, row++, "Current Assignment:", "Not assigned");
        }

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Assignment Panel
        JPanel assignmentPanel = new JPanel(new BorderLayout(10, 10));
        assignmentPanel.setBorder(BorderFactory.createTitledBorder("Assign Submission"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Select Submission:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        submissionComboBox = new JComboBox<>();
        loadAvailableSubmissions();
        formPanel.add(submissionComboBox, gbc);

        assignmentPanel.add(formPanel, BorderLayout.CENTER);

        JPanel assignButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton assignButton = new JButton("Assign");
        assignButton.addActionListener(e -> handleAssign());
        assignButtonPanel.add(assignButton);

        if (timeSlot.getSubmissionId() != null) {
            JButton unassignButton = new JButton("Unassign");
            unassignButton.addActionListener(e -> handleUnassign());
            assignButtonPanel.add(unassignButton);
        }

        assignmentPanel.add(assignButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(assignmentPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            new CoordinatorSessionDetailFrame(authController, sessionId);
            dispose();
        });
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAvailableSubmissions() {
        submissionComboBox.removeAllItems();

        // Get all submissions for the seminar with matching presentation type
        List<Submission> allSubmissions = submissionController.getSubmissionsBySeminar(
                session.getSeminarId());

        availableSubmissions = new java.util.ArrayList<>();

        for (Submission submission : allSubmissions) {
            // Only show approved submissions with matching presentation type
            if (submission.getStatus() == SubmissionStatus.APPROVED &&
                    submission.getPresentationType() == session.getPresentationType()) {

                // Check if not already assigned to another time slot
                TimeSlot existingSlot = timeSlotController.getTimeSlotBySubmission(
                        submission.getSubmissionId());

                if (existingSlot == null || existingSlot.getTimeSlotId() == timeSlotId) {
                    availableSubmissions.add(submission);
                    submissionComboBox.addItem(submission.getSubmissionId() + " - " +
                            submission.getResearchTitle());
                }
            }
        }

        if (submissionComboBox.getItemCount() == 0) {
            submissionComboBox.addItem("No available submissions");
        }
    }

    private void handleAssign() {
        int selectedIndex = submissionComboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a submission to assign",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (submissionComboBox.getItemAt(selectedIndex).equals("No available submissions")) {
            return;
        }

        Submission selectedSubmission = availableSubmissions.get(selectedIndex);

        boolean success = timeSlotController.assignSubmissionToTimeSlot(
                timeSlotId, selectedSubmission.getSubmissionId());

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Submission assigned successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new CoordinatorSessionDetailFrame(authController, sessionId);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to assign submission",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUnassign() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unassign this submission from the time slot?",
                "Confirm Unassign",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = timeSlotController.unassignSubmissionFromTimeSlot(timeSlotId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Submission unassigned successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new CoordinatorSessionDetailFrame(authController, sessionId);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to unassign submission",
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