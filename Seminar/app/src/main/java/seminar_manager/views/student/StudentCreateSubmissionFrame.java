package seminar_manager.views.student;

import javax.swing.*;

import seminar_manager.controllers.*;
import seminar_manager.models.enums.PresentationType;

import java.awt.*;

public class StudentCreateSubmissionFrame extends JFrame {
    private AuthController authController;
    private SubmissionController submissionController;

    private int seminarId;
    private JTextField titleField;
    private JTextArea abstractArea;
    private JTextField supervisorField;
    private JComboBox<PresentationType> typeComboBox;
    private JTextField filePathField;
    private JButton browseButton;
    private JButton saveButton;
    private JButton cancelButton;

    public StudentCreateSubmissionFrame(AuthController authController, int seminarId) {
        this.authController = authController;
        this.submissionController = new SubmissionController();
        this.seminarId = seminarId;

        initComponents();

        setTitle("Create New Submission");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Create New Submission");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Research Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Research Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);

        // Research Abstract
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Abstract:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        abstractArea = new JTextArea(8, 30);
        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);
        JScrollPane abstractScroll = new JScrollPane(abstractArea);
        formPanel.add(abstractScroll, gbc);

        // Supervisor Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Supervisor:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        supervisorField = new JTextField(30);
        formPanel.add(supervisorField, gbc);

        // Presentation Type
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Presentation Type:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        typeComboBox = new JComboBox<>(PresentationType.values());
        formPanel.add(typeComboBox, gbc);

        // File Upload
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Presentation File:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        filePathField = new JTextField(20);
        filePathField.setEditable(false);
        browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> handleBrowse());
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);
        formPanel.add(filePanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        saveButton = new JButton("Save Submission");
        saveButton.addActionListener(e -> handleSave());
        buttonPanel.add(saveButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleBrowse() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            filePathField.setText(filePath);
        }
    }

    private void handleSave() {
        String title = titleField.getText().trim();
        String abstractText = abstractArea.getText().trim();
        String supervisor = supervisorField.getText().trim();
        PresentationType type = (PresentationType) typeComboBox.getSelectedItem();

        // Validation
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a research title",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (abstractText.length() < 100) {
            JOptionPane.showMessageDialog(this,
                    "Abstract must be at least 100 characters",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (supervisor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter supervisor name",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create submission
        boolean success = submissionController.createSubmission(
                seminarId,
                authController.getCurrentStudent().getUserId(),
                title,
                abstractText,
                supervisor,
                type);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Submission created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            // Navigate back to submissions
            new StudentSubmissionsFrame(authController);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create submission",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}