package ua.edu.sumdu.j2se.clothes;

/**
 * Базовий виняток для некоректних операцій з товарами магазину
 * (товар не знайдено, товар повторюється тощо).
 */
public class StoreException extends RuntimeException {

    /**
     * Створює виняток з повідомленням.
     *
     * @param message опис помилки
     */
    public StoreException(String message) {
        super(message);
    }
}
