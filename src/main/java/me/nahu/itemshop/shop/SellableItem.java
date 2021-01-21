package me.nahu.itemshop.shop;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public final class SellableItem implements ConfigurationSerializable {
    private final int id;
    private String name;

    private Material material;
    private short data;

    private int price;

    public SellableItem(int id, @Nullable String name, @NotNull Material material, short data, int price) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.data = data;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    public boolean matchesItem(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != material || itemStack.getDurability() != data) {
            return false;
        }
        String name = itemStack.getItemMeta().getDisplayName();
        if (!getName().isPresent()) {
            return name == null;
        }
        return getName().get().equals(name);
    }

    @NotNull
    public ItemStack toItemStack() {
        return new ItemStack(material, 1, data);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
            .put("id", id)
            .put("material", getMaterial().toString())
            .put("data", data)
            .put("name", getName().orElse("none"))
            .put("price", price)
            .build();
    }

    @NotNull
    public static SellableItem deserialize(@NotNull Map<String, Object> args) {
        int id = (int) args.get("id");
        Material material = Material.matchMaterial((String) args.get("material"));
        short data = (short) ((int) args.get("data"));
        String name = (String) args.get("name");
        int price = (int) args.get("price");
        return new SellableItem(
            id,
            (name.equals("none")) ? null : name,
            material,
            data,
            price
        );
    }
}
