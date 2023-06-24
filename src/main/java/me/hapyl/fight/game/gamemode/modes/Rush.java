package me.hapyl.fight.game.gamemode.modes;

import me.hapyl.fight.game.GameInstance;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Rush extends DeathmatchKills {

    public Rush() {
        super("RUSH", 1200, 20);

        setDescription("""
                A faster version of kills death-match with lower cooldowns and accelerated ultimates!
                First player to reach &a%s&7 kills wins!
                 """.formatted(killsGoal));

        setPlayerRequirements(2);
        setMaterial(Material.CLOCK);

        setAllowRespawn(true);
        setRespawnTime(20);
    }

    @Override
    public boolean onStart(@Nonnull GameInstance instance) {
        instance.getPlayers().values().forEach(player -> player.setCooldownModifier(0.5d));
        return false;
    }

    @Override
    public void tick(@Nonnull GameInstance instance, int tick) {
        if (tick % 20 != 0) {
            return;
        }

        instance.getAlivePlayers().forEach(player -> {
            player.addUltimatePoints(1);
        });
    }
}
