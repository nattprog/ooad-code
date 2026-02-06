package seminar_manager.views.evaluator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class EvaluatorSubmissionDetailFrame extends JFrame {
  private AuthController authController;
  private SubmissionController submissionController;
  private SeminarController seminarController;
  private SessionController sessionController;
  private TimeSlotController timeSlotController;
  private AwardController awardController;
  private EvaluationController evaluationController;
  private EvaluatorAssignmentController evaluatorAssignmentController;

  private Evaluator currentEvaluator;
  private int submissionId;
  private Submission submission;

  public EvaluatorSubmissionDetailFrame(AuthController authController, int submissionId) {
    this.authController = authController;
    this.submissionController = new SubmissionController();
    this.seminarController = new SeminarController();
    this.sessionController = new SessionController();
    this.timeSlotController = new TimeSlotController();
    this.awardController = new AwardController();
    this.evaluationController = new EvaluationController();
    this.evaluatorAssignmentController = new EvaluatorAssignmentController();
    this.currentEvaluator = authController.getCurrentEvaluator();
    this.submissionId = submissionId;

    loadSubmission();
    initComponents();

    setTitle("Submission Details");
    setSize(700, 700);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void loadSubmission() {
    this.submission = submissionController.getSubmissionById(submissionId);
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Main Panel with Scroll
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Submission Details Panel
    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setBorder(BorderFactory.createTitledBorder("Submission Details"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 5, 5, 5);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Seminar seminar = seminarController.getSeminarById(submission.getSeminarId());
    Session session = submission.getSessionId() != null ? sessionController.getSessionById(submission.getSessionId())
        : null;
    TimeSlot timeSlot = timeSlotController.getTimeSlotBySubmission(submissionId);

    int row = 0;
    addDetailRow(detailsPanel, gbc, row++, "Title:", submission.getResearchTitle());
    addDetailRow(detailsPanel, gbc, row++, "Seminar:", seminar != null ? seminar.getTitle() : "N/A");
    addDetailRow(detailsPanel, gbc, row++, "Type:", submission.getPresentationType().toString());
    addDetailRow(detailsPanel, gbc, row++, "Status:", submission.getStatus().toString());
    addDetailRow(detailsPanel, gbc, row++, "Supervisor:", submission.getSupervisorName());

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

    // Awards Panel
    JPanel awardsPanel = createAwardsPanel();
    mainPanel.add(awardsPanel);

    JScrollPane mainScrollPane = new JScrollPane(mainPanel);
    add(mainScrollPane, BorderLayout.CENTER);

    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    // Find if evaluator has an evaluation for this submission
    Evaluation myEvaluation = findMyEvaluation();

    if (myEvaluation != null) {
      JButton evaluateButton = new JButton("Edit Evaluation");
      evaluateButton
          .addActionListener(e -> new EvaluatorEvaluationFormFrame(authController, myEvaluation.getEvaluationId()));
      buttonPanel.add(evaluateButton);
    }

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());
    buttonPanel.add(closeButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  private Evaluation findMyEvaluation() {
    // Get all evaluator assignments for this evaluator
    List<EvaluatorAssignment> myAssignments = evaluatorAssignmentController
        .getAssignmentsByEvaluatorUserId(currentEvaluator.getUserId());

    // Get evaluations for this submission
    List<Evaluation> evaluations = evaluationController.getEvaluationsBySubmission(submissionId);

    // Find evaluation that matches this evaluator's assignment
    for (Evaluation eval : evaluations) {
      for (EvaluatorAssignment assignment : myAssignments) {
        if (eval.getEvaluatorAssignmentId() == assignment.getEvaluatorAssignmentId()) {
          return eval;
        }
      }
    }

    return null;
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
      panel.add(new JLabel("No awards", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    return panel;
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