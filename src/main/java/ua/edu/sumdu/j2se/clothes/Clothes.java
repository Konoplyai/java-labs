package ua.edu.sumdu.j2se.clothes;

import java.util.Objects;

/**
 * Одяг: назва, бренд, розмір, колір та ціна.
 */
public class Clothes {

    /** Допустимі розміри. */
    public static final String[] SIZES = {"XS", "S", "M", "L", "XL", "XXL"};

    private String name;
    private String brand;
    private String size;
    private String color;
    private double price;

    /**
     * Створює одяг із заданими параметрами.
     *
     * @param name назва
     * @param brand бренд
     * @param size розмір (XS, S, M, L, XL, XXL)
     * @param color колір
     * @param price ціна, грн
     * @throws IllegalArgumentException якщо якесь значення некоректне
     */
    public Clothes(String name, String brand, String size, String color, double price) {
        setName(name);
        setBrand(brand);
        setSize(size);
        setColor(color);
        setPrice(price);
    }

    /**
     * Повертає назву.
     *
     * @return назва
     */
    public String getName() {
        return name;
    }

    /**
     * Задає назву.
     *
     * @param name назва, не порожня
     * @throws IllegalArgumentException якщо назва порожня
     */
    public void setName(String name) {
        this.name = checkText(name, "назва");
    }

    /**
     * Повертає бренд.
     *
     * @return бренд
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Задає бренд.
     *
     * @param brand бренд, не порожній
     * @throws IllegalArgumentException якщо бренд порожній
     */
    public void setBrand(String brand) {
        this.brand = checkText(brand, "бренд");
    }

    /**
     * Повертає розмір.
     *
     * @return розмір
     */
    public String getSize() {
        return size;
    }

    /**
     * Задає розмір.
     *
     * @param size розмір, один з {@link #SIZES} (регістр не важливий)
     * @throws IllegalArgumentException якщо такого розміру немає
     */
    public void setSize(String size) {
        if (!isValidSize(size)) {
            throw new IllegalArgumentException("розмір має бути одним з: " + String.join(", ", SIZES));
        }
        this.size = size.trim().toUpperCase();
    }

    /**
     * Повертає колір.
     *
     * @return колір
     */
    public String getColor() {
        return color;
    }

    /**
     * Задає колір.
     *
     * @param color колір, не порожній
     * @throws IllegalArgumentException якщо колір порожній
     */
    public void setColor(String color) {
        this.color = checkText(color, "колір");
    }

    /**
     * Повертає ціну.
     *
     * @return ціна, грн
     */
    public double getPrice() {
        return price;
    }

    /**
     * Задає ціну.
     *
     * @param price ціна, грн, більше 0
     * @throws IllegalArgumentException якщо ціна не додатна
     */
    public void setPrice(double price) {
        if (!(price > 0) || Double.isInfinite(price)) {
            throw new IllegalArgumentException("ціна має бути більше 0");
        }
        this.price = price;
    }

    /**
     * Перевіряє, чи є такий розмір у списку допустимих.
     *
     * @param size розмір
     * @return true, якщо розмір допустимий
     */
    public static boolean isValidSize(String size) {
        if (size == null) {
            return false;
        }
        for (String s : SIZES) {
            if (s.equalsIgnoreCase(size.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Перевіряє, що текст не порожній, і прибирає пробіли по краях.
     *
     * @param value текст
     * @param field назва поля для повідомлення про помилку
     * @return текст без пробілів по краях
     */
    private static String checkText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("поле \"" + field + "\" не може бути порожнім");
        }
        return value.trim();
    }

    /**
     * Повертає опис одягу одним рядком.
     *
     * @return рядок з усіма полями
     */
    @Override
    public String toString() {
        return "Clothes{назва='" + name + "', бренд='" + brand + "', розмір=" + size
                + ", колір='" + color + "', ціна=" + String.format("%.2f", price) + " грн}";
    }

    /**
     * Порівнює одяг за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return true, якщо всі поля однакові
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Clothes other = (Clothes) o;
        return Double.compare(price, other.price) == 0
                && Objects.equals(name, other.name)
                && Objects.equals(brand, other.brand)
                && Objects.equals(size, other.size)
                && Objects.equals(color, other.color);
    }

    /**
     * Хеш-код, узгоджений з equals.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, brand, size, color, price);
    }
}
