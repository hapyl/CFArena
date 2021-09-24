package kz.hapyl.fight.game;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class ProcessResult {

	private final Player victim;
	private final double originalDamage;
	private double damage;
	private boolean cancelled;

	public ProcessResult(Player victim, double originalDamage) {
		this.victim = victim;
		this.originalDamage = originalDamage;
		this.damage = originalDamage;
		this.cancelled = false;
	}

	public Player getVictim() {
		return victim;
	}

	public void setCancelled(boolean flag) {
		this.cancelled = flag;
	}

	public double getOriginalDamage() {
		return originalDamage;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public void feedEvent(EntityDamageEvent event) {
		if (this.cancelled) {
			event.setCancelled(true);
		}
	}

}
