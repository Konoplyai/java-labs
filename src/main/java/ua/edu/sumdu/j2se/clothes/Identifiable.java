package ua.edu.sumdu.j2se.clothes;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Об'єкт, який має унікальний ідентифікатор UUID.
 */
public interface Identifiable {

    /** Формат UUID: 8-4-4-4-12 шістнадцяткових цифр. */
    Pattern UUID_FORMAT = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    /**
     * Повертає унікальний ідентифікатор об'єкта.
     *
     * @return UUID
     */
    UUID getUuid();

    /**
     * Перетворює рядок у UUID. Приймається лише повний запис з 36 символів
     * (UUID.fromString сам по собі пропускає і скорочені записи на кшталт "1-2-3-4-5").
     *
     * @param text рядок з UUID
     * @return UUID
     * @throws IllegalArgumentException якщо формат некоректний
     */
    static UUID parseUuid(String text) {
        if (text == null || !UUID_FORMAT.matcher(text.trim()).matches()) {
            throw new IllegalArgumentException("некоректний формат UUID \"" + text
                    + "\", приклад: 123e4567-e89b-12d3-a456-426614174000");
        }
        return UUID.fromString(text.trim());
    }
}
