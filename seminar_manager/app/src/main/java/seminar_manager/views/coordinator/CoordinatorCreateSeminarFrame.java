package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;

public class CoordinatorCreateSeminarFrame extends JFrame {
    private AuthController authController;
    private SeminarController seminarController;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField locationField;
    private JDateChooser startDateChooser;
    private JSpinner startTimeSpinner;
    private JDateChooser endDateChooser;
    private JSpinner endTimeSpinner;

    public CoordinatorCreateSeminarFrame(AuthController authController) {
        this.authController = authController;
        this.seminarController = new SeminarController();

        initComponents();

        setTitle("Create New Seminar");
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
        JLabel titleLabel = new JLabel("Create New Seminar");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Seminar Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        locationField = new JTextField(30);
        formPanel.add(locationField, gbc);

        // Start Date and Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Start Date:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        startDateChooser.setMinSelectableDate(new Date());
        formPanel.add(startDateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Start Time:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startTimeModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        formPanel.add(startTimeSpinner, gbc);

        // End Date and Time
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        formPanel.add(new JLabel("End Date:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.setMinSelectableDate(new Date());
        formPanel.add(endDateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        formPanel.add(new JLabel("End Time:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endTimeModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        formPanel.add(endTimeSpinner, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Save Seminar");
        saveButton.addActionListener(e -> handleSave());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            new CoordinatorSeminarsFrame(authController);
            dispose();
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSave() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String location = locationField.getText().trim();

        // Validation
        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select start and end dates",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Combine date and time
        Date startTime = combineDateTime(startDate, (Date) startTimeSpinner.getValue());
        Date endTime = combineDateTime(endDate, (Date) endTimeSpinner.getValue());

        if (endTime.before(startTime)) {
            JOptionPane.showMessageDialog(this,
                    "End time must be after start time",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create seminar
        boolean success = seminarController.createSeminar(title, description, location,
                startTime, endTime);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Seminar created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new CoordinatorSeminarsFrame(authController);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create seminar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Date combineDateTime(Date date, Date time) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);

        return dateCal.getTime();
    }
}