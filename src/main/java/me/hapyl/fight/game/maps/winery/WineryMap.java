package me.hapyl.fight.game.maps.winery;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.HiddenLevelFeature;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.maps.features.WinerySteamFeature;
import me.hapyl.fight.game.maps.supply.Supplies;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.Collect;
import org.bukkit.*;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.util.Random;

public class WineryMap extends Level {

    private final Material lightningMarkerBlock = Material.CAVE_AIR;
    private final int howlPeriod = Tick.fromMinutes(3);
    private final double howlRange = 42.0d;

    private final WineryOwl[] owls = new WineryOwl[] { // FIXME (Sun, Feb 16 2025 @xanyjl):
            new WineryOwl(5064, 77, 11),
            new WineryOwl(5033, 80, -22),
            new WineryOwl(4986, 76, -33),
            new WineryOwl(4956, 76, 47),
    };

    public WineryMap(@Nonnull EnumLevel handle) {
        super(handle, "Winery \"Drunk Cat\"");

        setDescription("""
                A winery of a drunk cat.
                """);
        setMaterial(Material.SWEET_BERRIES);
        setTicksBeforeReveal(100);

        addLocation(4994, 68.0, 0, 0, 0);
        addLocation(4963, 68.0, 2, 0, 0);
        addLocation(4975, 75.0, 8, 0, 0);
        addLocation(4973, 66.0, -14, 0, 0);
        addLocation(4966, 58.0, -1, 0, 0);
        addLocation(5016, 91.0, 20, 0, 0);
        addLocation(5023, 73.0, 17, 0, 0);
        addLocation(5016, 65.0, -6, 0, 0);
        addLocation(5021, 66.0, 8, 0, 0);
        addLocation(4977, 63.0, 2, 0, 0);
        addLocation(5025, 61.0, 15, 0, 0);
        addLocation(5036, 65.0, -5, 0, 0);
        addLocation(5025, 81.0, 21, 0, 0);
        addLocation(5026, 80.0, 38, 0, 0);
        addLocation(5026, 73.0, 34, 0, 0);
        addLocation(4990, 65.0, 28, 0, 0);

        addPackLocation(Supplies.HEALTH, 5021, 81.0, 12);
        addPackLocation(Supplies.HEALTH, 4980, 83.0, 1);
        addPackLocation(Supplies.HEALTH, 5030, 65.0, -7);
        addPackLocation(Supplies.HEALTH, 5030, 61.0, 16);

        addPackLocation(Supplies.ENERGY, 5016, 91.0, 18);
        addPackLocation(Supplies.ENERGY, 5021, 80.0, 37);
        addPackLocation(Supplies.ENERGY, 5019, 66.0, 9);
        addPackLocation(Supplies.ENERGY, 5027, 68.0, 6);
        addPackLocation(Supplies.ENERGY, 4957, 58.0, -4);
        addPackLocation(Supplies.ENERGY, 4956, 74.0, -3);

        setWeather(WeatherType.DOWNFALL);
        setTime(18000);

        // Howl
        addFeature(new HiddenLevelFeature() {
            @Override
            public void onStart() {
                GameTask.runTaskTimer(
                        task -> {
                            // Howl
                            final Location location = BukkitUtils.defLocation(5001, 64, 17);
                            final World world = location.getWorld();

                            location.add(new Random().nextDouble(-howlRange, howlRange), 0.0d, new Random().nextDouble(-howlRange, howlRange));

                            if (world == null) {
                                return;
                            }

                            world.playSound(location, Sound.ENTITY_WOLF_GROWL, SoundCategory.RECORDS, 4.0f, new Random().nextFloat(0.0f, 1.0f));
                        }, howlPeriod, howlPeriod
                );
            }
        });

        // Steam
        addFeature(new WinerySteamFeature());

        // Owl Spy Achievement
        addFeature(new HiddenLevelFeature() {
            @Override
            public void tick(int tick) {
                if (true || tick % 5 != 0) { // FIXME (Mon, Feb 17 2025 @xanyjl): map is being rebuild again so replace the coordinates for owls
                    return;
                }

                // Tick owls
                for (WineryOwl owl : owls) {
                    owl.tick();
                }

                for (GamePlayer player : Collect.aliveGamePlayers()) {
                    if (Registries.achievements().OWL_SPY.hasCompletedAtLeastOnce(player)) {
                        continue;
                    }

                    int owlCount = 0;
                    for (WineryOwl owl : owls) {
                        if (owl.hasLookedAt(player)) {
                            owlCount++;
                            continue;
                        }

                        owl.isLookingAtTheTick(player);
                    }

                    if (owlCount == owls.length) {
                        Registries.achievements().OWL_SPY.complete(player);
                    }
                }
            }
        });

        // Lightning
        addFeature(new HiddenLevelFeature() {

            private final double minHeight = 77;

            @Override
            public void tick(int tick) {
                CF.getAlivePlayers().forEach(player -> {
                    final int y = player.getBlockY();

                    if (y < minHeight) {
                        return;
                    }

                    final Block block = player.getBlock();

                    if (block.getType() != lightningMarkerBlock) {
                        return;
                    }

                    final int frequency = Math.max(137 - y, 2);

                    if (tick > 0 && tick % frequency == 0) {
                        player.getWorld().strikeLightningEffect(player.getEyeLocation());
                        player.damage(2, DamageCause.LIGHTNING);
                    }
                });
            }
        });

    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        super.onStop(instance);

        for (WineryOwl owl : owls) {
            owl.reset();
        }
    }
}
