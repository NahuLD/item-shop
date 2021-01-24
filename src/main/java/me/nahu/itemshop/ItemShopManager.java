package me.nahu.itemshop;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import me.nahu.itemshop.shop.SellableItem;
import me.nahu.itemshop.shop.ShopUser;
import me.nahu.itemshop.utils.Utilities;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ItemShopManager {
    private static final Logger LOGGER = Logger.getLogger(ItemShopManager.class.getName());

    private final Map<Integer, SellableItem> sellableItems;
    @SuppressWarnings("UnstableApiUsage")
    private final LoadingCache<UUID, ShopUser> shopUsers = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build(new CacheLoader<UUID, ShopUser>() {
            @Override
            public ShopUser load(@NotNull UUID key){
                return new ShopUser(key);
            }
        });

    private final YamlConfiguration configuration;
    private final File configurationFile;

    public ItemShopManager(@NotNull File configurationFile) {
        this.configurationFile = configurationFile;
        this.configuration = YamlConfiguration.loadConfiguration(configurationFile);

        this.sellableItems = loadItems();
        LOGGER.info("Loaded '" + sellableItems.size() + "' sellable items.");
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    public ShopUser getShopUser(@NotNull Player player) {
        return shopUsers.getUnchecked(player.getUniqueId());
    }

    /**
     * Match a sellable item by comparing it to a Bukkit item stack.
     * @param itemStack Item stack to compare.
     * @return Optional containing the sellable item, empty if none are found.
     */
    @NotNull
    public Optional<SellableItem> getItemByItemStack(@NotNull ItemStack itemStack) {
        return sellableItems.values().stream()
            .filter(sellableItem -> sellableItem.matchesItem(itemStack))
            .findFirst();
    }

    /**
     * Get the combined price of a stack of items, this takes the amount of items into account.
     * @param itemStack Item to check.
     * @return Combined price.
     */
    public int getCombinedPrice(@NotNull ItemStack itemStack) {
        return getItemByItemStack(itemStack)
            .map(item -> item.getPrice() * itemStack.getAmount())
            .orElse(0);
    }

    /**
     * Check if the collection of items passed has any invalid items.
     * @param items Items to be checked.
     * @return True if there are, false if none.
     */
    public boolean hasInvalidItems(@NotNull List<ItemStack> items) {
        return items.parallelStream()
            .map(this::getItemByItemStack)
            .anyMatch(optional -> !optional.isPresent());
    }

    /**
     * Get an immutable set with all the sellable items.
     * @return Immutable set of sellable items.
     */
    @NotNull
    public ImmutableSet<SellableItem> getItems() {
        return ImmutableSet.copyOf(sellableItems.values());
    }

    /**
     * Load all items from the configuration file.
      * @return Map with the parent's format.
     */
    @NotNull
    public Map<Integer, SellableItem> loadItems() {
        return configuration.getKeys(false).stream()
            .map(this::loadItem)
            .collect(
                Collectors.toMap(SellableItem::getId, Function.identity())
            );
    }

    /**
     * Load a specific item by it's id, keep in mind that this method does only returns the object and does not
     * load it into the map.
     * @param id Id of the item.
     * @return Sellable item deserialized object.
     */
    @NotNull
    private SellableItem loadItem(@NotNull String id) {
        return (SellableItem) configuration.get(id);
    }

    /**
     * Save all items from the map.
     * @throws IOException Exception while writing into the file.
     */
    public void saveItems() throws IOException {
        getItems().forEach(this::saveItem);
        configuration.save(configurationFile);
    }

    /**
     * Save a specific item into the in-memory configuration instance.
     * @param item Item to save.
     */
    public void saveItem(@NotNull SellableItem item) {
        saveItem(item, false);
    }

    /**
     * Save a specific item, pass true to save write it onto the file.
     * @param item Item to save.
     * @param save Whether to write it to file.
     */
    public void saveItem(@NotNull SellableItem item, boolean save) {
        saveItem(item.getId(), item, false);
    }

    /**
     * Save an item with a specified id, this can be used to remove an item from config too by passing null to
     * the item instance.
     * @param id Id of the item to save.
     * @param item Item to save, can be null to erase.
     * @param save Whether to write into the config file.
     */
    public void saveItem(int id, @Nullable SellableItem item, boolean save) {
        configuration.set(String.valueOf(id), item);
        if (!save) return;

        Utilities.runTaskAsynchronously(() -> {
            try {
                configuration.save(configurationFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Remove an item from existence! You only need its id.
     * @param id Id of the item to erase.
     * @return True if it could be erased, false otherwise.
     */
    public boolean removeItem(int id) {
        if (!sellableItems.containsKey(id)) {
            return false;
        }
        saveItem(id, null, true);
        return sellableItems.remove(id) != null;
    }

    /**
     * Create a new item, this method will store in-memory and write to file.
     * @param item Item to get information from.
     * @param price Price set for the item-
     * @return New sellable item instance.
     */
    @NotNull
    public SellableItem createSellableItem(@NotNull ItemStack item, int price) {
        int id = getItems().stream()
                .mapToInt(SellableItem::getId)
                .max().orElse(0) + 1;
        SellableItem newItem = new SellableItem(
            id,
            item.getItemMeta().getDisplayName(),
            item.getType(),
            item.getDurability(),
            price
        );
        sellableItems.put(newItem.getId(), newItem);
        saveItem(newItem, true);
        return newItem;
    }
}
