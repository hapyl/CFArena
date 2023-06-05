package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.Size;
import me.hapyl.fight.game.maps.features.Direction;
import me.hapyl.fight.game.maps.features.Turbine;
import me.hapyl.fight.game.maps.features.TurbineFeature;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.util.BoundingBoxCollector;
import org.bukkit.Material;

import java.util.Random;

public class MoonBase extends GameMap {

    private byte gate = 0;

    public MoonBase() {
        super("Moon Station");

        setDescription("");
        setMaterial(Material.END_STONE_BRICKS);
        setSize(Size.MEDIUM);
        setTime(9500);
        setTicksBeforeReveal(160);

        addLocation(1905, 82, 1921, -180, 0, t -> gate == 0);
        addLocation(1922, 73, 1860, t -> gate == 0);
        addLocation(1895, 76, 1927, -90, 0, t -> gate == 0);
        addLocation(1936, 75, 1897, 90, 0, t -> gate == 1);
        addLocation(1890, 73, 1905, -90, 0, t -> gate == 1);
        addLocation(1929, 73, 1937, -180, 0, t -> gate == 1);
        addLocation(1908, 73, 1882, t -> gate == 1);

        addPackLocation(PackType.HEALTH, 1936.5, 74.0, 1919.5);
        addPackLocation(PackType.HEALTH, 1936.5, 75.0, 1932.5);
        addPackLocation(PackType.HEALTH, 1902.5, 73.0, 1859.5);

        addPackLocation(PackType.CHARGE, 1924.5, 74.0, 1870.5);
        addPackLocation(PackType.CHARGE, 1936.5, 74.0, 1875.5);
        addPackLocation(PackType.CHARGE, 1937.5, 85.0, 1936.5);

        // Turbines
        final TurbineFeature turbines = new TurbineFeature();

        turbines.addTurbine(
                new Turbine(
                        new BoundingBoxCollector(1899.5, 81.0, 1881.5, 1903.5, 85.0, 1890.5),
                        new BoundingBoxCollector(1903.5, 85.0, 1880.5, 1899.5, 81.0, 1880.5)
                ).setDirections(Direction.POSITIVE_Z));

        turbines.addTurbine(
                new Turbine(
                        new BoundingBoxCollector(1861.5, 75.0, 1912.5, 1872.5, 80.0, 1917.5),
                        new BoundingBoxCollector(1872.5, 75.0, 1912.5, 1873.5, 80.0, 1917.5)
                ).setDirections(Direction.NEGATIVE_X)
        );

        addFeature(turbines);
    }

    @Override
    public void onStartOnce() {
        final boolean b = new Random().nextBoolean();

        gate = (byte) (b ? 0 : 1);
        Debug.info("gate = " + gate);
    }

}
