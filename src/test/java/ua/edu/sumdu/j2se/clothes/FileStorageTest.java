package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.function.Executable;

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
 * Тести збереження товарів магазину у файли input.txt і input.json.
 */
class FileStorageTest {

    @TempDir
    Path dir;

    /**
     * Створює магазин з об'єктами всіх п'яти класів ієрархії, кількість у кожного різна.
     *
     * @return магазин
     */
    private Store sample() {
        Clothes base = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299.5);
        Pants pants = new Pants(base, 80, 104);
        Shirts shirts = new Shirts(base, Sleeve.LONG, 40);
        Store store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(base, 1);
        store.addNewClothes(pants, 2);
        store.addNewClothes(new Jeans(pants, Fit.SLIM, 13), 3);
        store.addNewClothes(shirts, 4);
        store.addNewClothes(new FormalShirt(shirts, true, false), 5);
        return store;
    }

    /**
     * Перевіряє, що в магазинах однакові товари з однаковою кількістю.
     *
     * @param expected очікуваний магазин
     * @param actual прочитаний магазин
     */
    private void assertSameGoods(Store expected, Store actual) {
        assertEquals(expected.getClothes(), actual.getClothes());
        for (Clothes clothes : expected.getClothes()) {
            assertEquals(expected.getQuantity(clothes), actual.getQuantity(clothes));
        }
    }

    @Test
    void textFileShouldRestoreSameGoods() throws IOException {
        TextFileStorage storage = new TextFileStorage(dir.resolve("input.txt").toString());
        storage.save(sample());
        List<String> errors = new ArrayList<>();
        Store loaded = new Store("Магазин одягу", "м. Суми");
        assertEquals(5, storage.load(loaded, errors));
        assertSameGoods(sample(), loaded);
        assertTrue(errors.isEmpty());
        assertTrue(loaded.getClothes().get(2) instanceof Jeans);
    }

    @Test
    void jsonFileShouldRestoreSameGoods() throws IOException {
        JsonFileStorage storage = new JsonFileStorage(dir.resolve("input.json").toString());
        storage.save(sample());
        List<String> errors = new ArrayList<>();
        Store loaded = new Store("Магазин одягу", "м. Суми");
        assertEquals(5, storage.load(loaded, errors));
        assertSameGoods(sample(), loaded);
        assertTrue(errors.isEmpty());
        assertTrue(loaded.getClothes().get(4) instanceof FormalShirt);
    }

    @Test
    void repeatedLineShouldIncreaseQuantity() throws IOException {
        Path file = dir.resolve("input.txt");
        Files.write(file, Arrays.asList(
                "Sweater;Футболка;Nike;M;білий;COTTON;2022;450;3",
                "Pants;Шорти;Puma;M;сірий;COTTON;2023;700;80;50;1",
                "Sweater;Футболка;Nike;M;білий;COTTON;2022;450;4"), StandardCharsets.UTF_8);
        Store store = new Store("Магазин одягу", "м. Суми");
        List<String> errors = new ArrayList<>();
        assertEquals(3, new TextFileStorage(file.toString()).load(store, errors));
        assertTrue(errors.isEmpty());
        assertEquals(2, store.size());
        assertEquals(7, store.getQuantity(store.getClothes().get(0)));
    }

    @Test
    void invalidLinesShouldBeSkipped() throws IOException {
        Path file = dir.resolve("input.txt");
        Files.write(file, Arrays.asList(
                "# коментар",
                "",
                "Sweater;Футболка;Nike;M;білий;COTTON;2022;450;2",
                "Hat;Кепка;Nike;S;червоний;COTTON;2022;450;1",
                "Sweater;Футболка;Nike;XXXL;білий;COTTON;2022;450;1",
                "Pants;Шорти;Puma;M;сірий;COTTON;2023;abc;80;50;1",
                "Sweater;Футболка;Nike;M;білий;COTTON;2022;450",
                "Sweater;Футболка;Nike;M;білий;COTTON;2022;450;0",
                "Sweater;Футболка;Nike;M;білий;COTTON;2022;450;багато",
                "Футболка"), StandardCharsets.UTF_8);
        Store store = new Store("Магазин одягу", "м. Суми");
        List<String> errors = new ArrayList<>();
        assertEquals(1, new TextFileStorage(file.toString()).load(store, errors));
        assertEquals(1, store.size());
        assertEquals(2, store.getTotalQuantity());
        assertEquals(7, errors.size());
    }

    @Test
    void jsonWithoutQuantityShouldBeSkipped() throws IOException {
        Path file = dir.resolve("input.json");
        Files.write(file, ("[{\"type\":\"Clothes\",\"name\":\"Футболка\",\"brand\":\"Nike\",\"size\":\"M\","
                + "\"color\":\"білий\",\"material\":\"COTTON\",\"year\":2022,\"price\":450}]")
                .getBytes(StandardCharsets.UTF_8));
        Store store = new Store("Магазин одягу", "м. Суми");
        List<String> errors = new ArrayList<>();
        assertEquals(0, new JsonFileStorage(file.toString()).load(store, errors));
        assertTrue(store.isEmpty());
        assertEquals(1, errors.size());
    }

    @Test
    void missingFileShouldGiveEmptyStore() throws IOException {
        List<String> errors = new ArrayList<>();
        Store store = new Store("Магазин одягу", "м. Суми");
        assertEquals(0, new TextFileStorage(dir.resolve("none.txt").toString()).load(store, errors));
        assertTrue(store.isEmpty());
        assertEquals(1, errors.size());
    }

    @Test
    void brokenJsonShouldThrowIOException() throws IOException {
        Path file = dir.resolve("input.json");
        Files.write(file, "{oops".getBytes(StandardCharsets.UTF_8));
        Store store = new Store("Магазин одягу", "м. Суми");
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new JsonFileStorage(file.toString()).load(store, new ArrayList<>());
            }
        });
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
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        new Sweater("a;b", "H&M", Size.L, "сірий", Material.WOOL, 2021, 100);
                    }
                });
    }
}
