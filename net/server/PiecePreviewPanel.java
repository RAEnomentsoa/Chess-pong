package net.server;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PiecePreviewPanel extends JPanel {

    private int size;

    public PiecePreviewPanel(int size) {
        this.size = size;
        setPreferredSize(new Dimension(360, 120));
    }

    public void setSize(int size) {
        this.size = size;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<String> pieces = getPiecesForSize(size);

        int cell = 40;
        int startX = (getWidth() - pieces.size() * cell) / 2;

        for (int i = 0; i < pieces.size(); i++) {
            int x = startX + i * cell;
            int y = 40;

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, cell, cell);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, cell, cell);

            g.drawString(pieces.get(i), x + 10, y + 25);
        }
    }

    private List<String> getPiecesForSize(int size) {
        return switch (size) {
            case 2 -> List.of("K", "Q");
            case 4 -> List.of("B", "K", "Q", "B");
            case 6 -> List.of("N", "B", "K", "Q", "B", "N");
            case 8 -> List.of("R", "N", "B", "Q", "K", "B", "N", "R");
            default -> List.of();
        };
    }
}
