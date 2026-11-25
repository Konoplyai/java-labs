package ua.edu.sumdu.j2se.clothes;

/**
 * Розміри одягу.
 */
public enum Size {
    /** Дуже маленький. */
    XS,
    /** Маленький. */
    S,
    /** Середній. */
    M,
    /** Великий. */
    L,
    /** Дуже великий. */
    XL,
    /** Дуже-дуже великий. */
    XXL;

    /**
     * Шукає розмір за текстом (регістр і пробіли по краях не важливі).
     *
     * @param text текст розміру, наприклад "xl"
     * @return знайдений розмір
     * @throws InvalidClothesException якщо такого розміру немає
     */
    public static Size fromString(String text) {
        if (text != null) {
            for (Size size : values()) {
                if (size.name().equalsIgnoreCase(text.trim())) {
                    return size;
                }
            }
        }
        throw new InvalidClothesException("розмір має бути одним з: " + list());
    }

    /**
     * Повертає всі розміри через кому.
     *
     * @return рядок з переліком розмірів
     */
    public static String list() {
        StringBuilder sb = new StringBuilder();
        for (Size size : values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(size.name());
        }
        return sb.toString();
    }
}
