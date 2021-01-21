package me.nahu.itemshop.menu.admin;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import me.nahu.itemshop.ItemShopManager;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.Menu;
import me.nahu.itemshop.shop.SellableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.nahu.itemshop.utils.Utilities.color;

public class AdminMenu extends Menu {
    private static final ItemStack SORTING = new ItemStack(Material.PAPER);
    private static final ItemStack ARROW = new ItemStack(Material.ARROW);

    private final ItemShopPlugin itemShopPlugin;
    private final ItemShopManager itemShopManager;

    private SortType sortType = SortType.NORMAL;

    public AdminMenu(@NotNull ItemShopPlugin plugin) {
        super(plugin);
        this.itemShopPlugin = plugin;
        this.itemShopManager = plugin.getItemShopManager();
    }

    @Override
    protected @NotNull InventoryGui newInventory(@NotNull Player player) {
        List<SellableItem> items = itemShopManager.getItems().stream()
            .sorted(sortType.getComparator())
            .collect(Collectors.toList());

        InventoryGui inventoryGui = new InventoryGui(
            getPlugin(),
            "Item Shop Configuration",
            new String[]{ "         ", "         ", "         ", "         ", "< az lh >" }
        );

        // player inventory handler
        inventoryGui.setPlayerInventoryAction(click -> {
            ItemStack itemStack = click.getEvent().getCurrentItem();
            if (itemShopManager.getItemByItemStack(itemStack).isPresent()) // replicas are BAD
                return true;

            inventoryGui.close();
            EditingMenu.editPriceMenu(this, player, itemShopManager.createSellableItem(itemStack, 0));
            return true;
        });

        // arrows
        inventoryGui.addElement(forwardArrow());
        inventoryGui.addElement(backwardArrow());

        // sorting items
        Arrays.stream(SortType.values()).map(type -> staticElement(
            type.getSlot(),
            SORTING,
            color("&aSort by " + type.getName()),
            click -> {
                sortType = type;
                reopen(player);
                return true;
            })
        ).forEach(inventoryGui::addElement);

        // all items
        GuiElementGroup elementGroup = new GuiElementGroup(' ');
        items.stream().map(sellableItem ->
            staticElement(
                ' ',
                sellableItem.toItemStack(),
                color(
                    formatSellableItem(sellableItem),
                    "&8Right Click to &cDelete",
                    "&8Left Click to &bEdit"
                ),
                click -> {
                    switch (click.getType()) {
                        case RIGHT:
                            new DeletionMenu(itemShopPlugin, sellableItem).open(player);
                            break;
                        case LEFT:
                            new EditingMenu(itemShopPlugin, sellableItem).open(player);
                            break;
                    }
                    return true;
                }
            )
        ).forEach(elementGroup::addElement);
        inventoryGui.addElement(elementGroup);
        return inventoryGui;
    }

    private GuiElement backwardArrow() {
        return staticElement(
            '<', ARROW, color("&eGo to previous page"), click -> {
                int pageNumber = click.getGui().getPageNumber(click.getWhoClicked());
                if (pageNumber <= 0)
                    return true;
                click.getGui().playClickSound();
                click.getGui().setPageNumber(pageNumber - 1);
                return true;
            }
        );
    }

    private GuiElement forwardArrow() {
        return staticElement(
            '<', ARROW, color("&eGo to previous page"), click -> {
                int pageNumber = click.getGui().getPageNumber(click.getWhoClicked());
                if (pageNumber + 1 >= click.getGui().getPageAmount(click.getWhoClicked()))
                    return true;
                click.getGui().playClickSound();
                click.getGui().setPageNumber(pageNumber + 1);
                return true;
            }
        );
    }
}
