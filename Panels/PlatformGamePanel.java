package Panels;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * PJ-05 -- PlatformGamePanel.java
 * <p>
 * This class handles the entire logic of the game. It is a JPanel that utilizes
 * grid-based
 * collision detection. The map is a [25][25] array, and the tile size is 25
 * pixels, giving
 * us a screen of 800x800. Inside the class is the functionality for how player
 * movement is handled
 * detection of whether they are on a surface or in the air, they are out of
 * bounds, they have
 * collided with a monster, and jumping mechanics. Including the player, there
 * are 8 sprites,
 * and the player must jump over them and traverse the platforms using W,A,S,D,
 * or the
 * up, left, right, and down characters.
 * 
 * The timer loop for this game has a delay of 100ms, and continually checks the
 * applyGravity() method, and repaint() for the panel.
 * 
 * Additionally, there are four animations at play:
 * 1. the berserk orc on the first floor is constantly moving left to right
 * 2. the centipede monster on the third floor is constantly moving left to
 * right
 * 3. the big bloated monster on the fourth floor is constantly moving left to
 * right
 * 4. the wizard player sprite has different animations for moving left to right
 * 
 * @author Harjot Singh, 10841
 * @version Apr 24, 2026
 */
public class PlatformGamePanel extends JPanel implements KeyListener {
    int[][] map = new int[25][25];
    int tileSize = 32;
    int GROUND_TILE_INTEGER = 3;
    int MONSTER_TILE_INTEGER = 2;

    int counter = 0;
    int velocityY = 0;

    private Image backgroundImage;

    // Sprites
    private Sprite playerSprite;
    private Sprite berserkOrcSprite;
    private Sprite shamanOrcSprite;
    private Sprite warriorOrcSprite;
    private Sprite centipedeSprite;
    private Sprite battleTurtleSprite;
    private Sprite bigBloatedSprite;
    private Sprite monsterFly;

    private CardLayout cardLayout;
    private JPanel container;

    Timer gameLoop;

    public PlatformGamePanel(CardLayout cardLayout, JPanel container) {
        try {
            backgroundImage = ImageIO.read(new File("Sprites/platformer.png"));
        } catch (IOException e) {
            System.out.println(e);
        }

        this.cardLayout = cardLayout;
        this.container = container;

        playerSprite = new Sprite("Sprites/wizard.png");
        playerSprite.setLeftSprite("Sprites/wizard_left.png");
        playerSprite.setRightSprite("Sprites/wizard_right.png");
        addMonsterSprites();
        initializeMap();

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        gameLoop = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyGravity();
                repaint();
            }
        });
        gameLoop.start();

    }

    public void addSpriteToMap(Sprite sprite, int spriteX, int spriteY, int spriteTileHeight, int spriteTileWidth) {
        sprite.setSpriteX(spriteX);
        sprite.setSpriteY(spriteY);
        sprite.setSpriteTileHeight(spriteTileHeight);
        sprite.setSpriteTileWidth(spriteTileWidth);
        int spriteHeight = spriteY;
        for (int i = 0; i < spriteTileHeight; i++) {
            editTileMap(spriteHeight, spriteX, spriteX + (spriteTileWidth - 1), MONSTER_TILE_INTEGER);
            spriteHeight++;
        }
    }

    public void applyGravity() {
        moveMonsterLeft(centipedeSprite);
        moveMonsterLeft(bigBloatedSprite);
        moveMonsterLeft(berserkOrcSprite);

        if (playerSprite.isOutOfBounds() || collidedWithMonster(playerSprite.getSpriteY(), playerSprite.getSpriteX())) {
            playerSprite.setImage(playerSprite.getSprite());
            playerSprite.resetSprite();
            velocityY = 0;
            cardLayout.show(container, "restart_menu");
        }

        updateSpritePosition(playerSprite.getSpriteY(), playerSprite.getSpriteX(), playerSprite.getSpriteY(),
                playerSprite.getSpriteX());

        // Only reset velocity if grounded AND not in a jump (velocityY < 0 means going
        // up)
        if (velocityY >= 0 && underneathSpriteIsFloor(playerSprite.getSpriteY(), playerSprite.getSpriteX())) {
            velocityY = 0;
            return;
        }

        velocityY += 1; // gravity

        int currentY = playerSprite.getSpriteY();
        int x = playerSprite.getSpriteX();

        int steps = Math.abs(velocityY);
        int direction = (velocityY > 0) ? 1 : -1;

        for (int i = 0; i < steps; i++) {
            if (direction > 0 && underneathSpriteIsFloor(currentY, x)) {
                velocityY = 0;
                break;
            }
            currentY += direction;
        }
        // And the call inside the loop (old Y → new currentY)
        if (collidedWithMonster(currentY, playerSprite.getSpriteX())) {
            playerSprite.resetSprite();
            playerSprite.setImage(playerSprite.getSprite());
            cardLayout.show(container, "restart_menu");
            return;
        }

        try {
            updateSpritePosition(playerSprite.getSpriteY(), x, currentY, x);
            playerSprite.setSpriteY(currentY);
        } catch (ArrayIndexOutOfBoundsException a) {
            playerSprite.resetSprite();
            playerSprite.setImage(playerSprite.getSprite());
            cardLayout.show(container, "restart_menu");
            return;
        }

        if (checkVictoryCondition()) {
            playerSprite.resetSprite();
            playerSprite.setImage(playerSprite.getSprite());
            cardLayout.show(container, "victory_menu");
        }
    }

    public void moveSprite(char keyChar, int keyCode) {
        int oldX = playerSprite.getSpriteX();
        int oldY = playerSprite.getSpriteY();

        if (keyChar == 'a' || keyCode == 37) {
            int newX = oldX - 1;
            updateSpritePosition(oldY, oldX, oldY, newX);
            playerSprite.setSpriteX(newX);
            playerSprite.setImage(playerSprite.getLeftSprite());
        }
        if (keyChar == 'd' || keyCode == 39) {
            int newX = oldX + 1;
            updateSpritePosition(oldY, oldX, oldY, newX);
            playerSprite.setSpriteX(newX);
            playerSprite.setImage(playerSprite.getRightSprite());
        }
        if (keyChar == 'w' || keyCode == 38) {
            if (underneathSpriteIsFloor(oldY, oldX)) {
                velocityY = -3;
            }
        }
        System.out.println("-".repeat(80));
        printTileMap();
        System.out.println("-".repeat(80));
    }

    public boolean collidedWithMonster(int row, int col) {
        try {
            return (map[row][col] == MONSTER_TILE_INTEGER);
        } catch (ArrayIndexOutOfBoundsException a) {
            return false;
        }
    }

    public boolean underneathSpriteIsFloor(int y_coord, int x_coord) {
        try {
            if (map[y_coord + 1][x_coord] == GROUND_TILE_INTEGER) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            // System.out.println(a);
        }
        return false;
    }

    public void updateSpritePosition(int oldRow, int oldCol, int newRow, int newCol) {

        if (map[oldRow][oldCol] == 1) { // only erase if it's the player tile
            map[oldRow][oldCol] = 0;
        }
        if (map[newRow][newCol] != GROUND_TILE_INTEGER && map[newRow][newCol] != 2) {
            map[newRow][newCol] = 1;
        }

    }

    public void printTileMap() {
        for (int[] x : map) {
            for (int i = 0; i < x.length; i++) {
                if (i != 0) {
                    System.out.printf(", %d", x[i]);
                } else {
                    System.out.printf("[%d", x[i]);
                }
            }
            System.out.printf("] \n");
        }

    }

    public void editTileMap(int row, int colBegin, int colEnd, int val) {
        for (int i = colBegin; i <= colEnd; i++) {
            map[row][i] = val;
        }
    }

    public void keyPressed(KeyEvent e) {
        char keyChar = e.getKeyChar();
        int keyCode = e.getKeyCode();
        moveSprite(keyChar, keyCode);
        repaint();
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void updateSpritePositionIfReset(int row, int col) {
        map[row][col] = 0;
    }

    public void initializeMap() {
        editTileMap(4, 0, 12, GROUND_TILE_INTEGER);
        editTileMap(4, 15, 19, GROUND_TILE_INTEGER);

        editTileMap(9, 3, 17, GROUND_TILE_INTEGER);
        editTileMap(9, 19, 24, GROUND_TILE_INTEGER);

        editTileMap(14, 0, 6, GROUND_TILE_INTEGER);
        editTileMap(14, 8, 14, GROUND_TILE_INTEGER);
        editTileMap(14, 16, 21, GROUND_TILE_INTEGER);

        editTileMap(19, 6, 24, GROUND_TILE_INTEGER);

        editTileMap(22, 3, 5, GROUND_TILE_INTEGER);

        editTileMap(23, 7, 9, GROUND_TILE_INTEGER);

        editTileMap(24, 11, 13, GROUND_TILE_INTEGER);
        editTileMap(24, 16, 18, GROUND_TILE_INTEGER);
        editTileMap(24, 21, 23, GROUND_TILE_INTEGER);
    }

    public void addMonsterSprites() {
        shamanOrcSprite = new Sprite("Sprites/shaman_orc.png");
        addSpriteToMap(shamanOrcSprite, 8, 2, 2, 2);

        berserkOrcSprite = new Sprite("Sprites/berserk_orc.png");
        addSpriteToMap(berserkOrcSprite, 16, 2, 2, 2);
        berserkOrcSprite.setLeftSprite("Sprites/berserk_orc_left.png");
        berserkOrcSprite.setRightSprite("Sprites/berserk_orc_right.png");

        warriorOrcSprite = new Sprite("Sprites/warrior_orc.png");
        addSpriteToMap(warriorOrcSprite, 12, 7, 2, 2);

        centipedeSprite = new Sprite("Sprites/centipede_monster_left.png");
        addSpriteToMap(centipedeSprite, 3, 12, 2, 3);
        centipedeSprite.setLeftSprite("Sprites/centipede_monster_left.png");
        centipedeSprite.setRightSprite("Sprites/centipede_monster_right.png");

        battleTurtleSprite = new Sprite("Sprites/battle_turtle_monster.png");
        addSpriteToMap(battleTurtleSprite, 17, 12, 2, 2);

        bigBloatedSprite = new Sprite("Sprites/big_bloated_monster.png");
        addSpriteToMap(bigBloatedSprite, 14, 17, 2, 2);
        bigBloatedSprite.setLeftSprite("Sprites/big_bloated_monster_left.png");
        bigBloatedSprite.setRightSprite("Sprites/big_bloated_monster_right.png");

        monsterFly = new Sprite("Sprites/fly_monster.png");
        addSpriteToMap(monsterFly, 7, 15, 2, 2);
    }

    public void moveMonsterLeft(Sprite monster) {
        if (monster.getMovementDirection() < 0) {
            monster.setImage(monster.getLeftSprite());
        } else {
            monster.setImage(monster.getRightSprite());
        }

        int x = monster.getSpriteX();
        int y = monster.getSpriteY();
        int dir = monster.getMovementDirection();
        int bottomY = y + monster.getSpriteTileHeight() - 1;

        int newX = x + dir;

        if (newX < 0 || (newX + monster.getSpriteTileWidth() - 1) >= map[0].length) {
            monster.setMovementDirection(-dir);
            return;
        }

        if (!underneathSpriteIsFloor(bottomY, newX)) {
            monster.setMovementDirection(-dir);
            return;
        }

        editTileMap(y, x, x + monster.getSpriteTileWidth() - 1, 0);
        editTileMap(bottomY, x, x + monster.getSpriteTileWidth() - 1, 0);

        monster.setSpriteX(newX);

        editTileMap(y, newX, newX + monster.getSpriteTileWidth() - 1, MONSTER_TILE_INTEGER);
        editTileMap(bottomY, newX, newX + monster.getSpriteTileWidth() - 1, MONSTER_TILE_INTEGER);
    }

    public boolean checkVictoryCondition() {
        if (playerSprite.getSpriteY() == 23) {
            if (playerSprite.getSpriteX() == 21) {
                return true;
            }
            if (playerSprite.getSpriteX() == 22) {
                return true;
            }
            if (playerSprite.getSpriteX() == 23) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        playerSprite.draw(g);
        berserkOrcSprite.draw(g);
        shamanOrcSprite.draw(g);
        warriorOrcSprite.draw(g);
        centipedeSprite.draw(g);
        battleTurtleSprite.draw(g);
        bigBloatedSprite.draw(g);
        monsterFly.draw(g);
    }

}