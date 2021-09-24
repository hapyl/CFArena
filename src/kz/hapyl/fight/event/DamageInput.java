package kz.hapyl.fight.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DamageInput {

	private final Player player;
	private final LivingEntity entity;
	private final double damage;

	public DamageInput(Player player, @Nullable LivingEntity entity, double originalDamage) {
		this.player = player;
		this.entity = entity;
		this.damage = originalDamage;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public Player getPlayer() {
		return player;
	}

	public double getDamage() {
		return damage;
	}
}
