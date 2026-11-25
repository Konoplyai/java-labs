package ua.edu.sumdu.j2se.clothes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Робота з базою даних через JDBC: створення таблиці clothes і збереження в неї об'єктів.
 * Усі об'єкти ієрархії зберігаються в одній таблиці, клас об'єкта записується в поле type,
 * поля, яких немає у класу, залишаються NULL.
 */
public class DatabaseManager {

    /** Назва таблиці. */
    public static final String TABLE = "clothes";

    private static final String INSERT_SQL = "INSERT INTO clothes (type, name, brand, size, color, material, "
            + "release_year, price, quantity, waist, leg_length, fit, density, sleeve, collar, cufflinks, non_iron) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM clothes";

    private final DatabaseConfig config;

    /**
     * Створює менеджер для заданих параметрів підключення.
     *
     * @param config параметри підключення
     */
    public DatabaseManager(DatabaseConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("параметри підключення не задано");
        }
        this.config = config;
    }

    /**
     * Відкриває нове підключення до бази даних.
     *
     * @return підключення
     * @throws SQLException якщо підключитися не вдалося
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    /**
     * Перевіряє, що до бази даних можна підключитися.
     *
     * @throws SQLException якщо підключитися не вдалося
     */
    public void checkConnection() throws SQLException {
        try (Connection connection = getConnection()) {
            if (!connection.isValid(5)) {
                throw new SQLException("підключення до бази даних не відповідає");
            }
        }
    }

    /**
     * Виконує команди з файлу schema.sql (створення таблиці, якщо її ще немає).
     * Команди у файлі розділяються символом ';', рядки, що починаються з "--", пропускаються.
     *
     * @param schemaFile файл з SQL-командами
     * @throws IOException якщо файл не вдалося прочитати
     * @throws SQLException якщо команда не виконалась
     */
    public void createTable(Path schemaFile) throws IOException, SQLException {
        StringBuilder sql = new StringBuilder();
        for (String line : Files.readAllLines(schemaFile, StandardCharsets.UTF_8)) {
            if (!line.trim().startsWith("--")) {
                sql.append(line).append('\n');
            }
        }
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            for (String command : sql.toString().split(";")) {
                if (!command.trim().isEmpty()) {
                    statement.execute(command.trim());
                }
            }
        }
    }

    /**
     * Додає об'єкт у таблицю clothes. Значення передаються через PreparedStatement,
     * у поле type записується назва класу об'єкта.
     *
     * @param clothes об'єкт будь-якого класу ієрархії
     * @param quantity кількість товару
     * @return id нового запису
     * @throws SQLException якщо запит не виконався
     */
    public int insert(Clothes clothes, int quantity) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, new String[] {"id"})) {
            fillStatement(statement, clothes, quantity);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("база даних не повернула id нового запису");
        }
    }

    /**
     * Рахує кількість записів у таблиці.
     *
     * @return кількість записів
     * @throws SQLException якщо запит не виконався
     */
    public int count() throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_SQL);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    /**
     * Заповнює параметри запиту INSERT значеннями полів об'єкта.
     *
     * @param statement запит
     * @param clothes об'єкт
     * @param quantity кількість
     * @throws SQLException якщо параметр не вдалося встановити
     */
    private static void fillStatement(PreparedStatement statement, Clothes clothes, int quantity)
            throws SQLException {
        statement.setString(1, clothes.getClass().getSimpleName());
        statement.setString(2, clothes.getName());
        statement.setString(3, clothes.getBrand());
        statement.setString(4, clothes.getSize().name());
        statement.setString(5, clothes.getColor());
        statement.setString(6, clothes.getMaterial().name());
        statement.setInt(7, clothes.getReleaseYear());
        statement.setDouble(8, clothes.getPrice());
        statement.setInt(9, quantity);
        statement.setNull(10, Types.INTEGER);
        statement.setNull(11, Types.INTEGER);
        statement.setNull(12, Types.VARCHAR);
        statement.setNull(13, Types.INTEGER);
        statement.setNull(14, Types.VARCHAR);
        statement.setNull(15, Types.INTEGER);
        statement.setNull(16, Types.BOOLEAN);
        statement.setNull(17, Types.BOOLEAN);
        if (clothes instanceof Pants) {
            Pants pants = (Pants) clothes;
            statement.setInt(10, pants.getWaist());
            statement.setInt(11, pants.getLength());
        }
        if (clothes instanceof Jeans) {
            Jeans jeans = (Jeans) clothes;
            statement.setString(12, jeans.getFit().name());
            statement.setInt(13, jeans.getDensity());
        }
        if (clothes instanceof Shirts) {
            Shirts shirts = (Shirts) clothes;
            statement.setString(14, shirts.getSleeve().name());
            statement.setInt(15, shirts.getCollar());
        }
        if (clothes instanceof FormalShirt) {
            FormalShirt formal = (FormalShirt) clothes;
            statement.setBoolean(16, formal.hasCufflinks());
            statement.setBoolean(17, formal.isNonIron());
        }
    }
}
