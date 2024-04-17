package me.hapyl.fight.game.gamemode.modes;

import me.hapyl.fight.CF;
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
    public void onStart(@Nonnull GameInstance instance) {
        // FIXME (hapyl): 028, Nov 28:
        //CF.getPlayers().forEach(player -> player.setCooldownModifier(0.5d));
    }

    @Override
    public void tick(@Nonnull GameInstance instance, int tick) {
        if (tick % 20 != 0) {
            return;
        }

        CF.getAlivePlayers().forEach(player -> {
            player.addEnergy(1);
        });
    }
}
