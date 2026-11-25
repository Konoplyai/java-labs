package ua.edu.sumdu.j2se.clothes;

import java.util.Objects;

/**
 * Одяг: назва, бренд, розмір, колір та ціна.
 */
public class Clothes {

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
     */
    public Clothes(String name, String brand, String size, String color, double price) {
        setName(name);
        setBrand(brand);
        setSize(size);
        setColor(color);
        setPrice(price);
    }

    /**
     * @return назва
     */
    public String getName() {
        return name;
    }

    /**
     * @param name назва, не порожня
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return бренд
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand бренд
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return розмір
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size розмір
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return колір
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color колір
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return ціна, грн
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price ціна, грн
     */
    public void setPrice(double price) {
        this.price = price;
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
