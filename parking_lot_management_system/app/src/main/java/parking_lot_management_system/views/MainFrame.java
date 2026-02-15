package parking_lot_management_system.views;

import parking_lot_management_system.models.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("University Parking Lot System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        ParkingLot lot = ParkingLot.getInstance();

        tabbedPane.add("Entry", new EntryPanel(lot));
        tabbedPane.add("Exit", new ExitPanel(lot));
        tabbedPane.add("Reports", new ReportPanel(lot));
        tabbedPane.add("Admin", new AdminPanel(lot));

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
