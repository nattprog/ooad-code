package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import seminar_manager.models.enums.AwardType;
import seminar_manager.models.enums.SubmissionStatus;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CoordinatorSubmissionDetailFrame extends JFrame {
    private AuthController authController;
    private SubmissionController submissionController;
    private SeminarController seminarController;
    private SessionController sessionController;
    private TimeSlotController timeSlotController;
    private EvaluationController evaluationController;
    private AwardController awardController;

    private int submissionId;
    private int seminarId;
    private Submission submission;
    private JComboBox<SubmissionStatus> statusComboBox;
    private JPanel evaluationsPanel;
    private JPanel awardsPanel;

    public CoordinatorSubmissionDetailFrame(AuthController authController, int submissionId, int seminarId) {
        this.authController = authController;
        this.submissionController = new SubmissionController();
        this.seminarController = new SeminarController();
        this.sessionController = new SessionController();
        this.timeSlotController = new TimeSlotController();
        this.evaluationController = new EvaluationController();
        this.awardController = new AwardController();
        this.submissionId = submissionId;
        this.seminarId = seminarId;

        loadSubmission();
        initComponents();

        setTitle("Submission Details");
        setSize(800, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadSubmission() {
        this.submission = submissionController.getSubmissionById(submissionId);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Submission Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Content Panel with Scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Submission Details Panel
        JPanel detailsPanel = createDetailsPanel();
        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Status Management Panel
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Evaluations Panel
        evaluationsPanel = createEvaluationsPanel();
        mainPanel.add(evaluationsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Awards Panel
        awardsPanel = createAwardsPanel();
        mainPanel.add(awardsPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Submission Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Seminar seminar = seminarController.getSeminarById(submission.getSeminarId());
        Session session = submission.getSessionId() != null
                ? sessionController.getSessionById(submission.getSessionId())
                : null;
        TimeSlot timeSlot = timeSlotController.getTimeSlotBySubmission(submissionId);

        seminar_manager.database.dao.UserDAO userDAO = new seminar_manager.database.dao.UserDAO();
        User student = userDAO.getUserById(submission.getStudentId());

        int row = 0;
        addDetailRow(panel, gbc, row++, "Submission ID:", String.valueOf(submission.getSubmissionId()));
        addDetailRow(panel, gbc, row++, "Title:", submission.getResearchTitle());
        addDetailRow(panel, gbc, row++, "Student:", student != null ? student.getFullName() : "Unknown");
        addDetailRow(panel, gbc, row++, "Seminar:", seminar != null ? seminar.getTitle() : "N/A");
        addDetailRow(panel, gbc, row++, "Type:", submission.getPresentationType().toString());
        addDetailRow(panel, gbc, row++, "Status:", submission.getStatus().toString());
        addDetailRow(panel, gbc, row++, "Supervisor:", submission.getSupervisorName());
        addDetailRow(panel, gbc, row++, "Submitted:", dateFormat.format(submission.getSubmittedAt()));

        if (session != null) {
            addDetailRow(panel, gbc, row++, "Session:", "Session " + session.getSessionId());
        }

        if (timeSlot != null) {
            addDetailRow(panel, gbc, row++, "Time Slot:",
                    dateFormat.format(timeSlot.getStartTime()) + " - " +
                            dateFormat.format(timeSlot.getEndTime()));
        }

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Abstract:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JTextArea abstractArea = new JTextArea(submission.getResearchAbstract());
        abstractArea.setWrapStyleWord(true);
        abstractArea.setLineWrap(true);
        abstractArea.setEditable(false);
        abstractArea.setRows(5);
        JScrollPane abstractScroll = new JScrollPane(abstractArea);
        panel.add(abstractScroll, gbc);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Status Management"));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        formPanel.add(new JLabel("Set Status:"));
        statusComboBox = new JComboBox<>(SubmissionStatus.values());
        statusComboBox.setSelectedItem(submission.getStatus());
        formPanel.add(statusComboBox);

        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(e -> handleUpdateStatus());
        formPanel.add(updateStatusButton);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEvaluationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Evaluations"));

        List<Evaluation> evaluations = evaluationController.getEvaluationsBySubmission(submissionId);

        if (!evaluations.isEmpty()) {
            JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            int evaluationCount = 0;
            for (Evaluation eval : evaluations) {
                if (eval.getTotalScore() != null) { // Only show completed evaluations
                    evaluationCount++;
                    JPanel evalPanel = new JPanel(new GridLayout(0, 1));
                    evalPanel.setBorder(BorderFactory.createEtchedBorder());

                    evalPanel.add(new JLabel("Evaluation #" + evaluationCount));
                    evalPanel.add(new JLabel("Problem Clarity: " + eval.getProblemClarityScore()));
                    evalPanel.add(new JLabel("Methodology: " + eval.getMethodologyScore()));
                    evalPanel.add(new JLabel("Results: " + eval.getResultsScore()));
                    evalPanel.add(new JLabel("Presentation: " + eval.getPresentationScore()));
                    evalPanel.add(new JLabel("Total Score: " + eval.getTotalScore()));
                    if (eval.getComments() != null && !eval.getComments().trim().isEmpty()) {
                        JTextArea commentsArea = new JTextArea(eval.getComments());
                        commentsArea.setWrapStyleWord(true);
                        commentsArea.setLineWrap(true);
                        commentsArea.setEditable(false);
                        commentsArea.setRows(2);
                        commentsArea.setBorder(BorderFactory.createTitledBorder("Comments"));
                        evalPanel.add(commentsArea);
                    }

                    listPanel.add(evalPanel);
                }
            }

            Double avgScore = evaluationController.getAverageScoreForSubmission(submissionId);
            if (avgScore != null && evaluationCount > 0) {
                JLabel avgLabel = new JLabel(String.format("Average Score: %.2f", avgScore));
                avgLabel.setFont(new Font("Arial", Font.BOLD, 14));
                panel.add(avgLabel, BorderLayout.NORTH);
            }

            if (evaluationCount > 0) {
                JScrollPane scrollPane = new JScrollPane(listPanel);
                scrollPane.setPreferredSize(new Dimension(0, 200));
                panel.add(scrollPane, BorderLayout.CENTER);
            } else {
                panel.add(new JLabel("No completed evaluations yet", SwingConstants.CENTER),
                        BorderLayout.CENTER);
            }
        } else {
            panel.add(new JLabel("No evaluations yet", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createAwardsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Awards Management"));

        // Current Awards
        List<Award> awards = awardController.getAwardsBySubmission(submissionId);

        JPanel currentAwardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentAwardsPanel.setBorder(BorderFactory.createTitledBorder("Current Awards"));

        if (!awards.isEmpty()) {
            for (Award award : awards) {
                JPanel awardPanel = new JPanel(new BorderLayout(5, 5));
                awardPanel.setBorder(BorderFactory.createEtchedBorder());

                JLabel awardLabel = new JLabel("🏆 " + award.getAwardType());
                awardLabel.setFont(new Font("Arial", Font.BOLD, 14));
                awardPanel.add(awardLabel, BorderLayout.CENTER);

                JButton deleteButton = new JButton("Remove");
                deleteButton.setForeground(Color.RED);
                final int awardId = award.getAwardId();
                deleteButton.addActionListener(e -> handleRemoveAward(awardId));
                awardPanel.add(deleteButton, BorderLayout.EAST);

                currentAwardsPanel.add(awardPanel);
            }
        } else {
            currentAwardsPanel.add(new JLabel("No awards yet"));
        }

        panel.add(currentAwardsPanel, BorderLayout.NORTH);

        // Add Award Form
        JPanel addAwardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addAwardPanel.setBorder(BorderFactory.createTitledBorder("Add Award"));

        addAwardPanel.add(new JLabel("Award Type:"));
        JComboBox<AwardType> awardTypeCombo = new JComboBox<>(AwardType.values());
        addAwardPanel.add(awardTypeCombo);

        JButton addAwardButton = new JButton("Add Award");
        addAwardButton.addActionListener(e -> {
            AwardType selectedType = (AwardType) awardTypeCombo.getSelectedItem();
            handleAddAward(selectedType);
        });
        addAwardPanel.add(addAwardButton);

        panel.add(addAwardPanel, BorderLayout.CENTER);

        return panel;
    }

    private void handleUpdateStatus() {
        SubmissionStatus newStatus = (SubmissionStatus) statusComboBox.getSelectedItem();

        if (newStatus == submission.getStatus()) {
            JOptionPane.showMessageDialog(this,
                    "Status is already set to " + newStatus,
                    "No Change",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        boolean success = submissionController.updateSubmissionStatus(submissionId, newStatus);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Status updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            submission.setStatus(newStatus);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update status",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddAward(AwardType awardType) {
        // Check if this award type already exists
        List<Award> existingAwards = awardController.getAwardsBySubmission(submissionId);
        for (Award award : existingAwards) {
            if (award.getAwardType() == awardType) {
                JOptionPane.showMessageDialog(this,
                        "This submission already has a " + awardType + " award",
                        "Award Exists",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        boolean success = awardController.createAward(seminarId, submissionId, awardType);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Award added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshAwardsPanel();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to add award",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRemoveAward(int awardId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this award?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = awardController.deleteAward(awardId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Award removed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshAwardsPanel();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to remove award",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAwardsPanel() {
        // Remove old panel
        Container parent = awardsPanel.getParent();
        parent.remove(awardsPanel);

        // Create new panel
        awardsPanel = createAwardsPanel();
        parent.add(awardsPanel);

        // Refresh
        parent.revalidate();
        parent.repaint();
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row,
            String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(value), gbc);
    }
}