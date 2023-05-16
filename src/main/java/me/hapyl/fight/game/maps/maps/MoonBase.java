package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.maps.Size;
import me.hapyl.fight.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collection;

public class MoonBase extends GameMap {

    public MoonBase() {
        super("Moon Station \"Lypah\"");

        setDescription("");
        setMaterial(Material.END_STONE_BRICKS);
        setSize(Size.MEDIUM);
        setTime(9500);
        setTicksBeforeReveal(160);

        // Wind
        addFeature(new MapFeature("Sucking", "Will suck you") {

            @Nonnull private final World world = Bukkit.getWorlds().get(0);
            private final Vector vector = new Vector(0.0d, 0.08d, 2.0d);
            private final BoundingBox boundingBox = new BoundingBox(1899.5, 81.0, 1881.5, 1903.5, 85.0, 1890.5);

            @Override
            public void tick(int tick) {
                final Collection<Entity> entities = world.getNearbyEntities(boundingBox, Utils::isEntityValid);

                entities.forEach(entity -> {
                    if (entity.isDead() || !(entity instanceof LivingEntity living)) {
                        return;
                    }

                    living.setVelocity(living.getVelocity().add(vector));
                });
            }

        });

    }
}
