package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.enums.PresentationType;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;

public class CoordinatorCreateSessionFrame extends JFrame {
    private AuthController authController;
    private SessionController sessionController;

    private int seminarId;
    private JComboBox<PresentationType> typeComboBox;
    private JSpinner slotsCountSpinner;
    private JComboBox<Integer> slotDurationCombo;
    private JDateChooser startDateChooser;
    private JSpinner startTimeSpinner;
    private JDateChooser endDateChooser;
    private JSpinner endTimeSpinner;

    public CoordinatorCreateSessionFrame(AuthController authController, int seminarId) {
        this.authController = authController;
        this.sessionController = new SessionController();
        this.seminarId = seminarId;

        initComponents();

        setTitle("Create New Session");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Create New Session");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Presentation Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Presentation Type:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        typeComboBox = new JComboBox<>(PresentationType.values());
        formPanel.add(typeComboBox, gbc);

        // Time Slots Count
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Number of Time Slots:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        slotsCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        formPanel.add(slotsCountSpinner, gbc);

        // Time Slot Duration
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Time Slot Duration (min):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        slotDurationCombo = new JComboBox<>(new Integer[] { 15, 30, 45, 60 });
        slotDurationCombo.setSelectedItem(30);
        formPanel.add(slotDurationCombo, gbc);

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

        JButton saveButton = new JButton("Save Session");
        saveButton.addActionListener(e -> handleSave());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            new CoordinatorSeminarDetailFrame(authController, seminarId);
            dispose();
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSave() {
        PresentationType type = (PresentationType) typeComboBox.getSelectedItem();
        int slotsCount = (Integer) slotsCountSpinner.getValue();
        int slotDuration = (Integer) slotDurationCombo.getSelectedItem();

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

        // Create session (time slots will be auto-generated)
        boolean success = sessionController.createSession(seminarId, type, slotsCount,
                slotDuration, startTime, endTime);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Session created successfully!\nTime slots have been auto-generated.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new CoordinatorSeminarDetailFrame(authController, seminarId);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create session",
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