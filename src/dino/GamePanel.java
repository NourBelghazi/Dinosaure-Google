package dino;

import dino.engine.GameEngine;
import dino.input.InputHandler;
import dino.renderer.GameRenderer;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {

    private final GameEngine   engine;
    private final GameRenderer renderer;

    public GamePanel() {
        engine   = new GameEngine();
        renderer = new GameRenderer(engine);

        setPreferredSize(new Dimension(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(new InputHandler(engine));

        new Timer(1000 / 60, this).start();
        new Timer(1500, e -> engine.placeObstacle()).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        engine.move();
        repaint();
    }
}
