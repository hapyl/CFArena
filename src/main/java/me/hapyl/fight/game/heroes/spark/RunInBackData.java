package me.hapyl.fight.game.heroes.spark;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

public record RunInBackData(GamePlayer player, Location location, double health) {
}
