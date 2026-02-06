package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import seminar_manager.database.dao.UserDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class CoordinatorSessionDetailFrame extends JFrame {
    private AuthController authController;
    private SessionController sessionController;
    private SeminarController seminarController;
    private TimeSlotController timeSlotController;
    private EvaluatorAssignmentController evaluatorAssignmentController;
    private UserDAO userDAO;

    private int sessionId;
    private Session session;
    private JTable timeSlotsTable;
    private DefaultTableModel timeSlotsTableModel;
    private JTable evaluatorsTable;
    private DefaultTableModel evaluatorsTableModel;

    public CoordinatorSessionDetailFrame(AuthController authController, int sessionId) {
        this.authController = authController;
        this.sessionController = new SessionController();
        this.seminarController = new SeminarController();
        this.timeSlotController = new TimeSlotController();
        this.evaluatorAssignmentController = new EvaluatorAssignmentController();
        this.userDAO = new UserDAO();
        this.sessionId = sessionId;

        loadSession();
        initComponents();

        setTitle("Session Details");
        setSize(1000, 800);
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

        // Top: Session Info
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Center: Split panel for time slots and evaluators
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Time Slots Panel
        JPanel timeSlotsPanel = createTimeSlotsPanel();
        splitPane.setTopComponent(timeSlotsPanel);

        // Evaluators Panel
        JPanel evaluatorsPanel = createEvaluatorsPanel();
        splitPane.setBottomComponent(evaluatorsPanel);

        splitPane.setDividerLocation(400);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addEvaluatorButton = new JButton("Add Evaluator");
        addEvaluatorButton.addActionListener(e -> handleAddEvaluator());
        buttonPanel.add(addEvaluatorButton);

        JButton deleteSessionButton = new JButton("Delete Session");
        deleteSessionButton.setForeground(Color.RED);
        deleteSessionButton.addActionListener(e -> handleDeleteSession());
        buttonPanel.add(deleteSessionButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            new CoordinatorSeminarDetailFrame(authController, session.getSeminarId());
            dispose();
        });
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Session Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Seminar seminar = seminarController.getSeminarById(session.getSeminarId());

        int row = 0;
        addDetailRow(panel, gbc, row++, "Seminar:", seminar != null ? seminar.getTitle() : "N/A");
        addDetailRow(panel, gbc, row++, "Type:", session.getPresentationType().toString());
        addDetailRow(panel, gbc, row++, "Start Time:", dateFormat.format(session.getStartTime()));
        addDetailRow(panel, gbc, row++, "End Time:", dateFormat.format(session.getEndTime()));
        addDetailRow(panel, gbc, row++, "Time Slots:", String.valueOf(session.getTimeSlotsCount()));
        addDetailRow(panel, gbc, row++, "Slot Duration:", session.getTimeSlotsDuration() + " minutes");

        return panel;
    }

    private JPanel createTimeSlotsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Time Slots"));

        String[] columnNames = { "Time Slot ID", "Start Time", "End Time", "Assigned Submission" };
        timeSlotsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timeSlotsTable = new JTable(timeSlotsTableModel);
        timeSlotsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadTimeSlots();

        JScrollPane scrollPane = new JScrollPane(timeSlotsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewTimeSlotButton = new JButton("View/Edit Time Slot");
        viewTimeSlotButton.addActionListener(e -> viewSelectedTimeSlot());
        buttonPanel.add(viewTimeSlotButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Double-click to view
        timeSlotsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewSelectedTimeSlot();
                }
            }
        });

        return panel;
    }

    private JPanel createEvaluatorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Assigned Evaluators"));

        String[] columnNames = { "Assignment ID", "Evaluator ID", "Evaluator Name", "Email" };
        evaluatorsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        evaluatorsTable = new JTable(evaluatorsTableModel);
        evaluatorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadEvaluators();

        JScrollPane scrollPane = new JScrollPane(evaluatorsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Remove Evaluator");
        removeButton.addActionListener(e -> handleRemoveEvaluator());
        buttonPanel.add(removeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadTimeSlots() {
        timeSlotsTableModel.setRowCount(0);

        List<TimeSlot> timeSlots = timeSlotController.getTimeSlotsBySession(sessionId);
        Collections.sort(timeSlots, Comparator.comparing(TimeSlot::getStartTime));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SubmissionController submissionController = new SubmissionController();

        for (TimeSlot timeSlot : timeSlots) {
            String submissionInfo = "Not assigned";
            if (timeSlot.getSubmissionId() != null) {
                Submission submission = submissionController.getSubmissionById(timeSlot.getSubmissionId());
                if (submission != null) {
                    submissionInfo = submission.getResearchTitle();
                }
            }

            Object[] row = {
                    timeSlot.getTimeSlotId(),
                    timeFormat.format(timeSlot.getStartTime()),
                    timeFormat.format(timeSlot.getEndTime()),
                    submissionInfo
            };
            timeSlotsTableModel.addRow(row);
        }
    }

    private void loadEvaluators() {
        evaluatorsTableModel.setRowCount(0);

        List<EvaluatorAssignment> assignments = evaluatorAssignmentController
                .getAssignmentsBySession(sessionId);

        for (EvaluatorAssignment assignment : assignments) {
            User evaluatorUser = userDAO.getUserById(assignment.getEvaluatorId());

            if (evaluatorUser != null) {
                Object[] row = {
                        assignment.getEvaluatorAssignmentId(),
                        assignment.getEvaluatorId(),
                        evaluatorUser.getFullName(),
                        evaluatorUser.getEmail()
                };
                evaluatorsTableModel.addRow(row);
            }
        }
    }

    private void viewSelectedTimeSlot() {
        int selectedRow = timeSlotsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a time slot to view",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int timeSlotId = (int) timeSlotsTableModel.getValueAt(selectedRow, 0);
        new CoordinatorTimeSlotDetailFrame(authController, timeSlotId, sessionId);
        dispose();
    }

    private void handleAddEvaluator() {
        new CoordinatorAddEvaluatorDialog(this, authController, sessionId);
        loadEvaluators(); // Refresh after adding
    }

    private void handleRemoveEvaluator() {
        int selectedRow = evaluatorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an evaluator to remove",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this evaluator?\n" +
                        "This will also delete all their evaluations for this session.",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int assignmentId = (int) evaluatorsTableModel.getValueAt(selectedRow, 0);
            boolean success = evaluatorAssignmentController.deleteEvaluatorAssignment(assignmentId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Evaluator removed successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadEvaluators();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to remove evaluator",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteSession() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this session?\n" +
                        "This will also delete all time slots and evaluator assignments.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = sessionController.deleteSession(sessionId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Session deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new CoordinatorSeminarDetailFrame(authController, session.getSeminarId());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete session",
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