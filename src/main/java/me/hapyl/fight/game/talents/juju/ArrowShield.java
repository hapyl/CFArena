package me.hapyl.fight.game.talents.juju;


import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ArrowShield extends Talent implements Listener {

    private final PlayerMap<List<Display>> shieldMap = PlayerMap.newMap();

    @DisplayField private final short shieldCharges = 5;
    @DisplayField private final double explosionRadius = 2.0d;
    @DisplayField private final double explosionDamage = 5.0d;
    @DisplayField private final int poisonDuration = Tick.fromSecond(3);
    @DisplayField private final short poisonStrength = 2;

    private final DisplayData model;

    public ArrowShield(@Nonnull Key key) {
        super(key, "Arrow Shield");

        setDescription("""
                Creates a &eshield&7 of arrows for {duration} that blocks &nany&7 damage.
                
                When hit, an arrow triggers a rapid &4explosion&7 in small &cAoE&7, dealing &cdamage&7, applying &2poison&7, and reducing %s.
                """.formatted(AttributeType.DEFENSE)
        );

        model = BDEngine.parse(
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0039f,-0.0039f,0.2228f,-0.0951f,0.0498f,-0.0985f,0f,0.6758f,0.0843f,0.0842f,0.0106f,0.0267f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.1876f,0f,0f,-0.1888f,0f,-0.4282f,0f,0.6804f,0f,0f,-0.1904f,0.1267f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:soul_torch\",Properties:{}},transformation:[0f,0f,0.1841f,-0.1869f,0f,0.4727f,0f,0.2368f,-0.1869f,0f,0f,0.125f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.1876f,0f,0f,-0.1888f,0f,-0.2397f,0f,0.2656f,0f,0f,-0.1904f,0.1267f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0039f,-0.0039f,-0.2228f,-0.0951f,0.0498f,-0.0985f,0f,0.6758f,-0.0843f,-0.0842f,0.0106f,0.0375f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[0.0835f,0.0834f,0.0105f,-0.1005f,0.0498f,-0.0985f,0f,0.6758f,0.004f,0.004f,-0.2249f,0.0321f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0836f,-0.0832f,0.0105f,-0.0897f,0.0497f,-0.0987f,0f,0.6758f,0.004f,0.004f,0.2249f,0.0321f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0256f,0f,0f,-0.1079f,0f,-0.0478f,0f,0.5196f,0f,0f,-0.0259f,0.044f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0237f,0f,0f,-0.1079f,0f,-0.02f,0f,0.5392f,0f,0f,-0.0181f,0.0438f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_concrete_powder\",Properties:{}},transformation:[0.0237f,0f,0f,-0.1084f,0f,-0.02f,0f,0.5097f,0f,0f,-0.021f,0.0443f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0237f,0f,0f,-0.1061f,0f,-0.02f,0f,0.4745f,0f,0f,-0.0181f,0.0438f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:ghast_tear\",Count:1},item_display:\"none\",transformation:[0.1379f,0f,0f,-0.0943f,0f,-0.2149f,0f,0.1399f,0f,0f,-0.1941f,0.0329f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:ghast_tear\",Count:1},item_display:\"none\",transformation:[0f,0f,0.1923f,-0.0943f,0f,-0.2149f,0f,0.1399f,0.1392f,0f,0f,0.0329f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[0f,0f,0.1339f,-0.0941f,-0.0758f,-0.071f,0f,0.2167f,0.0848f,-0.092f,0f,0.0308f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[0f,0f,-0.1339f,-0.0941f,-0.0754f,-0.0714f,0f,0.2167f,-0.0852f,0.0916f,0f,0.0334f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[-0.084f,0.0912f,0f,-0.0928f,-0.0758f,-0.071f,0f,0.2167f,0f,0f,0.1352f,0.0326f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[0.0844f,-0.0907f,0f,-0.0949f,-0.0754f,-0.0714f,0f,0.2167f,0f,0f,-0.1352f,0.0326f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0256f,0f,0f,-0.1079f,0f,-0.0179f,0f,0.2783f,0f,0f,-0.0259f,0.044f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0119f,0f,0f,-0.1079f,0f,-0.0362f,0f,0.308f,0f,0f,-0.017f,0.044f,0f,0f,0f,1f]}]}"
        );

        setType(TalentType.DEFENSE);
        setItem(Material.STRING);
        setDurationSec(15);
        setCooldownSec(40);
    }

    public int getCharges(GamePlayer player) {
        return getArrows(player).size();
    }

    public void removeCharge(GamePlayer player) {
        final List<Display> list = getArrows(player);
        final int sizeMinusOne = list.size() - 1;

        if (sizeMinusOne <= 0) {
            player.sendMessage("&a🛡 Your shield has broke!");
            shieldMap.remove(player);
        }

        final Display arrow = list.remove(sizeMinusOne);

        createExplosion(player, arrow.getLocation());
        arrow.remove();
    }

    public void createExplosion(GamePlayer player, Location location) {
        final List<LivingGameEntity> livingEntities = Collect.nearbyEntities(location, explosionRadius, lv -> !lv.equals(player));

        livingEntities.forEach(entity -> {
            Temper.POISON_IVY.temper(entity, AttributeType.DEFENSE, -0.2d, poisonDuration, player);

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
        final List<Display> list = getArrows(player);
        removeArrows(player);

        for (int i = 0; i < shieldCharges; i++) {
            list.add(model.spawnInterpolated(player.getLocation()));
        }

        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
        player.playWorldSound(Sound.ITEM_SHIELD_BLOCK, 1.25f);

        shieldMap.put(player, list);

        new GameTask() {
            private int tick = getDuration();
            private double theta = 0.0d;

            @Override
            public void run() {
                final List<Display> arrows = getArrows(player);

                if (tick-- <= 0 || arrows.isEmpty() || player.isDead()) {
                    if (!arrows.isEmpty()) {
                        player.sendMessage("&a🛡 Your shield has run out!");
                        removeArrows(player);
                    }

                    cancel();
                    return;
                }

                final Location location = player.getLocation().add(0, 0.2, 0);
                location.setYaw(0.0f);
                location.setPitch(0.0f);

                final double offset = ((Math.PI * 2) / Math.max(arrows.size(), 1));

                int pos = 1;
                for (final Display arrow : arrows) {
                    final double x = 1.25d * Math.sin(theta + offset * pos);
                    final double y = Math.cos(Math.toRadians(tick * pos)) * 0.2d;
                    final double z = 1.25d * Math.cos(theta + offset * pos);

                    location.add(x, y, z);
                    arrow.teleport(location);

                    // Fx
                    player.spawnWorldParticle(location, Particle.TOTEM_OF_UNDYING, 1, 0, 0, 0, 0.05f);

                    location.subtract(x, y, z);
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
    public void onStop(@Nonnull GameInstance instance) {
        shieldMap.keySet().forEach(this::removeArrows);
        shieldMap.clear();
    }

    private List<Display> getArrows(GamePlayer player) {
        return this.shieldMap.getOrDefault(player, new ArrayList<>());
    }

    private void removeArrows(GamePlayer player) {
        final List<Display> arrows = getArrows(player);

        arrows.forEach(entity -> {
            player.spawnWorldParticle(entity.getLocation(), Particle.POOF, 3, 0, 0, 0, 0.01f);
            entity.remove();
        });

        arrows.clear();
    }

}
