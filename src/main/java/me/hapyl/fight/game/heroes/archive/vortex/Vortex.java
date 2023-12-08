package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.vortex.StarAligner;
import me.hapyl.fight.game.talents.archive.vortex.VortexSlash;
import me.hapyl.fight.game.talents.archive.vortex.VortexStar;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.ItemStackRandomizedData;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Compute;
import org.bukkit.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Vortex extends Hero implements UIComplexComponent {

    @DisplayField private final double damagePerDreamStack = 0.15d;
    @DisplayField private final double sotsDamage = 15.0d;
    @DisplayField private final double ultimateRange = 10.0d;

    private final PlayerMap<DreamStack> dreamStackMap = PlayerMap.newConcurrentMap();

    public Vortex() {
        super("Vortex");

        setArchetype(Archetype.STRATEGY);

        setDescription("A young boy with the power of speaking to starts...");
        setItem("2adc458dfabc20b8d587b0476280da2fb325fc616a5212784466a78b85fb7e4d");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(102, 51, 0);
        equipment.setLeggings(179, 89, 0);
        equipment.setBoots(255, 140, 26);

        setWeapon(new VortexWeapon(this));

        setUltimate(new UltimateTalent("All the Stars", """
                Instantly create &b10 &eAstral Stars&7 around you.
                                
                After a short delay, &brapidly&7 slash between them, &cdealing damage&7 in a process before finishing with a &4final blow&7 that &bslows&7 enemies.
                                
                &8;;This will not affect already placed stars.
                """, 70)
                .setItem(Material.QUARTZ)
                .setCastDuration(15)
                .setCooldownSec(30));
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
    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final double halfRange = ultimateRange / 2.0d;
        final double quarterRange = halfRange / 3.0d;

        final Location groundLocation = player.getLocation();
        final Location location = player.getMidpointLocation();

        final Location[] ultimateStarPoints = createLocationArray(
                location,
                loc -> loc.add(0, halfRange, 0),
                loc -> loc.add(-quarterRange, 0, -quarterRange),
                loc -> loc.add(halfRange, 0, 0),
                loc -> loc.add(-quarterRange, 0, quarterRange),
                loc -> loc.add(0, 0, -halfRange),
                loc -> loc.add(0, 0, halfRange),
                loc -> loc.add(-halfRange, 0, 0),
                loc -> loc.add(quarterRange, 0, -quarterRange),
                loc -> loc.add(quarterRange, 0, quarterRange)
        );

        final int castDuration = getUltimate().getCastDuration();
        final Random random = new Random();

        // Fx for stars "appearing"
        for (Location point : ultimateStarPoints) {
            player.spawnWorldParticle(point, Particle.FLASH, 1);

            final World world = player.getWorld();
            world.dropItem(location, new ItemStackRandomizedData(Material.NETHER_STAR), self -> {
                self.setGravity(false);
                self.setVelocity(new Vector(CFUtils.randomAxis(0.1d, 0.4d), random.nextDouble(0.1d, 0.2d), CFUtils.randomAxis(0.1d, 0.4d)));
                self.setPickupDelay(10000);
                self.setTicksLived(6000 - castDuration - 20);
            });

            player.playWorldSound(point, Sound.ITEM_TRIDENT_THROW, 0.0f);
            player.playWorldSound(point, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 0.0f);
        }

        return new UltimateCallback() {
            @Override
            public void callback(@Nonnull GamePlayer player) {
                new TickingGameTask() {
                    private int pos = 0;

                    @Override
                    public void run(int tick) {
                        if (modulo(5)) {
                            if (pos >= ultimateStarPoints.length) {
                                performFinalSlash(groundLocation, player);
                                cancel();
                                return;
                            }

                            performStarSlash(pos == 0 ? location : ultimateStarPoints[pos - 1], ultimateStarPoints[pos], player);
                            pos++;
                        }

                        // Fx
                        if (modulo(10)) {
                            Geometry.drawCircleAnchored(location, ultimateRange, Quality.HIGH, new Draw(null) {
                                @Override
                                public void draw(Location location) {
                                    player.spawnWorldParticle(location, Particle.FIREWORKS_SPARK, 1);
                                    player.spawnWorldParticle(location, Particle.CRIT, 1, 0.1d, 0.1d, 0.1d, 0.05f);
                                }
                            });
                        }
                    }
                }.runTaskTimer(0, 1);
            }
        };
    }

    public int getStack(@Nonnull GamePlayer player) {
        final DreamStack dreamStack = dreamStackMap.get(player);
        return dreamStack != null ? dreamStack.stacks : 0;
    }

    public void performStarSlash(@Nonnull Location from, @Nonnull Location end, @Nonnull GamePlayer player) {
        // Calculate damage
        final int stack = getStack(player);
        final double damage = sotsDamage * (damagePerDreamStack * stack + 1);

        // Perform slash
        CFUtils.rayTracePath(from, end, 1.0d, 2.0d, entity -> {
            if (entity.equals(player)) {
                return;
            }

            entity.damage(damage, player, EnumDamageCause.STAR_SLASH);
        }, location -> {
            // Fx
            PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
            PlayerLib.spawnParticle(location, Particle.CRIT, 1, 0.25d, 0.25d, 0.25d, 0.05f);
            PlayerLib.spawnParticle(location, Particle.SPELL, 3, 0.25d, 0.25d, 0.25d, 0.02f);

            PlayerLib.playSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f);
        });
    }

    @Override
    public VortexSlash getFirstTalent() {
        return (VortexSlash) Talents.VORTEX_SLASH.getTalent();
    }

    @Override
    public VortexStar getSecondTalent() {
        return (VortexStar) Talents.VORTEX_STAR.getTalent();
    }

    @Override
    public StarAligner getThirdTalent() {
        return (StarAligner) Talents.STAR_ALIGNER.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.LIKE_A_DREAM.getTalent();
    }

    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final int starAmount = getSecondTalent().getStarAmount(player);
        final DreamStack dreamStacks = dreamStackMap.get(player);

        return List.of("&6⭐ &l" + starAmount, dreamStacks == null ? "" : "&e⚡ &l" + dreamStacks.stacks);
    }

    @SafeVarargs
    private Location[] createLocationArray(Location parent, Function<Location, Location>... functions) {
        if (functions == null) {
            return new Location[] {};
        }

        final Location[] array = new Location[functions.length];

        for (int i = 0; i < functions.length; i++) {
            array[i] = functions[i].apply(parent.clone());
        }

        return array;
    }

    private void performFinalSlash(Location location, GamePlayer player) {
        for (double i = 0; i < Math.PI * 2; i += Math.PI / 16) {
            final double x = Math.sin(i) * ultimateRange;
            final double z = Math.cos(i) * ultimateRange;

            location.add(x, 0, z);

            // Damage
            Collect.nearbyEntities(location, 2.0d).forEach(entity -> {
                if (entity.equals(player)) {
                    return;
                }

                entity.setLastDamager(player);
                entity.damage(1.0d, EnumDamageCause.ENTITY_ATTACK);
                entity.addPotionEffect(PotionEffectType.SLOW, 80, 3);
            });

            // Fx
            PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
            PlayerLib.spawnParticle(location, Particle.CRIT, 1, 0.25d, 0.25d, 0.25d, 0.05f);
            PlayerLib.spawnParticle(location, Particle.SPELL, 3, 0.25d, 0.25d, 0.25d, 0.02f);

            PlayerLib.playSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f);

            location.subtract(x, 0, z);
        }

    }
}
