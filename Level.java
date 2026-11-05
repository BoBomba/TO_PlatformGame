import java.util.List;
import java.util.ArrayList;

public class Level {
    private final int levelNumber;
    private final List<Platform> platforms;
    private final List<Point> points;

    public Level(int levelNumber) {
        platforms = new ArrayList<>();
        points = new ArrayList<>();
        this.levelNumber = levelNumber;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void addPlatform(Platform platform) {
        platforms.add(platform);
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void erasePoint(Point point)
    {
        points.remove(point);
    }

    public List<Platform> getPlatforms() {
        return new ArrayList<>(platforms);
    }

    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    public void loadLevel(GamePanel gamePanel) {
        gamePanel.setPlatforms(getPlatforms());
        gamePanel.setPoints(getPoints());
    }
}
