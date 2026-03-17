package dino.entity;

import java.awt.Image;

public class Block {

    public Image image;
    public int x, y, width, height;
    public int animFrame = -1;

    public int hx, hy, hw, hh;

    public Block(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hx = 0;
        this.hy = 0;
        this.hw = width;
        this.hh = height;
    }

    public Block withHitbox(int insetX, int insetY) {
        this.hx = insetX;
        this.hy = insetY;
        this.hw = width  - insetX * 2;
        this.hh = height - insetY * 2;
        return this;
    }

    public Block withHitbox(int hx, int hy, int hw, int hh) {
        this.hx = hx;
        this.hy = hy;
        this.hw = hw;
        this.hh = hh;
        return this;
    }

    public static boolean collides(Block a, Block b) {
        int ax = a.x + a.hx, ay = a.y + a.hy;
        int bx = b.x + b.hx, by = b.y + b.hy;
        return ax < bx + b.hw &&
               bx < ax + a.hw &&
               ay < by + b.hh &&
               by < ay + a.hh;
    }
}
