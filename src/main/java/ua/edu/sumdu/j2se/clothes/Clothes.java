package ua.edu.sumdu.j2se.clothes;

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
}
