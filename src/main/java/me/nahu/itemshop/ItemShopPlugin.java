package me.nahu.itemshop;

import co.aikar.commands.BukkitCommandManager;
import me.nahu.itemshop.command.ItemShopCommand;
import me.nahu.itemshop.shop.SellableItem;
import me.nahu.itemshop.shop.ShopUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ItemShopPlugin extends JavaPlugin {
    static {
        ConfigurationSerialization.registerClass(SellableItem.class, "SellableItem");
    }

    private ItemShopManager itemShopManager;

    private BukkitCommandManager commandManager;
    private Economy economy;

    private double hagglePercentageRange;

    @Override
    public void onEnable() {
        saveResource("items.yml", false);
        saveResource("config.yml", false);

        hagglePercentageRange = getConfig().getDouble("haggle-percentage-range");

        int haggleAttemptCooldown = getConfig().getInt("haggle-attempt-cooldown");
        ShopUser.setHaggleAttemptCooldown(TimeUnit.SECONDS.toMillis(haggleAttemptCooldown));

        economy = loadEconomy();

        File itemsFile = new File(getDataFolder(), "items.yml");
        itemShopManager = new ItemShopManager(itemsFile);

        commandManager = new BukkitCommandManager(this);
        commandManager.registerCommand(
            new ItemShopCommand(this)
        );
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        commandManager.unregisterCommands();
        try {
            itemShopManager.saveItems();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @NotNull
    public ItemShopManager getItemShopManager() {
        return itemShopManager;
    }

    @NotNull
    public Economy getEconomy() {
        return economy;
    }

    public double getHagglePercentageRange() {
        return hagglePercentageRange;
    }

    private Economy loadEconomy() {
        return getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }
}
