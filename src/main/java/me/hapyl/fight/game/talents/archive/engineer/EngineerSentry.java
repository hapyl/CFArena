package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Response;
import org.bukkit.entity.Player;

public class EngineerSentry extends EngineerTalent {
    public EngineerSentry() {
        super("Sentry", 5);

        setDescription("""
                Create a sentry.
                """);
    }

    @Override
    public Response create(Player player) {
        return null;
    }
}
