package ua.edu.sumdu.j2se.clothes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести роботи з базою даних. Замість PostgreSQL використовується вбудована база H2
 * в режимі сумісності з PostgreSQL, тому тести не потребують запущеного сервера.
 */
class DatabaseManagerTest {

    private static int counter;

    @TempDir
    Path dir;

    private DatabaseManager database;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        counter++;
        DatabaseConfig config = new DatabaseConfig(
                "jdbc:h2:mem:clothes" + counter + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        database = new DatabaseManager(config);
        database.createTable(Paths.get(ClothesDriver.SCHEMA_FILE));
    }

    /**
     * Повертає значення одного поля запису з заданим id.
     *
     * @param id id запису
     * @param column назва поля
     * @return значення поля або null
     * @throws SQLException якщо запит не виконався
     */
    private Object valueOf(int id, String column) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM clothes WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                return result.getObject(column);
            }
        }
    }

    @Test
    void insertShouldSaveBaseClothes() throws SQLException {
        Clothes sweater = new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299.5);
        int id = database.insert(sweater, 5);
        assertEquals(1, database.count());
        assertEquals("Clothes", valueOf(id, "type"));
        assertEquals("Светр", valueOf(id, "name"));
        assertEquals("WOOL", valueOf(id, "material"));
        assertEquals(1299.5, ((Number) valueOf(id, "price")).doubleValue(), 0.001);
        assertEquals(5, ((Number) valueOf(id, "quantity")).intValue());
        assertNull(valueOf(id, "waist"));
        assertNull(valueOf(id, "sleeve"));
    }

    @Test
    void typeShouldMatchSubclass() throws SQLException {
        Pants pants = new Pants("Брюки", "Mango", Size.M, "чорний", Material.WOOL, 2022, 1899, 80, 104);
        Jeans jeans = new Jeans(pants, Fit.SLIM, 13);
        Shirts shirts = new Shirts("Сорочка", "Zara", Size.L, "білий", Material.LINEN, 2024, 999, Sleeve.LONG, 41);
        FormalShirt formal = new FormalShirt(shirts, true, false);
        int pantsId = database.insert(pants, 1);
        int jeansId = database.insert(jeans, 2);
        int shirtsId = database.insert(shirts, 3);
        int formalId = database.insert(formal, 4);
        assertEquals(4, database.count());
        assertEquals("Pants", valueOf(pantsId, "type"));
        assertEquals("Jeans", valueOf(jeansId, "type"));
        assertEquals("Shirts", valueOf(shirtsId, "type"));
        assertEquals("FormalShirt", valueOf(formalId, "type"));
        assertEquals(104, ((Number) valueOf(pantsId, "leg_length")).intValue());
        assertNull(valueOf(pantsId, "fit"));
        assertEquals("SLIM", valueOf(jeansId, "fit"));
        assertEquals(13, ((Number) valueOf(jeansId, "density")).intValue());
        assertEquals("LONG", valueOf(shirtsId, "sleeve"));
        assertNull(valueOf(shirtsId, "cufflinks"));
        assertEquals(Boolean.TRUE, valueOf(formalId, "cufflinks"));
        assertEquals(Boolean.FALSE, valueOf(formalId, "non_iron"));
    }

    @Test
    void textWithQuotesShouldBeSavedAsIs() throws SQLException {
        Clothes jeans = new Clothes("Джинси' OR '1'='1", "Levi's", Size.M, "синій",
                Material.DENIM, 2023, 2599);
        int id = database.insert(jeans, 1);
        assertEquals("Levi's", valueOf(id, "brand"));
        assertEquals("Джинси' OR '1'='1", valueOf(id, "name"));
        assertEquals(1, database.count());
    }

    @Test
    void createTableTwiceShouldKeepData() throws IOException, SQLException {
        database.insert(new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299), 1);
        database.createTable(Paths.get(ClothesDriver.SCHEMA_FILE));
        assertEquals(1, database.count());
    }

    @Test
    void wrongUrlShouldThrowSQLException() {
        DatabaseManager wrong = new DatabaseManager(new DatabaseConfig("jdbc:unknown:db", "user", "pass"));
        assertThrows(SQLException.class, wrong::checkConnection);
        assertThrows(SQLException.class,
                () -> wrong.insert(new Clothes("Светр", "H&M", Size.L, "сірий", Material.WOOL, 2021, 1299), 1));
    }

    @Test
    void configShouldBeReadFromFile() throws IOException {
        Path file = dir.resolve("app.properties");
        Files.write(file, Arrays.asList("# параметри", "db.url=jdbc:postgresql://localhost:5432/clothes_db",
                "db.user=clothes_user", "db.password=secret"), StandardCharsets.UTF_8);
        DatabaseConfig config = DatabaseConfig.load(file.toString());
        assertEquals("jdbc:postgresql://localhost:5432/clothes_db", config.getUrl());
        assertEquals("clothes_user", config.getUser());
        assertEquals("secret", config.getPassword());
    }

    @Test
    void badConfigShouldThrow() throws IOException {
        assertThrows(IOException.class, () -> DatabaseConfig.load(dir.resolve("none.properties").toString()));
        Path file = dir.resolve("bad.properties");
        Files.write(file, Arrays.asList("db.url=localhost:5432", "db.user=clothes_user"), StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, () -> DatabaseConfig.load(file.toString()));
        assertThrows(IllegalArgumentException.class, () -> new DatabaseConfig("jdbc:h2:mem:x", " ", ""));
    }

    @Test
    void configPathShouldBeTakenFromArgs() {
        assertEquals("db.properties", ClothesDriver.findConfigPath(new String[] {"db.properties"}));
        assertEquals("conf/app.properties", ClothesDriver.findConfigPath(new String[] {"json", "conf/app.properties"}));
        assertNull(ClothesDriver.findConfigPath(new String[] {"json"}));
        assertNull(ClothesDriver.findConfigPath(new String[0]));
        assertFalse(ClothesDriver.createStorage(new String[] {"db.properties"}) instanceof JsonFileStorage);
        assertTrue(ClothesDriver.createStorage(new String[] {"db.properties", "json"}) instanceof JsonFileStorage);
    }
}
