package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NinjaDash extends Talent {
	public NinjaDash() {
		super("Dashing Wind", "Instantly propel yourself into direction you looking.");
		this.setItem(Material.FEATHER);
		this.setCd(100);
	}

	@Override
	protected Response execute(Player player) {
		final Vector vector = player.getLocation().getDirection();
		player.setVelocity(new Vector(vector.getX(), 0, vector.getZ()).normalize().multiply(1.5f));
		PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1.1f);
		return Response.OK;
	}
}
