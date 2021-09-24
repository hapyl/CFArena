package kz.hapyl.fight.game.effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class GameEffect {

	private final String name;
	private String about;
	private boolean isPositive;
	private EffectParticle effectParticle;

	public GameEffect(String name) {
		this.name = name;
		this.about = "";
		this.isPositive = true;
	}

	public void setEffectParticle(EffectParticle effectParticle) {
		this.effectParticle = effectParticle;
	}

	public String getName() {
		return name;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public void setPositive(boolean positive) {
		isPositive = positive;
	}

	public String getAbout() {
		return about;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public abstract void onTick(Player player, int tick);

	public abstract void onStart(Player player);

	public abstract void onStop(Player player);

	public void onUpdate(Player player) {

	}

	public String getExtra() {
		return "";
	}


	public void displayParticles(Location location, Player ignore) {
		if (effectParticle == null) {
			return;
		}
		effectParticle.display(location, ignore);
	}

}
