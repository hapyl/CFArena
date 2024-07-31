package me.hapyl.fight.anticheat.trait;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.hapyl.fight.anticheat.AntiCheat;
import me.hapyl.fight.anticheat.AntiCheatCheck;
import me.hapyl.fight.anticheat.AntiData;
import me.hapyl.fight.anticheat.Variables;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AntiTraitCPS extends AntiTrait {

    public AntiTraitCPS() {
    }

    @EventHandler
    public void handle(PrePlayerAttackEntityEvent ev) {
        final Entity entity = ev.getAttacked();
        final Player player = ev.getPlayer();

        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        final AntiData data = AntiCheat.getInstance().getData(player);
        data.recentCps.click();

        final int size = data.recentCps.size();

        if (size >= Variables.MAX_CPS.getSoftLimit()) {
            ev.setCancelled(true);

            if (size >= Variables.MAX_CPS.getHardLimit()) {
                data.checks.fail(AntiCheatCheck.CPS);
            }
        }
    }

}
