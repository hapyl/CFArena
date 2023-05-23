package me.hapyl.fight.game.maps.maps;

import me.hapyl.fight.GVar;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.game.maps.Size;
import me.hapyl.fight.game.maps.gamepack.PackType;
import me.hapyl.fight.util.BoundingBoxCollector;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class MoonBase extends GameMap {

    public MoonBase() {
        super("Moon Station");

        setDescription("");
        setMaterial(Material.END_STONE_BRICKS);
        setSize(Size.MEDIUM);
        setTime(9500);
        setTicksBeforeReveal(160);

        addLocation(1905.5, 82.0, 1921.5);
        addLocation(1905.5, 82, 1921.5, -180, 0);
        addLocation(1922.5, 73, 1860.5);
        addLocation(1895.5, 76.5, 1927.5, -90, 0);
        addLocation(1936.5, 75, 1897.5, 90, 0);
        addLocation(1890.5, 73, 1905.5, -90, 0);
        addLocation(1929.5, 73.0, 1937.5, -180, 0);
        addLocation(1908.5, 73.5, 1882.5);

        addPackLocation(PackType.HEALTH, 1936.5, 74.0, 1919.5);
        addPackLocation(PackType.HEALTH, 1936.5, 75.0, 1932.5);
        addPackLocation(PackType.HEALTH, 1902.5, 73.0, 1860.5);

        addPackLocation(PackType.CHARGE, 1924.5, 74.0, 1870.5);
        addPackLocation(PackType.CHARGE, 1936.5, 74.0, 1875.5);
        addPackLocation(PackType.CHARGE, 1937.5, 85.0, 1936.5);

        // Wind
        addFeature(new MapFeature("Sucking", "Will suck you") {

            @Nonnull private final World world = Bukkit.getWorlds().get(0);
            private final BoundingBoxCollector boundingBoxVent = new BoundingBoxCollector(1899.5, 81.0, 1881.5, 1903.5, 85.0, 1890.5);
            private final BoundingBoxCollector boundingBoxDeath = new BoundingBoxCollector(1903.5, 85.0, 1880.5, 1899.5, 81.0, 1880.5);
            private final Location fxLocation = new Location(world, 1901.0d, 83.0d, 1885.0d);

            @Override
            public void tick(int tick) {
                boundingBoxVent.collectValid(world).forEach(entity -> {
                    entity.setVelocity(entity.getVelocity().add(new Vector(0.0d, GVar.get("suckY", 0.03d), GVar.get("suckZ", -0.05d))));
                });

                boundingBoxDeath.collectValid(world).forEach(entity -> {
                    Cosmetics.BLOOD.getCosmetic().onDisplay(entity.getLocation());
                    GamePlayer.damageEntity(entity, 100, null, EnumDamageCause.SHREDS_AND_PIECES);

                    // Trigger achievement
                    if (entity instanceof Player player) {
                        Achievements.SHREDDING_TIME.complete(player);
                    }
                });

                // Fx
                if (tick != 0) {
                    return;
                }

                PlayerLib.spawnParticle(fxLocation, Particle.EXPLOSION_NORMAL, 10, 0.5, 0.5d, 3.0d, 0.08f);
            }

        });

    }
}
