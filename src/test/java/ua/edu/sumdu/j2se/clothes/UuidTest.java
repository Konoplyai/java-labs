package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести UUID: створення, збереження у файлах, пошук у магазині та через меню.
 */
class UuidTest {

    @TempDir
    Path dir;

    private Store store;
    private Sweater sweater;
    private Jeans jeans;

    @BeforeEach
    void setUp() {
        sweater = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        jeans = new Jeans(new Pants("Джинси", "Levi's", Size.M, "синій", Material.DENIM, 2023, 2599, 82, 102),
                Fit.SLIM, 13);
        store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(sweater, 5);
        store.addNewClothes(jeans, 4);
    }

    /**
     * Запускає підменю пошуку з заданим введенням і повертає вивід.
     *
     * @param input рядки введення через \n
     * @return текст виводу
     */
    private String runSearch(String input) {
        PrintStream old = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8);
            ClothesDriver.searchMenu(scanner, store);
        } finally {
            System.setOut(old);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    void everyObjectShouldGetOwnUuid() {
        assertTrue(sweater instanceof Identifiable);
        assertNotNull(sweater.getUuid());
        assertNotNull(jeans.getUuid());
        assertNotEquals(sweater.getUuid(), jeans.getUuid());
        Sweater copy = new Sweater(sweater);
        assertNotEquals(sweater.getUuid(), copy.getUuid());
        assertEquals(sweater, copy);
        assertEquals(0, sweater.compareTo(copy));
    }

    @Test
    void uuidShouldBeVisibleInOutput() {
        assertTrue(jeans.toString().contains("uuid=" + jeans.getUuid()));
        assertEquals("Джинси: Джинси | UUID: " + jeans.getUuid(), jeans.getShortInfo());
    }

    @Test
    void findByUuidInStore() {
        assertSame(jeans, store.findByUuid(jeans.getUuid()));
        assertSame(sweater, store.findByUuid(sweater.getUuid()));
        assertNull(store.findByUuid(UUID.randomUUID()));
    }

    @Test
    void mergedItemShouldKeepFirstUuid() {
        Sweater same = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        store.addNewClothes(same, 2);
        assertEquals(7, store.getQuantity(sweater));
        assertSame(sweater, store.findByUuid(sweater.getUuid()));
        assertNull(store.findByUuid(same.getUuid()));
    }

    @Test
    void differentItemWithSameUuidShouldBeRejected() {
        Sweater other = new Sweater("Кофта", "Nike", Size.S, "червоний", Material.COTTON, 2025, 700);
        other.restoreUuid(sweater.getUuid());
        assertThrows(DuplicateObjectException.class, () -> store.addNewClothes(other, 1));
        assertEquals(2, store.size());
    }

    @Test
    void parseUuidShouldCheckFormat() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, Identifiable.parseUuid("  " + uuid.toString().toUpperCase() + " "));
        assertThrows(IllegalArgumentException.class, () -> Identifiable.parseUuid("abc"));
        assertThrows(IllegalArgumentException.class, () -> Identifiable.parseUuid("1-2-3-4-5"));
        assertThrows(IllegalArgumentException.class, () -> Identifiable.parseUuid(""));
        assertThrows(IllegalArgumentException.class, () -> Identifiable.parseUuid(null));
        assertThrows(IllegalArgumentException.class,
                () -> Identifiable.parseUuid(uuid.toString().substring(1) + "g"));
    }

    @Test
    void uuidShouldStayTheSameAfterSaveAndLoad() throws IOException {
        TextFileStorage text = new TextFileStorage(dir.resolve("input.txt").toString());
        text.save(store);
        Store fromText = new Store("Магазин одягу", "м. Суми");
        text.load(fromText, new ArrayList<>());
        assertEquals(jeans, fromText.findByUuid(jeans.getUuid()));

        JsonFileStorage json = new JsonFileStorage(dir.resolve("input.json").toString());
        json.save(store);
        Store fromJson = new Store("Магазин одягу", "м. Суми");
        json.load(fromJson, new ArrayList<>());
        assertEquals(sweater, fromJson.findByUuid(sweater.getUuid()));
        assertEquals(4, fromJson.getQuantity(fromJson.findByUuid(jeans.getUuid())));
    }

    @Test
    void menuSearchByUuid() {
        String output = runSearch("5\n" + jeans.getUuid() + "\n0\n");
        assertTrue(output.contains("Знайдено: Джинси: Jeans{"));
        assertTrue(output.contains("в наявності 4 шт."));
    }

    @Test
    void menuSearchNotFoundAndWrongFormat() {
        UUID missing = UUID.randomUUID();
        String output = runSearch("5\n" + missing + "\n5\nне-uuid\n0\n");
        assertTrue(output.contains("Товар з UUID " + missing + " не знайдено."));
        assertTrue(output.contains("Помилка: некоректний формат UUID"));
    }
}
