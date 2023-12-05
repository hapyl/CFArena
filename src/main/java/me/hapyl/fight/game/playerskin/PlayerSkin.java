package me.hapyl.fight.game.playerskin;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.util.Runnables;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.dimension.DimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// With help from https://wiki.vg/Protocol, SkinsRestorer I finally got this to work.
// (September 3rd, 2023) -h
public class PlayerSkin {

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

    public void apply(Player player) {
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

    private void removePlayer(Player player) {
        final ClientboundPlayerInfoRemovePacket remove = new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));

        sendGlobalPacket(remove);
    }

    private void createPlayer(Player player) {
        final EntityPlayer mcPlayer = Reflect.getMinecraftPlayer(player);
        final ClientboundPlayerInfoUpdatePacket packet = ClientboundPlayerInfoUpdatePacket.a(List.of(mcPlayer)); // createPlayerInitializing()

        sendGlobalPacket(packet);

        // Respawn player
        final World playerWorld = player.getWorld();
        final Location location = player.getLocation();
        final PlayerInventory inventory = player.getInventory();
        final int heldItemSlot = inventory.getHeldItemSlot();

        final net.minecraft.world.level.World mcWorld = Reflect.getMinecraftWorld(playerWorld);
        final ResourceKey<DimensionManager> rkDimension = mcWorld.aa(); // dimensionTypeId()
        final ResourceKey<net.minecraft.world.level.World> rkWorld = mcWorld.ac(); // dimension()

        final PacketPlayOutRespawn respawnPacket = new PacketPlayOutRespawn(
                new CommonPlayerSpawnInfo(
                        rkDimension, rkWorld, playerWorld.getSeed(),
                        getNmsGameMode(player.getGameMode()),
                        getNmsGameMode(player.getPreviousGameMode()),
                        false,
                        false,
                        Optional.empty(),
                        0
                ), (byte) 0
        );

        sendPacket(player, respawnPacket);
        mcPlayer.w(); // onUpdateAbilities()

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

        // Update effects
        final Collection<MobEffect> activeEffects = mcPlayer.er(); // getActiveEffects()
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

        Debug.warn("Could not get %s's textures, using default. (Offline mode?)", player.getName());
        return new PlayerSkin("", "");
    }
}
