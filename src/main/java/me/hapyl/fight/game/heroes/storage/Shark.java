package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Shark extends Hero implements Listener {

    public Shark() {
        super(
                "Shark",
                "Strong warrior from the &bDepth of Waters&7... not well versed in on-land fights but don't let it touch the water or you'll regret it."
        );

        setRole(Role.STRATEGIST);

        setItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0N2U3ZTgyNzFmNTczOTY5ZjJkYTczNGM0MTI1ZjkzYjI4NjRmYjUxZGI2OWRhNWVjYmE3NDg3Y2Y4ODJiMCJ9fX0="
        );

        final ClassEquipment equipment = getEquipment();
        equipment.setHelmet(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0N2U3ZTgyNzFmNTczOTY5ZjJkYTczNGM0MTI1ZjkzYjI4NjRmYjUxZGI2OWRhNWVjYmE3NDg3Y2Y4ODJiMCJ9fX0="
        );
        equipment.setChestplate(116, 172, 204);
        equipment.setLeggings(116, 172, 204);
        equipment.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(116, 172, 204))
                .addEnchant(Enchantment.DEPTH_STRIDER, 5)
                .cleanToItemSack());

        setWeapon(new Weapon(Material.QUARTZ)
                .setName("Claws")
                .setDescription("Using one's claws is the better idea than using a stick, don't you think so?")
                .setDamage(7.0d));

        setUltimate(new UltimateTalent(
                "Ocean Madness",
                "Creates a &bShark Aura &7that follow you for {duration} and imitates water.",
                70
        ).setItem(Material.WATER_BUCKET).setDuration(120).setSound(Sound.AMBIENT_UNDERWATER_ENTER, 0.0f).setCdSec(60));

    }

    @Override
    public void useUltimate(Player player) {
        setState(player, true, getUltimateDuration());

        new GameTask() {
            private int tick = getUltimateDuration();

            @Override
            public void run() {
                if (tick < 0) {
                    setState(player, false, 0);
                    this.cancel();
                    return;
                }

                final Location location = player.getLocation();

                // Fx
                Geometry.drawCircle(location, 3.5d, Quality.HIGH, new WorldParticle(Particle.WATER_DROP));
                Geometry.drawCircle(location, 1.0d, Quality.VERY_HIGH, new WorldParticle(Particle.WATER_SPLASH));

                --tick;
            }
        }.runTaskTimer(0, 1);

    }

    @EventHandler()
    public void handlePlayerMove(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        if (!validatePlayer(player, Heroes.SHARK) || isUsingUltimate(player)) {
            return;
        }

        setState(player, player.isInWater(), 10);
    }

    public void setState(Player player, boolean state, int duration) {
        if (state) {
            player.setWalkSpeed(0.6f);
            PlayerLib.addEffect(player, EffectType.STRENGTH, duration, 2);
            PlayerLib.addEffect(player, EffectType.RESISTANCE, duration, 1);
        }
        else {
            player.setWalkSpeed(0.2f);
        }
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final LivingEntity entity = input.getEntity();
        if (player.hasCooldown(getPassiveTalent().getMaterial()) || entity == null || entity == player) {
            return null;
        }

        if (Math.random() >= 0.9) {
            player.setCooldown(getPassiveTalent().getMaterial(), 20 * 5);
            performCriticalHit(player, entity);
        }

        return null;
    }

    public void performCriticalHit(Player player, LivingEntity entity) {
        final EvokerFangs fangs = Entities.EVOKER_FANGS.spawn(entity.getLocation());
        fangs.setOwner(player);

        // Sync for effect only
        GameTask.runTaskTimerTimes(r -> fangs.teleport(entity.getLocation()), 0, 1, 30);
        GamePlayer.damageEntity(entity, 5.0d, player, EnumDamageCause.FEET_ATTACK);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.SUBMERGE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.WHIRLPOOL.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.CLAW_CRITICAL.getTalent();
    }
}
