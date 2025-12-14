package net.server;

import net.core.GameLogic;
import net.core.GameState;

import entities.Terrain;
import entities.Bishop;
import entities.King;
import entities.Knight;
import entities.Pawn;
import entities.Queen;
import entities.Rook;

import pong_entities.Ball;
import pong_entities.Raquete;
import Users.Players;

import java.awt.Color;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    private ServerSocket serverSocket;

    private Socket p1Socket;
    private Socket p2Socket;

    private ObjectOutputStream p1Out;
    private ObjectOutputStream p2Out;

    private ObjectInputStream p1In;
    private ObjectInputStream p2In;

    private GameLogic logic;

    private boolean running = true;

    public GameServer(int port, int size, PieceLifeConfig life) throws Exception {

        serverSocket = new ServerSocket(port);
        System.out.println("[SERVER] Started on port " + port);

        System.out.println("[SERVER] Waiting for Player 1...");
        p1Socket = serverSocket.accept();
        System.out.println("[SERVER] Player 1 connected from " + p1Socket.getInetAddress());

        System.out.println("[SERVER] Waiting for Player 2...");
        p2Socket = serverSocket.accept();
        System.out.println("[SERVER] Player 2 connected from " + p2Socket.getInetAddress());

        // IMPORTANT: create ObjectOutputStream BEFORE ObjectInputStream
        p1Out = new ObjectOutputStream(p1Socket.getOutputStream());
        p2Out = new ObjectOutputStream(p2Socket.getOutputStream());

        p1In = new ObjectInputStream(p1Socket.getInputStream());
        p2In = new ObjectInputStream(p2Socket.getInputStream());

        System.out.println("Waiting for player 1 name...");
        String p1Name = ((String) p1In.readObject()).replace("NAME:", "");

        System.out.println("Waiting for player 2 name...");
        String p2Name = ((String) p2In.readObject()).replace("NAME:", "");

        // init game (terrain, pieces, ball, paddles, players)
        setupGame(p1Name, p2Name, size, life);

        // send initial state so clients don't block / show empty
        sendInitialState();

        // start input + game loop
        startInputThreads();
        startGameLoop();
    }

    // not responsive at the moment

    private void setupGame(String p1Name, String p2Name, int size, PieceLifeConfig life) {

        int cellSize = 60;
        // int size = 4;
        int sizey = 8;
        // fix 8 ny x @ zay largeur ihany no mihena
        int boardWidth = size * cellSize;
        int boardHeight = sizey * cellSize;

        Terrain terrain = new Terrain(size);

        Ball ball = new Ball(200, 200, 20);

        // paddles
        Raquete upper = new Raquete(100, 150, 20 * size, 20, Color.GREEN);
        Raquete lower = new Raquete(100, 300, 20 * size, 20, Color.BLACK);

        // players
        Players player1 = new Players(1, p1Name, 0, "Male");
        Players player2 = new Players(2, p2Name, 0, "Male");

        // --------------------
        // BLACK BACK RANK
        // --------------------
        if (size == 8) {
            terrain.placePiece(0, 0, new Rook(life.rook, player1));
            terrain.placePiece(1, 0, new Knight(life.knight, player1));
            terrain.placePiece(2, 0, new Bishop(life.bishop, player1));
            terrain.placePiece(3, 0, new Queen(life.queen, player1));
            terrain.placePiece(4, 0, new King(life.king, player1));
            terrain.placePiece(5, 0, new Bishop(life.bishop, player1));
            terrain.placePiece(6, 0, new Knight(life.knight, player1));
            terrain.placePiece(7, 0, new Rook(life.rook, player1));
        }
        if (size == 6) {
            terrain.placePiece(0, 0, new Knight(life.knight, player1));
            terrain.placePiece(1, 0, new Bishop(life.bishop, player1));
            terrain.placePiece(2, 0, new King(life.king, player1));
            terrain.placePiece(3, 0, new Queen(life.queen, player1));
            terrain.placePiece(4, 0, new Bishop(life.bishop, player1));
            terrain.placePiece(5, 0, new Knight(life.knight, player1));
        }

        if (size == 4) {
            terrain.placePiece(0, 0, new Bishop(life.bishop, player1));
            terrain.placePiece(1, 0, new King(life.king, player1));
            terrain.placePiece(2, 0, new Queen(life.queen, player1));
            terrain.placePiece(3, 0, new Bishop(life.bishop, player1));
        }
        if (size == 2) {
            terrain.placePiece(0, 0, new King(life.king, player1));
            terrain.placePiece(1, 0, new Queen(life.queen, player1));
        }

        // --------------------
        // BLACK PAWNS
        // --------------------
        for (int x = 0; x < size; x++) {
            terrain.placePiece(x, 1, new Pawn(life.pawn, player1));
        }

        // --------------------
        // WHITE PAWNS
        // --------------------
        for (int x = 0; x < size; x++) {
            terrain.placePiece(x, 6, new Pawn(life.pawn, player2));
        }

        // --------------------
        // WHITE BACK RANK
        // --------------------
        // --------------------
        if (size == 8) {
            terrain.placePiece(0, 7, new Rook(life.rook, player2));
            terrain.placePiece(1, 7, new Knight(life.knight, player2));
            terrain.placePiece(2, 7, new Bishop(life.bishop, player2));
            terrain.placePiece(3, 7, new Queen(life.queen, player2));
            terrain.placePiece(4, 7, new King(life.king, player2));
            terrain.placePiece(5, 7, new Bishop(life.bishop, player2));
            terrain.placePiece(6, 7, new Knight(life.king, player2));
            terrain.placePiece(7, 7, new Rook(life.rook, player2));
        }
        if (size == 6) {
            terrain.placePiece(0, 7, new Knight(life.knight, player2));
            terrain.placePiece(1, 7, new Bishop(life.bishop, player2));
            terrain.placePiece(2, 7, new King(life.king, player2));
            terrain.placePiece(3, 7, new Queen(life.queen, player2));
            terrain.placePiece(4, 7, new Bishop(life.bishop, player2));
            terrain.placePiece(5, 7, new Knight(life.knight, player2));
        }

        if (size == 4) {
            terrain.placePiece(0, 7, new Bishop(life.bishop, player2));
            terrain.placePiece(1, 7, new King(life.king, player2));
            terrain.placePiece(2, 7, new Queen(life.queen, player2));
            terrain.placePiece(3, 7, new Bishop(life.bishop, player2));
        }
        if (size == 2) {
            terrain.placePiece(0, 7, new King(life.king, player2));
            terrain.placePiece(1, 7, new Queen(life.queen, player2));
        }

        logic = new GameLogic(
                terrain,
                cellSize,
                ball,
                upper,
                lower,
                player1,
                player2,
                boardWidth,
                boardHeight);

        System.out.println("[SERVER] Game initialized");
    }

    // // responsive
    // private void setupGame(String p1Name, String p2Name) {

    // int size = 2;
    // int cellSize = 60;

    // int boardWidth = size * cellSize;
    // int boardHeight = size * cellSize;

    // Terrain terrain = new Terrain(size);

    // // ===========================
    // // BALL (dynamic scaling)
    // // ===========================
    // int ballSize = Math.max(cellSize / 3, 10);
    // Ball ball = new Ball(
    // boardWidth / 2 - ballSize / 2,
    // boardHeight / 2 - ballSize / 2,
    // ballSize);

    // // ===========================
    // // PLAYERS
    // // ===========================
    // Players player1 = new Players(1, p1Name, 0, "Male");
    // Players player2 = new Players(2, p2Name, 0, "Male");

    // // ===========================
    // // ADAPTIVE ROWS (works for any board size)
    // // ===========================
    // int p1Back = 0;
    // int p1Pawns = 1;

    // int p2Back = 7;
    // int p2Pawns = 6;

    // // ===========================
    // // PADDLE POSITIONS (DEPEND ON PAWN ROWS)
    // // ===========================
    // int upperPaddleRow = p1Pawns + 1; // directly above pawns
    // int lowerPaddleRow = p2Pawns - 1; // directly below pawns

    // // ===========================
    // // PADDLES (FULLY DYNAMIC)
    // // ===========================
    // int paddleWidth = cellSize * 2;
    // int paddleHeight = cellSize / 4;

    // int upperY = upperPaddleRow * cellSize;
    // int lowerY = lowerPaddleRow * cellSize;

    // Raquete upper = new Raquete(
    // boardWidth / 2 - paddleWidth / 2,
    // upperY,
    // paddleWidth,
    // paddleHeight,
    // Color.GREEN);

    // Raquete lower = new Raquete(
    // boardWidth / 2 - paddleWidth / 2,
    // lowerY,
    // paddleWidth,
    // paddleHeight,
    // Color.BLACK);

    // // ===========================
    // // PIECE LAYOUT (adaptive)
    // // ===========================
    // placeBackRank(terrain, p1Back, player1);
    // placePawns(terrain, p1Pawns, size, player1);

    // placeBackRank(terrain, p2Back, player2);
    // placePawns(terrain, p2Pawns, size, player2);

    // // ===========================
    // // GAME LOGIC
    // // ===========================
    // logic = new GameLogic(
    // terrain,
    // cellSize,
    // ball,
    // upper,
    // lower,
    // player1,
    // player2,
    // boardWidth,
    // boardHeight);

    // System.out.println("[SERVER] Game initialized (DYNAMIC WITH CORRECT
    // PADDLES)");
    // }

    private void placeBackRank(Terrain t, int row, Players p) {
        t.placePiece(0, row, new Rook(6, p));
        t.placePiece(1, row, new Knight(5, p));
        t.placePiece(2, row, new Bishop(8, p));
        t.placePiece(3, row, new Queen(12, p));
        t.placePiece(4, row, new King(10, p));
        t.placePiece(5, row, new Bishop(8, p));
        t.placePiece(6, row, new Knight(5, p));
        t.placePiece(7, row, new Rook(6, p));
    }

    private void placePawns(Terrain t, int row, int size, Players p) {
        for (int x = 0; x < size; x++)
            t.placePiece(x, row, new Pawn(3, p));
    }

    // Send first GameState so client GUI has something to draw ASAP
    private void sendInitialState() {
        try {
            GameState initial = logic.buildState();

            System.out.println("[SERVER] Sending initial GameState...");
            p1Out.writeObject(initial);
            p1Out.flush();

            p2Out.writeObject(initial);
            p2Out.flush();

            System.out.println("[SERVER] Initial GameState sent.");
        } catch (IOException e) {
            System.out.println("[SERVER] Failed to send initial state");
            e.printStackTrace();
        }
    }

    private void startInputThreads() {

        // Player 1 controls UPPER paddle
        new Thread(() -> {
            try {
                while (running) {
                    Object obj = p1In.readObject();
                    if (!(obj instanceof String cmd))
                        continue;

                    switch (cmd) {
                        case "LEFT" -> logic.moveUpperLeft();
                        case "RIGHT" -> logic.moveUpperRight();
                    }
                }
            } catch (Exception e) {
                System.out.println("[SERVER] Player 1 disconnected / input thread stopped");
                e.printStackTrace();
                running = false;
            }
        }, "P1-Input-Thread").start();

        // Player 2 controls LOWER paddle
        new Thread(() -> {
            try {
                while (running) {
                    Object obj = p2In.readObject();
                    if (!(obj instanceof String cmd))
                        continue;

                    switch (cmd) {
                        case "LEFT" -> logic.moveLowerLeft();
                        case "RIGHT" -> logic.moveLowerRight();
                    }
                }
            } catch (Exception e) {
                System.out.println("[SERVER] Player 2 disconnected / input thread stopped");
                e.printStackTrace();
                running = false;
            }
        }, "P2-Input-Thread").start();
    }

    private void startGameLoop() {
        new Thread(() -> {
            try {
                while (running) {

                    logic.update(); // update physics & collisions
                    GameState state = logic.buildState();

                    // broadcast to both players
                    try {
                        p1Out.reset();
                        p1Out.writeObject(state);
                        p1Out.flush();
                    } catch (IOException e) {
                        System.out.println("[SERVER] Failed to send state to Player 1");
                        e.printStackTrace();
                        running = false;
                    }

                    try {
                        p2Out.reset();
                        p2Out.writeObject(state);
                        p2Out.flush();
                    } catch (IOException e) {
                        System.out.println("[SERVER] Failed to send state to Player 2");
                        e.printStackTrace();
                        running = false;
                    }

                    // when game over → stop
                    if (state.gameOver) {
                        running = false;
                        System.out.println("[SERVER] Game finished! Winner: " + state.winnerName);
                    }

                    Thread.sleep(16); // ~60 FPS
                }

                closeAll();

            } catch (Exception e) {
                System.out.println("[SERVER] Game loop crashed");
                e.printStackTrace();
                running = false;
                closeAll();
            }
        }, "GameLoop-Thread").start();
    }

    private void closeAll() {
        try {
            if (p1In != null)
                p1In.close();
        } catch (IOException ignored) {
        }
        try {
            if (p2In != null)
                p2In.close();
        } catch (IOException ignored) {
        }
        try {
            if (p1Out != null)
                p1Out.close();
        } catch (IOException ignored) {
        }
        try {
            if (p2Out != null)
                p2Out.close();
        } catch (IOException ignored) {
        }
        try {
            if (p1Socket != null && !p1Socket.isClosed())
                p1Socket.close();
        } catch (IOException ignored) {
        }
        try {
            if (p2Socket != null && !p2Socket.isClosed())
                p2Socket.close();
        } catch (IOException ignored) {
        }
        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
        } catch (IOException ignored) {
        }

        System.out.println("[SERVER] All connections closed.");
    }

}
