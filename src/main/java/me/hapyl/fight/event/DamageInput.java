package me.hapyl.fight.event;

import me.hapyl.fight.game.EnumDamageCause;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DamageInput {

	private final Player player;
	private final LivingEntity entity;
    private final double damage;
    private final EnumDamageCause cause;

    public DamageInput(Player player, @Nullable LivingEntity entity, @Nullable EnumDamageCause damageCause, double originalDamage) {
        this.player = player;
        this.entity = entity;
        this.damage = originalDamage;
        this.cause = damageCause;
    }

	public DamageInput(Player player, @Nullable LivingEntity entity, double originalDamage) {
		this(player, entity, null, originalDamage);
	}

    @Nullable
    public LivingEntity getEntity() {
        return entity;
    }

    public EnumDamageCause getDamageCause() {
        return cause;
    }

	public Player getPlayer() {
		return player;
	}

	public double getDamage() {
		return damage;
	}
}
