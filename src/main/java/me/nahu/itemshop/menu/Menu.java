package me.nahu.itemshop.menu;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiStorageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Menu {
    private final JavaPlugin plugin;
    private Inventory virtualInventory;

    public Menu(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player player) {
        InventoryGui inventoryGui = newInventory(player);
        if (inventoryGui.getCloseAction() == null) {
            inventoryGui.setCloseAction(close -> true);
        }
        inventoryGui.show(player);
    }

    @NotNull
    protected abstract InventoryGui newInventory(@NotNull Player player);

    protected GuiElement staticElement(
        char slot,
        @NotNull ItemStack item,
        @NotNull String[] text,
        @NotNull GuiElement.Action click
    ) {
        return new StaticGuiElement(slot, item, click, text);
    }

    protected GuiStorageElement storageElement(
        char slot,
        Runnable action
    ) {
        GuiStorageElement storageElement = new GuiStorageElement(slot, getVirtualInventory());
        storageElement.setApplyStorage(action);
        return storageElement;
    }

    @NotNull
    public Inventory getVirtualInventory() {
        if (virtualInventory == null) {
            virtualInventory = Bukkit.createInventory(null, 54);
        }
        return virtualInventory;
    }

    @NotNull
    public Set<ItemStack> getInventoryContents() {
        return Stream.of(getVirtualInventory().getContents()).collect(Collectors.toSet());
    }

    @NotNull
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
