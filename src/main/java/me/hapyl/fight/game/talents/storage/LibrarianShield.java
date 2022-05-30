package me.hapyl.fight.game.talents.storage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.extra.GrimoireTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LibrarianShield extends Talent implements GrimoireTalent {
    public LibrarianShield() {
        super("Voidless Shield");
        this.setInfo(String.format(
                "Creates a shield with voidless capacity of absorbing damage for &b%s&7 seconds.",
                formatValues()
        ));
        this.setItem(Material.SHIELD);
        this.setAutoAdd(false);
    }

    @Override
    public Response execute(Player player) {
        if (HeroHandle.LIBRARIAN.hasICD(player)) {
            return ERROR;
        }

        final int value = (int) (getCurrentValue(player) * 20);

        player.setInvulnerable(true);
        GameTask.runLater(() -> {
            player.setInvulnerable(false);
        }, value);

        HeroHandle.LIBRARIAN.removeSpellItems(player, Talents.LIBRARIAN_SHIELD);
        Chat.sendMessage(player, "&aApplied shield for %ss!", value);
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
