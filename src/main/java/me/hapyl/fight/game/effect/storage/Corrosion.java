package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Corrosion extends GameEffect {

    public final int DAMAGE_PERIOD = 10;

    public Corrosion() {
        super("Corrosion");
        this.setDescription("Slows, disturbs vision and rapidly damages players.");
        this.setPositive(false);
    }

    @Override
    public void onTick(Player player, int tick) {
        if (tick % DAMAGE_PERIOD == 0) {
            GamePlayer.damageEntityTick(player, 1.0d, null, EnumDamageCause.CORROSION, DAMAGE_PERIOD);
        }
    }

    @Override
    public void onStart(Player player) {
        PlayerLib.addEffect(player, PotionEffectType.SLOW, 999999, 4);
        PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 999999, 4);
    }

    @Override
    public void onStop(Player player) {
        PlayerLib.removeEffect(player, PotionEffectType.SLOW);
        PlayerLib.removeEffect(player, PotionEffectType.BLINDNESS);
    }

}
