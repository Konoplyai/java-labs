package ua.edu.sumdu.j2se.clothes;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Параметри підключення до бази даних, які зчитуються з файлу .properties
 * (ключі db.url, db.user, db.password).
 */
public class DatabaseConfig {

    /** Ключ адреси бази даних. */
    public static final String URL_KEY = "db.url";
    /** Ключ імені користувача. */
    public static final String USER_KEY = "db.user";
    /** Ключ пароля. */
    public static final String PASSWORD_KEY = "db.password";

    private final String url;
    private final String user;
    private final String password;

    /**
     * Створює параметри підключення.
     *
     * @param url адреса бази даних (jdbc:...)
     * @param user ім'я користувача
     * @param password пароль (може бути порожнім)
     * @throws IllegalArgumentException якщо адреса або користувач порожні
     */
    public DatabaseConfig(String url, String user, String password) {
        if (url == null || !url.trim().startsWith("jdbc:")) {
            throw new IllegalArgumentException("параметр " + URL_KEY + " має починатися з jdbc:");
        }
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("параметр " + USER_KEY + " не задано");
        }
        this.url = url.trim();
        this.user = user.trim();
        this.password = password == null ? "" : password;
    }

    /**
     * Зчитує параметри підключення з файлу.
     *
     * @param fileName шлях до файлу .properties
     * @return параметри підключення
     * @throws IOException якщо файл не знайдено або не вдалося прочитати
     * @throws IllegalArgumentException якщо у файлі немає потрібних параметрів
     */
    public static DatabaseConfig load(String fileName) throws IOException {
        Path file = Paths.get(fileName);
        if (!Files.exists(file)) {
            throw new IOException("файл " + fileName + " не знайдено");
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return new DatabaseConfig(properties.getProperty(URL_KEY), properties.getProperty(USER_KEY),
                properties.getProperty(PASSWORD_KEY));
    }

    /**
     * Повертає адресу бази даних.
     *
     * @return адреса
     */
    public String getUrl() {
        return url;
    }

    /**
     * Повертає ім'я користувача.
     *
     * @return ім'я користувача
     */
    public String getUser() {
        return user;
    }

    /**
     * Повертає пароль.
     *
     * @return пароль
     */
    public String getPassword() {
        return password;
    }
}
