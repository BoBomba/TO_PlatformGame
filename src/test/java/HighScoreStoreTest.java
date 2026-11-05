import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HighScoreStoreTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createsResultsFileWhenReadingBeforeAnyScoresExist() throws Exception {
        File resultFile = new File(temporaryFolder.getRoot(), "new-folder/scores.txt");
        HighScoreStore store = new HighScoreStore(resultFile.toPath());

        assertTrue(store.readResults().isEmpty());
        assertTrue(resultFile.isFile());
        assertEquals("# seed\tlevels\telapsedMillis\tpoints\tdeaths",
                Files.readAllLines(resultFile.toPath(), StandardCharsets.UTF_8).get(0));
    }

    @Test
    public void replacesOnlyWithBetterResultAndSortsBySeedThenDescendingLevelCount() throws Exception {
        File resultFile = new File(temporaryFolder.getRoot(), "scores.txt");
        HighScoreStore store = new HighScoreStore(resultFile.toPath());
        String seed = "seed\twith\nUnicode 🌱";

        assertTrue(store.recordResult(seed, 5, 10000, 4, 3));
        assertFalse(store.recordResult(seed, 5, 10001, 100, 0));
        assertTrue(store.recordResult(seed, 5, 10000, 5, 9));
        assertTrue(store.recordResult(seed, 5, 10000, 5, 2));
        assertFalse(store.recordResult(seed, 5, 10000, 5, 3));
        assertTrue(store.recordResult(seed, 10, 20000, 0, 0));
        assertTrue(store.recordResult("another-seed", 3, 5000, 0, 0));

        List<HighScoreStore.Result> results = store.readResults();
        assertEquals(3, results.size());
        assertEquals("another-seed", results.get(0).getSeed());
        assertEquals(seed, results.get(1).getSeed());
        assertEquals(10, results.get(1).getLevelCount());
        assertEquals(5, results.get(2).getLevelCount());
        assertEquals(5, results.get(2).getPoints());
        assertEquals(2, results.get(2).getDeaths());
        assertTrue(new String(Files.readAllBytes(resultFile.toPath()), StandardCharsets.UTF_8)
                .contains("seed\\twith\\nUnicode 🌱"));
    }
}
