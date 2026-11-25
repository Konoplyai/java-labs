package ua.edu.sumdu.j2se.clothes;

/**
 * Виняток, який кидається, коли операція призвела б до появи в магазині двох однакових товарів
 * або двох товарів з однаковим UUID.
 */
public class DuplicateObjectException extends StoreException {

    /**
     * Створює виняток з повідомленням.
     *
     * @param message опис повтору
     */
    public DuplicateObjectException(String message) {
        super(message);
    }
}
