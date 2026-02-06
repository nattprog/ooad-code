package seminar_manager.views.evaluator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class EvaluatorSessionDetailFrame extends JFrame {
  private AuthController authController;
  private SessionController sessionController;
  private SeminarController seminarController;
  private SubmissionController submissionController;
  private TimeSlotController timeSlotController;

  private int sessionId;
  private Session session;
  private JTable submissionsTable;
  private DefaultTableModel tableModel;

  public EvaluatorSessionDetailFrame(AuthController authController, int sessionId) {
    this.authController = authController;
    this.sessionController = new SessionController();
    this.seminarController = new SeminarController();
    this.submissionController = new SubmissionController();
    this.timeSlotController = new TimeSlotController();
    this.sessionId = sessionId;

    loadSession();
    initComponents();

    setTitle("Session Details");
    setSize(800, 700);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void loadSession() {
    this.session = sessionController.getSessionById(sessionId);
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Title Panel
    JPanel titlePanel = new JPanel();
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JLabel titleLabel = new JLabel("Session Details");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titlePanel.add(titleLabel);
    add(titlePanel, BorderLayout.NORTH);

    // Main Panel
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Session Details Panel
    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setBorder(BorderFactory.createTitledBorder("Session Information"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 5, 5, 5);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Seminar seminar = seminarController.getSeminarById(session.getSeminarId());

    int row = 0;
    addDetailRow(detailsPanel, gbc, row++, "Seminar:", seminar != null ? seminar.getTitle() : "N/A");
    addDetailRow(detailsPanel, gbc, row++, "Type:", session.getPresentationType().toString());
    addDetailRow(detailsPanel, gbc, row++, "Start Time:", dateFormat.format(session.getStartTime()));
    addDetailRow(detailsPanel, gbc, row++, "End Time:", dateFormat.format(session.getEndTime()));
    addDetailRow(detailsPanel, gbc, row++, "Time Slots:", String.valueOf(session.getTimeSlotsCount()));
    addDetailRow(detailsPanel, gbc, row++, "Slot Duration:", session.getTimeSlotsDuration() + " minutes");

    mainPanel.add(detailsPanel, BorderLayout.NORTH);

    // Submissions Table
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.setBorder(BorderFactory.createTitledBorder("Submissions"));

    String[] columnNames = { "ID", "Title", "Time Slot", "Status" };
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    submissionsTable = new JTable(tableModel);
    submissionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    loadSubmissions();

    JScrollPane scrollPane = new JScrollPane(submissionsTable);
    tablePanel.add(scrollPane, BorderLayout.CENTER);

    mainPanel.add(tablePanel, BorderLayout.CENTER);

    add(mainPanel, BorderLayout.CENTER);

    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    JButton viewSubmissionButton = new JButton("View Submission");
    viewSubmissionButton.addActionListener(e -> viewSelectedSubmission());
    buttonPanel.add(viewSubmissionButton);

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());
    buttonPanel.add(closeButton);

    add(buttonPanel, BorderLayout.SOUTH);

    // Double-click to view
    submissionsTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
          viewSelectedSubmission();
        }
      }
    });
  }

  private void loadSubmissions() {
    tableModel.setRowCount(0);

    List<TimeSlot> timeSlots = timeSlotController.getTimeSlotsBySession(sessionId);
    Collections.sort(timeSlots, Comparator.comparing(TimeSlot::getStartTime));

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    for (TimeSlot timeSlot : timeSlots) {
      if (timeSlot.getSubmissionId() != null) {
        Submission submission = submissionController.getSubmissionById(
            timeSlot.getSubmissionId());

        if (submission != null) {
          String timeSlotStr = timeFormat.format(timeSlot.getStartTime()) + " - " +
              timeFormat.format(timeSlot.getEndTime());

          Object[] row = {
              submission.getSubmissionId(),
              submission.getResearchTitle(),
              timeSlotStr,
              submission.getStatus()
          };
          tableModel.addRow(row);
        }
      }
    }
  }

  private void viewSelectedSubmission() {
    int selectedRow = submissionsTable.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this,
          "Please select a submission to view",
          "No Selection",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    int submissionId = (int) tableModel.getValueAt(selectedRow, 0);
    new EvaluatorSubmissionDetailFrame(authController, submissionId);
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