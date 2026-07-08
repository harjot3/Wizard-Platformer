package Panels;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * PJ-05 -- Sprite.java
 * <p>
 * This class manages the sprite's position, images, and rendering on the tile-based grid. 
 * It also supports directional, for if they are constantly moving monsters on a set basis
 * and has a method of out of boundary detection.
 * There a lot of setter and getter methods, as the information of the sprites
 * (their coordinates/images) needs to consistently be attained or updated.
 * 
 * @author Harjot Singh, 10841
 * @version Apr 26, 2026
 */
public class Sprite {

    private Image sprite;
    private Image sprite_left;
    private Image sprite_right;

    private int START_PLAYER_X = 0;
    private int START_PLAYER_Y = 3;

    public int spriteX = START_PLAYER_X;
    public int spriteY = START_PLAYER_Y;
    private int movementDirection = -1;
    int tileSize = 32;
    int tileWidth = 1;
    int tileHeight = 1;

    public Sprite() {
    }

    public Sprite(String filename) {
        try {
            this.sprite = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void setLeftSprite(String filename) {
        try {
            this.sprite_left = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void setRightSprite(String filename) {
        try {
            this.sprite_right = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Image getSprite() {
        return this.sprite;
    }

    public Image getRightSprite() {
        return this.sprite_right;
    }

    public Image getLeftSprite() {
        return this.sprite_left;
    }

    public void setImage(Image image) {
        this.sprite = image;
    }

    public void draw(Graphics g) {
        g.drawImage(this.sprite, spriteX * tileSize, spriteY * tileSize, tileSize * tileWidth, tileSize * tileHeight,
                null);
    }

    public int getSpriteX() {
        return this.spriteX;
    }

    public int getSpriteY() {
        return this.spriteY;
    }

    public void setSpriteX(int x) {
        this.spriteX = x;
    }

    public void setSpriteY(int y) {
        this.spriteY = y;
    }

    public int getSpriteTileHeight() {
        return this.tileHeight;
    }

    public int getSpriteTileWidth() {
        return this.tileWidth;
    }

    public void setSpriteTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public void setSpriteTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public boolean isOutOfBounds() {
        return (this.spriteY > 24 || this.spriteX < 0 || this.spriteX > 24);
    }

    public void resetSprite() {
        this.spriteX = START_PLAYER_X;
        this.spriteY = START_PLAYER_Y;
    }

    public int getMovementDirection() {
        return movementDirection;
    }

    public void setMovementDirection(int movementDirection) {
        this.movementDirection = movementDirection;
    }

}
