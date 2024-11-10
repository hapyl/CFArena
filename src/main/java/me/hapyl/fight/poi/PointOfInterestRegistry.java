package me.hapyl.fight.poi;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.SimpleRegistry;
import org.bukkit.util.BoundingBox;

public class PointOfInterestRegistry extends SimpleRegistry<PointOfInterest> {

    public final PointOfInterest STORE;

    public PointOfInterestRegistry() {
        STORE = new PointOfInterest(Key.ofString("test"), new BoundingBox());
    }

}
