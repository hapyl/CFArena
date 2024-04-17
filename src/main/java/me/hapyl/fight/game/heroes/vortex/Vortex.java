package me.hapyl.fight.game.heroes.vortex;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.talents.vortex.*;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Compute;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Vortex extends Hero implements UIComplexComponent {

    @DisplayField private final double damagePerDreamStack = 0.15d;
    @DisplayField private final double sotsDamage = 15.0d;

    private final PlayerMap<DreamStack> dreamStackMap = PlayerMap.newConcurrentMap();

    private final int blinkIterations = 3;
    private final double blinkStep = 0.75d;

    public Vortex(@Nonnull Heroes handle) {
        super(handle, "Vortex");

        setArchetype(Archetype.STRATEGY);
        setGender(Gender.MALE);

        setDescription("A young boy with the power of speaking to stars...");
        setItem("2adc458dfabc20b8d587b0476280da2fb325fc616a5212784466a78b85fb7e4d");

        final HeroAttributes attributes = getAttributes();
        attributes.setHealth(120);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(102, 51, 0);
        equipment.setLeggings(179, 89, 0);
        equipment.setBoots(255, 140, 26);

        setWeapon(new VortexWeapon(this));
        setUltimate(new VortexUltimate());
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final DreamStack stacks = dreamStackMap.remove(player);

        if (stacks != null) {
            stacks.cancel();
        }
    }

    @Override
    public void onStop() {
        dreamStackMap.values().forEach(DreamStack::cancel);
        dreamStackMap.clear();
    }

    public void addDreamStack(@Nonnull GamePlayer player) {
        dreamStackMap.compute(player, Compute.nullable(fn -> new DreamStack(player, dreamStackMap), DreamStack::increment));
    }

    public int getStack(@Nonnull GamePlayer player) {
        final DreamStack dreamStack = dreamStackMap.get(player);
        return dreamStack != null ? dreamStack.stacks : 0;
    }

    @Override
    public VortexStarTalent getFirstTalent() {
        return (VortexStarTalent) Talents.VORTEX_STAR.getTalent();
    }

    @Override
    public StarAligner getSecondTalent() {
        return (StarAligner) Talents.STAR_ALIGNER.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.LIKE_A_DREAM.getTalent();
    }

    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final int starAmount = getFirstTalent().getStarAmount(player);
        final DreamStack dreamStacks = dreamStackMap.get(player);

        return List.of("&6⭐ &l" + starAmount, dreamStacks == null ? "" : "&e⚡ &l" + dreamStacks.stacks);
    }

    public void sacrificeHealth(@Nonnull GamePlayer player) {
        final VortexStarTalent talent = getFirstTalent();

        final EntityAttributes attributes = player.getAttributes();
        attributes.subtractSilent(AttributeType.MAX_HEALTH, talent.healthSacrificePerStar);
    }

    public void restoreHealth(@Nonnull GamePlayer player, @Nonnull AstralStar star) {
        if (star.isDead()) {
            return;
        }

        final VortexStarTalent talent = getFirstTalent();
        final double health = star.getHealth();

        // Return sacrificed health
        final EntityAttributes attributes = player.getAttributes();
        attributes.addSilent(AttributeType.MAX_HEALTH, talent.healthSacrificePerStar);

        // Only heal for whatever the star's health is
        player.heal(health);
    }

    public double calculateAstralDamage(@Nonnull GamePlayer player, double damage) {
        final int stack = getStack(player);
        return damage * (damagePerDreamStack * stack + 1);
    }

    public void performStarBlink(@Nonnull GamePlayer player, @Nonnull AstralStar targetStar) {
        // Calculate damage
        final VortexStarTalent talent = targetStar.getTalent();
        final double damage = calculateAstralDamage(player, sotsDamage);

        targetStar.setState(StarState.LINKING);

        final AstralStarList stars = talent.getStars(player);
        final Location location = player.getLocation();
        final Location starLocation = targetStar.getLocation();

        final double distance = location.distance(starLocation);
        final Vector vector = starLocation.toVector().subtract(location.toVector()).normalize().multiply(blinkStep);

        player.setGameMode(GameMode.SPECTATOR);

        new AstralTask(player, targetStar) {

            private double distanceTravelled = 0.0d;

            @Override
            public void onTaskStop() {
                player.setGameMode(GameMode.SURVIVAL);
            }

            @Override
            public void run(@Nonnull GamePlayer player, @Nonnull AstralStar star, int tick) {
                for (int i = 0; i < blinkIterations; i++) {
                    if (next()) {
                        return;
                    }
                }
            }

            private boolean next() {
                if (distanceTravelled >= distance) {
                    restoreHealth(player, targetStar);
                    stars.removeStar(targetStar);

                    cancel();
                    return true;
                }

                // Travel
                location.add(vector);
                player.teleport(location);

                // Damage
                Collect.nearbyEntities(location, 1.5d).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    entity.damageNoKnockback(damage, player, EnumDamageCause.STAR_SLASH);
                });

                // Fx
                player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
                player.spawnWorldParticle(location, Particle.CRIT, 1, 0.25d, 0.25d, 0.25d, 0.05f);
                player.spawnWorldParticle(location, Particle.SPELL, 3, 0.25d, 0.25d, 0.25d, 0.02f);

                player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f);

                distanceTravelled += blinkStep;
                return false;
            }
        };
    }

    private class VortexUltimate extends UltimateTalent {

        @DisplayField private final double ultimateBaseDamage = 1d;
        @DisplayField private final double ultimateSpeed = 0.3d;
        @DisplayField(dp = 3) private final double ultimateSpeedStuck = 0.05d;
        @DisplayField(percentage = true) private final double knockback = 0.3d;

        public VortexUltimate() {
            super("Arcana", 50);

            setDescription("""
                    Launch an &6Astral&7 energy in front of you that &nfollows&7 your crosshair and &brapidly&7 deals &cdamage&7 in small &cAoE&7.
                    """);

            setItem(Material.QUARTZ);
            setDurationSec(5);
            setCooldownSec(30);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final double damage = calculateAstralDamage(player, ultimateBaseDamage);

            new PlayerTickingGameTask(player) {
                private final Location location = player.getLocationInFrontFromEyes(0.75f);

                @Override
                public void run(int tick) {
                    if (tick >= getUltimateDuration()) {
                        cancel();
                        return;
                    }

                    boolean isHit = false;

                    for (LivingGameEntity entity : Collect.nearbyEntities(location, 1.5d)) {
                        if (player.isSelfOrTeammate(entity)) {
                            continue;
                        }

                        isHit = true;

                        entity.modifyKnockback(1 - knockback, self -> {
                            self.damageTick(damage, player, EnumDamageCause.SOTS, 2);
                        });
                    }

                    final Vector vector = player.getEyeLocation().getDirection();
                    final Vector nextVector = vector.multiply(isHit ? ultimateSpeedStuck : ultimateSpeed);

                    location.add(nextVector);

                    if (!location.getBlock().isPassable()) {
                        location.subtract(nextVector);
                    }

                    // Fx
                    player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0.0f);

                    if (tick % 5 == 0) {
                        PlayerLib.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
                    }
                }
            }.runTaskTimer(0, 1);

            return UltimateResponse.OK;
        }
    }
}
