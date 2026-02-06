package seminar.views.student;

import seminar.controllers.*;
import seminar.models.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class StudentSeminarDetailFrame extends JFrame {
  private AuthController authController;
  private SeminarController seminarController;
  private SubmissionController submissionController;

  private Student currentStudent;
  private int seminarId;
  private Seminar seminar;

  public StudentSeminarDetailFrame(AuthController authController, int seminarId) {
    this.authController = authController;
    this.seminarController = new SeminarController();
    this.submissionController = new SubmissionController();
    this.currentStudent = authController.getCurrentStudent();
    this.seminarId = seminarId;

    loadSeminar();
    initComponents();

    setTitle("Seminar Details");
    setSize(600, 500);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void loadSeminar() {
    this.seminar = seminarController.getSeminarById(seminarId);
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Details Panel
    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setBorder(BorderFactory.createTitledBorder("Seminar Information"));
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

    add(detailsPanel, BorderLayout.CENTER);

    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    // Check if student already has submission for this seminar
    boolean hasSubmission = submissionController.hasSubmissionForSeminar(
        currentStudent.getUserId(), seminarId);

    if (!hasSubmission) {
      JButton createSubmissionButton = new JButton("Create Submission");
      createSubmissionButton.addActionListener(e -> {
        new StudentCreateSubmissionFrame(authController, seminarId);
        dispose();
      });
      buttonPanel.add(createSubmissionButton);
    } else {
      JLabel hasSubmissionLabel = new JLabel("You already have a submission for this seminar");
      hasSubmissionLabel.setForeground(Color.BLUE);
      buttonPanel.add(hasSubmissionLabel);
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
    gbc.weightx = 0;
    panel.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    panel.add(new JLabel(value), gbc);
  }
}