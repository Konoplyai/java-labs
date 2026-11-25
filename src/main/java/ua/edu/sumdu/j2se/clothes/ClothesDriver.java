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
        System.out.println("Практична робота №6, варіант 1 (клас Clothes)");
        System.out.println("Виконав: Конопля Іван, група Ін-42/1");
    }

    /**
     * Показує меню та виконує вибрані дії, доки користувач не вибере вихід.
     *
     * @param scanner сканер
     * @param items список одягу
     */
    public static void runMenu(Scanner scanner, List<Clothes> items) {
        while (true) {
            System.out.println();
            System.out.println("Меню:");
            System.out.println("1. Створити новий одяг");
            System.out.println("2. Показати весь одяг");
            System.out.println("3. Вихід");
            int choice = readInt(scanner, "Ваш вибір: ", 1, 3);
            switch (choice) {
                case 1:
                    addClothes(scanner, items);
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
     * Зчитує дані нового одягу та додає його до списку.
     * Якщо дані некоректні або така річ уже є, річ не додається.
     *
     * @param scanner сканер
     * @param items список одягу
     */
    public static void addClothes(Scanner scanner, List<Clothes> items) {
        try {
            addToList(items, readClothes(scanner));
        } catch (InvalidClothesException e) {
            System.out.println("Помилка: " + e.getMessage() + ". Річ не додано.");
        }
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
     * Виводить усі речі через toString().
     *
     * @param items список одягу
     */
    public static void printAll(List<Clothes> items) {
        if (items.isEmpty()) {
            System.out.println("Список порожній.");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
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
