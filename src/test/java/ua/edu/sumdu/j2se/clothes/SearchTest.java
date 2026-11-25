package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести методів пошуку в драйвері.
 */
class SearchTest {

    private List<Clothes> items;
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
        items = new ArrayList<>();
        items.add(sweater);
        items.add(trousers);
        items.add(jeans);
        items.add(shirt);
        items.add(formal);
    }

    @Test
    void findByNameShouldIgnoreCaseAndFindPart() {
        List<Clothes> found = ClothesDriver.findByName(items, "  СОРОЧКА ");
        assertEquals(2, found.size());
        assertSame(shirt, found.get(0));
        assertSame(formal, found.get(1));
    }

    @Test
    void findByTypeShouldIncludeSubclasses() {
        assertEquals(2, ClothesDriver.findByType(items, Pants.class).size());
        assertEquals(1, ClothesDriver.findByType(items, Jeans.class).size());
        assertEquals(2, ClothesDriver.findByType(items, Shirts.class).size());
        assertEquals(5, ClothesDriver.findByType(items, Clothes.class).size());
    }

    @Test
    void findByPriceRangeShouldIncludeBorders() {
        List<Clothes> found = ClothesDriver.findByPriceRange(items, 1299, 1899);
        assertEquals(3, found.size());
        assertTrue(found.contains(sweater));
        assertTrue(found.contains(trousers));
        assertTrue(found.contains(formal));
    }

    @Test
    void findBySizeShouldReturnOnlyThatSize() {
        assertEquals(2, ClothesDriver.findBySize(items, Size.M).size());
        assertEquals(3, ClothesDriver.findBySize(items, Size.L).size());
    }

    @Test
    void emptyResultWhenNothingMatches() {
        assertTrue(ClothesDriver.findByName(items, "пальто").isEmpty());
        assertTrue(ClothesDriver.findBySize(items, Size.XXL).isEmpty());
        assertTrue(ClothesDriver.findByPriceRange(items, 5000, 9000).isEmpty());
        assertTrue(ClothesDriver.findByName(new ArrayList<>(), "светр").isEmpty());
    }

    @Test
    void searchShouldNotChangeCollection() {
        List<Clothes> before = new ArrayList<>(items);
        ClothesDriver.findByName(items, "о");
        ClothesDriver.findByType(items, Shirts.class);
        ClothesDriver.findByPriceRange(items, 0.01, 10000);
        ClothesDriver.findBySize(items, Size.L);
        assertEquals(before, items);
    }
}
