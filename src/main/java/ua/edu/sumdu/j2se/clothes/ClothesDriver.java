package ua.edu.sumdu.j2se.clothes;

import java.io.Console;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Драйвер з консольним меню для роботи зі списком одягу.
 */
public class ClothesDriver {

    /**
     * Закритий конструктор, бо клас містить лише статичні методи.
     */
    private ClothesDriver() {
    }

    /**
     * Точка входу в програму.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        printHeader();
        Scanner scanner = createScanner();
        List<Clothes> items = new ArrayList<>();
        try {
            runMenu(scanner, items);
        } catch (IllegalStateException e) {
            System.out.println();
            System.out.println("Помилка: " + e.getMessage() + ".");
        }
        scanner.close();
    }

    /**
     * Виводить інформаційну шапку програми.
     */
    public static void printHeader() {
        System.out.println("Практична робота №8, варіант 1 (ієрархія класів одягу)");
        System.out.println("Виконав: Конопля Іван, група Ін-42/1");
    }

    /**
     * Показує головне меню та виконує вибрані дії, доки користувач не вибере вихід.
     *
     * @param scanner сканер
     * @param items список одягу
     */
    public static void runMenu(Scanner scanner, List<Clothes> items) {
        while (true) {
            System.out.println();
            System.out.println("Меню:");
            System.out.println("1. Створити новий об'єкт");
            System.out.println("2. Вивести інформацію про всі об'єкти");
            System.out.println("3. Завершити роботу");
            int choice = readInt(scanner, "Ваш вибір: ", 1, 3);
            switch (choice) {
                case 1:
                    createObject(scanner, items);
                    break;
                case 2:
                    printAll(items);
                    break;
                default:
                    System.out.println("Роботу завершено.");
                    return;
            }
        }
    }

    /**
     * Пропонує вибрати тип нового об'єкта, зчитує його дані та додає до списку.
     * Пункт 0 повертає до головного меню без створення об'єкта.
     *
     * @param scanner сканер
     * @param items список одягу
     */
    public static void createObject(Scanner scanner, List<Clothes> items) {
        System.out.println("Оберіть тип:");
        System.out.println("1. Одяг");
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
                    clothes = readClothes(scanner);
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
            addToList(items, clothes);
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
     * Додає річ до списку, якщо такої ще немає, і повідомляє результат.
     *
     * @param items список одягу
     * @param clothes річ
     */
    private static void addToList(List<Clothes> items, Clothes clothes) {
        if (items.contains(clothes)) {
            System.out.println("Така річ уже є в списку, не додано.");
        } else {
            items.add(clothes);
            System.out.println("Додано: " + clothes);
        }
    }

    /**
     * Зчитує з клавіатури дані однієї речі.
     *
     * @param scanner сканер
     * @return створений об'єкт одягу
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
        return new Clothes(name, brand, size, color, material, year, price);
    }

    /**
     * Виводить усі речі зі спільного списку. Для кожного об'єкта через посилання
     * типу Clothes викликаються getType() і toString(), а виконується версія
     * методу з реального класу об'єкта (поліморфізм).
     *
     * @param items список одягу
     */
    public static void printAll(List<Clothes> items) {
        if (items.isEmpty()) {
            System.out.println("Список порожній.");
            return;
        }
        int pants = 0;
        int shirts = 0;
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            Clothes clothes = items.get(i);
            System.out.println((i + 1) + ". " + clothes.getType() + ": " + clothes);
            if (clothes instanceof Pants) {
                pants++;
            } else if (clothes instanceof Shirts) {
                shirts++;
            }
            total += clothes.getPrice();
        }
        System.out.println("Усього речей: " + items.size() + " (штанів: " + pants + ", сорочок: " + shirts
                + ", іншого одягу: " + (items.size() - pants - shirts) + "), загальна вартість: "
                + String.format("%.2f", total) + " грн");
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
