package ua.edu.sumdu.j2se.clothes;

/**
 * Крій (посадка) джинсів.
 */
public enum Fit {
    /** Дуже вузькі. */
    SKINNY("скіні"),
    /** Звужені. */
    SLIM("слім"),
    /** Прямі. */
    REGULAR("прямі"),
    /** Вільні. */
    LOOSE("вільні");

    private final String title;

    /**
     * Створює значення з українською назвою.
     *
     * @param title назва для виводу
     */
    Fit(String title) {
        this.title = title;
    }

    /**
     * Повертає українську назву.
     *
     * @return назва крою
     */
    public String getTitle() {
        return title;
    }

    /**
     * Шукає крій за українською назвою або назвою константи.
     *
     * @param text текст, наприклад "слім" або "slim"
     * @return знайдений крій
     * @throws InvalidClothesException якщо такого крою немає
     */
    public static Fit fromString(String text) {
        if (text != null) {
            String value = text.trim();
            for (Fit fit : values()) {
                if (fit.title.equalsIgnoreCase(value) || fit.name().equalsIgnoreCase(value)) {
                    return fit;
                }
            }
        }
        throw new InvalidClothesException("крій має бути одним з: " + list());
    }

    /**
     * Повертає назви всіх значень через кому.
     *
     * @return рядок з переліком
     */
    public static String list() {
        StringBuilder sb = new StringBuilder();
        for (Fit fit : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(fit.title);
        }
        return sb.toString();
    }

    /**
     * Повертає українську назву.
     *
     * @return назва крою
     */
    @Override
    public String toString() {
        return title;
    }
}
