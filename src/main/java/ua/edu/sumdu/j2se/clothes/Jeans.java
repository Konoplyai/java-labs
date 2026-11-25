package ua.edu.sumdu.j2se.clothes;

import java.util.Objects;

/**
 * Джинси - різновид штанів. Додатково мають крій і щільність деніму.
 */
public class Jeans extends Pants {

    /** Мінімальна щільність деніму, унцій. */
    public static final int MIN_DENSITY = 6;
    /** Максимальна щільність деніму, унцій. */
    public static final int MAX_DENSITY = 21;

    private Fit fit;
    private int density;

    /**
     * Створює джинси з усіма параметрами.
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
     * @param fit крій
     * @param density щільність деніму, унцій
     * @throws InvalidClothesException якщо якесь значення некоректне
     */
    public Jeans(String name, String brand, Size size, String color, Material material, int releaseYear,
                 double price, int waist, int length, Fit fit, int density) {
        super(name, brand, size, color, material, releaseYear, price, waist, length);
        setFit(fit);
        setDensity(density);
    }

    /**
     * Створює джинси на основі вже заданих полів штанів.
     *
     * @param base штани, з яких беруться поля
     * @param fit крій
     * @param density щільність деніму, унцій
     * @throws InvalidClothesException якщо base дорівнює null або значення некоректні
     */
    public Jeans(Pants base, Fit fit, int density) {
        super(base);
        setFit(fit);
        setDensity(density);
    }

    /**
     * Конструктор копіювання.
     *
     * @param other джинси, які копіюються
     * @throws InvalidClothesException якщо other дорівнює null
     */
    public Jeans(Jeans other) {
        super(other);
        this.fit = other.fit;
        this.density = other.density;
    }

    /**
     * Повертає крій.
     *
     * @return крій
     */
    public Fit getFit() {
        return fit;
    }

    /**
     * Задає крій.
     *
     * @param fit крій, не null
     * @throws InvalidClothesException якщо крій не задано
     */
    public void setFit(Fit fit) {
        if (fit == null) {
            throw new InvalidClothesException("крій не задано");
        }
        this.fit = fit;
    }

    /**
     * Повертає щільність деніму.
     *
     * @return щільність, унцій
     */
    public int getDensity() {
        return density;
    }

    /**
     * Задає щільність деніму.
     *
     * @param density щільність від {@link #MIN_DENSITY} до {@link #MAX_DENSITY} унцій
     * @throws InvalidClothesException якщо значення поза межами
     */
    public void setDensity(int density) {
        if (density < MIN_DENSITY || density > MAX_DENSITY) {
            throw new InvalidClothesException("щільність має бути від " + MIN_DENSITY + " до " + MAX_DENSITY + " унцій");
        }
        this.density = density;
    }

    /**
     * Визначає вид деніму за щільністю.
     *
     * @return "легкий" (до 11 oz), "середній" (12-15 oz) або "щільний" (від 16 oz)
     */
    public String getDensityKind() {
        if (density <= 11) {
            return "легкий";
        }
        if (density <= 15) {
            return "середній";
        }
        return "щільний";
    }

    /**
     * Повертає тип речі.
     *
     * @return "Джинси"
     */
    @Override
    public String getType() {
        return "Джинси";
    }

    /**
     * Повертає пораду з догляду за джинсами.
     *
     * @return порада з догляду
     */
    @Override
    public String getCareInstructions() {
        return "прати навиворіт при 30°C, щоб не вимивався колір, не сушити в машині";
    }

    /**
     * Повертає опис джинсів одним рядком.
     *
     * @return рядок з усіма полями
     */
    @Override
    public String toString() {
        return "Jeans{" + baseInfo() + ", талія=" + getWaist() + " см, довжина=" + getLength()
                + " см, крій=" + fit.getTitle() + ", денім=" + density + " oz (" + getDensityKind() + ")" + uuidInfo() + "}";
    }

    /**
     * Порівнює джинси за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return true, якщо всі поля однакові
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Jeans other = (Jeans) o;
        return fit == other.fit && density == other.density;
    }

    /**
     * Хеш-код, узгоджений з equals.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fit, density);
    }
}
