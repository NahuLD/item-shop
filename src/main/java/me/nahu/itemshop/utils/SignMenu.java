package me.nahu.itemshop.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SignMenu {
    private static final Logger logger = Logger.getLogger(SignMenu.class.getName());

    private final Plugin plugin;

    private BiConsumer<Player, String[]> listener;
    private String[] lines;
    private BlockPosition position;
    private boolean color;

    public SignMenu(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public void show(@NotNull Player player) {
        ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

        PacketContainer open = protocol.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        PacketContainer remove = protocol.createPacket(PacketType.Play.Server.BLOCK_CHANGE);

        Location location = SignLocator.get().next(player.getLocation().getChunk());
        BlockPosition position = this.position = new BlockPosition(location.getBlockX(), 0, location.getBlockZ());

        open.getBlockPositionModifier().write(0, position);
        remove.getBlockPositionModifier().write(0, position);
        remove.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));

        try {
            PacketContainer block = protocol.createPacket(PacketType.Play.Server.BLOCK_CHANGE);

            if(lines != null) {
                PacketContainer update = protocol.createPacket(PacketType.Play.Server.UPDATE_SIGN);

                block.getBlockPositionModifier().write(0, position);
                block.getBlockData().write(0, WrappedBlockData.createData(Material.SIGN_POST));
                update.getBlockPositionModifier().write(0, position);
                update.getChatComponentArrays().write(0, wrap());

                protocol.sendServerPacket(player, block);
                protocol.sendServerPacket(player, update);
            }

            protocol.sendServerPacket(player, open);
            protocol.sendServerPacket(player, block);
            protocol.sendServerPacket(player, remove);
        } catch (InvocationTargetException e) {
            logger.info("Exception in reflection.");
            return;
        }

        if(listener != null) {
            protocol.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.UPDATE_SIGN) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    protocol.removePacketListener(this);

                    PacketContainer packet = event.getPacket();
                    BlockPosition position = packet.getBlockPositionModifier().read(0);
                    WrappedChatComponent[] components = packet.getChatComponentArrays().read(0);

                    if(SignMenu.this.position == null) {
                        throw new IllegalStateException("Sign update called but position not yet set.");
                    }

                    if(SignMenu.this.position.equals(position)) {
                        Bukkit.getScheduler().runTask(plugin, () -> listener.accept(event.getPlayer(), unwrap(components)));
                    }
                }
            });
        }
    }

    private WrappedChatComponent[] wrap() {
        return Stream.of(lines)
            .map(line -> line == null ? "" : line)
            .map(WrappedChatComponent::fromText)
            .toArray(WrappedChatComponent[]::new);
    }

    private String[] unwrap(@NotNull WrappedChatComponent[] components) {
        return Stream.of(components)
            .map(component -> IChatBaseComponent.ChatSerializer.a(component.getJson()).getText())
            .map(text -> color ? ChatColor.translateAlternateColorCodes('&', text) : text)
            .toArray(String[]::new);
    }

    public SignMenu line(int index, @NotNull String text) {
        if(lines == null) {
            lines = new String[4];
        }
        lines[index] = text;
        return this;
    }

    public SignMenu listener(BiConsumer<Player, String[]> listener) {
        this.listener = listener;
        return this;
    }

    public SignMenu color() {
        this.color = true;
        return this;
    }

    public enum Line {
        FIRST,
        SECOND,
        THIRD,
        FOURTH
    }

    public static class SignLocator implements Listener {
        private static final SignLocator instance = new SignLocator();
        private final Map<Chunk, Iterator<Location>> iterators = new HashMap<>();

        @EventHandler(ignoreCancelled = true)
        public void onChunkUnload(ChunkUnloadEvent event) {
            iterators.remove(event.getChunk());
        }

        public Location next(Chunk chunk) {
            Iterator<Location> iterator = iterators.get(chunk);
            if (iterator == null || !iterator.hasNext()) {
                iterator = create(chunk);
                iterators.put(chunk, iterator);
            }
            return iterator.next();
        }

        private Iterator<Location> create(Chunk chunk) {
            int minX = chunk.getX() << 4;
            int minZ = chunk.getZ() << 4;
            int maxX = minX + 16;
            int maxZ = minZ + 16;

            return IntStream.range(0, chunk.getWorld().getMaxHeight())
                .mapToObj(y -> IntStream.range(minX, maxX)
                    .mapToObj(x -> IntStream.range(minZ, maxZ)
                        .mapToObj(z -> new Location(chunk.getWorld(), x, y, z))
                    ).flatMap(Function.identity())
                ).flatMap(Function.identity())
                .iterator();
        }

        public static SignLocator get() {
            return instance;
        }
    }
}