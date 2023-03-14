package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.game.maps.GameMap;
import org.bukkit.Material;
import org.bukkit.WeatherType;

public class WineryMap extends GameMap {
    public WineryMap() {
        super("Winery \"Drunk Cat\"", Material.SWEET_BERRIES, 100);

        addLocation(201.5, 64.0, 199.5);
        addLocation(201.5, 64.0, 235.5, -180f, 0.0f);
        addLocation(184.5, 66.0, 224.5, -90f, 0.0f);
        addLocation(185.5, 60.0, 213.5, 90.0f, 0.0f);
        addLocation(219.5, 74.0, 217.5, 90.0f, 0.0f);
        addLocation(228.5, 64.0, 235.5, 90.0f, 0.0f);

        setWeather(WeatherType.DOWNFALL);
        setTime(18000);
    }
}
