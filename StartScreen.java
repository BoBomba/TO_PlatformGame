import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartScreen extends JPanel {
    private JFrame mainFrame;

    public StartScreen(JFrame frame) {
        this.mainFrame = frame;

        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Platformówka", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getContentPane().removeAll();
                Character character = new Character(50, 50, 30, 30);
                GamePanel gamePanel = new GamePanel(character);
                mainFrame.add(gamePanel);
                mainFrame.revalidate();
                mainFrame.repaint();
                gamePanel.requestFocusInWindow();
            }
        });
        add(startButton, BorderLayout.SOUTH);
    }
}
