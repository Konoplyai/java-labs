package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести збереження одягу у файли input.txt і input.json.
 */
class FileStorageTest {

    @TempDir
    Path dir;

    /**
     * Створює список з об'єктів усіх п'яти класів ієрархії.
     *
     * @return список одягу
     */
    private List<Clothes> sample() {
        Clothes base = new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299.5);
        Pants pants = new Pants(base, 80, 104);
        Shirts shirts = new Shirts(base, Sleeve.LONG, 40);
        List<Clothes> items = new ArrayList<>();
        items.add(base);
        items.add(pants);
        items.add(new Jeans(pants, Fit.SLIM, 13));
        items.add(shirts);
        items.add(new FormalShirt(shirts, true, false));
        return items;
    }

    @Test
    void textFileShouldRestoreSameObjects() throws IOException {
        TextFileStorage storage = new TextFileStorage(dir.resolve("input.txt").toString());
        storage.save(sample());
        List<String> errors = new ArrayList<>();
        List<Clothes> loaded = storage.load(errors);
        assertEquals(sample(), loaded);
        assertTrue(errors.isEmpty());
        assertTrue(loaded.get(2) instanceof Jeans);
    }

    @Test
    void jsonFileShouldRestoreSameObjects() throws IOException {
        JsonFileStorage storage = new JsonFileStorage(dir.resolve("input.json").toString());
        storage.save(sample());
        List<String> errors = new ArrayList<>();
        List<Clothes> loaded = storage.load(errors);
        assertEquals(sample(), loaded);
        assertTrue(errors.isEmpty());
        assertTrue(loaded.get(4) instanceof FormalShirt);
    }

    @Test
    void invalidLinesShouldBeSkipped() throws IOException {
        Path file = dir.resolve("input.txt");
        Files.write(file, Arrays.asList(
                "# коментар",
                "",
                "Clothes;Футболка;Nike;M;білий;COTTON;2022;450",
                "Hat;Кепка;Nike;S;червоний;COTTON;2022;450",
                "Clothes;Футболка;Nike;XXXL;білий;COTTON;2022;450",
                "Pants;Шорти;Puma;M;сірий;COTTON;2023;abc;80;50",
                "Clothes;Футболка;Nike;M;білий;COTTON;2022;450"), StandardCharsets.UTF_8);
        List<String> errors = new ArrayList<>();
        List<Clothes> loaded = new TextFileStorage(file.toString()).load(errors);
        assertEquals(1, loaded.size());
        assertEquals(4, errors.size());
    }

    @Test
    void missingFileShouldGiveEmptyList() throws IOException {
        List<String> errors = new ArrayList<>();
        List<Clothes> loaded = new TextFileStorage(dir.resolve("none.txt").toString()).load(errors);
        assertTrue(loaded.isEmpty());
        assertEquals(1, errors.size());
    }

    @Test
    void brokenJsonShouldThrowIOException() throws IOException {
        Path file = dir.resolve("input.json");
        Files.write(file, "{oops".getBytes(StandardCharsets.UTF_8));
        assertThrows(IOException.class, () -> new JsonFileStorage(file.toString()).load(new ArrayList<>()));
    }

    @Test
    void saveShouldKeepBackupOfOldFile() throws IOException {
        Path file = dir.resolve("input.txt");
        Files.write(file, Arrays.asList("old"), StandardCharsets.UTF_8);
        new TextFileStorage(file.toString()).save(sample());
        assertEquals(Arrays.asList("old"), Files.readAllLines(dir.resolve("input.txt.bak"), StandardCharsets.UTF_8));
    }

    @Test
    void textShouldNotContainSeparator() {
        assertThrows(InvalidClothesException.class,
                () -> new Clothes("a;b", "H&M", Size.L, "сірий", Material.WOOL, 2021, 100));
    }
}
