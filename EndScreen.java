import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndScreen extends JPanel {
    int points;
    int elapsedTime;

    public EndScreen(int points, int elapsedTime) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setFocusable(true);
        requestFocusInWindow();
        this.points = points;
        this.elapsedTime = elapsedTime;

        JLabel congratulationsLabel1 = new JLabel("Gratulacje! Ukończyłeś grę!");
        JLabel congratulationsLabel2 = new JLabel("Zdobyłeś: "+ points + " Pkt");
        JLabel timeLabel = new JLabel("Czas: " + elapsedTime / 1000.0 + "s");
        JButton closeButton = new JButton("Zamknij grę");

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(congratulationsLabel1);
        add(congratulationsLabel2);
        add(timeLabel);
        add(closeButton);
    }
}
