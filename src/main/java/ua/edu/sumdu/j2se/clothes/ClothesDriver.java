package ua.edu.sumdu.j2se.clothes;

import java.util.Scanner;

/**
 * Драйвер: створює масив одягу, заповнює його з клавіатури та виводить.
 */
public class ClothesDriver {

    /**
     * Точка входу в програму.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Скільки речей додати: ");
        int count = Integer.parseInt(scanner.nextLine().trim());
        Clothes[] wardrobe = new Clothes[count];

        for (int i = 0; i < wardrobe.length; i++) {
            System.out.println("Річ " + (i + 1) + ":");
            System.out.print("  назва: ");
            String name = scanner.nextLine();
            System.out.print("  бренд: ");
            String brand = scanner.nextLine();
            System.out.print("  розмір: ");
            String size = scanner.nextLine();
            System.out.print("  колір: ");
            String color = scanner.nextLine();
            System.out.print("  ціна: ");
            double price = Double.parseDouble(scanner.nextLine());
            wardrobe[i] = new Clothes(name, brand, size, color, price);
        }

        System.out.println();
        System.out.println("Усі речі:");
        for (int i = 0; i < wardrobe.length; i++) {
            System.out.println((i + 1) + ". " + wardrobe[i]);
        }

        scanner.close();
    }
}
