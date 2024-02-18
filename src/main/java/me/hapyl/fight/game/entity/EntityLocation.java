package me.hapyl.fight.game.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class EntityLocation extends Location {

    public EntityLocation(@Nonnull Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void modifyAnd(double x, double y, double z, @Nonnull Consumer<EntityLocation> consumer) {
        add(x, y, z);

        consumer.accept(this);

        subtract(x, y, z);
    }

    @Nonnull
    @Override
    public EntityLocation add(@Nonnull Vector vec) {
        super.add(vec);

        return this;
    }

    @Nonnull
    @Override
    public EntityLocation add(@Nonnull Location vec) {
        super.add(vec);
        return this;
    }

    @Nonnull
    @Override
    public EntityLocation add(double x, double y, double z) {
        super.add(x, y, z);

        return this;
    }

    private EntityLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    private EntityLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }
}
