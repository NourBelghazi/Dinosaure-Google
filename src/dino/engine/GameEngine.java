package dino.engine;

import dino.GameConstants;
import dino.ImageLoader;
import dino.entity.Block;

import java.awt.Image;
import java.util.ArrayList;

public class GameEngine {

    private final Image[] runFrames;
    private final Image   deadImage;
    private final Image   jumpImage;
    private final Image[] duckFrames;
    private final Image[] birdFrames;
    private final Image   cactus1, cactus2, cactus3;
    private final Image   bigCactus1, bigCactus2, bigCactus3;
    private final Image   trackImg;
    private final Image   cloudImg;
    private final Image   gameOverImg;
    private final Image   resetImg;

    private final Block dinosaur;
    private final ArrayList<Block> obstacles = new ArrayList<>();
    private final ArrayList<Block> clouds    = new ArrayList<>();

    private final int dinosaurGroundY;
    private final int duckGroundY;

    private int trackX1;
    private int trackX2;
    private int velocityY;
    private int speed;
    private int score;
    private int highScore;
    private boolean gameOver;
    private boolean isDucking;
    private int animTick;

    public GameEngine() {
        runFrames = new Image[]{
            ImageLoader.load("/images/dino-run1.png"),
            ImageLoader.load("/images/dino-run2.png")
        };
        deadImage  = ImageLoader.load("/images/dino-dead.png");
        jumpImage  = ImageLoader.load("/images/dino-jump.png");
        duckFrames = new Image[]{
            ImageLoader.load("/images/dino-duck1.png"),
            ImageLoader.load("/images/dino-duck2.png")
        };
        birdFrames = new Image[]{
            ImageLoader.load("/images/bird1.png"),
            ImageLoader.load("/images/bird2.png")
        };
        cactus1    = ImageLoader.load("/images/cactus1.png");
        cactus2    = ImageLoader.load("/images/cactus2.png");
        cactus3    = ImageLoader.load("/images/cactus3.png");
        bigCactus1 = ImageLoader.load("/images/big-cactus1.png");
        bigCactus2 = ImageLoader.load("/images/big-cactus2.png");
        bigCactus3 = ImageLoader.load("/images/big-cactus3.png");
        trackImg   = ImageLoader.load("/images/track.png");
        cloudImg   = ImageLoader.load("/images/cloud.png");
        gameOverImg = ImageLoader.load("/images/game-over.png");
        resetImg    = ImageLoader.load("/images/reset.png");

        dinosaurGroundY = GameConstants.BOARD_HEIGHT - GameConstants.DINO_HEIGHT;
        duckGroundY     = GameConstants.BOARD_HEIGHT - GameConstants.DINO_DUCK_HEIGHT;

        dinosaur = new Block(runFrames[0], GameConstants.DINO_X, dinosaurGroundY,
                             GameConstants.DINO_WIDTH, GameConstants.DINO_HEIGHT)
                       .withHitbox(15, 5);
        reset();
    }

    private void reset() {
        obstacles.clear();
        clouds.clear();
        dinosaur.image  = runFrames[0];
        dinosaur.x      = GameConstants.DINO_X;
        dinosaur.y      = dinosaurGroundY;
        dinosaur.width  = GameConstants.DINO_WIDTH;
        dinosaur.height = GameConstants.DINO_HEIGHT;
        dinosaur.withHitbox(15, 5);
        trackX1  = 0;
        trackX2  = GameConstants.BOARD_WIDTH;
        velocityY = 0;
        speed     = GameConstants.INITIAL_SPEED;
        score     = 0;
        gameOver  = false;
        isDucking = false;
        animTick  = 0;
        spawnCloud();
    }

    public void restartGame() {
        reset();
    }

    public void jump() {
        if (gameOver || isDucking) return;
        if (dinosaur.y == dinosaurGroundY) {
            velocityY     = GameConstants.JUMP_VELOCITY;
            dinosaur.image = jumpImage;
        }
    }

    public void startDucking() {
        if (gameOver || dinosaur.y != dinosaurGroundY) return;
        isDucking       = true;
        dinosaur.width  = GameConstants.DINO_DUCK_WIDTH;
        dinosaur.height = GameConstants.DINO_DUCK_HEIGHT;
        dinosaur.y      = duckGroundY;
        dinosaur.withHitbox(20, 10);
    }

    public void stopDucking() {
        if (!isDucking) return;
        isDucking       = false;
        dinosaur.width  = GameConstants.DINO_WIDTH;
        dinosaur.height = GameConstants.DINO_HEIGHT;
        dinosaur.y      = dinosaurGroundY;
        dinosaur.withHitbox(15, 5);
    }

    public void move() {
        if (gameOver) return;

        animTick++;
        speed = Math.min(GameConstants.INITIAL_SPEED + score / 500, GameConstants.MAX_SPEED);

        scrollTrack();
        scrollClouds();
        moveDinosaur();
        moveObstacles();

        score++;
    }

    private void scrollTrack() {
        trackX1 -= speed;
        trackX2 -= speed;
        if (trackX1 + GameConstants.BOARD_WIDTH <= 0) trackX1 = trackX2 + GameConstants.BOARD_WIDTH;
        if (trackX2 + GameConstants.BOARD_WIDTH <= 0) trackX2 = trackX1 + GameConstants.BOARD_WIDTH;
    }

    private void scrollClouds() {
        ArrayList<Block> done = new ArrayList<>();
        for (Block cloud : clouds) {
            cloud.x -= speed / 2;
            if (cloud.x + cloud.width < 0) done.add(cloud);
        }
        clouds.removeAll(done);
        if (clouds.isEmpty() || clouds.get(clouds.size() - 1).x < GameConstants.BOARD_WIDTH - 200) {
            spawnCloud();
        }
    }

    private void spawnCloud() {
        int y = 30 + (int)(Math.random() * 60);
        clouds.add(new Block(cloudImg, GameConstants.BOARD_WIDTH, y,
                             GameConstants.CLOUD_W, GameConstants.CLOUD_H));
    }

    private void moveDinosaur() {
        int groundY = isDucking ? duckGroundY : dinosaurGroundY;
        dinosaur.y += velocityY;
        if (dinosaur.y < groundY) {
            velocityY += GameConstants.GRAVITY;
        } else {
            dinosaur.y = groundY;
            velocityY  = 0;
            dinosaur.image = isDucking
                ? duckFrames[(animTick / GameConstants.ANIM_PERIOD) % 2]
                : runFrames [(animTick / GameConstants.ANIM_PERIOD) % 2];
        }
    }

    private void moveObstacles() {
        ArrayList<Block> done = new ArrayList<>();
        for (Block obs : obstacles) {
            obs.x -= speed;
            if (obs.animFrame >= 0) {
                obs.animFrame = (animTick / GameConstants.ANIM_PERIOD) % 2;
                obs.image     = birdFrames[obs.animFrame];
            }
            if (Block.collides(obs, dinosaur)) {
                dinosaur.image = deadImage;
                gameOver       = true;
                if (score > highScore) highScore = score;
                return;
            }
            if (obs.x + obs.width < 0) done.add(obs);
        }
        obstacles.removeAll(done);
    }

    public void placeObstacle() {
        if (gameOver) return;
        double r = Math.random();
        if (score > 500 && r < 0.25) {
            spawnBird();
            return;
        }
        boolean big = Math.random() < 0.3;
        if (big) {
            spawnBigCactus(r);
        } else {
            spawnSmallCactus(r);
        }
    }

    private void spawnBird() {
        int[] heights = {
            GameConstants.BOARD_HEIGHT - GameConstants.BIRD_H - 20,
            GameConstants.BOARD_HEIGHT - GameConstants.BIRD_H - 70,
            GameConstants.BOARD_HEIGHT - GameConstants.BIRD_H - 120
        };
        int y   = heights[(int)(Math.random() * heights.length)];
        Block b = new Block(birdFrames[0], GameConstants.BOARD_WIDTH, y,
                            GameConstants.BIRD_W, GameConstants.BIRD_H)
                      .withHitbox(15, 15);
        b.animFrame = 0;
        obstacles.add(b);
    }

    private void spawnSmallCactus(double r) {
        int groundY = GameConstants.BOARD_HEIGHT - GameConstants.CACTUS_H;
        if (r < 0.33) {
            obstacles.add(new Block(cactus1, GameConstants.BOARD_WIDTH, groundY, GameConstants.CACTUS1_W, GameConstants.CACTUS_H).withHitbox(5, 5));
        } else if (r < 0.66) {
            obstacles.add(new Block(cactus2, GameConstants.BOARD_WIDTH, groundY, GameConstants.CACTUS2_W, GameConstants.CACTUS_H).withHitbox(5, 5));
        } else {
            obstacles.add(new Block(cactus3, GameConstants.BOARD_WIDTH, groundY, GameConstants.CACTUS3_W, GameConstants.CACTUS_H).withHitbox(5, 5));
        }
    }

    private void spawnBigCactus(double r) {
        int groundY = GameConstants.BOARD_HEIGHT - GameConstants.BIG_CACTUS_H;
        if (r < 0.33) {
            obstacles.add(new Block(bigCactus1, GameConstants.BOARD_WIDTH, groundY, GameConstants.BIG_CACTUS1_W, GameConstants.BIG_CACTUS_H).withHitbox(8, 5));
        } else if (r < 0.66) {
            obstacles.add(new Block(bigCactus2, GameConstants.BOARD_WIDTH, groundY, GameConstants.BIG_CACTUS2_W, GameConstants.BIG_CACTUS_H).withHitbox(8, 5));
        } else {
            obstacles.add(new Block(bigCactus3, GameConstants.BOARD_WIDTH, groundY, GameConstants.BIG_CACTUS3_W, GameConstants.BIG_CACTUS_H).withHitbox(8, 5));
        }
    }

    public Block           getDinosaur()   { return dinosaur; }
    public ArrayList<Block> getObstacles() { return obstacles; }
    public ArrayList<Block> getClouds()    { return clouds; }
    public int  getTrackX1()    { return trackX1; }
    public int  getTrackX2()    { return trackX2; }
    public Image getTrackImg()  { return trackImg; }
    public Image getGameOverImg() { return gameOverImg; }
    public Image getResetImg()    { return resetImg; }
    public int  getScore()      { return score; }
    public int  getHighScore()  { return highScore; }
    public boolean isGameOver() { return gameOver; }
}
