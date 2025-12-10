package GUI;

import entities.Terrain;
import pong_entities.Ball;
import pong_entities.Raquete;
import entities.King;
import entities.Piece;

import javax.swing.*;

import Users.Players;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TerrainPanel extends JPanel {

    // terrain
    private Terrain terrain;
    private int cellSize = 60; // pixels per square

    // ball
    private Ball ball;
    private Timer timer;

    // raquetes
    private Raquete UpperPaddle;
    private Raquete LowerPaddle;

    // players
    private Players player1;
    private Players player2;

    public TerrainPanel(Terrain terrain, Players p1, Players p2) {

        this.terrain = terrain;
        this.player1 = p1;
        this.player2 = p2;

        setPreferredSize(new Dimension(
                terrain.getSize() * cellSize,
                terrain.getSize() * cellSize));

        // create paddles
        UpperPaddle = new Raquete(100, 150, 100, 20, Color.GREEN);
        LowerPaddle = new Raquete(100, 300, 100, 20, Color.black);

        setFocusable(true);

        addKeyListener((KeyListener) new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    // UPPER paddle
                    case KeyEvent.VK_A -> UpperPaddle.moveLeft();
                    case KeyEvent.VK_D -> UpperPaddle.moveRight();

                    // LOWER paddle
                    case KeyEvent.VK_LEFT -> LowerPaddle.moveLeft();
                    case KeyEvent.VK_RIGHT -> LowerPaddle.moveRight();
                }
            }
        });

        // create the ball at pixel position (center)
        ball = new Ball(200, 200, 20);

        // 60 FPS animation timer
        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    private void updateGame() {

        UpperPaddle.clamp(getWidth());
        LowerPaddle.clamp(getWidth());

        ball.update(getWidth(), getHeight());
        ball.clamp(getWidth(), getHeight());

        checkCollisions();

        repaint();
    }

    private void checkCollisions() {
        Rectangle b = ball.getBounds();
        int size = terrain.getSize();

        // -----------------------------------------
        // 1) COLLISION WITH CHESS PIECES
        // -----------------------------------------
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {

                Piece p = terrain.getPiece(x, y);
                if (p != null) {

                    int px = x * cellSize;
                    int py = y * cellSize;

                    Rectangle square = new Rectangle(px, py, cellSize, cellSize);

                    if (b.intersects(square)) {

                        long now = System.currentTimeMillis();
                        if (now - ball.lastHitTime < 80)
                            continue; // prevent double-hit
                        ball.lastHitTime = now;

                        System.out.println(
                                "Ball hit piece: " + p.getName() +
                                        " at (" + x + "," + y + ") " +
                                        "HP before = " + p.getlife());

                        // Ball hits this piece
                        p.decreaseLife(1);

                        System.out.println("HP after = " + p.getlife());

                        if (p.getlife() <= 0) {
                            System.out.println("Piece " + p.getName() + " at (" + x + "," + y + ") DESTROYED!");

                            if (p instanceof King) {
                                playerLose(p.getOwner());
                            }
                            terrain.removePiece(x, y);
                        }

                        // Physically correct bounce
                        ballBounce(b, square);

                        repaint();
                    }
                }
            }
        }

        // -----------------------------------------
        // 2) COLLISION WITH UPPER PADDLE
        // -----------------------------------------
        Rectangle upperRect = UpperPaddle.getBounds();

        if (b.intersects(upperRect)) {
            System.out.println("BALL HIT UPPER PADDLE");
            bounceOnPaddle(b, UpperPaddle, true);
        }

        // -----------------------------------------
        // 3) COLLISION WITH LOWER PADDLE
        // -----------------------------------------
        Rectangle lowerRect = LowerPaddle.getBounds();

        if (b.intersects(lowerRect)) {
            System.out.println("BALL HIT LOWER PADDLE");
            bounceOnPaddle(b, LowerPaddle, false);
        }
    }

    private void ballBounce(Rectangle ballRect, Rectangle hitRect) {
        try {
            var dxField = Ball.class.getDeclaredField("dx");
            var dyField = Ball.class.getDeclaredField("dy");

            dxField.setAccessible(true);
            dyField.setAccessible(true);

            int dx = dxField.getInt(ball);
            int dy = dyField.getInt(ball);

            // detect which side was hit
            int ballCenterX = ballRect.x + ballRect.width / 2;
            int ballCenterY = ballRect.y + ballRect.height / 2;

            int rectCenterX = hitRect.x + hitRect.width / 2;
            int rectCenterY = hitRect.y + hitRect.height / 2;

            int diffX = ballCenterX - rectCenterX;
            int diffY = ballCenterY - rectCenterY;

            boolean horizontal = Math.abs(diffX) > Math.abs(diffY);
            boolean vertical = Math.abs(diffY) > Math.abs(diffX);

            // HORIZONTAL BOUNCE
            if (horizontal) {
                dx = -dx;

                if (diffX > 0) {
                    // hit left side of paddle/piece → move ball to the right
                    ball.setX(hitRect.x + hitRect.width);
                } else {
                    // hit right side → move ball to the left
                    ball.setX(hitRect.x - ballRect.width);
                }
            }

            // VERTICAL BOUNCE
            if (vertical) {
                dy = -dy;

                if (diffY > 0) {
                    // hit top → push ball down
                    ball.setY(hitRect.y + hitRect.height);
                } else {
                    // hit bottom → push ball up
                    ball.setY(hitRect.y - ballRect.height);
                }
            }

            dxField.setInt(ball, dx);
            dyField.setInt(ball, dy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bounceOnPaddle(Rectangle b, Raquete paddle, boolean isUpper) {
        try {
            var dxField = Ball.class.getDeclaredField("dx");
            var dyField = Ball.class.getDeclaredField("dy");

            dxField.setAccessible(true);
            dyField.setAccessible(true);

            int dx = dxField.getInt(ball);
            int dy = dyField.getInt(ball);

            int ballCenterY = b.y + b.height / 2;
            int paddleCenterY = paddle.getY() + paddle.getHeight() / 2;

            boolean hitFromFront;
            if (isUpper) {
                // front = ball coming from BELOW the paddle
                hitFromFront = ballCenterY > paddle.getY();
            } else {
                // front = ball coming from ABOVE the paddle
                hitFromFront = ballCenterY < paddle.getY();
            }

            if (hitFromFront) {
                // NORMAL (FRONT) COLLISION — forced gameplay bounce
                if (isUpper) {
                    dy = Math.abs(dy); // bounce downward
                    ball.setY(paddle.getY() + paddle.getHeight() + 1);
                } else {
                    dy = -Math.abs(dy); // bounce upward
                    ball.setY(paddle.getY() - b.height - 1);
                }

                // spin
                int ballCenter = b.x + b.width / 2;
                int paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                dx += (ballCenter - paddleCenter) / 12;

            } else {
                // BACKSIDE COLLISION — REAL PHYSICS
                System.out.println("Back hit detected!");
                // normal rectangle bounce physics
                dx = -dx;
                dy = -dy;
            }

            dxField.setInt(ball, dx);
            dyField.setInt(ball, dy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // winner
    private void playerLose(Players loser) {
        Players winner = (loser == player1) ? player2 : player1;

        JOptionPane.showMessageDialog(
                this,
                "KING of " + loser.getName() + " has been destroyed!\n" +
                        winner.getName() + " WINS!",
                "GAME OVER",
                JOptionPane.INFORMATION_MESSAGE);

        // Stop the game loop (timer)
        timer.stop();
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
        // Draw the ball on top
        ball.draw(g);
        // Draw paddles on top
        UpperPaddle.draw(g);
        LowerPaddle.draw(g);
    }

    // Refresh board externally
    public void refresh() {
        repaint();
    }
}
