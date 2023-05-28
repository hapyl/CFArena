package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.ender.TransmissionBeacon;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Blocks;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.LinkedKeyValMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ender extends Hero implements Listener {

    private final LinkedKeyValMap<Player, Entity> beaconLocation = new LinkedKeyValMap<>();
    //private final IMap<Player, Entity> beaconLocation = new KVMap<>();
    private final int portKeyCooldown = 160;
    private final double locationError = 1.3;

    public Ender() {
        super("Ender");
        setRole(Role.ASSASSIN);
        setItem("aacb357709d8cdf1cd9c9dbe313e7bab3276ae84234982e93e13839ab7cc5d16");

        setMinimumLevel(5);

        setInfo("Weird enderman-like looking warrior with teleportation abilities. He hits you with his arm, but it hurts like a brick.");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestplate(85, 0, 102);
        equipment.setLeggings(128, 0, 128);
        equipment.setBoots(136, 0, 204);

        setWeapon(new Weapon(Material.ENDERMAN_SPAWN_EGG) {

            private final Map<Player, Location> targetLocation = new HashMap<>();

            private boolean hasLocation(Player player) {
                return targetLocation.containsKey(player);
            }

            @Override
            public void onRightClick(Player player, ItemStack item) {
                if (player.hasCooldown(item.getType())) {
                    return;
                }

                // Cancel
                if (hasLocation(player)) {
                    targetLocation.remove(player);
                    Chat.sendTitle(player, "", "&c&lCANCELLED", 0, 10, 10);
                    PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 0.0f);
                    return;
                }

                // Check for los block
                final Block targetBlock = player.getTargetBlockExact(25);

                if (!Blocks.isValid(targetBlock)) {
                    Chat.sendMessage(player, "&cNo valid block in sight!");
                    return;
                }

                // Initiate
                final List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 25);

                final Block preLastBlock = lastTwoTargetBlocks.get(0);
                final Block lastBlock = lastTwoTargetBlocks.get(1);

                if (preLastBlock == null || lastBlock == null) {
                    Chat.sendMessage(player, "&cNo valid block in sight!");
                    PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
                    return;
                }

                if (preLastBlock.getType().isAir()) {
                    final Block downBlock = lastBlock.getRelative(BlockFace.DOWN);
                    if (downBlock.getLocation().equals(preLastBlock.getLocation())) {
                        Chat.sendMessage(player, "&cCannot teleport from below a block!");
                        return;
                    }
                }

                final Location location = lastBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);

                if (!location.getBlock().getType().isAir() || !location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
                    Chat.sendMessage(player, "&cTarget location is not safe!");
                    PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
                    return;
                }

                targetLocation.put(player, location);

                new GameTask() {
                    private final int maxWindupTime = 15;
                    private int windupTime = maxWindupTime;

                    @Override
                    public void run() {
                        if (!hasLocation(player)) {
                            this.cancel();
                            return;
                        }

                        if (windupTime > 0) {
                            final StringBuilder builder = new StringBuilder();
                            final int percentOfEight = (windupTime * 8 / maxWindupTime);
                            for (int i = 0; i < 8; i++) {
                                builder.append(i >= percentOfEight ? "&8" : "&a");
                                builder.append("-");
                            }

                            // Display fx at target block
                            Geometry.drawCircle(location, 1.2d, Quality.NORMAL, new WorldParticle(Particle.SPELL_WITCH));

                            PlayerLib.playSound(
                                    player,
                                    Sound.BLOCK_LEVER_CLICK,
                                    Numbers.clamp((float) (1.3d - (0.1f * percentOfEight)), 0.0f, 2.0f)
                            );
                            Chat.sendTitle(player, "", builder.toString(), 0, 10, 0);
                            --windupTime;
                            return;
                        }

                        this.cancel();
                        Chat.sendTitle(player, "", "&aTeleporting...", 0, 20, 10);
                        // Teleport
                        final Location playerLocation = player.getLocation();

                        BukkitUtils.mergePitchYaw(playerLocation, location);
                        Geometry.drawLine(playerLocation, location, 0.25f, new WorldParticle(Particle.PORTAL));

                        player.teleport(location);
                        player.setCooldown(item.getType(), portKeyCooldown);
                        targetLocation.remove(player);

                        PlayerLib.playSound(playerLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);

                    }
                }.runTaskTimer(0, 1);

            }

        }.setName("Fist")
                .setId("ender_weapon")
                .setDescription(
                        "Just a normal sized a.____&e&lRIGHT CLICK &7to initiate teleport to the target block. &e&lRIGHT CLICK &7again to cancel.____&aCooldown: &l%ss",
                        BukkitUtils.roundTick(portKeyCooldown)
                )
                .setDamage(7.0));

        // Instantly teleports you to a placed transmission beacon and gives it back.
        // Instantly teleport to your &bTransmission Beacon &7and collect it for further use.

        this.setUltimate(new UltimateTalent(
                "Transmission!",
                "Instantly teleport to your &b&lTransmission &b&lBeacon &7and collect it for further use.",
                50
        ).setItem(Material.SHULKER_SHELL).setCdSec(20).setSound(Sound.ENTITY_GUARDIAN_HURT_LAND, 0.75f));

    }

    @Override
    public boolean predicateUltimate(Player player) {
        return beaconLocation.containsKey(player);
    }

    @Override
    public String predicateMessage(Player player) {
        return "Transmission Beacon is not placed!";
    }

    @Override
    public void useUltimate(Player player) {
        teleportToBeacon(player);
    }

    @EventHandler()
    public void handleEntityDamage(EntityDamageByEntityEvent ev) {
        final Manager current = Manager.current();
        if (current.isGameInProgress()
                && ev.getEntity() instanceof ArmorStand stand
                && ev.getDamager() instanceof Player player
                && current.isPlayerInGame(player)) {

            if (!beaconLocation.containsValue(stand)) {
                return;
            }

            final Player owner = beaconLocation.getKey(stand);
            if (owner == null) {
                return;
            }

            stand.remove();
            beaconLocation.remove(owner);

            final TransmissionBeacon talent = Talents.TRANSMISSION_BEACON.getTalent(TransmissionBeacon.class);
            talent.startCd(owner, talent.getDestroyCd());

            // Fx
            PlayerLib.playSound(stand.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.0f);
            Chat.sendMessage(player, "&aYou broke &l%s's &aTransmission Beacon!", owner.getName());
            Chat.sendTitle(owner, "", "&aBeacon Destroyed!", 10, 20, 10);
        }
    }

    @Override
    public void onDeath(Player player) {
        beaconLocation.useValueAndRemove(player, Entity::remove);
    }

    public void setBeaconLocation(Player player, Location location) {
        if (hasBeacon(player)) {
            return;
        }

        beaconLocation.put(player, Entities.ARMOR_STAND.spawn(location.add(0.5d, -locationError, 0.5d), me -> {
            me.setSilent(true);
            me.setVisible(false);
            me.setGravity(false);
            me.setMaxHealth(2048.0d);
            me.setHealth(2048.0d);
            if (me.getEquipment() != null) {
                me.getEquipment().setHelmet(new ItemStack(Material.BEACON));
            }
            // lock all the slots (I think that's how it works?)
            Utils.lockArmorStand(me);
            //for (final EquipmentSlot value : EquipmentSlot.values()) {
            //    for (final ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
            //        me.addEquipmentLock(value, lockType);
            //    }
            //}
        }));
    }

    public boolean hasBeacon(Player player) {
        return beaconLocation.containsKey(player);
    }

    public void teleportToBeacon(Player player) {
        final Entity entity = beaconLocation.getValue(player);
        if (entity == null) {
            return;
        }

        beaconLocation.remove(player);
        entity.remove();

        final Location location = entity.getLocation().add(0.0d, locationError, 0.0d);
        BukkitUtils.mergePitchYaw(player.getLocation(), location);
        player.teleport(location);
        PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
        PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
    }

    @Override
    public void onStop() {
        // Utils.clearCollection; modCheck
        // beaconLocation is not a collection you dumbo
        beaconLocation.values().forEach(Entity::remove);
        beaconLocation.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.ENDER.getAlivePlayers().forEach(player -> {
                    if (player.getPlayer().isInWater()) {
                        player.damage(2.0d);
                        PlayerLib.playSound(player.getPlayer(), Sound.ENTITY_ENDERMAN_HURT, 1.2f);
                    }
                });
            }
        }.runTaskTimer(0, 20);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TELEPORT_PEARL.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.TRANSMISSION_BEACON.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.ENDERMAN_FLESH.getTalent();
    }
}
