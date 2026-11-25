package ua.edu.sumdu.j2se.clothes;

import java.io.IOException;
import java.util.List;

/**
 * Сховище для списку одягу (файл певного формату).
 */
public interface ClothesStorage {

    /**
     * Повертає ім'я файлу сховища.
     *
     * @return ім'я файлу
     */
    String getFileName();

    /**
     * Зчитує одяг з файлу. Некоректні записи пропускаються,
     * а опис кожної проблеми додається до списку errors.
     *
     * @param errors список, у який додаються повідомлення про пропущені записи
     * @return список прочитаних об'єктів (порожній, якщо файлу немає)
     * @throws IOException якщо файл не вдалося прочитати
     */
    List<Clothes> load(List<String> errors) throws IOException;

    /**
     * Записує весь список одягу у файл (старий вміст замінюється).
     *
     * @param items список одягу
     * @throws IOException якщо файл не вдалося записати
     */
    void save(List<Clothes> items) throws IOException;
}
