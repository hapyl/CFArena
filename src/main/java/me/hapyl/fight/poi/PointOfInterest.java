package me.hapyl.fight.poi;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;

public class PointOfInterest implements Keyed {

    private final Key key;
    private final BoundingBox boundingBox;

    public PointOfInterest(@Nonnull Key key, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.key = key;
        this.boundingBox = new BoundingBox(x1, y1, z1, x2, y2, z2);
    }

    public PointOfInterest(@Nonnull Key key, @Nonnull BoundingBox boundingBox) {
        this.key = key;
        this.boundingBox = boundingBox;
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }

    public void discover(@Nonnull Player player) {

    }
}
