package kz.hapyl.fight.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nullable;

public class DamageInput {

	private final Player player;
	private final LivingEntity entity;
	private final double damage;
	private final EntityDamageEvent.DamageCause cause;

	public DamageInput(Player player, @Nullable LivingEntity entity, @Nullable EntityDamageEvent.DamageCause damageCause, double originalDamage) {
		this.player = player;
		this.entity = entity;
		this.damage = originalDamage;
		this.cause = damageCause;
	}

	public DamageInput(Player player, @Nullable LivingEntity entity, double originalDamage) {
		this(player, entity, null, originalDamage);
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public EntityDamageEvent.DamageCause getDamageCause() {
		return cause;
	}

	public Player getPlayer() {
		return player;
	}

	public double getDamage() {
		return damage;
	}
}
