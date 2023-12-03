package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.game.maps.MapFeature;
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
            asLocation(20, 75, -118),
            asLocation(23, 78, -117),
            asLocation(18, 75, -121),
            asLocation(17, 74.5d, -118)
    };

    private int currentCatPos;
    private Cat cat;

    public LibraryCat() {
        super("Void Cat", "A mysterious cat that doesn't like being touched.");
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

        cat = Entities.CAT.spawn(catLocations[0], me -> {
            me.setGravity(false);
            me.setSitting(true);
            me.setOwner(null);
            me.setCatType(Cat.Type.ALL_BLACK);
            me.setAdult();

            Nulls.runIfNotNull(me.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), attr -> attr.setBaseValue(1.0f));
            Nulls.runIfNotNull(me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), attr -> attr.setBaseValue(0.0f));
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
    public void tick(int tickMod20) {
        if (!catExists()) {
            return;
        }

        PlayerLib.spawnParticle(cat.getLocation(), Particle.SPELL_MOB, 0, 0, 0, 0, 1);
    }

    public Cat getCat() {
        return cat;
    }

    private Location asLocation(double x, double y, double z) {
        return BukkitUtils.defLocation(x + 0.5d, y, z + 0.5d);
    }

}
