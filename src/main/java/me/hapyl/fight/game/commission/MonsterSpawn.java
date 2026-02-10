package me.hapyl.fight.game.commission;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.WeightedCollection;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import me.hapyl.fight.game.entity.commission.EntityRegistry;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class MonsterSpawn {

    private final Location location;
    private final float spread;
    private final int count;
    private final BoundingBox boundingBox;

    private final WeightedCollection<Monster> entities;

    public MonsterSpawn(double x, double y, double z, float spread, int count, @Nonnull BoundingBox boundingBox) {
        this.location = BukkitUtils.defLocation(x, y, z);
        this.spread = spread;
        this.count = count;
        this.boundingBox = boundingBox;
        this.entities = new WeightedCollection<>();
    }

    @Nonnull
    public Location location() {
        return location;
    }

    public float spread() {
        return spread;
    }

    public int count() {
        return count;
    }

    @Nonnull
    public WeightedCollection<Monster> entities() {
        return entities;
    }

    public MonsterSpawn add(@Nonnull Function<EntityRegistry, CommissionEntityType> fn, int level, int weight) {
        entities.add(new Monster(() -> fn.apply(Registries.entities()), level), weight);
        return this;
    }

    public boolean shouldSpawn() {
        for (GamePlayer player : CF.getPlayers()) {
            if (boundingBox.overlaps(player.boundingBox())) {
                return true;
            }
        }

        return false;
    }

    public void doSpawn(@Nonnull CommissionInstance instance) {
        final EnumTier tier = instance.tier();

        for (int i = 0; i < count; i++) {
            final Monster monster = entities.getRandomElement();
            final CommissionEntityType entity = monster.type().get();
            final int levelScaled = (int) (monster.level() * tier.tier().enemyLevelMultiplier());

            offsetLocation(location -> entity.create(location, levelScaled));
        }
    }

    private void offsetLocation(Consumer<Location> consumer) {
        final Random random = BukkitUtils.RANDOM;
        final double x = random.nextDouble(spread);
        final double z = random.nextDouble(spread);

        final double fx = random.nextBoolean() ? x : -x;
        final double fz = random.nextBoolean() ? z : -z;

        location.add(fx, 0, fz);
        consumer.accept(location);
        location.subtract(fx, 0, fz);
    }

}
