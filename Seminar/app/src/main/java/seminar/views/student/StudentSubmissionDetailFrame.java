package seminar.views.student;

import seminar.controllers.*;
import seminar.models.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class StudentSubmissionDetailFrame extends JFrame {
    private AuthController authController;
    private SubmissionController submissionController;
    private SeminarController seminarController;
    private SessionController sessionController;
    private TimeSlotController timeSlotController;
    private EvaluationController evaluationController;
    private AwardController awardController;

    private Student currentStudent;
    private int submissionId;
    private Submission submission;

    public StudentSubmissionDetailFrame(AuthController authController, int submissionId) {
        this.authController = authController;
        this.submissionController = new SubmissionController();
        this.seminarController = new SeminarController();
        this.sessionController = new SessionController();
        this.timeSlotController = new TimeSlotController();
        this.evaluationController = new EvaluationController();
        this.awardController = new AwardController();
        this.currentStudent = authController.getCurrentStudent();
        this.submissionId = submissionId;

        loadSubmission();
        initComponents();

        setTitle("Submission Details");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadSubmission() {
        this.submission = submissionController.getSubmissionById(submissionId);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Main Content Panel with Scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Submission Details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Submission Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Seminar seminar = seminarController.getSeminarById(submission.getSeminarId());
        Session session = submission.getSessionId() != null
                ? sessionController.getSessionById(submission.getSessionId())
                : null;
        TimeSlot timeSlot = timeSlotController.getTimeSlotBySubmission(submissionId);

        int row = 0;

        addDetailRow(detailsPanel, gbc, row++, "Title:", submission.getResearchTitle());
        addDetailRow(detailsPanel, gbc, row++, "Seminar:", seminar != null ? seminar.getTitle() : "N/A");
        addDetailRow(detailsPanel, gbc, row++, "Type:", submission.getPresentationType().toString());
        addDetailRow(detailsPanel, gbc, row++, "Status:", submission.getStatus().toString());
        addDetailRow(detailsPanel, gbc, row++, "Supervisor:", submission.getSupervisorName());
        addDetailRow(detailsPanel, gbc, row++, "Submitted:", dateFormat.format(submission.getSubmittedAt()));

        if (session != null) {
            addDetailRow(detailsPanel, gbc, row++, "Session:", "Session " + session.getSessionId());
        }

        if (timeSlot != null) {
            addDetailRow(detailsPanel, gbc, row++, "Time Slot:",
                    dateFormat.format(timeSlot.getStartTime()) + " - " +
                            dateFormat.format(timeSlot.getEndTime()));
        }

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        detailsPanel.add(new JLabel("Abstract:"), gbc);

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
        detailsPanel.add(abstractScroll, gbc);

        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Evaluations Panel
        JPanel evaluationsPanel = createEvaluationsPanel();
        mainPanel.add(evaluationsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Awards Panel
        JPanel awardsPanel = createAwardsPanel();
        mainPanel.add(awardsPanel);

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        add(mainScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        if (submission.getStatus() == seminar.models.enums.SubmissionStatus.PENDING) {
            JButton deleteButton = new JButton("Delete Submission");
            deleteButton.addActionListener(e -> handleDelete());
            buttonPanel.add(deleteButton);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
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

    private JPanel createEvaluationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Evaluations"));

        List<Evaluation> evaluations = evaluationController.getEvaluationsBySubmission(submissionId);

        if (!evaluations.isEmpty()) {
            JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            for (Evaluation eval : evaluations) {
                if (eval.getTotalScore() != null) { // Only show completed evaluations
                    JPanel evalPanel = new JPanel(new GridLayout(0, 1));
                    evalPanel.setBorder(BorderFactory.createEtchedBorder());

                    evalPanel.add(new JLabel("Problem Clarity: " + eval.getProblemClarityScore()));
                    evalPanel.add(new JLabel("Methodology: " + eval.getMethodologyScore()));
                    evalPanel.add(new JLabel("Results: " + eval.getResultsScore()));
                    evalPanel.add(new JLabel("Presentation: " + eval.getPresentationScore()));
                    evalPanel.add(new JLabel("Total Score: " + eval.getTotalScore()));
                    if (eval.getComments() != null && !eval.getComments().trim().isEmpty()) {
                        evalPanel.add(new JLabel("Comments: " + eval.getComments()));
                    }

                    listPanel.add(evalPanel);
                }
            }

            Double avgScore = evaluationController.getAverageScoreForSubmission(submissionId);
            if (avgScore != null) {
                JLabel avgLabel = new JLabel(String.format("Average Score: %.2f", avgScore));
                avgLabel.setFont(new Font("Arial", Font.BOLD, 14));
                panel.add(avgLabel, BorderLayout.NORTH);
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setPreferredSize(new Dimension(0, 150));
            panel.add(scrollPane, BorderLayout.CENTER);
        } else {
            panel.add(new JLabel("No evaluations yet", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createAwardsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Awards"));

        List<Award> awards = awardController.getAwardsBySubmission(submissionId);

        if (!awards.isEmpty()) {
            JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            for (Award award : awards) {
                JLabel awardLabel = new JLabel("🏆 " + award.getAwardType());
                awardLabel.setFont(new Font("Arial", Font.BOLD, 16));
                listPanel.add(awardLabel);
            }

            panel.add(listPanel, BorderLayout.CENTER);
        } else {
            panel.add(new JLabel("No awards yet", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        return panel;
    }

    private void handleDelete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this submission?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = submissionController.deleteSubmission(submissionId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Submission deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete submission",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}