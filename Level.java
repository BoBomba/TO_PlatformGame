import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Level {

    // mozliwe zastosowanie fabryki do tworzenia np. roznych poziomow

    private static int levelNumber;

    private List<Platform> platforms;

    private List<Point> points;

    public Level(int levelNumber) {
        platforms = new ArrayList<>();
        points = new ArrayList<>();
        this.levelNumber = levelNumber;
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
        return platforms;
    }

    public void loadLevel(GamePanel gamePanel) {
        gamePanel.setPlatforms(platforms);
        gamePanel.setPoints(points);
    }

}
