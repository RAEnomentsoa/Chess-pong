package net.core;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {

    // ball
    public int ballX;
    public int ballY;
    public int ballDiameter;

    // paddles
    public int upperX, upperY, upperWidth, upperHeight;
    public int lowerX, lowerY, lowerWidth, lowerHeight;

    // pieces
    public List<PieceState> pieces;

    // players
    public String player1Name;
    public String player2Name;
    public int player1Score;
    public int player2Score;

    // game over
    public boolean gameOver = false;
    public String winnerName;
}
