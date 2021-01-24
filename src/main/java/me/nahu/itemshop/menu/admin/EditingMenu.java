package me.nahu.itemshop.menu.admin;

import de.themoep.inventorygui.InventoryGui;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.Menu;
import me.nahu.itemshop.shop.SellableItem;
import me.nahu.itemshop.utils.SignMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static me.nahu.itemshop.menu.admin.AdminConfigurationMenu.EXIT;
import static me.nahu.itemshop.utils.Utilities.color;

public class EditingMenu extends Menu {
    private static final ItemStack DATA = new ItemStack(Material.BEACON);
    private static final ItemStack PRICE = new ItemStack(Material.DOUBLE_PLANT);
    private static final ItemStack NAME = new ItemStack(Material.SIGN);

    private final SellableItem sellableItem;

    public EditingMenu(@NotNull ItemShopPlugin plugin, @NotNull SellableItem sellableItem) {
        super(plugin);
        this.sellableItem = sellableItem;
    }

    @Override
    protected @NotNull InventoryGui newInventory(@NotNull Player player) {
        InventoryGui inventoryGui = new InventoryGui(
            getPlugin(),
            "Editing Menu",
            new String[]{ "i md pn c" }
        );
        inventoryGui.addElement(
            staticElement(
                'i',
                sellableItem.toItemStack(),
                color(
                    "&aSellable Item",
                    "&8&m-------------------------",
                    "&7Id: &d" + sellableItem.getId(),
                    "&7Name: &f" + sellableItem.getName().orElse("&cN/A"),
                    "&7Price: &a$" + sellableItem.getPrice(),
                    "&8&m-------------------------"
                ),
                click -> true
            )
        );
        inventoryGui.addElement(
            staticElement(
                'c',
                EXIT,
                color("&cExit"),
                click -> {
                    inventoryGui.close(false);
                    return true;
                }
            )
        );
        inventoryGui.addElement(
            staticElement(
                'm',
                new ItemStack(sellableItem.getMaterial()),
                color("&eMaterial", "&7Name: &f" + sellableItem.getMaterial().toString()),
                click -> {
                    inventoryGui.close(false);
                    inputMenu(
                        this,
                        player,
                        sellableItem.getMaterial().toString(),
                        input -> {
                            Material newMaterial = Material.matchMaterial(input[0]);
                            if (newMaterial == null) {
                                return true;
                            }
                            sellableItem.setMaterial(newMaterial);
                            return false;
                        }
                    );
                    return true;
                }
            )
        );
        inventoryGui.addElement(
            staticElement(
                'd',
                DATA,
                color("&eData", "&7Id: &f" + sellableItem.getData()),
                click -> {
                    inventoryGui.close(false);
                    inputMenu(
                        this,
                        player,
                        String.valueOf(sellableItem.getData()),
                        input -> {
                            try {
                                sellableItem.setData(Short.parseShort(input[0]));
                                return false;
                            } catch (NumberFormatException exception) {
                                return true;
                            }
                        }
                    );
                    return true;
                }
            )
        );
        inventoryGui.addElement(
            staticElement(
                'p',
                PRICE,
                color("&ePrice", "&7Price: &a$" + sellableItem.getPrice()),
                click -> {
                    inventoryGui.close(false);
                    editPriceMenu(this, player, sellableItem);
                    return true;
                }
            )
        );
        inventoryGui.addElement(
            staticElement(
                'n',
                NAME,
                color("&eName", "&7Name: &f" + sellableItem.getName().orElse("&cN/A")),
                click -> {
                    inventoryGui.close(false);
                    inputMenu(
                        this,
                        player,
                        sellableItem.getName().orElse("N/A"),
                        input -> {
                            String[] newName = color(input[0]);
                            sellableItem.setName((newName.length <= 0) ? null : newName[0]);
                            return false;
                        }
                    );
                    return true;
                }
            )
        );
        return inventoryGui;
    }

    public static void editPriceMenu(
        @NotNull Menu menu,
        @NotNull Player player,
        @NotNull SellableItem sellableItem
    ) {
        inputMenu(menu, player, "$", response -> {
            try {
                int newPrice = Integer.parseInt(response[0]);
                sellableItem.setPrice(newPrice);
                return false;
            } catch (NumberFormatException exception) {
                return true;
            }
        });
    }

    private static void inputMenu(
        @NotNull Menu menu,
        @NotNull Player player,
        @NotNull String text,
        @NotNull Function<String[], Boolean> response
    ) {
        new SignMenu(menu.getPlugin())
            .listener((clicker, input) -> {
                if (!response.apply(input)) {
                    menu.open(player);
                    return;
                }
                inputMenu(menu, player, text, response); // retry
            })
            .show(player);
    }
}
