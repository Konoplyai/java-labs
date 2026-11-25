package ua.edu.sumdu.j2se.clothes;

import java.util.Objects;

/**
 * Сорочки. Крім полів звичайного одягу мають довжину рукава та розмір коміра.
 */
public class Shirts extends Clothes {

    /** Мінімальний розмір коміра, см. */
    public static final int MIN_COLLAR = 34;
    /** Максимальний розмір коміра, см. */
    public static final int MAX_COLLAR = 50;

    private Sleeve sleeve;
    private int collar;

    /**
     * Створює сорочку з усіма параметрами.
     *
     * @param name назва
     * @param brand бренд
     * @param size розмір
     * @param color колір
     * @param material матеріал
     * @param releaseYear рік випуску
     * @param price ціна, грн
     * @param sleeve довжина рукава
     * @param collar розмір коміра, см
     * @throws InvalidClothesException якщо якесь значення некоректне
     */
    public Shirts(String name, String brand, Size size, String color, Material material,
                  int releaseYear, double price, Sleeve sleeve, int collar) {
        super(name, brand, size, color, material, releaseYear, price);
        setSleeve(sleeve);
        setCollar(collar);
    }

    /**
     * Створює сорочку на основі вже заданих спільних полів одягу.
     *
     * @param base одяг, з якого беруться спільні поля
     * @param sleeve довжина рукава
     * @param collar розмір коміра, см
     * @throws InvalidClothesException якщо base дорівнює null або значення некоректні
     */
    public Shirts(Clothes base, Sleeve sleeve, int collar) {
        super(base);
        setSleeve(sleeve);
        setCollar(collar);
    }

    /**
     * Конструктор копіювання.
     *
     * @param other сорочка, яка копіюється
     * @throws InvalidClothesException якщо other дорівнює null
     */
    public Shirts(Shirts other) {
        super(other);
        this.sleeve = other.sleeve;
        this.collar = other.collar;
    }

    /**
     * Повертає довжину рукава.
     *
     * @return довжина рукава
     */
    public Sleeve getSleeve() {
        return sleeve;
    }

    /**
     * Задає довжину рукава.
     *
     * @param sleeve довжина рукава, не null
     * @throws InvalidClothesException якщо значення не задано
     */
    public void setSleeve(Sleeve sleeve) {
        if (sleeve == null) {
            throw new InvalidClothesException("довжину рукава не задано");
        }
        this.sleeve = sleeve;
    }

    /**
     * Повертає розмір коміра.
     *
     * @return розмір коміра, см
     */
    public int getCollar() {
        return collar;
    }

    /**
     * Задає розмір коміра.
     *
     * @param collar розмір коміра від {@link #MIN_COLLAR} до {@link #MAX_COLLAR} см
     * @throws InvalidClothesException якщо значення поза межами
     */
    public void setCollar(int collar) {
        if (collar < MIN_COLLAR || collar > MAX_COLLAR) {
            throw new InvalidClothesException("розмір коміра має бути від " + MIN_COLLAR + " до " + MAX_COLLAR + " см");
        }
        this.collar = collar;
    }

    /**
     * Повертає тип речі.
     *
     * @return "Сорочка"
     */
    @Override
    public String getType() {
        return "Сорочка";
    }

    /**
     * Повертає опис сорочки одним рядком.
     *
     * @return рядок з усіма полями
     */
    @Override
    public String toString() {
        return "Shirts{" + baseInfo() + ", рукав=" + sleeve.getTitle() + ", комір=" + collar + " см}";
    }

    /**
     * Порівнює сорочки за всіма полями, включно з полями базового класу.
     *
     * @param o об'єкт для порівняння
     * @return true, якщо всі поля однакові
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Shirts other = (Shirts) o;
        return sleeve == other.sleeve && collar == other.collar;
    }

    /**
     * Хеш-код, узгоджений з equals.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sleeve, collar);
    }
}
