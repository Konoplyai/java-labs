package ua.edu.sumdu.j2se.clothes;

import java.util.Objects;

/**
 * Класична (ділова) сорочка - різновид сорочок.
 * Додатково зберігає, чи є манжети під запонки і чи потрібне прасування.
 */
public class FormalShirt extends Shirts {

    private boolean cufflinks;
    private boolean nonIron;

    /**
     * Створює класичну сорочку з усіма параметрами.
     *
     * @param name назва
     * @param brand бренд
     * @param size розмір
     * @param color колір
     * @param material матеріал
     * @param releaseYear рік випуску
     * @param price ціна, грн
     * @param sleeve довжина рукава
     * @param collar розмір коміра, см
     * @param cufflinks чи є манжети під запонки
     * @param nonIron чи тканина не потребує прасування
     * @throws InvalidClothesException якщо якесь значення некоректне
     */
    public FormalShirt(String name, String brand, Size size, String color, Material material,
                       int releaseYear, double price, Sleeve sleeve, int collar,
                       boolean cufflinks, boolean nonIron) {
        super(name, brand, size, color, material, releaseYear, price, sleeve, collar);
        setCufflinks(cufflinks);
        this.nonIron = nonIron;
    }

    /**
     * Створює класичну сорочку на основі вже заданих полів сорочки.
     *
     * @param base сорочка, з якої беруться поля
     * @param cufflinks чи є манжети під запонки
     * @param nonIron чи тканина не потребує прасування
     * @throws InvalidClothesException якщо base дорівнює null або манжети під запонки
     *                                 задано для короткого рукава
     */
    public FormalShirt(Shirts base, boolean cufflinks, boolean nonIron) {
        super(base);
        setCufflinks(cufflinks);
        this.nonIron = nonIron;
    }

    /**
     * Конструктор копіювання.
     *
     * @param other сорочка, яка копіюється
     * @throws InvalidClothesException якщо other дорівнює null
     */
    public FormalShirt(FormalShirt other) {
        super(other);
        this.cufflinks = other.cufflinks;
        this.nonIron = other.nonIron;
    }

    /**
     * Чи має сорочка манжети під запонки.
     *
     * @return true, якщо манжети під запонки
     */
    public boolean hasCufflinks() {
        return cufflinks;
    }

    /**
     * Задає, чи має сорочка манжети під запонки.
     * Запонки можливі лише на довгому рукаві.
     *
     * @param cufflinks true, якщо манжети під запонки
     * @throws InvalidClothesException якщо запонки задано для короткого рукава
     */
    public void setCufflinks(boolean cufflinks) {
        if (cufflinks && getSleeve() != Sleeve.LONG) {
            throw new InvalidClothesException("манжети під запонки бувають лише на довгому рукаві");
        }
        this.cufflinks = cufflinks;
    }

    /**
     * Чи тканина не потребує прасування.
     *
     * @return true, якщо прасувати не потрібно
     */
    public boolean isNonIron() {
        return nonIron;
    }

    /**
     * Задає, чи тканина не потребує прасування.
     *
     * @param nonIron true, якщо прасувати не потрібно
     */
    public void setNonIron(boolean nonIron) {
        this.nonIron = nonIron;
    }

    /**
     * Задає довжину рукава. Якщо рукав короткий, запонки прибираються.
     *
     * @param sleeve довжина рукава, не null
     * @throws InvalidClothesException якщо значення не задано
     */
    @Override
    public void setSleeve(Sleeve sleeve) {
        super.setSleeve(sleeve);
        if (sleeve != Sleeve.LONG) {
            cufflinks = false;
        }
    }

    /**
     * Повертає тип речі.
     *
     * @return "Класична сорочка"
     */
    @Override
    public String getType() {
        return "Класична сорочка";
    }

    /**
     * Повертає пораду з догляду за класичною сорочкою:
     * до поради для матеріалу додається порада щодо прасування.
     *
     * @return порада з догляду
     */
    @Override
    public String getCareInstructions() {
        String ironing = nonIron ? "прасувати не потрібно, сушити на плічках" : "прасувати комір і манжети в першу чергу";
        return super.getCareInstructions() + "; " + ironing;
    }

    /**
     * Повертає опис сорочки одним рядком.
     *
     * @return рядок з усіма полями
     */
    @Override
    public String toString() {
        return "FormalShirt{" + baseInfo() + ", рукав=" + getSleeve().getTitle() + ", комір=" + getCollar()
                + " см, запонки=" + (cufflinks ? "так" : "ні") + ", без прасування=" + (nonIron ? "так" : "ні") + uuidInfo() + "}";
    }

    /**
     * Порівнює сорочки за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return true, якщо всі поля однакові
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        FormalShirt other = (FormalShirt) o;
        return cufflinks == other.cufflinks && nonIron == other.nonIron;
    }

    /**
     * Хеш-код, узгоджений з equals.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cufflinks, nonIron);
    }
}
