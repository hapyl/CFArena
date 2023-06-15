package me.hapyl.fight.game.talents;

import me.hapyl.fight.annotate.SelfCallable;
import org.bukkit.entity.Player;

public interface Creation extends Removable {

    @SelfCallable(false)
    void create(Player player);

}
