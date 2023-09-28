package me.hapyl.fight.game.cosmetic.crate;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.hologram.PlayerHologram;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrateChest extends Location {

    public static final String PREFIX = Color.BUTTON.color("&lCRATE! ") + Color.DEFAULT;
    public final PlayerHologram hologram;
    private Player occupiedBy;

    public CrateChest(int x, int y, int z) {
        super(BukkitUtils.defWorld(), x + 0.5d, y, z + 0.5);

        this.hologram = new PlayerHologram(this.addAsNew(0.0d, 1.0d, 0.0d));
    }

    public void onOpen(@Nonnull CrateLoot crateLoot) {
        final Player player = crateLoot.getPlayer();

        if (occupiedBy != null) {
            sendOccupiedMessage(player);
            return;
        }

        crateLoot.createLoot();
        final Cosmetics loot = crateLoot.getLoot();

        occupiedBy = player;

        final Rarity rarity = loot.getRarity();
        final CrateAnimation animation = CrateAnimation.byRarity(rarity);

        animation.play0(crateLoot);
    }

    public void sendOccupiedMessage(@Nonnull Player player) {
        Message.error(player, "{} is already opening a crate!", occupiedBy.getName());
    }

    public boolean checkOccupiedAndSendError(@Nonnull Player player) {
        final boolean occupied = isOccupied();

        if (occupied) {
            sendOccupiedMessage(player);
        }

        return occupied;
    }

    public boolean isOccupied() {
        return occupiedBy != null;
    }

    public void setOccupied(@Nullable Player player) {
        this.occupiedBy = player;
    }

    public void playOpenAnimation() {
        CFUtils.playChestAnimation(getBlock(), true);
    }

    public void playCloseAnimation() {
        CFUtils.playChestAnimation(getBlock(), false);
    }

    public void broadcastLoot(@Nonnull CrateLoot crateLoot) {
        final Player player = crateLoot.getPlayer();
        final Cosmetics loot = crateLoot.getLoot();
        final Cosmetic cosmetic = loot.getCosmetic();
        final Rarity rarity = cosmetic.getRarity();
        final boolean isNew = crateLoot.isLootNew();

        Chat.sendCenterMessage(player, Color.BUTTON.bold() + crateLoot.getEnumCrate().getCrate().getName());

        Chat.sendMessage(player, "");
        Chat.sendCenterMessage(player, "&lObtained:");
        Chat.sendCenterMessage(player, (isNew ? "&a" : "&a&m") + cosmetic.getName());
        Chat.sendCenterMessage(player, (isNew ? "&7" : "&7&m") + cosmetic.getType().getName());
        Chat.sendCenterMessage(player, rarity.getFormatted());

        if (!isNew) {
            Chat.sendMessage(player, "");
            Chat.sendCenterMessage(player, "&b&lConverted:");
            Chat.sendCenterMessage(player, rarity.getCompensationString());
            Chat.sendMessage(player, "");
        }

        if (rarity.isFlexible()) {
            Bukkit.getOnlinePlayers().forEach(online -> {
                if (player == online) {
                    return;
                }

                Chat.sendMessage(
                        online,
                        PREFIX + "%s has gotten %s from %s!",
                        player.getName(),
                        cosmetic.getFormatted() + Color.DEFAULT,
                        crateLoot.getEnumCrate().getCrate().getName()
                );
            });
        }
    }

    public CrateChest setYawPitch(float yaw, float pitch) {
        setYaw(yaw);
        setPitch(pitch);
        return this;
    }

    public Location addAsNew(double x, double y, double z) {
        return BukkitUtils.newLocation(this).add(x, y, z);
    }

    public Location subtractAsNew(double x, double y, double z) {
        return BukkitUtils.newLocation(this).subtract(x, y, z);
    }

    @Nonnull
    public World getWorld() {
        final World world = super.getWorld();

        return world == null ? BukkitUtils.defWorld() : world;
    }
}
