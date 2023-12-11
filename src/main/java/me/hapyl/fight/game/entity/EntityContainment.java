package me.hapyl.fight.game.entity;

import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

public class EntityContainment {

    private final LivingGameEntity entity;
    private final Location centre;
    private final double distance;

    public EntityContainment(LivingGameEntity entity, Location centre, double distance) {
        Validate.isTrue(distance > 1.0d, "containment distance must be at least 1");
        this.entity = entity;
        this.centre = centre;
        this.distance = distance;
    }

    public boolean isWithinContainment() {
        return entity.getLocation().distance(centre) <= distance;
    }

    public void pushBackToCentre(boolean notify) {
        final Location location = entity.getLocation();
        final Vector vector = centre.toVector().subtract(location.toVector());
        vector.multiply(0.5d);

        entity.setVelocity(vector);

        if (notify) {
            entity.sendTitle("&cᴄᴏɴᴛᴀɪɴᴍᴇɴᴛ", "&cYou cannot leave this area!", 0, 10, 5);
            entity.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
            entity.playSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.75f);
        }
    }
}
