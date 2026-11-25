package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тести перевірки даних у класі Clothes.
 */
class ClothesTest {

    /**
     * Створює коректний об'єкт для тестів.
     *
     * @return одяг з правильними даними
     */
    private Clothes validClothes() {
        return new Sweater("Джинси", "Levi's", Size.M, "синій", Material.DENIM, 2023, 2199.90);
    }

    @Test
    void shouldCreateObjectWithValidData() {
        Clothes clothes = new Sweater("  Куртка ", "Columbia", Size.XL, "чорний", Material.POLYESTER, 2022, 4599);
        assertEquals("Куртка", clothes.getName());
        assertEquals(Size.XL, clothes.getSize());
        assertEquals(2022, clothes.getReleaseYear());
        assertEquals(4599, clothes.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenInvalidConstructorData() {
        assertThrows(InvalidClothesException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        new Sweater("", "Levi's", Size.M, "синій", Material.DENIM, 2023, 100);
                    }
                });
        assertThrows(InvalidClothesException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        new Sweater("Джинси", null, Size.M, "синій", Material.DENIM, 2023, 100);
                    }
                });
        assertThrows(InvalidClothesException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        new Sweater("Джинси", "Levi's", null, "синій", Material.DENIM, 2023, 100);
                    }
                });
        assertThrows(InvalidClothesException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        new Sweater("Джинси", "Levi's", Size.M, "синій", Material.DENIM, 1800, 100);
                    }
                });
        assertThrows(InvalidClothesException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        new Sweater("Джинси", "Levi's", Size.M, "синій", Material.DENIM, 2023, -5);
                    }
                });
    }

    @Test
    void shouldThrowExceptionWhenInvalidPriceInSetter() {
        Clothes clothes = validClothes();
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setPrice(0);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setPrice(-100);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setPrice(Double.NaN);
            }
        });
        assertEquals(2199.90, clothes.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenInvalidTextInSetters() {
        Clothes clothes = validClothes();
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setName(" ");
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setColor(null);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setBrand("a".repeat(51));
            }
        });
        assertEquals("Джинси", clothes.getName());
    }

    @Test
    void shouldThrowExceptionWhenInvalidYearOrSizeInSetters() {
        Clothes clothes = validClothes();
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setReleaseYear(Clothes.MIN_YEAR - 1);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setReleaseYear(3000);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setSize(null);
            }
        });
        assertThrows(InvalidClothesException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                clothes.setMaterial(null);
            }
        });
    }

    @Test
    void customExceptionShouldBeIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                validClothes().setPrice(-1);
            }
        });
    }

    @Test
    void equalsShouldCompareAllFields() {
        assertEquals(validClothes(), validClothes());
        Clothes other = validClothes();
        other.setColor("чорний");
        assertNotEquals(validClothes(), other);
    }
}
