package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoordinatorSubmissionsFrame extends JFrame {
    private AuthController authController;
    private SubmissionController submissionController;

    private int seminarId;
    private JTable submissionsTable;
    private DefaultTableModel tableModel;

    public CoordinatorSubmissionsFrame(AuthController authController, int seminarId) {
        this.authController = authController;
        this.submissionController = new SubmissionController();
        this.seminarId = seminarId;

        initComponents();
        loadSubmissions();

        setTitle("Submissions for Seminar");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Submissions for Seminar");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Table Panel
        String[] columnNames = { "ID", "Title", "Type", "Status", "Student", "Session" };
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

    private void loadSubmissions() {
        tableModel.setRowCount(0);

        List<Submission> submissions = submissionController.getSubmissionsBySeminar(seminarId);
        seminar_manager.database.dao.UserDAO userDAO = new seminar_manager.database.dao.UserDAO();

        for (Submission submission : submissions) {
            User student = userDAO.getUserById(submission.getStudentId());
            String studentName = student != null ? student.getFullName() : "Unknown";

            String sessionInfo = submission.getSessionId() != null ? "Session " + submission.getSessionId()
                    : "Not assigned";

            Object[] row = {
                    submission.getSubmissionId(),
                    submission.getResearchTitle(),
                    submission.getPresentationType(),
                    submission.getStatus(),
                    studentName,
                    sessionInfo
            };
            tableModel.addRow(row);
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
        new CoordinatorSubmissionDetailFrame(authController, submissionId, seminarId);
    }
}