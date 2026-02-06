package seminar_manager.views;

import javax.swing.*;

import seminar_manager.controllers.AuthController;
import seminar_manager.models.User;
import seminar_manager.models.enums.UserRole;
// import seminar_manager.views.coordinator.CoordinatorHomeFrame;  // TODO: implement
// import seminar_manager.views.evaluator.EvaluatorHomeFrame;  // TODO: implement
import seminar_manager.views.student.StudentHomeFrame;

import java.awt.*;

public class LoginFrame extends JFrame {
    private AuthController authController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        this.authController = new AuthController();
        initComponents();
        setTitle("Login - Seminar Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Seminar Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

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

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(e -> handleLogin());
        buttonPanel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 35));
        registerButton.addActionListener(e -> {
            new RegisterFrame();
            dispose();
        });
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Enter key support
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authController.login(username, password);

        if (user != null) {
            // Navigate to appropriate home page based on role
            switch (user.getRole()) {
                case STUDENT:
                    new StudentHomeFrame(authController);
                    break;
                case EVALUATOR:
                    // new EvaluatorHomeFrame(authController); // TODO: implement
                    break;
                case COORDINATOR:
                    // new CoordinatorHomeFrame(authController); // TODO: implement
                    break;
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}