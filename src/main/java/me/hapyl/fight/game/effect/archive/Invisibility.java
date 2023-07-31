package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.util.Utils;
import org.bukkit.potion.PotionEffectType;

public class Invisibility extends GameEffect {

    public Invisibility() {
        super("Invisibility");
        setDescription("Makes player invisible.");
    }

    @Override
    public void onStart(GameEntity entity) {
        entity.asPlayer(Utils::hidePlayer);
        entity.addPotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
    }

    @Override
    public void onStop(GameEntity entity) {
        entity.asPlayer(Utils::showPlayer);
        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public void onTick(GameEntity entity, int tick) {
    }
}
