import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel{
    private Character character;
    private Memento initialPosition;
    private List<Platform> platforms;
    private int elapsedTime = 0;
    private List<Point> points;


    int levelNumber = 1;

    int pointCount = 0;

    public GamePanel(Character character) {
        this.character = character;
        this.initialPosition = character.saveStateToMemento();

        this.setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        requestFocusInWindow();

        // Definiowanie akcji // wzorzec komenda
        Action moveLeftAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                character.moveLeft();
            }
        };

        Action moveRightAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                character.jump();
            }
        };

        Action stopJumpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                character.stopJump();
            }
        };

        // Mapowanie klawiszy
        bindKeyStrokeToAction("LEFT", moveLeftAction);
        bindKeyStrokeToAction("RIGHT", moveRightAction);
        bindKeyStrokeToAction("UP", jumpAction);
        bindKeyStrokeToAction("released LEFT", stopAction);
        bindKeyStrokeToAction("released RIGHT", stopAction);
        bindKeyStrokeToAction("released UP", stopJumpAction);


        //Edytor leveli
        KillingPlatformDecorator killingPlatform = new KillingPlatformDecorator(new Platform(400,250,10,100));

        Level lvl1 = new Level(1);
        lvl1.addPlatform(new Platform(25, 200, 200, 10));
        lvl1.addPlatform(new Platform(350, 300, 200, 10));

        lvl1.addPlatform(killingPlatform);

        lvl1.addPoint(new Point(650, 200));
        lvl1.addPlatform(new Platform(700, 300, 100, 10));


        Level lvl2 = new Level(2);
        lvl2.addPlatform(new Platform(0, 300, 100, 10));
        lvl2.addPlatform(new Platform(200, 250, 100, 10));
        lvl2.addPlatform(new Platform(400, 400, 130, 10));
        lvl2.addPoint(new Point(650, 300));
        lvl2.addPlatform(new Platform(600, 350, 250, 10));

        Level lvl3 = new Level(3);
        lvl3.addPoint(new Point(200, 300));
        lvl3.addPlatform(new Platform(0, 350, 800, 10));

        lvl3.addPlatform(killingPlatform);

        EndPoint endPoint = new EndPoint(700, 220);
        lvl3.addPoint(endPoint);

        lvl1.loadLevel(this);
        //lvl3.loadLevel(this);
        //levelNumber = 3;

        Timer[] timer = new Timer[1];

        timer[0] = new Timer(16, e -> {
            character.update(platforms);
            if (character.y > GameWindow.getHeightWindow()-30) {
                character.getStateFromMemento(initialPosition);
            }

            if (character.checkCollision(character.x, character.y, killingPlatform)) {
                KillingPlatformDecorator.resetCharacter(character, initialPosition);
            }

            if (character.x > GameWindow.getWidthWindow()) {
                if (levelNumber == 1){
                    // Przejście do kolejnego poziomu
                    lvl2.loadLevel(this);
                    character.x = 0;
                    levelNumber++;
                }
                else if (levelNumber == 2) {
                    lvl3.loadLevel(this);
                    character.x = 0;
                    levelNumber++;
                }
                else {
                    timer[0].stop();
                    endPoint.ending(pointCount, elapsedTime);
                }
            }

            // zbieranie punktów / iterator
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point point = pointIterator.next();
                if (point.checkCollision(character)) {
                    if (point instanceof EndPoint) {
                        // Logika końca gry dla EndPoint
                        endPoint.ending(pointCount, elapsedTime);
                        timer[0].stop();
                        return; // Zakończ pętlę i timer, jeśli dotknięto EndPoint
                    } else {
                        // Logika dla zwykłego punktu
                        pointIterator.remove();  // Bezpieczne usunięcie punktu
                        pointCount++;
                    }
                }
            }

            elapsedTime += 16;

            repaint();
        });
        timer[0].start();
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    private void bindKeyStrokeToAction(String keyStroke, Action action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
        getActionMap().put(keyStroke, action);
    }

    public void drawPointsSidebar(Graphics g) {
        g.setColor(new Color(0, 0, 0, 0)); // Set color to transparent
        int sidebarWidth = 100;
        int sidebarHeight = 50;
        int sidebarX = GameWindow.getWidthWindow() - sidebarWidth; // Position the sidebar on the right
        int sidebarY = 0; // Position the sidebar at the top
        g.fillRect(sidebarX, sidebarY, sidebarWidth, sidebarHeight); // Draw the sidebar

        g.setColor(Color.DARK_GRAY); // Set color to dark gray
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Points: " + pointCount, sidebarX + 10, sidebarY + 30); // Display the points
        g.drawString("Time: " + elapsedTime / 1000.0 + "s", sidebarX + 10, sidebarY + 60); // Display the time
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
    }


}

