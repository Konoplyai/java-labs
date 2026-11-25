package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести класів-нащадків Pants і Shirts та поліморфізму.
 */
class InheritanceTest {

    /**
     * Створює коректний базовий об'єкт для тестів.
     *
     * @return одяг з правильними даними
     */
    private Clothes base() {
        return new Sweater("Річ", "Zara", Size.M, "синій", Material.COTTON, 2023, 999);
    }

    @Test
    void pantsShouldKeepBaseAndOwnFields() {
        Pants pants = new Pants(base(), 82, 100);
        assertEquals("Річ", pants.getName());
        assertEquals(82, pants.getWaist());
        assertEquals(100, pants.getLength());
        assertTrue(pants instanceof Clothes);
    }

    @Test
    void shouldThrowExceptionForInvalidPantsData() {
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Pants(base(), 20, 100);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Pants(base(), 82, 200);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Pants(null, 82, 100);
            }
        });
        Pants pants = new Pants(base(), 82, 100);
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                pants.setWaist(Pants.MAX_WAIST + 1);
            }
        });
    }

    @Test
    void shouldThrowExceptionForInvalidShirtsData() {
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Shirts(base(), null, 40);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Shirts(base(), Sleeve.LONG, 60);
            }
        });
        Shirts shirts = new Shirts(base(), Sleeve.SHORT, 40);
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                shirts.setCollar(Shirts.MIN_COLLAR - 1);
            }
        });
    }

    @Test
    void overriddenMethodsShouldBeCalledThroughBaseReference() {
        List<Clothes> items = new ArrayList<>();
        items.add(base());
        items.add(new Pants(base(), 82, 100));
        items.add(new Shirts(base(), Sleeve.LONG, 41));

        assertEquals("Светр", items.get(0).getType());
        assertEquals("Штани", items.get(1).getType());
        assertEquals("Сорочка", items.get(2).getType());
        assertTrue(items.get(0).toString().startsWith("Sweater{"));
        assertTrue(items.get(1).toString().startsWith("Pants{"));
        assertTrue(items.get(2).toString().contains("рукав=довгий"));
    }

    @Test
    void equalsShouldCompareTypeAndOwnFields() {
        assertEquals(new Pants(base(), 82, 100), new Pants(base(), 82, 100));
        assertNotEquals(new Pants(base(), 82, 100), new Pants(base(), 84, 100));
        assertNotEquals(base(), new Pants(base(), 82, 100));
        assertNotEquals(new Shirts(base(), Sleeve.LONG, 41), new Shirts(base(), Sleeve.SHORT, 41));
    }

    @Test
    void copyConstructorsShouldCopyAllFields() {
        Shirts shirts = new Shirts(base(), Sleeve.LONG, 41);
        assertEquals(shirts, new Shirts(shirts));
        Pants pants = new Pants(base(), 82, 100);
        assertEquals(pants, new Pants(pants));
    }
}
