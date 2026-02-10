package me.hapyl.fight.game.heroes.rogue;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class RogueData extends PlayerData {

    protected boolean secondWindAvailable;

    public RogueData(GamePlayer player) {
        super(player);

        this.secondWindAvailable = true;
    }

    public void refreshSecondWind() {
        this.secondWindAvailable = true;

        player.spawnBuffDisplay("&a&l+ " + Named.SECOND_WIND, 30);

        player.playSound(Sound.ENTITY_PHANTOM_AMBIENT, 0.75f);
        player.playSound(Sound.ENTITY_PHANTOM_HURT, 1.25f);

        player.spawnWorldParticle(player.getMidpointLocation(), Particle.ENCHANT, 50, 0, 0, 0, 1);
    }

    public boolean secondWindAvailable() {
        return secondWindAvailable;
    }

    @Override
    public void remove() {
    }
}
