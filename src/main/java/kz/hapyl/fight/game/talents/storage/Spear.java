package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Spear extends Talent {
	public Spear() {
		super(
				"Spear",
				"A knight without a spear is not a knight! Use your spear to dash forward and damage opponents on the way."
		);
		this.setItem(Material.TIPPED_ARROW, builder -> {
			builder.setPotionColor(Color.GRAY);
		});
		this.setCd(100);
	}

	@Override
	public Response execute(Player player) {
		player.setVelocity(player.getLocation().getDirection().setY(0.0d).multiply(1.5d));

		new GameTask() {
			private int tick = 15;

			@Override
			public void run() {
				if (tick < 0) {
					this.cancel();
					return;
				}

				Utils.getEntitiesInRange(player.getLocation(), 1.5d).forEach(entity -> {
					if (entity == player) {
						return;
					}

					GamePlayer.damageEntity(entity, 4.0d, player, EnumDamageCause.ENTITY_ATTACK);
				});

				--tick;
			}
		}.runTaskTimer(0, 1);

		// fx
		PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1.25f);
		return Response.OK;
	}
}
