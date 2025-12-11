package net.client;

import net.core.GameState;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class GameClient {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String playerName;

    private TerrainPanelNetwork panel;

    public GameClient(String host, int port, String playerName) throws Exception {

        System.out.println("🔌 Connecting to server...");
        socket = new Socket(host, port);
        System.out.println("✅ Connected!");

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        // send name to server
        out.writeObject("NAME:" + playerName);
        out.flush();

        panel = new TerrainPanelNetwork(this);

        JFrame frame = new JFrame("Chess Pong - Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        startReceiver();
    }

    private void startReceiver() {

        new Thread(() -> {
            try {
                while (true) {
                    GameState state = (GameState) in.readObject();
                    panel.updateState(state);
                }
            } catch (Exception e) {
                System.out.println("❌ Disconnected from server");
            }
        }).start();
    }

    public void sendInput(String cmd) {
        try {
            out.writeObject(cmd);
            out.flush();
        } catch (Exception e) {
            System.out.println("Failed to send input");
        }
    }

    public static void main(String[] args) throws Exception {

        String name = JOptionPane.showInputDialog("Enter your player name:");

        if (name == null || name.isBlank()) {
            name = "Player";
        }
        new GameClient("127.0.0.1", 5000, name);
    }
}
