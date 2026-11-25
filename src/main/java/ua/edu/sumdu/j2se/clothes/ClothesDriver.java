package ua.edu.sumdu.j2se.clothes;

import java.io.Console;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Драйвер з консольним меню для роботи з магазином одягу.
 */
public class ClothesDriver {

    /** Назва магазину. */
    public static final String STORE_NAME = "Магазин одягу";
    /** Адреса магазину. */
    public static final String STORE_ADDRESS = "м. Суми";

    /**
     * Закритий конструктор, бо клас містить лише статичні методи.
     */
    private ClothesDriver() {
    }

    /**
     * Точка входу в програму. На початку зчитує товари з файлу в магазин,
     * під час завершення записує актуальні товари з кількістю назад у цей самий файл.
     *
     * @param args якщо перший аргумент "json", використовується файл input.json,
     *             інакше input.txt
     */
    public static void main(String[] args) {
        printHeader();
        ClothesStorage storage = createStorage(args);
        Store store = new Store(STORE_NAME, STORE_ADDRESS);
        System.out.println("Магазин: " + store.getName() + ", " + store.getAddress());
        loadStore(storage, store);
        Scanner scanner = createScanner();
        try {
            runMenu(scanner, store);
        } catch (IllegalStateException e) {
            System.out.println();
            System.out.println("Помилка: " + e.getMessage() + ".");
        }
        saveStore(storage, store);
        scanner.close();
    }

    /**
     * Вибирає сховище за аргументами командного рядка.
     *
     * @param args аргументи командного рядка
     * @return сховище JSON, якщо перший аргумент "json", інакше текстове
     */
    public static ClothesStorage createStorage(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("json")) {
            return new JsonFileStorage(JsonFileStorage.DEFAULT_FILE);
        }
        return new TextFileStorage(TextFileStorage.DEFAULT_FILE);
    }

    /**
     * Зчитує товари зі сховища в магазин і виводить повідомлення про пропущені записи.
     *
     * @param storage сховище
     * @param store магазин
     */
    public static void loadStore(ClothesStorage storage, Store store) {
        List<String> errors = new ArrayList<>();
        int count = 0;
        try {
            count = storage.load(store, errors);
        } catch (IOException e) {
            System.out.println("Не вдалося прочитати файл " + storage.getFileName() + ": " + e.getMessage());
        }
        for (String error : errors) {
            System.out.println("  " + error);
        }
        System.out.println("Зчитано з файлу " + storage.getFileName() + ": " + count + " запис(ів), у магазині "
                + store.size() + " товар(ів), " + store.getTotalQuantity() + " шт.");
    }

    /**
     * Записує всі товари магазину у сховище.
     *
     * @param storage сховище
     * @param store магазин
     */
    public static void saveStore(ClothesStorage storage, Store store) {
        try {
            storage.save(store);
            System.out.println("Записано у файл " + storage.getFileName() + ": " + store.size() + " товар(ів).");
        } catch (IOException e) {
            System.out.println("Не вдалося записати файл " + storage.getFileName() + ": " + e.getMessage());
        }
    }

    /**
     * Виводить інформаційну шапку програми.
     */
    public static void printHeader() {
        System.out.println("Практична робота №13, варіант 1 (магазин одягу, сортування)");
        System.out.println("Виконав: Конопля Іван, група Ін-42/1");
    }

    /**
     * Показує головне меню та виконує вибрані дії, доки користувач не вибере вихід.
     *
     * @param scanner сканер
     * @param store магазин
     */
    public static void runMenu(Scanner scanner, Store store) {
        while (true) {
            System.out.println();
            System.out.println("Меню:");
            System.out.println("1. Пошук товару");
            System.out.println("2. Додати товар");
            System.out.println("3. Вивести всі товари");
            System.out.println("4. Вивести відсортовану інформацію про всі товари");
            System.out.println("5. Завершити роботу");
            int choice = readInt(scanner, "Ваш вибір: ", 1, 5);
            switch (choice) {
                case 1:
                    searchMenu(scanner, store);
                    break;
                case 2:
                    createObject(scanner, store);
                    break;
                case 3:
                    printAll(store);
                    break;
                case 4:
                    printSorted(store);
                    break;
                default:
                    System.out.println("Роботу завершено.");
                    return;
            }
        }
    }

    /**
     * Підменю пошуку: користувач вибирає критерій, вводить значення і бачить результат.
     * Пункт 0 повертає до головного меню. Пошук виконують методи класу Store,
     * колекція магазину при цьому не змінюється.
     *
     * @param scanner сканер
     * @param store магазин
     */
    public static void searchMenu(Scanner scanner, Store store) {
        while (true) {
            System.out.println();
            System.out.println("Пошук за:");
            System.out.println("1. Назвою (частина назви)");
            System.out.println("2. Типом");
            System.out.println("3. Діапазоном ціни");
            System.out.println("4. Розміром");
            System.out.println("0. Повернутися до головного меню");
            int choice = readInt(scanner, "Критерій: ", 0, 4);
            List<Clothes> found;
            switch (choice) {
                case 1:
                    found = store.findByName(readText(scanner, "  текст для пошуку в назві: "));
                    break;
                case 2:
                    found = store.findByType(readType(scanner));
                    break;
                case 3:
                    double min = readPrice(scanner, "  ціна від, грн: ");
                    double max = readMaxPrice(scanner, min);
                    found = store.findByPriceRange(min, max);
                    break;
                case 4:
                    found = store.findBySize(readSize(scanner, "  розмір (" + Size.list() + "): "));
                    break;
                default:
                    return;
            }
            printSearchResult(store, found);
        }
    }

    /**
     * Пропонує вибрати клас для пошуку за типом.
     *
     * @param scanner сканер
     * @return вибраний клас
     */
    public static Class<? extends Clothes> readType(Scanner scanner) {
        System.out.println("  1. Одяг (усі речі)");
        System.out.println("  2. Штани (разом з джинсами)");
        System.out.println("  3. Джинси");
        System.out.println("  4. Сорочки (разом з класичними)");
        System.out.println("  5. Класичні сорочки");
        System.out.println("  6. Светри");
        int type = readInt(scanner, "  тип: ", 1, 6);
        switch (type) {
            case 2:
                return Pants.class;
            case 3:
                return Jeans.class;
            case 4:
                return Shirts.class;
            case 5:
                return FormalShirt.class;
            case 6:
                return Sweater.class;
            default:
                return Clothes.class;
        }
    }

    /**
     * Зчитує верхню межу ціни, яка не може бути меншою за нижню.
     *
     * @param scanner сканер
     * @param min нижня межа ціни
     * @return верхня межа ціни
     */
    public static double readMaxPrice(Scanner scanner, double min) {
        while (true) {
            double max = readPrice(scanner, "  ціна до, грн: ");
            if (max >= min) {
                return max;
            }
            System.out.println("  Помилка: верхня межа не може бути меншою за " + String.format("%.2f", min) + ".");
        }
    }

    /**
     * Виводить результат пошуку з кількістю кожного товару
     * або повідомлення, що нічого не знайдено.
     *
     * @param store магазин
     * @param found знайдені товари
     */
    public static void printSearchResult(Store store, List<Clothes> found) {
        if (found.isEmpty()) {
            System.out.println("Жоден товар не відповідає умовам пошуку.");
            return;
        }
        System.out.println("Знайдено: " + found.size());
        for (int i = 0; i < found.size(); i++) {
            Clothes clothes = found.get(i);
            System.out.println((i + 1) + ". " + clothes.getType() + ": " + clothes
                    + ", в наявності " + store.getQuantity(clothes) + " шт.");
        }
    }

    /**
     * Пропонує вибрати тип нового товару, зчитує його дані і кількість та додає в магазин.
     * Пункт 0 повертає до головного меню без створення об'єкта.
     *
     * @param scanner сканер
     * @param store магазин
     */
    public static void createObject(Scanner scanner, Store store) {
        System.out.println("Оберіть тип:");
        System.out.println("1. Светр");
        System.out.println("2. Штани");
        System.out.println("3. Джинси");
        System.out.println("4. Сорочка");
        System.out.println("5. Класична сорочка");
        System.out.println("0. Повернутися до головного меню");
        int type = readInt(scanner, "Тип: ", 0, 5);
        if (type == 0) {
            return;
        }
        try {
            Clothes clothes;
            switch (type) {
                case 1:
                    clothes = new Sweater(readClothes(scanner));
                    break;
                case 2:
                    clothes = readPants(scanner);
                    break;
                case 3:
                    clothes = readJeans(scanner);
                    break;
                case 4:
                    clothes = readShirts(scanner);
                    break;
                default:
                    clothes = readFormalShirt(scanner);
                    break;
            }
            int quantity = readInt(scanner, "  кількість, шт.: ", 1, Store.MAX_QUANTITY);
            addToStore(store, clothes, quantity);
        } catch (InvalidClothesException e) {
            System.out.println("Помилка: " + e.getMessage() + ". Об'єкт не створено.");
        }
    }

    /**
     * Створює сканер у кодуванні консолі, щоб правильно читалась кирилиця.
     *
     * @return сканер для System.in
     */
    public static Scanner createScanner() {
        Console console = System.console();
        if (console != null) {
            return new Scanner(System.in, console.charset());
        }
        return new Scanner(System.in);
    }

    /**
     * Зчитує дані штанів.
     *
     * @param scanner сканер
     * @return штани
     * @throws InvalidClothesException якщо дані не пройшли перевірку
     */
    public static Pants readPants(Scanner scanner) {
        Clothes base = readClothes(scanner);
        int waist = readInt(scanner, "  обхват талії, см: ", Pants.MIN_WAIST, Pants.MAX_WAIST);
        int length = readInt(scanner, "  довжина, см: ", Pants.MIN_LENGTH, Pants.MAX_LENGTH);
        return new Pants(base, waist, length);
    }

    /**
     * Зчитує дані джинсів.
     *
     * @param scanner сканер
     * @return джинси
     * @throws InvalidClothesException якщо дані не пройшли перевірку
     */
    public static Jeans readJeans(Scanner scanner) {
        Pants base = readPants(scanner);
        Fit fit = readFit(scanner, "  крій (" + Fit.list() + "): ");
        int density = readInt(scanner, "  щільність деніму, унцій: ", Jeans.MIN_DENSITY, Jeans.MAX_DENSITY);
        return new Jeans(base, fit, density);
    }

    /**
     * Зчитує дані сорочки.
     *
     * @param scanner сканер
     * @return сорочка
     * @throws InvalidClothesException якщо дані не пройшли перевірку
     */
    public static Shirts readShirts(Scanner scanner) {
        Clothes base = readClothes(scanner);
        Sleeve sleeve = readSleeve(scanner, "  рукав (" + Sleeve.list() + "): ");
        int collar = readInt(scanner, "  розмір коміра, см: ", Shirts.MIN_COLLAR, Shirts.MAX_COLLAR);
        return new Shirts(base, sleeve, collar);
    }

    /**
     * Зчитує дані класичної сорочки.
     *
     * @param scanner сканер
     * @return класична сорочка
     * @throws InvalidClothesException якщо дані не пройшли перевірку
     */
    public static FormalShirt readFormalShirt(Scanner scanner) {
        Shirts base = readShirts(scanner);
        boolean cufflinks = false;
        if (base.getSleeve() == Sleeve.LONG) {
            cufflinks = readYesNo(scanner, "  манжети під запонки (так/ні): ");
        }
        boolean nonIron = readYesNo(scanner, "  не потребує прасування (так/ні): ");
        return new FormalShirt(base, cufflinks, nonIron);
    }

    /**
     * Додає товар у магазин методом addNewClothes і повідомляє результат.
     *
     * @param store магазин
     * @param clothes товар
     * @param quantity кількість
     * @throws InvalidClothesException якщо загальна кількість перевищить допустиму
     */
    private static void addToStore(Store store, Clothes clothes, int quantity) {
        if (store.addNewClothes(clothes, quantity)) {
            System.out.println("Додано новий товар: " + clothes + ", " + quantity + " шт.");
        } else {
            System.out.println("Такий товар уже є, кількість збільшено до " + store.getQuantity(clothes) + " шт.");
        }
    }

    /**
     * Зчитує з клавіатури спільні поля одягу. Оскільки Clothes абстрактний, поля
     * зберігаються в об'єкті Sweater, з якого їх копіюють конструктори інших класів.
     *
     * @param scanner сканер
     * @return об'єкт зі спільними полями
     * @throws InvalidClothesException якщо дані не пройшли перевірку в класі Clothes
     */
    public static Clothes readClothes(Scanner scanner) {
        String name = readText(scanner, "  назва: ");
        String brand = readText(scanner, "  бренд: ");
        Size size = readSize(scanner, "  розмір (" + Size.list() + "): ");
        String color = readText(scanner, "  колір: ");
        Material material = readMaterial(scanner, "  матеріал (" + Material.list() + "): ");
        int year = readInt(scanner, "  рік випуску: ", Clothes.MIN_YEAR, Year.now().getValue());
        double price = readPrice(scanner, "  ціна, грн: ");
        return new Sweater(name, brand, size, color, material, year, price);
    }

    /**
     * Виводить усі товари магазину, відсортовані за назвою (через Comparable).
     * Сортується копія списку, порядок товарів у магазині не змінюється.
     *
     * @param store магазин
     */
    public static void printSorted(Store store) {
        if (store.isEmpty()) {
            System.out.println("У магазині немає товарів.");
            return;
        }
        List<Clothes> sorted = store.getSortedClothes();
        System.out.println("Товари, відсортовані за назвою:");
        for (int i = 0; i < sorted.size(); i++) {
            Clothes clothes = sorted.get(i);
            System.out.println((i + 1) + ". " + clothes.getName() + " - " + clothes.getType() + ": " + clothes
                    + ", " + store.getQuantity(clothes) + " шт.");
        }
    }

    /**
     * Виводить усі товари магазину з кількістю. Для кожного об'єкта через посилання
     * типу Clothes викликаються getType(), toString() і getCareInstructions(),
     * а виконується версія методу з реального класу об'єкта (поліморфізм).
     *
     * @param store магазин
     */
    public static void printAll(Store store) {
        if (store.isEmpty()) {
            System.out.println("У магазині немає товарів.");
            return;
        }
        List<Clothes> items = store.getClothes();
        Map<String, Integer> byType = new LinkedHashMap<>();
        for (int i = 0; i < items.size(); i++) {
            Clothes clothes = items.get(i);
            int quantity = store.getQuantity(clothes);
            System.out.println((i + 1) + ". " + clothes.getType() + ": " + clothes + ", " + quantity + " шт.");
            System.out.println("   догляд: " + clothes.getCareInstructions());
            Integer count = byType.get(clothes.getType());
            byType.put(clothes.getType(), count == null ? quantity : count + quantity);
        }
        StringBuilder types = new StringBuilder();
        for (Map.Entry<String, Integer> entry : byType.entrySet()) {
            if (types.length() > 0) {
                types.append(", ");
            }
            types.append(entry.getKey()).append(" - ").append(entry.getValue());
        }
        System.out.println("Усього товарів: " + store.size() + ", одиниць: " + store.getTotalQuantity()
                + ", загальна вартість: " + String.format("%.2f", store.getTotalValue()) + " грн");
        System.out.println("Одиниць за типами: " + types);
    }

    /**
     * Зчитує ціле число в заданих межах, доки не буде введено правильне значення.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @param min мінімальне значення
     * @param max максимальне значення
     * @return введене число
     */
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            String line = readLine(scanner, prompt);
            if (line.isEmpty()) {
                System.out.println("  Помилка: значення не може бути порожнім.");
                continue;
            }
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("  Помилка: число має бути від " + min + " до " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Помилка: потрібно ввести ціле число.");
            }
        }
    }

    /**
     * Зчитує непорожній текст.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return введений текст
     */
    public static String readText(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt);
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("  Помилка: значення не може бути порожнім.");
        }
    }

    /**
     * Зчитує розмір зі списку допустимих.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return розмір
     */
    public static Size readSize(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt);
            try {
                return Size.fromString(line);
            } catch (InvalidClothesException e) {
                System.out.println("  Помилка: такого розміру немає.");
            }
        }
    }

    /**
     * Зчитує крій джинсів зі списку допустимих.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return крій
     */
    public static Fit readFit(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt);
            try {
                return Fit.fromString(line);
            } catch (InvalidClothesException e) {
                System.out.println("  Помилка: такого крою немає.");
            }
        }
    }

    /**
     * Зчитує відповідь так/ні.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return true для "так", false для "ні"
     */
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt).toLowerCase();
            if (line.equals("так") || line.equals("т") || line.equals("y")) {
                return true;
            }
            if (line.equals("ні") || line.equals("н") || line.equals("n")) {
                return false;
            }
            System.out.println("  Помилка: введіть так або ні.");
        }
    }

    /**
     * Зчитує довжину рукава зі списку допустимих.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return довжина рукава
     */
    public static Sleeve readSleeve(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt);
            try {
                return Sleeve.fromString(line);
            } catch (InvalidClothesException e) {
                System.out.println("  Помилка: такого значення немає.");
            }
        }
    }

    /**
     * Зчитує матеріал зі списку допустимих.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return матеріал
     */
    public static Material readMaterial(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt);
            try {
                return Material.fromString(line);
            } catch (InvalidClothesException e) {
                System.out.println("  Помилка: такого матеріалу немає.");
            }
        }
    }

    /**
     * Зчитує ціну більше 0 (можна вводити через крапку або кому).
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return ціна
     */
    public static double readPrice(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt).replace(',', '.');
            if (line.isEmpty()) {
                System.out.println("  Помилка: значення не може бути порожнім.");
                continue;
            }
            try {
                double value = Double.parseDouble(line);
                if (value > 0 && !Double.isInfinite(value)) {
                    return value;
                }
                System.out.println("  Помилка: ціна має бути більше 0.");
            } catch (NumberFormatException e) {
                System.out.println("  Помилка: потрібно ввести число.");
            }
        }
    }

    /**
     * Виводить підказку та зчитує рядок без пробілів по краях.
     *
     * @param scanner сканер
     * @param prompt підказка
     * @return введений рядок
     * @throws IllegalStateException якщо введення закінчилось
     */
    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("введення даних перервано");
        }
        return scanner.nextLine().trim();
    }
}
