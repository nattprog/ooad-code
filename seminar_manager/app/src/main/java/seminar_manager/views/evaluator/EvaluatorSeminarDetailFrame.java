package seminar_manager.views.evaluator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class EvaluatorSeminarDetailFrame extends JFrame {
  private AuthController authController;
  private SeminarController seminarController;
  private SessionController sessionController;
  private EvaluatorAssignmentController evaluatorAssignmentController;

  private Evaluator currentEvaluator;
  private int seminarId;
  private Seminar seminar;

  public EvaluatorSeminarDetailFrame(AuthController authController, int seminarId) {
    this.authController = authController;
    this.seminarController = new SeminarController();
    this.sessionController = new SessionController();
    this.evaluatorAssignmentController = new EvaluatorAssignmentController();
    this.currentEvaluator = authController.getCurrentEvaluator();
    this.seminarId = seminarId;

    loadSeminar();
    initComponents();

    setTitle("Seminar Details");
    setSize(700, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void loadSeminar() {
    this.seminar = seminarController.getSeminarById(seminarId);
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Title Panel
    JPanel titlePanel = new JPanel();
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JLabel titleLabel = new JLabel("Seminar Details");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titlePanel.add(titleLabel);
    add(titlePanel, BorderLayout.NORTH);

    // Main Panel
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    // Seminar Details
    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setBorder(BorderFactory.createTitledBorder("Seminar Information"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 5, 5, 5);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    int row = 0;
    addDetailRow(detailsPanel, gbc, row++, "Title:", seminar.getTitle());
    addDetailRow(detailsPanel, gbc, row++, "Description:", seminar.getDescription());
    addDetailRow(detailsPanel, gbc, row++, "Location:", seminar.getLocation());
    addDetailRow(detailsPanel, gbc, row++, "Start Time:", dateFormat.format(seminar.getStartTime()));
    addDetailRow(detailsPanel, gbc, row++, "End Time:", dateFormat.format(seminar.getEndTime()));

    mainPanel.add(detailsPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // Sessions Panel
    JPanel sessionsPanel = createSessionsPanel();
    mainPanel.add(sessionsPanel);

    JScrollPane scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());
    buttonPanel.add(closeButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  private JPanel createSessionsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("My Assigned Sessions"));

    List<Session> allSessions = sessionController.getSessionsBySeminar(seminarId);
    List<EvaluatorAssignment> myAssignments = evaluatorAssignmentController
        .getAssignmentsByEvaluatorUserId(currentEvaluator.getUserId());

    // Filter sessions where evaluator is assigned
    JPanel listPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    boolean hasAssignedSessions = false;

    for (Session session : allSessions) {
      for (EvaluatorAssignment assignment : myAssignments) {
        if (assignment.getSessionId() == session.getSessionId()) {
          hasAssignedSessions = true;

          JPanel sessionPanel = new JPanel(new BorderLayout(5, 5));
          sessionPanel.setBorder(BorderFactory.createEtchedBorder());

          JPanel infoPanel = new JPanel(new GridLayout(0, 1));
          infoPanel.add(new JLabel("Type: " + session.getPresentationType()));
          infoPanel.add(new JLabel("Time: " + session.getStartTime()));

          JButton viewButton = new JButton("View");
          viewButton.addActionListener(e -> new EvaluatorSessionDetailFrame(authController, session.getSessionId()));

          sessionPanel.add(infoPanel, BorderLayout.CENTER);
          sessionPanel.add(viewButton, BorderLayout.EAST);

          listPanel.add(sessionPanel);
          break;
        }
      }
    }

    if (hasAssignedSessions) {
      JScrollPane scrollPane = new JScrollPane(listPanel);
      scrollPane.setPreferredSize(new Dimension(0, 200));
      panel.add(scrollPane, BorderLayout.CENTER);
    } else {
      panel.add(new JLabel("No assigned sessions for this seminar", SwingConstants.CENTER),
          BorderLayout.CENTER);
    }

    return panel;
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