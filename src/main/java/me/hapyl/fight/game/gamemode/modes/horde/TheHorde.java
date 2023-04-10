package me.hapyl.fight.game.gamemode.modes.horde;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.gamemode.CFGameMode;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class TheHorde extends CFGameMode {
    public TheHorde() {
        super("The Horde", 1800);

        setDescription("Survive endless waves of enemies. The more you kill, the more you get rewarded.");
        setPlayerRequirements(1);
        setMaterial(Material.ZOMBIE_HEAD);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        return false;
    }
}
