package me.hapyl.fight.event;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Manager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityHandler implements Listener {

    @EventHandler()
    public void handleEntitySpawn(EntitySpawnEvent ev) {
        final Entity entity = ev.getEntity();

        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        Debug.info("spawned using EVENT " + living);
        Manager.current().createEntity(living);
    }

    @EventHandler()
    public void handleEntityDeath(EntityDeathEvent ev) {
        final LivingEntity entity = ev.getEntity();
        if (entity instanceof Player) {
            return;
        }

        Manager.current().removeEntity(entity);
    }


}
