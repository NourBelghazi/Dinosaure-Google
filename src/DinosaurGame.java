import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DinosaurGame extends JPanel implements ActionListener, KeyListener {

    static final int BOARD_WIDTH = 750;
    static final int BOARD_HEIGHT = 250;
    static final int GRAVITY = 2;
    static final int DINO_WIDTH = 88;
    static final int DINO_HEIGHT = 94;
    static final int DINO_DUCK_WIDTH = 118;
    static final int DINO_DUCK_HEIGHT = 60;
    static final int DINO_X = 50;
    static final int CACTUS1_W = 34, CACTUS2_W = 69, CACTUS3_W = 103;
    static final int CACTUS_H = 70;
    static final int BIG_CACTUS1_W = 49, BIG_CACTUS2_W = 98, BIG_CACTUS3_W = 147;
    static final int BIG_CACTUS_H = 100;
    static final int BIRD_W = 92, BIRD_H = 80;
    static final int TRACK_HEIGHT = 26;
    static final int CLOUD_W = 92, CLOUD_H = 28;
    static final int GAME_OVER_W = 382, GAME_OVER_H = 21;
    static final int RESET_W = 36, RESET_H = 32;
    static final int ANIM_PERIOD = 8;
    static final int INITIAL_SPEED = 8;
    static final int MAX_SPEED = 20;

    private Image[] runFrames;
    private Image dinosaurDeadImage;
    private Image dinosaurJumpImage;
    private Image[] duckFrames;
    private Image[] birdFrames;
    private Image cactus1Img, cactus2Img, cactus3Img;
    private Image bigCactus1Img, bigCactus2Img, bigCactus3Img;
    private Image trackImg;
    private Image cloudImg;
    private Image gameOverImg;
    private Image resetImg;

    int dinosaurGroundY;
    int duckGroundY;
    Block dinosaur;

    ArrayList<Block> obstacles = new ArrayList<>();
    ArrayList<Block> clouds = new ArrayList<>();

    int trackX1 = 0;
    int trackX2 = BOARD_WIDTH;

    Timer gameLoop;
    Timer placeObstacleTimer;

    int velocityY = 0;
    int speed = INITIAL_SPEED;
    int score = 0;
    int highScore = 0;
    boolean gameOver = false;
    boolean isDucking = false;
    int animTick = 0;

    public DinosaurGame() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        runFrames = new Image[]{
            new ImageIcon(getClass().getResource("./images/dino-run1.png")).getImage(),
            new ImageIcon(getClass().getResource("./images/dino-run2.png")).getImage()
        };
        dinosaurDeadImage = new ImageIcon(getClass().getResource("./images/dino-dead.png")).getImage();
        dinosaurJumpImage = new ImageIcon(getClass().getResource("./images/dino-jump.png")).getImage();
        duckFrames = new Image[]{
            new ImageIcon(getClass().getResource("./images/dino-duck1.png")).getImage(),
            new ImageIcon(getClass().getResource("./images/dino-duck2.png")).getImage()
        };
        birdFrames = new Image[]{
            new ImageIcon(getClass().getResource("./images/bird1.png")).getImage(),
            new ImageIcon(getClass().getResource("./images/bird2.png")).getImage()
        };
        cactus1Img = new ImageIcon(getClass().getResource("./images/cactus1.png")).getImage();
        cactus2Img = new ImageIcon(getClass().getResource("./images/cactus2.png")).getImage();
        cactus3Img = new ImageIcon(getClass().getResource("./images/cactus3.png")).getImage();
        bigCactus1Img = new ImageIcon(getClass().getResource("./images/big-cactus1.png")).getImage();
        bigCactus2Img = new ImageIcon(getClass().getResource("./images/big-cactus2.png")).getImage();
        bigCactus3Img = new ImageIcon(getClass().getResource("./images/big-cactus3.png")).getImage();
        trackImg = new ImageIcon(getClass().getResource("./images/track.png")).getImage();
        cloudImg = new ImageIcon(getClass().getResource("./images/cloud.png")).getImage();
        gameOverImg = new ImageIcon(getClass().getResource("./images/game-over.png")).getImage();
        resetImg = new ImageIcon(getClass().getResource("./images/reset.png")).getImage();

        dinosaurGroundY = BOARD_HEIGHT - DINO_HEIGHT;
        duckGroundY = BOARD_HEIGHT - DINO_DUCK_HEIGHT;
        dinosaur = new Block(runFrames[0], DINO_X, dinosaurGroundY, DINO_WIDTH, DINO_HEIGHT);

        spawnCloud();

        gameLoop = new Timer(1000 / 60, this);
        placeObstacleTimer = new Timer(1500, e -> placeObstacle());
        gameLoop.start();
        placeObstacleTimer.start();
    }

    void spawnCloud() {
        int y = 30 + (int)(Math.random() * 60);
        clouds.add(new Block(cloudImg, BOARD_WIDTH, y, CLOUD_W, CLOUD_H));
    }

    void placeObstacle() {
        if (gameOver) return;
        double r = Math.random();
        if (score > 500 && r < 0.25) {
            int[] birdHeights = {BOARD_HEIGHT - BIRD_H - 20, BOARD_HEIGHT - BIRD_H - 70, BOARD_HEIGHT - BIRD_H - 120};
            int y = birdHeights[(int)(Math.random() * birdHeights.length)];
            Block bird = new Block(birdFrames[0], BOARD_WIDTH, y, BIRD_W, BIRD_H);
            bird.animFrame = 0;
            obstacles.add(bird);
            return;
        }
        boolean big = Math.random() < 0.3;
        if (big) {
            if (r < 0.33) {
                obstacles.add(new Block(bigCactus1Img, BOARD_WIDTH, BOARD_HEIGHT - BIG_CACTUS_H, BIG_CACTUS1_W, BIG_CACTUS_H));
            } else if (r < 0.66) {
                obstacles.add(new Block(bigCactus2Img, BOARD_WIDTH, BOARD_HEIGHT - BIG_CACTUS_H, BIG_CACTUS2_W, BIG_CACTUS_H));
            } else {
                obstacles.add(new Block(bigCactus3Img, BOARD_WIDTH, BOARD_HEIGHT - BIG_CACTUS_H, BIG_CACTUS3_W, BIG_CACTUS_H));
            }
        } else {
            if (r < 0.33) {
                obstacles.add(new Block(cactus1Img, BOARD_WIDTH, BOARD_HEIGHT - CACTUS_H, CACTUS1_W, CACTUS_H));
            } else if (r < 0.66) {
                obstacles.add(new Block(cactus2Img, BOARD_WIDTH, BOARD_HEIGHT - CACTUS_H, CACTUS2_W, CACTUS_H));
            } else {
                obstacles.add(new Block(cactus3Img, BOARD_WIDTH, BOARD_HEIGHT - CACTUS_H, CACTUS3_W, CACTUS_H));
            }
        }
    }

    void restart() {
        dinosaur.image = runFrames[0];
        dinosaur.x = DINO_X;
        dinosaur.y = dinosaurGroundY;
        dinosaur.width = DINO_WIDTH;
        dinosaur.height = DINO_HEIGHT;
        obstacles.clear();
        clouds.clear();
        trackX1 = 0;
        trackX2 = BOARD_WIDTH;
        velocityY = 0;
        speed = INITIAL_SPEED;
        score = 0;
        gameOver = false;
        isDucking = false;
        animTick = 0;
        spawnCloud();
        gameLoop.start();
        placeObstacleTimer.start();
    }

    static class Block {
        Image image;
        int x, y, width, height;
        int animFrame = -1;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    boolean collision(Block a, Block b) {
        return a.x < b.x + b.width  - 5 &&
               b.x < a.x + a.width  - 5 &&
               a.y < b.y + b.height - 5 &&
               b.y < a.y + a.height - 5;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            placeObstacleTimer.stop();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            stopDucking();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (gameOver) {
            if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) restart();
            return;
        }
        if (key == KeyEvent.VK_UP && dinosaur.y == dinosaurGroundY && !isDucking) {
            velocityY = -17;
            dinosaur.image = dinosaurJumpImage;
        }
        if (key == KeyEvent.VK_DOWN && dinosaur.y == dinosaurGroundY) {
            isDucking = true;
            dinosaur.width = DINO_DUCK_WIDTH;
            dinosaur.height = DINO_DUCK_HEIGHT;
            dinosaur.y = duckGroundY;
        }
    }

    void stopDucking() {
        if (!isDucking) return;
        isDucking = false;
        dinosaur.width = DINO_WIDTH;
        dinosaur.height = DINO_HEIGHT;
        dinosaur.y = dinosaurGroundY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    void draw(Graphics g) {
        for (Block cloud : clouds) {
            g.drawImage(cloud.image, cloud.x, cloud.y, cloud.width, cloud.height, null);
        }

        g.drawImage(trackImg, trackX1, BOARD_HEIGHT - TRACK_HEIGHT, BOARD_WIDTH, TRACK_HEIGHT, null);
        g.drawImage(trackImg, trackX2, BOARD_HEIGHT - TRACK_HEIGHT, BOARD_WIDTH, TRACK_HEIGHT, null);

        g.drawImage(dinosaur.image, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);
        for (Block obs : obstacles) {
            g.drawImage(obs.image, obs.x, obs.y, obs.width, obs.height, null);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", Font.PLAIN, 18));
        g.drawString("HI " + highScore + "   " + score, BOARD_WIDTH - 200, 25);

        if (gameOver) {
            int goX = (BOARD_WIDTH - GAME_OVER_W) / 2;
            int goY = BOARD_HEIGHT / 2 - 40;
            g.drawImage(gameOverImg, goX, goY, GAME_OVER_W, GAME_OVER_H, null);
            int rstX = (BOARD_WIDTH - RESET_W) / 2;
            int rstY = goY + GAME_OVER_H + 15;
            g.drawImage(resetImg, rstX, rstY, RESET_W, RESET_H, null);
        }
    }

    void move() {
        if (gameOver) return;

        animTick++;
        speed = Math.min(INITIAL_SPEED + score / 500, MAX_SPEED);

        trackX1 -= speed;
        trackX2 -= speed;
        if (trackX1 + BOARD_WIDTH <= 0) trackX1 = trackX2 + BOARD_WIDTH;
        if (trackX2 + BOARD_WIDTH <= 0) trackX2 = trackX1 + BOARD_WIDTH;

        ArrayList<Block> cloudsToRemove = new ArrayList<>();
        for (Block cloud : clouds) {
            cloud.x -= speed / 2;
            if (cloud.x + cloud.width < 0) cloudsToRemove.add(cloud);
        }
        clouds.removeAll(cloudsToRemove);
        if (clouds.isEmpty() || clouds.get(clouds.size() - 1).x < BOARD_WIDTH - 200) {
            spawnCloud();
        }

        int groundY = isDucking ? duckGroundY : dinosaurGroundY;
        dinosaur.y += velocityY;
        if (dinosaur.y < groundY) {
            velocityY += GRAVITY;
        } else {
            dinosaur.y = groundY;
            velocityY = 0;
            if (isDucking) {
                dinosaur.image = duckFrames[(animTick / ANIM_PERIOD) % 2];
            } else {
                dinosaur.image = runFrames[(animTick / ANIM_PERIOD) % 2];
            }
        }

        ArrayList<Block> toRemove = new ArrayList<>();
        for (Block obs : obstacles) {
            obs.x -= speed;
            if (obs.animFrame >= 0) {
                obs.animFrame = (animTick / ANIM_PERIOD) % 2;
                obs.image = birdFrames[obs.animFrame];
            }
            if (collision(obs, dinosaur)) {
                dinosaur.image = dinosaurDeadImage;
                gameOver = true;
                if (score > highScore) highScore = score;
                return;
            }
            if (obs.x + obs.width < 0) toRemove.add(obs);
        }
        obstacles.removeAll(toRemove);

        score++;
    }
}
