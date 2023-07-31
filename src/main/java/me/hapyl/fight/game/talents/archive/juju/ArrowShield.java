package me.hapyl.fight.game.talents.archive.juju;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Temper;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrowShield extends Talent implements Listener {

    // should really be linkedlist
    private final Map<Player, List<Arrow>> shieldMap = new HashMap<>();

    @DisplayField private final short shieldCharges = 5;
    @DisplayField private final double explosionRadius = 2.0d;
    @DisplayField private final double explosionDamage = 5.0d;
    @DisplayField private final int poisonDuration = Tick.fromSecond(3);

    private final PotionEffect effect = PotionEffectType.POISON.createEffect(poisonDuration, 2);

    public ArrowShield() {
        super("Arrow Shield");

        setItem(Material.STRING);

        setDescription("""
                Creates a shield of arrows for {duration} that blocks any damage.
                                
                When hit, an arrow triggers a rapid explosion in small AoE, dealing damage, applying poison, and reducing %s.
                """, AttributeType.DEFENSE);

        setDurationSec(15);
        setCooldownSec(40);
    }

    @EventHandler()
    public void handleProjectileHit(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow) {
            if (arrow.getScoreboardTags().contains("FAKE_ARROW")) {
                ev.setCancelled(true);
            }
        }
    }

    public int getCharges(Player player) {
        return getArrows(player).size();
    }

    public void removeCharge(Player player) {
        final List<Arrow> list = getArrows(player);
        final int sizeMinusOne = list.size() - 1;

        if (sizeMinusOne <= 0) {
            Chat.sendMessage(player, "&a🛡 Your shield has broke!");
            shieldMap.remove(player);
        }

        final Arrow arrow = list.remove(sizeMinusOne);

        createExplosion(player, arrow.getLocation());
        arrow.remove();
    }

    public void createExplosion(Player player, Location location) {
        final List<GameEntity> livingEntities = Collect.nearbyEntities(location, explosionRadius, lv -> lv.isNot(player));

        livingEntities.forEach(entity -> {
            if (entity instanceof Player target) {
                final GamePlayer gamePlayer = CF.getOrCreatePlayer(player);
                gamePlayer.getAttributes().decreaseTemporary(Temper.POISON_IVY, AttributeType.DEFENSE, 0.2d, poisonDuration);
            }

            entity.addPotionEffect(effect);
            entity.damage(explosionDamage, CF.getPlayer(player), EnumDamageCause.POISON_IVY);
        });

        // Fx
        PlayerLib.spawnParticle(location, Particle.TOTEM, 25, 0, 0, 0, 0.75f);
        PlayerLib.spawnParticle(location, Particle.VILLAGER_HAPPY, 5, 0.25d, 0.25d, 0.25d, 0.0f);

        PlayerLib.playSound(location, Sound.ENCHANT_THORNS_HIT, 0.75f);
        PlayerLib.playSound(location, Sound.ENCHANT_THORNS_HIT, 1.25f);
    }

    @Override
    public Response execute(Player player) {
        final List<Arrow> list = getArrows(player);
        removeArrows(player);

        for (int i = 0; i < shieldCharges; i++) {
            list.add(Entities.ARROW.spawn(player.getLocation(), me -> {
                me.setSilent(true);
                me.setGravity(false);
                me.setDamage(0.0d);
                me.setCritical(false);
                me.addScoreboardTag("FAKE_ARROW");
            }));
        }

        PlayerLib.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
        PlayerLib.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.25f);
        shieldMap.put(player, list);

        new GameTask() {
            private int tick = getDuration();
            private double theta = 0.0d;

            @Override
            public void run() {
                final List<Arrow> arrows = getArrows(player);

                if (tick-- <= 0 || arrows.isEmpty() || GamePlayer.getPlayer(player).isDead()) {
                    if (!arrows.isEmpty()) {
                        Chat.sendMessage(player, "&a🛡 Your shield has run out!");
                        removeArrows(player);
                    }
                    this.cancel();
                    return;
                }

                final Location location = player.getLocation();
                location.setYaw(0.0f);
                location.setPitch(-90.0f);
                final double offset = ((Math.PI * 2) / Math.max(arrows.size(), 1));

                int pos = 1;
                for (final Arrow arrow : arrows) {
                    final double x = 1.25d * Math.sin(theta + offset * pos);
                    final double z = 1.25d * Math.cos(theta + offset * pos);
                    location.add(x, 1, z);

                    //arrow.setVelocity(new Vector(0.0d, 0.1d, 0.0d));
                    arrow.teleport(location);

                    // Fx
                    PlayerLib.spawnParticle(location, Particle.TOTEM, 1, 0, 0, 0, 0.05f);

                    location.subtract(x, 1, z);
                    ++pos;
                }

                theta += Math.PI / 20;
                if (theta >= Math.PI * 2) {
                    theta = 0;
                }
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }

    @Override
    public void onDeath(Player player) {
        shieldMap.keySet().forEach(this::removeArrows);
        shieldMap.remove(player);
    }

    @Override
    public void onStop() {
        shieldMap.keySet().forEach(this::removeArrows);
        shieldMap.clear();
    }

    private List<Arrow> getArrows(Player player) {
        return this.shieldMap.getOrDefault(player, new ArrayList<>());
    }

    private void removeArrows(Player player) {
        final List<Arrow> arrows = getArrows(player);
        arrows.forEach(entity -> {
            PlayerLib.spawnParticle(entity.getLocation(), Particle.EXPLOSION_NORMAL, 3, 0, 0, 0, 0.01f);
            entity.remove();
        });
        arrows.clear();
    }

}
