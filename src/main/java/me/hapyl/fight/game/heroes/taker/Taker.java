package me.hapyl.fight.game.heroes.taker;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.taker.DeathSwap;
import me.hapyl.fight.game.talents.taker.FatalReap;
import me.hapyl.fight.game.talents.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Taker extends Hero implements UIComponent, DisplayFieldProvider {

    private final PlayerMap<SpiritualBones> playerBones = PlayerMap.newConcurrentMap();

    public Taker(@Nonnull Heroes handle) {
        super(handle, "Taker");

        setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.POWERFUL_ULTIMATE);
        setGender(Gender.UNKNOWN);

        setDescription("""
                Will take your life away!
                """);
        setItem("ff1e554161bd4b2ce4cad18349fd756994f74cabf1fd1dacdf91b6d05dffaf");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(28, 28, 28);
        equipment.setLeggings(0, 0, 0, TrimPattern.SILENCE, TrimMaterial.QUARTZ);
        equipment.setBoots(28, 28, 28, TrimPattern.SILENCE, TrimMaterial.QUARTZ);

        setWeapon(Material.IRON_HOE, "Reaper Scythe", """
                The sharpest of them all!
                """, 6.66d);

        setUltimate(new TakerUltimate());
    }

    @Nonnull
    public SpiritualBones getBones(GamePlayer player) {
        return playerBones.computeIfAbsent(player, SpiritualBones::new);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        playerBones.values().forEach(SpiritualBones::clearArmorStands);
        playerBones.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final SpiritualBones bones = playerBones.remove(player);

        if (bones != null) {
            bones.reset();
        }
    }

    @Override
    public void onPlayersRevealed(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                playerBones.values().forEach(SpiritualBones::tick);
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (player == null) {
            return;
        }

        final SpiritualBones bones = getBones(player);

        if (bones.getBones() == 0) {
            return;
        }

        final double healing = bones.getHealing();
        final double damage = instance.getDamage();

        final double healingScaled = damage * healing / 100.0d;
        player.heal(healingScaled);

        instance.multiplyDamage(1 + bones.getDamageMultiplier() / 100);
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final SpiritualBones bones = getBones(player);

        if (bones.getBones() == 0) {
            return;
        }

        instance.multiplyDamage(1 - bones.getDamageReduction() / 100);
    }

    @Override
    public FatalReap getFirstTalent() {
        return (FatalReap) Talents.FATAL_REAP.getTalent();
    }

    @Override
    public DeathSwap getSecondTalent() {
        return (DeathSwap) Talents.DEATH_SWAP.getTalent();
    }

    @Override
    public SpiritualBonesPassive getPassiveTalent() {
        return (SpiritualBonesPassive) Talents.SPIRITUAL_BONES.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        return "&f☠: &l" + getBones(player).getBones();
    }

    private class TakerUltimate extends UltimateTalent {

        @DisplayField private final double ultimateSpeed = 0.45d;
        @DisplayField private final int hitDelay = 10;

        public TakerUltimate() {
            super("Embodiment of Death", 70);

            setDescription("""
                    Instantly consume all %s to cloak yourself in the &8darkness&7 for {duration}.
                                            
                    After a short delay, embrace the death and become &binvulnerable&7.
                                            
                    The &8darkness&7 force will constantly &brush forward&7, dealing &cAoE damage&7 and &eimpairing&7 anyone who dares to stay in the way.
                    &8;;Also recover health every time an enemy is hit.
                                            
                    Hold &6&lSNEAK&7 to rush slower.
                                    
                    &8;;The damage and healing is scaled with %s consumed.
                    """.formatted(Named.SPIRITUAL_BONES, Named.SPIRITUAL_BONES.toStringRaw()));

            setSound(Sound.ENTITY_HORSE_DEATH, 0.0f);
            setItem(Material.WITHER_SKELETON_SKULL);
            setDurationSec(4);
            setCastDuration(20);
            setCdFromCost(2);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final SpiritualBones bones = getBones(player);

            if (bones.getBones() <= 0) {
                return UltimateResponse.error("Not enough &l%s&c!".formatted(getPassiveTalent().getName()));
            }

            final Location location = player.getLocation();
            final int castDuration = getUltimate().getCastDuration();

            final int bonesAmount = bones.getBones();

            final double damage = 5.0d + bonesAmount;
            final double healing = 1.0d + bonesAmount;

            bones.reset();

            player.addEffect(Effects.SLOW, 255, castDuration);

            new TickingGameTask() {
                @Override
                public void run(int tick) {
                    if (tick >= castDuration) {
                        cancel();
                        return;
                    }

                    final double distance = Math.sin(tick * 0.1 + 0.5d);

                    for (double d = 0.0d; d < Math.PI * 2; d += Math.PI / 16) {
                        final double x = Math.sin(d) * distance;
                        final double y = player.getEyeHeight() - (2.0d / castDuration * tick);
                        final double z = Math.cos(d) * distance;

                        location.add(x, y, z);

                        player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 1);
                        player.spawnWorldParticle(location, Particle.SMOKE, 1);

                        location.subtract(x, y, z);
                    }

                    // SFX
                    if (modulo(2)) {
                        player.playWorldSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0f / castDuration * tick);
                    }
                }
            }.runTaskTimer(0, 1);

            return new UltimateResponse() {
                @Override
                public void onCastFinished(@Nonnull GamePlayer player) {
                    player.setInvulnerable(true);

                    new TimedGameTask(getUltimateDuration()) {
                        @Override
                        public void run(int tick) {
                            final boolean sneaking = player.isSneaking();

                            player.setVelocity(player.getLocation()
                                    .getDirection()
                                    .normalize()
                                    .multiply(sneaking ? ultimateSpeed / 2 : ultimateSpeed)
                                    .add(new Vector(0.0d, BukkitUtils.GRAVITY, 0.0d)));

                            // Damage
                            if (modulo(hitDelay)) {
                                final Location hitLocation = player.getLocationInFrontFromEyes(1.5d);

                                Collect.nearbyEntities(hitLocation, 2.0d, living -> living.isValid(player))
                                        .forEach(entity -> {
                                            entity.damage(damage, player, EnumDamageCause.EMBODIMENT_OF_DEATH);
                                            player.heal(healing);
                                        });

                                // Hit Fx
                                player.spawnWorldParticle(hitLocation, Particle.SWEEP_ATTACK, 20, 1, 1, 1, 0.0f);
                                player.spawnWorldParticle(hitLocation, Particle.LARGE_SMOKE, 20, 1, 1, 1, 0.0f);
                                player.spawnWorldParticle(hitLocation, Particle.EFFECT, 20, 1, 1, 1, 0.0f);

                                player.playWorldSound(hitLocation, Sound.ITEM_TRIDENT_THROW, 0.0f);
                                player.playWorldSound(hitLocation, Sound.ENTITY_WITHER_HURT, 0.75f);
                            }

                            // Instant Fx
                            player.spawnWorldParticle(player.getEyeLocation(), Particle.SQUID_INK, 5, 0.03125d, 0.6d, 0.03125d, 0.01f);
                            player.spawnWorldParticle(player.getEyeLocation(), Particle.LAVA, 2, 0.03125d, 0.6d, 0.03125d, 0.01f);
                        }
                    }.runTaskTimer(0, 1);
                }

                @Override
                public void onUltimateEnd(@Nonnull GamePlayer player) {
                    player.setInvulnerable(false);
                }
            };
        }
    }
}
