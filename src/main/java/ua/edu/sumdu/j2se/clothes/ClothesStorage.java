package ua.edu.sumdu.j2se.clothes;

import java.io.IOException;
import java.util.List;

/**
 * Сховище товарів магазину (файл певного формату).
 */
public interface ClothesStorage {

    /**
     * Повертає ім'я файлу сховища.
     *
     * @return ім'я файлу
     */
    String getFileName();

    /**
     * Зчитує товари з файлу і додає їх у магазин методом {@link Store#addNewClothes(Clothes, int)}.
     * Некоректні записи пропускаються, а опис кожної проблеми додається до списку errors.
     *
     * @param store магазин, у який додаються товари
     * @param errors список, у який додаються повідомлення про пропущені записи
     * @return кількість прочитаних записів
     * @throws IOException якщо файл не вдалося прочитати
     */
    int load(Store store, List<String> errors) throws IOException;

    /**
     * Записує всі товари магазину разом з кількістю у файл (старий вміст замінюється).
     *
     * @param store магазин
     * @throws IOException якщо файл не вдалося записати
     */
    void save(Store store) throws IOException;
}
