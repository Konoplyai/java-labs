package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести сортування за різними критеріями (анонімні класи Comparator) і підменю сортування.
 */
class ComparatorTest {

    private Store store;
    private Sweater sweater;
    private Pants trousers;
    private Jeans jeans;
    private Shirts shirt;
    private FormalShirt formal;

    @BeforeEach
    void setUp() {
        sweater = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        trousers = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
        jeans = new Jeans(new Pants("Джинси", "Levi's", Size.M, "синій", Material.DENIM, 2023, 2599, 82, 102),
                Fit.SLIM, 13);
        shirt = new Shirts("Сорочка", "Zara", Size.L, "білий", Material.LINEN, 2024, 999, Sleeve.SHORT, 41);
        formal = new FormalShirt(new Shirts("Сорочка класична", "Arber", Size.L, "блакитний", Material.COTTON,
                2024, 1499, Sleeve.LONG, 40), true, false);
        store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(sweater, 5);
        store.addNewClothes(trousers, 3);
        store.addNewClothes(jeans, 4);
        store.addNewClothes(shirt, 6);
        store.addNewClothes(formal, 2);
    }

    /**
     * Сортує товари магазину за критерієм з драйвера.
     *
     * @param criterion номер критерію
     * @return відсортований список
     */
    private List<Clothes> sortBy(int criterion) {
        return store.getSortedClothes(ClothesDriver.createComparator(criterion, store));
    }

    /**
     * Запускає підменю сортування з заданим введенням і повертає вивід.
     *
     * @param target магазин
     * @param input рядки введення через \n
     * @return текст виводу
     */
    private String runSortMenu(Store target, String input) {
        PrintStream old = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8);
            ClothesDriver.sortMenu(scanner, target);
        } finally {
            System.setOut(old);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    void sortByNameShouldUseCompareTo() {
        List<Clothes> sorted = sortBy(1);
        assertSame(trousers, sorted.get(0));
        assertSame(jeans, sorted.get(1));
        assertSame(sweater, sorted.get(2));
        assertSame(shirt, sorted.get(3));
        assertSame(formal, sorted.get(4));
    }

    @Test
    void sortByPriceShouldStartFromCheapest() {
        List<Clothes> sorted = sortBy(2);
        assertSame(shirt, sorted.get(0));
        assertSame(sweater, sorted.get(1));
        assertSame(formal, sorted.get(2));
        assertSame(trousers, sorted.get(3));
        assertSame(jeans, sorted.get(4));
    }

    @Test
    void sortByYearShouldStartFromNewestAndUseNameForSameYear() {
        List<Clothes> sorted = sortBy(3);
        assertSame(shirt, sorted.get(0));
        assertSame(formal, sorted.get(1));
        assertSame(jeans, sorted.get(2));
        assertSame(trousers, sorted.get(3));
        assertSame(sweater, sorted.get(4));
    }

    @Test
    void sortByQuantityShouldStartFromLargest() {
        List<Clothes> sorted = sortBy(4);
        assertSame(shirt, sorted.get(0));
        assertSame(sweater, sorted.get(1));
        assertSame(jeans, sorted.get(2));
        assertSame(trousers, sorted.get(3));
        assertSame(formal, sorted.get(4));
    }

    @Test
    void comparatorsShouldNotBeAnonymousClasses() {
        for (int i = 1; i <= ClothesDriver.SORT_CRITERIA; i++) {
            Comparator<Clothes> comparator = ClothesDriver.createComparator(i, store);
            assertFalse(comparator.getClass().isAnonymousClass());
        }
        assertThrows(IllegalArgumentException.class, () -> ClothesDriver.createComparator(0, store));
        assertThrows(IllegalArgumentException.class, () -> ClothesDriver.createComparator(5, store));
    }

    @Test
    void sortingShouldNotChangeStore() {
        List<Clothes> before = new ArrayList<>(store.getClothes());
        for (int i = 1; i <= ClothesDriver.SORT_CRITERIA; i++) {
            sortBy(i);
        }
        assertEquals(before, store.getClothes());
        assertThrows(IllegalArgumentException.class, () -> store.getSortedClothes(null));
    }

    @Test
    void oneAndZeroItems() {
        Store single = new Store("Магазин одягу", "м. Суми");
        for (int i = 1; i <= ClothesDriver.SORT_CRITERIA; i++) {
            assertTrue(single.getSortedClothes(ClothesDriver.createComparator(i, single)).isEmpty());
        }
        single.addNewClothes(sweater, 1);
        for (int i = 1; i <= ClothesDriver.SORT_CRITERIA; i++) {
            List<Clothes> sorted = single.getSortedClothes(ClothesDriver.createComparator(i, single));
            assertEquals(1, sorted.size());
            assertSame(sweater, sorted.get(0));
        }
    }

    @Test
    void sortMenuShouldPrintChosenOrderAndReturn() {
        String output = runSortMenu(store, "2\n0\n");
        assertTrue(output.contains("Оберіть критерій сортування:"));
        assertTrue(output.contains("за ціною (від дешевших)"));
        assertTrue(output.indexOf("1. Сорочка") < output.indexOf("5. Джинси"));
    }

    @Test
    void sortMenuShouldHandleWrongInputAndEmptyStore() {
        String output = runSortMenu(new Store("Магазин одягу", "м. Суми"), "x\n7\n3\n0\n");
        assertTrue(output.contains("потрібно ввести ціле число"));
        assertTrue(output.contains("число має бути від 0 до 4"));
        assertTrue(output.contains("У магазині немає товарів."));
        assertFalse(output.contains("Товари, відсортовані"));
    }

    @Test
    void zeroShouldReturnWithoutSorting() {
        String output = runSortMenu(store, "0\n");
        assertFalse(output.contains("Товари, відсортовані"));
    }
}
