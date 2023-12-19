package me.hapyl.fight.game.talents.archive.bloodfiend.candlebane;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class CandlebaneProtocol extends ProtocolListener implements TalentReference<CandlebaneTalent> {
    public CandlebaneProtocol() {
        super(PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();
        final Integer entityId = packet.getIntegers().read(0);
        final WrappedEnumEntityUseAction useAction = packet.getEnumEntityUseActions().read(0);
        final EnumWrappers.EntityUseAction action = useAction.getAction();

        if (action != EnumWrappers.EntityUseAction.ATTACK && action != EnumWrappers.EntityUseAction.INTERACT_AT) {
            return;
        }

        final CandlebaneTalent talent = getTalent();
        final boolean isLeftClick = action == EnumWrappers.EntityUseAction.ATTACK;
        final boolean isSneaking = player.isSneaking();

        for (Candlebane pillar : talent.getPillars()) {
            if (!pillar.isPart(entityId) || pillar.isAnimation()) {
                continue;
            }

            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    pillar.click(gamePlayer, isLeftClick ? ClickType.LEFT : ClickType.RIGHT);
                }
            }.runTask(Main.getPlugin());
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }

    @Nonnull
    @Override
    public CandlebaneTalent getTalent() {
        return Talents.CANDLEBANE.getTalent(CandlebaneTalent.class);
    }
}
