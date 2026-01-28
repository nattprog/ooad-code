import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // This line will only work if StudentLogin.java is error-free and saved
            new StudentLogin().setVisible(true);
        });
    }
}