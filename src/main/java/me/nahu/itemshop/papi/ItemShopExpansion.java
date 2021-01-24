package me.nahu.itemshop.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nahu.itemshop.ItemShopManager;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.shop.ShopUser;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static me.nahu.itemshop.menu.user.ItemShopMenu.DECIMAL_FORMAT;

public class ItemShopExpansion extends PlaceholderExpansion {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ItemShopPlugin itemShopPlugin;
    private final ItemShopManager itemShopManager;

    public ItemShopExpansion(@NotNull ItemShopPlugin itemShopPlugin) {
        this.itemShopPlugin = itemShopPlugin;
        this.itemShopManager = itemShopPlugin.getItemShopManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return itemShopPlugin.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return Arrays.toString(itemShopPlugin.getDescription().getAuthors().toArray());
    }

    @Override
    public @NotNull String getVersion() {
        return itemShopPlugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "N/A";
        }

        ShopUser shopUser = itemShopManager.getShopUser(player.getUniqueId());
        String response = "N/A";
        switch (params) {
            case "can_haggle":
                response = String.valueOf(shopUser.canHaggle());
                break;
            case "haggle_attempt_formatted":
                response = DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(shopUser.getNextHaggleAttempt()));
                break;
            case "haggle_modifier":
                response = "x" + DECIMAL_FORMAT.format(shopUser.getHaggleModifier());
                break;
        }
        return response;
    }
}
