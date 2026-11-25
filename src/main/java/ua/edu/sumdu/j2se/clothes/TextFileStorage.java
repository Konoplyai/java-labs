package ua.edu.sumdu.j2se.clothes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Збереження одягу в текстовий файл (за замовчуванням input.txt).
 * Кожен об'єкт записується в окремий рядок, значення розділяються символом ';'.
 * Перше значення - назва класу, далі поля в порядку {@link ClothesRecord#keysFor(String)}.
 * Порожні рядки і рядки, що починаються з '#', пропускаються. Некоректні рядки
 * та повтори не зупиняють читання, а потрапляють у список помилок.
 */
public class TextFileStorage implements ClothesStorage {

    /** Ім'я файлу за замовчуванням. */
    public static final String DEFAULT_FILE = "input.txt";
    /** Роздільник значень у рядку. */
    public static final String SEPARATOR = ";";
    /** Початок рядка-коментаря, такі рядки пропускаються під час читання. */
    public static final String COMMENT = "#";
    /** Розширення резервної копії попереднього вмісту файлу. */
    public static final String BACKUP_SUFFIX = ".bak";

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
            if (i == 0 && line.startsWith("\uFEFF")) {
                line = line.substring(1).trim();
            }
            if (line.isEmpty() || line.startsWith(COMMENT)) {
                continue;
            }
            try {
                Clothes clothes = parseLine(line);
                if (items.contains(clothes)) {
                    errors.add("рядок " + (i + 1) + ": така річ уже є, рядок пропущено");
                } else {
                    items.add(clothes);
                }
            } catch (InvalidClothesException e) {
                errors.add("рядок " + (i + 1) + ": " + e.getMessage() + ", рядок пропущено");
            }
        }
        return items;
    }

    /**
     * Записує весь список одягу у файл. Попередній вміст файлу перед цим
     * зберігається в копію з розширенням .bak, щоб не втратити пропущені рядки.
     *
     * @param items список одягу
     * @throws IOException якщо файл не вдалося записати
     */
    @Override
    public void save(List<Clothes> items) throws IOException {
        if (Files.exists(file)) {
            Files.copy(file, Paths.get(file + BACKUP_SUFFIX), StandardCopyOption.REPLACE_EXISTING);
        }
        List<String> lines = new ArrayList<>();
        lines.add(COMMENT + " тип;назва;бренд;розмір;колір;матеріал;рік;ціна;додаткові поля типу");
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
