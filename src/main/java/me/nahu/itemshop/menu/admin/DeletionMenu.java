package me.nahu.itemshop.menu.admin;

import de.themoep.inventorygui.InventoryGui;
import me.nahu.itemshop.ItemShopManager;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.Menu;
import me.nahu.itemshop.shop.SellableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.nahu.itemshop.utils.Utilities.color;

public class DeletionMenu extends Menu {
    private static final ItemStack ACCEPT = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
    private static final ItemStack DENY = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

    private final ItemShopManager itemShopManager;
    private final SellableItem sellableItem;

    public DeletionMenu(@NotNull ItemShopPlugin plugin, @NotNull SellableItem sellableItem) {
        super(plugin);
        this.itemShopManager = plugin.getItemShopManager();
        this.sellableItem = sellableItem;
    }

    @Override
    protected @NotNull InventoryGui newInventory(@NotNull Player player) {
        InventoryGui inventoryGui = new InventoryGui(
            getPlugin(),
            "Are you sure?",
            new String[] { " y  i  n " }
        );
        inventoryGui.addElement(
            staticElement(
                'i',
                sellableItem.toItemStack(),
                color("&aAre you sure you want to delete it?", "&8Id: &e" + sellableItem.getId()),
                click -> true
            )
        );
        inventoryGui.addElement(
            staticElement(
                'y',
                ACCEPT,
                color("&aAccept"),
                click -> {
                    itemShopManager.removeItem(sellableItem.getId());
                    inventoryGui.close();
                    return true;
                }
            )
        );
        inventoryGui.addElement(
            staticElement(
                'n',
                DENY,
                color("&cCancel"),
                click -> {
                    inventoryGui.close();
                    return true;
                }
            )
        );
        return inventoryGui;
    }
}
