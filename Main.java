import javax.swing.SwingUtilities;
import GUI.*;
import entities.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TerrainWindow());
    }
}
