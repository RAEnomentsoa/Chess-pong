package entities;

public class Terrain {

    private int size; // board size (ex: 4x4, 6x6, 8x8)
    private Piece[][] grid; // the board

    // ---------------------------
    // Constructor
    // ---------------------------
    public Terrain(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be > 0");
        }

        this.size = size;
        this.grid = new Piece[size][size];
    }

    // ---------------------------
    // Get board size
    // ---------------------------
    public int getSize() {
        return size;
    }

    // ---------------------------
    // Check if coordinates are valid
    // ---------------------------
    public boolean isInside(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < size;
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
        if (!isInside(x, y))
            return false;

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
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = null;
            }
        }
    }
}
