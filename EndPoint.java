import javax.swing.*;
import java.awt.Color;


public class EndPoint extends Point {

    private boolean isEndScreenShown = false;
    public EndPoint(int x, int y) {
        super(x, y);
        this.color = Color.YELLOW; // Złoty kolor
    }

    public void ending(int pointCount) {
        if (!isEndScreenShown) {
            JFrame endFrame = new JFrame("Koniec Gry");
            endFrame.setContentPane(new EndScreen(pointCount));
            endFrame.pack();
            endFrame.setLocationRelativeTo(null); // Ustawienie na środku ekranu
            endFrame.setVisible(true);
            endFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            isEndScreenShown = true; // Zapobiegaj ponownemu wyświetleniu
        }
    }
}
