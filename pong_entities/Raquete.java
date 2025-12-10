package pong_entities;

import java.awt.*;

public class Raquete {
    private int x, y; // paddle position
    private int width;
    private int height;
    private int speed = 10; // movement speed
    private Color color;

    public Raquete(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    // movement up+down
    // public void moveUp() {
    // y -= speed;
    // }

    // public void moveDown() {
    // y += speed;
    // }

    // movement left+right
    public void moveLeft() {
        x -= speed;
    }

    public void moveRight() {
        x += speed;
    }

    public void clamp(int panelWidth) {
        if (x < 0)
            x = 0;
        if (x + width > panelWidth)
            x = panelWidth - width;
    }

    // draw paddle
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    // collision rectangle
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
