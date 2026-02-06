package seminar_manager.views.coordinator;

import seminar_manager.controllers.*;
import seminar_manager.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CoordinatorSeminarsFrame extends JFrame {
    private AuthController authController;
    private SeminarController seminarController;

    private JTable seminarsTable;
    private DefaultTableModel tableModel;

    public CoordinatorSeminarsFrame(AuthController authController) {
        this.authController = authController;
        this.seminarController = new SeminarController();

        initComponents();
        loadSeminars();

        setTitle("All Seminars");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("All Seminars");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Table Panel
        String[] columnNames = { "ID", "Title", "Location", "Start Time", "End Time" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        seminarsTable = new JTable(tableModel);
        seminarsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(seminarsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton createButton = new JButton("Create New Seminar");
        createButton.addActionListener(e -> {
            new CoordinatorCreateSeminarFrame(authController);
            dispose();
        });
        buttonPanel.add(createButton);

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewSelectedSeminar());
        buttonPanel.add(viewButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Double-click to view
        seminarsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewSelectedSeminar();
                }
            }
        });
    }

    private void loadSeminars() {
        tableModel.setRowCount(0);

        List<Seminar> seminars = seminarController.getAllSeminars();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Seminar seminar : seminars) {
            Object[] row = {
                    seminar.getSeminarId(),
                    seminar.getTitle(),
                    seminar.getLocation(),
                    dateFormat.format(seminar.getStartTime()),
                    dateFormat.format(seminar.getEndTime())
            };
            tableModel.addRow(row);
        }
    }

    private void viewSelectedSeminar() {
        int selectedRow = seminarsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a seminar to view",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int seminarId = (int) tableModel.getValueAt(selectedRow, 0);
        new CoordinatorSeminarDetailFrame(authController, seminarId);
        dispose();
    }
}