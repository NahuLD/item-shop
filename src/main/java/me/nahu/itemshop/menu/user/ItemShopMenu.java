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

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.DoubleConsumer;

import static me.nahu.itemshop.menu.admin.AdminConfigurationMenu.FILLER;
import static me.nahu.itemshop.utils.Utilities.color;

public class ItemShopMenu extends Menu {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final ItemStack EMPTY = new ItemStack(Material.BARRIER);
    private static final ItemStack ACCEPT = new ItemStack(Material.POISONOUS_POTATO);

    private final ItemShopManager itemShopManager;
    private final double hagglePercentageRange;

    private BukkitTask task;
    private DoubleConsumer consumer;

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
            new String[]{
                "ppppppppp",
                "         ",
                "ppppppppa"
            }
        );

        ShopUser shopUser = itemShopManager.getShopUser(player);
        inventoryGui.addElement(new DynamicGuiElement('a', (viewer) -> {
            if (getInventoryContents().size() <= 1) {
                return staticElement(
                    'a',
                    EMPTY,
                    color(
                        "&cUnable to Sell",
                        "&8&m-------------------------",
                        "&7",
                        "&7Place items to sell!",
                        "&7",
                        "&8&m-------------------------"
                    ),
                    click -> true
                );
            }
            if (itemShopManager.hasInvalidItems(getVirtualInventory().getContents())) {
                return staticElement(
                    'a',
                    EMPTY,
                    color(
                        "&cUnable to Sell",
                        "&8&m-------------------------",
                        "&7",
                        "&7You can &cnot sell &7some &citems&7!",
                        "&7",
                        "&8&m-------------------------"
                    ),
                    click -> true
                );
            }
            return staticElement(
                'a',
                ACCEPT,
                color(
                    "&aSell Items",
                    "&8&m-------------------------",
                    "&7",
                    "&7Sell Price: &a&n$" + DECIMAL_FORMAT.format(calculateTotal() * shopUser.getHaggleModifier()),
                    "&7",
                    "&7Max Worth: &e$" + DECIMAL_FORMAT.format(calculateTotal() * (hagglePercentageRange + 1)),
                    "&7Min Worth: &c$" + DECIMAL_FORMAT.format(calculateTotal() * (1 - hagglePercentageRange)),
                    "&7",
                    "&8[&6Left-Click&8] &7to &aSell Items&7.",
                    "&8[&6Right-Click&8] &7to &eHaggle Price&7.",
                    "&8&m-------------------------"
                ),
                click -> {
                    switch (click.getType()) {
                        case LEFT:
                            if (consumer == null) {
                                break;
                            }
                            consumer.accept(calculateTotal() * shopUser.getHaggleModifier());
                            break;
                        case RIGHT:
                            if (!shopUser.canHaggle()) {
                                break;
                            }
                            shopUser.updateHaggleAttempt();
                            shopUser.setHaggleModifier(calculateHaggleModifier());

                            inventoryGui.draw(); // update
                            break;
                    }
                    return true;
                }
            );
        }));

        inventoryGui.addElement(
            storageElement(' ', () -> {
                if (task != null) task.cancel();
                task = Utilities.runDelayedTask(inventoryGui::draw, 10);
            })
        );

        inventoryGui.addElement(staticElement('p', FILLER, color("&7", "&7"), click -> true));
        return inventoryGui;
    }

    public void setHandleTransactionAction(@NotNull DoubleConsumer consumer) {
        this.consumer = consumer;
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
