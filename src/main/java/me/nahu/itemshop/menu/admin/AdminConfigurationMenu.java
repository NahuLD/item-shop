package me.nahu.itemshop.menu.admin;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import me.nahu.itemshop.ItemShopManager;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.Menu;
import me.nahu.itemshop.shop.SellableItem;
import me.nahu.itemshop.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.nahu.itemshop.utils.Utilities.color;

public class AdminConfigurationMenu extends Menu {
    public static final ItemStack EXIT = new ItemStack(Material.WHEAT);
    public static final ItemStack FILLER = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
    private static final ItemStack SORTING = new ItemStack(Material.EYE_OF_ENDER);

    private static final StaticGuiElement FORWARD_ARROW_ELEMENT = new StaticGuiElement(
        '>',
        new ItemStack(Material.MAGMA_CREAM),
        click -> {
            int pageNumber = click.getGui().getPageNumber(click.getWhoClicked());
            if (pageNumber + 1 >= click.getGui().getPageAmount(click.getWhoClicked()))
                return true;
            click.getGui().playClickSound();
            click.getGui().setPageNumber(pageNumber + 1);
            return true;
        },
        color("&aNext Page")
    );
    private static final StaticGuiElement PREVIOUS_ARROW_ELEMENT = new StaticGuiElement(
        '<',
        new ItemStack(Material.SPECKLED_MELON),
        click -> {
            int pageNumber = click.getGui().getPageNumber(click.getWhoClicked());
            if (pageNumber <= 0)
                return true;
            click.getGui().playClickSound();
            click.getGui().setPageNumber(pageNumber - 1);
            return true;
        },
        color("&aLast Page")
    );

    private final ItemShopPlugin itemShopPlugin;
    private final ItemShopManager itemShopManager;

    private SortType sortType = SortType.NORMAL;

    public AdminConfigurationMenu(@NotNull ItemShopPlugin plugin) {
        super(plugin);
        this.itemShopPlugin = plugin;
        this.itemShopManager = plugin.getItemShopManager();
    }

    @Override
    protected @NotNull InventoryGui newInventory(@NotNull Player player) {
        InventoryGui inventoryGui = new InventoryGui(
            getPlugin(),
            color("&cItem Sell Config")[0],
            new String[]{
                "       p>",
                "       p<",
                "       p-",
                "       p-",
                "       ps",
                "       pe"
            }
        );

        // player inventory handler
        inventoryGui.setPlayerInventoryAction(click -> {
            ItemStack itemStack = click.getEvent().getCurrentItem();
            if (itemShopManager.getItemByItemStack(itemStack).isPresent()) return true;

            inventoryGui.close();
            EditingMenu.editPriceMenu(this, player, itemShopManager.createSellableItem(itemStack, 0));
            return true;
        });

        // sorting item
        inventoryGui.addElement(
            new DynamicGuiElement('s', view -> staticElement(
                's',
                SORTING,
                color(Utilities.merge(
                    new String[]{ "&eSort Items By:", "&8&m-------------------------" },
                    Arrays.stream(SortType.values())
                        .filter(SortType::isHidden)
                        .map(type -> (this.sortType == type) ?
                            "&6・Sort by " + type.getName() :
                            "&7・Sort by " + type.getName()
                        ).toArray(String[]::new),
                    new String[]{ "&8&m-------------------------" }
                )),
                click -> {
                    this.sortType = SortType.next(this.sortType);
                    inventoryGui.draw();
                    return true;
                }
            ))
        );

        // all items
        inventoryGui.addElement(new DynamicGuiElement(' ', viewer -> {
            List<SellableItem> items = itemShopManager.getItems().stream()
                .sorted(sortType.getComparator())
                .collect(Collectors.toList());

            GuiElementGroup elementGroup = new GuiElementGroup(' ');
            items.stream().map(sellableItem ->
                staticElement(
                    ' ',
                    sellableItem.toItemStack(),
                    color(
                        sellableItem.getName().orElse("&cN/A"),
                        "&7Sell Price: &a$" + sellableItem.getPrice(),
                        "&7",
                        "&eLeft Click &7to &eEdit Price",
                        "&9Shift-Left &7for &9Edit Menu",
                        "&cRight-Click &7to &cDelete Item",
                        "&9Shift-Right &7to &9Quick Delete"
                    ),
                    click -> {
                        switch (click.getType()) {
                            case RIGHT:
                                new ConfirmDeletionMenu(itemShopPlugin, sellableItem).open(player);
                                break;
                            case SHIFT_RIGHT:
                                itemShopManager.removeItem(sellableItem.getId());
                                inventoryGui.draw();
                                break;
                            case LEFT:
                                EditingMenu.editPriceMenu(this, player, sellableItem);
                                break;
                            case SHIFT_LEFT:
                                new EditingMenu(itemShopPlugin, sellableItem).open(player);
                                break;
                        }
                        return true;
                    }
                )
            ).forEach(elementGroup::addElement);
            return elementGroup;
        }));

        // arrows
        inventoryGui.addElement(FORWARD_ARROW_ELEMENT);
        inventoryGui.addElement(PREVIOUS_ARROW_ELEMENT);

        // exit
        inventoryGui.addElement(
            staticElement(
                'e',
                EXIT,
                color(
                    "&cExit",
                    "&8&m-------------------------",
                    "&7",
                    "&cExit the shop",
                    "&7",
                    "&8&m-------------------------"
                ),
                click -> {
                    inventoryGui.close();
                    return true;
                }
            )
        );

        // filler
        inventoryGui.addElement(staticElement('p', FILLER, color("&7", "&7"), click -> true));
        return inventoryGui;
    }
}
