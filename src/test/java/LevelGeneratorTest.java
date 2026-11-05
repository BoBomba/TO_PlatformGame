import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LevelGeneratorTest {
    @Test
    public void generatesRepeatableLevelsFromSameSeed() {
        LevelGenerator generator = new LevelGenerator();
        List<Level> first = generator.generateLevels(4, "test-seed-12345");
        List<Level> second = generator.generateLevels(4, "test-seed-12345");

        assertEquals(first.size(), second.size());
        for (int i = 0; i < first.size(); i++) {
            Level firstLevel = first.get(i);
            Level secondLevel = second.get(i);
            assertEquals(i + 1, firstLevel.getLevelNumber());
            assertEquals(firstLevel.getPlatforms().size(), secondLevel.getPlatforms().size());

            for (int j = 0; j < firstLevel.getPlatforms().size(); j++) {
                Platform firstPlatform = firstLevel.getPlatforms().get(j);
                Platform secondPlatform = secondLevel.getPlatforms().get(j);
                assertEquals(firstPlatform.x, secondPlatform.x);
                assertEquals(firstPlatform.y, secondPlatform.y);
                assertEquals(firstPlatform.width, secondPlatform.width);
                assertEquals(firstPlatform.isLethal(), secondPlatform.isLethal());
            }
        }
    }

    @Test
    public void addsAnEndPointOnlyToTheFinalLevelAndDoesNotSharePlatforms() {
        List<Level> levels = new LevelGenerator().generateLevels(2, "final-level-test");

        assertFalse(hasEndPoint(levels.get(0)));
        assertTrue(hasEndPoint(levels.get(1)));
        assertNotSame(levels.get(0).getPlatforms().get(0), levels.get(1).getPlatforms().get(0));
    }

    @Test
    public void keepsGeneratedSafePathInsideReachableBounds() {
        List<Level> levels = new LevelGenerator().generateLevels(10, "reachable-path-test");

        for (int levelIndex = 0; levelIndex < levels.size(); levelIndex++) {
            List<Platform> path = safePath(levels.get(levelIndex));

            assertTrue(path.size() > 1);
            assertEquals(0, path.get(0).x);
            if (levelIndex == 0) {
                assertEquals(450, path.get(0).y);
            } else {
                List<Platform> previousPath = safePath(levels.get(levelIndex - 1));
                assertEquals(previousPath.get(previousPath.size() - 1).y, path.get(0).y);
            }
            Platform lastPlatform = path.get(path.size() - 1);
            assertEquals(GameWindow.getWidthWindow(), lastPlatform.x + lastPlatform.width);

            for (int i = 1; i < path.size(); i++) {
                Platform previous = path.get(i - 1);
                Platform current = path.get(i);
                int gap = current.x - (previous.x + previous.width);
                assertTrue(gap >= LevelGenerator.minimumReachableGap(current.y - previous.y));
                assertTrue(gap <= LevelGenerator.maximumReachableGap(current.y - previous.y));
                assertTrue(Math.abs(current.y - previous.y)
                        <= Character.maximumJumpHeight() - 20);
            }
        }
    }

    @Test
    public void variesPlatformWidthsAndGapSizes() {
        List<Level> levels = new LevelGenerator().generateLevels(20, "platform-variety");
        int minWidth = Integer.MAX_VALUE;
        int maxWidth = Integer.MIN_VALUE;
        int minGap = Integer.MAX_VALUE;
        int maxGap = Integer.MIN_VALUE;

        for (Level level : levels) {
            List<Platform> path = safePath(level);
            for (int i = 0; i < path.size(); i++) {
                minWidth = Math.min(minWidth, path.get(i).width);
                maxWidth = Math.max(maxWidth, path.get(i).width);
                if (i > 0) {
                    int gap = path.get(i).x - (path.get(i - 1).x + path.get(i - 1).width);
                    minGap = Math.min(minGap, gap);
                    maxGap = Math.max(maxGap, gap);
                }
            }
        }

        assertTrue(maxWidth - minWidth >= 80);
        assertTrue(maxGap - minGap >= 20);
    }

    @Test
    public void placesCoinsAboveHazardsAtThePlayerJumpHeight() {
        List<Level> levels = new LevelGenerator().generateLevels(100, "coins-over-hazards");
        int matchingCoins = 0;

        for (Level level : levels) {
            List<Platform> platforms = level.getPlatforms();
            for (Platform hazard : platforms) {
                if (!hazard.isLethal()) {
                    continue;
                }

                int hazardCenterX = hazard.x + hazard.width / 2;
                for (Platform safePlatform : platforms) {
                    if (!safePlatform.isLethal()
                            && safePlatform.x <= hazardCenterX
                            && hazardCenterX < safePlatform.x + safePlatform.width) {
                        for (Point point : level.getPoints()) {
                            if (!(point instanceof EndPoint)
                                    && point.x == safePlatform.x + safePlatform.width / 2
                                    && point.y == safePlatform.y - Character.maximumJumpHeight()) {
                                matchingCoins++;
                            }
                        }
                    }
                }
            }
        }

        assertTrue(matchingCoins > 0);
        assertEquals(120, Character.maximumJumpHeight());
    }

    @Test
    public void leavesPlayerWidthOfClearanceOnBothSidesOfEachHazard() {
        List<Level> levels = new LevelGenerator().generateLevels(100, "hazard-clearance");
        int hazardCount = 0;

        for (Level level : levels) {
            List<Platform> platforms = level.getPlatforms();
            for (Platform hazard : platforms) {
                if (!hazard.isLethal()) {
                    continue;
                }

                hazardCount++;
                boolean hasSafeClearance = false;
                for (Platform platform : platforms) {
                    if (!platform.isLethal()
                            && platform.y == hazard.y + 12
                            && hazard.x >= platform.x + LevelGenerator.HAZARD_CLEARANCE
                            && hazard.x + hazard.width
                                    <= platform.x + platform.width - LevelGenerator.HAZARD_CLEARANCE) {
                        hasSafeClearance = true;
                        break;
                    }
                }
                assertTrue(hasSafeClearance);
            }
        }

        assertTrue(hazardCount > 0);
    }

    @Test
    public void rejectsZeroLevels() {
        try {
            new LevelGenerator().generateLevels(0, "test");
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Liczba poziomów musi być większa od zera.", expected.getMessage());
        }
    }

    @Test
    public void acceptsArbitraryTextAsSeed() {
        LevelGenerator generator = new LevelGenerator();
        List<Level> first = generator.generateLevels(3, "ćma 🌙 / spaces + 123");
        List<Level> second = generator.generateLevels(3, "ćma 🌙 / spaces + 123");

        assertEquals(first.get(0).getPlatforms().get(0).y,
                second.get(0).getPlatforms().get(0).y);
        assertEquals(first.get(1).getPlatforms().get(1).x,
                second.get(1).getPlatforms().get(1).x);
    }

    private boolean hasEndPoint(Level level) {
        for (Point point : level.getPoints()) {
            if (point instanceof EndPoint) {
                return true;
            }
        }
        return false;
    }

    private List<Platform> safePath(Level level) {
        List<Platform> path = new ArrayList<>();
        for (Platform platform : level.getPlatforms()) {
            if (!platform.isLethal()) {
                path.add(platform);
            }
        }
        return path;
    }
}
