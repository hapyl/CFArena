package kz.hapyl.fight.game;

import kz.hapyl.fight.game.effect.GameEffectType;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public class AbstractGamePlayer {

	public static final AbstractGamePlayer NULL_GAME_PLAYER = new AbstractGamePlayer();

	// this works as IllegalStateException but without the error
	private void displayError() {
		//Bukkit.getConsoleSender().sendMessage("&4IllegalState! &cCalled null IGamePlayer");
	}

	public String getStatusString() {
		return isDead() ? "&cDead" : isSpectator() ? "&7Spectator" : "&aAlive";
	}

	public boolean isDead() {
		return false;
	}

	public boolean isSpectator() {
		return true;
	}

	public boolean isAlive() {
		displayError();
		return false;
	}

	public void heal(double d) {
		displayError();
	}

	public void damage(double d) {
		displayError();
	}

	public void damage(double d, EnumDamageCause cause) {
		displayError();
	}

	public void damage(double d, LivingEntity damager, EnumDamageCause cause) {
		displayError();
	}

	public void die(boolean force) {
		displayError();
	}

	// effects
	public void addEffect(GameEffectType type, int ticks) {
		displayError();
	}

	public void addEffect(GameEffectType type, int ticks, boolean override) {
		displayError();
	}

	public boolean hasEffect(GameEffectType type) {
		displayError();
		return false;
	}

	public void removeEffect(GameEffectType type) {
		displayError();
	}

	public EnumDamageCause getLastDamageCause() {
		displayError();
		return EnumDamageCause.NONE;
	}

	@Nullable
	public LivingEntity getLastDamager() {
		return null;
	}

	public void setLastDamager(LivingEntity entity) {

	}

	public void setHealth(double d) {
		displayError();
	}

	public double getHealth() {
		displayError();
		return getMaxHealth();
	}

	public double getMaxHealth() {
		displayError();
		return 100.d;
	}

	public boolean isUltimateReady() {
		return false;
	}

	public void sendMessage(String message, Object... objects) {

	}

	@Nullable
	public StatContainer getStats() {
		return null;
	}

	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
	}

	public void sendActionbar(String text, Object... objects) {
	}

}
