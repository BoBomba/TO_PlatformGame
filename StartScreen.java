import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.ThreadLocalRandom;

public class StartScreen extends JPanel {
    private static final Color BACKGROUND = new Color(238, 238, 238);
    private static final Color TEXT_COLOR = new Color(55, 55, 55);
    private static final Color FIELD_BORDER = new Color(185, 185, 185);
    private static final Color BUTTON_COLOR = new Color(125, 125, 125);
    private static final Color BUTTON_HOVER = new Color(105, 105, 105);

    private final JFrame mainFrame;
    private final JTextField levelCountField;
    private final JTextField seedField;

    public StartScreen(JFrame frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("Platformówka");
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(34));

        content.add(createLabel("Liczba poziomów"));
        content.add(Box.createVerticalStrut(8));
        levelCountField = createInput("5");
        content.add(levelCountField);
        content.add(Box.createVerticalStrut(22));

        content.add(createLabel("Seed"));
        content.add(Box.createVerticalStrut(8));
        seedField = createInput("67");
        content.add(seedField);
        content.add(Box.createVerticalStrut(12));

        RoundedButton randomSeedButton = new RoundedButton("Losuj seed", BUTTON_COLOR);
        randomSeedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomSeedButton.addActionListener(e -> seedField.setText(randomSeed()));
        content.add(randomSeedButton);
        content.add(Box.createVerticalStrut(28));

        RoundedButton startButton = new RoundedButton("Start", new Color(105, 105, 105));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());
        levelCountField.addActionListener(e -> startButton.doClick());
        seedField.addActionListener(e -> startButton.doClick());
        mainFrame.getRootPane().setDefaultButton(startButton);
        content.add(startButton);
        content.add(Box.createVerticalStrut(12));

        RoundedButton resultsButton = new RoundedButton("Najlepsze wyniki", BUTTON_COLOR);
        resultsButton.addActionListener(e -> BestResultsDialog.show(this));
        content.add(resultsButton);

        add(content);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createInput(String value) {
        RoundedTextField field = new RoundedTextField(value);
        field.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        field.setForeground(TEXT_COLOR);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(300, 48));
        field.setPreferredSize(new Dimension(300, 48));
        return field;
    }

    private void startGame() {
        int levelCount;
        try {
            levelCount = Integer.parseInt(levelCountField.getText().trim());
        } catch (NumberFormatException exception) {
            showLevelCountError();
            return;
        }

        if (levelCount < 1 || levelCount > 100) {
            showLevelCountError();
            return;
        }

        GamePanel gamePanel = new GamePanel(
                new Character(50, 50, Character.DEFAULT_WIDTH, 30),
                levelCount,
                seedField.getText());
        mainFrame.getContentPane().removeAll();
        mainFrame.add(gamePanel);
        mainFrame.revalidate();
        mainFrame.repaint();
        gamePanel.requestFocusInWindow();
    }

    private void showLevelCountError() {
        JOptionPane.showMessageDialog(
                this,
                "Wpisz liczbę poziomów od 1 do 100.",
                "Nieprawidłowa liczba poziomów",
                JOptionPane.ERROR_MESSAGE);
        levelCountField.requestFocusInWindow();
        levelCountField.selectAll();
    }

    private String randomSeed() {
        return Integer.toString(ThreadLocalRandom.current().nextInt(1, 1000000));
    }

    private static class RoundedTextField extends JTextField {
        RoundedTextField(String text) {
            super(text);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics = (Graphics2D) g.create();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            graphics.setColor(Color.WHITE);
            graphics.fill(shape);
            graphics.setColor(FIELD_BORDER);
            graphics.draw(shape);
            graphics.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {
        private final Color baseColor;

        RoundedButton(String text, Color baseColor) {
            super(text);
            this.baseColor = baseColor;
            setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(220, 48));
            setMaximumSize(new Dimension(220, 48));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics = (Graphics2D) g.create();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getModel().isRollover() ? BUTTON_HOVER : baseColor);
            graphics.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            graphics.dispose();
            super.paintComponent(g);
        }
    }
}
