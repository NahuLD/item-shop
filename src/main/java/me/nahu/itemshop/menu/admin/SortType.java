package me.nahu.itemshop.menu.admin;

import me.nahu.itemshop.shop.SellableItem;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public enum SortType {
    NORMAL(
        true,
        "Normal",
        (first, second) -> first.getId() - second.getId()
    ),
    ALPHABETICAL(
        "A-Z",
        (first, second) -> first.getName().orElse("").compareTo(second.getName().orElse(""))
    ),
    REVERSED_ALPHABETICAL(
        "Z-A",
        (first, second) -> second.getName().orElse("").compareTo(first.getName().orElse(""))
    ),
    LOW_PRICE(
        "Price Low-High",
        (first, second) -> first.getPrice() - second.getPrice()
    ),
    HIGH_PRICE(
        "Price High-Low",
        (first, second) -> second.getPrice() - first.getPrice()
    );

    private final boolean hidden;
    private final String name;
    private final Comparator<SellableItem> comparator;

    SortType(@NotNull String name, @NotNull Comparator<SellableItem> comparator) {
        this(false, name, comparator);
    }

    SortType(boolean hidden, @NotNull String name, @NotNull Comparator<SellableItem> comparator) {
        this.hidden = hidden;
        this.name = name;
        this.comparator = comparator;
    }

    public boolean isHidden() {
        return !hidden;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Comparator<SellableItem> getComparator() {
        return comparator;
    }

    @NotNull
    public static SortType next(SortType current) {
        try {
            return values()[current.ordinal() + 1];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return ALPHABETICAL;
        }
    }
}
