package me.hapyl.fight.game.talents.juju;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ArrowShield extends Talent implements Listener {

    private final PlayerMap<List<Arrow>> shieldMap = PlayerMap.newMap();

    @DisplayField private final short shieldCharges = 5;
    @DisplayField private final double explosionRadius = 2.0d;
    @DisplayField private final double explosionDamage = 5.0d;
    @DisplayField private final int poisonDuration = Tick.fromSecond(3);
    @DisplayField private final short poisonStrength = 2;

    public ArrowShield() {
        super("Arrow Shield");

        setDescription("""
                Creates a &eshield&7 of arrows for {duration} that blocks &nany&7 damage.
                                
                When hit, an arrow triggers a rapid &4explosion&7 in small &cAoE&7, dealing &cdamage&7, applying &2poison&7, and reducing %s.
                """, AttributeType.DEFENSE);

        setType(TalentType.DEFENSE);
        setItem(Material.STRING);
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

    public int getCharges(GamePlayer player) {
        return getArrows(player).size();
    }

    public void removeCharge(GamePlayer player) {
        final List<Arrow> list = getArrows(player);
        final int sizeMinusOne = list.size() - 1;

        if (sizeMinusOne <= 0) {
            player.sendMessage("&a🛡 Your shield has broke!");
            shieldMap.remove(player);
        }

        final Arrow arrow = list.remove(sizeMinusOne);

        createExplosion(player, arrow.getLocation());
        arrow.remove();
    }

    public void createExplosion(GamePlayer player, Location location) {
        final List<LivingGameEntity> livingEntities = Collect.nearbyEntities(location, explosionRadius, lv -> !lv.equals(player));

        livingEntities.forEach(entity -> {
            Temper.POISON_IVY.temper(entity.getAttributes(), AttributeType.DEFENSE, -0.2d, poisonDuration);

            entity.damage(explosionDamage, player, EnumDamageCause.POISON_IVY);
            entity.addEffect(Effects.POISON, poisonStrength, poisonDuration);
        });

        // Fx
        player.spawnWorldParticle(location, Particle.TOTEM_OF_UNDYING, 25, 0, 0, 0, 0.75f);
        player.spawnWorldParticle(location, Particle.HAPPY_VILLAGER, 5, 0.25d, 0.25d, 0.25d, 0.0f);

        player.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 0.75f);
        player.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 1.25f);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
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

        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
        player.playWorldSound(Sound.ITEM_SHIELD_BLOCK, 1.25f);

        shieldMap.put(player, list);

        new GameTask() {
            private int tick = getDuration();
            private double theta = 0.0d;

            @Override
            public void run() {
                final List<Arrow> arrows = getArrows(player);

                if (tick-- <= 0 || arrows.isEmpty() || player.isDead()) {
                    if (!arrows.isEmpty()) {
                        player.sendMessage("&a🛡 Your shield has run out!");
                        removeArrows(player);
                    }

                    cancel();
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
                    arrow.teleport(location);

                    // Fx
                    player.spawnWorldParticle(location, Particle.TOTEM_OF_UNDYING, 1, 0, 0, 0, 0.05f);

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
    public void onDeath(@Nonnull GamePlayer player) {
        shieldMap.keySet().forEach(this::removeArrows);
        shieldMap.remove(player);
    }

    @Override
    public void onStop() {
        shieldMap.keySet().forEach(this::removeArrows);
        shieldMap.clear();
    }

    private List<Arrow> getArrows(GamePlayer player) {
        return this.shieldMap.getOrDefault(player, new ArrayList<>());
    }

    private void removeArrows(GamePlayer player) {
        final List<Arrow> arrows = getArrows(player);

        arrows.forEach(entity -> {
            player.spawnWorldParticle(entity.getLocation(), Particle.POOF, 3, 0, 0, 0, 0.01f);
            entity.remove();
        });

        arrows.clear();
    }

}
