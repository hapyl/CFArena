package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.Response;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public abstract class UltimateTalent extends Talent {

	private final int cost;

	private Sound sound;
	private float pitch;

	public UltimateTalent(String name, String description, int pointCost) {
		super(name, description, Type.ULTIMATE);
		this.cost = pointCost;
		this.sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
		this.pitch = 2.0f;
	}

	public UltimateTalent setSound(Sound sound) {
		this.sound = sound;
		return this;
	}

	public UltimateTalent setSound(Sound sound, float pitch) {
		setSound(sound);
		setPitch(pitch);
		return this;
	}

	public UltimateTalent setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}

	public float getPitch() {
		return pitch;
	}

	public Sound getSound() {
		return sound;
	}

	public int getCost() {
		return cost;
	}

	public UltimateTalent setItem(Material material) {
		super.setItem(material);
		return this;
	}

	@Override
	public UltimateTalent setItem(String headTexture) {
		super.setItem(headTexture);
		return this;
	}

	@Override
	public UltimateTalent setCd(int cd) {
		super.setCd(cd);
		return this;
	}

	@Override
	public UltimateTalent setCdSec(int cd) {
		super.setCdSec(cd);
		return this;
	}

	public abstract void useUltimate(Player player);

	// use @useUltimate
	@Override
	public final Response execute(Player player) {
		this.useUltimate(player);
		return Response.OK;
	}

	/**
	 * Must return true in order for talent to execute. If returns false shows a message.
	 *
	 * @param player - player to test.
	 * @see UltimateTalent#predicateMessage()
	 */
	public boolean predicate(Player player) {
		return true;
	}

	public String predicateMessage() {
		return "can not use this.";
	}

}
