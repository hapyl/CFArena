package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.UltimateTalent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BoomBow extends UltimateTalent {
	public BoomBow() {
		super("BOOM BOW", "Equip a &6&lBOOM BOW &7for 6s that fires explosive arrows which explodes on impact dealing massive damage.", 50);
		this.setCdSec(20);
		this.setItem(Material.BLAZE_POWDER);
	}

	@Override
	public Response execute(Player player) {
		return null;
	}
}
