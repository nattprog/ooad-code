package seminar_manager.views.evaluator;

import seminar_manager.controllers.AuthController;
import seminar_manager.models.Evaluator;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class EvaluatorProfileFrame extends JFrame {
    private AuthController authController;
    private Evaluator currentEvaluator;

    public EvaluatorProfileFrame(AuthController authController) {
        this.authController = authController;
        this.currentEvaluator = authController.getCurrentEvaluator();

        initComponents();

        setTitle("My Profile");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Profile Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Details Panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int row = 0;
        addDetailRow(detailsPanel, gbc, row++, "Evaluator ID:", currentEvaluator.getEvaluatorId());
        addDetailRow(detailsPanel, gbc, row++, "Full Name:", currentEvaluator.getFullName());
        addDetailRow(detailsPanel, gbc, row++, "Username:", currentEvaluator.getUsername());
        addDetailRow(detailsPanel, gbc, row++, "Email:", currentEvaluator.getEmail());
        addDetailRow(detailsPanel, gbc, row++, "Role:", currentEvaluator.getRole().toString());
        addDetailRow(detailsPanel, gbc, row++, "Member Since:",
                dateFormat.format(currentEvaluator.getCreatedAt()));

        add(detailsPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
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
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(valueComponent, gbc);
    }
}