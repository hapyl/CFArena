package me.hapyl.fight.game.talents.archive.sun;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class SyntheticSun extends Talent {
    public SyntheticSun() {
        super("Synthetic Sun");

        setDescription("""
                Create a {name} in front of you that gradually expands while pulling enemies in.
                                
                After {duration}, explode it and deal lethal damage to enemies in range.
                """);
    }

    @Override
    public Response execute(Player player) {
        return null;
    }
}
