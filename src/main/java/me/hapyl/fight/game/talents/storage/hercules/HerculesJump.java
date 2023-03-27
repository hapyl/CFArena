package me.hapyl.fight.game.talents.storage.hercules;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HerculesJump extends Talent {
	public HerculesJump() {
        super("Updraft", "Instantly propel yourself high up to perform plunging attack.", Type.COMBAT);

        setCdSec(10);
        setItem(Material.SLIME_BALL);
    }

	@Override
	public Response execute(Player player) {
		player.setVelocity(new Vector(0.0d, 1.05d, 0.0d));

		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 0.5f);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.25f);

		return Response.OK;
	}
}
