package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;

import java.util.Random;

public class WineryMap extends GameMap {

    private final int howlPeriod = Tick.fromMinute(3);
    private final double howlRange = 42.0d;

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

        addFeature(new MapFeature("...", "") {

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

            @Override
            public void tick(int tick) {

            }
        });

        setWeather(WeatherType.DOWNFALL);
        setTime(18000);
    }
}
