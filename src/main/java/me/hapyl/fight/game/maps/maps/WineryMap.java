package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.MapFeature;
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

        addLocation(201.5, 64.0, 199.5);
        addLocation(201.5, 64.0, 235.5, -180f, 0.0f);
        addLocation(184.5, 66.0, 224.5, -90f, 0.0f);
        addLocation(185.5, 60.0, 213.5, 90.0f, 0.0f);
        addLocation(219.5, 74.0, 217.5, 90.0f, 0.0f);
        addLocation(228.5, 64.0, 235.5, 90.0f, 0.0f);

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
