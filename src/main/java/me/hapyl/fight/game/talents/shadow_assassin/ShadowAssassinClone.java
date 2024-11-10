package me.hapyl.fight.game.talents.shadow_assassin;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public class ShadowAssassinClone extends ShadowAssassinTalent implements Listener {

    @DisplayField protected final double cloneDamage = 10.0d;
    @DisplayField protected final double defenseReduction = 0.4d;
    @DisplayField protected final int defenseReductionDuration = Tick.fromSecond(6);
    @DisplayField protected final short cloneLimit = 3;
    @DisplayField protected final short energyRegen = 25;

    @DisplayField protected final short furyCloneLimit = 3;
    @DisplayField protected final double furyCloneDamage = cloneDamage * 2.5d;
    @DisplayField protected final double furyCloneRadius = 10.0d;

    private final PlayerMap<PlayerCloneList> clones = PlayerMap.newMap();

    public ShadowAssassinClone(@Nonnull Key key) {
        super(key, "Shadow Clone");

        setType(TalentType.IMPAIR);
        setItem(Material.DRAGON_EGG);

        setTalents(new Stealth(), new Fury(50));
    }

    @EventHandler()
    public void handleDamage(GameDamageEvent ev) {

    }

    @Nonnull
    public PlayerCloneList getPlayerClones(@Nonnull GamePlayer player) {
        return clones.computeIfAbsent(player, fn -> new PlayerCloneList(this, player));
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        clones.values().forEach(PlayerCloneList::disappearAll);
        clones.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final PlayerCloneList playerClones = clones.remove(player);

        if (playerClones != null) {
            playerClones.disappearAll();
        }
    }

    private class Stealth extends StealthTalent {

        public Stealth() {
            super(ShadowAssassinClone.this);

            setDescription("""
                    Summon a &8Shadow Clone&7 at your current location.
                                        
                    The clone waits patiently for an &cenemy&7 to come close before &cattacking&7, reducing their %s and &cdisappearing&7.
                                        
                    Before the clone disappears, you can &bteleport&7 to it, &nregenerating&7 %s{energyRegen} %s.

                    &8;;Up to {cloneLimit} clones can exist at the same time.
                    """.formatted(AttributeType.DEFENSE, Named.SHADOW_ENERGY.getColor(), Named.SHADOW_ENERGY));
            setCooldownSec(16);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final PlayerCloneList playerClones = getPlayerClones(player);

            playerClones.createClone(player.getLocationAnchored()).schedule();
            return Response.OK;
        }
    }

    private class Fury extends FuryTalent {

        public Fury(int furyCost) {
            super(ShadowAssassinClone.this, furyCost);

            setDescription("""
                    Summon three &8Shadow Clones&7 behind nearby &cenemies&7.
                                        
                    Each clone instantly attacks the enemy, dealing &cdamage&7 and reducing %s.
                                        
                    &8;;A single clone can target at most one enemy.
                    &8;;Any existing clones will disappear.
                    """.formatted(AttributeType.DEFENSE));

            setCooldownSec(20);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final PlayerCloneList clones = getPlayerClones(player);

            final Location location = player.getLocation();
            final List<LivingGameEntity> entities = Lists.newArrayList();

            Collect.nearbyEntities(location, furyCloneRadius).forEach(entity -> {
                if (player.isSelfOrTeammate(entity)) {
                    return;
                }

                entities.add(entity);
            });

            final List<LivingGameEntity> toHitEntities = entities.stream()
                    .sorted((o1, o2) -> {
                        final double distance1 = o1.getLocation().distance(location);
                        final double distance2 = o2.getLocation().distance(location);

                        return distance2 > distance1 ? 1 : distance1 < distance2 ? -1 : 0;
                    })
                    .limit(furyCloneLimit).toList();

            entities.clear();

            if (toHitEntities.isEmpty()) {
                return Response.error("No valid targets!");
            }

            clones.disappearAll();

            toHitEntities.forEach(entity -> {
                final Location entityLocation = entity.getLocation();
                final Vector vector = entityLocation.getDirection().setY(0.0d);

                entityLocation.add(vector.multiply(-1.25d));

                final CloneNPC clone = clones.createClone(entityLocation);
                clone.attack(entity, furyCloneDamage);

                GameTask.runLater(clone::disappear, 15);

                // Fx
                player.playWorldSound(entityLocation, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.25f);
            });

            return Response.OK;
        }


    }
}
