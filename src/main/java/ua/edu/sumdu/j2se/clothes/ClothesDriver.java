package ua.edu.sumdu.j2se.clothes;

import java.io.Console;
import java.util.Scanner;

/**
 * Драйвер: створює масив одягу, заповнює його з клавіатури та виводить.
 */
public class ClothesDriver {

    /** Максимальна кількість речей. */
    public static final int MAX_COUNT = 20;

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
        Scanner scanner = createScanner();
        try {
            int count = readInt(scanner, "Скільки речей додати (1-" + MAX_COUNT + "): ", 1, MAX_COUNT);
            Clothes[] wardrobe = new Clothes[count];

            for (int i = 0; i < wardrobe.length; i++) {
                System.out.println("Річ " + (i + 1) + ":");
                wardrobe[i] = readClothes(scanner);
            }

            System.out.println();
            System.out.println("Усі речі:");
            printAll(wardrobe);
            printDuplicates(wardrobe);
        } catch (IllegalStateException e) {
            System.out.println();
            System.out.println("Помилка: " + e.getMessage() + ".");
        }
        scanner.close();
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
     * Зчитує з клавіатури дані однієї речі.
     *
     * @param scanner сканер
     * @return створений об'єкт одягу
     */
    public static Clothes readClothes(Scanner scanner) {
        String name = readText(scanner, "  назва: ");
        String brand = readText(scanner, "  бренд: ");
        String size = readSize(scanner, "  розмір (" + String.join(", ", Clothes.SIZES) + "): ");
        String color = readText(scanner, "  колір: ");
        double price = readPrice(scanner, "  ціна, грн: ");
        return new Clothes(name, brand, size, color, price);
    }

    /**
     * Виводить усі речі масиву через toString().
     *
     * @param wardrobe масив одягу
     */
    public static void printAll(Clothes[] wardrobe) {
        for (int i = 0; i < wardrobe.length; i++) {
            System.out.println((i + 1) + ". " + wardrobe[i]);
        }
    }

    /**
     * Шукає однакові речі в масиві за допомогою equals() і виводить їх номери.
     *
     * @param wardrobe масив одягу
     */
    public static void printDuplicates(Clothes[] wardrobe) {
        boolean found = false;
        for (int i = 0; i < wardrobe.length; i++) {
            for (int j = i + 1; j < wardrobe.length; j++) {
                if (wardrobe[i].equals(wardrobe[j])) {
                    System.out.println("Однакові речі: " + (i + 1) + " і " + (j + 1));
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("Однакових речей немає.");
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
     * @return розмір великими літерами
     */
    public static String readSize(Scanner scanner, String prompt) {
        while (true) {
            String line = readLine(scanner, prompt);
            if (Clothes.isValidSize(line)) {
                return line.toUpperCase();
            }
            System.out.println("  Помилка: такого розміру немає.");
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
