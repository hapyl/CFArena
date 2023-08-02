package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Utils;
import org.bukkit.potion.PotionEffectType;

public class Invisibility extends GameEffect {

    public Invisibility() {
        super("Invisibility");
        setDescription("Makes player invisible.");
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        entity.asPlayer(Utils::hidePlayer);
        entity.addPotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        entity.asPlayer(Utils::showPlayer);
        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
    }
}
