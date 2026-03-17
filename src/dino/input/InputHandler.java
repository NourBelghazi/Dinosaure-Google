package dino.input;

import dino.engine.GameEngine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {

    private final GameEngine engine;

    public InputHandler(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (engine.isGameOver()) {
            if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) engine.restartGame();
            return;
        }
        if (key == KeyEvent.VK_UP)   engine.jump();
        if (key == KeyEvent.VK_DOWN) engine.startDucking();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) engine.stopDucking();
    }
}
