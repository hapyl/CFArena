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
            new WineryOwl(233, 80, 178),
            new WineryOwl(186, 76, 167),
            new WineryOwl(156, 76, 247),
            new WineryOwl(264, 77, 211),
    };

    public WineryMap() {
        super("Winery \"Drunk Cat\"");

        setDescription("");
        setMaterial(Material.SWEET_BERRIES);
        setTicksBeforeReveal(100);

        addLocation(201, 64, 199);
        addLocation(201, 64, 235, -180f, 0.0f);
        addLocation(184, 66, 224, -90f, 0.0f);
        addLocation(185, 60, 213, 90.0f, 0.0f);
        addLocation(219, 74, 217, 90.0f, 0.0f);
        addLocation(228, 64, 235, 90.0f, 0.0f);

        addPackLocation(PackType.HEALTH, 171.5, 64.0, 214.5);
        addPackLocation(PackType.HEALTH, 227.5, 74.0, 218.5);
        addPackLocation(PackType.HEALTH, 178.5, 60.0, 235.5);
        addPackLocation(PackType.HEALTH, 235.5, 61.0, 216.5);

        addPackLocation(PackType.CHARGE, 201.5, 73.0, 217.5);
        addPackLocation(PackType.CHARGE, 166.5, 64.0, 231.5);
        addPackLocation(PackType.CHARGE, 223.5, 84.0, 227.5);
        addPackLocation(PackType.CHARGE, 190.5, 60.0, 215.5);

        setWeather(WeatherType.DOWNFALL);
        setTime(18000);

        // Howl
        addFeature(new HiddenMapFeature() {
            @Override
            public void onStart() {
                GameTask.runTaskTimer(task -> {
                    // Howl
                    final Location location = BukkitUtils.defLocation(201.5, 64.0, 217.5);
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
                        player.damageTick(2, EnumDamageCause.LIGHTNING, 1);
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
