package me.hapyl.fight.game.maps.features.library;

import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.garbage.CFGarbageCollector;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class LibraryCat extends MapFeature implements Listener {

    private final Location[] catLocations = {
            asLocation(4021, 75, -18),
            asLocation(4023, 78, -17),
            asLocation(4018, 75, -21),
            asLocation(4017, 74.5, -18)
    };

    private int currentCatPos;
    private Cat cat;

    public LibraryCat() {
        super("Void Cat", """
                "A mysterious cat that doesn't like being touched."
                """);
    }

    @Override
    public void onStart() {
        spawnVoidCat();
    }

    @Override
    public void onStop() {
        removeVoidCat();
    }

    @EventHandler()
    public void handleCatDamage(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();

        if (entity instanceof Cat cat && cat == this.cat) {
            ev.setCancelled(true);
            ev.setDamage(0.0d);
            nextPosition();
        }
    }

    private void spawnVoidCat() {
        if (catExists()) {
            return;
        }

        cat = Entities.CAT.spawn(catLocations[0], self -> {
            self.setGravity(false);
            self.setSitting(true);
            self.setOwner(null);
            self.setCatType(Cat.Type.ALL_BLACK);
            self.setAdult();
            CFGarbageCollector.add(self);

            Nulls.runIfNotNull(self.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), attr -> attr.setBaseValue(1.0f));
            Nulls.runIfNotNull(self.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), attr -> attr.setBaseValue(0.0f));
        });

        currentCatPos = 0;
    }

    private void removeVoidCat() {
        if (!catExists()) {
            return;
        }

        cat.remove();
        cat = null;
    }

    public void nextPosition() {
        currentCatPos = currentCatPos + 1 >= catLocations.length ? 0 : currentCatPos + 1;

        final Location location = catLocations[currentCatPos];
        cat.teleport(location);
        cat.setSitting(true);

        PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.6f);
    }

    private boolean catExists() {
        return cat != null && !cat.isDead();
    }

    @Override
    public void tick(int tick) {
        if (!catExists()) {
            return;
        }

        PlayerLib.spawnParticle(cat.getLocation(), Particle.EFFECT, 0, 0, 0, 0, 1);
    }

    public Cat getCat() {
        return cat;
    }

    private Location asLocation(double x, double y, double z) {
        return BukkitUtils.defLocation(x + 0.5d, y, z + 0.5d);
    }

}
