package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Автотести для власних винятків: ObjectNotFoundException, DuplicateObjectException
 * та InvalidClothesException.
 */
class StoreExceptionTest {

    private Store store;
    private Sweater sweater;
    private Pants trousers;
    private Sweater missing;

    @BeforeEach
    void setUp() {
        sweater = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
        trousers = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
        missing = new Sweater("Кофта", "Nike", Size.S, "червоний", Material.COTTON, 2025, 700);
        store = new Store("Магазин одягу", "м. Суми");
        store.addNewClothes(sweater, 5);
        store.addNewClothes(trousers, 3);
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
    void shouldThrowObjectNotFoundExceptionWhenDeletingNonExistingObject() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class, () -> {
            store.delete(missing);
        });
        assertSame(missing, e.getMissing());
        assertEquals("товар \"Кофта\" не знайдено в магазині", e.getMessage());
        assertEquals(2, store.size());
    }

    @Test
    void shouldThrowObjectNotFoundExceptionWhenUpdatingNonExistingObject() {
        Sweater newData = new Sweater("Кофта", "Nike", Size.S, "червоний", Material.COTTON, 2025, 800);
        assertThrows(ObjectNotFoundException.class, () -> store.update(missing, newData));
        assertThrows(ObjectNotFoundException.class, () -> store.update(null, newData));
        assertEquals(2, store.size());
    }

    @Test
    void shouldThrowObjectNotFoundExceptionInEmptyStore() {
        Store empty = new Store("Порожній", "м. Суми");
        assertThrows(ObjectNotFoundException.class, () -> empty.delete(sweater));
        assertThrows(ObjectNotFoundException.class, () -> empty.setQuantity(sweater, 2));
        assertThrows(ObjectNotFoundException.class, () -> empty.delete(null));
    }

    @Test
    void deletingTwiceShouldThrowSecondTime() {
        assertTrue(store.delete(trousers));
        assertThrows(ObjectNotFoundException.class, () -> store.delete(trousers));
    }

    @Test
    void shouldThrowDuplicateObjectExceptionWhenUpdateCreatesDuplicate() {
        Pants copyOfTrousers = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
        DuplicateObjectException e = assertThrows(DuplicateObjectException.class,
                () -> store.update(sweater, copyOfTrousers));
        assertTrue(e.getMessage().contains("вже є в магазині"));
        assertSame(sweater, store.getClothes().get(0));
    }

    @Test
    void shouldThrowDuplicateObjectExceptionForSameUuid() {
        missing.restoreUuid(sweater.getUuid());
        assertThrows(DuplicateObjectException.class, () -> store.addNewClothes(missing, 1));
        assertEquals(2, store.size());
    }

    @Test
    void shouldThrowInvalidClothesExceptionForWrongValues() {
        assertThrows(InvalidClothesException.class,
                () -> new Sweater("", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299));
        assertThrows(InvalidClothesException.class, () -> sweater.setPrice(-1));
        assertThrows(InvalidClothesException.class, () -> store.addNewClothes(missing, 0));
        assertThrows(InvalidClothesException.class, () -> store.setQuantity(sweater, Store.MAX_QUANTITY + 1));
        assertEquals(1299, sweater.getPrice(), 0.001);
    }

    @Test
    void storeExceptionsShouldHaveCommonParent() {
        assertTrue(new ObjectNotFoundException(sweater) instanceof StoreException);
        assertTrue(new DuplicateObjectException("повтор") instanceof StoreException);
        assertTrue(new StoreException("помилка") instanceof RuntimeException);
        assertEquals("товар не задано", new ObjectNotFoundException(null).getMessage());
    }

    @Test
    void menuShouldShowMessageAndReturnToMenuAfterDuplicate() {
        Sweater cheaper = new Sweater("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 999);
        store.addNewClothes(cheaper, 1);
        String output = runMenu("3\n3\n7\n1299\n5\n7\n");
        assertTrue(output.contains("Помилка: товар \"Светр\" з такими даними вже є в магазині. Зміни не збережено."));
        assertTrue(output.contains("Усього товарів: 3"));
        assertTrue(output.contains("Роботу завершено."));
        assertEquals(999, store.getClothes().get(2).getPrice(), 0.001);
    }
}
