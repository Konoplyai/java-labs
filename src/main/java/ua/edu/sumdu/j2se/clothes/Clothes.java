package ua.edu.sumdu.j2se.clothes;

import java.text.Collator;
import java.time.Year;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Одяг: назва, бренд, розмір, колір, матеріал, рік випуску та ціна.
 * Клас абстрактний: створюються лише об'єкти конкретних класів (Sweater, Pants, Jeans, Shirts, FormalShirt).
 * Об'єкти порівнюються за назвою за українським алфавітом (інтерфейс Comparable).
 * Кожен об'єкт під час створення отримує унікальний UUID (інтерфейс Identifiable).
 */
public abstract class Clothes implements Comparable<Clothes>, Identifiable {

    /** Найменший допустимий рік випуску. */
    public static final int MIN_YEAR = 1950;
    /** Максимальна довжина текстових полів. */
    public static final int MAX_TEXT_LENGTH = 50;
    /** Порівняння рядків за правилами українського алфавіту без урахування регістру. */
    private static final Collator COLLATOR = Collator.getInstance(new Locale("uk", "UA"));

    static {
        COLLATOR.setStrength(Collator.SECONDARY);
    }

    private String name;
    private String brand;
    private Size size;
    private String color;
    private Material material;
    private int releaseYear;
    private double price;
    private UUID uuid = UUID.randomUUID();

    /**
     * Створює одяг із заданими параметрами.
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
    public Clothes(String name, String brand, Size size, String color,
                   Material material, int releaseYear, double price) {
        setName(name);
        setBrand(brand);
        setSize(size);
        setColor(color);
        setMaterial(material);
        setReleaseYear(releaseYear);
        setPrice(price);
    }

    /**
     * Конструктор копіювання: створює новий об'єкт з тими самими значеннями полів.
     *
     * @param other одяг, який копіюється
     * @throws InvalidClothesException якщо other дорівнює null
     */
    public Clothes(Clothes other) {
        if (other == null) {
            throw new InvalidClothesException("об'єкт для копіювання не задано");
        }
        this.name = other.name;
        this.brand = other.brand;
        this.size = other.size;
        this.color = other.color;
        this.material = other.material;
        this.releaseYear = other.releaseYear;
        this.price = other.price;
    }

    /**
     * Повертає унікальний ідентифікатор речі. Він створюється автоматично
     * і не змінюється (після читання з файлу відновлюється збережений).
     *
     * @return UUID
     */
    @Override
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Відновлює UUID, збережений у файлі. Використовується лише під час читання файлів.
     *
     * @param uuid збережений UUID
     * @throws InvalidClothesException якщо uuid дорівнює null
     */
    void restoreUuid(UUID uuid) {
        if (uuid == null) {
            throw new InvalidClothesException("UUID не задано");
        }
        this.uuid = uuid;
    }

    /**
     * Повертає короткий опис для списків: тип, назва і UUID.
     *
     * @return короткий опис
     */
    public String getShortInfo() {
        return getType() + ": " + name + " | UUID: " + uuid;
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
    public Size getSize() {
        return size;
    }

    /**
     * Задає розмір.
     *
     * @param size розмір, не null
     * @throws InvalidClothesException якщо розмір не задано
     */
    public void setSize(Size size) {
        if (size == null) {
            throw new InvalidClothesException("розмір не задано");
        }
        this.size = size;
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
    public Material getMaterial() {
        return material;
    }

    /**
     * Задає матеріал.
     *
     * @param material матеріал, не null
     * @throws InvalidClothesException якщо матеріал не задано
     */
    public void setMaterial(Material material) {
        if (material == null) {
            throw new InvalidClothesException("матеріал не задано");
        }
        this.material = material;
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
     * Перевіряє, що текст не порожній, і прибирає пробіли по краях.
     *
     * @param value текст
     * @param field назва поля для повідомлення про помилку
     * @return текст без пробілів по краях
     * @throws InvalidClothesException якщо текст порожній, довший за {@link #MAX_TEXT_LENGTH}
     *                                 або містить символ ';' (роздільник у файлі)
     */
    private static String checkText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidClothesException("поле \"" + field + "\" не може бути порожнім");
        }
        if (value.trim().length() > MAX_TEXT_LENGTH) {
            throw new InvalidClothesException("поле \"" + field + "\" не може бути довшим за "
                    + MAX_TEXT_LENGTH + " символів");
        }
        if (value.contains(";")) {
            throw new InvalidClothesException("поле \"" + field + "\" не може містити символ ';'");
        }
        return value.trim();
    }

    /**
     * Повертає тип речі для виводу. Кожен конкретний клас реалізує цей метод.
     *
     * @return назва типу
     */
    public abstract String getType();

    /**
     * Повертає пораду з догляду за річчю залежно від матеріалу.
     * Класи-нащадки можуть перевизначати цей метод.
     *
     * @return порада з догляду
     */
    public String getCareInstructions() {
        switch (material) {
            case WOOL:
                return "ручне прання при 30°C, сушити на горизонтальній поверхні";
            case SILK:
                return "делікатне прання при 30°C, не віджимати";
            case LEATHER:
                return "тільки хімчистка";
            case POLYESTER:
                return "прання при 30°C, прасувати на низькій температурі";
            default:
                return "прання при 40°C";
        }
    }

    /**
     * Повертає UUID у форматі для toString() (додається в кінці опису).
     *
     * @return рядок ", uuid=..."
     */
    protected String uuidInfo() {
        return ", uuid=" + uuid;
    }

    /**
     * Повертає спільні для всього одягу поля одним рядком.
     * Використовується в toString() цього класу і класів-нащадків.
     *
     * @return поля базового класу
     */
    protected String baseInfo() {
        return "назва='" + name + "', бренд='" + brand + "', розмір=" + size
                + ", колір='" + color + "', матеріал=" + material.getTitle() + ", рік=" + releaseYear
                + ", ціна=" + String.format("%.2f", price) + " грн";
    }

    /**
     * Порівнює одяг для сортування. Основний критерій - назва за українським алфавітом
     * без урахування регістру. Якщо назви однакові, порівнюються бренд, назва класу, ціна,
     * а в кінці повний опис toString(), тому порядок однозначний для будь-яких об'єктів,
     * а 0 повертається лише для однакових речей.
     *
     * @param other інша річ
     * @return від'ємне число, 0 або додатне число
     * @throws NullPointerException якщо other дорівнює null
     */
    @Override
    public int compareTo(Clothes other) {
        if (other == null) {
            throw new NullPointerException("не можна порівнювати з null");
        }
        int result = COLLATOR.compare(name, other.name);
        if (result == 0) {
            result = COLLATOR.compare(brand, other.brand);
        }
        if (result == 0) {
            result = getClass().getSimpleName().compareTo(other.getClass().getSimpleName());
        }
        if (result == 0) {
            result = Double.compare(price, other.price);
        }
        if (result == 0 && !equals(other)) {
            result = toString().compareTo(other.toString());
        }
        return result;
    }

    /**
     * Порівнює одяг за всіма полями. UUID не враховується, тому однакові речі,
     * створені окремо, вважаються однаковим товаром.
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
