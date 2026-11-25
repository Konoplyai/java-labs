package ua.edu.sumdu.j2se.clothes;

import java.util.ArrayList;
import java.util.List;

/**
 * Перетворення об'єктів ієрархії одягу в набір текстових значень і назад.
 * Перше значення - назва класу, за якою під час читання визначається,
 * об'єкт якого класу треба створити. Використовується для збереження у файли.
 */
public final class ClothesRecord {

    /** Назви полів, спільних для всіх класів. */
    private static final String[] BASE_KEYS =
        {"type", "name", "brand", "size", "color", "material", "year", "price"};
    /** Додаткові поля штанів. */
    private static final String[] PANTS_KEYS = {"waist", "length"};
    /** Додаткові поля джинсів. */
    private static final String[] JEANS_KEYS = {"waist", "length", "fit", "density"};
    /** Додаткові поля сорочок. */
    private static final String[] SHIRTS_KEYS = {"sleeve", "collar"};
    /** Додаткові поля класичних сорочок. */
    private static final String[] FORMAL_KEYS = {"sleeve", "collar", "cufflinks", "nonIron"};

    /**
     * Закритий конструктор, бо клас містить лише статичні методи.
     */
    private ClothesRecord() {
    }

    /**
     * Повертає назви всіх полів для заданого типу в тому порядку,
     * у якому вони записуються у файл.
     *
     * @param type назва класу (Sweater, Pants, Jeans, Shirts, FormalShirt)
     * @return список назв полів
     * @throws InvalidClothesException якщо тип невідомий
     */
    public static List<String> keysFor(String type) {
        List<String> keys = new ArrayList<>();
        for (String key : BASE_KEYS) {
            keys.add(key);
        }
        String[] extra;
        switch (type == null ? "" : type) {
            case "Sweater":
                extra = new String[0];
                break;
            case "Pants":
                extra = PANTS_KEYS;
                break;
            case "Jeans":
                extra = JEANS_KEYS;
                break;
            case "Shirts":
                extra = SHIRTS_KEYS;
                break;
            case "FormalShirt":
                extra = FORMAL_KEYS;
                break;
            default:
                throw new InvalidClothesException("невідомий тип \"" + type + "\"");
        }
        for (String key : extra) {
            keys.add(key);
        }
        keys.add("uuid");
        return keys;
    }

    /**
     * Перетворює об'єкт у список значень (у тому ж порядку, що й {@link #keysFor(String)}).
     *
     * @param clothes об'єкт будь-якого класу ієрархії
     * @return список значень, перше - назва класу
     */
    public static List<String> toValues(Clothes clothes) {
        List<String> values = new ArrayList<>();
        values.add(clothes.getClass().getSimpleName());
        values.add(clothes.getName());
        values.add(clothes.getBrand());
        values.add(clothes.getSize().name());
        values.add(clothes.getColor());
        values.add(clothes.getMaterial().name());
        values.add(String.valueOf(clothes.getReleaseYear()));
        values.add(String.valueOf(clothes.getPrice()));
        if (clothes instanceof Pants) {
            Pants pants = (Pants) clothes;
            values.add(String.valueOf(pants.getWaist()));
            values.add(String.valueOf(pants.getLength()));
        }
        if (clothes instanceof Jeans) {
            Jeans jeans = (Jeans) clothes;
            values.add(jeans.getFit().name());
            values.add(String.valueOf(jeans.getDensity()));
        }
        if (clothes instanceof Shirts) {
            Shirts shirts = (Shirts) clothes;
            values.add(shirts.getSleeve().name());
            values.add(String.valueOf(shirts.getCollar()));
        }
        if (clothes instanceof FormalShirt) {
            FormalShirt formal = (FormalShirt) clothes;
            values.add(String.valueOf(formal.hasCufflinks()));
            values.add(String.valueOf(formal.isNonIron()));
        }
        values.add(clothes.getUuid().toString());
        return values;
    }

    /**
     * Створює об'єкт потрібного класу зі списку значень.
     * Усі значення проходять перевірку в конструкторах класів.
     *
     * @param values список значень, перше - назва класу
     * @return створений об'єкт
     * @throws InvalidClothesException якщо тип невідомий, кількість значень
     *                                 не та або якесь значення некоректне
     */
    public static Clothes fromValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new InvalidClothesException("порожній запис");
        }
        String type = values.get(0).trim();
        List<String> keys = keysFor(type);
        if (values.size() != keys.size()) {
            throw new InvalidClothesException("для типу " + type + " потрібно " + keys.size()
                    + " значень, а задано " + values.size());
        }
        Clothes base = new Sweater(values.get(1), values.get(2), Size.fromString(values.get(3)),
                values.get(4), Material.fromString(values.get(5)),
                parseInt(values.get(6), "year"), parseDouble(values.get(7), "price"));
        Clothes result;
        switch (type) {
            case "Pants":
                result = new Pants(base, parseInt(values.get(8), "waist"), parseInt(values.get(9), "length"));
                break;
            case "Jeans":
                Pants pants = new Pants(base, parseInt(values.get(8), "waist"), parseInt(values.get(9), "length"));
                result = new Jeans(pants, Fit.fromString(values.get(10)), parseInt(values.get(11), "density"));
                break;
            case "Shirts":
                result = new Shirts(base, Sleeve.fromString(values.get(8)), parseInt(values.get(9), "collar"));
                break;
            case "FormalShirt":
                Shirts shirts = new Shirts(base, Sleeve.fromString(values.get(8)), parseInt(values.get(9), "collar"));
                result = new FormalShirt(shirts, parseBoolean(values.get(10), "cufflinks"),
                        parseBoolean(values.get(11), "nonIron"));
                break;
            default:
                result = base;
                break;
        }
        String uuidText = values.get(values.size() - 1).trim();
        if (!uuidText.isEmpty()) {
            try {
                result.restoreUuid(Identifiable.parseUuid(uuidText));
            } catch (IllegalArgumentException e) {
                throw new InvalidClothesException(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Перетворює текст у ціле число.
     *
     * @param text текст
     * @param field назва поля для повідомлення про помилку
     * @return число
     * @throws InvalidClothesException якщо текст не є цілим числом
     */
    private static int parseInt(String text, String field) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidClothesException("поле " + field + " має бути цілим числом, а не \"" + text + "\"");
        }
    }

    /**
     * Перетворює текст у дійсне число (можна через кому або крапку).
     *
     * @param text текст
     * @param field назва поля для повідомлення про помилку
     * @return число
     * @throws InvalidClothesException якщо текст не є числом
     */
    private static double parseDouble(String text, String field) {
        try {
            return Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new InvalidClothesException("поле " + field + " має бути числом, а не \"" + text + "\"");
        }
    }

    /**
     * Перетворює текст true/false у логічне значення.
     *
     * @param text текст
     * @param field назва поля для повідомлення про помилку
     * @return логічне значення
     * @throws InvalidClothesException якщо текст не true і не false
     */
    private static boolean parseBoolean(String text, String field) {
        String value = text.trim();
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new InvalidClothesException("поле " + field + " має бути true або false, а не \"" + text + "\"");
    }
}
