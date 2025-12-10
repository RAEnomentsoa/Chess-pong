package entities;

public class Piece {
    protected final String Name;
    protected int life;

    public String getName() {
        return Name;
    }

    public int getlife() {
        return life;
    }

    public void setlife(int life) {
        this.life = life;
    }

    public Piece(String Name, int life) {
        this.Name = Name;
        this.setlife(life);
    }

    public void decreaseLife(int amount) {
        this.setlife(amount - amount);
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