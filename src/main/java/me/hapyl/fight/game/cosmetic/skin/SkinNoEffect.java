package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.game.heroes.Heroes;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkinNoEffect extends Skin {
    public SkinNoEffect(Heroes hero, String name) {
        super(hero, name);
    }

    @Override
    public void onTick(Player player, int tick) {

    }

    @Override
    public void onKill(Player player, LivingEntity victim) {

    }

    @Override
    public void onDeath(Player player, LivingEntity killer) {

    }

    @Override
    public void onMove(Player player, Location to) {

    }

    @Override
    public void onStandingStill(Player player) {

    }
}
