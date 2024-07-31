package me.hapyl.fight.game.maps.winery;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.HiddenMapFeature;
import me.hapyl.fight.game.maps.features.WinerySteamFeature;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.Random;

public class WineryMap extends GameMap {

    private final Material lightningMarkerBlock = Material.CAVE_AIR;
    private final int howlPeriod = Tick.fromMinute(3);
    private final double howlRange = 42.0d;
    private final WineryOwl[] owls = new WineryOwl[] {
            new WineryOwl(5064, 77, 11),
            new WineryOwl(5033, 80, -22),
            new WineryOwl(4986, 76, -33),
            new WineryOwl(4956, 76, 47),
    };

    public WineryMap() {
        super("Winery \"Drunk Cat\"");

        setDescription("");
        setMaterial(Material.SWEET_BERRIES);
        setTicksBeforeReveal(100);

        addLocation(5001, 64, 0);
        addLocation(5020, 66.65, 18);
        addLocation(5002, 64, 45, -180f, 0f);
        addLocation(4985, 60, 14);
        addLocation(5019, 74, 17, -180f, 0f);
        addLocation(4967, 64, 21, -90f, 0f);
        addLocation(4985, 66, 36, -180f, 0f);

        addPackLocation(PackType.HEALTH, 4978, 60, 35);
        addPackLocation(PackType.HEALTH, 5035, 61, 16);
        addPackLocation(PackType.HEALTH, 4972, 64, 13);
        addPackLocation(PackType.HEALTH, 5026, 74, 19);

        addPackLocation(PackType.CHARGE, 4990, 60, 15);
        addPackLocation(PackType.CHARGE, 4982, 81, 30);
        addPackLocation(PackType.CHARGE, 4982, 81, 30);
        addPackLocation(PackType.CHARGE, 4972, 64, 35);

        setWeather(WeatherType.DOWNFALL);
        setTime(18000);

        // Howl
        addFeature(new HiddenMapFeature() {
            @Override
            public void onStart() {
                GameTask.runTaskTimer(task -> {
                    // Howl
                    final Location location = BukkitUtils.defLocation(5001, 64, 17);
                    final World world = location.getWorld();

                    location.add(new Random().nextDouble(-howlRange, howlRange), 0.0d, new Random().nextDouble(-howlRange, howlRange));

                    if (world == null) {
                        return;
                    }

                    world.playSound(location, Sound.ENTITY_WOLF_HOWL, SoundCategory.RECORDS, 4.0f, new Random().nextFloat(0.0f, 1.0f));
                }, howlPeriod, howlPeriod);
            }
        });

        // Steam
        addFeature(new WinerySteamFeature());

        // Owl Spy Achievement
        addFeature(new HiddenMapFeature() {
            @Override
            public void tick(int tick) {
                if (tick % 5 != 0) {
                    return;
                }

                // Tick owls
                for (WineryOwl owl : owls) {
                    owl.tick();
                }

                for (GamePlayer player : Collect.aliveGamePlayers()) {
                    if (Achievements.OWL_SPY.hasCompletedAtLeastOnce(player)) {
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
                        Achievements.OWL_SPY.complete(player);
                    }
                }
            }
        });

        // Lightning
        addFeature(new HiddenMapFeature() {

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
                        player.damage(2, EnumDamageCause.LIGHTNING);
                    }
                });
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();

        for (WineryOwl owl : owls) {
            owl.reset();
        }
    }
}
