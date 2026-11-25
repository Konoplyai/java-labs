package ua.edu.sumdu.j2se.clothes;

/**
 * Виняток, який кидається, коли для одягу задано некоректне значення.
 */
public class InvalidClothesException extends IllegalArgumentException {

    /**
     * Створює виняток з повідомленням.
     *
     * @param message опис помилки
     */
    public InvalidClothesException(String message) {
        super(message);
    }
}
