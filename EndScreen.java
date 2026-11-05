import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;

public class EndScreen extends JPanel {
    private static final Color BACKGROUND = new Color(238, 238, 238);
    private static final Color TEXT_COLOR = new Color(55, 55, 55);
    private static final Color BUTTON_COLOR = new Color(125, 125, 125);
    private static final Color BUTTON_HOVER = new Color(105, 105, 105);

    public EndScreen(int points, int elapsedTime, int deathCount, Runnable onReplay) {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(460, 400));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("Gratulacje!");
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 34));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));

        JLabel subtitleLabel = new JLabel("Ukończyłeś grę");
        subtitleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(subtitleLabel);
        content.add(Box.createVerticalStrut(14));

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setBackground(BACKGROUND);
        stats.add(createStatCard("PUNKTY", Integer.toString(points)));
        stats.add(createStatCard("CZAS", String.format(Locale.ROOT, "%.2fs", elapsedTime / 1000.0)));
        stats.add(createStatCard("ŚMIERCI", Integer.toString(deathCount)));
        stats.setMaximumSize(new Dimension(600, 90));
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(stats);
        content.add(Box.createVerticalStrut(14));

        RoundedButton closeButton = new RoundedButton("Zamknij grę");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> System.exit(0));
        RoundedButton replayButton = new RoundedButton("Zagraj ponownie");
        replayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        replayButton.addActionListener(e -> onReplay.run());
        content.add(replayButton);
        content.add(Box.createVerticalStrut(8));

        RoundedButton resultsButton = new RoundedButton("Najlepsze wyniki");
        resultsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsButton.addActionListener(e -> BestResultsDialog.show(this));
        content.add(resultsButton);
        content.add(Box.createVerticalStrut(8));
        content.add(closeButton);

        add(content);
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D graphics = (Graphics2D) g.create();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setColor(Color.WHITE);
                graphics.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                graphics.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        titleLabel.setForeground(new Color(115, 115, 115));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 44));
            setMaximumSize(new Dimension(220, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics = (Graphics2D) g.create();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getModel().isRollover() ? BUTTON_HOVER : BUTTON_COLOR);
            graphics.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            graphics.dispose();
            super.paintComponent(g);
        }
    }
}
