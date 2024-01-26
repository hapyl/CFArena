package me.hapyl.fight.game.talents.archive.shadow_assassin;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public class ShadowAssassinClone extends ShadowAssassinTalent {

    @DisplayField protected final double cloneDamage = 10.0d;
    @DisplayField protected final double defenseReduction = 0.4d;
    @DisplayField protected final int defenseReductionDuration = Tick.fromSecond(6);
    @DisplayField protected final short cloneLimit = 3;
    @DisplayField protected final short energyRegen = 25;

    @DisplayField protected final double furyAoeDistance = 6.0d;
    @DisplayField protected final int furyImpairDuration = 200;
    @DisplayField protected final double furyCloneDamage = 20.0d;

    private final PlayerMap<PlayerCloneList> clones = PlayerMap.newMap();

    public ShadowAssassinClone() {
        super("Shadow Clone");

        setType(Type.IMPAIR);
        setItem(Material.DRAGON_EGG);

        setTalents(new Stealth(), new Fury(60));
    }

    @Nonnull
    public PlayerCloneList getPlayerClones(@Nonnull GamePlayer player) {
        return clones.computeIfAbsent(player, fn -> new PlayerCloneList(this, player));
    }

    @Override
    public void onStop() {
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
                                        
                    The clone waits patiently for an enemy to come close before &cattacking&7 and reducing their %s.
                                        
                    Upon attacking the clone will &cdisappear&7, and there is a short time window where you can &bteleport&7 to the clone.

                    &bTeleporting&7 regenerates %s{energyRegen} %s&7.
                                        
                    &8;;Up to {cloneLimit} clones can exist at the same time.
                    """, AttributeType.DEFENSE, Named.SHADOW_ENERGY.getColor(), Named.SHADOW_ENERGY);
            setCooldownSec(16);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final PlayerCloneList playerClones = getPlayerClones(player);

            playerClones.createClone(player.getLocationAnchored()).startTicking();
            return Response.OK;
        }
    }

    private class Fury extends FuryTalent {

        public Fury(int furyCost) {
            super(ShadowAssassinClone.this, furyCost);

            setDescription("""
                    Summon three &8Shadow Clones&7 in front of you.
                                        
                    The clones take turns attacking in a fixed AoE:
                    └ Slowing and blinding enemies.
                    └ Reducing %s.
                    └ Dealing damage.
                                        
                    &8;;Any existing clones will disappear.
                    """, AttributeType.DEFENSE);

            setCooldownSec(28);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final PlayerCloneList playerClones = getPlayerClones(player);
            playerClones.disappearAll();

            final Location attackLocation = getInFront(player, 3);
            final Location location = getInFront(player, 6);
            final Location locationCloser = getInFront(player, 4);

            final Vector direction = location.getDirection().normalize().setY(0.0d);
            final Vector vectorLeft = new Vector(direction.getZ(), 0.0d, -direction.getX()).normalize().multiply(2);
            final Vector vectorRight = new Vector(-direction.getZ(), 0.0d, direction.getX()).normalize().multiply(2);

            final Location secondCloneLocation = locationCloser.clone().add(vectorLeft);
            final Location thirdCloneLocation = locationCloser.clone().add(vectorRight);

            new FuryCloneNPC(ShadowAssassinClone.this, location, player, attackLocation, 1) {
                @Override
                public void onAttack(@Nonnull List<LivingGameEntity> entities) {
                    entities.forEach(entity -> {
                        entity.addEffect(Effects.SLOW, 1, furyImpairDuration);
                        entity.addEffect(Effects.DARKNESS, 1, furyImpairDuration);
                    });

                    player.playWorldSound(attackLocation, Sound.ENTITY_HORSE_DEATH, 0.75f);
                }
            };

            new FuryCloneNPC(ShadowAssassinClone.this, secondCloneLocation, player, attackLocation, 10) {
                @Override
                public void onAttack(@Nonnull List<LivingGameEntity> entities) {
                    entities.forEach(entity -> {
                        final EntityAttributes attributes = entity.getAttributes();

                        attributes.decreaseTemporary(Temper.SHADOW_CLONE, AttributeType.DEFENSE, defenseReduction, defenseReductionDuration);
                    });

                    // Fx
                    player.playWorldSound(attackLocation, Sound.ENTITY_BLAZE_HURT, 0.75f);
                }
            };

            new FuryCloneNPC(ShadowAssassinClone.this, thirdCloneLocation, player, attackLocation, 20) {
                @Override
                public void onAttack(@Nonnull List<LivingGameEntity> entities) {
                    entities.forEach(entity -> {
                        entity.damage(furyCloneDamage, player, EnumDamageCause.SHADOW_CLONE);
                    });

                    // Fx
                    player.playWorldSound(attackLocation, Sound.ENTITY_PLAYER_HURT, 0.0f);
                }
            };

            return Response.OK;
        }

        private Location getInFront(GamePlayer player, double distance) {
            final Location location = player.getLocation();
            final Vector vector = location.getDirection().normalize().setY(0.0d);

            return location.add(vector.multiply(distance));
        }
    }
}
