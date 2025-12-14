package net.server;

import javax.swing.SwingUtilities;

public class ServerLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameSetupFrame().setVisible(true);
        });
    }
}
