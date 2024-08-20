package me.hapyl.fight.game.talents.librarian;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class WeaponDarkness extends LibrarianTalent {
    public WeaponDarkness(@Nonnull DatabaseKey key) {
        super(key, "Infusion of Darkness");

        addDescription("Infuses your weapon for &b<scaled>s&7 with higher damage.");
        setItem(Material.INK_SAC);
    }

    @Override
    public Response executeGrimoire(@Nonnull GamePlayer player) {
        // fixme -> strength

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
