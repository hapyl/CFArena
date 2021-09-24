package kz.hapyl.fight.game;

import kz.hapyl.fight.game.effect.GameEffectType;
import org.bukkit.entity.LivingEntity;

public class SafeGamePlayer implements IGamePlayer {
	@Override
	public boolean isAlive() {
		return false;
	}

	@Override
	public void heal(double d) {

	}

	@Override
	public void damage(double d) {

	}

	@Override
	public void damage(double d, EnumDamageCause cause) {

	}

	@Override
	public void damage(double d, LivingEntity damager, EnumDamageCause cause) {

	}

	@Override
	public void die(boolean force) {

	}

	@Override
	public void addEffect(GameEffectType type, int ticks) {

	}

	@Override
	public void addEffect(GameEffectType type, int ticks, boolean override) {

	}

	@Override
	public boolean hasEffect(GameEffectType type) {
		return false;
	}

	@Override
	public void removeEffect(GameEffectType type) {

	}

	@Override
	public EnumDamageCause getLastDamageCause() {
		return null;
	}

	@Override
	public void setHealth(double d) {

	}

	@Override
	public double getHealth() {
		return 0;
	}

	@Override
	public double getMaxHealth() {
		return 0;
	}

	@Override
	public boolean isUltimateReady() {
		return false;
	}
}
