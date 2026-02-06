package seminar_manager.views.student;

import javax.swing.*;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import seminar_manager.views.LoginFrame;

import java.awt.*;
import java.util.List;

public class StudentHomeFrame extends JFrame {
  private AuthController authController;
  private SeminarController seminarController;
  private SubmissionController submissionController;
  private TimeSlotController timeSlotController;

  private Student currentStudent;
  private JPanel assignedPresentationPanel;
  private JPanel upcomingSeminarsPanel;

  public StudentHomeFrame(AuthController authController) {
    this.authController = authController;
    this.seminarController = new SeminarController();
    this.submissionController = new SubmissionController();
    this.timeSlotController = new TimeSlotController();
    this.currentStudent = authController.getCurrentStudent();

    initComponents();
    loadData();

    setTitle("Student Home - Seminar Management System");
    setSize(900, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Top Panel - Title and User Info
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel titleLabel = new JLabel("Student Dashboard");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    topPanel.add(titleLabel, BorderLayout.WEST);

    JLabel userLabel = new JLabel("Welcome, " + currentStudent.getFullName());
    userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    topPanel.add(userLabel, BorderLayout.EAST);

    add(topPanel, BorderLayout.NORTH);

    // Main Content Panel
    JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Assigned Presentation Panel
    assignedPresentationPanel = new JPanel(new BorderLayout());
    assignedPresentationPanel.setBorder(BorderFactory.createTitledBorder("My Assigned Presentation"));
    mainPanel.add(assignedPresentationPanel);

    // Upcoming Seminars Panel
    upcomingSeminarsPanel = new JPanel(new BorderLayout());
    upcomingSeminarsPanel.setBorder(BorderFactory.createTitledBorder("Upcoming Seminars"));
    mainPanel.add(upcomingSeminarsPanel);

    add(mainPanel, BorderLayout.CENTER);

    // Navigation Panel
    JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    JButton submissionsButton = new JButton("My Submissions");
    submissionsButton.addActionListener(e -> new StudentSubmissionsFrame(authController));
    navPanel.add(submissionsButton);

    JButton seminarsButton = new JButton("All Seminars");
    seminarsButton.addActionListener(e -> new StudentSeminarsFrame(authController));
    navPanel.add(seminarsButton);

    JButton profileButton = new JButton("Profile");
    profileButton.addActionListener(e -> new StudentProfileFrame(authController));
    navPanel.add(profileButton);

    JButton logoutButton = new JButton("Logout");
    logoutButton.addActionListener(e -> {
      authController.logout();
      new LoginFrame();
      dispose();
    });
    navPanel.add(logoutButton);

    add(navPanel, BorderLayout.SOUTH);
  }

  private void loadData() {
    loadAssignedPresentation();
    loadUpcomingSeminars();
  }

  private void loadAssignedPresentation() {
    assignedPresentationPanel.removeAll();

    List<Submission> submissions = submissionController.getSubmissionsByStudentUserId(
        currentStudent.getUserId());

    // Find submission with assigned session and timeslot
    Submission assignedSubmission = null;
    TimeSlot assignedTimeSlot = null;

    for (Submission sub : submissions) {
      if (sub.getSessionId() != null) {
        TimeSlot ts = timeSlotController.getTimeSlotBySubmission(sub.getSubmissionId());
        if (ts != null) {
          assignedSubmission = sub;
          assignedTimeSlot = ts;
          break;
        }
      }
    }

    if (assignedSubmission != null && assignedTimeSlot != null) {
      JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
      detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      detailsPanel.add(new JLabel("Title: " + assignedSubmission.getResearchTitle()));
      detailsPanel.add(new JLabel("Type: " + assignedSubmission.getPresentationType()));
      detailsPanel.add(new JLabel("Time: " + assignedTimeSlot.getStartTime() +
          " - " + assignedTimeSlot.getEndTime()));

      JButton viewButton = new JButton("View Details");
      final Submission finalSub = assignedSubmission;
      viewButton.addActionListener(e -> new StudentSubmissionDetailFrame(authController, finalSub.getSubmissionId()));
      detailsPanel.add(viewButton);

      assignedPresentationPanel.add(detailsPanel, BorderLayout.CENTER);
    } else {
      JLabel noAssignmentLabel = new JLabel("No assigned presentation yet", SwingConstants.CENTER);
      assignedPresentationPanel.add(noAssignmentLabel, BorderLayout.CENTER);
    }

    assignedPresentationPanel.revalidate();
    assignedPresentationPanel.repaint();
  }

  private void loadUpcomingSeminars() {
    upcomingSeminarsPanel.removeAll();

    List<Seminar> seminars = seminarController.getUpcomingSeminars(3);

    if (!seminars.isEmpty()) {
      JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));
      listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      for (Seminar seminar : seminars) {
        JPanel seminarPanel = new JPanel(new BorderLayout(5, 5));
        seminarPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.add(new JLabel(seminar.getTitle()));
        infoPanel.add(new JLabel(seminar.getStartTime().toString()));

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(e -> new StudentSeminarDetailFrame(authController, seminar.getSeminarId()));

        seminarPanel.add(infoPanel, BorderLayout.CENTER);
        seminarPanel.add(viewButton, BorderLayout.EAST);

        listPanel.add(seminarPanel);
      }

      JScrollPane scrollPane = new JScrollPane(listPanel);
      upcomingSeminarsPanel.add(scrollPane, BorderLayout.CENTER);

      JButton viewAllButton = new JButton("View All Seminars");
      viewAllButton.addActionListener(e -> new StudentSeminarsFrame(authController));
      upcomingSeminarsPanel.add(viewAllButton, BorderLayout.SOUTH);
    } else {
      JLabel noSeminarsLabel = new JLabel("No upcoming seminars", SwingConstants.CENTER);
      upcomingSeminarsPanel.add(noSeminarsLabel, BorderLayout.CENTER);
    }

    upcomingSeminarsPanel.revalidate();
    upcomingSeminarsPanel.repaint();
  }
}