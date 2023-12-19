package me.hapyl.fight.game.heroes.archive.pytaria;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.pytaria.FlowerBreeze;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Pytaria extends Hero {

    private final int healthRegenPercent = 40;
    private final double CRIT_MULTIPLIER = 8.0d;
    private final double ATTACK_MULTIPLIER = 6.5d;

    public Pytaria() {
        super("Pytaria");

        setArchetype(Archetype.DAMAGE);

        setDescription(
                "Beautiful, but deadly opponent with addiction to flowers. She suffered all her youth, which, in the end, only made her stronger."
        );
        setItem("7bb0752f9fa87a693c2d0d9f29549375feb6f76952da90d68820e7900083f801");

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.MAX_HEALTH, 125.0d);
        attributes.set(AttributeType.ATTACK, 0.9d);
        attributes.set(AttributeType.CRIT_CHANCE, 0.2d);
        attributes.set(AttributeType.CRIT_DAMAGE, 0.4d);

        setWeapon(new Weapon(Material.ALLIUM).setName("Annihilallium").setDamage(8.0).setDescription("A beautiful flower, nothing more."));

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(222, 75, 85);
        equipment.setLeggings(54, 158, 110, TrimPattern.SILENCE, TrimMaterial.IRON);
        equipment.setBoots(179, 204, 204, TrimPattern.SILENCE, TrimMaterial.IRON);

        setUltimate(new UltimateTalent("Feel the Breeze", 60)
                .setCooldownSec(50)
                .setDuration(60)
                .appendDescription("""
                        Summon a blooming &6Bee&7 in front of &aPytaria&7.
                                        
                        The Bee will lock on the closest enemy and charge for {cast}.
                                                
                        Once charged, the &6Bee&7 creates an explosion at the locked location, dealing damage in small &eAoE&7.
                                                
                        Also regenerates &c%s%% ❤&7 of &aPytaria's&7 missing health.
                        """, healthRegenPercent)
                .appendAttributeDescription("Health Regeneration", healthRegenPercent)
                .setCastDuration(40)
                .setSound(Sound.ENTITY_BEE_DEATH, 0.0f)
                .setTexture("d4579f1ea3864269c2148d827c0887b0c5ed43a975b102a01afb644efb85ccfd"));
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector vector = location.getDirection();

        location.add(vector.setY(0).multiply(5));
        location.add(0, 7, 0);

        final Bee bee = Entities.BEE.spawn(location, me -> {
            me.setSilent(true);
            me.setAI(false);
        });

        final double finalDamage = calculateDamage(player, 50.0d, EnumDamageCause.FEEL_THE_BREEZE);
        final LivingGameEntity entity = Collect.nearestEntityPrioritizePlayers(location, 50, check -> !check.equals(player));

        PlayerLib.playSound(location, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.0f);

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= getUltimate().getCastDuration()) {
                    cancel();
                    return;
                }

                final Location lockLocation = entity == null ? location.clone().subtract(0, 9, 0) : entity.getEyeLocation();
                drawLine(location.clone(), lockLocation.clone());
            }
        }.runTaskTimer(0, 1);

        return new UltimateCallback() {
            @Override
            public void callback(@Nonnull GamePlayer player) {
                final Location lockLocation = entity == null ? location.clone().subtract(0, 9, 0) : entity.getEyeLocation();
                bee.remove();

                Collect.nearbyEntities(lockLocation, 1.5d).forEach(victim -> {
                    victim.damage(finalDamage, player, EnumDamageCause.FEEL_THE_BREEZE);
                });

                // Heal
                final double health = player.getHealth();
                final double maxHealth = player.getMaxHealth();
                final double healingAmount = (maxHealth - health) * healthRegenPercent / maxHealth;

                player.heal(healingAmount);
                player.sendMessage("&6🐝 &aHealed for &c&l%s&c❤&a!", healingAmount);

                // Fx
                PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);

                PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 5, 0.2, 0.2, 0.2, 0.1f);
                PlayerLib.playSound(location, Sound.ENTITY_BEE_DEATH, 1.5f);

                PlayerLib.spawnParticle(lockLocation, Particle.EXPLOSION_LARGE, 3, 0.5, 0, 0.5, 0);
                PlayerLib.playSound(lockLocation, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.25f);
            }
        };
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.PYTARIA.getAlivePlayers().forEach(gamePlayer -> {
                    recalculateStats(gamePlayer);
                });
            }
        }.runTaskTimer(0, 5);
    }

    public void recalculateStats(@Nonnull GamePlayer gamePlayer) {
        final EntityAttributes attributes = gamePlayer.getAttributes();

        final double maxHealth = gamePlayer.getMaxHealth();
        final double health = gamePlayer.getHealth();
        final double factor = (maxHealth - health) / maxHealth / 10;

        final double critIncrease = CRIT_MULTIPLIER * factor;
        final double attackIncrease = ATTACK_MULTIPLIER * factor;

        attributes.reset(AttributeType.CRIT_CHANCE);
        attributes.reset(AttributeType.ATTACK);
        attributes.reset(AttributeType.DEFENSE);

        attributes.addSilent(AttributeType.CRIT_CHANCE, critIncrease);
        attributes.addSilent(AttributeType.ATTACK, attackIncrease);
        attributes.subtractSilent(AttributeType.DEFENSE, critIncrease);
    }

    // This is needed for "snapshot" damage.
    public double calculateDamage(@Nonnull GamePlayer player, double damage, @Nonnull EnumDamageCause cause) {
        return player.getAttributes().calculateOutgoingDamage(damage, cause).damage();
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.FLOWER_ESCAPE.getTalent();
    }

    @Override
    public FlowerBreeze getSecondTalent() {
        return (FlowerBreeze) Talents.FLOWER_BREEZE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.EXCELLENCY.getTalent();
    }

    private void drawLine(Location start, Location end) {
        final World world = start.getWorld();
        final Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(0.5d);
        final double distance = start.distance(end);

        for (double i = 0.0D; i < distance; i += 0.5) {
            start.add(vector);

            if (world == null) {
                return;
            }

            if (!start.getBlock().getType().isAir()) {
                final Location cloned = start.add(0, 0.15, 0);
                world.spawnParticle(Particle.FLAME, cloned, 3, 0.1, 0.1, 0.1, 0.02);

                return;
            }

            world.spawnParticle(Particle.REDSTONE, start, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.5f));
        }
    }

}
