package net.core;

import java.io.Serializable;

public class PieceState implements Serializable {
    public int x; // grid X (0–7)
    public int y; // grid Y (0–7)
    public String name; // "King", "Pawn", etc.
    public int life;
    public int ownerId;
}
