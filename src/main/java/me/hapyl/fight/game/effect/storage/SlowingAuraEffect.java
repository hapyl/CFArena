package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SlowingAuraEffect extends GameEffect {

    public final int COOLDOWN_MODIFIER = 2;

    public SlowingAuraEffect() {
        super("Slowing Aura");
        setDescription("Slows player and increases talent cooldowns.");
        setPositive(false);
    }

    @Override
    public void onStart(Player player) {
        PlayerLib.addEffect(player, PotionEffectType.SLOW, 10000, 1);
    }

    @Override
    public void onStop(Player player) {
        PlayerLib.removeEffect(player, PotionEffectType.SLOW);
    }

    @Override
    public void onTick(Player player, int tick) {

    }
}
