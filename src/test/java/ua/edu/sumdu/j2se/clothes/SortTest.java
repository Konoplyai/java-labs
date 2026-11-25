package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести абстрактного класу Clothes, методу compareTo та пункту меню з сортуванням.
 */
class SortTest {

    private final Sweater sweater = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
    private final Pants trousers = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
    private final Jeans jeans = new Jeans(
            new Pants("Джинси", "Levi's", Size.M, "синій", Material.DENIM, 2023, 2599, 82, 102), Fit.SLIM, 13);
    private final Shirts shirt = new Shirts("сорочка", "Zara", Size.L, "білий", Material.LINEN, 2024, 999,
            Sleeve.SHORT, 41);
    private final Sweater hoodie = new Sweater("Їжак худі", "Nike", Size.S, "чорний", Material.COTTON, 2025, 1500);
    private final Sweater jumper = new Sweater("Іграшкова кофта", "Nike", Size.S, "жовтий", Material.COTTON, 2025, 700);

    /**
     * Запускає меню з заданим введенням і повертає все, що програма вивела.
     *
     * @param store магазин
     * @param input рядки введення через \n
     * @return текст виводу
     */
    private String runMenu(Store store, String input) {
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
    void clothesShouldBeAbstractAndComparable() {
        assertTrue(Modifier.isAbstract(Clothes.class.getModifiers()));
        assertTrue(Comparable.class.isAssignableFrom(Clothes.class));
    }

    @Test
    void compareToShouldUseNameIgnoringCase() {
        assertTrue(trousers.compareTo(sweater) < 0);
        assertTrue(sweater.compareTo(shirt) < 0);
        assertTrue(shirt.compareTo(sweater) > 0);
        assertEquals(0, sweater.compareTo(new Sweater(sweater)));
    }

    @Test
    void ukrainianLettersShouldBeInAlphabetOrder() {
        List<Clothes> items = new ArrayList<>();
        items.add(hoodie);
        items.add(jumper);
        items.add(jeans);
        Collections.sort(items);
        assertSame(jeans, items.get(0));
        assertSame(jumper, items.get(1));
        assertSame(hoodie, items.get(2));
    }

    @Test
    void sameNameShouldBeOrderedByOtherFields() {
        Sweater cheap = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 999);
        Sweater zara = new Sweater("Светр", "Zara", Size.L, "сірий", Material.WOOL, 2021, 500);
        Pants samePants = new Pants("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299, 80, 104);
        assertTrue(cheap.compareTo(sweater) < 0);
        assertTrue(sweater.compareTo(zara) < 0);
        assertTrue(samePants.compareTo(sweater) < 0);
        assertTrue(sweater.compareTo(samePants) > 0);
        Pants otherWaist = new Pants("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299, 90, 104);
        assertTrue(samePants.compareTo(otherWaist) != 0);
        assertFalse(samePants.equals(otherWaist));
    }

    @Test
    void compareToNullShouldThrow() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                sweater.compareTo(null);
            }
        });
    }

    @Test
    void sortedListShouldNotChangeStore() {
        Store store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(sweater, 5);
        store.addNewClothes(trousers, 3);
        store.addNewClothes(shirt, 6);
        store.addNewClothes(jeans, 4);
        List<Clothes> sorted = store.getSortedClothes();
        assertSame(trousers, sorted.get(0));
        assertSame(jeans, sorted.get(1));
        assertSame(sweater, sorted.get(2));
        assertSame(shirt, sorted.get(3));
        assertSame(sweater, store.getClothes().get(0));
        assertSame(jeans, store.getClothes().get(3));
    }

    @Test
    void sortingEmptyAndSingleStore() {
        Store store = new Store("Магазин одягу", "м. Суми");
        assertTrue(store.getSortedClothes().isEmpty());
        store.addNewClothes(sweater, 1);
        assertEquals(1, store.getSortedClothes().size());
        assertSame(sweater, store.getSortedClothes().get(0));
    }

    @Test
    void menuItemFourShouldPrintSortedList() {
        Store store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(sweater, 5);
        store.addNewClothes(trousers, 3);
        String output = runMenu(store, "4\n1\n0\n5\n");
        assertTrue(output.contains("1. Брюки"));
        assertTrue(output.contains("2. Светр"));
        assertTrue(output.indexOf("1. Брюки") < output.indexOf("2. Светр"));
    }

    @Test
    void menuShouldWorkWithEmptyStoreAndWrongInput() {
        String output = runMenu(new Store("Магазин одягу", "м. Суми"), "abc\n\n9\n4\n1\n0\n5\n");
        assertTrue(output.contains("потрібно ввести ціле число"));
        assertTrue(output.contains("значення не може бути порожнім"));
        assertTrue(output.contains("число має бути від 1 до 5"));
        assertTrue(output.contains("У магазині немає товарів."));
        assertTrue(output.contains("Роботу завершено."));
    }
}
