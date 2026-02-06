package seminar_manager.views;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Home extends JFrame {
  // public Home() {

  // setSize(720, 720);
  // setVisible(true);
  // setDefaultCloseOperation(EXIT_ON_CLOSE);
  // }
  public Home() {
    setTitle("Seminar Management System");
    setSize(400, 300);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    JButton loginBtn = new JButton("Login");
    JButton registerBtn = new JButton("Register");

    loginBtn.addActionListener(e -> {
      new LoginFrame();
      dispose();
    });

    registerBtn.addActionListener(e -> {
      new RegisterFrame();
      dispose();
    });

    setLayout(new GridLayout(2, 1, 10, 10));
    add(loginBtn);
    add(registerBtn);

    setVisible(true);
  }
}
