package me.nahu.itemshop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import de.themoep.minedown.MineDown;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.admin.AdminConfigurationMenu;
import me.nahu.itemshop.menu.user.ItemShopMenu;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.nahu.itemshop.menu.user.ItemShopMenu.DECIMAL_FORMAT;

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
                    MineDown.parse(successfulTransaction, "amount", DECIMAL_FORMAT.format(amount))
                );
                return;
            }
            player.spigot().sendMessage(
                MineDown.parse(unexpectedErrorTransaction, "error_message", response.errorMessage)
            );
        });
        shopMenu.open(player);
    }

    @CommandAlias("open")
    @CommandPermission("itemshop.open")
    @CommandCompletion("@players")
    public void open(
        @NotNull CommandSender sender,
        @NotNull OnlinePlayer target
    ) {
        sell(target.getPlayer());
    }

    @Subcommand("admin")
    @CommandPermission("itemshop.admin")
    public void admin(
        @NotNull Player player
    ) {
        new AdminConfigurationMenu(itemShopPlugin).open(player);
    }
}
