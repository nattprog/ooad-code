package parking_lot_management_system.database;

import java.io.*;
import java.text.DecimalFormat;

public class DatabaseManager {

  private static final String FILE = "revenue.txt";

  // Initialize file
  public static void initialize() {
    try {
      File f = new File(FILE);
      if (!f.exists()) {
        f.createNewFile();
      }
      System.out.println("Database (file) initialized successfully.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Save revenue
  public static void saveRevenue(double amount) {
    try (FileWriter fw = new FileWriter(FILE, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw)) {

      DecimalFormat df = new DecimalFormat("#.##");
      out.println(df.format(amount));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Read total revenue
  public static double getTotalRevenue() {
    double total = 0;
    try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
      String line;
      while ((line = br.readLine()) != null) {
        total += Double.parseDouble(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return total;
  }
}
