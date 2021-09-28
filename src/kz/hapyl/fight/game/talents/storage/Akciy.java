package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Akciy extends Talent {

	private final int stunDuration = 100;

	public Akciy() {
		super("Axii");
		this.setDescription(
				"Stuns your target opponent for &b%ss &7or until they get hit. Stunned opponent are immovable and cannot use their abilities.",
				BukkitUtils.roundTick(stunDuration)
		);
		this.setCdSec(40);
		this.setItem(Material.SLIME_BALL);
	}

	@Override
	public Response execute(Player player) {
		final Player target = Utils.getTargetPlayer(player, 50.0d);

		if (target == null) {
			return Response.error("No valid target!");
		}

		GamePlayer.getPlayer(target).addEffect(GameEffectType.STUN, stunDuration);
		Chat.sendMessage(player, "&aStunned %s!", target.getName());

		return Response.OK;
	}
}
