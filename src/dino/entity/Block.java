package dino.entity;

import java.awt.Image;

public class Block {

    public Image image;
    public int x, y, width, height;
    public int animFrame = -1;

    public Block(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static boolean collides(Block a, Block b) {
        return a.x < b.x + b.width  - 5 &&
               b.x < a.x + a.width  - 5 &&
               a.y < b.y + b.height - 5 &&
               b.y < a.y + a.height - 5;
    }
}
