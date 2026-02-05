import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentDashboard extends JFrame {

    // FILE PATH: Points to the single student.txt file
    private static final String FILE_PATH = "../data/student.txt";

    private String currentStudentId;

    public StudentDashboard(String studentId) {
        this.currentStudentId = studentId;

        setTitle("Student Dashboard - ID: " + studentId);
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Home", createHomePanel());
        tabs.addTab("Register Seminar", createRegisterPanel());
        tabs.addTab("Upload Materials", createUploadPanel());
        tabs.addTab("My Status", createStatusPanel());
        
        // Logout Tab
        JPanel logoutPanel = new JPanel();
        tabs.addTab("Logout", logoutPanel);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 4) { // Index 4 is Logout
                dispose();
                new StudentLogin().setVisible(true);
            }
        });

        add(tabs);
    }

    // =======================================================
    // TAB 1: HOME
    // =======================================================
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Get Name from file (Index 1)
        String[] data = getCurrentUserData();
        String name = (data != null && data.length > 1) ? data[1] : currentStudentId;

        JLabel lblWelcome = new JLabel("Welcome, " + name + "!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 26));
        lblWelcome.setForeground(new Color(0, 102, 204));

        JLabel lblInstructions = new JLabel("<html><center>Use the tabs above to register for your seminar,<br>select your evaluator, and upload your slides.</center></html>", SwingConstants.CENTER);
        lblInstructions.setFont(new Font("SansSerif", Font.PLAIN, 16));

        panel.add(lblWelcome, BorderLayout.NORTH);
        panel.add(lblInstructions, BorderLayout.CENTER);

        return panel;
    }

    // =======================================================
    // TAB 2: REGISTER (With 4 Evaluator Examples)
    // =======================================================
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10)); // Increased rows for Evaluator
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtTitle = new JTextField();
        
        // --- 4 EXAMPLES OF EVALUATORS ---
        String[] evaluators = {
            "Dr. Alice Vincent", 
            "Prof. Duraisingam", 
            "Dr. Christopher Yeslan", 
            "Ms. Mary"
        };
        JComboBox<String> cmbEvaluator = new JComboBox<>(evaluators);
        
        JComboBox<String> cmbType = new JComboBox<>(new String[]{"Oral", "Poster"});
        JButton btnSubmit = new JButton("Update Registration");

        // Pre-fill data if already saved
        String[] data = getCurrentUserData();
        if(data != null) {
            if(!data[3].equals("None")) txtTitle.setText(data[3]);          // Title
            if(!data[4].equals("None")) cmbEvaluator.setSelectedItem(data[4]); // Evaluator
            if(!data[5].equals("None")) cmbType.setSelectedItem(data[5]);   // Type
        }

        panel.add(new JLabel("Research Title:")); panel.add(txtTitle);
        panel.add(new JLabel("Select Evaluator:")); panel.add(cmbEvaluator);
        panel.add(new JLabel("Presentation Type:")); panel.add(cmbType);
        panel.add(new JLabel("")); panel.add(new JLabel("")); // Spacer
        panel.add(new JLabel("")); panel.add(btnSubmit);

        btnSubmit.addActionListener(e -> {
            if(txtTitle.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a title.");
                return;
            }
            
            // Update Title (Index 3)
            updateUserFile(3, txtTitle.getText());
            
            // Update Evaluator (Index 4)
            updateUserFile(4, (String)cmbEvaluator.getSelectedItem());
            
            // Update Type (Index 5)
            updateUserFile(5, (String)cmbType.getSelectedItem());
            
            JOptionPane.showMessageDialog(this, "Registration & Evaluator Updated Successfully!");
        });

        return panel;
    }

    // =======================================================
    // TAB 3: UPLOAD
    // =======================================================
    private JPanel createUploadPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        JLabel lblFile = new JLabel("Current File: None");
        JButton btnBrowse = new JButton("Select File");
        JButton btnUpload = new JButton("Upload");

        // Check current status (Index 6)
        String[] data = getCurrentUserData();
        if(data != null && !data[6].equals("None")) {
            File f = new File(data[6]);
            lblFile.setText("Current File: " + f.getName());
        }

        panel.add(btnBrowse);
        panel.add(lblFile);
        panel.add(btnUpload);

        final String[] tempPath = { "" };

        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                tempPath[0] = f.getAbsolutePath();
                lblFile.setText("Selected: " + f.getName());
            }
        });

        btnUpload.addActionListener(e -> {
            if (tempPath[0].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a file first.");
                return;
            }
            // Update File Path (Index 6)
            updateUserFile(6, tempPath[0]);
            JOptionPane.showMessageDialog(this, "File Uploaded to System!");
        });

        return panel;
    }

    // =======================================================
    // TAB 4: STATUS
    // =======================================================
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea txtStatus = new JTextArea();
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Monospaced", Font.BOLD, 14));
        txtStatus.setMargin(new Insets(20,20,20,20));

        JButton btnRefresh = new JButton("Refresh Status");
        
        panel.add(new JScrollPane(txtStatus), BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);

        // Action to populate text
        Runnable loadStatus = () -> {
            String[] data = getCurrentUserData();
            if(data == null) {
                txtStatus.setText("Error: User data not found.");
                return;
            }
            // Format: ID,Name,Pass,Title,Evaluator,Type,File
            String title = data[3].equals("None") ? "Not Registered" : data[3];
            String evaluator = data[4].equals("None") ? "Pending" : data[4];
            String type = data[5].equals("None") ? "-" : data[5];
            String file = data[6].equals("None") ? "Not Uploaded" : "Uploaded";

            txtStatus.setText(
                "--- MY SEMINAR STATUS ---\n\n" +
                "Student ID:       " + data[0] + "\n" +
                "Name:             " + data[1] + "\n\n" +
                "Research Title:   " + title + "\n" +
                "Presentation Type:" + type + "\n" +
                "Assigned Evaluator:" + evaluator + "\n" +  // Shows the saved evaluator
                "Presentation File:" + file + "\n"
            );
        };

        btnRefresh.addActionListener(e -> loadStatus.run());
        loadStatus.run(); // Load immediately on open

        return panel;
    }

    // =======================================================
    // FILE HANDLING (READ & UPDATE)
    // =======================================================

    // 1. Read the specific line for the current user
    private String[] getCurrentUserData() {
        File file = new File(FILE_PATH);
        if(!file.exists()) return null;

        try (Scanner sc = new Scanner(file)) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(",");
                // ID is at Index 0
                if(parts.length > 0 && parts[0].trim().equals(currentStudentId)) {
                    // Ensure array is big enough (fill missing columns with "None")
                    String[] padded = new String[7];
                    for(int i=0; i<7; i++) {
                        if(i < parts.length) padded[i] = parts[i].trim();
                        else padded[i] = "None";
                    }
                    return padded;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 2. Update a specific column for the current user
    // This reads the whole file, changes one line, and writes it back.
    private void updateUserFile(int columnIndex, String newValue) {
        File file = new File(FILE_PATH);
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (Scanner sc = new Scanner(file)) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(",");
                
                if(parts.length > 0 && parts[0].trim().equals(currentStudentId)) {
                    // This is our user. Update the column.
                    String[] newParts = new String[7];
                    for(int i=0; i<7; i++) {
                        if(i < parts.length) newParts[i] = parts[i].trim();
                        else newParts[i] = "None";
                    }
                    
                    newParts[columnIndex] = newValue; // Set the new value
                    
                    lines.add(String.join(",", newParts));
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        if(found) {
            try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
                for(String l : lines) {
                    out.println(l);
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}