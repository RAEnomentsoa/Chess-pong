package net.client;

import net.core.GameState;
import net.core.PieceState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TerrainPanelNetwork extends JPanel {

    private GameClient client;
    private GameState state;

    private int cellSize = 60;
    private int size = 8;
    private int sizey = 8;

    public TerrainPanelNetwork(GameClient client) {
        this.client = client;

        setPreferredSize(new Dimension(size * cellSize, size * cellSize));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT ->
                        client.sendInput("LEFT");

                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT ->
                        client.sendInput("RIGHT");
                }
            }
        });

        // repaint loop
        new Timer(16, e -> repaint()).start();
    }

    public void updateState(GameState state) {
        this.state = state;

        // read dynamic world info from server
        this.size = state.boardSizex;
        this.sizey = state.boardSizey;
        this.cellSize = state.cellSize;

        // resize client window to match board
        setPreferredSize(new Dimension(state.boardWidth, state.boardHeight));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state == null)
            return;

        // Draw board grid
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < sizey; y++) { // fixena 8 ny axe y @zay tsy miova ny longeur fa ny largeur ihany

                int px = x * cellSize;
                int py = y * cellSize;

                g.setColor(((x + y) % 2 == 0) ? Color.LIGHT_GRAY : Color.GRAY);
                g.fillRect(px, py, cellSize, cellSize);

                g.setColor(Color.BLACK);
                g.drawRect(px, py, cellSize, cellSize);
            }
        }

        // Draw pieces
        if (state.pieces != null) {
            for (PieceState ps : state.pieces) {

                int px = ps.x * cellSize;
                int py = ps.y * cellSize;

                // player colors
                if (ps.ownerId == 1)
                    g.setColor(Color.GRAY); // Player 1
                else
                    g.setColor(Color.black); // Player 2

                g.fillOval(px + 10, py + 10, cellSize - 20, cellSize - 20);

                g.setColor(Color.BLACK); // outline
                g.drawOval(px + 10, py + 10, cellSize - 20, cellSize - 20);

                g.setColor(Color.WHITE);
                g.drawString(ps.name, px + 15, py + 25);
                g.drawString("HP:" + ps.life, px + 15, py + 40);
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Player 1: " + state.player1Name, 10, 20);
        g.drawString("Player 2: " + state.player2Name, 10, getHeight() - 10);

        // Ball
        g.setColor(Color.BLUE);
        g.fillOval(state.ballX, state.ballY, state.ballDiameter, state.ballDiameter);

        // Upper paddle
        g.setColor(Color.GREEN);
        g.fillRect(state.upperX, state.upperY, state.upperWidth, state.upperHeight);

        // Lower paddle
        g.setColor(Color.BLACK);
        g.fillRect(state.lowerX, state.lowerY, state.lowerWidth, state.lowerHeight);

        // Game Over
        if (state.gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 40));
            g.drawString(state.winnerName + " WINS!", 100, getHeight() / 2);
        }
    }
}
