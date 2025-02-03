package me.hapyl.fight.game.heroes.ronin;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.TalentPreconditionEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Map;

public class RoninWeapon extends Weapon {

    private final Map<GamePlayer, Deflect> deflectMap;
    private final DisplayData displayData;

    public RoninWeapon() {
        super(Material.IRON_SWORD, Key.ofString("kensei"));

        setName("Kensei");
        setDamage(8.0d);

        setAbility(AbilityType.RIGHT_CLICK, new DeflectAbility());

        deflectMap = Maps.newHashMap();
        displayData = BDEngine.parse(
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:iron_sword\",Count:1},item_display:\"none\",transformation:[-0.9659f,0f,0.2588f,0f,-0.067f,0.9659f,-0.25f,0.5f,-0.25f,-0.2588f,-0.933f,0f,0f,0f,0f,1f]}]}"
        );
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        deflectMap.values().forEach(Deflect::cancel);
        deflectMap.clear();
    }

    public class DeflectAbility extends Ability implements Listener {

        @DisplayField private final int cooldownIfDeflected = Tick.fromSecond(5);
        @DisplayField private final double deflectDamageRadius = 2.0d;

        public DeflectAbility() {
            super("Deflect", """
                    Enter a &3defensive stance&7, &nblocking&7 incoming attacks with you katana.
                    
                    Deflect blocked attack towards the attacker, dealing &cdamage&7.
                    &8&o;;Successful deflects reduces the cooldown of this ability.
                    """);

            setDurationSec(2);
            setCooldownSec(20);

            CF.registerEvents(this);
        }

        @EventHandler
        public void handleTalentPreconditionEvent(TalentPreconditionEvent ev) {
            final GamePlayer player = ev.getPlayer();

            if (deflectMap.containsKey(player)) {
                ev.setCancelled(true, "Unable to use this while deflecting!");
            }
        }

        @EventHandler
        public void handleGameDamageEvent(GameDamageEvent ev) {
            if (!(ev.getEntity() instanceof GamePlayer player)) {
                return;
            }

            final GameEntity damager = ev.getDamager();

            if (damager == null) {
                return;
            }

            // Check dot
            final Location location = player.getEyeLocation();
            final Vector vector = damager.getEyeLocation().subtract(location).toVector().normalize();
            final double dot = location.getDirection().dot(vector);

            if (dot < 0.6d) {
                return;
            }

            final Deflect deflect = stopDeflecting(player);

            if (deflect == null) {
                return;
            }

            ev.setCancelled(true);

            final double damage = ev.getDamage();
            final long deflectDifference = System.currentTimeMillis() - deflect.startedDeflectingAt;

            final DeflectAbility ability = deflect.ability;

            final int maxDurationInMillis = ability.getDuration() * 50;
            final double deflectDamage = damage * Math.max(1.0d, 0.5d + (double) (maxDurationInMillis - deflectDifference) / 1000);

            // Reset cooldown
            ability.startCooldown(player, cooldownIfDeflected);

            Collect.nearbyEntities(player.getLocationInFrontFromEyes(0.75d), ability.deflectDamageRadius, player::isNotSelfOrTeammate)
                    .forEach(entity -> {
                        entity.damageNoKnockback(deflectDamage, player, EnumDamageCause.DEFLECT);

                        // Fx
                    });

            // Fx
            player.playWorldSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.25f);
            player.swingMainHand();

            player.spawnWorldParticle(location, Particle.GUST, 1);

            // Achievement
            Registries.getAchievements().RONIN_DEFLECT.complete(player);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            if (deflectMap.containsKey(player)) {
                return Response.AWAIT;
            }

            deflectMap.put(player, new Deflect(player, this));

            // Fx
            player.playWorldSound(Sound.BLOCK_BELL_RESONATE, 1.75f);
            player.setAttributeValue(Attribute.ATTACK_SPEED, -999);

            return Response.AWAIT;
        }
    }

    private Deflect stopDeflecting(@Nonnull GamePlayer player) {
        final Deflect deflect = deflectMap.remove(player);

        if (deflect == null) {
            return null;
        }

        deflect.cancel();
        deflect.entity.remove();
        deflect.ability.startCooldown(player);

        player.resetAttributeValue(Attribute.ATTACK_SPEED);
        return deflect;
    }

    private class Deflect extends TickingGameTask {
        private final GamePlayer player;
        private final DeflectAbility ability;
        private final DisplayEntity entity;
        private final long startedDeflectingAt;

        private Deflect(GamePlayer player, DeflectAbility ability) {
            this.player = player;
            this.ability = ability;
            this.entity = displayData.spawnInterpolated(getDisplayLocation());
            this.startedDeflectingAt = System.currentTimeMillis();

            runTaskTimer(0, 1);
        }

        @Override
        public void run(int tick) {
            if (tick >= this.ability.getDuration()) {
                stopDeflecting(this.player);
                return;
            }

            // Sync entity
            this.entity.teleport(getDisplayLocation());

            // Add slowness
            this.player.addEffect(Effects.MOVEMENT_CONTAINMENT, 2, true);
        }

        private Location getDisplayLocation() {
            final Location location = this.player.getLocation();
            location.setPitch(0.0f);

            final Vector vector = location.getDirection();
            vector.setY(0.0d);

            location.add(vector.multiply(0.5f));
            location.add(0, 0.75d, 0);

            return location;
        }

    }
}
