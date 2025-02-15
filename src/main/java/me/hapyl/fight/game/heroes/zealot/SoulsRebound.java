package me.hapyl.fight.game.heroes.zealot;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class SoulsRebound extends GameTask {

    private final Zealot zealot;
    private final Player player;
    private final Map<LivingGameEntity, Double> damageTaken;

    public SoulsRebound(Zealot zealot, Player player) {
        this.zealot = zealot;
        this.player = player;
        this.damageTaken = Maps.newHashMap();

        runTaskLater(zealot.getUltimateDuration());
    }

    @Override
    public final void run() {
        damageTaken.forEach((entity, damage) -> {
            entity.damage(damage, player, DamageCause.SOULS_REBOUND);

            // Fx
            entity.sendMessage("&d👻 &5Took &c%s❤&5 damage from %s's Souls Rebound!".formatted(
                    CFUtils.decimalFormat(damage),
                    player.getName()
            ));
            entity.playSound(Sound.ITEM_SHIELD_BREAK, 0.0f);
            entity.playSound(Sound.ENTITY_ENDERMAN_HURT, 0.25f);
            entity.playSound(Sound.ENTITY_ENDERMAN_HURT, 0.5f);

            Debug.info("dealt " + damage + " to " + entity.getName());
        });

        damageTaken.clear();
    }

    public void addDamage(LivingGameEntity entity, double damage) {
        damageTaken.compute(entity, (e, d) -> d == null ? damage : d + damage);

        PlayerLib.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f);
        PlayerLib.playSound(player, Sound.BLOCK_SOUL_SAND_BREAK, 0.75f);
    }

}
