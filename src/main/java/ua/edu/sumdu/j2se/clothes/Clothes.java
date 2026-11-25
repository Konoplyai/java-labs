package ua.edu.sumdu.j2se.clothes;

import java.time.Year;
import java.util.Objects;

/**
 * Одяг: назва, бренд, розмір, колір, матеріал, рік випуску та ціна.
 */
public class Clothes {

    /** Допустимі розміри. */
    public static final String[] SIZES = {"XS", "S", "M", "L", "XL", "XXL"};
    /** Найменший допустимий рік випуску. */
    public static final int MIN_YEAR = 1950;
    /** Максимальна довжина текстових полів. */
    public static final int MAX_TEXT_LENGTH = 50;

    private String name;
    private String brand;
    private String size;
    private String color;
    private String material;
    private int releaseYear;
    private double price;

    /**
     * Створює одяг із заданими параметрами.
     *
     * @param name назва
     * @param brand бренд
     * @param size розмір (XS, S, M, L, XL, XXL)
     * @param color колір
     * @param material матеріал
     * @param releaseYear рік випуску
     * @param price ціна, грн
     * @throws InvalidClothesException якщо якесь значення некоректне
     */
    public Clothes(String name, String brand, String size, String color,
                   String material, int releaseYear, double price) {
        setName(name);
        setBrand(brand);
        setSize(size);
        setColor(color);
        setMaterial(material);
        setReleaseYear(releaseYear);
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
     * @throws InvalidClothesException якщо назва порожня або задовга
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
     * @throws InvalidClothesException якщо бренд порожній або задовгий
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
     * @throws InvalidClothesException якщо такого розміру немає
     */
    public void setSize(String size) {
        if (!isValidSize(size)) {
            throw new InvalidClothesException("розмір має бути одним з: " + String.join(", ", SIZES));
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
     * @throws InvalidClothesException якщо колір порожній або задовгий
     */
    public void setColor(String color) {
        this.color = checkText(color, "колір");
    }

    /**
     * Повертає матеріал.
     *
     * @return матеріал
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Задає матеріал.
     *
     * @param material матеріал, не порожній
     * @throws InvalidClothesException якщо матеріал порожній або задовгий
     */
    public void setMaterial(String material) {
        this.material = checkText(material, "матеріал");
    }

    /**
     * Повертає рік випуску.
     *
     * @return рік випуску
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Задає рік випуску.
     *
     * @param releaseYear рік випуску, від {@link #MIN_YEAR} до поточного року
     * @throws InvalidClothesException якщо рік поза допустимими межами
     */
    public void setReleaseYear(int releaseYear) {
        int currentYear = Year.now().getValue();
        if (releaseYear < MIN_YEAR || releaseYear > currentYear) {
            throw new InvalidClothesException("рік випуску має бути від " + MIN_YEAR + " до " + currentYear);
        }
        this.releaseYear = releaseYear;
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
     * @throws InvalidClothesException якщо ціна не додатна
     */
    public void setPrice(double price) {
        if (!(price > 0) || Double.isInfinite(price)) {
            throw new InvalidClothesException("ціна має бути більше 0");
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
     * @throws InvalidClothesException якщо текст порожній або довший за {@link #MAX_TEXT_LENGTH}
     */
    private static String checkText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidClothesException("поле \"" + field + "\" не може бути порожнім");
        }
        if (value.trim().length() > MAX_TEXT_LENGTH) {
            throw new InvalidClothesException("поле \"" + field + "\" не може бути довшим за "
                    + MAX_TEXT_LENGTH + " символів");
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
                + ", колір='" + color + "', матеріал='" + material + "', рік=" + releaseYear
                + ", ціна=" + String.format("%.2f", price) + " грн}";
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
                && Objects.equals(color, other.color)
                && Objects.equals(material, other.material)
                && releaseYear == other.releaseYear;
    }

    /**
     * Хеш-код, узгоджений з equals.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, brand, size, color, material, releaseYear, price);
    }
}
