import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class StudentLogin extends JFrame {

    // FILE PATH: "../data/" steps out of the 'student' folder and into 'data'
    private static final String FILE_PATH = "../data/student.txt";

    // Layout to swap between Login and Signup screens
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    // Allow running this file directly for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentLogin().setVisible(true);
        });
    }

    public StudentLogin() {
        setTitle("Student Access System");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create the two screens (panels)
        mainContainer.add(createLoginPanel(), "LOGIN");
        mainContainer.add(createSignupPanel(), "SIGNUP");

        add(mainContainer);
    }

    // =======================================================
    // PANEL 1: LOGIN SCREEN
    // =======================================================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("STUDENT LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        JButton btnGoSignup = new JButton("New Student? Sign Up Here");

        // Styling the link button
        btnGoSignup.setForeground(Color.BLUE);
        btnGoSignup.setBorderPainted(false);
        btnGoSignup.setContentAreaFilled(false);

        // Layout Components
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; panel.add(lblTitle, gbc);
        
        gbc.gridwidth=1;
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx=1; panel.add(txtUser, gbc);
        
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; panel.add(txtPass, gbc);
        
        gbc.gridx=1; gbc.gridy=3; panel.add(btnLogin, gbc);
        
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; panel.add(btnGoSignup, gbc);

        // --- BUTTON ACTIONS ---
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            
            // Validate against the new format
            String foundID = checkLogin(user, pass);
            
            if(foundID != null) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user);
                // PASS THE ID TO DASHBOARD
                new StudentDashboard(foundID).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password.");
            }
        });

        btnGoSignup.addActionListener(e -> cardLayout.show(mainContainer, "SIGNUP"));

        return panel;
    }

    // =======================================================
    // PANEL 2: SIGN UP SCREEN
    // =======================================================
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnRegister = new JButton("Sign Up");
        JButton btnBack = new JButton("Back to Login");

        // Layout Components
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; panel.add(lblTitle, gbc);
        
        gbc.gridwidth=1;
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("New Username:"), gbc);
        gbc.gridx=1; panel.add(txtUser, gbc);
        
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx=1; panel.add(txtPass, gbc);
        
        gbc.gridx=1; gbc.gridy=3; panel.add(btnRegister, gbc);
        gbc.gridx=1; gbc.gridy=4; panel.add(btnBack, gbc);

        // --- BUTTON ACTIONS ---
        btnRegister.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            if(user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            // 1. Generate ID (Based on Index 0)
            String newID = generateID();
            
            // 2. Save to file (New Format)
            saveUser(newID, user, pass);

            JOptionPane.showMessageDialog(this, 
                "Account Created!\n\nUsername: " + user + "\nYour ID: " + newID);
            
            // Return to login
            cardLayout.show(mainContainer, "LOGIN");
        });

        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "LOGIN"));

        return panel;
    }

    // =======================================================
    // FILE HANDLING (DATA FOLDER)
    // =======================================================
    
    // Returns the ID if login is correct, null otherwise
    private String checkLogin(String user, String pass) {
        File file = new File(FILE_PATH);
        if(!file.exists()) return null;

        try (Scanner sc = new Scanner(file)) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(",");
                // New Format: ID[0], Name[1], Pass[2], Title[3], Evaluator[4], Oral[5], Submissions[6]
                if(parts.length >= 3) {
                    if(parts[1].trim().equals(user) && parts[2].trim().equals(pass)) {
                        return parts[0].trim(); // Return the ID
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // Save new user with placeholders for the future data
    private void saveUser(String id, String user, String pass) {
        File folder = new File("../data");
        if(!folder.exists()) folder.mkdir();

        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter out = new PrintWriter(fw)) {
            // Format: ID,NAME,PASSWORD,RESEARCH TITLE,EVALUATOR,ORAL,SUBMISSIONS
            // We use "None" for the empty fields
            out.println(id + "," + user + "," + pass + ",None,None,None,None");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving to " + FILE_PATH);
        }
    }

    // Generate unique ID (001-S, 002-S...) based on Index 0
    private String generateID() {
        int max = 0;
        File file = new File(FILE_PATH);
        if(file.exists()) {
            try (Scanner sc = new Scanner(file)) {
                while(sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(",");
                    if(parts.length >= 1) {
                        String idStr = parts[0].trim(); // ID is now Index 0
                        if(idStr.endsWith("-S")) {
                            try {
                                int num = Integer.parseInt(idStr.replace("-S", ""));
                                if(num > max) max = num;
                            } catch (Exception e) {}
                        }
                    }
                }
            } catch (Exception e) {}
        }
        return String.format("%03d-S", max + 1);
    }
}