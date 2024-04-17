package me.hapyl.fight.game.playerskin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.util.Runnables;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.dimension.DimensionManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// With help from https://wiki.vg/Protocol, SkinsRestorer I finally got this to work.
// (September 3rd, 2023) -h
public class PlayerSkin {

    public static final Cache cache = new Cache();

    private final String texture;
    private final String signature;

    public PlayerSkin(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;
    }

    @Nonnull
    public String getTexture() {
        return texture;
    }

    @Nonnull
    public String getSignature() {
        return signature;
    }

    public void apply(@Nonnull Player player) {
        final GameProfile gameProfile = Reflect.getGameProfile(player);
        final PropertyMap properties = gameProfile.getProperties();

        removePlayer(player);

        properties.removeAll("textures");
        properties.put("textures", new Property("textures", texture, signature));

        createPlayer(player);
    }

    @Nonnull
    public String[] getTextures() {
        return new String[] { texture, signature };
    }

    private void removePlayer(@Nonnull Player player) {
        final ClientboundPlayerInfoRemovePacket remove = new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));

        sendGlobalPacket(remove);
    }

    private void createPlayer(@Nonnull Player player) {
        final EntityPlayer mcPlayer = Reflect.getMinecraftPlayer(player);
        final ClientboundPlayerInfoUpdatePacket packet = ClientboundPlayerInfoUpdatePacket.a(List.of(mcPlayer)); // createPlayerInitializing()

        sendGlobalPacket(packet);

        // Re-created for others
        final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(player.getEntityId());
        final PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(mcPlayer);

        Bukkit.getOnlinePlayers().forEach(online -> {
            if (online == player) {
                return;
            }

            sendPacket(online, destroyPacket);
            sendPacket(online, spawnPacket);
        });

        // Respawn player
        final World playerWorld = player.getWorld();
        final Location location = player.getLocation();
        final PlayerInventory inventory = player.getInventory();
        final int heldItemSlot = inventory.getHeldItemSlot();

        final net.minecraft.world.level.World mcWorld = Reflect.getMinecraftWorld(playerWorld);

        final PacketPlayOutRespawn respawnPacket = new PacketPlayOutRespawn(
                mcPlayer.d(mcWorld.getMinecraftWorld()), (byte) 0
        );

        sendPacket(player, respawnPacket);
        mcPlayer.y(); // onUpdateAbilities()

        // Load chunk (wtf?)
        final PacketPlayOutGameStateChange packetLoadChunk = new PacketPlayOutGameStateChange(
                PacketPlayOutGameStateChange.n,
                0.0f
        );

        sendPacket(player, packetLoadChunk);

        // Update player position
        final PacketPlayOutPosition positionPacket = new PacketPlayOutPosition(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                Sets.newHashSet(), 0
        );

        // Update EXP
        final PacketPlayOutExperience packetExp = new PacketPlayOutExperience(
                player.getExp(),
                player.getTotalExperience(),
                player.getLevel()
        );

        // Update 2nd layer
        final Entity minecraftEntity = Reflect.getMinecraftEntity(player);

        if (minecraftEntity != null) {
            Bukkit.getOnlinePlayers().forEach(online -> {
                Reflect.updateMetadata(minecraftEntity, online);
            });
        }

        // Update effects
        final Collection<MobEffect> activeEffects = mcPlayer.es(); // getActiveEffects()
        activeEffects.forEach(effect -> {
            final PacketPlayOutEntityEffect packetEffect = new PacketPlayOutEntityEffect(player.getEntityId(), effect);
            sendPacket(player, packetEffect);
        });

        sendPacket(player, positionPacket);
        sendPacket(player, packetExp);

        inventory.setHeldItemSlot(heldItemSlot);

        // Delayed updates
        Runnables.runLater(() -> {
            player.updateInventory();

            // Update equipment
            final PacketPlayOutEntityEquipment packetUpdateEquipment = new PacketPlayOutEntityEquipment(
                    player.getEntityId(),
                    List.of(
                            getPair(player, EquipmentSlot.HAND),
                            getPair(player, EquipmentSlot.OFF_HAND),
                            getPair(player, EquipmentSlot.FEET),
                            getPair(player, EquipmentSlot.LEGS),
                            getPair(player, EquipmentSlot.CHEST),
                            getPair(player, EquipmentSlot.HEAD)
                    )
            );

            sendGlobalPacket(packetUpdateEquipment);

            // Fix "Unable to switch game mode; no permission"
            if (player.isOp()) {
                player.setOp(false);
                player.setOp(true);
            }
        }, 1);
    }

    private EnumGamemode getNmsGameMode(GameMode mode) {
        if (mode == null) {
            return null;
        }

        return switch (mode) {
            case SURVIVAL -> EnumGamemode.a;
            case CREATIVE -> EnumGamemode.b;
            case ADVENTURE -> EnumGamemode.c;
            case SPECTATOR -> EnumGamemode.d;
        };
    }

    private EnumItemSlot getNmsItemSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HAND -> EnumItemSlot.a;
            case OFF_HAND -> EnumItemSlot.b;
            case FEET -> EnumItemSlot.c;
            case LEGS -> EnumItemSlot.d;
            case CHEST -> EnumItemSlot.e;
            case HEAD -> EnumItemSlot.f;
        };
    }

    private Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> getPair(Player player, EquipmentSlot slot) {
        final ItemStack item = player.getInventory().getItem(slot);

        return new Pair<>(getNmsItemSlot(slot), Reflect.bukkitItemToNMS(item != null ? item : new ItemStack(Material.AIR)));
    }

    private void sendPacket(@Nonnull Player player, @Nonnull Packet<?> packet) {
        Reflect.sendPacket(player, packet);
    }

    private void sendGlobalPacket(@Nonnull Packet<?> packet) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Reflect.sendPacket(player, packet);
        });
    }

    public static void reset(Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        if (profile == null) {
            Debug.severe("Cannot set textures for offline player!");
            return;
        }

        profile.getOriginalSkin().apply(player);
    }

    @Nonnull
    public static PlayerSkin of(Player player) {
        final GameProfile profile = Reflect.getGameProfile(player);
        final Collection<Property> textures = profile.getProperties().get("textures");

        for (Property property : textures) {
            return new PlayerSkin(property.value(), property.signature());
        }

        Debug.warn("Could not get %s's textures, using default. (Offline mode?)".formatted(player.getName()));
        return new PlayerSkin("", "");
    }

    public static class Cache {
        private final Map<String, PlayerSkin> cached;

        public Cache() {
            this.cached = Maps.newHashMap();
        }

        public boolean isCached(@Nonnull String name) {
            return cached.containsKey(name.toLowerCase());
        }

        public boolean cache(@Nonnull String name, @Nonnull PlayerSkin skin) {
            return cached.put(name.toLowerCase(), skin) == null;
        }

        @Nullable
        public PlayerSkin getCached(@Nonnull String name) {
            return cached.get(name.toLowerCase());
        }
    }
}
