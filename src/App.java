import dino.GamePanel;
import javax.swing.JFrame;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dinosaur Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();
        panel.requestFocus();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
