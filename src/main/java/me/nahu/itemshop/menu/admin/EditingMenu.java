package me.nahu.itemshop.menu.admin;

import de.themoep.inventorygui.InventoryGui;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.Menu;
import me.nahu.itemshop.shop.SellableItem;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static me.nahu.itemshop.utils.Utilities.color;

public class EditingMenu extends Menu {
    private static final ItemStack CLOSE = new ItemStack(Material.BARRIER);
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
                formatSellableItem(sellableItem),
                click -> true
            )
        );

        inventoryGui.addElement(
            staticElement(
                'c',
                CLOSE,
                color("&cClose"),
                click -> {
                    inventoryGui.close();
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
                    inventoryGui.close();
                    inputMenu(
                        this,
                        player,
                        sellableItem.getMaterial().toString(),
                        new ItemStack(sellableItem.getMaterial()),
                        input -> {
                            Material newMaterial = Material.matchMaterial(input);
                            if (newMaterial == null) {
                                return AnvilGUI.Response.text("Invalid material!");
                            }
                            sellableItem.setMaterial(newMaterial);
                            return AnvilGUI.Response.close();
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
                    inventoryGui.close();
                    inputMenu(
                        this,
                        player,
                        String.valueOf(sellableItem.getData()),
                        DATA,
                        input -> {
                            try {
                                sellableItem.setData(Short.parseShort(input));
                                return AnvilGUI.Response.close();
                            } catch (NumberFormatException exception) {
                                return AnvilGUI.Response.text("Invalid number!");
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
                    inventoryGui.close();
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
                    inventoryGui.close();
                    inputMenu(
                        this,
                        player,
                        sellableItem.getName().orElse("N/A"),
                        NAME,
                        input -> {
                            String[] newName = color(input);
                            sellableItem.setName((newName.length <= 0) ? null : newName[0]);
                            return AnvilGUI.Response.close();
                        }
                    );
                    return true;
                }
            )
        );
        return inventoryGui;
    }

    public static void editPriceMenu(@NotNull Menu menu, @NotNull Player player, @NotNull SellableItem sellableItem) {
        inputMenu(
            menu,
            player,
            "$",
            sellableItem.toItemStack(),
            input -> {
                try {
                    int newPrice = Integer.parseInt(input.replace("$", ""));
                    if (newPrice < 1) {
                        return AnvilGUI.Response.text("Must be greater than zero!");
                    }
                    sellableItem.setPrice(newPrice);
                    return AnvilGUI.Response.close();
                } catch (NumberFormatException exception) {
                    return AnvilGUI.Response.text("Not a valid number!");
                }
            }
        );
    }

    private static void inputMenu(
        @NotNull Menu menu,
        @NotNull Player player,
        @NotNull String text,
        @NotNull ItemStack itemStack,
        @NotNull Function<String, AnvilGUI.Response> responseFunction
    ) {
        new AnvilGUI.Builder()
            .onComplete((clicker, input) -> responseFunction.apply(input))
            .onClose(menu::open)
            .preventClose()
            .text(text)
            .itemLeft(itemStack)
            .plugin(menu.getPlugin())
            .open(player);
    }
}
