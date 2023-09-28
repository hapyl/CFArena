package me.hapyl.fight.game.heroes.archive.ender;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.ender.TransmissionBeacon;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import me.hapyl.spigotutils.module.util.LinkedKeyValMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Ender extends Hero implements Listener {

    private final LinkedKeyValMap<Player, Entity> beaconLocation = new LinkedKeyValMap<>();
    private final double locationError = 1.3;

    public Ender() {
        super("Ender");

        setArchetype(Archetype.MOBILITY);

        setItem("aacb357709d8cdf1cd9c9dbe313e7bab3276ae84234982e93e13839ab7cc5d16");
        setMinimumLevel(5);

        setDescription(
                "Weird enderman-like looking warrior with teleportation abilities. He hits you with his arm, but it hurts like a brick."
        );

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(85, 0, 102);
        equipment.setLeggings(128, 0, 128);
        equipment.setBoots(136, 0, 204);

        setWeapon(new EnderWeapon());

        // Instantly teleports you to a placed transmission beacon and gives it back.
        // Instantly teleport to your &bTransmission Beacon &7and collect it for further use.

        setUltimate(new UltimateTalent(
                "Transmission!",
                "Instantly teleport to your &b&lTransmission &b&lBeacon &7and collect it for further use.",
                50
        ).setItem(Material.SHULKER_SHELL).setCooldownSec(20).setSound(Sound.ENTITY_GUARDIAN_HURT_LAND, 0.75f));

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

            CFUtils.lockArmorStand(me);
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
