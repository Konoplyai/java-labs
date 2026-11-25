package ua.edu.sumdu.j2se.clothes;

/**
 * Довжина рукава сорочки.
 */
public enum Sleeve {
    /** Короткий рукав. */
    SHORT("короткий"),
    /** Довгий рукав. */
    LONG("довгий");

    private final String title;

    /**
     * Створює значення з українською назвою.
     *
     * @param title назва для виводу
     */
    Sleeve(String title) {
        this.title = title;
    }

    /**
     * Повертає українську назву.
     *
     * @return назва рукава
     */
    public String getTitle() {
        return title;
    }

    /**
     * Шукає значення за українською назвою або назвою константи.
     *
     * @param text текст, наприклад "довгий" або "long"
     * @return знайдене значення
     * @throws InvalidClothesException якщо такого значення немає
     */
    public static Sleeve fromString(String text) {
        if (text != null) {
            String value = text.trim();
            for (Sleeve sleeve : values()) {
                if (sleeve.title.equalsIgnoreCase(value) || sleeve.name().equalsIgnoreCase(value)) {
                    return sleeve;
                }
            }
        }
        throw new InvalidClothesException("рукав має бути: " + list());
    }

    /**
     * Повертає назви всіх значень через кому.
     *
     * @return рядок з переліком
     */
    public static String list() {
        StringBuilder sb = new StringBuilder();
        for (Sleeve sleeve : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(sleeve.title);
        }
        return sb.toString();
    }

    /**
     * Повертає українську назву.
     *
     * @return назва рукава
     */
    @Override
    public String toString() {
        return title;
    }
}
