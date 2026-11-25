package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести класу Store.
 */
class StoreTest {

    private Store store;
    private Clothes sweater;
    private Pants trousers;

    @BeforeEach
    void setUp() {
        store = new Store("Магазин одягу", "м. Суми");
        sweater = new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        trousers = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
    }

    @Test
    void newStoreShouldBeEmpty() {
        assertTrue(store.isEmpty());
        assertEquals(0, store.size());
        assertEquals(0, store.getTotalQuantity());
        assertEquals("Магазин одягу", store.getName());
        assertEquals("м. Суми", store.getAddress());
    }

    @Test
    void addNewClothesShouldAddNewItem() {
        assertTrue(store.addNewClothes(sweater, 5));
        assertTrue(store.addNewClothes(trousers, 3));
        assertEquals(2, store.size());
        assertEquals(5, store.getQuantity(sweater));
        assertEquals(3, store.getQuantity(trousers));
        assertEquals(8, store.getTotalQuantity());
    }

    @Test
    void addingSameItemShouldIncreaseQuantity() {
        store.addNewClothes(sweater, 5);
        Clothes sameSweater = new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        assertFalse(store.addNewClothes(sameSweater, 2));
        assertEquals(1, store.size());
        assertEquals(7, store.getQuantity(sweater));
        assertSame(sweater, store.getClothes().get(0));
    }

    @Test
    void differentPriceOrTypeShouldBeNewItem() {
        store.addNewClothes(sweater, 1);
        store.addNewClothes(new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1399), 1);
        store.addNewClothes(new Pants(sweater, 80, 104), 1);
        assertEquals(3, store.size());
    }

    @Test
    void wrongQuantityShouldThrow() {
        assertThrows(InvalidClothesException.class, () -> store.addNewClothes(sweater, 0));
        assertThrows(InvalidClothesException.class, () -> store.addNewClothes(sweater, -3));
        assertThrows(InvalidClothesException.class, () -> store.addNewClothes(sweater, Store.MAX_QUANTITY + 1));
        assertThrows(InvalidClothesException.class, () -> store.addNewClothes(null, 1));
        assertTrue(store.isEmpty());
    }

    @Test
    void totalQuantityShouldNotExceedMax() {
        store.addNewClothes(sweater, Store.MAX_QUANTITY - 1);
        assertThrows(InvalidClothesException.class, () -> store.addNewClothes(sweater, 2));
        assertEquals(Store.MAX_QUANTITY - 1, store.getQuantity(sweater));
        assertFalse(store.addNewClothes(sweater, 1));
        assertEquals(Store.MAX_QUANTITY, store.getQuantity(sweater));
    }

    @Test
    void quantityOfMissingItemShouldBeZero() {
        store.addNewClothes(sweater, 2);
        assertEquals(0, store.getQuantity(trousers));
        assertEquals(0, store.getQuantity(null));
    }

    @Test
    void totalValueShouldUseQuantity() {
        store.addNewClothes(sweater, 2);
        store.addNewClothes(trousers, 3);
        assertEquals(1299 * 2 + 1899 * 3, store.getTotalValue(), 0.001);
    }

    @Test
    void clothesListShouldBeReadOnly() {
        store.addNewClothes(sweater, 1);
        List<Clothes> items = store.getClothes();
        assertThrows(UnsupportedOperationException.class, () -> items.add(trousers));
        assertThrows(UnsupportedOperationException.class, () -> items.remove(0));
        assertEquals(1, store.size());
    }

    @Test
    void wrongStoreDataShouldThrow() {
        assertThrows(InvalidClothesException.class, () -> new Store("", "м. Суми"));
        assertThrows(InvalidClothesException.class, () -> new Store("Магазин", "   "));
        assertThrows(InvalidClothesException.class, () -> new Store(null, "м. Суми"));
    }
}
