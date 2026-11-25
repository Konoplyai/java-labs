package ua.edu.sumdu.j2se.clothes;

import java.util.Objects;

/**
 * Штани. Крім полів звичайного одягу мають обхват талії та довжину.
 */
public class Pants extends Clothes {

    /** Мінімальний обхват талії, см. */
    public static final int MIN_WAIST = 50;
    /** Максимальний обхват талії, см. */
    public static final int MAX_WAIST = 150;
    /** Мінімальна довжина штанів, см. */
    public static final int MIN_LENGTH = 40;
    /** Максимальна довжина штанів, см. */
    public static final int MAX_LENGTH = 130;

    private int waist;
    private int length;

    /**
     * Створює штани з усіма параметрами.
     *
     * @param name назва
     * @param brand бренд
     * @param size розмір
     * @param color колір
     * @param material матеріал
     * @param releaseYear рік випуску
     * @param price ціна, грн
     * @param waist обхват талії, см
     * @param length довжина, см
     * @throws InvalidClothesException якщо якесь значення некоректне
     */
    public Pants(String name, String brand, Size size, String color, Material material,
                 int releaseYear, double price, int waist, int length) {
        super(name, brand, size, color, material, releaseYear, price);
        setWaist(waist);
        setLength(length);
    }

    /**
     * Створює штани на основі вже заданих спільних полів одягу.
     *
     * @param base одяг, з якого беруться спільні поля
     * @param waist обхват талії, см
     * @param length довжина, см
     * @throws InvalidClothesException якщо base дорівнює null або значення некоректні
     */
    public Pants(Clothes base, int waist, int length) {
        super(base);
        setWaist(waist);
        setLength(length);
    }

    /**
     * Конструктор копіювання.
     *
     * @param other штани, які копіюються
     * @throws InvalidClothesException якщо other дорівнює null
     */
    public Pants(Pants other) {
        super(other);
        this.waist = other.waist;
        this.length = other.length;
    }

    /**
     * Повертає обхват талії.
     *
     * @return обхват талії, см
     */
    public int getWaist() {
        return waist;
    }

    /**
     * Задає обхват талії.
     *
     * @param waist обхват талії від {@link #MIN_WAIST} до {@link #MAX_WAIST} см
     * @throws InvalidClothesException якщо значення поза межами
     */
    public void setWaist(int waist) {
        if (waist < MIN_WAIST || waist > MAX_WAIST) {
            throw new InvalidClothesException("обхват талії має бути від " + MIN_WAIST + " до " + MAX_WAIST + " см");
        }
        this.waist = waist;
    }

    /**
     * Повертає довжину штанів.
     *
     * @return довжина, см
     */
    public int getLength() {
        return length;
    }

    /**
     * Задає довжину штанів.
     *
     * @param length довжина від {@link #MIN_LENGTH} до {@link #MAX_LENGTH} см
     * @throws InvalidClothesException якщо значення поза межами
     */
    public void setLength(int length) {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new InvalidClothesException("довжина має бути від " + MIN_LENGTH + " до " + MAX_LENGTH + " см");
        }
        this.length = length;
    }

    /**
     * Повертає тип речі.
     *
     * @return "Штани"
     */
    @Override
    public String getType() {
        return "Штани";
    }

    /**
     * Повертає опис штанів одним рядком.
     *
     * @return рядок з усіма полями
     */
    @Override
    public String toString() {
        return "Pants{" + baseInfo() + ", талія=" + waist + " см, довжина=" + length + " см" + uuidInfo() + "}";
    }

    /**
     * Порівнює штани за всіма полями, включно з полями базового класу.
     *
     * @param o об'єкт для порівняння
     * @return true, якщо всі поля однакові
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Pants other = (Pants) o;
        return waist == other.waist && length == other.length;
    }

    /**
     * Хеш-код, узгоджений з equals.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), waist, length);
    }
}
