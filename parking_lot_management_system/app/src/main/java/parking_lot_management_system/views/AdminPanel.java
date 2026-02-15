package parking_lot_management_system.views;

import parking_lot_management_system.models.*;
import parking_lot_management_system.models.enums.FineScheme;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AdminPanel extends JPanel {

    private ParkingLot lot;
    private JComboBox<String> fineCombo;
    private JButton applyButton;

    public AdminPanel(ParkingLot lot) {
        this.lot = lot;
        setLayout(new FlowLayout());

        add(new JLabel("Select Fine Scheme:"));

        // fineCombo = new JComboBox<>(new String[] { "Fixed", "Progressive", "Hourly"
        // });

        fineCombo = new JComboBox<>(
                Arrays.stream(FineScheme.values())
                        .map(scheme -> scheme.name().charAt(0) + scheme.name().substring(1).toLowerCase())
                        .toArray(String[]::new));

        add(fineCombo);

        applyButton = new JButton("Apply");
        add(applyButton);

        applyButton.addActionListener(e -> applyFineScheme());
    }

    private void applyFineScheme() {
        int index = fineCombo.getSelectedIndex();
        lot.setFineScheme(FineScheme.values()[index]);
        JOptionPane.showMessageDialog(this, "Fine scheme updated successfully.");
    }
}
