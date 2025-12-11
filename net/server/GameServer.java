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

    public GameServer(int port) throws Exception {
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
        setupGame(p1Name, p2Name);

        // send initial state so clients don't block / show empty
        sendInitialState();

        // start input + game loop
        startInputThreads();
        startGameLoop();
    }

    private void setupGame(String p1Name, String p2Name) {

        int cellSize = 60;
        int size = 8;
        int boardWidth = size * cellSize;
        int boardHeight = size * cellSize;

        Terrain terrain = new Terrain(size);

        Ball ball = new Ball(200, 200, 20);

        // paddles
        Raquete upper = new Raquete(100, 150, 100, 20, Color.GREEN);
        Raquete lower = new Raquete(100, 300, 100, 20, Color.BLACK);

        // players
        Players player1 = new Players(1, p1Name, 0, "Male");
        Players player2 = new Players(2, p2Name, 0, "Male");

        // --------------------
        // BLACK BACK RANK
        // --------------------
        terrain.placePiece(0, 0, new Rook(6, player1));
        terrain.placePiece(1, 0, new Knight(5, player1));
        terrain.placePiece(2, 0, new Bishop(8, player1));
        terrain.placePiece(3, 0, new Queen(12, player1));
        terrain.placePiece(4, 0, new King(10, player1));
        terrain.placePiece(5, 0, new Bishop(8, player1));
        terrain.placePiece(6, 0, new Knight(5, player1));
        terrain.placePiece(7, 0, new Rook(6, player1));

        // --------------------
        // BLACK PAWNS
        // --------------------
        for (int x = 0; x < 8; x++) {
            terrain.placePiece(x, 1, new Pawn(3, player1));
        }

        // --------------------
        // WHITE PAWNS
        // --------------------
        for (int x = 0; x < 8; x++) {
            terrain.placePiece(x, 6, new Pawn(3, player2));
        }

        // --------------------
        // WHITE BACK RANK
        // --------------------
        terrain.placePiece(0, 7, new Rook(6, player2));
        terrain.placePiece(1, 7, new Knight(5, player2));
        terrain.placePiece(2, 7, new Bishop(8, player2));
        terrain.placePiece(3, 7, new Queen(12, player2));
        terrain.placePiece(4, 7, new King(10, player2));
        terrain.placePiece(5, 7, new Bishop(8, player2));
        terrain.placePiece(6, 7, new Knight(5, player2));
        terrain.placePiece(7, 7, new Rook(6, player2));

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

    public static void main(String[] args) throws Exception {
        new GameServer(5000);
    }
}
