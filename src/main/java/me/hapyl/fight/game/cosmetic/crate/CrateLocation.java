package me.hapyl.fight.game.cosmetic.crate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.ItemStackRandomizedData;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.hologram.PlayerHologram;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CrateLocation extends Location {

    public static final String PREFIX = Color.BUTTON.color("&lCRATE! ") + Color.DEFAULT;

    public static final long MIN_TO_OPEN_TEN = 10;
    public static final long MIN_BULK_OPEN = 20;
    public static final long MAX_BULK_OPEN = 100;

    public static final PlayerRank RANK_TO_OPEN_TEN = PlayerRank.VIP;
    public static final PlayerRank RANK_TO_BULK_OPEN = PlayerRank.PREMIUM;

    private static final int LOOT_DISPLAY_TIME = 40;

    public final PlayerHologram hologram;
    private Player occupiedBy;

    public CrateLocation(int x, int y, int z) {
        super(BukkitUtils.defWorld(), x + 0.5d, y, z + 0.5d);

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

        animation.play0(crateLoot, this);
    }

    public void sendOccupiedMessage(@Nonnull Player player) {
        Notifier.error(player, "{} already opening a crate!", occupiedBy == player ? "You are" : occupiedBy.getName() + " is");
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
                        PREFIX + "%s has gotten %s from %s!".formatted(
                                player.getName(),
                                cosmetic.getFormatted() + Color.DEFAULT,
                                crateLoot.getEnumCrate().getCrate().getName()
                        )
                );
            });
        }
    }

    public CrateLocation setYawPitch(float yaw, float pitch) {
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

    public void openBulk(@Nonnull Player player, @Nonnull CrateEntry entry) {
        final Map<Crates, Long> totalCrates = entry.getTotalCrates();
        final List<CrateLoot> crateLoots = Lists.newArrayList();

        totalCrates.forEach((crate, amount) -> {
            if (crateLoots.size() >= MAX_BULK_OPEN) {
                return;
            }

            for (long i = 0; i < amount; i++) {
                crateLoots.add(new CrateLoot(player, crate));

                if (crateLoots.size() >= MAX_BULK_OPEN) {
                    break;
                }
            }
        });

        Chat.sendMessage(player, "");
        Chat.sendCenterMessage(player, "&6&lYour loot from %s crates:".formatted(crateLoots.size()));
        Chat.sendMessage(player, "");

        generateAndDisplayLoot(player, crateLoots, false);
    }

    public void openMultiple(@Nonnull Player player, @Nonnull CrateEntry entry, @Nonnull Crates enumCrate, long toOpen) {
        final Set<CrateLoot> crateLoots = Sets.newHashSet();

        for (long i = 0; i < toOpen; i++) {
            if (!entry.hasCrate(enumCrate)) {
                break;
            }

            crateLoots.add(new CrateLoot(player, enumCrate));
        }

        Chat.sendMessage(player, "");
        Chat.sendCenterMessage(player, "&6&lOpened %s %s crates!".formatted(crateLoots.size(), enumCrate.getName()));
        Chat.sendMessage(player, "");

        generateAndDisplayLoot(player, crateLoots, true);
    }

    private void generateAndDisplayLoot(Player player, Collection<CrateLoot> collection, boolean duplicateStyle) {
        final long[] convert = { 0, 0 };
        final Random random = new Random();
        final Set<Item> spawnedItems = Sets.newHashSet();
        final Set<Material> spawnedMaterials = Sets.newHashSet();

        // Explode the items explosions are cool
        final Location location = addAsNew(0, 0.7, 0);

        player.closeInventory();
        occupiedBy = player;

        playOpenAnimation();

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
        PlayerLib.playSound(location, Sound.BLOCK_CHEST_OPEN, 0.75f);
        PlayerLib.playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, 0.75f);

        collection.forEach(crateLoot -> {
            crateLoot.createLoot();

            final Cosmetics loot = crateLoot.getLoot();
            final Cosmetic cosmetic = loot.getCosmetic();
            final Rarity rarity = loot.getRarity();
            final boolean isNewLoot = crateLoot.isLootNew();
            final String lootName = cosmetic.getName();

            if (isNewLoot) {
                Chat.sendCenterMessage(player, "&a+ %s &7(%s) %s".formatted(
                        rarity.getColor() + lootName,
                        loot.getType().toSmallCaps(),
                        rarity
                ));
            }
            else {
                if (duplicateStyle) {
                    Chat.sendCenterMessage(
                            player,
                            "%s&b » %s".formatted(
                                    rarity.getColor().toString() + ChatColor.STRIKETHROUGH + lootName,
                                    rarity.getCompensationString()
                            )
                    );
                }

                convert[0] += rarity.getCoinCompensation();
                convert[1] += rarity.getDustCompensation();
            }

            // Don't spawn if already spawned the same cosmetic
            final Material material = cosmetic.getIcon();

            if (spawnedMaterials.contains(material)) {
                return;
            }

            final ItemStackRandomizedData itemStack = new ItemStackRandomizedData(material);
            final Vector vector = new Vector(
                    random.nextBoolean() ? -0.1 : 0.1,
                    0.07,
                    random.nextBoolean() ? -0.1 : 0.1
            );

            final Item item = getWorld().dropItem(location, itemStack);

            item.setGravity(false);
            item.setPickupDelay(100000);
            item.setTicksLived(6000 - LOOT_DISPLAY_TIME);
            item.setVelocity(vector);
            item.setCustomName(Chat.format(rarity.getColor() + lootName));
            item.setCustomNameVisible(true);

            Glowing.glow(player, item, rarity.getBukkitColor(), 50);

            spawnedItems.add(item);
            spawnedMaterials.add(material);
        });

        if (convert[0] > 0 && !duplicateStyle) {
            Chat.sendCenterMessage(player, Currency.COINS.formatProduct(convert[0]));
            Chat.sendCenterMessage(player, Currency.CHEST_DUST.formatProduct(convert[1]));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                spawnedItems.forEach(item -> {
                    PlayerLib.spawnParticle(item.getLocation(), Particle.EXPLOSION_NORMAL, 3);

                    item.remove();
                });

                spawnedItems.clear();
                spawnedMaterials.clear();

                occupiedBy = null;

                playCloseAnimation();

                // Fx
                PlayerLib.playSound(location, Sound.BLOCK_CHEST_CLOSE, 0.75f);
                PlayerLib.playSound(location, Sound.BLOCK_ENDER_CHEST_CLOSE, 0.75f);
            }
        }.runTaskLater(Main.getPlugin(), LOOT_DISPLAY_TIME);
    }
}
