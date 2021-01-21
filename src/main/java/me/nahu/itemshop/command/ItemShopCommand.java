package me.nahu.itemshop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.nahu.itemshop.ItemShopPlugin;
import me.nahu.itemshop.menu.admin.AdminMenu;
import me.nahu.itemshop.menu.user.ItemShopMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("itemshop")
@CommandPermission("itemshop.*")
public final class ItemShopCommand extends BaseCommand {
    private final ItemShopPlugin itemShopPlugin;

    public ItemShopCommand(@NotNull ItemShopPlugin itemShopPlugin) {
        this.itemShopPlugin = itemShopPlugin;
    }

    @Default
    @CommandPermission("itemshop.sell")
    public void sell(
        @NotNull Player player
    ) {
        new ItemShopMenu(itemShopPlugin).open(player);
    }

    @Subcommand("admin")
    @CommandPermission("itemshop.admin")
    public void admin(
        @NotNull Player player
    ) {
        new AdminMenu(itemShopPlugin).open(player);
    }
}
