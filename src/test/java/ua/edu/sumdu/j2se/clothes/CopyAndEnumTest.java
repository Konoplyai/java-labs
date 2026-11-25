package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тести конструктора копіювання та перерахувань.
 */
class CopyAndEnumTest {

    /**
     * Створює коректний об'єкт для тестів.
     *
     * @return одяг з правильними даними
     */
    private Clothes validClothes() {
        return new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299);
    }

    @Test
    void copyConstructorShouldCreateEqualButDifferentObject() {
        Clothes original = validClothes();
        Clothes copy = new Clothes(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.setColor("білий");
        assertEquals("сірий", original.getColor());
    }

    @Test
    void copyConstructorShouldThrowExceptionForNull() {
        assertThrows(InvalidClothesException.class, () -> new Clothes((Clothes) null));
    }

    @Test
    void enumsShouldBeFoundByText() {
        assertEquals(Size.XL, Size.fromString(" xl "));
        assertEquals(Material.LINEN, Material.fromString("Льон"));
        assertEquals(Material.COTTON, Material.fromString("cotton"));
        assertThrows(InvalidClothesException.class, () -> Size.fromString("XXXL"));
        assertThrows(InvalidClothesException.class, () -> Material.fromString("картон"));
    }
}
