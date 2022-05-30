package me.hapyl.fight.event;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EnderPearlController implements Listener {

	@EventHandler
	public void handleHit(ProjectileHitEvent ev) {
		if (ev.getEntity() instanceof EnderPearl enderPearl && enderPearl.getShooter() instanceof Player player) {
			Location location = enderPearl.getLocation();
			if (!isSafeLocation(location)) {
				enderPearl.remove();
				//if (player.getGameMode() != GameMode.CREATIVE) {
				//	player.getInventory().addItem(enderPearl.getItem());
				//}
				Chat.sendMessage(player, "&cYou cannot travel there using Ender Pearls!");
				PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
				ev.setCancelled(true);
			}
			else {
				enderPearl.getPassengers().forEach(Entity::eject);
				location.setYaw(player.getLocation().getYaw());
				location.setPitch(player.getLocation().getPitch());
				GamePlayer.getPlayer(player).addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, 20, true);
				player.teleport(location);
				PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
			}
		}
	}

	private boolean isSafeLocation(Location location) {
		return isSafeBlock(location.getBlock()) && isSafeBlock(location.getBlock().getRelative(BlockFace.UP));
	}

	private boolean isSafeBlock(Block block) {
		return block.getType().isAir() || block.isPassable();
	}

}
