package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.Response;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class UltimateTalent extends Talent {

	private final int cost;
	private Sound sound;
	private float pitch;
	private int duration;

	public UltimateTalent(String name, String info, int pointCost) {
		super(name, info, Type.ULTIMATE);
		this.cost = pointCost;
		this.sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
		this.pitch = 2.0f;
		this.duration = 0;
	}

	public UltimateTalent setDuration(int duration) {
		this.duration = duration;
		return this;
	}

	public UltimateTalent setDurationSec(int duration) {
		return setDuration(duration * 20);
	}

	public int getDuration() {
		return duration;
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

	@Deprecated
	public void useUltimate(Player player) {

	}

	@Deprecated
	@Override
	public final Response execute(Player player) {
		throw new IllegalStateException("use Hero#useUltimate");
		//this.useUltimate(player);
		//return Response.OK;
	}

	/**
	 * Must return true in order for talent to execute. If returns false shows a message.
	 *
	 * @param player - player to test.
	 * @see UltimateTalent#predicateMessage()
	 * @deprecated use Hero instead
	 */
	@Deprecated
	public boolean predicateUltimate(Player player) {
		return true;
	}

	@Deprecated
	public String predicateMessage() {
		return "invalid class call, use 'Hero#useUltimate' instead";
	}

}
