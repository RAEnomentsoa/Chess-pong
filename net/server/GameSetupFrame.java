package net.server;

import javax.swing.*;
import java.awt.*;
import net.server.EJB.GameConfigEntity;
import net.server.EJB.GameConfigServiceRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class GameSetupFrame extends JFrame {

    private JComboBox<Integer> sizeBox;
    private PiecePreviewPanel preview;
    private JSpinner kingLife, queenLife, rookLife, bishopLife, knightLife, pawnLife;
    private JPanel kingRow, queenRow, rookRow, bishopRow, knightRow, pawnRow;
    private JPanel lifePanel;

    public GameSetupFrame() {
        setTitle("Chess Pong – Game Setup");
        setSize(420, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    // connection EJB
    private GameConfigServiceRemote lookupEJB() throws Exception {

        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL,
                "http-remoting://localhost:8080");

        Context ctx = new InitialContext(props);

        return (GameConfigServiceRemote) ctx.lookup(
                "ejb:/chess-pong-ejb/GameConfigServiceBean!" +
                        "net.server.EJB.GameConfigServiceRemote");
    }

    // save to EJB
    private void saveConfigToEJB(int size, PieceLifeConfig life) {
        try {
            GameConfigServiceRemote service = lookupEJB();

            GameConfigEntity e = new GameConfigEntity();
            e.setSize(size);
            e.setKingLife(life.king);
            e.setQueenLife(life.queen);
            e.setRookLife(life.rook);
            e.setBishopLife(life.bishop);
            e.setKnightLife(life.knight);
            e.setPawnLife(life.pawn);

            service.save(e);

            System.out.println("[SETUP] Config saved to EJB");

        } catch (Exception ex) {
            System.out.println("[SETUP] Failed to save config to EJB");
            ex.printStackTrace();
        }
    }

    // peice life config ejb
    private PieceLifeConfig loadConfigFromEJB() {
        try {
            GameConfigServiceRemote service = lookupEJB();
            GameConfigEntity e = service.loadLast();

            PieceLifeConfig life = new PieceLifeConfig();
            life.king = e.getKingLife();
            life.queen = e.getQueenLife();
            life.rook = e.getRookLife();
            life.bishop = e.getBishopLife();
            life.knight = e.getKnightLife();
            life.pawn = e.getPawnLife();

            sizeBox.setSelectedItem(e.getSize());

            kingLife.setValue(life.king);
            queenLife.setValue(life.queen);
            rookLife.setValue(life.rook);
            bishopLife.setValue(life.bishop);
            knightLife.setValue(life.knight);
            pawnLife.setValue(life.pawn);

            System.out.println("[SETUP] Config loaded from EJB");

            return life;

        } catch (Exception ex) {
            System.out.println("[SETUP] Failed to load config from EJB");
            ex.printStackTrace();
            return null;
        }
    }

    private JComponent createHeader() {
        JLabel title = new JLabel("GAME SETUP", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return title;
    }

    private JComponent createCenter() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        top.add(new JLabel("Board size:"));

        sizeBox = new JComboBox<>(new Integer[] { 2, 4, 6, 8 });
        sizeBox.setSelectedItem(4);
        top.add(sizeBox);

        panel.add(top, BorderLayout.NORTH);

        panel.add(createLifePanel(), BorderLayout.CENTER);

        lifePanel.add(pawnLife);

        panel.add(lifePanel, BorderLayout.CENTER);

        updateLifeVisibility(4); // default

        sizeBox.addActionListener(e -> {
            int size = (int) sizeBox.getSelectedItem();
            // preview.setSize(size);
            updateLifeVisibility(size);
        });

        return panel;
    }

    private JComponent createLifePanel() {
        lifePanel = new JPanel();
        lifePanel.setLayout(new BoxLayout(lifePanel, BoxLayout.Y_AXIS));
        lifePanel.setBorder(BorderFactory.createTitledBorder("Piece Life"));

        kingLife = spinner(10);
        queenLife = spinner(12);
        rookLife = spinner(6);
        bishopLife = spinner(8);
        knightLife = spinner(5);
        pawnLife = spinner(3);

        kingRow = row("King", kingLife);
        queenRow = row("Queen", queenLife);
        rookRow = row("Rook", rookLife);
        bishopRow = row("Bishop", bishopLife);
        knightRow = row("Knight", knightLife);
        pawnRow = row("Pawn", pawnLife);

        lifePanel.add(kingRow);
        lifePanel.add(queenRow);
        lifePanel.add(rookRow);
        lifePanel.add(bishopRow);
        lifePanel.add(knightRow);
        lifePanel.add(pawnRow);

        return lifePanel;
    }

    private JPanel row(String name, JSpinner spinner) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(name));
        p.add(spinner);
        p.add(new JLabel("HP"));
        return p;
    }

    private JSpinner spinner(int value) {
        return new JSpinner(new SpinnerNumberModel(value, 1, 99, 1));
    }

    private void updateLifeVisibility(int size) {

        // always visible
        kingRow.setVisible(true);
        queenRow.setVisible(true);
        pawnRow.setVisible(true);

        bishopRow.setVisible(size >= 4);
        knightRow.setVisible(size >= 6);
        rookRow.setVisible(size >= 8);

        lifePanel.revalidate();
        lifePanel.repaint();
    }

    private JComponent createFooter() {
        JButton start = new JButton("START GAME");
        start.setFont(new Font("Arial", Font.BOLD, 16));

        // start.addActionListener(e -> {
        // int size = (int) sizeBox.getSelectedItem();

        // PieceLifeConfig life = new PieceLifeConfig();
        // life.king = (int) kingLife.getValue();
        // life.queen = (int) queenLife.getValue();
        // life.rook = (int) rookLife.getValue();
        // life.bishop = (int) bishopLife.getValue();
        // life.knight = (int) knightLife.getValue();
        // life.pawn = (int) pawnLife.getValue();

        // dispose();

        // new Thread(() -> {
        // try {
        // new GameServer(5000, size, life);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
        // }).start();
        // });

        // -----------------------------------------------------mande tsisy egb
        // start.addActionListener(e -> {

        // int size = (int) sizeBox.getSelectedItem();

        // PieceLifeConfig initialLife = new PieceLifeConfig();
        // initialLife.king = (int) kingLife.getValue();
        // initialLife.queen = (int) queenLife.getValue();
        // initialLife.rook = (int) rookLife.getValue();
        // initialLife.bishop = (int) bishopLife.getValue();
        // initialLife.knight = (int) knightLife.getValue();
        // initialLife.pawn = (int) pawnLife.getValue();

        // // 1️⃣ SAVE GUI → EJB
        // saveConfigToEJB(size, initialLife);

        // // 2️⃣ LOAD BACK FROM EJB (proof + sync)
        // PieceLifeConfig fromEJB = loadConfigFromEJB();
        // final int finalSize;
        // final PieceLifeConfig finalLife;
        // if (fromEJB != null) {
        // finalLife = fromEJB;
        // finalSize = (int) sizeBox.getSelectedItem();
        // } else {
        // finalLife = initialLife;
        // finalSize = size;
        // }

        // dispose();

        // new Thread(() -> {
        // try {
        // new GameServer(5000, finalSize, finalLife);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
        // }).start();
        // });

        start.addActionListener(evt -> {

            try {
                // EJB IS REQUIRED
                GameConfigServiceRemote service = lookupEJB();
                int size = (int) sizeBox.getSelectedItem();

                GameConfigEntity entity = new GameConfigEntity();
                entity.setSize(size);
                entity.setKingLife((int) kingLife.getValue());
                entity.setQueenLife((int) queenLife.getValue());
                entity.setRookLife((int) rookLife.getValue());
                entity.setBishopLife((int) bishopLife.getValue());
                entity.setKnightLife((int) knightLife.getValue());
                entity.setPawnLife((int) pawnLife.getValue());

                // 1️⃣ SAVE GUI → EJB
                service.save(entity);
                GameConfigEntity e = service.loadLast();

                // ny any @ ejb no ampiasaina raha ohatra ka misy
                kingLife.setEnabled(false);
                queenLife.setEnabled(false);
                rookLife.setEnabled(false);
                bishopLife.setEnabled(false);
                knightLife.setEnabled(false);
                pawnLife.setEnabled(false);
                sizeBox.setEnabled(false);

                if (e == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No configuration found in EJB",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Build config ONLY from EJB
                PieceLifeConfig life = new PieceLifeConfig();
                life.king = e.getKingLife();
                life.queen = e.getQueenLife();
                life.rook = e.getRookLife();
                life.bishop = e.getBishopLife();
                life.knight = e.getKnightLife();
                life.pawn = e.getPawnLife();

                int size2 = e.getSize();

                dispose();

                new Thread(() -> {
                    try {
                        new GameServer(5000, size2, life);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "EJB / WildFly is not available.\nServer cannot start.",
                        "EJB REQUIRED",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panel = new JPanel();
        panel.add(start);
        return panel;
    }
}
