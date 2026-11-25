package ua.edu.sumdu.j2se.clothes;

/**
 * Матеріали, з яких може бути пошитий одяг.
 */
public enum Material {
    /** Бавовна. */
    COTTON("бавовна"),
    /** Вовна. */
    WOOL("вовна"),
    /** Льон. */
    LINEN("льон"),
    /** Шовк. */
    SILK("шовк"),
    /** Денім (джинсова тканина). */
    DENIM("денім"),
    /** Шкіра. */
    LEATHER("шкіра"),
    /** Поліестер. */
    POLYESTER("поліестер");

    private final String title;

    /**
     * Створює матеріал з українською назвою.
     *
     * @param title назва для виводу
     */
    Material(String title) {
        this.title = title;
    }

    /**
     * Повертає українську назву матеріалу.
     *
     * @return назва матеріалу
     */
    public String getTitle() {
        return title;
    }

    /**
     * Шукає матеріал за українською назвою або назвою константи.
     *
     * @param text назва матеріалу, наприклад "бавовна" або "cotton"
     * @return знайдений матеріал
     * @throws InvalidClothesException якщо такого матеріалу немає
     */
    public static Material fromString(String text) {
        if (text != null) {
            String value = text.trim();
            for (Material material : values()) {
                if (material.title.equalsIgnoreCase(value) || material.name().equalsIgnoreCase(value)) {
                    return material;
                }
            }
        }
        throw new InvalidClothesException("матеріал має бути одним з: " + list());
    }

    /**
     * Повертає назви всіх матеріалів через кому.
     *
     * @return рядок з переліком матеріалів
     */
    public static String list() {
        StringBuilder sb = new StringBuilder();
        for (Material material : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(material.title);
        }
        return sb.toString();
    }

    /**
     * Повертає українську назву матеріалу.
     *
     * @return назва матеріалу
     */
    @Override
    public String toString() {
        return title;
    }
}
