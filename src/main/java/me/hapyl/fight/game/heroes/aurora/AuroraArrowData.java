package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.aurora.AuroraArrowTalent;
import org.bukkit.entity.Arrow;

public record AuroraArrowData(GamePlayer player, AuroraArrowTalent type, Arrow arrow) {
}
