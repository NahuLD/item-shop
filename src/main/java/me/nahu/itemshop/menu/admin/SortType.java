package me.nahu.itemshop.menu.admin;

import me.nahu.itemshop.shop.SellableItem;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public enum SortType {
    NORMAL(
        'n',
        "Normal",
        (first, second) -> first.getId() - second.getId()
    ),
    ALPHABETICAL(
        'a',
        "Alphabetical",
        (first, second) -> first.getName().orElse("").compareTo(second.getName().orElse(""))
    ),
    REVERSED_ALPHABETICAL(
        'z',
        "Reversed Alphabetical",
        (first, second) -> second.getName().orElse("").compareTo(first.getName().orElse(""))
    ),
    LOW_PRICE(
        'l',
        "Low Price",
        (first, second) -> first.getPrice() - second.getPrice()
    ),
    HIGH_PRICE(
        'h',
        "High Price",
        (first, second) -> second.getPrice() - first.getPrice()
    );

    private final char slot;
    private final String name;
    private final Comparator<SellableItem> comparator;

    SortType(char slot, @NotNull String name, @NotNull Comparator<SellableItem> comparator) {
        this.slot = slot;
        this.name = name;
        this.comparator = comparator;
    }

    public char getSlot() {
        return slot;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Comparator<SellableItem> getComparator() {
        return comparator;
    }
}
