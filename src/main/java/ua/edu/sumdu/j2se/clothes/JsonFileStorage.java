package ua.edu.sumdu.j2se.clothes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

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
 * Збереження товарів магазину у файл формату JSON (за замовчуванням input.json) за допомогою бібліотеки Gson.
 * Файл містить масив об'єктів; поле "type" визначає клас, який треба створити,
 * поле "quantity" - кількість товару.
 * Об'єкти створюються через конструктори, тому всі значення проходять перевірку.
 */
public class JsonFileStorage implements ClothesStorage {

    /** Ім'я файлу за замовчуванням. */
    public static final String DEFAULT_FILE = "input.json";

    /** Поля, які записуються як цілі числа. */
    private static final List<String> INT_KEYS = Arrays.asList("year", "waist", "length", "density", "collar");
    /** Поле з кількістю товару. */
    public static final String QUANTITY = "quantity";
    /** Поля, які записуються як логічні значення. */
    private static final List<String> BOOLEAN_KEYS = Arrays.asList("cufflinks", "nonIron");

    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Створює сховище для заданого файлу.
     *
     * @param fileName ім'я файлу
     * @throws IllegalArgumentException якщо ім'я файлу порожнє
     */
    public JsonFileStorage(String fileName) {
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
     * Зчитує товари з JSON-файлу і додає їх у магазин. Некоректні елементи масиву пропускаються.
     *
     * @param store магазин
     * @param errors список, у який додаються повідомлення про пропущені елементи
     * @return кількість прочитаних елементів
     * @throws IOException якщо файл не вдалося прочитати або він не є JSON-масивом
     */
    @Override
    public int load(Store store, List<String> errors) throws IOException {
        int count = 0;
        if (!Files.exists(file)) {
            errors.add("файл " + file + " не знайдено, магазин порожній");
            return count;
        }
        String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        if (text.startsWith("﻿")) {
            text = text.substring(1);
        }
        if (text.trim().isEmpty()) {
            return count;
        }
        JsonElement root;
        try {
            root = JsonParser.parseString(text);
        } catch (JsonParseException e) {
            throw new IOException("некоректний формат JSON: " + e.getMessage(), e);
        }
        if (!root.isJsonArray()) {
            throw new IOException("у файлі має бути масив об'єктів [...]");
        }
        JsonArray array = root.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            try {
                Clothes clothes = fromJson(array.get(i));
                int quantity = TextFileStorage.parseQuantity(getValue(array.get(i).getAsJsonObject(), QUANTITY));
                store.addNewClothes(clothes, quantity);
                count++;
            } catch (InvalidClothesException | StoreException e) {
                errors.add("елемент " + (i + 1) + ": " + e.getMessage() + ", пропущено");
            }
        }
        return count;
    }

    /**
     * Записує всі товари магазину у JSON-файл. Попередній вміст зберігається в копію .bak.
     *
     * @param store магазин
     * @throws IOException якщо файл не вдалося записати
     */
    @Override
    public void save(Store store) throws IOException {
        if (Files.exists(file)) {
            Files.copy(file, Paths.get(file + TextFileStorage.BACKUP_SUFFIX), StandardCopyOption.REPLACE_EXISTING);
        }
        JsonArray array = new JsonArray();
        for (Clothes clothes : store.getClothes()) {
            JsonObject object = toJson(clothes);
            object.addProperty(QUANTITY, store.getQuantity(clothes));
            array.add(object);
        }
        Files.write(file, gson.toJson(array).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Перетворює об'єкт у JSON-об'єкт з полем type і всіма полями класу.
     *
     * @param clothes об'єкт
     * @return JSON-об'єкт
     */
    public static JsonObject toJson(Clothes clothes) {
        List<String> values = ClothesRecord.toValues(clothes);
        List<String> keys = ClothesRecord.keysFor(values.get(0));
        JsonObject object = new JsonObject();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = values.get(i);
            if (INT_KEYS.contains(key)) {
                object.addProperty(key, Integer.parseInt(value));
            } else if (key.equals("price")) {
                object.addProperty(key, Double.parseDouble(value));
            } else if (BOOLEAN_KEYS.contains(key)) {
                object.addProperty(key, Boolean.parseBoolean(value));
            } else {
                object.addProperty(key, value);
            }
        }
        return object;
    }

    /**
     * Створює об'єкт потрібного класу з JSON-елемента.
     *
     * @param element елемент масиву
     * @return створений об'єкт
     * @throws InvalidClothesException якщо елемент не є об'єктом, не має потрібних полів
     *                                 або значення некоректні
     */
    public static Clothes fromJson(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            throw new InvalidClothesException("елемент має бути об'єктом {...}");
        }
        JsonObject object = element.getAsJsonObject();
        String type = getValue(object, "type");
        List<String> values = new ArrayList<>();
        for (String key : ClothesRecord.keysFor(type)) {
            values.add(getValue(object, key));
        }
        return ClothesRecord.fromValues(values);
    }

    /**
     * Повертає значення поля як текст.
     *
     * @param object JSON-об'єкт
     * @param key назва поля
     * @return значення поля
     * @throws InvalidClothesException якщо поля немає або воно не є простим значенням
     */
    private static String getValue(JsonObject object, String key) {
        JsonElement value = object.get(key);
        if (value == null || value.isJsonNull()) {
            throw new InvalidClothesException("немає поля \"" + key + "\"");
        }
        if (!value.isJsonPrimitive()) {
            throw new InvalidClothesException("поле \"" + key + "\" має бути рядком, числом або true/false");
        }
        return value.getAsString();
    }
}
