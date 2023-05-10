package me.hapyl.fight.game.gamemode.modes;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.gamemode.CFGameMode;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class AmongUs extends CFGameMode {
    public AmongUs() {
        super("Among Us", 1200);

        setMaterial(Material.SCULK_VEIN);
        setPlayerRequirements(3);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        return false;
    }
}
