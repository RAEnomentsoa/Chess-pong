package entities;

public class Terrain {

    private int sizex; // board size (ex: 4x4, 6x6, 8x8)
    private Piece[][] grid; // the board
    private final int sizey = 8; // board size y fix 8

    // ---------------------------
    // Constructor
    // ---------------------------
    public Terrain(int sizex) {
        if (sizex <= 0) {
            throw new IllegalArgumentException("Size must be > 0");
        }

        // fix 8 @ zay afaka ahena ny largeur
        this.sizex = sizex;
        this.grid = new Piece[sizex][sizey];
    }

    // ---------------------------
    // Get board size
    // ---------------------------
    public int getSizex() {
        return sizex;
    }

    public int getSizey() {
        return sizey;
    }

    // ---------------------------
    // Check if coordinates are valid
    // ---------------------------
    public boolean isInside(int x, int y) {
        return x >= 0 && y >= 0 && x < sizex && y < sizey;
    }

    // ---------------------------
    // Get a piece at a location
    // ---------------------------
    public Piece getPiece(int x, int y) {
        if (!isInside(x, y))
            return null;
        return grid[x][y];
    }

    // ---------------------------
    // Place a piece on the board
    // ---------------------------
    public boolean placePiece(int x, int y, Piece piece) {
        if (!isInside(x, y)) {

            System.out.println("REJECTED piece at x=" + x + " y=" + y);
            return false;
        }

        grid[x][y] = piece;
        return true;
    }

    // ---------------------------
    // Remove piece (set cell empty)
    // ---------------------------
    public boolean removePiece(int x, int y) {
        if (!isInside(x, y))
            return false;

        grid[x][y] = null;
        return true;
    }

    // ---------------------------
    // Clear board
    // ---------------------------
    public void reset() {
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {
                grid[i][j] = null;
            }
        }
    }
}
