package me.nahu.itemshop.shop;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ShopUser {
    private static long haggleAttemptCooldown = 0L;

    private final UUID uniqueId;
    private long lastHaggleAttempt = 0;
    private double haggleModifier;

    public ShopUser(@NotNull UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    public long getLastHaggleAttempt() {
        return lastHaggleAttempt;
    }

    public double getHaggleModifier() {
        return haggleModifier;
    }

    public void setHaggleModifier(double haggleModifier) {
        this.haggleModifier = haggleModifier;
    }

    public void updateHaggleAttempt() {
        setLastHaggleAttempt(System.currentTimeMillis());
    }

    public void setLastHaggleAttempt(long lastHaggleAttempt) {
        this.lastHaggleAttempt = lastHaggleAttempt;
    }

    public boolean canHaggle() {
        return (lastHaggleAttempt + haggleAttemptCooldown) <= System.currentTimeMillis();
    }

    public static void setHaggleAttemptCooldown(long haggleAttemptCooldown) {
        ShopUser.haggleAttemptCooldown = haggleAttemptCooldown;
    }
}
