package me.nahu.itemshop.utils;

import me.nahu.itemshop.ItemShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

public final class Utilities {
    private static final Plugin PLUGIN = ItemShopPlugin.getPlugin(ItemShopPlugin.class);

    public static String[] color(String[] existing, String... input) {
        return color(Stream.concat(Arrays.stream(existing), Stream.of(input)).toArray(String[]::new));
    }

    public static String[] color(String... input) {
        return Stream.of(input)
                .map(it -> ChatColor.translateAlternateColorCodes('&', it))
                .toArray(String[]::new);
    }

    public static BukkitTask runDelayedTask(@NotNull Runnable runnable, int delayInTicks) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(PLUGIN, delayInTicks);
    }

    public static void runTaskAsynchronously(@NotNull Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(PLUGIN);
    }
}
