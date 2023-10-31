package me.hapyl.fight.game.talents.archive.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect {

    private final String effectName;
    private final String effectChar;
    private final PotionEffect potionEffect;

    public void affect(GamePlayer player) {
    }

    public Effect(String effectChar, String effectName, PotionEffectType effect, int duration, int level) {
        this.effectChar = effectChar;
        this.effectName = effectName;
        this.potionEffect = effect == null ? null : new PotionEffect(effect, duration, level);
    }

    public Effect(String effectChar, String effectName) {
        this(effectChar, effectName, null, 0, 0);
    }

    public String getEffectChar() {
        return effectChar;
    }

    public String getEffectName() {
        return effectName;
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    public void applyEffectsIgnoreFx(GamePlayer player) {
        affect(player);

        if (potionEffect != null) {
            player.addPotionEffect(potionEffect.getType(), potionEffect.getDuration(), potionEffect.getAmplifier());
        }
    }

    public void applyEffects(GamePlayer player) {
        applyEffectsIgnoreFx(player);

        // Fx
        player.playSound(Sound.ENTITY_PLAYER_SWIM, 1.8f);
        player.sendTitle("&a" + effectChar, "&6Gained " + effectName, 5, 10, 5);
        player.sendMessage("&a" + effectChar + " &6Gained " + effectName);
    }

}
