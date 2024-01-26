package me.hapyl.fight.event;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.GameEntityContactPortalEvent;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EntityHandler implements Listener {

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
        if (target.hasEffect(Effects.INVISIBILITY)) {
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

    private void callGameEntityContactPortalEvent(LivingGameEntity entity, GameEntityContactPortalEvent.PortalType type) {
        if (type == null) {
            return;
        }

        final LivingEntity bukkitEntity = entity.getEntity();
        final int portalCooldown = bukkitEntity.getPortalCooldown();

        if (portalCooldown > 0) {
            return;
        }

        bukkitEntity.setPortalCooldown(20);
        new GameEntityContactPortalEvent(entity, type).call();
    }

}
