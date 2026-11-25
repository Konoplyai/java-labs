package ua.edu.sumdu.j2se.clothes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Магазин одягу. Зберігає товари в ArrayList, а кількість кожного товару
 * в іншому ArrayList з тим самим індексом (кількість зберігається як Integer).
 * Магазин "володіє" своєю колекцією: змінити її можна лише через методи класу.
 */
public class Store {

    /** Найбільша кількість одного товару в магазині. */
    public static final int MAX_QUANTITY = 10000;

    private final String name;
    private final String address;
    private final ArrayList<Clothes> clothes = new ArrayList<>();
    private final ArrayList<Integer> quantities = new ArrayList<>();

    /**
     * Створює порожній магазин.
     *
     * @param name назва магазину
     * @param address адреса магазину
     * @throws InvalidClothesException якщо назва або адреса порожні чи задовгі
     */
    public Store(String name, String address) {
        this.name = checkText(name, "назва магазину");
        this.address = checkText(address, "адреса магазину");
    }

    /**
     * Повертає назву магазину.
     *
     * @return назва
     */
    public String getName() {
        return name;
    }

    /**
     * Повертає адресу магазину.
     *
     * @return адреса
     */
    public String getAddress() {
        return address;
    }

    /**
     * Додає товар у магазин. Якщо такий самий товар (equals) уже є,
     * новий об'єкт не додається, а збільшується кількість наявного.
     *
     * @param cl товар
     * @param quantity кількість, від 1 до {@link #MAX_QUANTITY}
     * @return true, якщо додано новий товар; false, якщо збільшено кількість наявного
     * @throws InvalidClothesException якщо товар null, кількість некоректна
     *                                 або загальна кількість перевищить {@link #MAX_QUANTITY}
     */
    public boolean addNewClothes(Clothes cl, int quantity) {
        if (cl == null) {
            throw new InvalidClothesException("товар не задано");
        }
        if (quantity < 1 || quantity > MAX_QUANTITY) {
            throw new InvalidClothesException("кількість має бути від 1 до " + MAX_QUANTITY);
        }
        int index = clothes.indexOf(cl);
        if (index == -1) {
            if (findByUuid(cl.getUuid()) != null) {
                throw new InvalidClothesException("товар з UUID " + cl.getUuid() + " уже є в магазині");
            }
            clothes.add(cl);
            quantities.add(quantity);
            return true;
        }
        int total = quantities.get(index) + quantity;
        if (total > MAX_QUANTITY) {
            throw new InvalidClothesException("разом буде " + total + " шт., а можна не більше " + MAX_QUANTITY);
        }
        quantities.set(index, total);
        return false;
    }

    /**
     * Оновлює дані товару: товар, рівний existingObject (equals), замінюється на newObject.
     * Новий об'єкт отримує UUID старого, кількість і місце в списку не змінюються.
     *
     * @param existingObject товар, який треба змінити
     * @param newObject нові дані товару (може бути іншого класу ієрархії)
     * @return true, якщо оновлення виконано; false, якщо такого товару немає в магазині
     * @throws InvalidClothesException якщо нові дані не задано або такий товар уже є в магазині
     */
    public boolean update(Clothes existingObject, Clothes newObject) {
        if (newObject == null) {
            throw new InvalidClothesException("нові дані товару не задано");
        }
        int index = clothes.indexOf(existingObject);
        if (index == -1) {
            return false;
        }
        for (int i = 0; i < clothes.size(); i++) {
            if (i != index && clothes.get(i).equals(newObject)) {
                throw new InvalidClothesException("такий товар уже є в магазині");
            }
        }
        newObject.restoreUuid(clothes.get(index).getUuid());
        clothes.set(index, newObject);
        return true;
    }

    /**
     * Видаляє товар з магазину разом з усією його кількістю.
     *
     * @param existingObject товар, який треба видалити (шукається через equals)
     * @return true, якщо товар видалено; false, якщо такого товару немає в магазині
     */
    public boolean delete(Clothes existingObject) {
        int index = clothes.indexOf(existingObject);
        if (index == -1) {
            return false;
        }
        clothes.remove(index);
        quantities.remove(index);
        return true;
    }

    /**
     * Змінює кількість наявного товару.
     *
     * @param cl товар
     * @param quantity нова кількість, від 1 до {@link #MAX_QUANTITY}
     * @throws InvalidClothesException якщо товару немає в магазині або кількість некоректна
     */
    public void setQuantity(Clothes cl, int quantity) {
        int index = indexOfExisting(cl);
        if (quantity < 1 || quantity > MAX_QUANTITY) {
            throw new InvalidClothesException("кількість має бути від 1 до " + MAX_QUANTITY);
        }
        quantities.set(index, quantity);
    }

    /**
     * Повертає індекс товару, який точно є в магазині.
     *
     * @param cl товар
     * @return індекс у списку
     * @throws InvalidClothesException якщо такого товару немає
     */
    private int indexOfExisting(Clothes cl) {
        int index = clothes.indexOf(cl);
        if (index == -1) {
            throw new InvalidClothesException("такого товару немає в магазині");
        }
        return index;
    }

    /**
     * Повертає кількість заданого товару в магазині.
     *
     * @param cl товар
     * @return кількість або 0, якщо такого товару немає
     */
    public int getQuantity(Clothes cl) {
        int index = clothes.indexOf(cl);
        if (index == -1) {
            return 0;
        }
        return quantities.get(index);
    }

    /**
     * Повертає список товарів лише для читання.
     *
     * @return незмінний список товарів у порядку додавання
     */
    public List<Clothes> getClothes() {
        return Collections.unmodifiableList(clothes);
    }

    /**
     * Повертає новий список усіх товарів, відсортований за назвою (див. {@link Clothes#compareTo(Clothes)}).
     * Порядок товарів у самому магазині не змінюється.
     *
     * @return відсортований список товарів
     */
    public List<Clothes> getSortedClothes() {
        List<Clothes> sorted = new ArrayList<>(clothes);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Повертає новий список усіх товарів, відсортований заданим компаратором.
     * Порядок товарів у самому магазині не змінюється.
     *
     * @param comparator правило порівняння товарів
     * @return відсортований список товарів
     * @throws IllegalArgumentException якщо компаратор не задано
     */
    public List<Clothes> getSortedClothes(Comparator<Clothes> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("критерій сортування не задано");
        }
        List<Clothes> sorted = new ArrayList<>(clothes);
        Collections.sort(sorted, comparator);
        return sorted;
    }

    /**
     * Повертає кількість різних товарів (позицій).
     *
     * @return кількість позицій
     */
    public int size() {
        return clothes.size();
    }

    /**
     * Перевіряє, чи магазин порожній.
     *
     * @return true, якщо товарів немає
     */
    public boolean isEmpty() {
        return clothes.isEmpty();
    }

    /**
     * Рахує загальну кількість одиниць усіх товарів.
     *
     * @return сума кількостей
     */
    public int getTotalQuantity() {
        int total = 0;
        for (Integer quantity : quantities) {
            total += quantity;
        }
        return total;
    }

    /**
     * Рахує вартість усіх товарів з урахуванням кількості.
     *
     * @return загальна вартість, грн
     */
    public double getTotalValue() {
        double total = 0;
        for (int i = 0; i < clothes.size(); i++) {
            total += clothes.get(i).getPrice() * quantities.get(i);
        }
        return total;
    }

    /**
     * Шукає товар за UUID.
     *
     * @param uuid ідентифікатор
     * @return знайдений товар або null, якщо такого немає
     */
    public Clothes findByUuid(UUID uuid) {
        for (Clothes item : clothes) {
            if (item.getUuid().equals(uuid)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Шукає товари, назва яких містить заданий текст (без урахування регістру).
     * Колекція не змінюється, результат повертається в новому списку.
     *
     * @param text частина назви
     * @return знайдені товари (порожній список, якщо нічого не знайдено)
     */
    public List<Clothes> findByName(String text) {
        List<Clothes> result = new ArrayList<>();
        String query = text.trim().toLowerCase();
        for (Clothes item : clothes) {
            if (item.getName().toLowerCase().contains(query)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Шукає товари заданого класу, включно з його нащадками
     * (наприклад, пошук штанів знаходить і джинси).
     *
     * @param type клас, об'єкти якого потрібно знайти
     * @return знайдені товари (порожній список, якщо нічого не знайдено)
     */
    public List<Clothes> findByType(Class<? extends Clothes> type) {
        List<Clothes> result = new ArrayList<>();
        for (Clothes item : clothes) {
            if (type.isInstance(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Шукає товари з ціною в заданих межах (межі включаються).
     *
     * @param min найменша ціна, грн
     * @param max найбільша ціна, грн
     * @return знайдені товари (порожній список, якщо нічого не знайдено)
     */
    public List<Clothes> findByPriceRange(double min, double max) {
        List<Clothes> result = new ArrayList<>();
        for (Clothes item : clothes) {
            if (item.getPrice() >= min && item.getPrice() <= max) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Шукає товари заданого розміру.
     *
     * @param size розмір
     * @return знайдені товари (порожній список, якщо нічого не знайдено)
     */
    public List<Clothes> findBySize(Size size) {
        List<Clothes> result = new ArrayList<>();
        for (Clothes item : clothes) {
            if (item.getSize() == size) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Повертає коротку інформацію про магазин.
     *
     * @return назва, адреса і кількість товарів
     */
    @Override
    public String toString() {
        return "Store{назва='" + name + "', адреса='" + address + "', позицій=" + clothes.size()
                + ", одиниць=" + getTotalQuantity() + "}";
    }

    /**
     * Перевіряє текстове поле магазину.
     *
     * @param value значення
     * @param field назва поля для повідомлення
     * @return значення без пробілів по краях
     * @throws InvalidClothesException якщо значення порожнє або задовге
     */
    private static String checkText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidClothesException("поле \"" + field + "\" не може бути порожнім");
        }
        if (value.trim().length() > Clothes.MAX_TEXT_LENGTH) {
            throw new InvalidClothesException("поле \"" + field + "\" не може бути довшим за "
                    + Clothes.MAX_TEXT_LENGTH + " символів");
        }
        return value.trim();
    }
}
