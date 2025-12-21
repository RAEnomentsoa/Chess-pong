package net.core;

import entities.*;
import pong_entities.Ball;
import pong_entities.Raquete;
import Users.Players;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private Terrain terrain;
    private int cellSize; // 60

    private Ball ball;
    private Raquete upperPaddle;
    private Raquete lowerPaddle;

    private Players player1;
    private Players player2;

    private int boardWidth;
    private int boardHeight;

    // game over state
    private boolean gameOver = false;
    private Players winner = null;

    public GameLogic(Terrain terrain,
            int cellSize,
            Ball ball,
            Raquete upperPaddle,
            Raquete lowerPaddle,
            Players player1,
            Players player2,
            int boardWidth,
            int boardHeight) {

        this.terrain = terrain;
        this.cellSize = cellSize;
        this.ball = ball;
        this.upperPaddle = upperPaddle;
        this.lowerPaddle = lowerPaddle;
        this.player1 = player1;
        this.player2 = player2;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    // ---------------------------
    // PUBLIC API (used by server)
    // ---------------------------

    // Called from game loop (local or server)
    public void update() {
        if (gameOver) {
            return;
        }

        // paddles stay inside board
        upperPaddle.clamp(boardWidth);
        lowerPaddle.clamp(boardWidth);

        // ball movement + clamp
        ball.update(boardWidth, boardHeight);
        ball.clamp(boardWidth, boardHeight);

        // collisions
        checkPieceCollisions();
        checkPaddleCollisions();
    }

    // Control methods (called from input)
    public void moveUpperLeft() {
        upperPaddle.moveLeft();
    }

    public void moveUpperRight() {
        upperPaddle.moveRight();
    }

    public void moveLowerLeft() {
        lowerPaddle.moveLeft();
    }

    public void moveLowerRight() {
        lowerPaddle.moveRight();
    }

    // Build a serializable snapshot for clients / UI
    public GameState buildState() {

        GameState state = new GameState();

        state.boardSizex = terrain.getSizex();
        state.boardSizey = terrain.getSizey();
        state.cellSize = this.cellSize;
        state.boardWidth = this.boardWidth;
        state.boardHeight = this.boardHeight;

        // ball
        state.ballX = ball.getX();
        state.ballY = ball.getY();
        state.ballDiameter = ball.getBounds().width;

        // paddles
        state.upperX = upperPaddle.getX();
        state.upperY = upperPaddle.getY();
        state.upperWidth = upperPaddle.getWidth();
        state.upperHeight = upperPaddle.getHeight();

        state.lowerX = lowerPaddle.getX();
        state.lowerY = lowerPaddle.getY();
        state.lowerWidth = lowerPaddle.getWidth();
        state.lowerHeight = lowerPaddle.getHeight();

        // players
        state.player1Name = player1.getName();
        state.player2Name = player2.getName();
        state.player1Score = player1.getScore();
        state.player2Score = player2.getScore();

        // pieces
        List<PieceState> list = new ArrayList<>();
        int size = terrain.getSizex();
        int sizey = terrain.getSizey();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < sizey; y++) { // fix 8 ny axe y @zay tsy miova ny longeur fa ny largeur ihany
                Piece p = terrain.getPiece(x, y);
                if (p != null) {
                    PieceState ps = new PieceState();
                    ps.x = x;
                    ps.y = y;
                    ps.name = p.getName();
                    ps.life = p.getlife();
                    ps.ownerId = (p.getOwner() == player1) ? 1 : 2;
                    list.add(ps);
                }
            }
        }
        state.pieces = list;

        // game over
        state.gameOver = gameOver;
        state.winnerName = (winner != null) ? winner.getName() : null;

        return state;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Players getWinner() {
        return winner;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Ball getBall() {
        return ball;
    }

    public Raquete getUpperPaddle() {
        return upperPaddle;
    }

    public Raquete getLowerPaddle() {
        return lowerPaddle;
    }

    // ---------------------------
    // INTERNAL LOGIC
    // ---------------------------

    private void checkPieceCollisions() {
        Rectangle b = ball.getBounds();
        int size = terrain.getSizex();
        int sizey = terrain.getSizey();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < sizey; y++) {

                Piece p = terrain.getPiece(x, y);
                if (p != null) {

                    int px = x * cellSize;
                    int py = y * cellSize;

                    Rectangle square = new Rectangle(px, py, cellSize, cellSize);

                    if (b.intersects(square)) {

                        long now = System.currentTimeMillis();
                        if (now - ball.lastHitTime < 80) {
                            continue; // prevent double-hit
                        }
                        ball.lastHitTime = now;

                        System.out.println(
                                "Ball hit piece: " + p.getName() +
                                        " at (" + x + "," + y + ") " +
                                        "HP before = " + p.getlife());

                        int maxscore = 20;
                        if (p.getOwner() == player1) {
                            player2.setScore(player2.getScore() + 1);
                            if (player2.getScore() >= maxscore && p.getlife() >= 3) {
                                p.decreaseLife(3);
                            } else {
                                // Ball hits this piece
                                p.decreaseLife(1);
                            }
                        } else if (p.getOwner() == player2) {
                            player1.setScore(player1.getScore() + 1);
                            if (player1.getScore() >= maxscore && p.getlife() >= 3) {
                                p.decreaseLife(3);
                            } else {
                                // Ball hits this piece
                                p.decreaseLife(1);
                            }
                        }
                        if (player1.getScore() == maxscore) {
                            p.decreaseLife(3);
                        }

                        System.out.println("HP after = " + p.getlife());

                        if (p.getlife() <= 0) {
                            System.out.println("Piece " + p.getName() + " at (" + x + "," + y + ") DESTROYED!");

                            if (p instanceof King king) {
                                // king owner loses
                                playerLose(king.getOwner());
                            }
                            terrain.removePiece(x, y);
                        }

                        // bounce on piece
                        ballBounce(b, square);

                        // update rect position after bouncing
                        b = ball.getBounds();
                    }
                }
            }
        }
    }

    private void checkPaddleCollisions() {
        Rectangle b = ball.getBounds();

        // UPPER
        Rectangle upperRect = upperPaddle.getBounds();
        if (b.intersects(upperRect)) {
            System.out.println("BALL HIT UPPER PADDLE");
            bounceOnPaddle(b, upperPaddle, true);
            b = ball.getBounds();
        }

        // LOWER
        Rectangle lowerRect = lowerPaddle.getBounds();
        if (b.intersects(lowerRect)) {
            System.out.println("BALL HIT LOWER PADDLE");
            bounceOnPaddle(b, lowerPaddle, false);
        }
    }

    // SIDE-BASED bounce on generic rectangles (pieces, etc.)
    private void ballBounce(Rectangle ballRect, Rectangle hitRect) {
        try {
            Field dxField = Ball.class.getDeclaredField("dx");
            Field dyField = Ball.class.getDeclaredField("dy");

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
                    ball.setX(hitRect.x + hitRect.width + 1);
                } else {
                    // hit right side → move ball to the left
                    ball.setX(hitRect.x - ballRect.width - 1);
                }
            }

            // VERTICAL BOUNCE
            if (vertical) {
                dy = -dy;

                if (diffY > 0) {
                    // hit top → push ball down
                    ball.setY(hitRect.y + hitRect.height + 1);
                } else {
                    // hit bottom → push ball up
                    ball.setY(hitRect.y - ballRect.height - 1);
                }
            }

            dxField.setInt(ball, dx);
            dyField.setInt(ball, dy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Special paddle bounce (front/back logic with spin)
    private void bounceOnPaddle(Rectangle b, Raquete paddle, boolean isUpper) {
        try {
            Field dxField = Ball.class.getDeclaredField("dx");
            Field dyField = Ball.class.getDeclaredField("dy");

            dxField.setAccessible(true);
            dyField.setAccessible(true);

            int dx = dxField.getInt(ball);
            int dy = dyField.getInt(ball);

            int ballCenterY = b.y + b.height / 2;

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
                dx = -dx;
                dy = -dy;
            }

            dxField.setInt(ball, dx);
            dyField.setInt(ball, dy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playerLose(Players loser) {
        gameOver = true;
        winner = (loser == player1) ? player2 : player1;
        System.out.println("GAME OVER: " + winner.getName() + " WINS (king of "
                + loser.getName() + " destroyed)");
    }
}
