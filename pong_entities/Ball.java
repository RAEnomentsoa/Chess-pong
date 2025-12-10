package pong_entities;

import java.awt.*;

public class Ball {
    private int x, y;
    private int diameter;
    private int dx = 4;
    private int dy = 4;
    public long lastHitTime = 0;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Ball(int x, int y, int diameter) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
    }

    public void clamp(int maxWidth, int maxHeight) {
        if (x < 0)
            x = 0;
        if (x + diameter > maxWidth)
            x = maxWidth - diameter;

        if (y < 0)
            y = 0;
        if (y + diameter > maxHeight)
            y = maxHeight - diameter;
    }

    public void update(int maxWidth, int maxHeight) {
        x += dx;
        y += dy;

        // bounce on edges
        if (x <= 0 || x + diameter >= maxWidth)
            dx = -dx;
        if (y <= 0 || y + diameter >= maxHeight)
            dy = -dy;
    }

    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, diameter, diameter);
    }

    // collision rectangle
    public Rectangle getBounds() {
        return new Rectangle(x, y, diameter, diameter);
    }
}
