package me.hapyl.fight.game.entity.overlay;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nullable;

public class OverlayListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleEntityDamageByEntityEvent(EntityDamageByEntityEvent ev) {
        final Entity damager = ev.getDamager();
        final OverlayNamedGameEntity overlayEntity = getOverlayEntity(damager);

        if (overlayEntity == null) {
            return;
        }

        overlayEntity.simulateAttack(ev);
    }

    @EventHandler()
    public void handleEntityDamage(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();
        final OverlayNamedGameEntity overlayEntity = getOverlayEntity(entity);

        if (overlayEntity == null) {
            return;
        }

        overlayEntity.simulateTakeDamage();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleEntityFire(EntityCombustEvent ev) {
        final Entity entity = ev.getEntity();

        if (isOverlayEntity(entity)) {
            ev.setCancelled(true);
        }
    }

    @Nullable
    public OverlayNamedGameEntity getOverlayEntity(Entity entity) {
        final GameEntity gameEntity = Manager.current().getEntity(entity.getUniqueId());

        if (gameEntity instanceof OverlayNamedGameEntity overlayNamedGameEntity) {
            return overlayNamedGameEntity;
        }

        return null;
    }

    private boolean isOverlayEntity(Entity entity) {
        return entity.getScoreboardTags().contains(OverlayNamedGameEntity.OVERLAY_TAG);
    }

}
