package me.hapyl.fight.event;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.GameEntityContactPortalEvent;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.function.Consumer;

public class EntityHandler implements Listener {

    private static final EntityCooldown COOLDOWN = EntityCooldown.of("portal", 1000L);

    @EventHandler()
    public void handlePlayerPortal(PlayerTeleportEvent ev) {
        final PlayerTeleportEvent.TeleportCause cause = ev.getCause();
        final GamePlayer gamePlayer = CF.getPlayer(ev.getPlayer());

        switch (cause) {
            case NETHER_PORTAL, END_PORTAL, END_GATEWAY -> {
                // Cancel event either way, doesn't matter if in a game or not
                ev.setCancelled(true);

                if (gamePlayer == null) {
                    return;
                }

                final GameEntityContactPortalEvent.PortalType portalType = GameEntityContactPortalEvent.PortalType.fromCause(cause);
                callGameEntityContactPortalEvent(gamePlayer, portalType);
            }
        }
    }

    @EventHandler()
    public void handleEntityPortal(EntityPortalEvent ev) {
        final Entity entity = ev.getEntity();
        final LivingGameEntity gameEntity = CF.getEntity(entity);

        // Cancel either way, doesn't matter if in a game or not
        ev.setCancelled(true);

        if (gameEntity == null) {
            return;
        }

        final Block block = ev.getFrom().getBlock();
        final GameEntityContactPortalEvent.PortalType portalType = GameEntityContactPortalEvent.PortalType.fromBlock(block);

        callGameEntityContactPortalEvent(gameEntity, portalType);
    }

    @EventHandler()
    public void handleEntitySpawn(EntitySpawnEvent ev) {
        final Entity entity = ev.getEntity();
        final Manager manager = Manager.current();

        // Mark display entities as 'temporary' entities to remove them
        if (entity instanceof BlockDisplay || entity instanceof ItemDisplay) {
            SynchronizedGarbageEntityCollector.add(entity);
            return;
        }

        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        // adding delay because it's the easiest way to do so.
        // in reality tho, all entities

        // ALL ENTITIES WHAT? FINISH YOUR FUCKING SENTENCE
        // And what delay are you talking about?
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
        final LivingGameEntity target = CF.getEntity(ev.getTarget());

        if (target == null) {
            return;
        }

        // Don't target invisible entities
        if (target.hasEffect(EffectType.INVISIBILITY)) {
            ev.setTarget(null);
            ev.setCancelled(true);
            return;
        }

        final GameTeam team = GameTeam.getEntryTeam(Entry.of(entity));

        // Don't target teammates
        if (team != null && team.isEntry(Entry.of(target))) {
            ev.setTarget(null);
            ev.setCancelled(true);
        }
    }

    private void forEachNamedEntity(Consumer<CommissionEntity> consumer) {
        CF.getEntities().forEach(gameEntity -> {
            if (gameEntity instanceof CommissionEntity named) {
                consumer.accept(named);
            }
        });
    }

    private void callGameEntityContactPortalEvent(LivingGameEntity entity, GameEntityContactPortalEvent.PortalType type) {
        if (type == null) {
            return;
        }

        if (entity.hasCooldown(COOLDOWN)) {
            return;
        }

        entity.startCooldown(COOLDOWN);
        new GameEntityContactPortalEvent(entity, type).call();
    }

}
