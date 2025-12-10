package GUI;

import entities.*;

import javax.swing.*;

public class TerrainWindow extends JFrame {

    public TerrainWindow() {
        // Create a terrain 6x6
        Terrain terrain = new Terrain(8);

        // --------------------
        // BLACK BACK RANK (y = 0)
        // --------------------
        terrain.placePiece(0, 0, new Rook(6)); // a8
        terrain.placePiece(1, 0, new Knight(5)); // b8
        terrain.placePiece(2, 0, new Bishop(8)); // c8
        terrain.placePiece(3, 0, new Queen(12)); // d8
        terrain.placePiece(4, 0, new King(10)); // e8
        terrain.placePiece(5, 0, new Bishop(8)); // f8
        terrain.placePiece(6, 0, new Knight(5)); // g8
        terrain.placePiece(7, 0, new Rook(6)); // h8

        // --------------------
        // BLACK PAWNS (y = 1)
        // --------------------
        for (int x = 0; x < 8; x++) {
            terrain.placePiece(x, 1, new Pawn(3));
        }

        // --------------------
        // WHITE PAWNS (y = 6)
        // --------------------
        for (int x = 0; x < 8; x++) {
            terrain.placePiece(x, 6, new Pawn(3));
        }

        // --------------------
        // WHITE BACK RANK (y = 7)
        // --------------------
        terrain.placePiece(0, 7, new Rook(6)); // a1
        terrain.placePiece(1, 7, new Knight(5)); // b1
        terrain.placePiece(2, 7, new Bishop(8)); // c1
        terrain.placePiece(3, 7, new Queen(12)); // d1
        terrain.placePiece(4, 7, new King(10)); // e1
        terrain.placePiece(5, 7, new Bishop(8)); // f1
        terrain.placePiece(6, 7, new Knight(5)); // g1
        terrain.placePiece(7, 7, new Rook(6)); // h1

        // Create panel
        TerrainPanel panel = new TerrainPanel(terrain);

        add(panel);

        setTitle("Chess Pong - Terrain");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
