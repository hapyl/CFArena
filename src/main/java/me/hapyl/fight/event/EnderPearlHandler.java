package me.hapyl.fight.event;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.EnderPearlTeleportEvent;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.MessageType;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

/**
 * Handles ender pearls.
 */
public final class EnderPearlHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleHit(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof EnderPearl enderPearl) || !(enderPearl.getShooter() instanceof Player player)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);
        final Location location = enderPearl.getLocation();

        if (gamePlayer == null) {
            return;
        }

        enderPearl.remove();
        ev.setCancelled(true);

        if (!isSafeLocation(location)) {
            gamePlayer.sendMessage(MessageType.ERROR, "You cannot travel there using Ender Pearls!");
            gamePlayer.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        final Location playerLocation = player.getLocation();
        enderPearl.getPassengers().forEach(Entity::eject);

        location.setYaw(playerLocation.getYaw());
        location.setPitch(playerLocation.getPitch());

        final EnderPearlTeleportEvent event = new EnderPearlTeleportEvent(gamePlayer, location);

        if (event.callAndCheck()) {
            return;
        }

        gamePlayer.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 20, true);
        gamePlayer.teleport(location);
        gamePlayer.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
    }

    private boolean isSafeLocation(Location location) {
        return isSafeBlock(location.getBlock()) && isSafeBlock(location.getBlock().getRelative(BlockFace.UP));
    }

    private boolean isSafeBlock(Block block) {
        return block.getType().isAir() || block.isPassable();
    }

}
