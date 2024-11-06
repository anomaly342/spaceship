import javax.swing.JFrame;

public class Main extends JFrame {

    private GameEngine gameEngine = new GameEngine();

    public Main() {
        add(gameEngine);
    }

    public static void main(String[] args) {
        Main Game = new Main();
        Game.setSize(900, 1000);
        Game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game.setLocationRelativeTo(null);
        Game.setVisible(true);
    }
}