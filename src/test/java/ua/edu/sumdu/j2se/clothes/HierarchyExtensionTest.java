package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести класів Jeans і FormalShirt.
 */
class HierarchyExtensionTest {

    /**
     * Створює коректні штани для тестів.
     *
     * @return штани
     */
    private Pants pants() {
        return new Pants("Джинси", "Levi's", Size.M, "синій", Material.DENIM, 2023, 2500, 82, 102);
    }

    /**
     * Створює коректну сорочку з довгим рукавом для тестів.
     *
     * @return сорочка
     */
    private Shirts longShirt() {
        return new Shirts("Сорочка", "Arber", Size.L, "білий", Material.COTTON, 2024, 1500, Sleeve.LONG, 40);
    }

    @Test
    void jeansShouldBePantsAndClothes() {
        Jeans jeans = new Jeans(pants(), Fit.SLIM, 13);
        assertTrue(jeans instanceof Pants);
        assertTrue(jeans instanceof Clothes);
        assertEquals(82, jeans.getWaist());
        assertEquals("середній", jeans.getDensityKind());
    }

    @Test
    void shouldThrowExceptionForInvalidJeansData() {
        assertThrows(InvalidClothesException.class, () -> new Jeans(pants(), null, 13));
        assertThrows(InvalidClothesException.class, () -> new Jeans(pants(), Fit.SLIM, 30));
        Jeans jeans = new Jeans(pants(), Fit.SLIM, 13);
        assertThrows(InvalidClothesException.class, () -> jeans.setDensity(Jeans.MIN_DENSITY - 1));
    }

    @Test
    void densityKindShouldDependOnDensity() {
        assertEquals("легкий", new Jeans(pants(), Fit.REGULAR, 9).getDensityKind());
        assertEquals("щільний", new Jeans(pants(), Fit.REGULAR, 18).getDensityKind());
    }

    @Test
    void cufflinksShouldBeOnlyOnLongSleeve() {
        Shirts shortShirt = new Shirts("Сорочка", "Arber", Size.L, "білий", Material.COTTON, 2024, 1500,
                Sleeve.SHORT, 40);
        assertThrows(InvalidClothesException.class, () -> new FormalShirt(shortShirt, true, false));
        FormalShirt formal = new FormalShirt(longShirt(), true, false);
        assertTrue(formal.hasCufflinks());
        formal.setSleeve(Sleeve.SHORT);
        assertFalse(formal.hasCufflinks());
    }

    @Test
    void careInstructionsShouldBeOverridden() {
        List<Clothes> items = new ArrayList<>();
        items.add(new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299));
        items.add(new Jeans(pants(), Fit.SLIM, 13));
        items.add(new FormalShirt(longShirt(), true, true));

        assertTrue(items.get(0).getCareInstructions().contains("ручне прання"));
        assertTrue(items.get(1).getCareInstructions().contains("навиворіт"));
        assertTrue(items.get(2).getCareInstructions().contains("прасувати не потрібно"));
        assertEquals("Класична сорочка", items.get(2).getType());
    }

    @Test
    void equalsShouldDistinguishSubclasses() {
        Pants pants = pants();
        Jeans jeans = new Jeans(pants, Fit.SLIM, 13);
        assertNotEquals(pants, jeans);
        assertEquals(jeans, new Jeans(jeans));
        assertNotEquals(new FormalShirt(longShirt(), true, false), new FormalShirt(longShirt(), false, false));
    }
}
