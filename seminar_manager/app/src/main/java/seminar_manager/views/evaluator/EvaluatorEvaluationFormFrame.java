package seminar_manager.views.evaluator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import javax.swing.*;
import java.awt.*;

public class EvaluatorEvaluationFormFrame extends JFrame {
  private AuthController authController;
  private EvaluationController evaluationController;
  private SubmissionController submissionController;

  private int evaluationId;
  private Evaluation evaluation;
  private Submission submission;

  private JSpinner problemClaritySpinner;
  private JSpinner methodologySpinner;
  private JSpinner resultsSpinner;
  private JSpinner presentationSpinner;
  private JLabel totalScoreLabel;
  private JTextArea commentsArea;

  public EvaluatorEvaluationFormFrame(AuthController authController, int evaluationId) {
    this.authController = authController;
    this.evaluationController = new EvaluationController();
    this.submissionController = new SubmissionController();
    this.evaluationId = evaluationId;

    loadEvaluation();
    initComponents();

    setTitle("Evaluation Form");
    setSize(600, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void loadEvaluation() {
    this.evaluation = evaluationController.getEvaluationById(evaluationId);
    this.submission = submissionController.getSubmissionById(evaluation.getSubmissionId());
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Title Panel
    JPanel titlePanel = new JPanel();
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JLabel titleLabel = new JLabel("Evaluation Form");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titlePanel.add(titleLabel);
    add(titlePanel, BorderLayout.NORTH);

    // Main Panel
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

    // Submission Info
    JPanel submissionPanel = new JPanel(new GridLayout(0, 1));
    submissionPanel.setBorder(BorderFactory.createTitledBorder("Submission"));
    submissionPanel.add(new JLabel("Title: " + submission.getResearchTitle()));
    submissionPanel.add(new JLabel("Type: " + submission.getPresentationType()));
    mainPanel.add(submissionPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    // Evaluation Criteria Panel
    JPanel criteriaPanel = new JPanel(new GridBagLayout());
    criteriaPanel.setBorder(BorderFactory.createTitledBorder("Evaluation Criteria (1-10)"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Problem Clarity Score
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    criteriaPanel.add(new JLabel("Problem Clarity (1-10):"), gbc);
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    problemClaritySpinner = new JSpinner(new SpinnerNumberModel(
        evaluation.getProblemClarityScore() != null ? evaluation.getProblemClarityScore() : 5,
        1, 10, 1));
    problemClaritySpinner.addChangeListener(e -> updateTotalScore());
    criteriaPanel.add(problemClaritySpinner, gbc);

    // Methodology Score
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    criteriaPanel.add(new JLabel("Methodology (1-10):"), gbc);
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    methodologySpinner = new JSpinner(new SpinnerNumberModel(
        evaluation.getMethodologyScore() != null ? evaluation.getMethodologyScore() : 5,
        1, 10, 1));
    methodologySpinner.addChangeListener(e -> updateTotalScore());
    criteriaPanel.add(methodologySpinner, gbc);

    // Results Score
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    criteriaPanel.add(new JLabel("Results (1-10):"), gbc);
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    resultsSpinner = new JSpinner(new SpinnerNumberModel(
        evaluation.getResultsScore() != null ? evaluation.getResultsScore() : 5,
        1, 10, 1));
    resultsSpinner.addChangeListener(e -> updateTotalScore());
    criteriaPanel.add(resultsSpinner, gbc);

    // Presentation Score
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 0;
    criteriaPanel.add(new JLabel("Presentation (1-10):"), gbc);
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    presentationSpinner = new JSpinner(new SpinnerNumberModel(
        evaluation.getPresentationScore() != null ? evaluation.getPresentationScore() : 5,
        1, 10, 1));
    presentationSpinner.addChangeListener(e -> updateTotalScore());
    criteriaPanel.add(presentationSpinner, gbc);

    // Total Score
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.weightx = 0;
    JLabel totalLabel = new JLabel("Total Score:");
    totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
    criteriaPanel.add(totalLabel, gbc);
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    totalScoreLabel = new JLabel("20");
    totalScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
    criteriaPanel.add(totalScoreLabel, gbc);

    mainPanel.add(criteriaPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    // Comments Panel
    JPanel commentsPanel = new JPanel(new BorderLayout());
    commentsPanel.setBorder(BorderFactory.createTitledBorder("Comments"));
    commentsArea = new JTextArea(5, 30);
    commentsArea.setLineWrap(true);
    commentsArea.setWrapStyleWord(true);
    if (evaluation.getComments() != null) {
      commentsArea.setText(evaluation.getComments());
    }
    JScrollPane commentsScroll = new JScrollPane(commentsArea);
    commentsPanel.add(commentsScroll, BorderLayout.CENTER);
    mainPanel.add(commentsPanel);

    // Initialize total score
    updateTotalScore();

    JScrollPane mainScroll = new JScrollPane(mainPanel);
    add(mainScroll, BorderLayout.CENTER);

    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    JButton saveButton = new JButton("Save Evaluation");
    saveButton.addActionListener(e -> handleSave());
    buttonPanel.add(saveButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void updateTotalScore() {
    int problemClarity = (Integer) problemClaritySpinner.getValue();
    int methodology = (Integer) methodologySpinner.getValue();
    int results = (Integer) resultsSpinner.getValue();
    int presentation = (Integer) presentationSpinner.getValue();

    int total = problemClarity + methodology + results + presentation;
    totalScoreLabel.setText(String.valueOf(total));
  }

  private void handleSave() {
    evaluation.setProblemClarityScore((Integer) problemClaritySpinner.getValue());
    evaluation.setMethodologyScore((Integer) methodologySpinner.getValue());
    evaluation.setResultsScore((Integer) resultsSpinner.getValue());
    evaluation.setPresentationScore((Integer) presentationSpinner.getValue());
    evaluation.setComments(commentsArea.getText().trim());

    boolean success = evaluationController.updateEvaluation(evaluation);

    if (success) {
      JOptionPane.showMessageDialog(this,
          "Evaluation saved successfully!",
          "Success",
          JOptionPane.INFORMATION_MESSAGE);
      dispose();
    } else {
      JOptionPane.showMessageDialog(this,
          "Failed to save evaluation",
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}