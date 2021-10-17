package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.storage.extra.GrimoireTalent;
import kz.hapyl.spigotutils.module.player.EffectType;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponDarkness extends Talent implements GrimoireTalent {
	public WeaponDarkness() {
		super("Infusion of Darkness");
		this.setInfo(String.format("Infuses your weapon for &b%ss&7 with higher damage.", formatValues()));
		this.setItem(Material.INK_SAC);
		this.setAutoAdd(false);
	}

	@Override
	protected Response execute(Player player) {
		if (HeroHandle.LIBRARIAN.hasICD(player)) {
			return ERROR;
		}

		PlayerLib.addEffect(player, EffectType.STRENGTH, (int)(getCurrentValue(player) * 20), 1);

		HeroHandle.LIBRARIAN.removeSpellItems(player, Talents.WEAPON_DARKNESS);
		return Response.OK;
	}

	@Override
	public int getGrimoireCd() {
		return 30;
	}

	@Override
	public double[] getValues() {
		return new double[]{10.0d, 13.0d, 16.0d, 19.0d};
	}
}
