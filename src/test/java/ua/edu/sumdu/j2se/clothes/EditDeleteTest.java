package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести редагування і видалення товарів, повного сценарію роботи меню
 * та сортування за всіма критеріями з однаковими значеннями полів.
 */
class EditDeleteTest {

    private Store store;
    private Sweater sweater;
    private Pants trousers;
    private Shirts shirt;

    @BeforeEach
    void setUp() {
        sweater = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        trousers = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
        shirt = new Shirts("Сорочка", "Zara", Size.L, "білий", Material.LINEN, 2024, 999, Sleeve.SHORT, 41);
        store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(sweater, 5);
        store.addNewClothes(trousers, 3);
        store.addNewClothes(shirt, 6);
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
    void removePartOfQuantity() {
        assertEquals(2, store.removeClothes(sweater, 3));
        assertEquals(2, store.getQuantity(sweater));
        assertEquals(3, store.size());
    }

    @Test
    void removeWholeQuantityShouldRemoveItem() {
        assertEquals(0, store.removeClothes(trousers, 3));
        assertEquals(2, store.size());
        assertFalse(store.getClothes().contains(trousers));
        assertEquals(0, store.getQuantity(trousers));
    }

    @Test
    void wrongRemoveShouldThrowAndKeepData() {
        assertThrows(InvalidClothesException.class, () -> store.removeClothes(sweater, 0));
        assertThrows(InvalidClothesException.class, () -> store.removeClothes(sweater, 6));
        Sweater missing = new Sweater("Кофта", "Nike", Size.S, "червоний", Material.COTTON, 2025, 700);
        assertThrows(InvalidClothesException.class, () -> store.removeClothes(missing, 1));
        assertEquals(5, store.getQuantity(sweater));
        assertEquals(3, store.size());
    }

    @Test
    void setQuantityShouldChangeOnlyThatItem() {
        store.setQuantity(shirt, 10);
        assertEquals(10, store.getQuantity(shirt));
        assertEquals(5, store.getQuantity(sweater));
        assertThrows(InvalidClothesException.class, () -> store.setQuantity(shirt, 0));
        assertThrows(InvalidClothesException.class, () -> store.setQuantity(shirt, Store.MAX_QUANTITY + 1));
        assertEquals(10, store.getQuantity(shirt));
    }

    @Test
    void changePriceShouldUpdateItem() {
        store.changePrice(sweater, 1499.5);
        assertEquals(1499.5, sweater.getPrice(), 0.001);
        assertEquals(5, store.getQuantity(sweater));
        assertThrows(InvalidClothesException.class, () -> store.changePrice(sweater, -10));
        assertEquals(1499.5, sweater.getPrice(), 0.001);
    }

    @Test
    void changePriceShouldNotCreateDuplicate() {
        Sweater cheaper = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 999);
        store.addNewClothes(cheaper, 1);
        assertThrows(InvalidClothesException.class, () -> store.changePrice(cheaper, 1299));
        assertEquals(999, cheaper.getPrice(), 0.001);
        assertEquals(4, store.size());
    }

    @Test
    void sameValuesShouldBeSortedByName() {
        Sweater samePrice = new Sweater("Арафатка", "Zara", Size.M, "білий", Material.COTTON, 2024, 999);
        store.addNewClothes(samePrice, 6);
        for (int i = 2; i <= ClothesDriver.SORT_CRITERIA; i++) {
            List<Clothes> sorted = store.getSortedClothes(ClothesDriver.createComparator(i, store));
            assertTrue(sorted.indexOf(samePrice) < sorted.indexOf(shirt));
        }
        List<Clothes> byPrice = store.getSortedClothes(ClothesDriver.createComparator(2, store));
        assertSame(samePrice, byPrice.get(0));
        assertSame(shirt, byPrice.get(1));
    }

    @Test
    void menuEditPriceAndQuantity() {
        String output = runMenu("5\n1\n1\n1500\n5\n3\n2\n8\n7\n");
        assertTrue(output.contains("Ціну змінено"));
        assertTrue(output.contains("Кількість змінено: Сорочка, 8 шт."));
        assertEquals(1500, sweater.getPrice(), 0.001);
        assertEquals(8, store.getQuantity(shirt));
    }

    @Test
    void menuDeletePartAndWholeItem() {
        String output = runMenu("6\n1\n2\n6\n2\n3\n7\n");
        assertTrue(output.contains("Видалено 2 шт., залишилось 3 шт."));
        assertTrue(output.contains("Товар \"Брюки\" видалено з магазину."));
        assertEquals(2, store.size());
    }

    @Test
    void menuShouldHandleWrongNumbersAndBackOption() {
        String output = runMenu("6\n9\n0\n5\n1\n7\n0\n6\n1\n10\n1\n7\n");
        assertTrue(output.contains("число має бути від 0 до 3"));
        assertTrue(output.contains("число має бути від 0 до 2"));
        assertTrue(output.contains("число має бути від 1 до 5"));
        assertTrue(output.contains("Видалено 1 шт., залишилось 4 шт."));
        assertEquals(1299, sweater.getPrice(), 0.001);
    }

    @Test
    void editAndDeleteInEmptyStore() {
        store = new Store("Магазин одягу", "м. Суми");
        String output = runMenu("5\n6\n7\n");
        assertEquals(2, output.split("У магазині немає товарів.", -1).length - 1);
        assertTrue(output.contains("Роботу завершено."));
    }

    @Test
    void fullScenarioAddViewSortDelete() {
        store = new Store("Магазин одягу", "м. Суми");
        String output = runMenu("2\n1\nКофта\nNike\nS\nчервоний\nбавовна\n2025\n700\n2\n"
                + "3\n4\n2\n0\n6\n1\n2\n3\n7\n");
        assertTrue(output.contains("Додано новий товар: Sweater{назва='Кофта'"));
        assertTrue(output.contains("Усього товарів: 1, одиниць: 2"));
        assertTrue(output.contains("Товари, відсортовані: за ціною (від дешевших)"));
        assertTrue(output.contains("Товар \"Кофта\" видалено з магазину."));
        assertTrue(output.contains("У магазині немає товарів."));
        assertTrue(store.isEmpty());
    }
}
