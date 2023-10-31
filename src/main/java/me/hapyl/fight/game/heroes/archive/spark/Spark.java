package me.hapyl.fight.game.heroes.archive.spark;

import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.RangeWeapon;
import me.hapyl.fight.util.MetadataValue;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Spark extends Hero implements PlayerElement {

    private final PlayerMap<ArmorStand> markerLocation = PlayerMap.newMap();

    public Spark() {
        super("Spark");

        setArchetype(Archetype.RANGE);

        setDescription("Strikes as fire with his fire abilities.");
        setItem("ade095332720215ca9b85e7eacd1d092b1697fad34d696add94d3b70976702c");

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(Color.ORANGE);
        equipment.setLeggings(Color.RED);
        equipment.setBoots(Color.ORANGE);

        setWeapon(new RangeWeapon(Material.STICK, "fire_weapon") {
            @Override
            public void onHit(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, boolean headshot) {
                entity.setFireTicks(10);
            }

            @Nonnull
            @Override
            public EnumDamageCause getDamageCause(@Nonnull GamePlayer player) {
                return EnumDamageCause.FIRE_SPRAY;
            }
        }.setCooldown(30)
                .setSound(Sound.ENTITY_BLAZE_SHOOT, 1.75f)
                .setParticleHit(new PackedParticle(Particle.LAVA).setAmount(3).setSpeed(0.2f))
                .setParticleTick(new PackedParticle(Particle.FLAME).setSpeed(0.001f))
                .setDamage(8.0d)
                .setName("Fire Sprayer")
                .setDescription("A long range weapon that can shoot fire lasers in front of you! How cool is that..."));

        setUltimate(new UltimateTalent(
                "Run it Back",
                "Instantly place a marker at your current location for {duration}.____Upon death or after duration ends, safely teleport to the marked location with health you had upon activating the ability.",
                80
        ).setDuration(200).setItem(Material.TOTEM_OF_UNDYING).setCooldownSec(40));

    }

    @Override
    public void useUltimate(@Nonnull GamePlayer player) {
        final Location location = getSafeLocation(player.getLocation());
        final double health = player.getHealth();

        setUsingUltimate(player, true);

        final ArmorStand marker = Entities.ARMOR_STAND.spawn(location, me -> {
            me.setInvulnerable(true);
            me.setGravity(false);
            me.setInvisible(true);
            me.setVisible(false);
            me.setFireTicks(getUltimateDuration());
            me.setMetadata("Health", new MetadataValue(health));
        });

        markerLocation.put(player, marker);

        new GameTask() {
            private int tick = getUltimateDuration();

            @Override
            public void run() {
                // if already on rebirth, can happen when damage is called rebirth
                if (getMarker(player) == null) {
                    cancel();
                    return;
                }

                if (tick < 0) {
                    rebirthPlayer(player);
                    cancel();
                    return;
                }

                // display how much time left
                // symbols => ■□
                final StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    builder.append(Chat.format(i >= (tick / 10) ? "&c|" : "&a|"));
                }

                player.sendSubtitle(builder.toString(), 0, 10, 0);

                --tick;

                // Fx
                player.spawnWorldParticle(player.getEyeLocation(), Particle.FLAME, 1, 0.5d, 0.0d, 0.5d, 0.01f);

                // Fx at marker
                player.spawnWorldParticle(location, Particle.LANDING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
                player.spawnWorldParticle(location, Particle.DRIP_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
            }
        }.runTaskTimer(0, 1);

    }

    @Override
    public boolean predicateUltimate(@Nonnull GamePlayer player) {
        return isSafeLocation(player.getLocation());
    }

    @Override
    public String predicateMessage(@Nonnull GamePlayer player) {
        return "Location is not safe!";
    }

    private ArmorStand getMarker(GamePlayer player) {
        return markerLocation.get(player);
    }

    private boolean isSafeLocation(Location location) {
        return getSafeLocation(location) != null;
    }

    private Location getSafeLocation(Location location) {
        // start with a bit of Y offset
        location.add(0, 2, 0);
        for (int i = 0; i < 10; i++) {
            location.subtract(0, 1, 0);
            if (!location.getBlock().getType().isAir()) {
                return location;
            }
        }
        return null;
    }

    public void rebirthPlayer(GamePlayer player) {
        final ArmorStand stand = markerLocation.get(player);

        if (stand == null) {
            return;
        }

        final double health = Math.max(stand.getMetadata("Health").get(0).asDouble(), player.getMinHealth());

        player.setInvulnerable(true);
        player.setHealth(health);

        final Location teleportLocation = stand.getLocation().add(0.0d, 1.0d, 0.0d);

        markerLocation.remove(player);
        stand.remove();

        player.teleport(teleportLocation);

        // Fx
        player.addPotionEffect(PotionEffectType.SLOW, 20, 50);
        player.playWorldSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.5f);
        player.spawnWorldParticle(Particle.FIREWORKS_SPARK, 50, 0.1d, 0.5d, 0.1d, 0.2f);
        player.spawnWorldParticle(Particle.LAVA, 10, 0.1d, 0.5d, 0.1d, 0.2f);
        player.sendTitle("&6🔥", "&eOn Rebirth...", 5, 10, 5);

        GameTask.runLater(() -> player.setInvulnerable(false), 20);
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();
        final EnumDamageCause cause = input.getDamageCause();

        if (!validatePlayer(player) || cause == null) {
            return null;
        }

        boolean validCause = switch (cause) {
            case FIRE, FIRE_TICK, LAVA -> true;
            default -> false;
        };

        // Check for ultimate death
        if (isUsingUltimate(player) && input.getDamage() >= player.getHealth()) {
            rebirthPlayer(player);
            setUsingUltimate(player, false);

            return DamageOutput.CANCEL;
        }

        // Cancel any fire damage
        if (validCause) {
            player.setFireTicks(0);
            return DamageOutput.CANCEL;
        }
        return null;
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 1);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.SPARK_MOLOTOV.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SPARK_FLASH.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.FIRE_GUY.getTalent();
    }
}
