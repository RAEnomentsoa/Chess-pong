package GUI;

import net.core.GameLogic;
import net.core.GameState;
import net.core.PieceState;

import entities.Terrain;
import pong_entities.Ball;
import pong_entities.Raquete;
import Users.Players;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TerrainPanelLocal extends JPanel {

    private GameLogic logic;
    private GameState state;

    private Timer timer;

    private int cellSize = 60;

    public TerrainPanelLocal(Terrain terrain, Players p1, Players p2) {

        // ------------------------------
        // INITIALIZE GAME OBJECTS
        // ------------------------------
        int size = terrain.getSize();
        int boardWidth = size * cellSize;
        int boardHeight = size * cellSize;

        Ball ball = new Ball(200, 200, 20);

        Raquete upper = new Raquete(100, 150, 100, 20, Color.GREEN);
        Raquete lower = new Raquete(100, 300, 100, 20, Color.BLACK);

        // ------------------------------
        // CREATE GAME LOGIC ENGINE
        // ------------------------------
        logic = new GameLogic(
                terrain,
                cellSize,
                ball,
                upper,
                lower,
                p1,
                p2,
                boardWidth,
                boardHeight);

        // ------------------------------
        // PANEL CONFIG
        // ------------------------------
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);

        // ------------------------------
        // INPUT HANDLING
        // ------------------------------
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    // UPPER paddle (player 1)
                    case KeyEvent.VK_A -> logic.moveUpperLeft();
                    case KeyEvent.VK_D -> logic.moveUpperRight();

                    // LOWER paddle (player 2)
                    case KeyEvent.VK_LEFT -> logic.moveLowerLeft();
                    case KeyEvent.VK_RIGHT -> logic.moveLowerRight();
                }
            }
        });

        // ------------------------------
        // GAME LOOP TIMER (60 FPS)
        // ------------------------------
        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    private void updateGame() {
        logic.update();
        state = logic.buildState();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state == null)
            return;

        // ------------------------------
        // DRAW BOARD GRID
        // ------------------------------
        int size = (state.pieces != null) ? 8 : 8;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int px = x * cellSize;
                int py = y * cellSize;

                g.setColor(((x + y) % 2 == 0) ? Color.LIGHT_GRAY : Color.GRAY);
                g.fillRect(px, py, cellSize, cellSize);

                g.setColor(Color.BLACK);
                g.drawRect(px, py, cellSize, cellSize);
            }
        }

        // ------------------------------
        // DRAW PIECES
        // ------------------------------
        if (state.pieces != null) {
            for (PieceState ps : state.pieces) {

                int px = ps.x * cellSize;
                int py = ps.y * cellSize;

                g.setColor(Color.RED);
                g.fillOval(px + 10, py + 10, cellSize - 20, cellSize - 20);

                g.setColor(Color.WHITE);
                g.drawString(ps.name, px + 15, py + 25);
                g.drawString("HP:" + ps.life, px + 15, py + 40);
            }
        }

        // ------------------------------
        // DRAW BALL
        // ------------------------------
        g.setColor(Color.BLUE);
        g.fillOval(state.ballX, state.ballY, state.ballDiameter, state.ballDiameter);

        // ------------------------------
        // DRAW UPPER PADDLE
        // ------------------------------
        g.setColor(Color.GREEN);
        g.fillRect(state.upperX, state.upperY, state.upperWidth, state.upperHeight);

        // ------------------------------
        // DRAW LOWER PADDLE
        // ------------------------------
        g.setColor(Color.BLACK);
        g.fillRect(state.lowerX, state.lowerY, state.lowerWidth, state.lowerHeight);

        // ------------------------------
        // GAME OVER
        // ------------------------------
        if (state.gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 40));
            g.drawString(state.winnerName + " WINS!", 100, getHeight() / 2);
            timer.stop();
        }
    }
}
