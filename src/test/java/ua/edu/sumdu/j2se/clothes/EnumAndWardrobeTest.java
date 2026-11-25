package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести конструктора копіювання, статичного лічильника, enum і класу Wardrobe.
 */
class EnumAndWardrobeTest {

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
    void counterShouldGrowForEveryCreatedObject() {
        int before = Clothes.getCreatedCount();
        Clothes clothes = validClothes();
        new Clothes(clothes);
        assertEquals(before + 2, Clothes.getCreatedCount());
    }

    @Test
    void counterShouldNotGrowWhenDataInvalid() {
        int before = Clothes.getCreatedCount();
        assertThrows(InvalidClothesException.class,
                () -> new Clothes("", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299));
        assertEquals(before, Clothes.getCreatedCount());
    }

    @Test
    void enumsShouldBeFoundByText() {
        assertEquals(Size.XL, Size.fromString(" xl "));
        assertEquals(Material.LINEN, Material.fromString("Льон"));
        assertEquals(Material.COTTON, Material.fromString("cotton"));
        assertThrows(InvalidClothesException.class, () -> Size.fromString("XXXL"));
        assertThrows(InvalidClothesException.class, () -> Material.fromString("картон"));
    }

    @Test
    void wardrobeShouldNotAddDuplicatesOrNull() {
        Wardrobe wardrobe = new Wardrobe("Іван");
        assertTrue(wardrobe.add(validClothes()));
        assertFalse(wardrobe.add(validClothes()));
        assertThrows(IllegalArgumentException.class, () -> wardrobe.add(null));
        assertEquals(1, wardrobe.size());
        assertEquals(1299, wardrobe.getTotalPrice());
    }

    @Test
    void wardrobeShouldValidateOwnerAndIndex() {
        assertThrows(IllegalArgumentException.class, () -> new Wardrobe("  "));
        Wardrobe wardrobe = new Wardrobe("Іван");
        assertThrows(IllegalArgumentException.class, () -> wardrobe.get(0));
        assertThrows(UnsupportedOperationException.class, () -> wardrobe.getItems().add(validClothes()));
    }
}
