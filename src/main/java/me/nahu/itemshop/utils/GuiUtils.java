package me.nahu.itemshop.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Utilities regarding GUIs, mainly for use with {@link de.themoep.inventorygui.InventoryGui InventoryGui}.
 *
 * @since 0.2.0
 */
public class GuiUtils {
    /**
     * All the characters rows may contain.
     * <p>
     * There are in total 54 characters, fitting a chunk of 9 characters at a time.
     */
    public static final String GUI_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqr";
    /**
     * The default value for how many rows should be allowed at maximum for non-paging interfaces.
     * <p>
     * This is only available for non-paging calculations.
     *
     * @since 0.3.5
     */
    public static final int DEFAULT_NONPAGING_INVENTORY_ROWS = 6;
    /**
     * The default value for how many rows should be allowed at maximum for paging interfaces.
     * <p>
     * This is only available for paging calculations.
     *
     * @since 0.3.5
     */
    public static final int DEFAULT_PAGING_INVENTORY_ROWS = 6;
    /**
     * The default value for whether the rows should grow per amount of items.
     *
     * @since 0.3.5
     */
    public static final boolean DEFAULT_GROWING = true;
    /**
     * The default value for whether we should attempt to fit any overflowing items in the 6th row instead of using a
     * paging bar.
     * <p>
     * This is only available for paging calculations.
     *
     * @since 0.3.5
     */
    public static final boolean DEFAULT_ATTEMPT_FIT = true;
    /**
     * The default value for whether {@link #GUI_CHARACTERS} should be ignored and we should only use spaces instead.
     *
     * @since 0.3.5
     */
    public static final boolean DEFAULT_EMPTY_CHARACTERS = false;
    /**
     * The default value for what how the page bar should look like.
     * <p>
     * This is designed for the use of {@code "<"} as the previous page button, {@code "~"} as the current page
     * information item, and {@code ">"} as the next page button.
     * <p>
     * This is only available for paging calculations.
     *
     * @since 0.3.5
     */
    public static final String DEFAULT_PAGE_BAR = "<   ~   >";

    private GuiUtils() throws IllegalAccessException {
        throw new IllegalAccessException(getClass().getSimpleName() + " cannot be instantiated.");
    }

    /**
     * Calculates how many rows a specific amount of items may need.
     *
     * @param items How many items are to be stored in this inventory.
     * @return The calculated rows.
     * @see #calculateRows(int, int, boolean, boolean)
     */
    public static String[] calculateRows(int items) {
        return calculateRows(items,
                DEFAULT_NONPAGING_INVENTORY_ROWS, DEFAULT_GROWING, DEFAULT_EMPTY_CHARACTERS);
    }

    /**
     * Calculates how many rows a specific amount of items may need.
     *
     * @param items         How many items are to be stored in this inventory.
     * @param inventoryRows The max amount of rows allowed. This is capped to 6 at most and 1 at least.
     * @return The calculated rows.
     * @see #calculateRows(int, int, boolean, boolean)
     */
    public static String[] calculateRows(int items, int inventoryRows) {
        return calculateRows(items, inventoryRows, DEFAULT_GROWING, DEFAULT_EMPTY_CHARACTERS);
    }

    /**
     * Calculates how many rows a specific amount of items may need.
     *
     * @param items         How many items are to be stored in this inventory.
     * @param inventoryRows The max amount of rows allowed. This is capped to 6 at most and 1 at least.
     * @param growing       Whether the rows should grow per the amount of items.
     * @return The calculated rows.
     * @see #calculateRows(int, int, boolean, boolean)
     * @since 0.3.5
     */
    public static String[] calculateRows(int items, int inventoryRows, boolean growing) {
        return calculateRows(items, inventoryRows, growing, DEFAULT_EMPTY_CHARACTERS);
    }

    /**
     * Calculates how many rows a specific amount of items may need.
     *
     * @param items           How many items are to be stored in this inventory.
     * @param inventoryRows   The max amount of rows allowed. This is capped to 6 at most and 1 at least.
     * @param growing         Whether the rows should grow per the amount of items.
     * @param emptyCharacters Skip the use of {@link #GUI_CHARACTERS} and use spaces instead.
     * @return The calculated rows.
     */
    public static String[] calculateRows(
            int items,
            int inventoryRows,
            boolean growing,
            boolean emptyCharacters
    ) {
        int maximumPageRows = Math.max(1, Math.min(inventoryRows, 6));
        int requiredRows;
        if (!growing) {
            requiredRows = maximumPageRows;
        } else {
            requiredRows = items > maximumPageRows * 9
                    ? maximumPageRows
                    : (int) Math.ceil((double) items / 9);
        }

        String[] rows = new String[requiredRows + (items > maximumPageRows * 9 ? 1 : 0)];
        int index = 0;
        for (int i = 0; i < requiredRows; ++i) { // this was shifting the characters ABC BCD CDE
            rows[i] = emptyCharacters ?
                    new String(new char[9]).replace("\0", " ") :
                    GUI_CHARACTERS.substring(index, index += 9);
        }

        if (items < requiredRows * 9) {
            int last = Math.abs(items - (requiredRows * 9));
            rows[rows.length - 1] = rows[rows.length - 1].substring(0, 9 - last) +
                    new String(new char[last]).replace("\0", " ");
        }

        return rows;
    }

    /**
     * Calculates how many rows a specific amount of items may need with regard to a paging bar at the bottom.
     *
     * @param items How many items are to be stored in this inventory.
     * @return The calculated rows.
     * @see #calculatePagingRows(int, int, boolean, boolean, boolean, String)
     */
    public static String[] calculatePagingRows(int items) {
        return calculatePagingRows(items, DEFAULT_PAGING_INVENTORY_ROWS);
    }

    /**
     * Calculates how many rows a specific amount of items may need with regard to a paging bar at the bottom.
     *
     * @param items    How many items are to be stored in this inventory.
     * @param pageRows The max amount of rows allowed in a single page. This is capped to 5 at most and 1 at least.
     * @return The calculated rows.
     * @see #calculatePagingRows(int, int, boolean, boolean, boolean, String)
     */
    public static String[] calculatePagingRows(int items, int pageRows) {
        return calculatePagingRows(items, pageRows, DEFAULT_GROWING);
    }

    /**
     * Calculates how many rows a specific amount of items may need with regard to a paging bar at the bottom.
     *
     * @param items    How many items are to be stored in this inventory.
     * @param pageRows The max amount of rows allowed in a single page. This is capped to 5 at most and 1 at least.
     * @param growing  Whether the rows should grow per the amount of items.
     * @return The calculated rows.
     * @see #calculatePagingRows(int, int, boolean, boolean, boolean, String)
     */
    public static String[] calculatePagingRows(int items, int pageRows, boolean growing) {
        return calculatePagingRows(items, pageRows, growing, DEFAULT_ATTEMPT_FIT);
    }

    /**
     * Calculates how many rows a specific amount of items may need with regard to a paging bar at the bottom.
     *
     * @param items    How many items are to be stored in this inventory.
     * @param pageRows The max amount of rows allowed in a single page. This is capped to 5 at most and 1 at least.
     * @param growing  Whether the rows should grow per the amount of items.
     * @param pageBar  The format of the paging bar.
     * @return The calculated rows.
     * @see #calculatePagingRows(int, int, boolean, boolean, boolean, String)
     * @since 0.3.5
     */
    public static String[] calculatePagingRows(
            int items,
            int pageRows,
            boolean growing,
            @NotNull String pageBar
    ) {
        return calculatePagingRows(
                items,
                pageRows,
                growing,
                DEFAULT_ATTEMPT_FIT,
                DEFAULT_EMPTY_CHARACTERS,
                pageBar
        );
    }

    /**
     * Calculates how many rows a specific amount of items may need with regard to a paging bar at the bottom.
     *
     * @param items      How many items are to be stored in this inventory.
     * @param pageRows   The max amount of rows allowed in a single page. This is capped to 5 at most and 1 at least.
     * @param growing    Whether the rows should grow per the amount of items.
     * @param attemptFit Whether we should attempt to fit any overflowing items in the 6th row instead of using a paging
     *                   bar.
     * @return The calculated rows.
     * @see #calculatePagingRows(int, int, boolean, boolean, boolean, String)
     */
    public static String[] calculatePagingRows(
            int items,
            int pageRows,
            boolean growing,
            boolean attemptFit
    ) {
        return calculatePagingRows(
                items,
                pageRows,
                growing,
                attemptFit,
                DEFAULT_EMPTY_CHARACTERS,
                DEFAULT_PAGE_BAR
        );
    }

    /**
     * Calculates how many rows a specific amount of items may need with regard to a paging bar at the bottom.
     *
     * @param items           How many items are to be stored in this inventory.
     * @param pageRows        The max amount of rows allowed in a single page. This is capped to 5 at most and 1 at
     *                        least.
     * @param growing         Whether the rows should grow per the amount of items.
     * @param attemptFit      Whether we should attempt to fit any overflowing items in the 6th row instead of using a
     *                        paging bar.
     * @param emptyCharacters Skip the use of {@link #GUI_CHARACTERS} and use spaces instead.
     * @return The calculated rows.
     * @see #calculatePagingRows(int, int, boolean, boolean, boolean, String)
     * @since 0.3.5
     */
    public static String[] calculatePagingRows(
            int items,
            int pageRows,
            boolean growing,
            boolean attemptFit,
            boolean emptyCharacters
    ) {
        return calculatePagingRows(
                items,
                pageRows,
                growing,
                attemptFit,
                emptyCharacters,
                DEFAULT_PAGE_BAR
        );
    }

    /**
     * Calculates how many rows a specific amount of items may need with regard to a paging bar at the bottom.
     *
     * @param items           How many items are to be stored in this inventory.
     * @param pageRows        The max amount of rows allowed in a single page. This is capped to 5 at most and 1 at
     *                        least.
     * @param growing         Whether the rows should grow per the amount of items.
     * @param attemptFit      Whether we should attempt to fit any overflowing items in the 6th row instead of using a
     *                        pagingbar.
     * @param emptyCharacters Skip the use of {@link #GUI_CHARACTERS} and use spaces instead.
     * @param pageBar         The format for the paging bar.
     * @return The calculated rows.
     */
    public static String[] calculatePagingRows(
            int items,
            int pageRows,
            boolean growing,
            boolean attemptFit,
            boolean emptyCharacters,
            String pageBar
    ) {
        int requiredRows = Math.max(1, Math.min(pageRows, 5));

        boolean pagingBar = items > requiredRows * 9;
        if (attemptFit && items <= 6 * 9) {
            pagingBar = false;
        }

        int calculatingRows = items <= requiredRows * 9
                ? requiredRows
                : (pagingBar
                ? requiredRows + 1
                : (int) Math.ceil((double) items / 9));

        String[] rows = calculateRows(items,
                pagingBar ? requiredRows : calculatingRows,
                growing,
                emptyCharacters);

        if (pagingBar) {
            rows[rows.length - 1] = pageBar;
        }

        return (rows.length <= 0) ? new String[] { "         " } : rows;
    }
}