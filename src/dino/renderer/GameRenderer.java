package dino.renderer;

import dino.GameConstants;
import dino.engine.GameEngine;
import dino.entity.Block;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class GameRenderer {

    private final GameEngine engine;

    public GameRenderer(GameEngine engine) {
        this.engine = engine;
    }

    public void draw(Graphics g) {
        for (Block cloud : engine.getClouds()) {
            g.drawImage(cloud.image, cloud.x, cloud.y, cloud.width, cloud.height, null);
        }

        g.drawImage(engine.getTrackImg(),
                    engine.getTrackX1(), GameConstants.BOARD_HEIGHT - GameConstants.TRACK_HEIGHT,
                    GameConstants.BOARD_WIDTH, GameConstants.TRACK_HEIGHT, null);
        g.drawImage(engine.getTrackImg(),
                    engine.getTrackX2(), GameConstants.BOARD_HEIGHT - GameConstants.TRACK_HEIGHT,
                    GameConstants.BOARD_WIDTH, GameConstants.TRACK_HEIGHT, null);

        Block dino = engine.getDinosaur();
        g.drawImage(dino.image, dino.x, dino.y, dino.width, dino.height, null);

        for (Block obs : engine.getObstacles()) {
            g.drawImage(obs.image, obs.x, obs.y, obs.width, obs.height, null);
        }

        drawHUD(g);

        if (engine.isGameOver()) {
            drawGameOver(g);
        }
    }

    private void drawHUD(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", Font.PLAIN, 18));
        String hud = "HI " + engine.getHighScore() + "   " + engine.getScore();
        g.drawString(hud, GameConstants.BOARD_WIDTH - 200, 25);
    }

    private void drawGameOver(Graphics g) {
        int goX = (GameConstants.BOARD_WIDTH  - GameConstants.GAME_OVER_W) / 2;
        int goY = GameConstants.BOARD_HEIGHT / 2 - 40;
        g.drawImage(engine.getGameOverImg(), goX, goY,
                    GameConstants.GAME_OVER_W, GameConstants.GAME_OVER_H, null);
        int rstX = (GameConstants.BOARD_WIDTH - GameConstants.RESET_W) / 2;
        int rstY = goY + GameConstants.GAME_OVER_H + 15;
        g.drawImage(engine.getResetImg(), rstX, rstY,
                    GameConstants.RESET_W, GameConstants.RESET_H, null);
    }
}
