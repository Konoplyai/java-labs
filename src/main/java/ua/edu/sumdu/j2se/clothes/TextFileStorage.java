package ua.edu.sumdu.j2se.clothes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Збереження одягу в текстовий файл (за замовчуванням input.txt).
 * Кожен об'єкт записується в окремий рядок, значення розділяються символом ';'.
 * Перше значення - назва класу, далі поля в порядку {@link ClothesRecord#keysFor(String)}.
 */
public class TextFileStorage implements ClothesStorage {

    /** Ім'я файлу за замовчуванням. */
    public static final String DEFAULT_FILE = "input.txt";
    /** Роздільник значень у рядку. */
    public static final String SEPARATOR = ";";

    private final Path file;

    /**
     * Створює сховище для заданого файлу.
     *
     * @param fileName ім'я файлу
     * @throws IllegalArgumentException якщо ім'я файлу порожнє
     */
    public TextFileStorage(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("ім'я файлу не може бути порожнім");
        }
        this.file = Paths.get(fileName.trim());
    }

    /**
     * Повертає ім'я файлу.
     *
     * @return ім'я файлу
     */
    @Override
    public String getFileName() {
        return file.toString();
    }

    /**
     * Зчитує одяг з файлу.
     *
     * @param errors список, у який додаються повідомлення про пропущені рядки
     * @return список прочитаних об'єктів
     * @throws IOException якщо файл не вдалося прочитати
     */
    @Override
    public List<Clothes> load(List<String> errors) throws IOException {
        List<Clothes> items = new ArrayList<>();
        if (!Files.exists(file)) {
            errors.add("файл " + file + " не знайдено, список порожній");
            return items;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            Clothes clothes = parseLine(line);
            items.add(clothes);
        }
        return items;
    }

    /**
     * Записує весь список одягу у файл.
     *
     * @param items список одягу
     * @throws IOException якщо файл не вдалося записати
     */
    @Override
    public void save(List<Clothes> items) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Clothes clothes : items) {
            lines.add(formatLine(clothes));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    /**
     * Перетворює об'єкт у рядок файлу.
     *
     * @param clothes об'єкт
     * @return рядок зі значеннями через ';'
     */
    public static String formatLine(Clothes clothes) {
        return String.join(SEPARATOR, ClothesRecord.toValues(clothes));
    }

    /**
     * Створює об'єкт з рядка файлу.
     *
     * @param line рядок зі значеннями через ';'
     * @return створений об'єкт
     * @throws InvalidClothesException якщо рядок некоректний
     */
    public static Clothes parseLine(String line) {
        return ClothesRecord.fromValues(Arrays.asList(line.split(SEPARATOR, -1)));
    }
}
