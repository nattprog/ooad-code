package seminar_manager.views;

import javax.swing.*;

import seminar_manager.controllers.AuthController;
import seminar_manager.models.enums.UserRole;

import java.awt.*;

public class RegisterFrame extends JFrame {
    private AuthController authController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JComboBox<UserRole> roleComboBox;
    private JTextField roleIdField;
    private JButton registerButton;
    private JButton backToLoginButton;

    public RegisterFrame() {
        this.authController = new AuthController();
        initComponents();
        setTitle("Register - Seminar Management System");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roleComboBox = new JComboBox<>(UserRole.values());
        roleComboBox.addActionListener(e -> updateRoleIdLabel());
        formPanel.add(roleComboBox, gbc);

        // Role ID
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        JLabel roleIdLabel = new JLabel("Student ID:");
        formPanel.add(roleIdLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roleIdField = new JTextField(20);
        formPanel.add(roleIdField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> handleRegister());
        buttonPanel.add(registerButton);

        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setPreferredSize(new Dimension(120, 35));
        backToLoginButton.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });
        buttonPanel.add(backToLoginButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateRoleIdLabel() {
        UserRole selectedRole = (UserRole) roleComboBox.getSelectedItem();
        String labelText = "";
        switch (selectedRole) {
            case STUDENT:
                labelText = "Student ID:";
                break;
            case EVALUATOR:
                labelText = "Evaluator ID:";
                break;
            case COORDINATOR:
                labelText = "Coordinator ID:";
                break;
        }

        // Update the label in the form panel
        Container parent = roleIdField.getParent();
        for (Component comp : parent.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().contains("ID:")) {
                    label.setText(labelText);
                    break;
                }
            }
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        UserRole role = (UserRole) roleComboBox.getSelectedItem();
        String roleId = roleIdField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() ||
                email.isEmpty() || roleId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Register
        boolean success = authController.register(username, password, fullName, email, role, roleId);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Username may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}