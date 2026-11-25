package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести методів пошуку в магазині.
 */
class SearchTest {

    private Store store;
    private Clothes sweater;
    private Pants trousers;
    private Jeans jeans;
    private Shirts shirt;
    private FormalShirt formal;

    @BeforeEach
    void setUp() {
        sweater = new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
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

    @Test
    void findByNameShouldIgnoreCaseAndFindPart() {
        List<Clothes> found = store.findByName("  СОРОЧКА ");
        assertEquals(2, found.size());
        assertSame(shirt, found.get(0));
        assertSame(formal, found.get(1));
    }

    @Test
    void findByTypeShouldIncludeSubclasses() {
        assertEquals(2, store.findByType(Pants.class).size());
        assertEquals(1, store.findByType(Jeans.class).size());
        assertEquals(2, store.findByType(Shirts.class).size());
        assertEquals(5, store.findByType(Clothes.class).size());
    }

    @Test
    void findByPriceRangeShouldIncludeBorders() {
        List<Clothes> found = store.findByPriceRange(1299, 1899);
        assertEquals(3, found.size());
        assertTrue(found.contains(sweater));
        assertTrue(found.contains(trousers));
        assertTrue(found.contains(formal));
    }

    @Test
    void findBySizeShouldReturnOnlyThatSize() {
        assertEquals(2, store.findBySize(Size.M).size());
        assertEquals(3, store.findBySize(Size.L).size());
    }

    @Test
    void emptyResultWhenNothingMatches() {
        assertTrue(store.findByName("пальто").isEmpty());
        assertTrue(store.findBySize(Size.XXL).isEmpty());
        assertTrue(store.findByPriceRange(5000, 9000).isEmpty());
        assertTrue(new Store("Порожній", "м. Суми").findByName("светр").isEmpty());
    }

    @Test
    void searchShouldNotChangeStore() {
        List<Clothes> before = new ArrayList<>(store.getClothes());
        int quantity = store.getTotalQuantity();
        store.findByName("о");
        store.findByType(Shirts.class);
        store.findByPriceRange(0.01, 10000);
        store.findBySize(Size.L);
        assertEquals(before, store.getClothes());
        assertEquals(quantity, store.getTotalQuantity());
    }
}
