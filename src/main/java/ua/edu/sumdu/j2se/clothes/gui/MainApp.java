package ua.edu.sumdu.j2se.clothes.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ua.edu.sumdu.j2se.clothes.Clothes;
import ua.edu.sumdu.j2se.clothes.ClothesDriver;
import ua.edu.sumdu.j2se.clothes.ClothesRecord;
import ua.edu.sumdu.j2se.clothes.ClothesStorage;
import ua.edu.sumdu.j2se.clothes.Fit;
import ua.edu.sumdu.j2se.clothes.Identifiable;
import ua.edu.sumdu.j2se.clothes.InvalidClothesException;
import ua.edu.sumdu.j2se.clothes.Material;
import ua.edu.sumdu.j2se.clothes.Size;
import ua.edu.sumdu.j2se.clothes.Sleeve;
import ua.edu.sumdu.j2se.clothes.Store;
import ua.edu.sumdu.j2se.clothes.TextFileStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Вікно JavaFX для магазину одягу: додавання товарів, короткий список товарів з UUID
 * і пошук товару за UUID з виводом повної інформації. Товари читаються з input.txt
 * під час запуску і записуються назад під час закриття вікна.
 */
public class MainApp extends Application {

    /** Назви типів для списку вибору. */
    private static final String[] TYPE_TITLES = {"Светр", "Штани", "Джинси", "Сорочка", "Класична сорочка"};
    /** Назви класів у тому ж порядку, що й TYPE_TITLES. */
    private static final String[] TYPE_CLASSES = {"Sweater", "Pants", "Jeans", "Shirts", "FormalShirt"};

    private final Store store = new Store(ClothesDriver.STORE_NAME, ClothesDriver.STORE_ADDRESS);
    private final ClothesStorage storage = new TextFileStorage(TextFileStorage.DEFAULT_FILE);

    private final ComboBox<String> typeBox = new ComboBox<>();
    private final TextField nameField = new TextField();
    private final TextField brandField = new TextField();
    private final ComboBox<String> sizeBox = new ComboBox<>();
    private final TextField colorField = new TextField();
    private final ComboBox<String> materialBox = new ComboBox<>();
    private final TextField yearField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField quantityField = new TextField("1");
    private final TextField waistField = new TextField();
    private final TextField lengthField = new TextField();
    private final ComboBox<String> fitBox = new ComboBox<>();
    private final TextField densityField = new TextField();
    private final ComboBox<String> sleeveBox = new ComboBox<>();
    private final TextField collarField = new TextField();
    private final CheckBox cufflinksBox = new CheckBox("манжети під запонки");
    private final CheckBox nonIronBox = new CheckBox("не потребує прасування");

    private final ListView<String> listView = new ListView<>();
    private final List<Clothes> listItems = new ArrayList<>();
    private final TextField uuidField = new TextField();
    private final TextArea detailsArea = new TextArea();
    private final Label statusLabel = new Label();

    /**
     * Створює об'єкт вікна (викликається JavaFX під час запуску).
     */
    public MainApp() {
    }

    /**
     * Точка входу для запуску вікна.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Будує вікно, зчитує товари з файлу і показує їх у списку.
     *
     * @param stage головне вікно
     */
    @Override
    public void start(Stage stage) {
        fillChoices();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setLeft(createForm());
        VBox listPane = createListPane();
        VBox center = new VBox(listPane, createSearchPane());
        VBox.setVgrow(listPane, Priority.ALWAYS);
        root.setCenter(center);

        loadStore();
        refreshList();

        stage.setTitle("Магазин одягу - Конопля Іван, Ін-42/1");
        stage.setScene(new Scene(root, 1150, 660));
        stage.setOnCloseRequest(event -> saveStore());
        stage.show();
    }

    /**
     * Заповнює списки вибору значеннями з enum-ів.
     */
    private void fillChoices() {
        typeBox.getItems().addAll(TYPE_TITLES);
        typeBox.getSelectionModel().select(0);
        for (Size size : Size.values()) {
            sizeBox.getItems().add(size.name());
        }
        for (Material material : Material.values()) {
            materialBox.getItems().add(material.getTitle());
        }
        for (Fit fit : Fit.values()) {
            fitBox.getItems().add(fit.getTitle());
        }
        for (Sleeve sleeve : Sleeve.values()) {
            sleeveBox.getItems().add(sleeve.getTitle());
        }
        sizeBox.getSelectionModel().select(Size.M.name());
        materialBox.getSelectionModel().select(0);
        fitBox.getSelectionModel().select(0);
        sleeveBox.getSelectionModel().select(0);
        typeBox.setOnAction(event -> updateExtraFields());
        updateExtraFields();
    }

    /**
     * Створює форму для додавання товару.
     *
     * @return панель з формою
     */
    private VBox createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        String[] labels = {"Тип", "Назва", "Бренд", "Розмір", "Колір", "Матеріал", "Рік випуску", "Ціна, грн",
            "Кількість, шт.", "Обхват талії, см", "Довжина, см", "Крій", "Щільність деніму", "Рукав",
            "Комір, см"};
        Node[] fields = {typeBox, nameField, brandField, sizeBox, colorField, materialBox, yearField,
            priceField, quantityField, waistField, lengthField, fitBox, densityField, sleeveBox, collarField};
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i] + ":"), 0, i);
            grid.add(fields[i], 1, i);
        }
        grid.add(cufflinksBox, 1, labels.length);
        grid.add(nonIronBox, 1, labels.length + 1);

        Button addButton = new Button("Додати");
        addButton.setDefaultButton(true);
        addButton.setOnAction(event -> addClothes());

        Label title = new Label("Новий товар");
        title.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(8, title, grid, addButton);
        box.setPadding(new Insets(0, 15, 0, 0));
        return box;
    }

    /**
     * Створює блок зі скороченим списком усіх товарів.
     *
     * @return панель зі списком
     */
    private VBox createListPane() {
        Label title = new Label("Товари в магазині (коротко)");
        title.setStyle("-fx-font-weight: bold;");
        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index >= 0 && index < listItems.size()) {
                uuidField.setText(listItems.get(index).getUuid().toString());
            }
        });
        VBox box = new VBox(8, title, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        return box;
    }

    /**
     * Створює блок пошуку за UUID і вивід повної інформації.
     *
     * @return панель пошуку
     */
    private VBox createSearchPane() {
        Label title = new Label("Пошук за UUID");
        title.setStyle("-fx-font-weight: bold;");
        uuidField.setPromptText("наприклад 123e4567-e89b-12d3-a456-426614174000");
        Button findButton = new Button("Знайти");
        findButton.setOnAction(event -> findByUuid());
        HBox searchRow = new HBox(8, new Label("UUID:"), uuidField, findButton);
        HBox.setHgrow(uuidField, Priority.ALWAYS);
        detailsArea.setEditable(false);
        detailsArea.setPrefRowCount(6);
        detailsArea.setWrapText(true);
        VBox box = new VBox(8, title, searchRow, detailsArea, statusLabel);
        box.setPadding(new Insets(10, 0, 0, 0));
        return box;
    }

    /**
     * Вмикає лише ті додаткові поля, які є у вибраного типу.
     */
    private void updateExtraFields() {
        String type = getSelectedClass();
        boolean pants = type.equals("Pants") || type.equals("Jeans");
        boolean jeans = type.equals("Jeans");
        boolean shirts = type.equals("Shirts") || type.equals("FormalShirt");
        boolean formal = type.equals("FormalShirt");
        waistField.setDisable(!pants);
        lengthField.setDisable(!pants);
        fitBox.setDisable(!jeans);
        densityField.setDisable(!jeans);
        sleeveBox.setDisable(!shirts);
        collarField.setDisable(!shirts);
        cufflinksBox.setDisable(!formal);
        nonIronBox.setDisable(!formal);
    }

    /**
     * Повертає назву класу для вибраного типу.
     *
     * @return назва класу (Sweater, Pants, Jeans, Shirts, FormalShirt)
     */
    private String getSelectedClass() {
        int index = typeBox.getSelectionModel().getSelectedIndex();
        return TYPE_CLASSES[index < 0 ? 0 : index];
    }

    /**
     * Створює товар з даних форми і додає його в магазин.
     * Значення перевіряються конструкторами класів, як і під час читання файлу.
     */
    private void addClothes() {
        try {
            String type = getSelectedClass();
            List<String> values = new ArrayList<>();
            for (String key : ClothesRecord.keysFor(type)) {
                values.add(getFormValue(key, type));
            }
            Clothes clothes = ClothesRecord.fromValues(values);
            int quantity = parseQuantity(quantityField.getText());
            boolean added = store.addNewClothes(clothes, quantity);
            refreshList();
            if (added) {
                showStatus("Додано: " + clothes.getShortInfo(), false);
            } else {
                showStatus("Такий товар уже є, кількість збільшено до " + store.getQuantity(clothes) + " шт.", false);
            }
        } catch (InvalidClothesException e) {
            showStatus("Помилка: " + e.getMessage(), true);
        }
    }

    /**
     * Повертає значення поля форми для заданого ключа запису.
     *
     * @param key ключ з ClothesRecord.keysFor()
     * @param type назва класу
     * @return значення текстом
     */
    private String getFormValue(String key, String type) {
        switch (key) {
            case "type":
                return type;
            case "name":
                return nameField.getText();
            case "brand":
                return brandField.getText();
            case "size":
                return sizeBox.getValue();
            case "color":
                return colorField.getText();
            case "material":
                return materialBox.getValue();
            case "year":
                return yearField.getText();
            case "price":
                return priceField.getText();
            case "waist":
                return waistField.getText();
            case "length":
                return lengthField.getText();
            case "fit":
                return fitBox.getValue();
            case "density":
                return densityField.getText();
            case "sleeve":
                return sleeveBox.getValue();
            case "collar":
                return collarField.getText();
            case "cufflinks":
                return String.valueOf(cufflinksBox.isSelected());
            case "nonIron":
                return String.valueOf(nonIronBox.isSelected());
            default:
                return "";
        }
    }

    /**
     * Перетворює текст у кількість товару.
     *
     * @param text текст з поля
     * @return кількість
     * @throws InvalidClothesException якщо текст не є цілим числом
     */
    private static int parseQuantity(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidClothesException("кількість має бути цілим числом");
        }
    }

    /**
     * Шукає товар за UUID з поля вводу і показує повну інформацію про нього.
     */
    private void findByUuid() {
        detailsArea.clear();
        try {
            Clothes found = store.findByUuid(Identifiable.parseUuid(uuidField.getText()));
            if (found == null) {
                showStatus("Товар з таким UUID не знайдено.", true);
                return;
            }
            detailsArea.setText(describe(found));
            showStatus("Знайдено: " + found.getShortInfo(), false);
        } catch (IllegalArgumentException e) {
            showStatus("Помилка: " + e.getMessage(), true);
        }
    }

    /**
     * Формує повну інформацію про товар: усі поля, UUID і кількість.
     *
     * @param clothes товар
     * @return багаторядковий опис
     */
    private String describe(Clothes clothes) {
        StringBuilder text = new StringBuilder();
        text.append("Тип: ").append(clothes.getType()).append('\n');
        text.append(clothes).append('\n');
        text.append("UUID: ").append(clothes.getUuid()).append('\n');
        text.append("В наявності: ").append(store.getQuantity(clothes)).append(" шт.\n");
        text.append("Догляд: ").append(clothes.getCareInstructions());
        return text.toString();
    }

    /**
     * Оновлює скорочений список товарів.
     */
    private void refreshList() {
        listItems.clear();
        listItems.addAll(store.getClothes());
        listView.getItems().clear();
        for (Clothes clothes : listItems) {
            listView.getItems().add(clothes.getShortInfo());
        }
    }

    /**
     * Зчитує товари з файлу в магазин.
     */
    private void loadStore() {
        List<String> errors = new ArrayList<>();
        try {
            int count = storage.load(store, errors);
            showStatus("Зчитано з файлу " + storage.getFileName() + ": " + count + " запис(ів)"
                    + (errors.isEmpty() ? "" : ", пропущено: " + errors.size()), !errors.isEmpty());
        } catch (IOException e) {
            showStatus("Не вдалося прочитати файл: " + e.getMessage(), true);
        }
    }

    /**
     * Записує товари магазину у файл під час закриття вікна.
     */
    private void saveStore() {
        try {
            storage.save(store);
        } catch (IOException e) {
            System.out.println("Не вдалося записати файл " + storage.getFileName() + ": " + e.getMessage());
        }
    }

    /**
     * Показує повідомлення під блоком пошуку.
     *
     * @param message текст
     * @param error true, якщо це помилка (червоний колір)
     */
    private void showStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setStyle(error ? "-fx-text-fill: #c62828;" : "-fx-text-fill: #2e7d32;");
    }
}
