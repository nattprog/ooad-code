package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import seminar_manager.models.enums.UserRole;
import seminar_manager.database.dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CoordinatorAddEvaluatorDialog extends JDialog {
    private AuthController authController;
    private EvaluatorAssignmentController evaluatorAssignmentController;
    private UserDAO userDAO;

    private int sessionId;
    private JList<String> evaluatorList;
    private DefaultListModel<String> listModel;
    private List<User> availableEvaluators;

    public CoordinatorAddEvaluatorDialog(JFrame parent, AuthController authController, int sessionId) {
        super(parent, "Add Evaluator to Session", true);
        this.authController = authController;
        this.evaluatorAssignmentController = new EvaluatorAssignmentController();
        this.userDAO = new seminar_manager.database.dao.UserDAO();
        this.sessionId = sessionId;

        initComponents();
        loadEvaluators();

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Select Evaluator to Add");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // List Panel
        listModel = new DefaultListModel<>();
        evaluatorList = new JList<>(listModel);
        evaluatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(evaluatorList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> handleAdd());
        buttonPanel.add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEvaluators() {
        availableEvaluators = userDAO.getUsersByRole(UserRole.EVALUATOR);

        // Filter out already assigned evaluators
        List<EvaluatorAssignment> existingAssignments = evaluatorAssignmentController
                .getAssignmentsBySession(sessionId);

        for (User evaluator : availableEvaluators) {
            boolean alreadyAssigned = false;
            for (EvaluatorAssignment assignment : existingAssignments) {
                if (assignment.getEvaluatorId() == evaluator.getUserId()) {
                    alreadyAssigned = true;
                    break;
                }
            }

            if (!alreadyAssigned) {
                listModel.addElement(evaluator.getUserId() + " - " + evaluator.getFullName() +
                        " (" + evaluator.getEmail() + ")");
            }
        }

        if (listModel.isEmpty()) {
            listModel.addElement("No available evaluators");
        }
    }

    private void handleAdd() {
        int selectedIndex = evaluatorList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an evaluator to add",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (listModel.getElementAt(selectedIndex).equals("No available evaluators")) {
            return;
        }

        // Get evaluator user ID from selection
        String selectedItem = listModel.getElementAt(selectedIndex);
        int evaluatorUserId = Integer.parseInt(selectedItem.split(" - ")[0]);

        // Create evaluator assignment (will also auto-create evaluations)
        boolean success = evaluatorAssignmentController.createEvaluatorAssignment(
                sessionId, evaluatorUserId);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Evaluator added successfully!\n" +
                            "Evaluations have been auto-created for all submissions in this session.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to add evaluator",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}