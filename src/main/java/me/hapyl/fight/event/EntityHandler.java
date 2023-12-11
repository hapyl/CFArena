package me.hapyl.fight.event;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class EntityHandler implements Listener {

    @EventHandler()
    public void handleEntitySpawn(EntitySpawnEvent ev) {
        final Entity entity = ev.getEntity();
        final Manager manager = Manager.current();

        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        // adding delay because it's the easiest way to do so.
        // in reality tho, all entities
        if (manager.isEntity(living) || manager.isIgnored(living)) {
            return;
        }

        manager.createEntity(living);
        // who cares it's probably temp entity
        //Debug.warn("Created GameEntity for %s because a developer used the wrong way to spawn entities!", living.getName());
    }

    @EventHandler()
    public void handleEntityDeath(EntityDeathEvent ev) {
        final LivingEntity entity = ev.getEntity();
        if (entity instanceof Player) {
            return;
        }

        Manager.current().removeEntity(entity);
    }

    @EventHandler()
    public void handleTargetEvent(EntityTargetEvent ev) {
        final Entity entity = ev.getEntity();
        final Entity target = ev.getTarget();

        if (target == null) {
            return;
        }

        final GameTeam team = GameTeam.getEntryTeam(Entry.of(entity));

        if (team == null) {
            return;
        }

        if (!team.isEntry(Entry.of(target))) {
            return;
        }

        // Cancel targeting teammates
        ev.setTarget(null);
        ev.setCancelled(true);
    }

}
