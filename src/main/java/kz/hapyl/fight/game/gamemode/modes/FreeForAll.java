package kz.hapyl.fight.game.gamemode.modes;

import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.gamemode.CFGameMode;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class FreeForAll extends CFGameMode {
	public FreeForAll() {
		super("Fre for All", 600);
		this.setInfo("One life, one chance to win. Last man standing wins.");
		this.setPlayerRequirements(2);
		this.setMaterial(Material.IRON_SWORD);
	}

	@Override
	public boolean testWinCondition(@Nonnull GameInstance instance) {
		return instance.getAlivePlayers().size() <= 1;
	}

}
