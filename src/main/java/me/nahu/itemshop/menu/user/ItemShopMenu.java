package me.nahu.itemshop.menu.user;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import me.nahu.itemshop.ItemShopManager;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.Menu;
import me.nahu.itemshop.shop.ShopUser;
import me.nahu.itemshop.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.nahu.itemshop.utils.Utilities.color;

public class ItemShopMenu extends Menu {
    private static final ItemStack EMPTY = new ItemStack(Material.BARRIER);
    private static final ItemStack ACCEPT = new ItemStack(Material.POISONOUS_POTATO);

    private final ItemShopManager itemShopManager;
    private final double hagglePercentageRange;
    private double haggleModifier = 1;

    private BukkitTask task;

    public ItemShopMenu(@NotNull ItemShopPlugin plugin) {
        super(plugin);
        this.itemShopManager = plugin.getItemShopManager();
        this.hagglePercentageRange = plugin.getHagglePercentageRange() / 100;
    }

    @Override
    protected @NotNull InventoryGui newInventory(@NotNull Player player) {
        InventoryGui inventoryGui = new InventoryGui(
            getPlugin(),
            "Item Shop",
            new String[]{ "        a" }
        );

        ShopUser shopUser = itemShopManager.getShopUser(player);
        inventoryGui.addElement(new DynamicGuiElement('a', (viewer) ->
            (getInventoryContents().size() <= 1) ?
            staticElement(
                'a',
                EMPTY,
                color("&cPlace items to sell!"),
                click -> true
            ) :
            staticElement(
                'a',
                ACCEPT,
                color(
                    "&aAccept",
                    "&7Total: &d$" + (int) (calculateTotal() * haggleModifier),
                    "&7",
                    "&7Unchanged total: &e$" + calculateTotal(),
                    "&7Maximum potential total: &a$" + calculateTotal() * (hagglePercentageRange + 1),
                    "&7Minimum potential total: &c$" + calculateTotal() * (1 - hagglePercentageRange),
                    "&8Right Click to &eHaggle",
                    "&8Left Click to &aSell"
                ),
                click -> {
                    switch (click.getType()) {
                        case LEFT:
                            // Right click to haggle
                            break;
                        case RIGHT:
                            if (!shopUser.canHaggle())
                                break;
                            shopUser.updateHaggleAttempt();
                            haggleModifier = calculateHaggleModifier();
                            System.out.println(haggleModifier);

                            inventoryGui.draw(); // update
                            break;
                    }
                    return true;
                }
            )
        ));

        inventoryGui.addElement(
            storageElement(' ', () -> {
                if (task != null) task.cancel();
                task = Utilities.runDelayedTask(inventoryGui::draw, 20);
            })
        );
        return inventoryGui;
    }

    private double calculateHaggleModifier() {
        double maxHagglePercentageRange = hagglePercentageRange + 1;
        double minHagglePercentageRange = 1 - hagglePercentageRange;
        return (Math.random() * (maxHagglePercentageRange - minHagglePercentageRange) + minHagglePercentageRange);
    }

    private int calculateTotal() {
        return getInventoryContents().stream()
            .filter(Objects::nonNull)
            .mapToInt(itemShopManager::getCombinedPrice)
            .reduce(0, Integer::sum);
    }
}
