package ua.edu.sumdu.j2se.clothes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Гардероб власника. Агрегує об'єкти Clothes: речі створюються окремо
 * і лише додаються до гардероба, тому можуть існувати і без нього.
 */
public class Wardrobe {

    private final String owner;
    private final List<Clothes> items = new ArrayList<>();

    /**
     * Створює порожній гардероб.
     *
     * @param owner ім'я власника, не порожнє
     * @throws IllegalArgumentException якщо ім'я власника порожнє
     */
    public Wardrobe(String owner) {
        if (owner == null || owner.trim().isEmpty()) {
            throw new IllegalArgumentException("ім'я власника не може бути порожнім");
        }
        this.owner = owner.trim();
    }

    /**
     * Повертає ім'я власника.
     *
     * @return ім'я власника
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Додає річ до гардероба, якщо такої ще немає.
     *
     * @param clothes річ, не null
     * @return true, якщо річ додано; false, якщо така річ уже є
     * @throws IllegalArgumentException якщо clothes дорівнює null
     */
    public boolean add(Clothes clothes) {
        if (clothes == null) {
            throw new IllegalArgumentException("річ не задано");
        }
        if (items.contains(clothes)) {
            return false;
        }
        items.add(clothes);
        return true;
    }

    /**
     * Повертає річ за номером.
     *
     * @param index номер від 0 до size() - 1
     * @return річ
     * @throws IllegalArgumentException якщо номер поза межами
     */
    public Clothes get(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("немає речі з номером " + (index + 1));
        }
        return items.get(index);
    }

    /**
     * Повертає список речей тільки для читання.
     *
     * @return незмінний список речей
     */
    public List<Clothes> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Повертає кількість речей у гардеробі.
     *
     * @return кількість речей
     */
    public int size() {
        return items.size();
    }

    /**
     * Перевіряє, чи гардероб порожній.
     *
     * @return true, якщо речей немає
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Рахує загальну вартість усіх речей.
     *
     * @return сума цін, грн
     */
    public double getTotalPrice() {
        double sum = 0;
        for (Clothes clothes : items) {
            sum += clothes.getPrice();
        }
        return sum;
    }

    /**
     * Повертає короткий опис гардероба.
     *
     * @return власник, кількість речей і загальна вартість
     */
    @Override
    public String toString() {
        return "Wardrobe{власник='" + owner + "', речей=" + items.size()
                + ", вартість=" + String.format("%.2f", getTotalPrice()) + " грн}";
    }
}
