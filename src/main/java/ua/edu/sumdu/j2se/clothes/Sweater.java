package ua.edu.sumdu.j2se.clothes;

/**
 * Светр (кофта, світшот). Має лише спільні поля одягу.
 * Цей клас замінив звичайний об'єкт Clothes після того, як Clothes став абстрактним.
 */
public class Sweater extends Clothes {

    /**
     * Створює светр із заданими параметрами.
     *
     * @param name назва
     * @param brand бренд
     * @param size розмір
     * @param color колір
     * @param material матеріал
     * @param releaseYear рік випуску
     * @param price ціна, грн
     * @throws InvalidClothesException якщо якесь значення некоректне
     */
    public Sweater(String name, String brand, Size size, String color,
                   Material material, int releaseYear, double price) {
        super(name, brand, size, color, material, releaseYear, price);
    }

    /**
     * Створює светр зі спільних полів іншого одягу (конструктор копіювання).
     *
     * @param other одяг, з якого копіюються поля
     * @throws InvalidClothesException якщо other дорівнює null
     */
    public Sweater(Clothes other) {
        super(other);
    }

    /**
     * Повертає тип речі.
     *
     * @return "Светр"
     */
    @Override
    public String getType() {
        return "Светр";
    }

    /**
     * Повертає опис светра одним рядком.
     *
     * @return рядок з усіма полями
     */
    @Override
    public String toString() {
        return "Sweater{" + baseInfo() + uuidInfo() + "}";
    }
}
