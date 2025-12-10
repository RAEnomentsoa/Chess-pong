package entities;

import Users.Players;

public class Piece {
    protected final String Name;
    protected int life;
    protected Players owner;

    public String getName() {
        return Name;
    }

    public int getlife() {
        return life;
    }

    public void setlife(int life) {
        this.life = life;
    }

    public Players getOwner() {
        return owner;
    }

    public void setOwner(Players owner) {
        this.owner = owner;
    }

    public Piece(String Name, int life, Players owner) {
        this.Name = Name;
        this.setlife(life);
        this.owner = owner;
    }

    public void decreaseLife(int amount) {
        this.setlife(this.getlife() - amount);
        if (this.getlife() < 0) {
            this.setlife(0);
        }
    }

    public void increaseLife(int amount) {
        this.setlife(this.getlife() + amount);
    }

    public boolean isAlive() {
        return this.life > 0;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "Name='" + Name + '\'' +
                ", life=" + life +
                '}';
    }

}