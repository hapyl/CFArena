package me.hapyl.fight;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.entity.Player;

public class Shortcuts {
    public static PlayerDatabase getDatabase(Player player) {
        return getProfile(player).getDatabase();
    }

    public static PlayerProfile getProfile(Player player) {
        return Manager.current().getProfile(player);
    }
}
