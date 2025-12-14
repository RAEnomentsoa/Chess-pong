package net.server;

import javax.swing.*;
import java.awt.*;

public class GameSetupFrame extends JFrame {

    private JComboBox<Integer> sizeBox;
    private PiecePreviewPanel preview;
    private JSpinner kingLife, queenLife, rookLife, bishopLife, knightLife, pawnLife;
    private JPanel kingRow, queenRow, rookRow, bishopRow, knightRow, pawnRow;
    private JPanel lifePanel;

    public GameSetupFrame() {
        setTitle("Chess Pong – Game Setup");
        setSize(420, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JComponent createHeader() {
        JLabel title = new JLabel("GAME SETUP", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return title;
    }

    private JComponent createCenter() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        top.add(new JLabel("Board size:"));

        sizeBox = new JComboBox<>(new Integer[] { 2, 4, 6, 8 });
        sizeBox.setSelectedItem(4);
        top.add(sizeBox);

        panel.add(top, BorderLayout.NORTH);

        panel.add(createLifePanel(), BorderLayout.CENTER);

        lifePanel.add(pawnLife);

        panel.add(lifePanel, BorderLayout.CENTER);

        updateLifeVisibility(4); // default

        sizeBox.addActionListener(e -> {
            int size = (int) sizeBox.getSelectedItem();
            // preview.setSize(size);
            updateLifeVisibility(size);
        });

        return panel;
    }

    private JComponent createLifePanel() {
        lifePanel = new JPanel();
        lifePanel.setLayout(new BoxLayout(lifePanel, BoxLayout.Y_AXIS));
        lifePanel.setBorder(BorderFactory.createTitledBorder("Piece Life"));

        kingLife = spinner(10);
        queenLife = spinner(12);
        rookLife = spinner(6);
        bishopLife = spinner(8);
        knightLife = spinner(5);
        pawnLife = spinner(3);

        kingRow = row("King", kingLife);
        queenRow = row("Queen", queenLife);
        rookRow = row("Rook", rookLife);
        bishopRow = row("Bishop", bishopLife);
        knightRow = row("Knight", knightLife);
        pawnRow = row("Pawn", pawnLife);

        lifePanel.add(kingRow);
        lifePanel.add(queenRow);
        lifePanel.add(rookRow);
        lifePanel.add(bishopRow);
        lifePanel.add(knightRow);
        lifePanel.add(pawnRow);

        return lifePanel;
    }

    private JPanel row(String name, JSpinner spinner) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(name));
        p.add(spinner);
        p.add(new JLabel("HP"));
        return p;
    }

    private JSpinner spinner(int value) {
        return new JSpinner(new SpinnerNumberModel(value, 1, 99, 1));
    }

    private void updateLifeVisibility(int size) {

        // always visible
        kingRow.setVisible(true);
        queenRow.setVisible(true);
        pawnRow.setVisible(true);

        bishopRow.setVisible(size >= 4);
        knightRow.setVisible(size >= 6);
        rookRow.setVisible(size >= 8);

        lifePanel.revalidate();
        lifePanel.repaint();
    }

    private JComponent createFooter() {
        JButton start = new JButton("START GAME");
        start.setFont(new Font("Arial", Font.BOLD, 16));

        start.addActionListener(e -> {
            int size = (int) sizeBox.getSelectedItem();

            PieceLifeConfig life = new PieceLifeConfig();
            life.king = (int) kingLife.getValue();
            life.queen = (int) queenLife.getValue();
            life.rook = (int) rookLife.getValue();
            life.bishop = (int) bishopLife.getValue();
            life.knight = (int) knightLife.getValue();
            life.pawn = (int) pawnLife.getValue();

            dispose();

            new Thread(() -> {
                try {
                    new GameServer(5000, size, life);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        JPanel panel = new JPanel();
        panel.add(start);
        return panel;
    }
}
