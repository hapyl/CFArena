package me.hapyl.fight.game.talents.bloodfiend.candlebane;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.PlayerClickAtEntityEvent;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CandlebanePacketHandler implements Listener {

    @EventHandler()
    public void handlePacketReceiveEvent(PlayerClickAtEntityEvent ev) {
        final GamePlayer player = ev.getPlayer();
        final Entity entity = ev.getEntity();

        TalentRegistry.CANDLEBANE.getPillars().forEach(pillar -> {
            if (!pillar.isPart(entity) || pillar.isAnimation()) {
                return;
            }
            
            CF.synchronizeToMainThread(() -> {
                pillar.click(player, ev.isLeftClick() ? ClickType.LEFT : ClickType.RIGHT);
            });
        });
    }

}
