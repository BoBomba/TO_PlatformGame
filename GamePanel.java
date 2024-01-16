import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel{
    private Character character;
    private Memento initialPosition;
    private List<Platform> platforms;

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

        Timer timer = new Timer(16, e -> {
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
                    endPoint.ending(pointCount);
                }
            }

            // zbieranie punktów / iterator
            Iterator<Point> pointIterator = points.iterator();
            while (pointIterator.hasNext()) {
                Point point = pointIterator.next();
                if (point.checkCollision(character)) {
                    if (point instanceof EndPoint) {
                        // Logika końca gry dla EndPoint
                        endPoint.ending(pointCount);
                        return; // Zakończ pętlę i timer, jeśli dotknięto EndPoint
                    } else {
                        // Logika dla zwykłego punktu
                        pointIterator.remove();  // Bezpieczne usunięcie punktu
                        pointCount++;
                    }
                }
            }


            repaint();
        });
        timer.start();
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
    }


}

