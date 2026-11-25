package ua.edu.sumdu.j2se.clothes;

/**
 * Виняток, який кидається, коли операцію виконують над товаром, якого немає в магазині
 * (модифікація, видалення, зміна кількості).
 */
public class ObjectNotFoundException extends StoreException {

    private final transient Clothes missing;

    /**
     * Створює виняток для товару, якого не знайдено.
     *
     * @param missing товар, який шукали (може бути null)
     */
    public ObjectNotFoundException(Clothes missing) {
        super(missing == null ? "товар не задано"
                : "товар \"" + missing.getName() + "\" не знайдено в магазині");
        this.missing = missing;
    }

    /**
     * Повертає товар, який не вдалося знайти.
     *
     * @return товар або null
     */
    public Clothes getMissing() {
        return missing;
    }
}
