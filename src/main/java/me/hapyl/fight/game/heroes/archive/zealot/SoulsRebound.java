package me.hapyl.fight.game.heroes.archive.zealot;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

public class SoulsRebound extends GameTask implements HeroReference<Zealot> {

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
            entity.damage(damage, player, EnumDamageCause.SOULS_REBOUND);

            // Fx
            entity.sendMessage("&d👻 &5Took &c%s❤&5 damage from %s's Souls Rebound!", CFUtils.decimalFormat(damage), player.getName());
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

    @Nonnull
    @Override
    public Zealot getHero() {
        return zealot;
    }
}
