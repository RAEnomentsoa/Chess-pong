package GUI;

import Users.Players;

import javax.swing.*;
import java.awt.*;

public class PlayerSetupWindow extends JFrame {

    private JTextField p1NameField, p2NameField;
    private JComboBox<String> p1GenderBox, p2GenderBox;

    public PlayerSetupWindow() {

        setTitle("Chess Pong - Player Setup");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // PANEL STYLING
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // FONT
        Font labelFont = new Font("Verdana", Font.BOLD, 14);
        Font fieldFont = new Font("Verdana", Font.PLAIN, 14);

        // ----- Player 1 -----
        JLabel p1Label = new JLabel("Player 1 Name:");
        p1Label.setForeground(Color.WHITE);
        p1Label.setFont(labelFont);

        p1NameField = new JTextField();
        p1NameField.setFont(fieldFont);

        JLabel p1GenderLabel = new JLabel("Gender:");
        p1GenderLabel.setForeground(Color.WHITE);
        p1GenderLabel.setFont(labelFont);

        p1GenderBox = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        p1GenderBox.setFont(fieldFont);

        // ----- Player 2 -----
        JLabel p2Label = new JLabel("Player 2 Name:");
        p2Label.setForeground(Color.WHITE);
        p2Label.setFont(labelFont);

        p2NameField = new JTextField();
        p2NameField.setFont(fieldFont);

        JLabel p2GenderLabel = new JLabel("Gender:");
        p2GenderLabel.setForeground(Color.WHITE);
        p2GenderLabel.setFont(labelFont);

        p2GenderBox = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        p2GenderBox.setFont(fieldFont);

        // ----- Start Button -----
        JButton startBtn = new JButton("START GAME");
        startBtn.setFont(new Font("Verdana", Font.BOLD, 16));
        startBtn.setBackground(new Color(70, 130, 180));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        startBtn.addActionListener(e -> startGame());

        // ----- Add components to panel -----

        gc.gridx = 0;
        gc.gridy = 0;
        panel.add(p1Label, gc);

        gc.gridx = 1;
        panel.add(p1NameField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        panel.add(p1GenderLabel, gc);

        gc.gridx = 1;
        panel.add(p1GenderBox, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        panel.add(p2Label, gc);

        gc.gridx = 1;
        panel.add(p2NameField, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        panel.add(p2GenderLabel, gc);

        gc.gridx = 1;
        panel.add(p2GenderBox, gc);

        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        panel.add(startBtn, gc);

        add(panel);
        setVisible(true);
    }

    private void startGame() {

        String p1Name = p1NameField.getText().trim();
        String p2Name = p2NameField.getText().trim();

        if (p1Name.isEmpty() || p2Name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Players must have names!");
            return;
        }

        Players p1 = new Players(p1Name, 0, (String) p1GenderBox.getSelectedItem());
        Players p2 = new Players(p2Name, 0, (String) p2GenderBox.getSelectedItem());

        // START GAME
        new TerrainWindow(p1, p2);

        // CLOSE SETUP WINDOW
        this.dispose();
    }
}
