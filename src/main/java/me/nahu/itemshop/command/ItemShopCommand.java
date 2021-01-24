package me.nahu.itemshop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.themoep.minedown.MineDown;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.admin.AdminConfigurationMenu;
import me.nahu.itemshop.menu.user.ItemShopMenu;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("itemshop")
@CommandPermission("itemshop.*")
public final class ItemShopCommand extends BaseCommand {
    private final ItemShopPlugin itemShopPlugin;
    private final Economy economy;

    private final String successfulTransaction;
    private final String unexpectedErrorTransaction;

    public ItemShopCommand(@NotNull ItemShopPlugin itemShopPlugin) {
        this.itemShopPlugin = itemShopPlugin;
        this.economy = itemShopPlugin.getEconomy();

        this.successfulTransaction = itemShopPlugin.getConfig().getString("successful-transaction", "N/A");
        this.unexpectedErrorTransaction = itemShopPlugin.getConfig().getString("unexpected-error-transaction", "N/A");
    }

    @Default
    @CommandPermission("itemshop.sell")
    public void sell(
        @NotNull Player player
    ) {
        ItemShopMenu shopMenu = new ItemShopMenu(itemShopPlugin);
        shopMenu.setHandleTransactionAction(amount -> {
            EconomyResponse response = economy.depositPlayer(player, amount);
            if (response.transactionSuccess()) {
                player.spigot().sendMessage(
                    MineDown.parse(successfulTransaction, "amount", String.valueOf(amount))
                );
                return;
            }
            player.spigot().sendMessage(
                MineDown.parse(unexpectedErrorTransaction, "error_message", response.errorMessage)
            );
        });
        shopMenu.open(player);
    }

    @Subcommand("admin")
    @CommandPermission("itemshop.admin")
    public void admin(
        @NotNull Player player
    ) {
        new AdminConfigurationMenu(itemShopPlugin).open(player);
    }
}
