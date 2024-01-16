import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow extends JFrame {

    // singleton

    private static int widthWindow = 800;
    private static int heightWindow = 600;

    public GameWindow() {
        this.setTitle("2D Platform Game");
        this.setSize(widthWindow, heightWindow);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Centruje okno

        StartScreen startScreen = new StartScreen(this);
        this.add(startScreen);
        this.setVisible(true);
        startScreen.requestFocusInWindow();

    }

    public static int getWidthWindow() {
        return widthWindow;
    }

    public static int getHeightWindow() {
        return heightWindow;
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
