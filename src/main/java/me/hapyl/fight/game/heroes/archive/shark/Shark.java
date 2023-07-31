package me.hapyl.fight.game.heroes.archive.shark;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Shark extends Hero implements Listener {

    private final double CRITICAL_AMOUNT = 15.0d;

    public Shark() {
        super(
                "Shark",
                "Strong warrior from the &3Depth of Waters&8&o... not well versed in on-land fights but don't let it touch the water, or you'll regret it."
        );

        setRole(Role.STRATEGIST);
        setArchetype(Archetype.STRATEGY);

        setItem("3447e7e8271f573969f2da734c4125f93b2864fb51db69da5ecba7487cf882b0");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(116, 172, 204);
        equipment.setLeggings(116, 172, 204);
        equipment.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(116, 172, 204))
                .addEnchant(Enchantment.DEPTH_STRIDER, 5)
                .cleanToItemSack());

        setWeapon(new Weapon(Material.QUARTZ)
                .setName("Claws")
                .setDescription("Using one's claws is a better idea than using a stick, don't you think so?")
                .setDamage(7.0d));

        setUltimate(new UltimateTalent(
                "Ocean Madness",
                "Creates a &bShark Aura &7that follow you for {duration} and imitates water.",
                70
        ).setItem(Material.WATER_BUCKET).setDuration(120).setSound(Sound.AMBIENT_UNDERWATER_ENTER, 0.0f).setCooldownSec(60));
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
        if (!validatePlayer(player) || isUsingUltimate(player)) {
            return;
        }

        setState(player, player.isInWater(), 10);
    }

    public void setState(Player player, boolean state, int duration) {
        if (state) {
            player.setWalkSpeed(0.4f);
            PlayerLib.addEffect(player, EffectType.STRENGTH, duration, 2);
            PlayerLib.addEffect(player, EffectType.RESISTANCE, duration, 1);
        }
        else {
            player.setWalkSpeed(0.2f);
        }
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getBukkitPlayer();
        final GameEntity entity = input.getDamager();
        if (player.hasCooldown(getPassiveTalent().getMaterial()) || entity == null || entity.is(player)) {
            return null;
        }

        if (input.isCrit()) {
            GamePlayer.setCooldown(player, getPassiveTalent().getMaterial(), 20 * 5);
            performCriticalHit(input.getPlayer(), entity);
        }

        return null;
    }

    public void performCriticalHit(GamePlayer player, GameEntity entity) {
        final EvokerFangs fangs = Entities.EVOKER_FANGS.spawn(entity.getLocation());
        fangs.setOwner(player.getPlayer());

        // Sync for effect only
        GameTask.runTaskTimerTimes(r -> fangs.teleport(entity.getLocation()), 0, 1, 30);

        // Perform critical hit and heal the player
        entity.damage(CRITICAL_AMOUNT, player, EnumDamageCause.FEET_ATTACK);
        player.heal(CRITICAL_AMOUNT);
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
