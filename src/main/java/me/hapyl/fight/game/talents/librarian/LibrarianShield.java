package me.hapyl.fight.game.talents.librarian;


import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class LibrarianShield extends LibrarianTalent {
    public LibrarianShield(@Nonnull Key key) {
        super(key, "Voidless Shield");

        addDescription("Creates a shield with voidless capacity of absorbing damage for &b<scaled>&7 seconds.");
        setItem(Material.SHIELD);
    }

    @Override
    public Response executeGrimoire(@Nonnull GamePlayer player) {
        final int value = (int) (getCurrentValue(player) * 20);

        player.setInvulnerable(true);
        player.schedule(() -> player.setInvulnerable(false), value);

        player.sendMessage("&aApplied shield for %ss!".formatted(getCurrentValue(player)));

        return Response.OK;
    }

    @Override
    public int getGrimoireCd() {
        return 30;
    }

    @Override
    public double[] getValues() {
        return new double[] { 5, 6, 7, 8 };
    }
}
