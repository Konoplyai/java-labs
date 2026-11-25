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
 * Збереження одягу у файл формату JSON (за замовчуванням input.json) за допомогою бібліотеки Gson.
 * Файл містить масив об'єктів; поле "type" визначає клас, який треба створити.
 * Об'єкти створюються через конструктори, тому всі значення проходять перевірку.
 */
public class JsonFileStorage implements ClothesStorage {

    /** Ім'я файлу за замовчуванням. */
    public static final String DEFAULT_FILE = "input.json";

    /** Поля, які записуються як цілі числа. */
    private static final List<String> INT_KEYS = Arrays.asList("year", "waist", "length", "density", "collar");
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
     * Зчитує одяг з JSON-файлу. Некоректні елементи масиву та повтори пропускаються.
     *
     * @param errors список, у який додаються повідомлення про пропущені елементи
     * @return список прочитаних об'єктів
     * @throws IOException якщо файл не вдалося прочитати або він не є JSON-масивом
     */
    @Override
    public List<Clothes> load(List<String> errors) throws IOException {
        List<Clothes> items = new ArrayList<>();
        if (!Files.exists(file)) {
            errors.add("файл " + file + " не знайдено, список порожній");
            return items;
        }
        String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        if (text.startsWith("﻿")) {
            text = text.substring(1);
        }
        if (text.trim().isEmpty()) {
            return items;
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
                if (items.contains(clothes)) {
                    errors.add("елемент " + (i + 1) + ": така річ уже є, пропущено");
                } else {
                    items.add(clothes);
                }
            } catch (InvalidClothesException e) {
                errors.add("елемент " + (i + 1) + ": " + e.getMessage() + ", пропущено");
            }
        }
        return items;
    }

    /**
     * Записує весь список одягу у JSON-файл. Попередній вміст зберігається в копію .bak.
     *
     * @param items список одягу
     * @throws IOException якщо файл не вдалося записати
     */
    @Override
    public void save(List<Clothes> items) throws IOException {
        if (Files.exists(file)) {
            Files.copy(file, Paths.get(file + TextFileStorage.BACKUP_SUFFIX), StandardCopyOption.REPLACE_EXISTING);
        }
        JsonArray array = new JsonArray();
        for (Clothes clothes : items) {
            array.add(toJson(clothes));
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
