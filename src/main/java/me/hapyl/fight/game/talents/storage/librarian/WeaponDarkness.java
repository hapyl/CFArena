package me.hapyl.fight.game.talents.storage.librarian;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.storage.extra.LibrarianTalent;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponDarkness extends LibrarianTalent {
    public WeaponDarkness() {
        super("Infusion of Darkness");

        setDescription("Infuses your weapon for &b%ss&7 with higher damage.");
        setItem(Material.INK_SAC);
    }

    @Override
    public Response executeGrimoire(Player player) {
        PlayerLib.addEffect(player, EffectType.STRENGTH, (int) (getCurrentValue(player) * 20), 1);

        return Response.OK;
    }

    @Override
    public int getGrimoireCd() {
        return 30;
    }

    @Override
    public double[] getValues() {
        return new double[] { 5.0d, 6.0d, 7.0d, 8.0d };
    }
}
