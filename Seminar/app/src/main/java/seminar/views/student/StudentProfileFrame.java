package seminar.views.student;

import seminar.controllers.AuthController;
import seminar.models.Student;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class StudentProfileFrame extends JFrame {
    private AuthController authController;
    private Student currentStudent;

    public StudentProfileFrame(AuthController authController) {
        this.authController = authController;
        this.currentStudent = authController.getCurrentStudent();

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
        addDetailRow(detailsPanel, gbc, row++, "Student ID:", currentStudent.getStudentId());
        addDetailRow(detailsPanel, gbc, row++, "Full Name:", currentStudent.getFullName());
        addDetailRow(detailsPanel, gbc, row++, "Username:", currentStudent.getUsername());
        addDetailRow(detailsPanel, gbc, row++, "Email:", currentStudent.getEmail());
        addDetailRow(detailsPanel, gbc, row++, "Role:", currentStudent.getRole().toString());
        addDetailRow(detailsPanel, gbc, row++, "Member Since:",
                dateFormat.format(currentStudent.getCreatedAt()));

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