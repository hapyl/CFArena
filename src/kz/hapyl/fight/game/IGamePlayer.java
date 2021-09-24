package kz.hapyl.fight.game;

import kz.hapyl.fight.game.effect.GameEffectType;
import org.bukkit.entity.LivingEntity;

public interface IGamePlayer {

	boolean isAlive();

	void heal(double d);

	void damage(double d);

	void damage(double d, EnumDamageCause cause);

	void damage(double d, LivingEntity damager, EnumDamageCause cause);

	void die(boolean force);

	// effects
	void addEffect(GameEffectType type, int ticks);

	void addEffect(GameEffectType type, int ticks, boolean override);

	boolean hasEffect(GameEffectType type);

	void removeEffect(GameEffectType type);

	EnumDamageCause getLastDamageCause();

	void setHealth(double d);

	double getHealth();

	double getMaxHealth();

	boolean isUltimateReady();

}
