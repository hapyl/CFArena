package me.hapyl.fight.game.talents.storage.witcher;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.BukkitUtils;
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
