package me.hapyl.fight.game.talents.storage.heavy_knight;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class Updraft extends Talent {
    public Updraft() {
        super("Updraft", "Leap into the air and smash down players lifted by Uppercut.");
    }

    @Override
    public Response execute(Player player) {
        return null;
    }
}
