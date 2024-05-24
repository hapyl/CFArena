package me.hapyl.fight.game.talents.bloodfiend.candlebane;

import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.event.protocol.PacketReceiveEvent;
import me.hapyl.spigotutils.module.reflect.packet.wrapped.PacketWrappers;
import me.hapyl.spigotutils.module.reflect.packet.wrapped.WrappedPacketPlayInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class CandlebaneListener implements Listener, TalentReference<CandlebaneTalent> {
    public CandlebaneListener() {
    }

    @EventHandler()
    public void handlePacketReceiveEvent(PacketReceiveEvent ev) {
        final Player player = ev.getPlayer();
        final WrappedPacketPlayInUseEntity packet = ev.getWrappedPacket(PacketWrappers.PACKET_PLAY_IN_USE_ENTITY);

        if (packet == null) {
            return;
        }

        final WrappedPacketPlayInUseEntity.WrappedAction action = packet.getAction();
        // TODO (hapyl): Thu, May 16:  
    }

    //public void onPacketReceiving(PacketEvent event) {
    //    final Player player = event.getPlayer();
    //    final PacketContainer packet = event.getPacket();
    //    final Integer entityId = packet.getIntegers().read(0);
    //    final WrappedEnumEntityUseAction useAction = packet.getEnumEntityUseActions().read(0);
    //    final EnumWrappers.EntityUseAction action = useAction.getAction();
    //
    //    if (action != EnumWrappers.EntityUseAction.ATTACK && action != EnumWrappers.EntityUseAction.INTERACT_AT) {
    //        return;
    //    }
    //
    //    final CandlebaneTalent talent = getTalent();
    //    final boolean isLeftClick = action == EnumWrappers.EntityUseAction.ATTACK;
    //    final boolean isSneaking = player.isSneaking();
    //
    //    for (Candlebane pillar : talent.getPillars()) {
    //        if (!pillar.isPart(entityId) || pillar.isAnimation()) {
    //            continue;
    //        }
    //
    //        final GamePlayer gamePlayer = CF.getPlayer(player);
    //
    //        if (gamePlayer == null) {
    //            return;
    //        }
    //
    //        new BukkitRunnable() {
    //            @Override
    //            public void run() {
    //                pillar.click(gamePlayer, isLeftClick ? ClickType.LEFT : ClickType.RIGHT);
    //            }
    //        }.runTask(Main.getPlugin());
    //    }
    //}

    @Nonnull
    @Override
    public CandlebaneTalent getTalent() {
        return Talents.CANDLEBANE.getTalent(CandlebaneTalent.class);
    }
}
