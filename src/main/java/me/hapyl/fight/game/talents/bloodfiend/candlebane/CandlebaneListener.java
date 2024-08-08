package me.hapyl.fight.game.talents.bloodfiend.candlebane;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.event.custom.PlayerClickAtEntityEvent;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.eterna.module.event.protocol.PacketReceiveEvent;
import me.hapyl.eterna.module.reflect.packet.wrapped.PacketWrappers;
import me.hapyl.eterna.module.reflect.packet.wrapped.WrappedPacketPlayInUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class CandlebaneListener implements Listener, TalentReference<CandlebaneTalent> {

    @EventHandler()
    public void handlePacketReceiveEvent(PlayerClickAtEntityEvent ev) {
        final GamePlayer player = ev.getPlayer();
        final Entity entity = ev.getEntity();

        getTalent().getPillars().forEach(pillar -> {
            if (!pillar.isPart(entity) || pillar.isAnimation()) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    pillar.click(player, ev.isLeftClick() ? ClickType.LEFT : ClickType.RIGHT);
                }
            }.runTask(Main.getPlugin());
        });
    }

    @Nonnull
    @Override
    public CandlebaneTalent getTalent() {
        return Talents.CANDLEBANE.getTalent(CandlebaneTalent.class);
    }
}
