package seminar.views.student;

import seminar.controllers.*;
import seminar.models.*;
import seminar.models.enums.SubmissionStatus;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class StudentSubmissionsFrame extends JFrame {
  private AuthController authController;
  private SubmissionController submissionController;

  private Student currentStudent;
  private JTable submissionsTable;
  private DefaultTableModel tableModel;
  private JComboBox<String> statusFilter;

  public StudentSubmissionsFrame(AuthController authController) {
    this.authController = authController;
    this.submissionController = new SubmissionController();
    this.currentStudent = authController.getCurrentStudent();

    initComponents();
    loadSubmissions(null);

    setTitle("My Submissions");
    setSize(900, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Title Panel
    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel titleLabel = new JLabel("My Submissions");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titlePanel.add(titleLabel, BorderLayout.WEST);

    // Filter Panel
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    filterPanel.add(new JLabel("Filter by Status:"));
    statusFilter = new JComboBox<>(new String[] { "All", "PENDING", "APPROVED", "REJECTED" });
    statusFilter.addActionListener(e -> {
      String selected = (String) statusFilter.getSelectedItem();
      SubmissionStatus status = selected.equals("All") ? null : SubmissionStatus.valueOf(selected);
      loadSubmissions(status);
    });
    filterPanel.add(statusFilter);

    titlePanel.add(filterPanel, BorderLayout.EAST);
    add(titlePanel, BorderLayout.NORTH);

    // Table Panel
    String[] columnNames = { "ID", "Title", "Seminar", "Type", "Status", "Submitted Date" };
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    submissionsTable = new JTable(tableModel);
    submissionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(submissionsTable);
    add(scrollPane, BorderLayout.CENTER);

    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    JButton viewButton = new JButton("View Details");
    viewButton.addActionListener(e -> viewSelectedSubmission());
    buttonPanel.add(viewButton);

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

  private void loadSubmissions(SubmissionStatus statusFilter) {
    tableModel.setRowCount(0);

    List<Submission> submissions = submissionController.getSubmissionsByStudentUserId(
        currentStudent.getUserId());

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SeminarController seminarController = new SeminarController();

    for (Submission submission : submissions) {
      if (statusFilter == null || submission.getStatus() == statusFilter) {
        Seminar seminar = seminarController.getSeminarById(submission.getSeminarId());
        String seminarTitle = seminar != null ? seminar.getTitle() : "N/A";

        Object[] row = {
            submission.getSubmissionId(),
            submission.getResearchTitle(),
            seminarTitle,
            submission.getPresentationType(),
            submission.getStatus(),
            dateFormat.format(submission.getSubmittedAt())
        };
        tableModel.addRow(row);
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
    new StudentSubmissionDetailFrame(authController, submissionId);
  }
}