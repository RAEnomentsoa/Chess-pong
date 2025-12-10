package GUI;

import entities.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;

import Users.Players;

public class TerrainWindow extends JFrame {

    private Players player1;
    private Players player2;

    private JLabel p1Label;
    private JLabel p2Label;

    public TerrainWindow(Players p1, Players p2) {
        // players
        this.player1 = p1;
        this.player2 = p2;

        System.out.println("Player 1: " + p1);
        System.out.println("Player 2: " + p2);

        // players board
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.setBackground(new Color(20, 20, 20));

        Font infoFont = new Font("Verdana", Font.BOLD, 16);

        p1Label = new JLabel(formatPlayerText(player1));
        p1Label.setForeground(Color.GREEN);
        p1Label.setHorizontalAlignment(SwingConstants.CENTER);
        p1Label.setFont(infoFont);

        p2Label = new JLabel(formatPlayerText(player2));
        p2Label.setForeground(Color.CYAN);
        p2Label.setHorizontalAlignment(SwingConstants.CENTER);
        p2Label.setFont(infoFont);

        topPanel.add(p1Label);
        topPanel.add(p2Label);

        add(topPanel, BorderLayout.NORTH);

        // Create a terrain 6x6
        Terrain terrain = new Terrain(8);

        // --------------------
        // BLACK BACK RANK (y = 0)
        // --------------------
        terrain.placePiece(0, 0, new Rook(6, player1)); // a8
        terrain.placePiece(1, 0, new Knight(5, player1)); // b8
        terrain.placePiece(2, 0, new Bishop(8, player1)); // c8
        terrain.placePiece(3, 0, new Queen(12, player1)); // d8
        terrain.placePiece(4, 0, new King(10, player1)); // e8
        terrain.placePiece(5, 0, new Bishop(8, player1)); // f8
        terrain.placePiece(6, 0, new Knight(5, player1)); // g8
        terrain.placePiece(7, 0, new Rook(6, player1)); // h8

        // --------------------
        // BLACK PAWNS (y = 1)
        // --------------------
        for (int x = 0; x < 8; x++) {
            terrain.placePiece(x, 1, new Pawn(3, player1));
        }

        // --------------------
        // WHITE PAWNS (y = 6)
        // --------------------
        for (int x = 0; x < 8; x++) {
            terrain.placePiece(x, 6, new Pawn(3, player2));
        }

        // --------------------
        // WHITE BACK RANK (y = 7)
        // --------------------
        terrain.placePiece(0, 7, new Rook(6, player2)); // a1
        terrain.placePiece(1, 7, new Knight(5, player2)); // b1
        terrain.placePiece(2, 7, new Bishop(8, player2)); // c1
        terrain.placePiece(3, 7, new Queen(12, player2)); // d1
        terrain.placePiece(4, 7, new King(10, player2)); // e1
        terrain.placePiece(5, 7, new Bishop(8, player2)); // f1
        terrain.placePiece(6, 7, new Knight(5, player2)); // g1
        terrain.placePiece(7, 7, new Rook(6, player2)); // h1

        // Create panel
        TerrainPanel panel = new TerrainPanel(terrain, player1, player2);

        add(panel);

        setTitle("Chess Pong - Terrain");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Formats text like: "Alex (M) - Score: 3"
    private String formatPlayerText(Players p) {
        return p.getName() + " (" + p.getGender() + ")  |  Score: " + p.getScore();
    }

    // Update score display when changed
    public void refreshPlayerHUD() {
        p1Label.setText(formatPlayerText(player1));
        p2Label.setText(formatPlayerText(player2));
    }
}
