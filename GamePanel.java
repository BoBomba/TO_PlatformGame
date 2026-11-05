import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.io.IOException;

public class GamePanel extends JPanel {
    private final Character character;
    private final String seed;
    private Memento initialPosition;
    private List<Platform> platforms;
    private int elapsedTime = 0;
    private List<Point> points;
    private final List<Level> levels;
    private int currentLevelIndex = 0;
    private int pointCount = 0;
    private int deathCount = 0;
    private boolean timingStarted = false;

    public GamePanel(Character character) {
        this(character, 5);
    }

    public GamePanel(Character character, int levelCount) {
        this(character, levelCount, Long.toString(System.nanoTime()));
    }

    public GamePanel(Character character, int levelCount, String seed) {
        this.character = character;
        this.seed = seed;
        this.initialPosition = character.saveStateToMemento();

        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        requestFocusInWindow();
        bindControls();

        levels = new LevelGenerator().generateLevels(levelCount, seed);
        levels.get(currentLevelIndex).loadLevel(this);

        Timer[] timer = new Timer[1];
        timer[0] = new Timer(16, e -> {
            character.update(platforms);

            if (character.y > GameWindow.getHeightWindow() - 30 || isTouchingHazard()) {
                deathCount++;
                character.getStateFromMemento(initialPosition);
            }

            if (character.x > GameWindow.getWidthWindow()) {
                if (currentLevelIndex < levels.size() - 1) {
                    currentLevelIndex++;
                    levels.get(currentLevelIndex).loadLevel(this);
                    Platform startingPlatform = platforms.get(0);
                    character.setPosition(0, startingPlatform.y - character.height);
                    initialPosition = character.saveStateToMemento();
                } else {
                    character.x = GameWindow.getWidthWindow() - character.width;
                }
            }

            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point point = pointIterator.next();
                if (point.checkCollision(character)) {
                    if (point instanceof EndPoint) {
                        GameWindow gameWindow = (GameWindow) SwingUtilities.getWindowAncestor(this);
                        try {
                            new HighScoreStore().recordResult(
                                    seed,
                                    levels.size(),
                                    elapsedTime,
                                    pointCount,
                                    deathCount);
                        } catch (IOException exception) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Nie udało się zapisać wyniku:\n" + exception.getMessage(),
                                    "Błąd zapisu wyników",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        ((EndPoint) point).ending(pointCount, elapsedTime, deathCount, gameWindow);
                        timer[0].stop();
                        repaint();
                        return;
                    }
                    pointIterator.remove();
                    pointCount++;
                }
            }

            if (timingStarted) {
                elapsedTime += 16;
            }
            repaint();
        });
        timer[0].start();
    }

    private void bindControls() {
        Action moveLeftAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timingStarted = true;
                character.moveLeft();
            }
        };
        Action moveRightAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timingStarted = true;
                character.moveRight();
            }
        };
        Action stopAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                character.stop();
            }
        };
        Action jumpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timingStarted = true;
                character.jump();
            }
        };
        Action stopJumpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                character.stopJump();
            }
        };

        bindKeyStrokeToAction("LEFT", moveLeftAction);
        bindKeyStrokeToAction("RIGHT", moveRightAction);
        bindKeyStrokeToAction("UP", jumpAction);
        bindKeyStrokeToAction("released LEFT", stopAction);
        bindKeyStrokeToAction("released RIGHT", stopAction);
        bindKeyStrokeToAction("released UP", stopJumpAction);
    }

    private boolean isTouchingHazard() {
        for (Platform platform : platforms) {
            if (platform.isLethal() && character.checkCollision(character.x, character.y, platform)) {
                return true;
            }
        }
        return false;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = new ArrayList<>(platforms);
    }

    public void setPoints(List<Point> points) {
        this.points = new ArrayList<>(points);
    }

    private void bindKeyStrokeToAction(String keyStroke, Action action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
        getActionMap().put(keyStroke, action);
    }

    private void drawPointsSidebar(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();
        int rightEdge = getWidth() - 20;
        String pointsText = "Points: " + pointCount;
        String timeText = String.format(Locale.ROOT, "Time: %.2fs", elapsedTime / 1000.0);
        String deathsText = "Deaths: " + deathCount;
        g.drawString(pointsText, rightEdge - metrics.stringWidth(pointsText), 30);
        g.drawString(timeText, rightEdge - metrics.stringWidth(timeText), 60);
        g.drawString(deathsText, rightEdge - metrics.stringWidth(deathsText), 90);
    }

    private void drawLevelIndicator(Graphics g) {
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String label = "" + levels.get(currentLevelIndex).getLevelNumber();
        Font font = new Font(Font.DIALOG, Font.BOLD, 16);
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        int paddingX = 14;
        int badgeHeight = 36;
        int badgeWidth = metrics.stringWidth(label) + paddingX * 2;
        int x = 20;
        int y = 20;

        graphics.setColor(new Color(235, 235, 235, 225));
        graphics.fillRoundRect(x, y, badgeWidth, badgeHeight, badgeHeight, badgeHeight);
        graphics.setColor(new Color(70, 70, 70));
        graphics.drawString(label, x + paddingX, y + (badgeHeight - metrics.getHeight()) / 2 + metrics.getAscent());
        graphics.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Platform platform : platforms) {
            platform.draw(g);
        }
        for (Point point : points) {
            point.draw(g);
        }

        g.setColor(Color.BLUE);
        g.fillRect(character.x, character.y, character.width, character.height);
        drawPointsSidebar(g);
        drawLevelIndicator(g);
    }
}
