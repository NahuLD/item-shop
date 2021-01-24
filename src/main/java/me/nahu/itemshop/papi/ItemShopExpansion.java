package me.nahu.itemshop.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nahu.itemshop.ItemShopManager;
import me.nahu.itemshop.ItemShopPlugin;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ItemShopExpansion extends PlaceholderExpansion {
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

        String response = "N/A";
        switch (params) {
            // TODO: Go through cases.
        }
        return response;
    }
}
