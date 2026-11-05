import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreStore {
    private static final String HEADER = "# seed\tlevels\telapsedMillis\tpoints\tdeaths";
    private static final Comparator<Result> RESULT_ORDER =
            Comparator.comparing(Result::getSeed)
                    .thenComparing(Comparator.comparingInt(Result::getLevelCount).reversed());

    private final Path file;

    public HighScoreStore() {
        this(Paths.get("best_results.txt"));
    }

    HighScoreStore(Path file) {
        this.file = file.toAbsolutePath().normalize();
    }

    public boolean recordResult(String seed, int levelCount, long elapsedMillis, int points, int deaths)
            throws IOException {
        if (seed == null || levelCount < 1 || elapsedMillis < 0 || points < 0 || deaths < 0) {
            throw new IllegalArgumentException("Dane wyniku są nieprawidłowe.");
        }

        List<Result> results = readResults();
        Result candidate = new Result(seed, levelCount, elapsedMillis, points, deaths);
        for (int i = 0; i < results.size(); i++) {
            Result previous = results.get(i);
            if (previous.seed.equals(seed) && previous.levelCount == levelCount) {
                if (!candidate.isBetterThan(previous)) {
                    return false;
                }
                results.set(i, candidate);
                writeResults(results);
                return true;
            }
        }

        results.add(candidate);
        writeResults(results);
        return true;
    }

    public List<Result> readResults() throws IOException {
        if (!Files.exists(file)) {
            Path parent = file.getParent();
            Files.createDirectories(parent);
            Files.write(file, java.util.Collections.singletonList(HEADER), StandardCharsets.UTF_8);
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty() || line.equals(HEADER)) {
                continue;
            }

            String[] fields = line.split("\t", -1);
            if (fields.length != 5) {
                throw new IOException("Nieprawidłowy format wyników w wierszu " + (i + 1) + ".");
            }
            try {
                String seed = unescapeSeed(fields[0], i + 1);
                int levelCount = Integer.parseInt(fields[1]);
                long elapsedMillis = Long.parseLong(fields[2]);
                int points = Integer.parseInt(fields[3]);
                int deaths = Integer.parseInt(fields[4]);
                results.add(new Result(seed, levelCount, elapsedMillis, points, deaths));
            } catch (IllegalArgumentException exception) {
                throw new IOException("Nieprawidłowe dane wyników w wierszu " + (i + 1) + ".", exception);
            }
        }

        results.sort(RESULT_ORDER);
        return results;
    }

    private void writeResults(List<Result> results) throws IOException {
        results.sort(RESULT_ORDER);
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Result result : results) {
            lines.add(escapeSeed(result.seed) + "\t" + result.levelCount + "\t" + result.elapsedMillis
                    + "\t" + result.points + "\t" + result.deaths);
        }

        Path parent = file.getParent();
        Files.createDirectories(parent);
        Path temporaryFile = Files.createTempFile(parent, file.getFileName().toString(), ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private String escapeSeed(String seed) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < seed.length(); i++) {
            char character = seed.charAt(i);
            if (character == '\\') {
                escaped.append("\\\\");
            } else if (character == '\t') {
                escaped.append("\\t");
            } else if (character == '\n') {
                escaped.append("\\n");
            } else if (character == '\r') {
                escaped.append("\\r");
            } else if (character < 0x20 || character == 0x7f) {
                escaped.append(String.format("\\u%04x", (int) character));
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }

    private String unescapeSeed(String seed, int lineNumber) throws IOException {
        StringBuilder unescaped = new StringBuilder();
        for (int i = 0; i < seed.length(); i++) {
            char character = seed.charAt(i);
            if (character != '\\') {
                unescaped.append(character);
                continue;
            }
            if (++i >= seed.length()) {
                throw invalidSeedEscape(lineNumber);
            }

            char escapedCharacter = seed.charAt(i);
            if (escapedCharacter == '\\') {
                unescaped.append('\\');
            } else if (escapedCharacter == 't') {
                unescaped.append('\t');
            } else if (escapedCharacter == 'n') {
                unescaped.append('\n');
            } else if (escapedCharacter == 'r') {
                unescaped.append('\r');
            } else if (escapedCharacter == 'u' && i + 4 < seed.length()) {
                try {
                    unescaped.append((char) Integer.parseInt(seed.substring(i + 1, i + 5), 16));
                } catch (NumberFormatException exception) {
                    throw invalidSeedEscape(lineNumber);
                }
                i += 4;
            } else {
                throw invalidSeedEscape(lineNumber);
            }
        }
        return unescaped.toString();
    }

    private IOException invalidSeedEscape(int lineNumber) {
        return new IOException("Nieprawidłowy seed w wierszu " + lineNumber + ".");
    }

    public static class Result {
        private final String seed;
        private final int levelCount;
        private final long elapsedMillis;
        private final int points;
        private final int deaths;

        Result(String seed, int levelCount, long elapsedMillis, int points, int deaths) {
            this.seed = seed;
            this.levelCount = levelCount;
            this.elapsedMillis = elapsedMillis;
            this.points = points;
            this.deaths = deaths;
        }

        public String getSeed() {
            return seed;
        }

        public int getLevelCount() {
            return levelCount;
        }

        public long getElapsedMillis() {
            return elapsedMillis;
        }

        public int getPoints() {
            return points;
        }

        public int getDeaths() {
            return deaths;
        }

        private boolean isBetterThan(Result other) {
            if (elapsedMillis != other.elapsedMillis) {
                return elapsedMillis < other.elapsedMillis;
            }
            if (points != other.points) {
                return points > other.points;
            }
            return deaths < other.deaths;
        }
    }
}
