package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.util.Utils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Invisibility extends GameEffect {

	public Invisibility() {
		super("Invisibility");
		setDescription("Makes player invisible.");
	}

    @Override
    public void onStart(LivingEntity entity) {
        if (entity instanceof Player player) {
            Utils.hidePlayer(player);
        }

        entity.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 1));
    }

    @Override
    public void onStop(LivingEntity entity) {
        if (entity instanceof Player player) {
            Utils.showPlayer(player);
        }

        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {

    }
}
