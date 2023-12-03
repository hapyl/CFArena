package me.hapyl.fight.game.talents.storage.librarian;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.storage.extra.LibrarianTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LibrarianShield extends LibrarianTalent {
    public LibrarianShield() {
        super("Voidless Shield");

        addDescription("Creates a shield with voidless capacity of absorbing damage for &b{}&7 seconds.");
        setItem(Material.SHIELD);
    }

    @Override
    public Response executeGrimoire(Player player) {
        final int value = (int) (getCurrentValue(player) * 20);

        player.setInvulnerable(true);
        GameTask.runLater(() -> player.setInvulnerable(false), value);

        Chat.sendMessage(player, "&aApplied shield for %ss!", getCurrentValue(player));

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
