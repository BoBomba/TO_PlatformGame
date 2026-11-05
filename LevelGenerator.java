import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.nio.charset.StandardCharsets;

public class LevelGenerator {
    private static final int PLATFORM_HEIGHT = 10;
    private static final int MIN_PLATFORM_WIDTH = 70;
    private static final int MAX_PLATFORM_WIDTH = 230;
    private static final int HAZARD_WIDTH = 24;
    static final int HAZARD_CLEARANCE =
            (int) Math.ceil(Character.DEFAULT_WIDTH * 1.10);
    private static final int MIN_HAZARD_PLATFORM_WIDTH =
            HAZARD_WIDTH + 2 * HAZARD_CLEARANCE;
    private static final int ABSOLUTE_MIN_GAP = 25;
    private static final int MIN_PLATFORM_Y = 350;
    private static final int MAX_PLATFORM_Y = 490;
    private static final int START_PLATFORM_Y = 450;
    private static final int JUMP_HEIGHT_SAFETY_MARGIN = 20;
    private static final int MAX_HEIGHT_CHANGE =
            Character.maximumJumpHeight() - JUMP_HEIGHT_SAFETY_MARGIN;
    private static final double MIN_JUMP_RANGE_RATIO = 0.40;
    private static final double MAX_JUMP_RANGE_RATIO = 0.80;

    public List<Level> generateLevels(int levelCount, String seed) {
        if (levelCount < 1) {
            throw new IllegalArgumentException("Liczba poziomów musi być większa od zera.");
        }
        if (seed == null) {
            throw new IllegalArgumentException("Seed nie może być pusty.");
        }

        Random random = new Random(seedToLong(seed));
        List<Level> levels = new ArrayList<>();
        int startY = START_PLATFORM_Y;
        for (int levelNumber = 1; levelNumber <= levelCount; levelNumber++) {
            Level level = generateLevel(levelNumber, levelNumber == levelCount, startY, random);
            levels.add(level);
            startY = getLastSafePlatformY(level);
        }
        return levels;
    }

    private long seedToLong(String seed) {
        long hash = 0xcbf29ce484222325L;
        for (byte value : seed.getBytes(StandardCharsets.UTF_8)) {
            hash ^= value & 0xffL;
            hash *= 0x100000001b3L;
        }
        return hash;
    }

    private Level generateLevel(int levelNumber, boolean finalLevel, int startY, Random random) {
        Level level = new Level(levelNumber);
        List<Platform> path = new ArrayList<>();
        int screenWidth = GameWindow.getWidthWindow();
        int x = 0;
        int y = startY;

        Platform platform = new Platform(x, y, randomPlatformWidth(random), PLATFORM_HEIGHT);
        while (true) {
            path.add(platform);
            level.addPlatform(platform);

            int remainingWidth = screenWidth - (platform.x + platform.width);
            if (remainingWidth <= ABSOLUTE_MIN_GAP + MIN_PLATFORM_WIDTH) {
                platform.width += remainingWidth;
                break;
            }

            int nextY = clamp(
                    y + random.nextInt(MAX_HEIGHT_CHANGE * 2 + 1) - MAX_HEIGHT_CHANGE,
                    MIN_PLATFORM_Y,
                    MAX_PLATFORM_Y);
            int minGap = minimumReachableGap(nextY - y);
            int maxGap = maximumReachableGap(nextY - y);

            if (remainingWidth <= maxGap + MAX_PLATFORM_WIDTH) {
                int minFinalWidth = Math.max(MIN_PLATFORM_WIDTH, remainingWidth - maxGap);
                int maxFinalWidth = Math.min(MAX_PLATFORM_WIDTH, remainingWidth - minGap);
                if (maxFinalWidth < minFinalWidth) {
                    platform.width += remainingWidth;
                    break;
                }
                int finalWidth = minFinalWidth
                        + random.nextInt(maxFinalWidth - minFinalWidth + 1);
                int finalX = screenWidth - finalWidth;
                platform = new Platform(finalX, nextY, finalWidth, PLATFORM_HEIGHT);
                path.add(platform);
                level.addPlatform(platform);
                break;
            }

            int gap = minGap + random.nextInt(maxGap - minGap + 1);
            x = platform.x + platform.width + gap;
            y = nextY;
            platform = new Platform(x, y, randomPlatformWidth(random), PLATFORM_HEIGHT);
        }

        for (int i = 1; i < path.size() - 1; i++) {
            Platform pathPlatform = path.get(i);
            boolean hasHazard = pathPlatform.width >= MIN_HAZARD_PLATFORM_WIDTH
                    && random.nextInt(100) < 20;
            if (hasHazard) {
                int hazardX = pathPlatform.x + pathPlatform.width / 2 - HAZARD_WIDTH / 2;
                level.addPlatform(new KillingPlatformDecorator(
                        new Platform(hazardX, pathPlatform.y - 12, HAZARD_WIDTH, 12)));
            }
            if (random.nextInt(100) < 65) {
                level.addPoint(new Point(
                        pathPlatform.x + pathPlatform.width / 2,
                        pathPlatform.y - (hasHazard ? Character.maximumJumpHeight() : 35)));
            }
        }

        Platform lastPlatform = path.get(path.size() - 1);
        if (finalLevel) {
            level.addPoint(new EndPoint(
                    Math.min(screenWidth - 40, lastPlatform.x + lastPlatform.width - 40),
                    lastPlatform.y - 35));
        } else if (random.nextBoolean()) {
            level.addPoint(new Point(
                    lastPlatform.x + lastPlatform.width / 2,
                    lastPlatform.y - 35));
        }

        return level;
    }

    private int getLastSafePlatformY(Level level) {
        int lastPlatformX = Integer.MIN_VALUE;
        int lastPlatformY = START_PLATFORM_Y;
        for (Platform platform : level.getPlatforms()) {
            if (!platform.isLethal() && platform.x > lastPlatformX) {
                lastPlatformX = platform.x;
                lastPlatformY = platform.y;
            }
        }
        return lastPlatformY;
    }

    private int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    static int maximumReachableGap(int verticalDifference) {
        return (int) Math.floor(jumpHorizontalRange(verticalDifference) * MAX_JUMP_RANGE_RATIO);
    }

    static int minimumReachableGap(int verticalDifference) {
        int maxGap = maximumReachableGap(verticalDifference);
        int minGap = Math.max(
                ABSOLUTE_MIN_GAP,
                (int) Math.ceil(jumpHorizontalRange(verticalDifference) * MIN_JUMP_RANGE_RATIO));
        return Math.min(minGap, maxGap);
    }

    private static int jumpHorizontalRange(int verticalDifference) {
        int velocity = Character.JUMP_VELOCITY;
        int displacement = 0;
        int previousDisplacement = 0;
        int landingFrames = 0;

        for (int frame = 1; frame <= 1000; frame++) {
            previousDisplacement = displacement;
            displacement += velocity;
            if (velocity >= 0
                    && previousDisplacement <= verticalDifference
                    && displacement >= verticalDifference) {
                landingFrames = frame;
                break;
            }
            velocity += Character.GRAVITY;
        }

        if (landingFrames == 0) {
            return ABSOLUTE_MIN_GAP;
        }

        return Character.MOVE_SPEED * landingFrames + 30 - 1;
    }

    private int randomPlatformWidth(Random random) {
        return MIN_PLATFORM_WIDTH
                + random.nextInt(MAX_PLATFORM_WIDTH - MIN_PLATFORM_WIDTH + 1);
    }
}
