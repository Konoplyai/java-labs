package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести методів update і delete класу Store та пунктів меню 3 і 4.
 */
class UpdateDeleteTest {

    private Store store;
    private Sweater sweater;
    private Pants trousers;
    private FormalShirt formal;

    @BeforeEach
    void setUp() {
        sweater = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        trousers = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
        formal = new FormalShirt(new Shirts("Сорочка класична", "Arber", Size.L, "блакитний", Material.COTTON,
                2024, 1499, Sleeve.LONG, 40), true, false);
        store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(sweater, 5);
        store.addNewClothes(trousers, 3);
        store.addNewClothes(formal, 2);
    }

    /**
     * Запускає головне меню з заданим введенням і повертає вивід.
     *
     * @param input рядки введення через \n
     * @return текст виводу
     */
    private String runMenu(String input) {
        PrintStream old = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8);
            ClothesDriver.runMenu(scanner, store);
        } finally {
            System.setOut(old);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    void updateShouldReplaceDataAndKeepUuidQuantityAndPlace() {
        UUID uuid = trousers.getUuid();
        Pants newData = new Pants("Брюки", "Mango", Size.L, "сірий", Material.WOOL, 2022, 1999, 82, 104);
        assertTrue(store.update(trousers, newData));
        assertSame(newData, store.getClothes().get(1));
        assertEquals(uuid, newData.getUuid());
        assertEquals(3, store.getQuantity(newData));
        assertEquals(0, store.getQuantity(trousers));
        assertSame(newData, store.findByUuid(uuid));
    }

    @Test
    void updateShouldFindObjectByEquals() {
        Sweater sameAsStored = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        Sweater newData = new Sweater("Светр вовняний", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        assertTrue(store.update(sameAsStored, newData));
        assertEquals("Светр вовняний", store.getClothes().get(0).getName());
        assertEquals(sweater.getUuid(), newData.getUuid());
    }

    @Test
    void updateCanChangeClassOfObject() {
        Jeans jeans = new Jeans(new Pants(trousers, 80, 104), Fit.REGULAR, 12);
        assertTrue(store.update(trousers, jeans));
        assertTrue(store.getClothes().get(1) instanceof Jeans);
        assertEquals(3, store.size());
    }

    @Test
    void updateWithoutNewDataShouldThrow() {
        assertThrows(InvalidClothesException.class, () -> store.update(sweater, null));
        assertSame(sweater, store.getClothes().get(0));
    }

    @Test
    void updateWithSameDataShouldWork() {
        Sweater same = new Sweater(sweater);
        assertTrue(store.update(sweater, same));
        assertEquals(5, store.getQuantity(same));
    }

    @Test
    void deleteShouldRemoveWholeItem() {
        assertTrue(store.delete(trousers));
        assertEquals(2, store.size());
        assertFalse(store.getClothes().contains(trousers));
        assertEquals(7, store.getTotalQuantity());
    }

    @Test
    void deleteByEqualObject() {
        assertTrue(store.delete(new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299)));
        assertEquals(2, store.size());
    }

    @Test
    void menuModifyTextNumberAndEnumAttributes() {
        String output = runMenu("3\n1\n1\nСветр вовняний\n"
                + "3\n2\n7\n2100\n"
                + "3\n3\n5\nденім\n7\n");
        assertTrue(output.contains("1. Назва (зараз: Светр)"));
        assertTrue(output.contains("Товар змінено: Светр: Sweater{назва='Светр вовняний'"));
        assertEquals(2100, store.getClothes().get(1).getPrice(), 0.001);
        assertEquals(Material.DENIM, store.getClothes().get(2).getMaterial());
        assertEquals(sweater.getUuid(), store.getClothes().get(0).getUuid());
    }

    @Test
    void menuModifySubclassAttributesAndQuantity() {
        String output = runMenu("3\n2\n8\n90\n3\n3\n10\nні\n3\n1\n8\n12\n7\n");
        assertTrue(output.contains("8. Обхват талії, см (зараз: 80)"));
        assertEquals(90, ((Pants) store.getClothes().get(1)).getWaist());
        assertFalse(((FormalShirt) store.getClothes().get(2)).hasCufflinks());
        assertTrue(output.contains("Кількість змінено: Светр, 12 шт."));
    }

    @Test
    void menuModifyWithInvalidDataShouldNotChangeStore() {
        String longName = "Светр з дуже довгою назвою, яка не вміщується в п'ятдесят символів";
        String output = runMenu("3\n3\n8\nкороткий\n3\n1\n1\n" + longName + "\n7\n");
        assertTrue(output.contains("Помилка: манжети під запонки бувають лише на довгому рукаві"));
        assertTrue(output.contains("не може бути довшим за 50 символів"));
        assertEquals(2, output.split("Зміни не збережено.", -1).length - 1);
        assertSame(formal, store.getClothes().get(2));
        assertEquals("Светр", store.getClothes().get(0).getName());
    }

    @Test
    void menuDeleteWithConfirmation() {
        String output = runMenu("4\n2\nні\n4\n2\nтак\n7\n");
        assertTrue(output.contains("Видалення скасовано."));
        assertTrue(output.contains("Товар \"Брюки\" видалено з магазину."));
        assertEquals(2, store.size());
    }

    @Test
    void menuBackOptionsAndWrongNumbers() {
        String output = runMenu("3\n9\n0\n4\n0\n3\n1\n12\n0\n7\n");
        assertTrue(output.contains("число має бути від 0 до 3"));
        assertTrue(output.contains("число має бути від 0 до 8"));
        assertEquals(3, store.size());
    }

    @Test
    void menuWithEmptyStore() {
        store = new Store("Магазин одягу", "м. Суми");
        String output = runMenu("3\n4\n7\n");
        assertEquals(2, output.split("У магазині немає товарів.", -1).length - 1);
    }
}
