package GUI;

import entities.Terrain;
import entities.Piece;

import javax.swing.*;
import java.awt.*;

public class TerrainPanel extends JPanel {

    private Terrain terrain;
    private int cellSize = 60; // pixels per square

    public TerrainPanel(Terrain terrain) {
        this.terrain = terrain;
        setPreferredSize(new Dimension(
                terrain.getSize() * cellSize,
                terrain.getSize() * cellSize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int size = terrain.getSize();

        // Draw grid + pieces
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {

                int px = x * cellSize;
                int py = y * cellSize;

                // Draw grid square
                g.setColor(((x + y) % 2 == 0) ? Color.LIGHT_GRAY : Color.GRAY);
                g.fillRect(px, py, cellSize, cellSize);

                // Draw piece if exists
                Piece p = terrain.getPiece(x, y);
                if (p != null) {
                    g.setColor(Color.RED);
                    g.fillOval(px + 10, py + 10, cellSize - 20, cellSize - 20);

                    g.setColor(Color.WHITE);
                    g.drawString(p.getName(), px + 15, py + 30);
                    g.drawString("HP: " + p.getlife(), px + 15, py + 45);
                }

                // Draw square border
                g.setColor(Color.BLACK);
                g.drawRect(px, py, cellSize, cellSize);
            }
        }
    }

    // Refresh board externally
    public void refresh() {
        repaint();
    }
}
